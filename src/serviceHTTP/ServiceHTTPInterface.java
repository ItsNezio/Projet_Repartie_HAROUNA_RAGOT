import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceHTTPInterface extends Remote {
    public void fetchData() throws RemoteException;
}
