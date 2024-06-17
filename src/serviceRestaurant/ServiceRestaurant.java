import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ServiceRestaurant implements ServiceRestaurantInterface {

    Connection conn;

    public ServiceRestaurant(Connection conn) {
        this.conn = conn;
    }


    public List<Restaurant> getRestaurants() throws RemoteException {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT id, nom, adresse, latitude, longitude FROM restaurant";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Restaurant restaurant = new Restaurant(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("latitude"),
                        rs.getString("longitude")
                );
                restaurants.add(restaurant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Restaurant restaurant : restaurants) {
            System.out.println(restaurant.nom + " (" + restaurant.adresse + ")");
        }

        return restaurants;
    }


    private int tablesDispo( Date dateDebut, Date dateFin) {
        int nbTables = -1;
        try {
            String sql = "SELECT COUNT(*) FROM table_restaurant WHERE numtab NOT IN (SELECT numtab FROM reservation WHERE datres BETWEEN ? AND ?)";
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setDate(1, dateDebut);
            stmt.setDate(2, dateFin);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nbTables = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nbTables;
    }

    public boolean reserverTable(String nomRestaurant, Reservation reservation) throws RemoteException {
        boolean reservationConfirmee = false;
        try {
            Date dateFin = new Date(reservation.dateReservation.getTime() + 2 * 60 * 60 * 1000);
            int tableDisponible = tablesDispo( reservation.dateReservation, dateFin);
            if (tableDisponible >= 0){
                this.conn.setAutoCommit(false);
                String sql = "INSERT INTO reservation (numres, numtab, datres, nbpers, nom, prenom, coordonnees_tel, restaurant_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = this.conn.prepareStatement(sql);
                stmt.setInt(1, getMaxReservationId() + 1);
                stmt.setInt(2, tableDisponible);
                stmt.setDate(3, new java.sql.Date(reservation.dateReservation.getTime()));
                stmt.setInt(4, reservation.nbPersonne);
                stmt.setString(5, reservation.nomClient);
                stmt.setString(6, reservation.prenomClient);
                stmt.setString(7, reservation.telClient);
                stmt.setInt(8, getRestaurantIdByName( nomRestaurant));
                stmt.executeUpdate();

                this.conn.commit();
                reservationConfirmee = true;
                System.out.println("Réservation pour la table :  " + tableDisponible + " confirmée");
            } else {
                System.out.println("Aucune table n'est dipsonible");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (this.conn != null) {
                    this.conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return reservationConfirmee;
    }

    private int getRestaurantIdByName( String name) throws SQLException {
        String sql = "SELECT id FROM restaurant WHERE nom = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Le restaurant suivant n'existe pas : " + name);
            }
        }
    }

    private int getMaxReservationId() throws SQLException {
        String sql = "SELECT MAX(numres) FROM reservation";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }
}
