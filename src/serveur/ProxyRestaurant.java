import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.io.*;
import java.sql.Date;
import java.util.List;

public class ProxyRestaurant implements HttpHandler {
    private Serveur serveur;

    public ProxyRestaurant(Serveur serveur) {
        this.serveur = serveur;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                ServiceRestaurantInterface serviceRestaurant = serveur.demanderServiceRestaurant();
                List<Restaurant> restaurants = serviceRestaurant.getRestaurants();
                String response = toJson(restaurants);
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
        } else if ("POST".equals(exchange.getRequestMethod())) {
            // Gestion des requêtes POST
            try {
                // Lire le corps de la requête
                InputStream is = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                is.close();

                // Extraire les données de la réservation du JSON
                String requestBody = sb.toString();
                Reservation reservation = fromJson(requestBody);

                // Ajouter la réservation via le service restaurant
                ServiceRestaurantInterface serviceRestaurant = serveur.demanderServiceRestaurant();
                boolean success = serviceRestaurant.reserverTable(reservation.nomClient, reservation);

                // Envoyer une réponse appropriée
                String response = "{\"success\": " + success + "}";
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

        } else {
            // Gestion des méthodes non autorisées
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private String toJson(List<Restaurant> restaurants) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Restaurant restaurant : restaurants) {
            if (!first) {
                sb.append(",");
            }
            sb.append("{");
            sb.append("\"id\":").append(restaurant.id).append(",");
            sb.append("\"nom\":\"").append(restaurant.nom).append("\",");
            sb.append("\"adresse\":\"").append(restaurant.adresse).append("\",");
            sb.append("\"latitude\":\"").append(restaurant.latitude).append("\",");
            sb.append("\"longitude\":\"").append(restaurant.longitude).append("\"");
            sb.append("}");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
    private Reservation fromJson(String json) {
        String[] parts = json.replace("{", "").replace("}", "").replace("\"", "").split(",");
        Date dateReservation = null;
        int nbPersonne = 0;
        String nomClient = "";
        String prenomClient = "";
        String telClient = "";
        for (String part : parts) {
            String[] pair = part.split(":");
            String key = pair[0].trim();
            String value = pair[1].trim();
            switch (key) {
                case "dateReservation":
                    dateReservation = Date.valueOf(value);
                    break;
                case "nbPersonne":
                    nbPersonne = Integer.parseInt(value);
                    break;
                case "nomClient":
                    nomClient = value;
                    break;
                case "prenomClient":
                    prenomClient = value;
                    break;
                case "telClient":
                    telClient = value;
                    break;
            }
        }
        return new Reservation(dateReservation, nbPersonne, nomClient, prenomClient, telClient);
    }


    public void addCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }
}
