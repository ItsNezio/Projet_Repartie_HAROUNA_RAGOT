import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServeurInterface extends Remote {

    public void enregistrerServiceRestaurant(ServiceRestaurantInterface ServiceRestaurant) throws RemoteException;

    public void enregistrerServiceHTTP(ServiceHTTPInterface ServiceHTTP) throws RemoteException;

    public ServiceRestaurantInterface demanderServiceRestaurant() throws RemoteException;

    public ServiceHTTPInterface demanderServiceHTTP() throws RemoteException;
}
