package com.storemanager;

import com.storemanager.dao.CustomerDAO;
import com.storemanager.dao.StaffDAO;
import com.storemanager.db.DBconnector;
import com.storemanager.model.users.*;
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
        Scene scene = new Scene(fxmlLoader.load(), 980.0, 620.0);//320x240
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

//    public static void main(String[] args) {
//        launch(); // Launch the JavaFX application
//    }

    public static void main(String[] args) {
        // generic staff read
        StaffDAO staffDAO = new StaffDAO();
        int staffId = 1;
        User user = staffDAO.read(staffId);
        if (user != null) {
            System.out.println("User fetched successfully!");
            System.out.println("Name: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Position: " + user.getClass().getSimpleName());
        } else {
            System.out.println("No user found with the provided staffId.");
        }


        // customer  read
        CustomerDAO customerDAO = new CustomerDAO();
        int customerID = 2;
        Customer customer = customerDAO.read(customerID);
        if (customer != null) {
            System.out.println("Customer fetched successfully!");
            System.out.println("Username: " + customer.getUsername());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Address: " + customer.getAddress());
            System.out.println("Phone Number: " + customer.getPhoneNumber());
            System.out.println("Cart ID: " + customer.getShoppingCart().getCartId());
        } else {
            System.out.println("No customer found with the provided customerID.");
        }
    }



}