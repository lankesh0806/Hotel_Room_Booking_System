package Hotel_Room_Booking_System;

import java.io.*;
import java.util.*;

class AdminLogin {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    public static boolean login(Scanner sc) {
        System.out.println("===== Admin Login =====");
        System.out.print("Enter username: ");
        String user = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();
        if (user.equals(USERNAME) && pass.equals(PASSWORD)) {
            System.out.println("Login successful!\n");
            return true;
        } else {
            System.out.println("Invalid credentials.\n");
            return false;
        }
    }
}

class Booking {
    String customerName;
    String phoneNumber;
    String email;
    String roomType;
    int nights;
    double pricePerNight;

    public Booking(String customerName, String phoneNumber, String email, String roomType, int nights, double pricePerNight) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.roomType = roomType;
        this.nights = nights;
        this.pricePerNight = pricePerNight;
    }

    public String toString() {
        return customerName + "," + phoneNumber + "," + email + "," + roomType + "," + nights + "," + pricePerNight;
    }

    public static Booking fromString(String line) {
        String[] parts = line.split(",");
        return new Booking(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), Double.parseDouble(parts[5]));
    }
}

public class HotelRoomBookingSystem {
    static Scanner sc = new Scanner(System.in);

    static Map<String, Double> roomPrices = new HashMap<>();

    public static void main(String[] args) {
        roomPrices.put("Deluxe", 2500.0);
        roomPrices.put("Super Deluxe", 3500.0);
        roomPrices.put("Suite", 5000.0);
        roomPrices.put("Standard", 1500.0);

        if (!AdminLogin.login(sc)) return;

        int choice;
        do {
            System.out.println("===== Hotel Room Booking System =====");
            System.out.println("1. Book a Room");
            System.out.println("2. View Available Room Types");
            System.out.println("3. View All Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Search Booking by Customer Name or Phone Number");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1: bookRoom(); break;
                case 2: viewRoomTypes(); break;
                case 3: viewBookings(); break;
                case 4: cancelBooking(); break;
                case 5: searchBooking(); break;
                case 6: System.out.println("Exiting system..."); break;
                default: System.out.println("Invalid choice.\n");
            }
        } while (choice != 6);
    }

    static void bookRoom() {
        System.out.print("Enter customer name: ");
        String name = sc.nextLine();
        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();

        viewRoomTypes();
        System.out.print("Choose room type: ");
        String type = sc.nextLine();

        if (!roomPrices.containsKey(type)) {
            System.out.println("Invalid room type.\n");
            return;
        }

        System.out.print("Enter number of nights: ");
        int nights = Integer.parseInt(sc.nextLine());
        double price = roomPrices.get(type);

        Booking booking = new Booking(name, phone, email, type, nights, price);

        try (FileWriter fw = new FileWriter("bookings.txt", true)) {
            fw.write(booking.toString() + "\n");
            System.out.println("Room booked successfully.\n");
        } catch (IOException e) {
            System.out.println("Error while booking room.");
        }
    }

    static void viewRoomTypes() {
        System.out.println("===== Room Types and Pricing =====");
        for (Map.Entry<String, Double> entry : roomPrices.entrySet()) {
            System.out.println("Room Type: " + entry.getKey() + ", Price per Night: Rs." + entry.getValue());
        }
        System.out.println();
    }

    static void viewBookings() {
        System.out.println("===== All Bookings =====");
        try (BufferedReader br = new BufferedReader(new FileReader("bookings.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Booking b = Booking.fromString(line);
                double total = b.nights * b.pricePerNight;
                System.out.println("Customer: " + b.customerName + ", Phone: " + b.phoneNumber + ", Email: " + b.email + ", Room Type: " + b.roomType + ", Nights: " + b.nights + ", Total: Rs." + total);
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("No bookings found.\n");
        }
    }

    static void cancelBooking() {
        System.out.print("Enter customer name to cancel booking: ");
        String name = sc.nextLine();
        File inputFile = new File("bookings.txt");
        File tempFile = new File("temp.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             FileWriter fw = new FileWriter(tempFile)) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Booking b = Booking.fromString(line);
                if (!b.customerName.equalsIgnoreCase(name)) {
                    fw.write(b.toString() + "\n");
                } else {
                    found = true;
                }
            }
            fw.flush();
            fw.close();
            br.close();

            if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                if (found) System.out.println("Booking cancelled successfully.\n");
                else System.out.println("Booking not found.\n");
            } else {
                System.out.println("Error replacing the booking file.\n");
            }
        } catch (IOException e) {
            System.out.println("Error cancelling booking.");
        }
    }

    static void searchBooking() {
        System.out.print("Enter customer name or phone number to search: ");
        String input = sc.nextLine();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader("bookings.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Booking b = Booking.fromString(line);
                if (b.customerName.equalsIgnoreCase(input) || b.phoneNumber.equals(input)) {
                    double total = b.nights * b.pricePerNight;
                    System.out.println("Booking Found:");
                    System.out.println("Customer: " + b.customerName + ", Phone: " + b.phoneNumber + ", Email: " + b.email + ", Room Type: " + b.roomType + ", Nights: " + b.nights + ", Total: Rs." + total + "\n");
                    found = true;
                    break;
                }
            }
            if (!found) System.out.println("Booking not found.\n");
        } catch (IOException e) {
            System.out.println("Error searching booking.");
        }
    }
}
