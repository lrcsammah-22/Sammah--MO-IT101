/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author lisma
 */
public class ComputeDeductions {
    public static void main(String[] args) {
        // Step 1: Declare employee details
        String employeeName = "Manuel III Garcia";
        int employeeID = 10001;
        String position = "Chief Executive Officer";
        double basicSalary = 90000;      // Monthly basic salary
        double riceSubsidy = 1500;       // Monthly rice subsidy
        double phoneAllowance = 2000;    // Monthly phone allowance
        double clothingAllowance = 1000; // Monthly clothing allowance

        // Step 2: Compute gross semi-monthly rate (half of monthly salary + half of allowances)
        double grossSemiMonthlyRate = (basicSalary / 2)
                                    + (riceSubsidy / 2)
                                    + (phoneAllowance / 2)
                                    + (clothingAllowance / 2);

        // Step 3: Validate input
        if (grossSemiMonthlyRate <= 0) {
            System.out.println("Invalid input. Gross salary must be positive.");
        } else {
            // Step 4: Call computeNetPay() to handle deductions and net pay
            computeNetPay(employeeID, employeeName, position, basicSalary, grossSemiMonthlyRate);
        }
    }

    // Method: Compute SSS deduction based on contribution table
    public static double computeSSS(double gross) {
        double contribution = 0;
        if (gross < 3250) contribution = 135.00;
        else if (gross <= 3750) contribution = 157.50;
        else if (gross <= 4250) contribution = 180.00;
        else if (gross <= 4750) contribution = 202.50;
        else if (gross <= 5250) contribution = 225.00;
        else if (gross <= 5750) contribution = 247.50;
        else if (gross <= 6250) contribution = 270.00;
        else if (gross <= 6750) contribution = 292.50;
        else if (gross <= 7250) contribution = 315.00;
        else if (gross <= 7750) contribution = 337.50;
        else if (gross <= 8250) contribution = 360.00;
        else if (gross <= 8750) contribution = 382.50;
        else if (gross <= 9250) contribution = 405.00;
        else if (gross <= 9750) contribution = 427.50;
        else if (gross <= 10250) contribution = 450.00;
        else if (gross <= 10750) contribution = 472.50;
        else if (gross <= 11250) contribution = 495.00;
        else if (gross <= 11750) contribution = 517.50;
        else if (gross <= 12250) contribution = 540.00;
        else if (gross <= 12750) contribution = 562.50;
        else if (gross <= 13250) contribution = 585.00;
        else if (gross <= 13750) contribution = 607.50;
        else if (gross <= 14250) contribution = 630.00;
        else if (gross <= 14750) contribution = 652.50;
        else if (gross <= 15250) contribution = 675.00;
        else if (gross <= 15750) contribution = 697.50;
        else if (gross <= 16250) contribution = 720.00;
        else if (gross <= 16750) contribution = 742.50;
        else if (gross <= 17250) contribution = 765.00;
        else if (gross <= 17750) contribution = 787.50;
        else if (gross <= 18250) contribution = 810.00;
        else if (gross <= 18750) contribution = 832.50;
        else if (gross <= 19250) contribution = 855.00;
        else if (gross <= 19750) contribution = 877.50;
        else if (gross <= 20250) contribution = 900.00;
        else if (gross <= 20750) contribution = 922.50;
        else if (gross <= 21250) contribution = 945.00;
        else if (gross <= 21750) contribution = 967.50;
        else if (gross <= 22250) contribution = 990.00;
        else if (gross <= 22750) contribution = 1012.50;
        else if (gross <= 23250) contribution = 1035.00;
        else if (gross <= 23750) contribution = 1057.50;
        else if (gross <= 24250) contribution = 1080.00;
        else if (gross <= 24750) contribution = 1102.50;
        else contribution = 1125.00; // Over 24,750
        return contribution;
    }

    // Method: Compute PhilHealth contribution (3% of monthly salary, capped, employee share = 50%)
    public static double computePhilHealth(double monthlyBasicSalary) {
        double monthlyPremium = monthlyBasicSalary * 0.03;
        if (monthlyBasicSalary <= 10000) monthlyPremium = 300.00;
        else if (monthlyBasicSalary >= 60000) monthlyPremium = 1800.00;
        return monthlyPremium / 2; // Employee share
    }

    // Method: Compute Pag-IBIG contribution (1% if 1,000–1,500, 2% if >1,500, capped at 100)
    public static double computePagIbig(double monthlyBasicSalary) {
        double contribution = 0;
        if (monthlyBasicSalary >= 1000 && monthlyBasicSalary <= 1500) {
            contribution = monthlyBasicSalary * 0.01;
        } else if (monthlyBasicSalary > 1500) {
            contribution = monthlyBasicSalary * 0.02;
        }
        if (contribution > 100) contribution = 100; // Cap
        return contribution;
    }

    // Method: Compute Withholding Tax based on taxable income
    public static double computeIncomeTax(double taxableIncome) {
        double tax = 0;
        if (taxableIncome <= 20832) tax = 0;
        else if (taxableIncome <= 33333) tax = (taxableIncome - 20833) * 0.20;
        else if (taxableIncome <= 66667) tax = 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome <= 166667) tax = 10833 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome <= 666667) tax = 40833.33 + (taxableIncome - 166667) * 0.32;
        else tax = 200833.33 + (taxableIncome - 666667) * 0.35;
        return tax;
    }

    // Method: Compute Net Pay by calling all deduction methods
    public static void computeNetPay(int employeeID, String employeeName, String position,
                                     double monthlyBasicSalary, double grossSemiMonthlyRate) {
        double sss = computeSSS(grossSemiMonthlyRate);
        double philHealth = computePhilHealth(monthlyBasicSalary);
        double pagIbig = computePagIbig(monthlyBasicSalary);

        // Taxable income = gross salary - (SSS + PhilHealth + Pag-IBIG)
        double taxableIncome = grossSemiMonthlyRate - (sss + philHealth + pagIbig);
        double tax = computeIncomeTax(taxableIncome);

        double totalDeductions = sss + philHealth + pagIbig + tax;
        double netPay = grossSemiMonthlyRate - totalDeductions;

        // Display results
        System.out.println("=== Payroll Deductions ===");
        System.out.println("SSS Deduction: PHP " + String.format("%.2f", sss));
        System.out.println("PhilHealth Deduction: PHP " + String.format("%.2f", philHealth));
        System.out.println("Pag-IBIG Deduction: PHP " + String.format("%.2f", pagIbig));
        System.out.println("Withholding Tax: PHP " + String.format("%.2f", tax));
        System.out.println("Total Deductions: PHP " + String.format("%.2f", totalDeductions));
        System.out.println("Net Pay: PHP " + String.format("%.2f", netPay));
        System.out.println("Computation verified successfully!");
    }
}

