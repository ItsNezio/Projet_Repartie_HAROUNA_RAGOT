import java.io.Serializable;
import java.sql.Date;

public class Reservation implements Serializable {
    public Date debut;
    public Date fin;
    public int nbReservation;
    public String modePaiement;
    public int nbPersonne;
    public String nomClient;
    public String prenomClient;
    public String telClient;
    public int nbTable;


    public Reservation( Date debut, Date fin, String modePaiement, int nbPersonne, String nomClient, String prenomClient, String telClient) {
        this.debut = debut;
        this.fin = fin;
        this.modePaiement = modePaiement;
        this.nbPersonne = nbPersonne;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.telClient = telClient;
    }


}
