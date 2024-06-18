import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServiceHTTP implements ServiceHTTPInterface {

    public String fetchData() {
        System.out.println("Récupération des données");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://carto.g-ny.org/data/cifs/cifs_waze_v2.json"))
                .build();

        try {
            //Envoi de la réponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Réponse reçue");
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "Échec de la requête: " + response.statusCode();
            }
        } catch (Exception e) {
            return "Erreur lors de la récupération des données: " + e.getMessage();
        }
    }

}
