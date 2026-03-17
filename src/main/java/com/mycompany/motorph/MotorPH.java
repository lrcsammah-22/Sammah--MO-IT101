/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.motorph;
        
/**
 *
 * @author lisma
 */

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.Scanner;

public class MotorPH {

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
            System.out.println("1. Enter your employee number");
            System.out.println("2. Exit");

            int choice = sc.nextInt();

            if (choice == 1) {
                System.out.print("Enter employee number: ");
                String empNo = sc.next();
                displayEmployee(empNo);
            }
        }

        else if (username.equals("payroll_staff")) {
            System.out.println("1. Process Payroll");
            System.out.println("2. Exit");

            int choice = sc.nextInt();

            if (choice == 1) {
                System.out.println("1. One employee");
                System.out.println("2. All employees");
                System.out.println("3. Exit");

                int sub = sc.nextInt();

                if (sub == 1) {
                    System.out.print("Enter employee number: ");
                    String empNo = sc.next();
                    processPayroll(empNo);
                } else if (sub == 2) {
                    processAllPayroll();
                }
            }
        }
    }

    // =========================
    // DISPLAY EMPLOYEE
    // =========================
    public static void displayEmployee(String empNo) {
        try (BufferedReader br = new BufferedReader(new FileReader("employees.csv"))) {
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(empNo)) {
                    System.out.println("Employee Number: " + data[0]);
                    System.out.println("Employee Name: " + data[2] + " " + data[1]);
                    System.out.println("Birthday: " + data[3]);
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Employee number does not exist.");
            }

        } catch (IOException e) {
            System.out.println("Error reading employee file.");
        }
    }

    // =========================
    // PROCESS ONE EMPLOYEE
    // =========================
    public static void processPayroll(String empNo) {

        String name = "";
        String birthday = "";
        double hourlyRate = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("employees.csv"))) {
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                if (data[0].equals("Employee #")) continue;
                
                String emp = data[0].replace("\"","").trim();
                
                if (emp.equals(empNo)) {
                    name = data[2] + " " + data[1];
                    birthday = data[3];
                    hourlyRate = Double.parseDouble(data[18].replace("\"","").trim());
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Employee number does not exist.");
                return;
            }

        } catch (IOException e) {
            System.out.println("Error reading employee file.");
            return;
        }

        double cutoff1Hours = 0;
        double cutoff2Hours = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("attendance.csv"))) {
            String line;

            while ((line = br.readLine()) != null) {
    String[] data = line.split(",");

    // ✅ ADD THIS LINE (skip header)
    if (data[0].equals("Employee #")) continue;

    // ✅ CLEAN DATA
    String emp = data[0].replace("\"", "").trim();
    String dateStr = data[3].replace("\"", "").trim();
    String logInStr = data[4].replace("\"", "").trim();
    String logOutStr = data[5].replace("\"", "").trim();

    // ✅ USE CLEANED VALUE
    if (!emp.equals(empNo)) continue;

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
LocalDate date = LocalDate.parse(dateStr.trim(), dateFormat);

                // Only June–December
                if (date.getMonthValue() < 6 || date.getMonthValue() > 12) continue;

                LocalTime logIn = LocalTime.parse(logInStr);
                LocalTime logOut = LocalTime.parse(logOutStr);

                LocalTime start = LocalTime.of(8, 0);
                LocalTime end = LocalTime.of(17, 0);

                if (logIn.isBefore(start)) logIn = start;
                if (logOut.isAfter(end)) logOut = end;

                double hours = Duration.between(logIn, logOut).toMinutes() / 60.0;

                if (date.getDayOfMonth() <= 15)
                    cutoff1Hours += hours;
                else
                    cutoff2Hours += hours;
            }

        } catch (Exception e) {
            System.out.println("Error reading attendance file.");
        }

        // =========================
        // OUTPUT
        // =========================
        System.out.println("\n================================");
        System.out.println("Employee Number: " + empNo);
        System.out.println("Employee Name: " + name);
        System.out.println("Birthday: " + birthday);

        // FIRST CUTOFF
        double gross1 = cutoff1Hours * hourlyRate;
        System.out.println("\nCutoff: June 1 - 15");
        System.out.println("Total Hours: " + cutoff1Hours);
        System.out.println("Gross Salary: " + gross1);
        System.out.println("Net Salary: " + gross1);

        // SECOND CUTOFF
        double gross2 = cutoff2Hours * hourlyRate;

        double sss = computeSSS(gross2);
        double phil = computePhilHealth(gross2);
        double pagibig = computePagIbig(gross2);
        double tax = computeWithholdingTax(gross2 - (sss + phil + pagibig));

        double net2 = gross2 - (sss + phil + pagibig + tax);

        System.out.println("\nCutoff: June 16 - 30");
        System.out.println("Total Hours: " + cutoff2Hours);
        System.out.println("Gross Salary: " + gross2);
        System.out.println("SSS: " + sss);
        System.out.println("PhilHealth: " + phil);
        System.out.println("Pag-IBIG: " + pagibig);
        System.out.println("Tax: " + tax);
        System.out.println("Net Salary: " + net2);
    }

    // =========================
    // PROCESS ALL EMPLOYEES
    // =========================
    public static void processAllPayroll() {
        try (BufferedReader br = new BufferedReader(new FileReader("employees.csv"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                processPayroll(data[0]);
            }

        } catch (IOException e) {
            System.out.println("Error reading employee file.");
        }
    }

    // =========================
    // DEDUCTIONS
    // =========================
    public static double computeSSS(double grossSalary) {
    if (grossSalary < 3250) return 135.00;
    else if (grossSalary <= 3750) return 157.50;
    else if (grossSalary <= 4250) return 180.00;
    else if (grossSalary <= 4750) return 202.50;
    else if (grossSalary <= 5250) return 225.00;
    else if (grossSalary <= 5750) return 247.50;
    else if (grossSalary <= 6250) return 270.00;
    else if (grossSalary <= 6750) return 292.50;
    else if (grossSalary <= 7250) return 315.00;
    else if (grossSalary <= 7750) return 337.50;
    else if (grossSalary <= 8250) return 360.00;
    else if (grossSalary <= 8750) return 382.50;
    else if (grossSalary <= 9250) return 405.00;
    else if (grossSalary <= 9750) return 427.50;
    else if (grossSalary <= 10250) return 450.00;
    else if (grossSalary <= 10750) return 472.50;
    else if (grossSalary <= 11250) return 495.00;
    else if (grossSalary <= 11750) return 517.50;
    else if (grossSalary <= 12250) return 540.00;
    else if (grossSalary <= 12750) return 562.50;
    else if (grossSalary <= 13250) return 585.00;
    else if (grossSalary <= 13750) return 607.50;
    else if (grossSalary <= 14250) return 630.00;
    else if (grossSalary <= 14750) return 652.50;
    else if (grossSalary <= 15250) return 675.00;
    else if (grossSalary <= 15750) return 697.50;
    else if (grossSalary <= 16250) return 720.00;
    else if (grossSalary <= 16750) return 742.50;
    else if (grossSalary <= 17250) return 765.00;
    else if (grossSalary <= 17750) return 787.50;
    else if (grossSalary <= 18250) return 810.00;
    else if (grossSalary <= 18750) return 832.50;
    else if (grossSalary <= 19250) return 855.00;
    else if (grossSalary <= 19750) return 877.50;
    else if (grossSalary <= 20250) return 900.00;
    else if (grossSalary <= 20750) return 922.50;
    else if (grossSalary <= 21250) return 945.00;
    else if (grossSalary <= 21750) return 967.50;
    else if (grossSalary <= 22250) return 990.00;
    else if (grossSalary <= 22750) return 1012.50;
    else if (grossSalary <= 23250) return 1035.00;
    else if (grossSalary <= 23750) return 1057.50;
    else if (grossSalary <= 24250) return 1080.00;
    else if (grossSalary <= 24750) return 1102.50;
    else return 1125.00; // 
}

    public static double computePhilHealth(double grossSalary) {
    // Premium rate is 3% of monthly salary
    double premium = grossSalary * 0.03;

    // Apply minimum and maximum rules
    if (grossSalary <= 10000) {
        premium = 300.00; // minimum contribution
    } else if (grossSalary >= 60000) {
        premium = 1800.00; // maximum contribution
    }

    // Employee pays half, employer pays half
    return premium / 2;
    }

    public static double computePagIbig(double grossSalary) {
    double contribution;

    if (grossSalary >= 1000 && grossSalary <= 1500) {
        contribution = grossSalary * 0.01; // 1% employee share
    } else if (grossSalary > 1500) {
        contribution = grossSalary * 0.02; // 2% employee share
    } else {
        contribution = 0.0; // below 1000, no contribution
    }

    // Cap at 100 pesos
    if (contribution > 100) {
        contribution = 100.0;
    }

    return contribution;
    }

    public static double computeWithholdingTax(double taxableIncome) {
    if (taxableIncome <= 20832) {
        return 0.0;
    } else if (taxableIncome <= 33333) {
        return (taxableIncome - 20833) * 0.20;
    } else if (taxableIncome <= 66667) {
        return 2500 + (taxableIncome - 33333) * 0.25;
    } else if (taxableIncome <= 166667) {
        return 10833 + (taxableIncome - 66667) * 0.30;
    } else if (taxableIncome <= 666667) {
        return 40833.33 + (taxableIncome - 166667) * 0.32;
    } else {
        return 200833.33 + (taxableIncome - 666667) * 0.35;
    }
    
    }
    
}