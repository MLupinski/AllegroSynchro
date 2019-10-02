package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.FileAction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AccessSystim {
    private String token;

    public void accessToken() throws IOException, ParseException {
        String authUrl = "https://allen.systim.pl/jsonAPI.php?act=login&login=api&pass=";

        HttpURLConnection myURL = (HttpURLConnection) new URL(authUrl).openConnection();

        myURL.setRequestMethod("POST");
        myURL.setRequestProperty("Accept", "application/json");
        myURL.setDoOutput(true);

        BufferedReader in = new BufferedReader(new InputStreamReader(myURL.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        String jsonString = response.toString();
        String token = null;

        Object obj = new JSONParser().parse(jsonString);
        JSONObject jo = (JSONObject) obj;
        Map result = ((Map)jo.get("result"));

        Iterator<Map.Entry> itr1 = result.entrySet().iterator();
        while (itr1.hasNext()) {
            Map.Entry pair = itr1.next();
            token = (String) pair.getValue();
        }

        getProductInfo(token);
    }

    public void getProductInfo(String token) throws IOException, ParseException {
        this.token = token;
        HttpURLConnection myURL = (HttpURLConnection) new URL("https://allen.systim.pl/jsonAPI.php").openConnection();

        myURL.setRequestMethod("POST");
        myURL.setRequestProperty("Accept", "application/json");
        myURL.setDoOutput(true);

        OutputStream outStream = myURL.getOutputStream();
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
        outStreamWriter.write("act=listPQuantities&token=" + token +"&id_magazynu=4");
        outStreamWriter.flush();
        outStreamWriter.close();
        outStream.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(myURL.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        String jsonString = response.toString();

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonParser jp = new JsonParser();
//        JsonElement je = jp.parse(jsonString);
//        String prettyJsonString = gson.toJson(je);

        // root - korzeń
        JSONObject root = (JSONObject) new JSONParser().parse(jsonString);

        // wchodzimy do result
        JSONObject result = (JSONObject) root.get("result");
        Set<Map.Entry> keyValues = result.entrySet();

        // pobieramy dane
        String barCodeAndQuant = "";

        for (Map.Entry<String, JSONObject> keyValue : keyValues) {
            barCodeAndQuant += (String.format("%s - %s", keyValue.getValue().get("kod_kreskowy"),
                    keyValue.getValue().get("ilosc"))) +  "\n";
        }
        FileAction save = new FileAction();
        save.saveData("kodyEan/SystimEAN.txt", barCodeAndQuant);
    }
}