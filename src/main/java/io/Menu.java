package io;

import model.AccessToken;

import java.io.IOException;
import java.util.Scanner;

public class Menu {

    Order order = new Order();

    public void showMenu() throws IOException {
        System.out.println("MENU: ");
        System.out.println("1. Zamówienia.");
        System.out.println("2. Zamówienia z Allegro.");
        System.out.println("3. Zamknij program.");

        Scanner sc = new Scanner(System.in);
        System.out.println("Wybierz jedną z opcji(1-3): ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                showOrderMenu();
                break;
            case 2:
                showAllegroOrderMenu();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Opcja niedostępna");
                showMenu();
        }
    }

    public void showOrderMenu() throws IOException {
        System.out.println("MENU: ");
        System.out.println("1. Podgląd zamówień.");
        System.out.println("2. Dodaj zamówienie.");
        System.out.println("3. Edytuj zamówienie.");
        System.out.println("4. Usuń zamówienie.");
        System.out.println("5. Wróć do poprzedniego menu.");
        System.out.println("6. Zamknij program.");

        Scanner sc = new Scanner(System.in);
        System.out.println("Wybierz jedną z opcji(1-6): ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                order.show();
                break;
            case 2:
                order.saveToFile(order.readAndRiseId());
                break;
            case 3:
                System.out.println("Edytujemy zamówienie na podstawie podanego ID");
                showOrderMenu();
                break;
            case 4:
                System.out.println("----- USUWANIE ZAMÓWIENIA -----");
                System.out.println("Podaj ID zamówienia: ");
                String id = sc.nextLine();
                order.delete(id);
                break;
            case 5:
                showMenu();
                break;
            case 6:
                System.exit(0);
                break;
            default:
                System.out.println("Opcja niedostępna");
                showOrderMenu();
        }
    }

    public void showAllegroOrderMenu() throws IOException {
        System.out.println("MENU: ");
        System.out.println("1. Podgląd zamówień.");
        System.out.println("2. Wróć do poprzedniego menu.");
        System.out.println("3. Zamknij program.");

        Scanner sc = new Scanner(System.in);
        System.out.println("Wybierz jedną z opcji(1-3): ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                String authUrl = "https://allegro.pl.allegrosandbox.pl/auth/oauth/device";
                AccessToken access = new AccessToken();
                break;
            case 2:
                showMenu();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Opcja niedostępna");
                showAllegroOrderMenu();
        }
    }
}