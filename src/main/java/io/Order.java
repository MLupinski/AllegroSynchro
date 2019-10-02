package io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

public class Order {
    private String id;

    public void saveToFile(String id) throws IOException {
        this.id = id;
        Menu menu = new Menu();
        Scanner sc = new Scanner(System.in);

        String buyer;
        String orderDate;
        String item;
        String price;
        String payment;
        String status;

        System.out.println("----- DODAWANIE ZAMÓWIENIA -----");
        System.out.println("Kupujący: ");
        buyer = sc.nextLine();
        System.out.println("Data zakupu (dd.mm.rrrr): ");
        orderDate = sc.nextLine();
        System.out.println("Przedmiot: ");
        item = sc.nextLine();
        System.out.println("Kwota: ");
        price = sc.nextLine();
        System.out.println("Forma płatności: ");
        payment = sc.nextLine();
        System.out.println("Czy zapłacone?: ");
        status = sc.nextLine();

        String order = buyer + " " + orderDate + " " + item + " " + price + " " + payment + " " + status;
        Files.write(Paths.get(id + ".txt"), order.getBytes());

        menu.showOrderMenu();
    }

    public void show() throws IOException {
        String fileName = "id.txt";
        String id = new String(Files.readAllBytes(Paths.get(fileName)));
        int ordersNumber = Integer.parseInt(id);

        for (int i = 1; i < ordersNumber; i++) {
            System.out.print(i +". ");
            fileName = i + ".txt";

            try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
                stream.forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(String id) {
        this.id = id;

        File file = new File(id + ".txt");
        if (file.delete()) {
            System.out.println("Zamówienie o id = " + id + " zostało usunięte.");
        } else {
            System.out.println("Nie udało się usunąć zamówienia o podanym id.");
        }
    }

    public String readAndRiseId() throws IOException {
        String fileName = "id.txt";
        String id = new String(Files.readAllBytes(Paths.get(fileName)));
        int newId = 0;

        try {
            newId = Integer.parseInt(id) + 1;
        } catch (NumberFormatException e) {
            System.out.println(e);
        }

        String newId2 = Integer.toString(newId);
        Files.write(Paths.get("id.txt"), newId2.getBytes());

        return id;
    }
}