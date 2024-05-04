import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeI extends Remote {
    // Machine addresses
    String[] ipAddr = {"127.0.0.1", "127.0.0.1", "127.0.0.1","127.0.0.1"};
    //Host names
    String[] services = {"A", "B", "C", "D"};
    // Port number
    Integer[] ports = {2000, 3000, 4000, 5000};

    //int getLogicalClock() throws RemoteException;
    void performRequest(Request r) throws RemoteException, NotBoundException;

    // File operations for local copy
    Request downloadFile(String fileName) throws RemoteException;

    boolean uploadFile(Request file) throws RemoteException;

    boolean searchFiles(String fileName) throws RemoteException;

    boolean deleteFile(String fileName) throws RemoteException;

    // Totally Ordered Multicasting methods
    void multicastRequest(Request request) throws RemoteException, NotBoundException;

    //void receiveRequest(Request request) throws RemoteException;

    void ack(Request request) throws RemoteException;

    boolean requestVote(int nodeId, int currentTerm) throws RemoteException;

    // void requestVote(int currentTerm, int myPort) throws RemoteException, NotBoundException;

  //  void receiveVoteResponse(int currentTerm, boolean isLeader) throws RemoteException;
}

