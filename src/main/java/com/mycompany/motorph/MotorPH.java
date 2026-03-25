package com.mycompany.motorph;

/**
 *
 * @author Pia
 */

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.Scanner;

public class MotorPH {

    // Centralized file location
    private static final String CSV_FILE = "Employees.csv";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (!(password.equals("12345") &&
                (username.equals("employee") || username.equals("payroll_staff")))) {
            System.out.println("Incorrect username and/or password.");
            return;
        }

        if (username.equals("employee")) {
            System.out.println("1. Enter your employee number\n2. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                System.out.print("Enter employee number: ");
                displayEmployee(sc.next());
            }
        } else if (username.equals("payroll_staff")) {
            System.out.println("1. Process Payroll\n2. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                System.out.println("1. One employee\n2. All employees\n3. Exit");
                int sub = sc.nextInt();
                if (sub == 1) {
                    System.out.print("Enter employee number: ");
                    processPayroll(sc.next());
                } else if (sub == 2) {
                    processAllPayroll();
                }
            }
        }
    }

    // ==========================================
    // DATA CLEANING HELPER (Prevents Zero Values)
    // ==========================================
    private static String clean(String data) {
        if (data == null) return "";
        return data.replace("\"", "").trim();
    }

    // =========================
    // DISPLAY EMPLOYEE
    // =========================
    public static void displayEmployee(String empNo) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (clean(data[0]).equals(empNo)) {
                    System.out.println("\n--- Employee Information ---");
                    System.out.println("ID: " + clean(data[0]));
                    System.out.println("Name: " + clean(data[2]) + " " + clean(data[1]));
                    System.out.println("Birthday: " + clean(data[3]));
                    found = true;
                    break;
                }
            }
            if (!found) System.out.println("Employee " + empNo + " not found.");
        } catch (IOException e) {
            System.out.println("Error: Could not read " + CSV_FILE);
        }
    }

    // =========================
    // PROCESS PAYROLL
    // =========================
    public static void processPayroll(String empNo) {
        String name = "";
        double hourlyRate = 0;
        double totalHours = 0;
        boolean found = false;

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].contains("Employee")) continue; // Skip header

                String currentID = clean(data[0]);
                if (currentID.equals(empNo)) {
                    found = true;
                    name = clean(data[2]) + " " + clean(data[1]);
                    
                    // Column 19 is typically Hourly Rate
                    String rateStr = clean(data[19]);
                    hourlyRate = Double.parseDouble(rateStr);

                    // Logic for reading Attendance within the SAME file
                    // Note: This assumes your CSV layout has date/time in columns 3, 4, 5
                    try {
                        LocalTime logIn = LocalTime.parse(clean(data[4]), timeFormat);
                        LocalTime logOut = LocalTime.parse(clean(data[5]), timeFormat);

                        // Normalize to 8:00 - 17:00
                        if (logIn.isBefore(LocalTime.of(8, 0))) logIn = LocalTime.of(8, 0);
                        if (logOut.isAfter(LocalTime.of(17, 0))) logOut = LocalTime.of(17, 0);

                        double hours = Duration.between(logIn, logOut).toMinutes() / 60.0;
                        if (hours > 4) hours -= 1.0; // Lunch deduction
                        totalHours += hours;
                    } catch (Exception e) {
                        // Skip rows that don't have valid date/time data
                    }
                }
            }

            if (!found) {
                System.out.println("No data found for Employee #" + empNo);
                return;
            }

            // Calculations
            double gross = totalHours * hourlyRate;
            double sss = computeSSS(gross);
            double phil = computePhilHealth(gross);
            double pagibig = computePagIbig(gross);
            double tax = computeWithholdingTax(gross - (sss + phil + pagibig));
            double net = gross - (sss + phil + pagibig + tax);

            System.out.println("\n================================");
            System.out.println("Employee: " + name + " [" + empNo + "]");
            System.out.println("Total Hours Worked: " + String.format("%.2f", totalHours));
            System.out.println("Hourly Rate: " + hourlyRate);
            System.out.println("--------------------------------");
            System.out.println("Gross Salary: " + String.format("%.2f", gross));
            System.out.println("Deductions: SSS=" + sss + " PhilHealth=" + phil + " Pag-IBIG=" + pagibig);
            System.out.println("Withholding Tax: " + String.format("%.2f", tax));
            System.out.println("NET SALARY: " + String.format("%.2f", net));
            System.out.println("================================\n");

        } catch (Exception e) {
            System.out.println("Error processing payroll: " + e.getMessage());
        }
    }

    public static void processAllPayroll() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].contains("Employee")) continue;
                processPayroll(clean(data[0]));
            }
        } catch (IOException e) {
            System.out.println("Batch error: " + e.getMessage());
        }
    }

    // Deductions Logic
    public static double computeSSS(double gross) { return (gross < 3250) ? 135.00 : 1125.00; }
    public static double computePhilHealth(double gross) { return (gross * 0.03) / 2; }
    public static double computePagIbig(double gross) { return Math.min(100.0, gross * 0.02); }
    public static double computeWithholdingTax(double taxable) { 
        return (taxable <= 20832) ? 0.0 : (taxable - 20833) * 0.20; 
    }
}