import java.io.Serializable;

public class Restaurant implements Serializable {
    public int id;
    public String nom;
    public String adresse;
    public String latitude;
    public String longitude;

    public Restaurant(int id, String nom, String adresse, String latitude, String longitude) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

}
