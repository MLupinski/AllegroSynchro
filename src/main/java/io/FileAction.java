package io;

import model.Request;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAction {
    private String fileName;
    private String putRequest;
    private String id;
    private String ean;
    private double newLine;
    private String token;

    public void saveData(String fileName, String putRequest) throws FileNotFoundException {
        this.fileName = fileName;
        this.putRequest = putRequest;

        try {
            PrintWriter zapis = new PrintWriter(fileName);
            zapis.println(putRequest);
            zapis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public String readData(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner sc = new Scanner(file);
        String newRequest = "";
        while (sc.hasNextLine()) {
            newRequest += sc.nextLine() + "\n\r";
        }
        return newRequest;
    }

    public void findEan(String ean, String id, String token) throws IOException {
        this.ean = ean;
        this.id = id;
        this.token = token;
        double quantity = 0;

        File txt = new File("kodyEan/SystimEAN.txt");
        try {
            BufferedReader odczytaj = new BufferedReader(new FileReader(txt));
            String line = "";

            while ((line = odczytaj.readLine()) != null) {
                Matcher m = Pattern.compile(ean).matcher(line);

                while (m.find()) {
                    String[] newLine = line.split(" ");
                    quantity = Double.parseDouble(newLine[2]);
                    replaceQuantity(id, quantity);
                }
            }
            Request putRequest = new Request();
            putRequest.putOffer(id, token, quantity);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void replaceId(String id, String fileName) {
        this.id = id;
        this.fileName = fileName;
        String replaceLine = "          \"id\": \"" + id + "\"";
        String line2 = "";

        File txt = new File("akcjedoOfert/" + fileName + ".txt");
        try {
            BufferedReader odczytaj = new BufferedReader(new FileReader(txt));
            StringBuffer inputBuffer = new StringBuffer();
            String line = "";

            while ((line = odczytaj.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');

                String inputStr = inputBuffer.toString();

                Matcher m = Pattern.compile("id").matcher(line);
                while (m.find()) {
                    line2 = line;
                }
                inputStr = inputStr.replace(line2, replaceLine);
                FileOutputStream fileOut = new FileOutputStream("akcjedoOfert/" + fileName + ".txt");
                fileOut.write(inputStr.getBytes());
                fileOut.close();
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    public void replaceQuantity(String id, double newLine) {
        this.id = id;
        this.newLine = newLine;
        String replaceLine = "    \"available\": " + newLine + ",";
        String replaceLine2;
        String replaceLine3 = "     \"republish\": true";
        String[] statusLine = new String[2];
        String line2 = "";
        String line3 = "";
        String line4 = "";

        File txt = new File("oferty/przed/" + id + ".txt");
        try {
            BufferedReader odczytaj = new BufferedReader(new FileReader(txt));
            StringBuffer inputBuffer = new StringBuffer();
            String line = "";

            while ((line = odczytaj.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');

                String inputStr = inputBuffer.toString();

                Matcher m = Pattern.compile("available").matcher(line);
                Matcher m2 = Pattern.compile("status").matcher(line);
                Matcher m3 = Pattern.compile("republish").matcher(line);
                while (m.find()) {
                    line2 = line;
                }
                while (m2.find()) {
                    line3 = line;
                    statusLine = line3.split(":");
                }

                while (m3.find()) {
                    line4 = line;
                }

                if (statusLine[1] == "\"ENDED\",") {
                    replaceLine2 = "    \"status\": \"ENDED\",";
                } else {
                    replaceLine2 = "    \"status\": \"ACTIVE\",";
                }
                inputStr = inputStr.replace(line2, replaceLine);
                inputStr = inputStr.replace(line3, replaceLine2);
                inputStr = inputStr.replace(line4, replaceLine3);
                FileOutputStream fileOut = new FileOutputStream("oferty/po/" + id + ".txt");
                fileOut.write(inputStr.getBytes());
                fileOut.close();
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }
}
