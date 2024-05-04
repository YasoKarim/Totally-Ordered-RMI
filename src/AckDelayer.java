import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AckDelayer implements Runnable {

    private int port;
    private String serviceName;
    private Request request;

    public AckDelayer(int port, String serviceName,Request request) {
        this.port = port;
        this.serviceName = serviceName;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(7000);
            Registry registry = LocateRegistry.getRegistry(port);
            NodeI node = (NodeI) registry.lookup(serviceName);
            node.ack(request);
        } catch (RemoteException | NotBoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
