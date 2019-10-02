package model;

import io.FileAction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

public class AccessToken {

    public String getAccessToken() throws IOException, ParseException, InterruptedException, URISyntaxException {
        String authUrl = "https://allegro.pl.allegrosandbox.pl/auth/oauth/device";
        String userCredentials = "clientid:clientsecret";
        String basicAuth = Base64.getEncoder().encodeToString(userCredentials.getBytes());

        HttpURLConnection myURL = (HttpURLConnection) new URL(authUrl).openConnection();

        myURL.setRequestMethod("POST");
        myURL.setRequestProperty("Authorization", "Basic " + basicAuth);
        myURL.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        myURL.setRequestProperty("Accept", "application/json");
        myURL.setDoOutput(true);

        OutputStream outStream = myURL.getOutputStream();
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
        outStreamWriter.write("client_id=");
        outStreamWriter.flush();
        outStreamWriter.close();
        outStream.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(myURL.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        String jsonString = response.toString();
        Object obj = new JSONParser().parse(jsonString);

        JSONObject jo = (JSONObject) obj;
        String code = (String) jo.get("verification_uri_complete");
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(code));
        }

        Thread.sleep(30000);

        String authUrl2 = "https://allegro.pl.allegrosandbox.pl/auth/oauth/token?grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Adevice_code&device_code="+
                (String) jo.get("device_code");
        HttpURLConnection myURL2 = (HttpURLConnection) new URL(authUrl2).openConnection();

        myURL2.setRequestMethod("POST");
        myURL2.setRequestProperty("Authorization", "Basic " + basicAuth);
        myURL2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        myURL2.setRequestProperty("Accept", "application/json");
        myURL2.setDoOutput(true);

//        int status2 =  myURL2.getResponseCode(); // 200 = HTTP_OK
//        System.out.println("Response (Code):" + status2);
//        System.out.println("Response (Message):" + myURL2.getResponseMessage());

        BufferedReader in2 = new BufferedReader(
                new InputStreamReader(myURL2.getInputStream()));
        String inputLine2;
        StringBuffer response2 = new StringBuffer();

        while ((inputLine2 = in2.readLine()) != null) {
            response2.append(inputLine2);
        }
        in.close();
        in2.close();

        jsonString = response2.toString();
        obj = new JSONParser().parse(jsonString);
        jo = (JSONObject) obj;
        code = (String) jo.get("access_token");

        return code;
    }
}