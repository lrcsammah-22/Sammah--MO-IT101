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

public class CalculateHoursWorked {
    public static void main(String[] args) {
        String csvFile = "attendance.csv"; // <-- name of the file
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader("attendance.csv"))) {
            // Skip header row
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                // Columns: Employee #, Last Name, First Name, Date, Log In, Log Out
                String empNo = data[0];
                String lastName = data[1];
                String firstName = data[2];
                String logIn = data[4];
                String logOut = data[5];

                // Convert times to decimal hours
                double timeIn = convertToDecimal(logIn);
                double timeOut = convertToDecimal(logOut);

                // Clamp to 8:00–17:00 as per rules
                if (timeIn < 8.0) timeIn = 8.0;
                if (timeOut > 17.0) timeOut = 17.0;

                double totalHours = timeOut - timeIn;

                // Special case: if login <= 8:05 and logout == 17:00 → count as 8 hours
                if (isWithinFiveMinutes(logIn) && logOut.equals("17:00")) {
                    totalHours = 8.0;
                }

                // Display results
                System.out.println("Employee #: " + empNo);
                System.out.println("Employee Name: " + firstName + " " + lastName);
                System.out.println("Total Hours Worked: " + totalHours);
                System.out.println("-----------------------------------");

                // Verification (optional for assignment)
                // Example: manually check one row
                if (empNo.equals("10001")) {
                    double expected = 7.0; // adjust based on manual calculation
                    if (totalHours == expected) {
                        System.out.println("Test passed: Computation is correct");
                    } else {
                        System.out.println("Test failed: Expected " + expected + " but got " + totalHours);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Convert HH:MM to decimal hours
    public static double convertToDecimal(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour + (minute / 60.0);
    }

    // Check if login is 8:00–8:05
    public static boolean isWithinFiveMinutes(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return (hour == 8 && minute <= 5);
    }
}
