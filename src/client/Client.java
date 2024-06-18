import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(args[0], 1099);
            ServeurInterface serveur = (ServeurInterface) registry.lookup("serveur");
            ServiceRestaurantInterface serviceRestaurant = serveur.demanderServiceRestaurant();

            // Récupérer tous les restaurants
            List<Restaurant> restaurants = serviceRestaurant.getRestaurants();
            for (Restaurant restaurant : restaurants) {
                System.out.println(restaurant.nom + " (" + restaurant.adresse + ")");
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Entrez le nom du restaurant pour lequel vous souhaitez réserver une table :");
            String nomRestaurant = scanner.nextLine();

            // Réserver une table
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date dateDebut = null;
            try {
                dateDebut = dateFormat.parse("15/06/2024 18:00");
            } catch (Exception e) {
                e.printStackTrace();
            }

            java.sql.Date dateDebutSql = new java.sql.Date(dateDebut.getTime());
            Reservation reservation = new Reservation(dateDebutSql, 4, "Doe", "John", "0123456789");
            boolean success = serviceRestaurant.reserverTable(nomRestaurant, reservation);
            if (success) {
                System.out.println("Réservation réussie !");
            } else {
                System.out.println("Échec de la réservation.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
