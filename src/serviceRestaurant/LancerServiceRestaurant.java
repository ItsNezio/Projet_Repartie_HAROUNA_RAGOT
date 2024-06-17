import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LancerServiceRestaurant
{
    private static Connection conn = null;

    public static void main(String[] args) throws RemoteException, SQLException {
        String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";

        try {
            // Charger le pilote JDBC et établir la connexion
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, "harouna4u", "azerty");
            System.out.println("Connexion établie à la base de données Oracle.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        int portService = 0;
        try {
            //On crée une instance du service
            ServiceRestaurant serviceRestaurant = new ServiceRestaurant(conn);
            ServiceRestaurantInterface serviceRestaurantInterface = (ServiceRestaurantInterface) UnicastRemoteObject.exportObject(serviceRestaurant, portService);

            // Recupération du serveur
            Registry reg = LocateRegistry.getRegistry(args[0], 1099);
            ServeurInterface serveur = (ServeurInterface) reg.lookup("serveur");

            // Ajout du service au serveur central
            serveur.enregistrerServiceRestaurant(serviceRestaurantInterface);

            System.out.println("Service Restaurant lancé");
        }catch (RemoteException r){
            System.out.println("Impossible d'ajouter le service au serveur");
            r.printStackTrace();
        }
        catch (NotBoundException e) {
            System.out.println("Impossible d'acceder au serveur central");
        }
    }
}