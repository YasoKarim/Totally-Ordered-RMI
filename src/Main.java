import java.io.File;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static String PATH = "D:\\Totally_ordered_rmi\\src";

    public static void main(String[] args) {
        // Print the storage path

        int numNodes = 4;  // Update with the actual number of nodes
        CountDownLatch allNodesReady = new CountDownLatch(numNodes);

        URL url = Main.class.getResource("Node0");
        //URL url1 = Main.class.getResource("/Node0/");
        URL url2 = Main.class.getResource("/Node1/");
        URL url3 = Main.class.getResource("/Node2/");
        URL url4 = Main.class.getResource("/Node3/");

        // Subdirectories for each node
        String nodeSubdirectory = "Node" + 0;
        //String nodeSubdirectory = "Node" + 1;
        //String nodeSubdirectory = "Node" + 2;
        //String nodeSubdirectory = "Node" + 3;

        // Set up URLs for each node's storage directory
        URL URl = Main.class.getResource(nodeSubdirectory);

        // Set up the storage path for the node
        String storagePath = PATH + File.separator + nodeSubdirectory;

        // Initialize the NodeImplement object with the node-specific storage directory
        //NodeImplement obj = new NodeImplement(pId, storagePath);

        PATH = url.getPath();
        System.out.println(PATH);
        //int pId = Integer.parseInt(args[0]);
        int pId = 0;
        //int pId = 1;
        //int pId = 2;
        //int pId = 3;

        try {
            System.out.println("Process: " + NodeI.services[pId]);

            //NodeImplement obj = new NodeImplement(pId,PATH);
            NodeImplement obj = new NodeImplement(pId,storagePath);
            List<RaftNode> raftNodes = new ArrayList<>();

            /*for (int i = 1; i <= numNodes; i++) {
                RaftNode raftNode = new RaftNode(i);  // Set process ID as 'i'
                raftNode.start();
                raftNodes.add(raftNode);  // Add RaftNode instance to the list
            }*/

            // Start RaftNode
            RaftNode raftNode = new RaftNode(pId);
            raftNode.start();
            // Create and start three nodes
            //raftNode.createNode(1).start();
            //RaftNode.createNode(2).start();
            //RaftNode.createNode(3).start();
            // Create and start each Raft node
            /*for (int i = 1; i <= numNodes; i++) {
                RaftCluster.createNode(i).start();
            }*/

            //RaftNode raftNode = new RaftNode(pId);
            //raftNode.start();

            /*for (int i = 0; i < numNodes; i++) {
               RaftNode raftNode1 = new RaftNode(i);  // Set process ID as 'i'
                raftNode.start();
                raftNodes.add(raftNode);
            }*/

            //RaftNode node1 = new RaftNode(0);
            //RaftNode node2 = new RaftNode(1);
            //RaftNode node3 = new RaftNode(2);
            //RaftNode node4 = new RaftNode(3);
            //node1.start();
            //node2.start();
            //node3.start();
            //node4.start();
            initServer(obj);
            //initClient(obj, pId,node1);
            //initClient(obj, pId,node2);
            //initClient(obj, pId,node3);
            //initClient(obj, pId,node4);

            //initClient(obj,pId,raftNode);
            initClient(obj,pId);
            // Initialize nodes with different storage directories


        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
    // Method to randomly select a leader ID
    private static int getRandomLeaderId(int numNodes) {
        return new Random().nextInt(numNodes);
    }
    public static void initServer(NodeImplement obj) throws RemoteException{
        Registry reg = LocateRegistry.createRegistry(obj.myPort);
        reg.rebind(obj.myService, obj);
    }
    static boolean leaderElectionSimulated = false;
    static int leaderId;

    private static void initClient(NodeImplement obj, int pId) throws RemoteException, NotBoundException {


        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    obj.fetchNewRequest();
                } catch (RemoteException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }, 0, 100);
        System.out.println("1 - Download\n2 - Upload \n3 - Search \n4 - Delete");

        // Determine a leader randomly
        /*int leaderId = new Random().nextInt(RaftCluster.getTotalNodes()) + 1;

        // Set the leader ID for all nodes
        for (RaftNode node : RaftCluster.getTotalNodes()) {
            node.setLeader(leaderId);
        }*/
        int numNodes = 4;  // Update with the actual number of nodes

        while (true) {
            if (!leaderElectionSimulated) {
                simulateLeaderElection();
                //System.out.println("TEst");
                leaderElectionSimulated = true;
            }
            System.out.println("Enter your Operation: ");
            int operation = obj.scan.nextInt();
            if (operation != Request.DOWNLOAD && operation != Request.UPLOAD && operation != Request.SEARCH && operation != Request.DELETE)
                continue;
            // Initialize message ID
            String RId = UUID.randomUUID().toString();
            String sender = obj.myService;

            // Update local logical clock before sending a message
            obj.lClock++;
            System.out.println("Node " + pId + " - Logical Clock: " + obj.lClock);

            RaftNode raftNode = new RaftNode(pId);
            raftNode.start();
            Request request = null;

            if (operation == Request.DOWNLOAD || operation == Request.SEARCH) {
                // Introduce a delay to allow time for leader election
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                //System.out.println("Print Leader result: " + raftNode.isLeader());
                // Only leader can execute Download and Search
                //int leaderId = getRandomLeaderId(numNodes);

                System.out.println("Process Id : " + pId + " leader Id " + leaderId);
                //if (pId == leaderId)  {
                //if()
                //if(pId == leaderId){
                if(raftNode.isLeader()){
                    //System.out.println("Successfully download or searched" + "is leader value " + raftNode.isLeader());
                    // Simulate file download or search request
                    request = new Request(obj.lClock, sender, RId, pId, operation, "test-upload.txt", null, null, false);
                    obj.multicastRequest(request);
                } else {
                    // Inform user that only leader can execute Download and Search
                    System.out.println("Node " + pId + " is not the leader. Only the leader can execute Download and Search.");
                    continue; // Skip processing further
                }
            } else {
                // For Upload and Delete, all nodes should execute the request
                byte[] data = (operation == Request.UPLOAD) ? "File content goes here.".getBytes() : null;
                request = new Request(obj.lClock, sender, RId, pId, operation, "test-upload.txt", data, new Date(), false);
                obj.multicastRequest(request);
            }

            System.out.println("Process: " + request.pid + " Operation : " + request.operation + " Logical Clock: " + obj.lClock);
            }

    }
    private static void simulateLeaderElection() {
        // Simulate leader election by randomly selecting a leader among the nodes
        int TOTAL_NODES = 4;
        leaderId = new Random().nextInt(TOTAL_NODES);
        System.out.println("Leader elected: Node " + leaderId);
    }
}





















/*else {
                    // Simulate file search request
                    request = new Request(obj.lClock, sender, RId, pId, Request.SEARCH, "test-upload.txt", null, null, false);
                }*/
// Determine if the current node is the leader
//boolean isLeader = obj.electLeader();
// Multicast the request to all nodes
            // Only multicast the request if it's valid
            /*if (request != null) {
                obj.multicastRequest(request);
                System.out.println("Process: " + request.pid + " Operation: " + request.operation + " Logical Clock: " + obj.lClock);
            } else {
                System.out.println("Request is null. Ignoring the request.");
            }*/
            //obj.multicastRequest(request);





/*
Leader Selection for Download and Search Requests:
Ensure that only one node (leader) executes Download and Search requests. You can randomly select a leader.

java
Copy code
// Pseudo code for leader selection
// When handling Download or Search request
if (operation == Request.DOWNLOAD || operation == Request.SEARCH) {
    // Randomly select a leader
    int leaderId = getRandomLeaderId();
    if (pId == leaderId) {
        executeRequest(request);
    }
}
 */