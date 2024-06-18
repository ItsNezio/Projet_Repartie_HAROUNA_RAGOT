import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.util.List;

public class ProxyHTTP implements HttpHandler {
    private Serveur serveur;

    public ProxyHTTP(Serveur serveur) {
        this.serveur = serveur;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            addCorsHeaders(exchange);
            try {
                ServiceHTTPInterface serviceHTTP = serveur.demanderServiceHTTP();
                String response = serviceHTTP.fetchData();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } catch (Exception e) {
                String errorMessage = "Erreur serveur";
                exchange.sendResponseHeaders(500, errorMessage.length());
                OutputStream os = exchange.getResponseBody();
                os.write(errorMessage.getBytes());
                os.close();
            }
        }
    }




    public void addCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }
}
