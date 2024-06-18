import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceHTTP {
    public static void main(String[] args) {
        int portService = 0;
        try {
            //On crée une instance du service
            ServiceHTTP serviceHTTP = new ServiceHTTP();
            ServiceHTTPInterface serviceHTTPInterface = (ServiceHTTPInterface) UnicastRemoteObject.exportObject(serviceHTTP, portService);

            // Recupération du serveur
            Registry reg = LocateRegistry.getRegistry(args[0], 1099);
            ServeurInterface serveur = (ServeurInterface) reg.lookup("serveur");

            // Ajout du service au serveur central
            serveur.enregistrerServiceHTTP(serviceHTTPInterface);

            System.out.println("Service HTTP lancé");
        }catch (RemoteException r){
            System.out.println("Impossible d'ajouter le service au serveur");
            r.printStackTrace();
        }
        catch (NotBoundException e) {
            System.out.println("Impossible d'acceder au serveur central");
        }
    }
}
