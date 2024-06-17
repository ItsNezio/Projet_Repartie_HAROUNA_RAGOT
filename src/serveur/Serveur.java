import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Serveur implements ServeurInterface {
    List<ServiceRestaurantInterface> listeServicesRestaurant;
    List<ServiceHTTPInterface> listeServicesHTTP;
    int indice = 0;

    public Serveur() {
        listeServices = new ArrayList<>();
    }


    public synchronized ServiceRestaurantInterface demanderServiceRestaurant() throws RemoteException {
        // Vérification si il y a bien un service
        if (this.listeServicesRestaurant.isEmpty()) {
            return null;
        }
        indice++;
        if (indice >= this.listeServicesRestaurant.size()){
            indice = 0;
        }

        return this.listeServicesRestaurant.get(indice);
    }

    public synchronized void  enregistrerServiceRestaurant(ServiceRestaurantInterface ServiceRestaurant) throws RemoteException {
        synchronized (this.listeServicesRestaurant){
            this.listeServicesRestaurant.add(ServiceRestaurant);
            System.out.println("Service Restaurant ajouté");
        }
    }

    public synchronized void  enregistrerServiceHTTP(ServiceHTTPInterface ServiceHTTP) throws RemoteException {
        synchronized (this.listeServicesHTTP){
            this.listeServicesHTTP.add(ServiceHTTP);
            System.out.println("Service HTTP ajouté");
        }
    }

    public synchronized ServiceHTTPInterface demanderServiceHTTP() throws RemoteException {
        // Vérification si il y a bien un service
        if (this.listeServicesHTTP.isEmpty()) {
            return null;
        }
        indice++;
        if (indice >= this.listeServicesHTTP.size()){
            indice = 0;
        }

        return this.listeServicesHTTP.get(indice);
    }




}
