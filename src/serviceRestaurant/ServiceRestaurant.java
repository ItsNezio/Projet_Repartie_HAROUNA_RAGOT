import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceRestaurant implements ServiceRestaurantInterface {
    private Connection conn;

    public ServiceRestaurant(Connection conn) {
        this.conn = conn;
    }

    @Override
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
        return restaurants;
    }

    private int tablesDispo(int restaurantId, Date dateDebut, Date dateFin) throws SQLException {
        int numTab = -1;
        String sql = "SELECT numtab FROM tabl WHERE restaurant_id = ? " +
                "AND numtab NOT IN (SELECT numtab FROM reservation WHERE restaurant_id = ? AND datres BETWEEN ? AND ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, restaurantId);
            stmt.setInt(2, restaurantId);
            stmt.setDate(3, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(4, new java.sql.Date(dateFin.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    numTab = rs.getInt("numtab");
                }
            }
        }
        System.out.println("Table disponible : " + numTab);
        return numTab;
    }

    @Override
    public boolean reserverTable(String nomRestaurant, Reservation reservation) throws RemoteException {
        boolean reservationConfirmee = false;
        try {
            int restaurantId = getRestaurantIdByName(nomRestaurant);
            System.out.println("ID du restaurant : " + restaurantId);
            Date dateFin = new Date(reservation.dateReservation.getTime() + 2 * 60 * 60 * 1000); // 2 heures plus tard
            int numTab = tablesDispo(restaurantId, reservation.dateReservation, dateFin);
            if (numTab > 0) {
                this.conn.setAutoCommit(false);
                String sql = "INSERT INTO reservation (numres, datres, nbpers, nom, prenom, coordonnees_tel, restaurant_id, numtab) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
                    stmt.setInt(1, getMaxReservationId() + 1);
                    stmt.setDate(2, new java.sql.Date(reservation.dateReservation.getTime()));
                    stmt.setInt(3, reservation.nbPersonne);
                    stmt.setString(4, reservation.nomClient);
                    stmt.setString(5, reservation.prenomClient);
                    stmt.setString(6, reservation.telClient);
                    stmt.setInt(7, restaurantId);
                    stmt.setInt(8, numTab);
                    stmt.executeUpdate();
                }
                this.conn.commit();
                reservationConfirmee = true;
                System.out.println("Réservation confirmée");
            } else {
                System.out.println("Aucune table n'est disponible.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (this.conn != null) {
                    this.conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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

    private int getRestaurantIdByName(String name) throws SQLException {
        String sql = "SELECT id FROM restaurant WHERE nom = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Le restaurant suivant n'existe pas : " + name);
                }
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
