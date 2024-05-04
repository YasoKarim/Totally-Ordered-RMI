import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class NodeImplement extends UnicastRemoteObject implements NodeI {

    private static final long serialVersionUID = 1L;
    //private int logicalClock = 0;
    final static int n = ipAddr.length;

    int lClock, myPort;
    String myIp, myService;
    PriorityQueue<Request> requests;
    HashMap<String, Integer> requestsAcks;
    Scanner scan;
    private static final int TOTAL_NODES = 4;
    private CountDownLatch ackLatch;
    private String storagePath; // Individual storage path for each node
    private boolean isLeader = false;
    private int currentTerm = 0;
    private int votedFor = -1;  // -1 indicates no votes
    private int votesReceived = 0;  // Add this line for votesReceived
    private static final int TIMEOUT_DURATION = 10000; // 5000 milliseconds (5 seconds)
    private RaftNode raftNode;
    private Timer heartbeatTimer;

    protected NodeImplement() throws RemoteException {
        super();
    }
    protected NodeImplement(int idx, String storageDirectory) throws RemoteException {
        myIp = ipAddr[idx];
        myPort = ports[idx];
        myService = services[idx];
        lClock = 0;
        requests = new PriorityQueue<>();
        requestsAcks = new HashMap<String, Integer>();
        scan = new Scanner(System.in);
        // Initialize file storage directory for the node
        this.storagePath = storageDirectory + File.separator + "Node" + idx + File.separator;
        raftNode = new RaftNode(idx);
        this.heartbeatTimer = new Timer();

    }

    // New method to initialize Raft node and start the Raft algorithm
    /*private void initRaft() {
        raftNode.start();
    }*/
    @Override
    public Request downloadFile(String fileName) throws RemoteException {
        System.out.println("Downloading File...");
        try {
            File f = new File(storagePath + fileName);

            if (!f.exists()) {
                System.out.println("File not found on node " + myService);
                return null;
            }

            int fileSize = (int) f.length();
            byte[] buffer = new byte[fileSize];

            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(f))) {
                in.read(buffer, 0, buffer.length);
            }

            // Create and return a Request object to represent the downloaded file
            return new Request(lClock, myService, "", -1, Request.DOWNLOAD, fileName, buffer, new Date(f.lastModified()), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean uploadFile(Request file) throws RemoteException {
        // Implementation for file upload
        System.out.println("Uploading File...");
        File localFile = new File(storagePath + file.getFileName());

        if(!localFile.exists()) {
            localFile.getParentFile().mkdir();
        }
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
            out.write(file.getData(), 0, file.getData().length);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean searchFiles(String fileName) throws RemoteException {
        // Implementation for file search
        System.out.println("Searching for File...");
        File f = new File(storagePath + fileName);
        if(f.exists())return true;
        return false;
    }

    @Override
    public boolean deleteFile(String fileName) throws RemoteException {
        // Implementation for file deletion
        System.out.println("Deleting File...");

        File f = new File(storagePath + fileName);
        if (f.exists()) {
            return f.delete();
        }
        return false;
    }

    public void multicastRequest(Request request) throws RemoteException, NotBoundException {
        // Implementation for TO-Multicasting
        boolean delay = true;
        for (int i = 0; i < n; i++) {
            if (delay && request.sender.equals("A")) {
                // Delay Sending Request from A to C
                if (i == 1) {
                    Delayer delayer = new Delayer(ports[i], services[i], request);
                    new Thread(delayer).start();
                    continue;
                }
            }

            Registry registry = LocateRegistry.getRegistry(ports[i]);
            NodeI node = (NodeI) registry.lookup(services[i]);
            node.performRequest(request);
        }
        System.out.println("End Multicast Request:");
        this.displayRequests();
        this.displayAcks();
    }
    @Override
    public void performRequest(Request request) throws RemoteException, NotBoundException {
        this.requests.add(request);
        if(!request.sender.equalsIgnoreCase(this.myService)) { // If receiver
            this.lClock = Math.max(this.lClock, request.clock) + 1;
        }

        System.out.println("Perform Request: ");
        // Example: Print the details of the processed request

        displayRequests();
        displayAcks();
        multicastAck(request);
    }

    private void multicastAck(Request request) throws RemoteException, NotBoundException {
        boolean delay = true;
        for(int i=0; i<n; i++){
            if(delay && myService.equals("A")) { // Delay Sending Ack from A to C
                if(i == 1) {
                    System.out.println("|||||||||||||||||||||||||||||||||||||||||");
                    AckDelayer ackDelayer = new AckDelayer(ports[i], services[i], request);
                    new Thread(ackDelayer).start();
                    continue;
                }
            }
            Registry reg = LocateRegistry.getRegistry(ports[i]);
            NodeI e = (NodeI) reg.lookup(services[i]);
            e.ack(request);
        }
    }

    public void ack(Request r) throws RemoteException {
        if(this.requestsAcks.containsKey(r.requestId)){
            this.requestsAcks.put(r.requestId, (Integer) this.requestsAcks.get(r.requestId) + 1);
        }
        else this.requestsAcks.put(r.requestId, 1);
        //ackLatch.countDown();
    }

    @Override
    public boolean requestVote(int nodeId, int currentTerm) {

        return false;
    }
    // Assuming there are 'n' nodes in the system


    public void waitForAcks(Request request) throws InterruptedException, NotBoundException, RemoteException {
        // Use a CountDownLatch to wait for acknowledgments from all nodes
        ackLatch = new CountDownLatch(TOTAL_NODES - 1); // Adjust the count based on your actual node count

        // Simulate waiting for acknowledgments (In a real implementation, you would handle asynchronous responses)
        // Here, we'll wait for a certain time (you might need to adjust the duration)
        boolean allAcksReceived = ackLatch.await(5000, java.util.concurrent.TimeUnit.MILLISECONDS);

        if (allAcksReceived) {
            // All acknowledgments received, proceed with processing the request
            performRequest(request);
        } else {
            // Handle the case where not all acknowledgments were received within the timeout
            System.out.println("Timeout: Not all acknowledgments received for request " + request.getRequestId());
        }
    }

    public void fetchNewRequest() throws RemoteException {
        if(!this.requests.isEmpty() && this.requestsAcks.containsKey(this.requests.peek().requestId)
                && requestsAcks.get(requests.peek().requestId) == n) {



            System.out.println("Fetch New Request:");
            displayRequests();
            displayAcks();

            Request r = this.requests.poll();
            this.requestsAcks.remove(r.requestId);

            System.out.println("After Fetch New Request:");
            displayRequests();
            displayAcks();

            System.out.println("Performing Request: " + String.valueOf(r));
            if(r.operation == Request.DOWNLOAD) {
                try {
                    Request downloadedFile = downloadFile(r.getFileName());

                    // Assuming downloadFile returns a Request object with file data
                    if (downloadedFile != null) {
                        // Save the downloaded file locally
                        File localFile = new File(storagePath + downloadedFile.getFileName());
                        if (!localFile.exists()) {
                            localFile.getParentFile().mkdirs();
                        }

                        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localFile))) {
                            out.write(downloadedFile.getData(), 0, downloadedFile.getData().length);
                            System.out.println("File downloaded successfully: " + localFile.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Download failed. File not found.");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            }
            else if(r.operation == Request.UPLOAD) {
                //boolean uploadResult = uploadFile(r);
                try {
                    boolean uploadResult = uploadFile(r);
                    if (uploadResult) {
                        System.out.println("File uploaded successfully.");
                    } else {
                        System.out.println("An error occurred. Try again later");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            } else if (r.operation == Request.SEARCH) {
               // boolean searchResult = searchFiles(r.getFileName());
                try {
                    boolean searchResult = searchFiles(r.getFileName());
                    if (searchResult) {
                        System.out.println("File found");
                    } else {
                        System.out.println("No matches found");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                //boolean res = fileServer.searchFiles(fileName);
                //if(res)System.out.println("File found");
                //else System.out.println("No matches found");
            }
            else if(r.operation == Request.DELETE){
                //boolean deleteResult = deleteFile(r.getFileName());
                try {
                    boolean deleteResult = deleteFile(r.getFileName());

                    if (deleteResult) {
                        System.out.println("File deleted successfully.");
                    } else {
                        System.out.println("An error occurred. Try again later");
                    }}
                 catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }
    }
            private void displayRequests() {
        System.out.println("---------------Requests--------------");
        /*for(Request r: requests) {
            System.out.println("(" + (r.operation == Request.DOWNLOAD ? "D, " : "I, ") + r.sender + ", " + r.clock + ", " + r.requestId + ")");
        }*/
                for (Request r : requests) {
                    String operationType = "";
                    switch (r.operation) {
                        case Request.DOWNLOAD:
                            operationType = "Download";
                            break;
                        case Request.UPLOAD:
                            operationType = "Upload";
                            break;
                        case Request.SEARCH:
                            operationType = "Search";
                            break;
                        case Request.DELETE:
                            operationType = "Delete";
                            break;
                        default:
                            operationType = "Unknown";
                            break;
                    }

                    System.out.println("(" + operationType + ", " + r.sender + ", " + r.clock + ", " + r.requestId + ")");
                }
        System.out.println("=========================================");
    }

    private void displayAcks() {
        System.out.println("---------------Acks--------------");
        Set<String> keys = requestsAcks.keySet();
        for(String k: keys) {
            System.out.println("(" + k + ", " + requestsAcks.get(k) + ")");
        }
        System.out.println("=========================================");
    }

}
