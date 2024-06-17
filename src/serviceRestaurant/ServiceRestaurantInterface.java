import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.List;

public interface ServiceRestaurantInterface extends Remote {
    List<Restaurant> getRestaurants() throws RemoteException;
    boolean reserverTable(Connection conn, String nomRestaurant, Reservation reservation) throws RemoteException;

}
