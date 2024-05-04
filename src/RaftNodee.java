import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/*
class RaftNodetest implements RaftI {

    private int nodeId;
    private int currentTerm;
    private int votedFor;
    public int leaderId;
    private static final int ELECTION_TIMEOUT = 500; // Set the election timeout to 500 milliseconds
    private long lastHeartbeatTime;
    // Update the ELECTION_TIMEOUT definition
    private static final int MIN_TIMEOUT = 300; // Minimum timeout in milliseconds
    private static final int MAX_TIMEOUT = 800; // Maximum timeout in milliseconds
    private final Object lock = new Object(); // Object for synchronization

    private List<LogEntry> log = new ArrayList<>();
    public RaftNodetest(int nodeId) {
        this.nodeId = nodeId;
        this.currentTerm = 0;
        this.votedFor = -1;
        this.leaderId = -1;
        this.lastHeartbeatTime = System.currentTimeMillis();

    }

    public boolean isLeader() {
        return nodeId == leaderId;
    }

    public void setLeader(int leaderId) {
        this.leaderId = leaderId;
    }

    @Override
    public boolean requestVote(int term, int candidateId) throws RemoteException {
        // Simulate responding to a vote request
        if (term > currentTerm) {
            currentTerm = term;
            votedFor = candidateId;
            return true;
        }
        return false;
    }

    @Override
    public void receiveVote(int term, int voterId, boolean voteGranted) throws RemoteException {
        // Process the vote response
        if (term == currentTerm && voteGranted) {
            // Node has received a vote from another node
            // Increment the vote count
            votesReceived++;

            // Check if the node has received a majority of votes
            int majorityVotes = (RaftCluster.getTotalNodes() / 2) + 1;
            if (votesReceived >= majorityVotes && !isLeader()) {
                // Node has received a majority of votes and becomes the leader
                setLeader(nodeId);
                System.out.println("Node " + nodeId + " is now the leader.");
            }
        } else if (term > currentTerm) {
            // The response is for a higher term, update the current term
            currentTerm = term;

            // Reset vote count and update internal state accordingly
            votesReceived = 0;

            // For simplicity, you might want to update your algorithm accordingly
        }
    }

    @Override
    public void sendHeartbeat(int term, int leaderId) throws RemoteException {

    }


    private int getCurrentTerm() {
        return currentTerm;
    }
   /* @Override
    // Method to simulate sending heartbeats
    public void sendHeartbeat(int term, int leaderId) {
        if (isLeader()) {
            // Simulate sending heartbeats
            // This method is called by the leader to maintain its leadership
            // You may add additional logic here if needed
            term = getCurrentTerm();
            System.out.println("Leader " + nodeId + " sending heartbeat for term " + term);

            // Notify followers by calling receiveHeartbeat on each follower
            for (int i = 0; i < RaftCluster.getTotalNodes(); i++) {
                if (i != nodeId - 1) {
                    RaftNodetest follower = RaftCluster.getNode(i + 1);
                    try {
                        follower.receiveHeartbeat(term, nodeId);
                    } catch (RemoteException e) {
                        // Handle RemoteException if necessary
                        e.printStackTrace();
                    }
                }
            }
        }
    }*/
/*
    public void startSendingHeartbeats() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Start a periodic task to simulate sending heartbeats
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, 0, 1000, TimeUnit.MILLISECONDS);
    }*/
/*
@Override
public void receiveHeartbeat(int term, int leaderId) throws RemoteException {
    // Simulate receiving heartbeats
    // This method is called by followers to acknowledge the leader's existence
    // You may add additional logic here if needed
    System.out.println("Node " + nodeId + " received heartbeat from leader " + leaderId + " for term " + term);

    // Update follower's understanding of the leader and its term
    if (term > currentTerm) {
        currentTerm = term;
        votedFor = -1; // Reset votedFor to indicate no vote in the current term
        setLeader(leaderId);
    }
    this.lastHeartbeatTime = System.currentTimeMillis();

}
    private int votesReceived = 0;

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Start a periodic task to simulate Raft election and heartbeat
        scheduler.scheduleAtFixedRate(this::runRaftAlgorithm, 0, 500, TimeUnit.MILLISECONDS);
    }
    // Helper method to generate a random timeout within the specified range
    private int getRandomizedTimeout() {
        Random random = new Random();
        return MIN_TIMEOUT + random.nextInt(MAX_TIMEOUT - MIN_TIMEOUT + 1);
    }
    private void runRaftAlgorithm() {
        if (isLeader()) {
            // Handle leader responsibilities (e.g., send heartbeats)
            System.out.println("Node " + nodeId + " is the leader.");

        } else {
            // Check election timeout
            Random random = new Random();
            //if (new Random().nextInt(10) == 0) { // Simulate a timeout with a 1 in 10 chance
              if(System.currentTimeMillis() - lastHeartbeatTime > getRandomizedTimeout())
            {
                try {
                    startElection();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /*private void startElection() throws RemoteException {
        synchronized (lock) {
        System.out.println("Node " + nodeId + " is starting an election.");

        // Increment current term
        currentTerm++;

        // Vote for itself
        votedFor = nodeId;

        // Send RequestVote RPCs to other nodes
        for (int i = 0; i < RaftCluster.getTotalNodes(); i++) {
            if (i != nodeId - 1) {
                RaftNode node = RaftCluster.getNode(i + 1);
                node.requestVote(currentTerm, nodeId);
            }
        }

        // Reset votesReceived for the new election
        votesReceived = 0;
        /*
        // Wait for votes
        try {
            Thread.sleep(1000); // Simulate the time it takes to gather votes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if received enough votes to become the leader
        if (votesReceived >= (RaftCluster.getTotalNodes() / 2) + 1) {
            setLeader(nodeId);
            System.out.println("Node " + nodeId + " became the leader for term " + currentTerm);
        } else {
            // Reset votedFor and continue the normal operation
            votedFor = -1;
        }*/

        // Use a separate thread to wait for votes
        /*Thread waitForVotesThread = new Thread(() -> {
            // Simulate waiting for votes
            try {
                Thread.sleep(1000); // Adjust the duration as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check if received enough votes to become the leader
            if (votesReceived >= (RaftCluster.getTotalNodes() / 2) + 1) {
                setLeader(nodeId);
                System.out.println("Node " + nodeId + " became the leader for term " + currentTerm);
            } else {
                // Reset votedFor and continue the normal operation
                votedFor = -1;
                System.out.println("Node " + nodeId + " did not receive enough votes to become the leader for term " + currentTerm);
            }
        });

        // Start the thread to wait for votes
        waitForVotesThread.start();
    }
    }*/
  /*      private void startElection() throws RemoteException {
            synchronized (lock) {
                System.out.println("Node " + nodeId + " is starting an election.");

                // Increment current term
                currentTerm++;

                // Vote for itself
                votedFor = nodeId;

                // Send RequestVote RPCs to other nodes
                for (int i = 0; i < RaftCluster.getTotalNodes(); i++) {
                    if (i != nodeId - 1) {
                        RaftNodetest node = RaftCluster.getNode(i + 1);
                        node.requestVote(currentTerm, nodeId);
                    }
                }

                // Reset votesReceived for the new election
                votesReceived = 0;

                // Use a separate thread to wait for votes
                Thread waitForVotesThread = new Thread(() -> {
                    // Simulate waiting for votes
                    try {
                        Thread.sleep(1000); // Adjust the duration as needed
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Check if received enough votes to become the leader
                    if (votesReceived >= (RaftCluster.getTotalNodes() / 2) + 1) {
                        setLeader(nodeId);
                        System.out.println("Node " + nodeId + " became the leader for term " + currentTerm);
                    } else {
                        // Reset votedFor and continue the normal operation
                        votedFor = -1;
                        System.out.println("Node " + nodeId + " did not receive enough votes to become the leader for term " + currentTerm);
                    }
                });

                // Start the thread to wait for votes
                waitForVotesThread.start();

                try {
                    // Wait for the waitForVotesThread to finish before returning
                    waitForVotesThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    /*    }

    @Override
    public void replicateLogEntry(int term, int leaderId, LogEntry logEntry) throws RemoteException {
        // Simulate log replication
        log.add(logEntry);

        // Notify followers about the new log entry
        for (int i = 0; i < RaftCluster.getTotalNodes(); i++) {
            if (i != nodeId - 1) {
                RaftNodetest follower = RaftCluster.getNode(i + 1);
                follower.applyLogEntry(logEntry);
            }
        }
    }

    @Override
    public void applyLogEntry(LogEntry logEntry) throws RemoteException {
        // Simulate applying log entries to the state machine
        System.out.println("Node " + nodeId + " applying log entry: " + logEntry);
    }


    /*
    @Override
    public void applyLogEntry(LogEntry logEntry) throws RemoteException {
        // Simulate applying log entries to the state machine
        System.out.println("Node " + nodeId + " applying log entry: " + logEntry);

        // Process the request in the log entry
        Request request = logEntry.getRequest();
        processRequest(request);
    }*/
/*
    private void processRequest(Request request) {
        // Simulate processing the request
        System.out.println("Node " + nodeId + " processing request: " + request);
    }
}*/