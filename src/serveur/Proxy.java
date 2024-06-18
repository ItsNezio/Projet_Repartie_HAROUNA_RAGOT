import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Proxy implements HttpHandler {
    private final String apiURL;

    public Proxy(String externalApiUrl) {
        this.apiURL = externalApiUrl;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            fetchData(exchange);
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
            exchange.close();
        }
    }

    private void fetchData(HttpExchange exchange) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiURL))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                sendResponse(exchange, response.body(), statusCode);
            } else {
                sendResponse(exchange, "Échec de la requête: " + statusCode, statusCode);
            }
        } catch (Exception e) {
            sendResponse(exchange, "Erreur lors de la récupération des données: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) {
        try {
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, OPTIONS, POST");
        headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }
}
