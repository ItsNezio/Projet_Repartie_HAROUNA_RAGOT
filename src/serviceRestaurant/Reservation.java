import java.io.Serializable;
import java.sql.Date;

public class Reservation implements Serializable {
    public Date dateReservation;
    public int nbPersonne;
    public String nomClient;
    public String prenomClient;
    public String telClient;

    public Reservation(Date debut, int nbPers, String nom, String prenom, String coordonneesTel) {
        this.dateReservation = debut;
        this.nbPersonne = nbPers;
        this.nomClient = nom;
        this.prenomClient = prenom;
        this.telClient = coordonneesTel;
    }

}
