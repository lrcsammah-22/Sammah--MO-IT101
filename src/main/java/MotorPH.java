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
import java.io.IOException;
import java.util.Scanner;

public class MotorPH {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Step 1: Login
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if ((username.equals("employee") || username.equals("payroll_staff"))
            && password.equals("12345")) {

            // Step 2: Employee Menu
            if (username.equals("employee")) {
                System.out.println("1. Enter your employee number");
                System.out.println("2. Exit the program");

                System.out.print("Choose option: ");
                int choice = sc.nextInt();

                if (choice == 1) {
                    System.out.print("Enter employee number: ");
                    String empNo = sc.next();

                    boolean found = false;

                    // Employee details lookup from CSV
                    try (BufferedReader br = new BufferedReader(new FileReader("employees.csv"))) {
                        String line;
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
                    } catch (IOException e) {
                        System.out.println("Error reading employee file.");
                    }

                    if (!found) {
                        System.out.println("Employee number does not exist.");
                    }
                } else {
                    System.out.println("Program terminated.");
                }
            }

            // Step 2: Payroll Staff Menu (we’ll expand this in Step 3 later)
            else if (username.equals("payroll_staff")) {
                System.out.println("1. Process Payroll");
                System.out.println("2. Exit the program");
            }

        } else {
            System.out.println("Incorrect username and/or password.");
        }

        sc.close();
    }
}
