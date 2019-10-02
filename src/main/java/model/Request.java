package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.FileAction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

public class Request {
    private String token;
    private String id;
    private double quantity;

    public void getOrder(String token) throws IOException, ParseException {
        this.token = token;
        String catUrl = "https://api.allegro.pl.allegrosandbox.pl/order/checkout-forms?status=READY_FOR_PROCESSING";

        HttpURLConnection myURL = (HttpURLConnection) new URL(catUrl).openConnection();

        myURL.setRequestProperty("Authorization","Bearer" + token);
        myURL.setRequestProperty("Accept", "application/vnd.allegro.beta.v1+json");
        myURL.setDoOutput(true);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(myURL.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }

    public void getOffer(String token) throws IOException, ParseException  {
        this.token = token;
        String catUrl = "https://api.allegro.pl.allegrosandbox.pl/sale/offers?publication.status=ENDED&publication.status=ACTIVE";

        HttpURLConnection myURL = (HttpURLConnection) new URL(catUrl).openConnection();

        myURL.setRequestProperty("Authorization","Bearer " + token);
        myURL.setRequestProperty("Accept", "application/vnd.allegro.public.v1+json");
        myURL.setDoOutput(true);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(myURL.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String jsonString = response.toString();

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonParser jp = new JsonParser();
//        JsonElement je = jp.parse(jsonString);
//        String prettyJsonString = gson.toJson(je);
//        System.out.println(prettyJsonString);

        // root - korzeń
        JSONObject root = (JSONObject) new JSONParser().parse(jsonString);
        Long size = (Long) root.get("totalCount");
        System.out.println("Ilość ofert aktywnych i zakończonych na Allegro: " + size);
        String offerId = "";

        for (int i = 0; i < size; i++) {
            JSONObject result = (JSONObject) (((JSONArray) root.get("offers")).get(i));

            if(i == (size - 1)) {
                offerId += (String) result.get("id");
            } else {
                offerId += (String) result.get("id") + "\n";
            }
        }
        FileAction saveId = new FileAction();
        saveId.saveData("oferty/id/offersId.txt", offerId);

        getOfferWithId(token);
    }

    public void getOfferWithId(String token) throws IOException, ParseException {
        this.token = token;
        File file = new File("oferty/id/offersId.txt");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String Url = "https://api.allegro.pl.allegrosandbox.pl/sale/offers/" + sc.nextLine();

            HttpURLConnection myURL = (HttpURLConnection) new URL(Url).openConnection();

            myURL.setRequestProperty("Authorization","Bearer " + token);
            myURL.setRequestProperty("Accept", "application/vnd.allegro.public.v1+json");
            myURL.setDoOutput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(myURL.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String jsonString = response.toString();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jsonString);
            String prettyJsonString = gson.toJson(je);

            JSONObject root = (JSONObject) new JSONParser().parse(jsonString);
            String id = (String) root.get("id");

            FileAction fileAction = new FileAction();
            fileAction.saveData("oferty/przed/" + id + ".txt", prettyJsonString);

            String result = (String) root.get("ean");

            fileAction.findEan(result, id, token);
        }
    }

    public void putOffer(String id, String token, double quantity) throws IOException, InterruptedException {
        this.id = id;
        this.token = token;
        this.quantity = quantity;

        String Url = "https://api.allegro.pl.allegrosandbox.pl/sale/offers/" + id;
        HttpURLConnection myURL = (HttpURLConnection) new URL(Url).openConnection();

        myURL.setRequestMethod("PUT");
        myURL.setRequestProperty("Authorization","Bearer " + token);
        myURL.setRequestProperty("Accept", "application/vnd.allegro.public.v1+json");
        myURL.setRequestProperty("Content-Type", "application/vnd.allegro.public.v1+json");
        myURL.setDoOutput(true);

        FileAction read = new FileAction();
        String newRequest = read.readData("oferty/po/" + id + ".txt");

        OutputStreamWriter outStreamWriter = new OutputStreamWriter(myURL.getOutputStream(), "UTF-8");
        outStreamWriter.write(String.valueOf(newRequest));
        outStreamWriter.flush();
        outStreamWriter.close();

        int status =  myURL.getResponseCode(); // 200 = HTTP_OK

        if(status == 200) {
            System.out.println("Informację dla oferty o id = " + id + " przesłane do allegro poprawnie.");
            System.out.println("Przesyłam informację o stanie oferty (aktywna / nieaktywna)");
            endOrActiveOffer(token, id, quantity);
            System.out.println("-----------------");
            Thread.sleep(5000);
        } else {
            System.out.println("Wystąpił problem z połączeniem, spróbuj ponownie później.");
            System.out.println(status + ", " + myURL.getResponseMessage());
            endOrActiveOffer(token, id, quantity);
        }
    }

    public void endOrActiveOffer(String token, String id, double quantity) throws IOException {
        this.token = token;
        this.id = id;
        this.quantity = quantity;

        Random rand = new Random();
        int  randomNumber = rand.nextInt(9999);
        randomNumber += 1;
        String uuid = "87c28171-243c-42df-" + randomNumber + "-" + id;
        System.out.println(uuid);

        FileAction file = new FileAction();

        String Url = "https://api.allegro.pl.allegrosandbox.pl/sale/offer-publication-commands/" + uuid;
        HttpURLConnection myURL = (HttpURLConnection) new URL(Url).openConnection();

        myURL.setRequestMethod("PUT");
        myURL.setRequestProperty("Authorization","Bearer " + token);
        myURL.setRequestProperty("Accept", "application/vnd.allegro.public.v1+json");
        myURL.setRequestProperty("Content-Type", "application/vnd.allegro.public.v1+json");
        myURL.setDoOutput(true);

        OutputStreamWriter outStreamWriter = new OutputStreamWriter(myURL.getOutputStream(), "UTF-8");

        if (quantity > 0) {
            file.replaceId(id, "active");
            String putRequest = file.readData("akcjedoOfert/active.txt");
            outStreamWriter.write(String.valueOf(putRequest));
        } else {
            file.replaceId(id, "end");
            String putRequest = file.readData("akcjedoOfert/end.txt");
            outStreamWriter.write(String.valueOf(putRequest));
        }
        outStreamWriter.flush();
        outStreamWriter.close();

        int status =  myURL.getResponseCode(); // 200 = HTTP_OK
        System.out.println(status + " " + myURL.getResponseMessage());
        System.out.println("Dane o stanie oferty o id = " + id + " przesłane prawidłowo.");
    }
}