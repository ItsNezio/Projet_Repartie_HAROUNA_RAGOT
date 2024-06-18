import com.sun.net.httpserver.HttpExchange;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceHTTPInterface extends Remote {
    public String fetchData() throws RemoteException;
}
