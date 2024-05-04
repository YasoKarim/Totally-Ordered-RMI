import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Delayer implements Runnable {

    private int port;
    private String serviceName;
    //private String requestId;  // Identifier for the request
    private Request delayedRequest;
    public Delayer(int port, String serviceName, Request delayedRequest) {
        this.port = port;
        this.serviceName = serviceName;
        this.delayedRequest = delayedRequest;
    }

    @Override
    public void run() {
        try {
            // Simulate delay (e.g., 5 seconds)
            Thread.sleep(5000);

            // Get the remote object from the RMI registry
            Registry registry = LocateRegistry.getRegistry(port);
            NodeI node = (NodeI) registry.lookup(serviceName);

            // Multicast the request with the timestamp (logical clock value)
            node.performRequest(delayedRequest);

            // The ACKs are multicast by the recipient upon receiving the request
        } catch (RemoteException | NotBoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
