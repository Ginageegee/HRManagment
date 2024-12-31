package com.example.javafxproject.models;

import java.sql.*;
import java.time.LocalDate;

public class ConnectDB {
    private Connection connection;

    public ConnectDB() {
        String url = "jdbc:sqlite:C:/Users/Regina/IdeaProjects/JavafxProject/employee.db";

        try {
            // Load and register the JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Establish the connection
            connection = DriverManager.getConnection(url);
            System.out.println("Connected");

            // Create the employees table if it doesn't exist
            String createEmployeesTableSQL = "CREATE TABLE IF NOT EXISTS employees ("
                    + "employee_id INTEGER PRIMARY KEY, "
                    + "first_name TEXT NOT NULL, "
                    + "last_name TEXT NOT NULL)";
            PreparedStatement pstmt = connection.prepareStatement(createEmployeesTableSQL);
            pstmt.executeUpdate();
            pstmt.close();

            String dropPaystubsTableSQL = "DROP TABLE IF EXISTS paystubs";
            PreparedStatement dropStmt4 = connection.prepareStatement(dropPaystubsTableSQL);
            dropStmt4.executeUpdate();
            dropStmt4.close();

            // create pay stub table
            String createPayStubTableSQL = "CREATE TABLE IF NOT EXISTS paystubs ("
                    + "paystub_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "employee_id INTEGER NOT NULL,"
                    + "pay_period_start_date DATE NOT NULL, "
                    + "pay_period_end_date DATE NOT NULL, "
                    + "total_hours_worked DOUBLE, "
                    + "total_overtime_hours DOUBLE, "
                    + "bonus DOUBLE, "
                    + "regular_payrate DOUBLE, "
                    + "gross_pay_total DOUBLE, "
                    + "net_pay_total DOUBLE, "
                    + "payment_date DATE, "
                    + "FOREIGN KEY(employee_id) REFERENCES employees(employee_id))";
            PreparedStatement pstmt2 = connection.prepareStatement(createPayStubTableSQL);
            pstmt2.executeUpdate();
            pstmt2.close();

            String dropTimesheetsTableSQL = "DROP TABLE IF EXISTS timesheets";
            PreparedStatement dropStmt3 = connection.prepareStatement(dropTimesheetsTableSQL);
            dropStmt3.executeUpdate();
            dropStmt3.close();

            // create timesheet table
            String createTimesheetsTableSQL = "CREATE TABLE IF NOT EXISTS timesheets ("
                    + "entry_id INTEGER PRIMARY KEY, "
                    + "employee_id INTEGER NOT NULL, "
                    + "date DATE NOT NULL, "
                    + "regular_hours_worked DOUBLE, "
                    + "overtime_hours_worked DOUBLE, "
                    + "start_time TEXT, "
                    + "end_time TEXT, "
                    + "hours_worked DOUBLE, "
                    + "FOREIGN KEY(employee_id) REFERENCES employees(employee_id))";
            PreparedStatement pstmt3 = connection.prepareStatement(createTimesheetsTableSQL);
            pstmt3.executeUpdate();
            pstmt3.close();

        } catch (SQLException e) {
            System.out.println("Error connecting to DB");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found");
            e.printStackTrace();
        }
    }

    // method for inserting data into the employees table
    public void addEmployee(int employeeId, String firstName, String lastName) {
        String insertSQL = "INSERT INTO employees (employee_id, first_name, last_name) VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.executeUpdate();
            pstmt.close();
            System.out.println("Employee added");

        } catch (SQLException e) {
            System.out.println("Error inserting employee");
            e.printStackTrace();
        }
    }

    // method for inserting data into the pay stub table
    public void generatePayStub(int employeeId, LocalDate payPeriodStart, LocalDate payPeriodEnd, double totalHoursWorked, double totalOvertimeHours, double bonus, double regularPayRate, double grossPaymentTotal, double netPaymentTotal, LocalDate paymentDate) throws SQLException {

        totalHoursWorked = calculateTotalHoursWorked(employeeId, payPeriodStart, payPeriodEnd);
        totalOvertimeHours = calculateTotalOvertimeHours(employeeId, payPeriodStart, payPeriodEnd);
        grossPaymentTotal = calculateGrossPay(employeeId, payPeriodStart, payPeriodEnd, regularPayRate);
        netPaymentTotal = grossPaymentTotal - (grossPaymentTotal * 0.15);
        bonus = (totalHoursWorked > 40) ? 200 : 0; // using a ternary operator to assign a value to bonus based on how many hours an employee has worked
        // employees that work more than 40 hours in a pay period are awarded a bonus of $200

        String insertSQL = "INSERT INTO paystubs (employee_id, pay_period_start_date, pay_period_end_date, total_hours_worked, total_overtime_hours, bonus, regular_payrate, gross_pay_total, net_pay_total, payment_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if (connection != null && !connection.isClosed()){

            try {
                PreparedStatement pstmt = connection.prepareStatement(insertSQL);
                pstmt.setInt(2, employeeId);
                pstmt.setString(3, payPeriodStart.toString());
                pstmt.setString(4, payPeriodEnd.toString());
                pstmt.setDouble(5, totalHoursWorked);
                pstmt.setDouble(6, totalOvertimeHours);
                pstmt.setDouble(7, bonus);
                pstmt.setDouble(8, regularPayRate);
                pstmt.setDouble(9, grossPaymentTotal);
                pstmt.setDouble(10, netPaymentTotal);
                pstmt.setString(11, paymentDate.toString());
                pstmt.executeUpdate();
                pstmt.close();
                System.out.println("Pay stub added");
            }catch(SQLException ex){
                System.out.println("Error inserting pay stub data: " + ex.getMessage());
                ex.printStackTrace();
            }

        }else {
            System.out.println("database connection is not valid");
        }

    }

    // method for inserting data into the timesheet table
    public void addTimesheet(int entryId, int employeeId, LocalDate date, double regularHoursWorked, double overtimeHoursWorked) {

        overtimeHoursWorked = regularHoursWorked > 8 ? regularHoursWorked - 8 : 0; // using a ternary operator to assign a value to overtimeHoursWorked based on if they've worked more than 8 hours in a day
        // regular employee hours are 8 per day, anything more is considered overtime (time and a half)

        String insertSQL = "INSERT INTO timesheets (entry_id, employee_id, date, regular_hours_worked, overtime_hours_worked) VALUES (?, ?, ?, ?, ?)";

        try{
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            pstmt.setInt(1, entryId);
            pstmt.setInt(2, employeeId);
            pstmt.setString(3, date.toString());
            pstmt.setDouble(4, regularHoursWorked);
            pstmt.setDouble(5, overtimeHoursWorked);
            pstmt.executeUpdate();
            pstmt.close();
        }catch(SQLException ex){
            System.out.println("Error inserting timesheet data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // method for calculating total hours worked
    public double calculateTotalHoursWorked(int employeeId, LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        double totalHours = 0;
        String totalHoursWorkedQuery = "SELECT SUM(regular_hours_worked) FROM timesheets WHERE employee_id = ? AND date BETWEEN ? AND ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(totalHoursWorkedQuery);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, payPeriodStart.toString());
            pstmt.setString(3, payPeriodEnd.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalHours = rs.getDouble(1);
            }

        }catch(SQLException ex){
            System.out.println("Error executing query: " + ex.getMessage());
            ex.printStackTrace();
        }

        return totalHours;
    }

    // method for calculating overtime hours
    private double calculateTotalOvertimeHours(int employeeId, LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        double totalOvertime = 0;
        String totalOvertimeQuery = "SELECT SUM(overtime_hours_worked) FROM timesheets WHERE employee_id = ? AND date BETWEEN ? AND ?";
        try (PreparedStatement pstmt = connection.prepareStatement(totalOvertimeQuery)) {
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, payPeriodStart.toString());
            pstmt.setString(3, payPeriodEnd.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalOvertime = rs.getDouble(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error executing query: " + ex.getMessage());
            ex.printStackTrace();
        }
        return totalOvertime;
    }

    // method for calculating gross pay (pay before deductions)
    private double calculateGrossPay(int employeeId, LocalDate payPeriodStart, LocalDate payPeriodEnd, double regularPayRate) {
        double totalHoursWorked = calculateTotalHoursWorked(employeeId, payPeriodStart, payPeriodEnd);
        double totalOvertimeHours = calculateTotalOvertimeHours(employeeId, payPeriodStart, payPeriodEnd);
        return (totalHoursWorked * regularPayRate) + (totalOvertimeHours * regularPayRate * 1.5); // 1.5 is for overtime (time and a half)
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                System.out.println("Closing connection");
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

