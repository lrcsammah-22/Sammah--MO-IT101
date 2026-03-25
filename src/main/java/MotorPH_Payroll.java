/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author lisma
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class MotorPH_Payroll {

    // One Scanner for all user input
    static Scanner input = new Scanner(System.in);

    // Employee info from employees.csv
    // Key = employee number, Value = [lastName, firstName, birthday, hourlyRate]
    static HashMap<String, String[]> employees = new HashMap<>();

    // Attendance info from attendance.csv
    // Key = employee number, Value = Map of date → hours worked
    static Map<Integer, Map<String, Double>> attendance = new HashMap<>();

    // SSS contribution table from CSV
    static TreeMap<Double, Double> sssTable = new TreeMap<>();

    // ===================== MAIN =====================
    public static void main(String[] args) {
        loadEmployees("employees.csv");
        loadAttendance("attendance.csv");
        loadSSS("SSS Contribution.csv");
        login();
        input.close();
    }

    // ===================== LOGIN =====================
    // Checks username and password, then shows the right menu
    static void login() {
        System.out.println("===== MotorPH Login ======");
        System.out.print("Username: ");
        String username = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        if (!password.equals("12345") ||
            (!username.equals("employee") && !username.equals("payroll_staff"))) {
            System.out.println("Incorrect username or password!");
            return;
        }

        if (username.equals("employee")) {
            employeeMenu();
        } else {
            payrollMenu();
        }
    }

    // ===================== LOADERS =====================
    // Reads employee records from CSV
    static void loadEmployees(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim().replaceAll("^\"|\"$", "");
                }
                String empNo = parts[0];
                String lastName = parts[1];
                String firstName = parts[2];
                String birthday = parts[3];
                String hourlyRate = parts[18].replace(",", "");
                employees.put(empNo, new String[]{lastName, firstName, birthday, hourlyRate});
            }
        } catch (Exception e) {
            System.out.println("Error loading employees.");
        }
    }

    // Reads attendance records from CSV
    static void loadAttendance(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim().replaceAll("^\"|\"$", "");
                }

                int empNo = Integer.parseInt(parts[0]);
                String date = parts[3];
                String loginStr = parts[4];
                String logoutStr = parts[5];

                // Parse login/logout times
                String[] loginParts = loginStr.split(":");
                int loginHour = Integer.parseInt(loginParts[0]);
                int loginMin = Integer.parseInt(loginParts[1]);

                String[] logoutParts = logoutStr.split(":");
                int logoutHour = Integer.parseInt(logoutParts[0]);
                int logoutMin = Integer.parseInt(logoutParts[1]);

                // Grace period: 8:00–8:10 counts as 8:00
                if (loginHour == 8 && loginMin <= 10) {
                    loginHour = 8;
                    loginMin = 0;
                }

                // Compute hours worked
                double hours = (logoutHour + logoutMin / 60.0) - (loginHour + loginMin / 60.0);
                if (hours > 8.0) hours = 8.0; // cap at 8 hours
                if (hours <= 0) continue;     // skip bad data

                attendance.putIfAbsent(empNo, new HashMap<>());
                attendance.get(empNo).put(date, hours);
            }
        } catch (Exception e) {
            System.out.println("Error loading attendance.");
        }
    }

    // Reads SSS contribution table from CSV
    static void loadSSS(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim().replaceAll("^\"|\"$", "").replace(",", "");
                }
                if (parts[0].equalsIgnoreCase("Below 3250")) {
                    sssTable.put(0.0, Double.parseDouble(parts[3]));
                    continue;
                }
                if (parts[2].equalsIgnoreCase("Over")) continue;
                double rangeMin = Double.parseDouble(parts[0]);
                double contribution = Double.parseDouble(parts[3]);
                sssTable.put(rangeMin, contribution);
            }
        } catch (Exception e) {
            System.out.println("Error loading SSS table.");
        }
    }

    // ===================== MENUS =====================
    static void employeeMenu() {
        System.out.println("1. Enter Employee Number");
        System.out.println("2. Exit");
        int choice = input.nextInt();
        input.nextLine();
        if (choice == 2) return;
        if (choice == 1) {
            System.out.print("Enter Employee Number: ");
            String empNo = input.nextLine();
            String[] emp = employees.get(empNo);
            if (emp == null) {
                System.out.println("Employee not found.");
                return;
            }
            System.out.println("Employee Number: " + empNo);
            System.out.println("Name: " + emp[1] + " " + emp[0]);
            System.out.println("Birthday: " + emp[2]);
        }
    }

    static void payrollMenu() {
        System.out.println("1. Process Payroll");
        System.out.println("2. Exit");
        int choice = input.nextInt();
        input.nextLine();
        if (choice == 2) return;
        if (choice == 1) {
            System.out.println("1. One employee");
            System.out.println("2. All employees");
            int sub = input.nextInt();
            input.nextLine();
            if (sub == 1) {
                System.out.print("Enter Employee Number: ");
                String empNo = input.nextLine();
                String[] emp = employees.get(empNo);
                if (emp == null) {
                    System.out.println("Employee not found.");
                    return;
                }
                processPayroll(empNo, emp);
            } else if (sub == 2) {
                for (Map.Entry<String, String[]> entry : employees.entrySet()) {
                    processPayroll(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    // ===================== PAYROLL =====================
    static void processPayroll(String empNo, String[] emp) {
        int empID = Integer.parseInt(empNo);
        Map<String, Double> empAttendance = attendance.get(empID);
        if (empAttendance == null) {
            System.out.println("No attendance records for: " + emp[1] + " " + emp[0]);
            return;
        }

        System.out.println("========================================");
        System.out.println("Processing payroll for: " + emp[1] + " " + emp[0]);
        System.out.println("Employee ID: " + empNo);
        System.out.printf("Hourly Rate: PHP %.2f%n", Double.parseDouble(emp[3]));

        double totalGross = 0, totalNet = 0, totalSSS = 0, totalPH = 0, totalPI = 0, totalTax = 0;
        double rate = Double.parseDouble(emp[3]);

        for (int month = 6; month <= 12; month++) {
            double hoursFirstHalf = 0, hoursSecondHalf = 0;
            for (Map.Entry<String, Double> entry : empAttendance.entrySet()) {
                String[] dateParts = entry.getKey().split("/");
                int attMonth = Integer.parseInt(dateParts[0]);
                int attDay = Integer.parseInt(dateParts[1]);
                if (attMonth != month) continue;
                if (attDay <= 15) hoursFirstHalf += entry.getValue();
                else hoursSecondHalf += entry.getValue();
            }
                        if (hoursFirstHalf == 0 && hoursSecondHalf == 0) continue;

            // Gross pay for each cutoff
            double grossFirstHalf = hoursFirstHalf * rate;
            double grossSecondHalf = hoursSecondHalf * rate;
            double monthlyGross = grossFirstHalf + grossSecondHalf;

            // Deductions based on monthly gross
            double sss = computeSSS(monthlyGross);
            double philHealth = computePhilHealth(monthlyGross);
            double pagIbig = computePagIbig(monthlyGross);
            double taxableIncome = monthlyGross - (sss + philHealth + pagIbig);
            double tax = computeTax(taxableIncome);

            // Net salary for cutoff 2 (deductions applied here)
            double netSecondHalf = grossSecondHalf - (sss + philHealth + pagIbig + tax);

            // Add to totals
            totalGross += monthlyGross;
            totalNet += grossFirstHalf + netSecondHalf;
            totalSSS += sss;
            totalPH += philHealth;
            totalPI += pagIbig;
            totalTax += tax;

            String monthName = getMonthName(month);

            // Print cutoff 1 (no deductions)
            System.out.println("\n--- Cutoff: " + monthName + " 1 to 15 ---");
            System.out.printf("Hours Worked: %.2f%n", hoursFirstHalf);
            System.out.printf("Gross Salary: PHP %.2f%n", grossFirstHalf);
            System.out.printf("Net Salary:   PHP %.2f%n", grossFirstHalf);

            // Print cutoff 2 (with deductions)
            System.out.println("\n--- Cutoff: " + monthName + " 16 to 30 ---");
            System.out.printf("Hours Worked: %.2f%n", hoursSecondHalf);
            System.out.printf("Gross Salary: PHP %.2f%n", grossSecondHalf);
            System.out.println("Deductions:");
            System.out.printf("  SSS:        PHP %.2f%n", sss);
            System.out.printf("  PhilHealth: PHP %.2f%n", philHealth);
            System.out.printf("  Pag-IBIG:   PHP %.2f%n", pagIbig);
            System.out.printf("  Tax:        PHP %.2f%n", tax);
            System.out.printf("Net Salary:   PHP %.2f%n", netSecondHalf);
        }

        // ===================== SUMMARY =====================
        System.out.println("\n========================================");
        System.out.println("PAYROLL SUMMARY (June - December)");
        System.out.println("========================================");
        System.out.printf("Total Gross Salary: PHP %.2f%n", totalGross);
        System.out.printf("Total SSS:          PHP %.2f%n", totalSSS);
        System.out.printf("Total PhilHealth:   PHP %.2f%n", totalPH);
        System.out.printf("Total Pag-IBIG:     PHP %.2f%n", totalPI);
        System.out.printf("Total Tax:          PHP %.2f%n", totalTax);
        System.out.printf("Total Net Salary:   PHP %.2f%n", totalNet);
        System.out.println("========================================");
    }

    // ===================== DEDUCTIONS =====================
    static double computeSSS(double monthlyGross) {
        Double bracket = sssTable.floorKey(monthlyGross);
        if (bracket == null) return 135.00; // minimum
        return sssTable.get(bracket);
    }

    static double computePhilHealth(double monthlyGross) {
        double share = (monthlyGross * 0.03) / 2.0;
        if (share < 150.00) share = 150.00;
        if (share > 900.00) share = 900.00;
        return share;
    }

    static double computePagIbig(double monthlyGross) {
        double contribution = monthlyGross * 0.02;
        return Math.min(contribution, 100.00);
    }

    static double computeTax(double taxableIncome) {
        if (taxableIncome <= 20832) return 0;
        else if (taxableIncome <= 33332) return (taxableIncome - 20832) * 0.20;
        else if (taxableIncome <= 66667) return 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome <= 166667) return 10833.33 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome <= 666667) return 40833.33 + (taxableIncome - 166667) * 0.32;
        else return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    // ===================== UTILITIES =====================
    static String getMonthName(int month) {
        switch (month) {
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "Unknown";
        }
    }
}


