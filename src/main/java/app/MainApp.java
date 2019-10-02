package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.FileAction;
import model.AccessSystim;
import model.AccessToken;
import model.Request;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) throws IOException, ParseException, InterruptedException, URISyntaxException {
        System.out.print("Pobieram kod EAN oraz ilość przedmiotów z księgowości.... ");

        AccessSystim accessSys = new AccessSystim();
        accessSys.accessToken();

        Thread.sleep(5000);

        System.out.println("Gotowe.");
        System.out.print("Łączę z Allegro...");

        Thread.sleep(5000);

        System.out.println("Połączenie zostało nawiązane.");
        System.out.println("W celu uzyskania autoryzacji zatwierdź swoje konto w przeglądarce.");

        AccessToken accessAll = new AccessToken();
        Request request = new Request();

        request.getOffer(accessAll.getAccessToken());
    }
}
