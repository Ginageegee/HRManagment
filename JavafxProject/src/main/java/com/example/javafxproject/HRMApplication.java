//Regina Slonimsky 101491915
//Olympia Santaguida-Antunes 101469745
//Sofia Beliak 101469384


 package com.example.javafxproject;

import com.example.javafxproject.models.ConnectDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HRMApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HRMApplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Welcome to the Human Resources and Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Initialize the database connection and create the table
        ConnectDB db = new ConnectDB();

        // Launch the JavaFX application
        launch();

        // Close the database connection
        db.closeConnection();
    }
}
