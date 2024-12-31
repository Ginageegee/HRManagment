package com.example.javafxproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class Controller {
    // Properties
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField employeeIdField;
    @FXML
    private ListView<String> listView;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField hoursWorkedField;
    @FXML
    private DatePicker StartDateField;
    @FXML
    private DatePicker endDateField;
    @FXML
    private TextField payRateField;

    private Connection connection;

    public ListView<String> listview;

    // Functionality for the human resource button in the main menu
    @FXML
    protected void onHumanResourceClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("human_resource.fxml")); // Redirects to human resource page
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Functionality for the payroll button in the main menu
    @FXML
    protected void onPayrollClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("payroll.fxml")); // Redirects to payroll page
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onBackToMenuClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view.fxml")); // Redirects back to main menu
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    // add an employee
    @FXML
    protected void onAddClick(ActionEvent event) {

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String employeeId = employeeIdField.getText().trim();

        // input validation
        if (firstName.isEmpty() || lastName.isEmpty() || employeeId.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }

        String url = "jdbc:sqlite:C:/Users/Regina/IdeaProjects/JavafxProject/employee.db";
        String checkSql = "SELECT COUNT(*) FROM employees WHERE employee_id = ?";
        String insertSql = "INSERT INTO employees (employee_id, first_name, last_name) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setInt(1, Integer.parseInt(employeeId));
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert("Validation Error", "An employee with this ID already exists.");
                return;
            }

            insertStmt.setInt(1, Integer.parseInt(employeeId));
            insertStmt.setString(2, firstName);
            insertStmt.setString(3, lastName);
            insertStmt.executeUpdate();
            showAlert("Success", "Employee added successfully.");

            firstNameField.clear();
            lastNameField.clear();
            employeeIdField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add the employee.");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert("Input Error", "Employee ID must be a valid number.");
        }
    }

    // Remove an employee
    @FXML
    protected void onRemoveClick(ActionEvent event) {
        String employeeId = employeeIdField.getText();

        if (employeeId.isEmpty()) {
            showAlert("Validation Error", "Please enter employee ID");
            return;
        }
        String url = "jdbc:sqlite:C:/Users/Regina/IdeaProjects/JavafxProject/employee.db"; // Ensure this is the same path used in ConnectDB
        String checkSql = "SELECT COUNT(*) FROM employees WHERE employee_id = ?";
        String deleteSql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement checkStmt = connection.prepareStatement(checkSql);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {

            // Check if employee ID exists
            checkStmt.setInt(1, Integer.parseInt(employeeId));
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                showAlert("Validation Error", "Employee does not exist");
                return;
            }

            // Remove employee from database
            deleteStmt.setInt(1, Integer.parseInt(employeeId));
            deleteStmt.executeUpdate();
            showAlert("Success", "Employee removed successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to remove employee");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert("Input Error", "Employee ID must be a number");
        }
    }

    // Update an employee's information
    @FXML
    protected void onUpdateClick(ActionEvent event) {
        String employeeId = employeeIdField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (employeeId.isEmpty()) {
            showAlert("Validation Error", "Please enter employee ID");
            return;
        }

        String url = "jdbc:sqlite:C:/Users/Regina/IdeaProjects/JavafxProject/employee.db"; // Ensure this is the same path used in ConnectDB
        String checkSql = "SELECT COUNT(*) FROM employees WHERE employee_id = ?";
        String updateSql = "UPDATE employees SET first_name = ?, last_name = ? WHERE employee_id = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement checkStmt = connection.prepareStatement(checkSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {

            checkStmt.setInt(1, Integer.parseInt(employeeId));
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                showAlert("Validation Error", "Employee does not exist");
                return;
            }

            updateStmt.setString(1, firstName);
            updateStmt.setString(2, lastName);
            updateStmt.setInt(3, Integer.parseInt(employeeId));
            updateStmt.executeUpdate();
            showAlert("Success", "Employee updated successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update employee");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert("Input Error", "Employee ID must be a number");
        }
    }

    // View all employees
    @FXML
    protected void onViewClick(ActionEvent event) {
        ObservableList<String> data = FXCollections.observableArrayList();
        String url = "jdbc:sqlite:C:/Users/Regina/IdeaProjects/JavafxProject/employee.db";
        String selectSql = "SELECT employee_id, first_name, last_name FROM employees";

        try (Connection connection = DriverManager.getConnection(url);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {

            while (rs.next()) {
                String employeeInfo = "ID: " + rs.getInt("employee_id") + ", Name: " + rs.getString("first_name") + " " + rs.getString("last_name");
                data.add(employeeInfo);
            }

            listView.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to retrieve employee data");
        }
    }

    //add attendance
    @FXML
    protected void onMarkAttendanceClick(ActionEvent event) {
        String employeeId = employeeIdField.getText();
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";
        String hoursWorked = hoursWorkedField.getText();

        if (employeeId.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || date.isEmpty() || hoursWorked.isEmpty()) {
            showAlert(" Error", "Enter all fields.");
            return;
        }

        String url = "jdbc:sqlite:C:/Users/Regina/IdeaProjects/JavafxProject/employee.db";
        String insertSql = "INSERT INTO timesheets (employee_id, date, start_time, end_time, hours_worked) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            // Add attendance for the employee
            insertStmt.setInt(1, Integer.parseInt(employeeId));
            insertStmt.setString(2, date);
            insertStmt.setString(3, startTime);
            insertStmt.setString(4, endTime);
            insertStmt.setDouble(5, Double.parseDouble(hoursWorked));
            insertStmt.executeUpdate();
            showAlert("Success", "Attendance is added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("DB Error", "Failed to add the attendance");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(" Error", "Make sure employee ID must be a number");
        }
    }

    @FXML
    protected void onPaystubsClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("paystub.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    @FXML
    protected void onTimesheetClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("timetable.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    @FXML
    protected void onPaystubViewClick(ActionEvent event) throws SQLException {
        ObservableList<String> data = FXCollections.observableArrayList();
        String url = "jdbc:sqlite:C:/Users/Regina/IdeaProjects/JavafxProject/employee.db";
        String selectSql = "SELECT * FROM paystubs";
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            connection = DriverManager.getConnection(url);
            stmt = connection.createStatement();
            rs = stmt.executeQuery(selectSql);

            while (rs.next()) {
                String employeeInfo = "ID: " + rs.getInt("employee_id")
                        + ", Pay Period: " + rs.getString("pay_period_start_date") + " to " + rs.getString("pay_period_end_date")
                        + ", Hours Worked: " + rs.getDouble("total_hours_worked")
                        + ", Overtime: " + rs.getDouble("total_overtime_hours")
                        + ", Bonus: $" + rs.getDouble("bonus")
                        + ", Pay Rate: $" + rs.getDouble("regular_payrate")
                        + ", Gross Pay: $" + rs.getDouble("gross_pay_total")
                        + ", Net Pay: $" + rs.getDouble("net_pay_total")
                        + ", Payment Date: " + rs.getString("payment_date");
                data.add(employeeInfo);
            }
            listView.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to retrieve employee data");

        }finally { // closing our resources once they have been used
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace(); }
            } if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onPaystubsAddClick() {
        // Validate inputs
        try {
            int employeeId = Integer.parseInt(employeeIdField.getText());
            LocalDate startDate = StartDateField.getValue();
            LocalDate endDate = endDateField.getValue();
            double regularPayRate = Double.parseDouble(payRateField.getText());

            if (startDate == null || endDate == null) {
                showAlert("Input Error", "Start and End dates must be selected.");
                return;
            }

            // SQL to calculate hours and insert paystub
            String calculateHoursSQL = "SELECT SUM(regular_hours_worked) AS total_regular_hours, " +
                    "SUM(overtime_hours_worked) AS total_overtime_hours " +
                    "FROM timesheets WHERE employee_id = ? AND date BETWEEN ? AND ?";
            String insertPaystubSQL = "INSERT INTO paystubs (employee_id, pay_period_start_date, pay_period_end_date, " +
                    "total_hours_worked, total_overtime_hours, regular_payrate, gross_pay_total, net_pay_total) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            // Establish database connection
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/Regina/IdeaProjects/JavafxProject/employee.db");
                 PreparedStatement pstmt1 = connection.prepareStatement(calculateHoursSQL);
                 PreparedStatement pstmt2 = connection.prepareStatement(insertPaystubSQL)) {

                // Calculate hours
                pstmt1.setInt(1, employeeId);
                pstmt1.setString(2, startDate.toString());
                pstmt1.setString(3, endDate.toString());
                ResultSet rs = pstmt1.executeQuery();

                double totalRegularHours = rs.getDouble("total_regular_hours");
                double totalOvertimeHours = rs.getDouble("total_overtime_hours");
                double grossPay = totalRegularHours * regularPayRate + totalOvertimeHours * (regularPayRate * 1.5);
                double netPay = grossPay; // Adjust for deductions if necessary

                // Insert paystub
                pstmt2.setInt(1, employeeId);
                pstmt2.setString(2, startDate.toString());
                pstmt2.setString(3, endDate.toString());
                pstmt2.setDouble(4, totalRegularHours);
                pstmt2.setDouble(5, totalOvertimeHours);
                pstmt2.setDouble(6, regularPayRate);
                pstmt2.setDouble(7, grossPay);
                pstmt2.setDouble(8, netPay);
                pstmt2.executeUpdate();

                showAlert("Success", "Paystub added successfully.");
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Ensure all fields are filled and correctly formatted.");
            e.printStackTrace();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add paystub.");
            e.printStackTrace();
        }
    }




    }