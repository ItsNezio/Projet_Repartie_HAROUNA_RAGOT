import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;


public class LancerServeurCentral {
    public static void main(String[] args) {
        try {
            Serveur serveur = new Serveur();
            ServeurInterface si = (ServeurInterface) UnicastRemoteObject.exportObject(serveur,0);
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("serveur", si);


            HttpServer serveurProxy = HttpServer.create(new InetSocketAddress(8000), 0);
            serveurProxy.createContext("/systemInformation", new Proxy("https://transport.data.gouv.fr/gbfs/nancy/system_information.json"));
            serveurProxy.createContext("/stationInformation", new Proxy("https://transport.data.gouv.fr/gbfs/nancy/station_information.json"));
            serveurProxy.createContext("/stationStatus", new Proxy("https://transport.data.gouv.fr/gbfs/nancy/station_status.json"));
            serveurProxy.createContext("/trafic", new Proxy("https://carto.g-ny.org/data/cifs/cifs_waze_v2.json"));
            serveurProxy.setExecutor(null);
            serveurProxy.start();
            System.out.println("Proxy lancé sur le port 8000");

            System.out.println("Serveur restaurant lancé");
        } catch(RemoteException e){
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}