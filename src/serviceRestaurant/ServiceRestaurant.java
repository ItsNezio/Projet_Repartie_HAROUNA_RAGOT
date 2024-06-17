import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ServiceRestaurant implements ServiceRestaurantInterface {


    public List<Restaurant> getRestaurants() throws RemoteException {
        return null;
    }

    /**
     * Vérifie si une table est disponible à une date donnée
     * @param dateDebut
     * @param dateFin
     */
    public static List<Integer> tablesDispo(Connection conn, Date dateDebut, Date dateFin) {
        List<Integer> tablesDisponibles = new ArrayList<>();
        try {
            String sql = "SELECT numtab FROM tabl WHERE numtab NOT IN (SELECT numtab FROM reservation WHERE datres BETWEEN ? AND ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(2, new java.sql.Date(dateFin.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int numtab = rs.getInt("numtab");
                tablesDisponibles.add(numtab);
            }
            System.out.println("Tables disponibles : " + tablesDisponibles);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tablesDisponibles;
    }


    public boolean reserverTable(Connection conn, String nomRestaurant, Reservation reservation) throws RemoteException {
        boolean reservationConfirmee = false; // Variable pour suivre l'état de la réservation
        int numres = reservation.nbReservation;
        int numtab = reservation.nbTable;
        Date dateDebut = reservation.debut;
        Date dateFin = reservation.fin;
        int nbPer = reservation.nbPersonne;
        String modePaiement = reservation.modePaiement;
        String nom = reservation.nomClient;
        String prenom = reservation.prenomClient;
        String coordonneesTel = reservation.telClient;

        try {
            // Vérifier la disponibilité de la table
            List<Integer> tablesDisponibles = tablesDispo(conn, dateDebut, dateFin);
            if (tablesDisponibles.contains(numtab)) {
                conn.setAutoCommit(false);

                String sql = "INSERT INTO reservation (numres, numtab, datres, nbpers, datpaie, modpaie, nom, prenom, coordonnees_tel) VALUES (?, ?, TO_DATE(?, 'dd/mm/yyyy hh24:mi'), ?, TO_DATE(?, 'dd/mm/yyyy hh24:mi'), ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, numres);
                stmt.setInt(2, numtab);
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String dDate = f.format(dateDebut);
                String fDate = f.format(dateFin);
                stmt.setString(3, dDate);
                stmt.setInt(4, nbPer);
                stmt.setString(5, fDate);
                stmt.setString(6, modePaiement);
                stmt.setString(7, nom);
                stmt.setString(8, prenom);
                stmt.setString(9, coordonneesTel);
                stmt.executeUpdate();

                conn.commit();  // Valider la transaction
                reservationConfirmee = true; // Marquer la réservation comme confirmée
                System.out.println("Réservation effectuée pour la table " + numtab + " à la date " + dDate + " jusqu'à " + fDate + " pour " + nbPer + " personnes. Réservé par " + prenom + " " + nom + " (" + coordonneesTel + ")");
            } else {
                System.out.println("La table " + numtab + " n'est pas disponible pour ce jour.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Envoyer un message au client si la réservation n'est pas confirmée
        if (!reservationConfirmee) {
            System.out.println("Désolé " + prenom + " " + nom + ", la réservation pour la table " + numtab + " à la date demandée n'est pas disponible.");
        }
        return true;
    }
}
