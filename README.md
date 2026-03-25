This project is a Payroll System made in Java for our Computer Programming subject. It reads employee records and attendance from CSV files, then calculates salaries, deductions, and net pay. The program is designed to help us practice file handling, loops, and conditionals while applying real‑life payroll rules.

Features: 

Login system  
Two roles: employee (can view personal info) and payroll_staff (can process payroll).

Employee data  
Loads employee info (name, birthday, hourly rate) from employees.csv.

Attendance data  
Loads daily login/logout times from attendance.csv.

Grace period: arrivals before 8:10 count as 8:00.

Hours capped at 8 per day.

Payroll processing  
Splits each month (June–December) into two cutoffs:

Cutoff 1 (days 1–15): no deductions.

Cutoff 2 (days 16–30): deductions applied.

Deductions

SSS: Uses official contribution table.

PhilHealth: 3% of gross ÷ 2, capped at ₱900.

Pag‑IBIG: 2% of gross, capped at ₱100.

Tax: Based on BIR 2023 monthly tax table.

Summary report  
Shows total gross, deductions, and net salary for June–December.

How It Works
Program loads CSV files for employees, attendance, and SSS contributions.

User logs in with username and password.

If payroll staff logs in, they can process payroll for one employee or all employees.

The program calculates gross pay, applies deductions, and prints results per cutoff.

At the end, a summary of totals is displayed.

Why We Made This
This project is a simple payroll system made in Java. It was created for our client MotorPH because they need a program that can calculate salaries and deductions automatically. The system reads employee records and attendance from CSV files, then computes gross pay, SSS, PhilHealth, Pag‑IBIG, tax, and net salary.

We made this because MotorPH needs it, and at the same time this project helps me understand how Java programming works with files, loops, and conditionals. It also shows how real‑life formulas like SSS, PhilHealth, Pag‑IBIG, and BIR tax can be applied in code. Doing this project makes me learn not just coding but also how payroll systems are used in companies.

File reading (BufferedReader)
Reason why BufferedReader is used:
1. It is low cost when it comes to time because if we will read those characters from a file that has more than lines, it will be time consuming.
2. The next reason is, easier writing method when the program is run. For example, when we use it, this provides the readline method where this method is allowing the smooth flow and process of the program where we do not have to read the array of each characters from each code one by one to save time and energy.
3. BufferedReader can also save our system from crashing and lagging.
Data structures (HashMap, TreeMap)

Loops and conditionals

Real‑world formulas for deductions
