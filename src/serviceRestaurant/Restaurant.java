import java.io.Serializable;

public class Restaurant implements Serializable {
    private int id;
    private String npm;
    private String adresse;
    private String gps;

    public Restaurant(int id,String nom, String adresse, String coor) {
        this.name = name;
        this.adresse = adresse;
        this.coor = coor;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getGpsCoordinates() {
        return gps;
    }

}
