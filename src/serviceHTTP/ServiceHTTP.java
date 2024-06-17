import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServiceHTTP implements ServiceHTTPInterface {

    public void fetchData() {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://carto.g-ny.org/data/cifs/cifs_waze_v2.json"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Réponse reçue: " + response.body());
            } else {
                System.out.println("Échec de la requête: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des données: " + e.getMessage());
        }
    }
}
