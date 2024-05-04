import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RaftI extends Remote {
    // Machine addresses
    String[] ipAddr = {"127.0.0.1", "127.0.0.1", "127.0.0.1","127.0.0.1"};
    //Host names
    String[] services = {"A", "B", "C", "D"};
    // Port number
    Integer[] ports = {2000, 3000, 4000, 5000};
    // Method for requesting votes during leader election
    boolean requestVote(int term, int candidateId) throws RemoteException;

    // Method for receiving votes during leader election
    void receiveVote(int term, int voterId, boolean voteGranted) throws RemoteException;

    // Method for sending heartbeats to maintain leadership
    void sendHeartbeat(int term, int leaderId) throws RemoteException;

    // Method for receiving heartbeats from the leader
    void receiveHeartbeat(int term, int leaderId) throws RemoteException;

    // Method for replicating log entries
    void replicateLogEntry(int term, int leaderId, LogEntry logEntry) throws RemoteException;

    // Method for applying log entries to the state machine
    void applyLogEntry(LogEntry logEntry) throws RemoteException;
    void setLeader(int leaderId) throws RemoteException;
    void informNewLeader(int leaderId) throws RemoteException;


}
