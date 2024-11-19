package com.storemanager;

import com.storemanager.db.DBconnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {
    @Override


    public void start(Stage stage) throws IOException {
        // Test the database connection
        DBconnector.testConnection();

        // test reading from db
        DBconnector.printAllUsernames();

        // Load the FXML for the main application UI
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}