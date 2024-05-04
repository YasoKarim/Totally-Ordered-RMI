import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RaftNode implements RaftI{

    private int nodeId;
    private int currentTerm;
    private int votedFor;
    private boolean isLeader;
    private int[] votes;
    private int leaderId;
    private static CountDownLatch allNodesReady;
    private boolean electionInProgress = false;
    // Static fields for RaftCluster functionality
    private static final int TOTAL_NODES = 4;
    private static RaftNode[] nodes = new RaftNode[TOTAL_NODES];
    private List<RaftNode> participatingNodes = new ArrayList<>();
    // Add host and port information
    private String host;
    private int port;
    private Timer heartbeatTimer;
    private long lastHeartbeatTimestamp;
    private static final int HEARTBEAT_INTERVAL = 1000; // 1000 milliseconds (1 second)

    public RaftNode(int nodeId) {
        this.nodeId = nodeId;
        this.currentTerm = 0;
        this.votedFor = -1; // -1 indicates no vote
        //this.isLeader = false;
        //this.leaderId = -1;
        this.isLeader = false;
        RaftNode.allNodesReady = allNodesReady;
        // Add the newly created node to the nodes array
        //nodes[nodeId - 1] = this;
        // Initialize host and port based on NodeI interface values
        this.host = NodeI.ipAddr[nodeId];
        this.port = NodeI.ports[nodeId];
        this.heartbeatTimer = new Timer();
        votes = new int[TOTAL_NODES];
        Arrays.fill(votes, -1); // Initialize to -1 indicating no vote
    }

    public static int getHeartbeatInterval() {
        return HEARTBEAT_INTERVAL;
    }
    // Getter for participating nodes
    public List<RaftNode> getParticipatingNodes() {
        return participatingNodes;
    }
    public boolean isLeader() {
        System.out.println("Node Id : " + nodeId + " Leader Id : " + leaderId);
        //return nodeId == leaderId;
        return isLeader;
    }
    public void setLeader(int leaderId) {
        this.leaderId = leaderId;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }
    // Create and add participating nodes
   // RaftNode node1 = new RaftNode(0);

    public long getLastHeartbeatTimestamp() {
        return lastHeartbeatTimestamp;
    }

    public void setLastHeartbeatTimestamp(long timestamp) {
        lastHeartbeatTimestamp = timestamp;
    }
    //RaftNode node2 = new RaftNode(1);
    //participatingNodes.add(node2);
    //RaftNode node3 = new RaftNode(2);
    //participatingNodes.add(node3);

    //RaftNode node4 = new RaftNode(3);
    //participatingNodes.add(node4);



    // Set the initial leader and leader ID for node1
//node1.setLeader(true);
//node1.setLeaderId(1);
    public void start() {


        /*try {
            NodeI nodeStub = (NodeI) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(NodeI.services[nodeId], nodeStub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
        // DAH SHAGALLL
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Start a periodic task to simulate Raft election and heartbeat
      //  scheduler.scheduleAtFixedRate(this::runRaftAlgorithm, 0, 500, TimeUnit.MILLISECONDS);


        //ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Start a periodic task to simulate Raft election and heartbeat
        scheduler.scheduleAtFixedRate(() -> {
            //TODO isleader()
            if (!isLeader()) {
                // Check election timeout
                if (new Random().nextInt(10) == 0) { // Simulate a timeout with a 1 in 10 chance
                    try {
                        startElection();
                    } catch (NotBoundException e) {
                        throw new RuntimeException(e);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                // If the node is already a leader, stop scheduling the task
                scheduler.shutdown();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        // Wait for all nodes to be ready

        /*try {
            allNodesReady.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
    // Placeholder for the logic to send RequestVote RPCs to other nodes
    private void sendRequestVote(int targetNodeId) {
        try {
            System.out.println("Target node: " + targetNodeId);
            // Assuming you have an RMI registry running on a specific port
            Registry registry = LocateRegistry.getRegistry(ports[targetNodeId]);

            // Lookup the remote object (assumes NodeI is the remote interface)
            NodeI remoteNode = (NodeI) registry.lookup(services[targetNodeId]);

            // Call the requestVote method on the remote node
           /* remoteNode.requestVote(this.nodeId, this.currentTerm);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            // Handle exceptions as needed (e.g., node not reachable)
        }*/
            // Call the requestVote method on the remote node
            boolean voteGranted = remoteNode.requestVote(this.currentTerm, this.nodeId);

            // Update the votes array
            if (voteGranted) {
                votes[targetNodeId] = this.nodeId;
            }
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            // Handle exceptions as needed (e.g., node not reachable)
        }
    }
    private void runRaftAlgorithm() throws NotBoundException, RemoteException {
        if (isLeader()) {
            // Handle leader responsibilities (e.g., send heartbeats)
            System.out.println("Node " + nodeId + " is the leader.");
        } else {
            // Check election timeout
            if (new Random().nextInt(10) == 0) { // Simulate a timeout with a 1 in 10 chance
                startElection();
            }
        }
    }

    private void startElection() throws NotBoundException, RemoteException {
        electionInProgress = true;

        System.out.println("Leader id before election " + leaderId);
        if (!isLeader) {
        System.out.println("Node " + nodeId + " is starting an election.");

        // Increment current term
        currentTerm++;
        System.out.println("Current term " + currentTerm);
        // Set leaderId to node
         //   leaderId = nodeId;

        System.out.println("leader id : " + leaderId + " isleader " + isLeader);
        // Vote for itself
        votedFor = nodeId;

        System.out.println("Voted for " + votedFor);
        // Send RequestVote RPCs to other nodes
            /*for (RaftNode targetNode : participatingNodes) {
                if (targetNode != this) {
                    System.out.println("Request vote from " + targetNode.nodeId);
                    sendRequestVote(targetNode);
                }
            }*/

           /* for (RaftNode targetNode : nodes) {
                //targetNode.nodeId = nodeId;
                System.out.println("Node " + targetNode);
                if (targetNode != this) {
                    System.out.println("Request vote ---- ");
                    // Skip sending a RequestVote to itself
                    sendRequestVote(targetNode);
                }
            }*/
            for (int i = 0; i < 4; i++) {
            if (i != nodeId) {
                System.out.println("Request vote from Node " + i);
                sendRequestVote(i);
                        }
                }
        // Wait for votes
       /* try {
            Thread.sleep(1000); // Simulate the time it takes to gather votes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        // Check if received enough votes to become the leader
        if (receivedEnoughVotes()) {
            System.out.println("RECEIEVED ENOUGH VOTES Become Leader " + isLeader);
            becomeLeader();
            //broadcastLeader();
            System.out.println("Updated is leader" + isLeader);
            //updateLeaderStatus(leaderId);
            electionInProgress = false;
        } else {
            // Reset votedFor and continue the normal operation
            votedFor = -1;
        }
    }


    }
    /*public void broadcastLeader() {
        // Implement logic to broadcast the leader's ID to all nodes
        // You can use RMI or any other communication mechanism here
        // For simplicity, let's assume there's a method broadcastLeaderId in RaftCluster
        RaftCluster.broadcastLeaderId(leaderId);
    }*/
    /*public void broadcastLeader(int leaderId) {
        System.out.println("Leader Id before broadcast " + leaderId);
        for (RaftNode node : nodes) {
            System.out.println("Node broadcast: " + node);
            node.setLeader(leaderId);
            System.out.println("----------");
        }
    }*/

    public void startHeartbeatThread() {
        if (isLeader) {
            // Schedule a task to send heartbeats periodically
            heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        sendHeartbeat(currentTerm,leaderId);  // Replace with your actual logic
                    } catch (RemoteException e) {
                        e.printStackTrace();  // Handle exceptions
                    }
                }
            }, 0, HEARTBEAT_INTERVAL);
        }
    }

    public void stopHeartbeatThread() {
        heartbeatTimer.cancel();
    }
    @Override
    public void informNewLeader(int leaderId) throws RemoteException {
        if (this.nodeId == leaderId) {
            System.out.println("Node " + this.nodeId + " becomes the leader.");
            this.isLeader = true;

            // Add any additional logic you need when becoming the leader

            // Example: Start a background thread to periodically send heartbeats
            startHeartbeatThread();
        } else {
            System.out.println("Node " + this.nodeId + " received information about the new leader: " + leaderId);
            this.isLeader = false;

            // Add any additional logic you need when not being the leader

            // Example: Stop the background thread sending heartbeats
            stopHeartbeatThread();
        }
        // ... handle the information as needed ...
    }
    public void multicastLeader(int leaderId) throws RemoteException, NotBoundException {
        // Implementation for TO-Multicasting
        boolean delay = true;
        for (int i = 0; i < TOTAL_NODES; i++) {

            Registry registry = LocateRegistry.getRegistry(ports[i]);
            RaftI node = (RaftI) registry.lookup(services[i]);
            node.informNewLeader(leaderId);


        }
        System.out.println("End Multicast Leader:");
        //this.displayRequests();
        //this.displayAcks();
    }

    /*public void updateLeaderStatus(int newLeaderId) {
        for (RaftNode node : nodes) {
            node.setLeader(newLeaderId);
        }
    }*/
    private void becomeLeader() throws NotBoundException, RemoteException {
        System.out.println("Before becoming leader - isLeader: " + isLeader);
        //isLeader = true;
        setLeader(true);
        leaderId = nodeId;
        System.out.println("Node " + nodeId + " became the leader for term " + currentTerm + " isleader : " +isLeader);
        //broadcastLeader(leaderId); // Broadcast the leader's ID to all nodes
        multicastLeader(leaderId);
        System.out.println("After broadcasting " + isLeader);
        // Add the following line to update the isLeader status across nodes
        //RaftCluster.updateLeaderStatus(nodeId);
        //updateLeaderStatus(nodeId);
        //System.out.println("After becoming leader - isLeader: " + isLeader);

    }

    private boolean receivedEnoughVotes() {
        /*int votesReceived = 1; // Start with 1 vote for itself
        for (RaftNode node : participatingNodes) {
            if (node.votedFor == nodeId) {
                votesReceived++;
            }
        }
        int votesNeeded = participatingNodes.size() / 2 + 1;
        return votesReceived >= votesNeeded;
        */
        /*
        // Simulate a simple check for receiving votes from a majority
        int votesNeeded = RaftCluster.getTotalNodes() / 2 + 1;
        // Simulate receiving votes from a majority by randomly deciding
        return new Random().nextInt(RaftCluster.getTotalNodes()) < votesNeeded;
    */
        int votesReceived = 1; // Count the self-vote
        for (int i = 0; i < RaftNode.TOTAL_NODES; i++) {
            if (votes[i] == this.nodeId) {
                votesReceived++;
            }
        }
        return votesReceived > RaftNode.TOTAL_NODES / 2;

    }


    @Override
    public boolean requestVote(int term, int candidateId) throws RemoteException {
        if (term > currentTerm) {
            currentTerm = term;
            // Additional logic if needed
            return true; // Vote for the candidate
        } else {
            // Additional logic if needed
            return false; // Do not vote for the candidate
        }    }

    @Override
    public void receiveVote(int term, int voterId, boolean voteGranted) throws RemoteException {

    }

    @Override
    public void sendHeartbeat(int term, int leaderId) throws RemoteException {
// Iterate through all nodes in the cluster and send heartbeats
        for (int i = 0; i < TOTAL_NODES; i++) {
            if (i != this.nodeId) {
                try {
                    Registry registry = LocateRegistry.getRegistry(ports[i]);
                    RaftI remoteNode = (RaftI) registry.lookup(services[i]);
                    remoteNode.receiveHeartbeat(term, leaderId);
                } catch (Exception e) {
                    e.printStackTrace(); // Handle exceptions
                }
            }
        }
    }

    @Override
    public void receiveHeartbeat(int term, int leaderId) throws RemoteException {
        System.out.println("Received heartbeat from Node " + leaderId + " for term " + term);

        // Check if the received term is greater than the current term
        if (term > currentTerm) {
            System.out.println("Updating term to " + term);
            currentTerm = term;
            // Reset votedFor since we're in a new term
            votedFor = -1;
            // Transition to Follower state because a leader in a new term has been detected
            //becomeFollower();
        }

        // Update the timestamp to indicate that the leader is still alive
        lastHeartbeatTimestamp = System.currentTimeMillis();

    }

    @Override
    public void replicateLogEntry(int term, int leaderId, LogEntry logEntry) throws RemoteException {

    }

    @Override
    public void applyLogEntry(LogEntry logEntry) throws RemoteException {

    }
}

   /* public static void main(String[] args) {
        // Create and start three nodes
        RaftCluster.createNode(1).start();
        RaftCluster.createNode(2).start();
        RaftCluster.createNode(3).start();

}*/
/*
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RaftNode extends Thread {
    private int nodeId;
    private int currentTerm;
    private int votedFor;
    private int votesReceived;
    private volatile boolean isLeader;
    private Timer electionTimer;
    private Timer heartbeatTimer;

    public RaftNode(int nodeId) {
        this.nodeId = nodeId;
        this.currentTerm = 0;
        this.votedFor = -1;
        this.votesReceived = 0;
        this.isLeader = false;
        this.electionTimer = new Timer();
    }

    public void startElection() {
        if (!isLeader) {
            // Start a new election
            currentTerm++;
            votedFor = nodeId;
            votesReceived = 1; // Vote for itself

            // Send RequestVote RPCs to other nodes
            for (int i = 0; i < NodeI.ipAddr.length; i++) {
                if (i != nodeId) {
                    try {
                        System.out.println("Trying to connect to: " + NodeI.ipAddr[i] + ":" + NodeI.ports[i] + " " + NodeI.services[i]);
                        Registry registry = LocateRegistry.createRegistry(NodeI.ports[i]);
                        //NodeI node = new NodeImplement();  // Replace with your actual implementation
                        //Naming.rebind("rmi://" + NodeI.ipAddr[i] + ":" + NodeI.ports[i] + "/" + NodeI.services[i], node);
                        NodeI node = (NodeI) registry.lookup(NodeI.services[i]);
                        node.requestVote(nodeId, currentTerm);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (NotBoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // Set a timeout for receiving votes
            electionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (votesReceived <= NodeI.ipAddr.length / 2) {
                        // Election timeout, not enough votes received, restart election
                        startElection();
                    } else {
                        // Become the leader
                        becomeLeader();
                    }
                }
            }, new Random().nextInt(150) + 150); // Randomize timeout to prevent split votes
        }
    }


    public void requestVote(int candidateId, int term) throws RemoteException {
        if (term > currentTerm) {
            // Candidate has a higher term, reset and vote for the candidate
            currentTerm = term;
            votedFor = candidateId;
            votesReceived = 1;
        } else if (term == currentTerm && (votedFor == -1 || votedFor == candidateId)) {
            // Candidate has the same term, vote for the candidate
            votedFor = candidateId;
            votesReceived++;
        }
    }

    public void becomeLeader() {
        isLeader = true;
        System.out.println("Node " + nodeId + " elected as the leader for term " + currentTerm);

        // Start heartbeat mechanism
        startHeartbeat();
    }

    private void startHeartbeat() {
        electionTimer.cancel(); // Cancel election timeout
        // Implement the logic for sending heartbeat messages to maintain leadership
        // This could involve periodically sending AppendEntries RPCs to other nodes

        // Initialize the heartbeat timer
        heartbeatTimer = new Timer();

        // Schedule a task to send periodic heartbeat messages
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendHeartbeat();
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 500);
    }

    private void sendHeartbeat() throws RemoteException, NotBoundException {
        // Iterate over all nodes (excluding the current node)
        for (int i = 0; i < NodeI.ipAddr.length; i++) {
            if (i != nodeId) {
                try {
                    // Lookup the remote node
                    Registry registry = LocateRegistry.getRegistry(NodeI.ports[i]);
                    NodeI node = (NodeI) registry.lookup(NodeI.services[i]);

                    // Send an AppendEntries RPC (heartbeat) to the remote node
                    // You may need to define an AppendEntries method in your NodeI interface
                    // and implement it in your remote node class
                    //node.appendEntries(nodeId, currentTerm);
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public boolean isLeader() {
        return isLeader;
    }

    @Override
    public void run() {
        // Raft node's main logic goes here
        // Start the election when the node starts
        startElection();
    }
}
*/