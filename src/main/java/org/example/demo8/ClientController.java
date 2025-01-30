package org.example.demo8;

import javafx.event.ActionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientController {
    public ClientApplication app;

    //users db
    public Socket usersDBClient;
    public BufferedReader usersDBIn;
    public PrintWriter usersDBOut;

    //orders db
    public Socket orderDBClient;
    public BufferedReader orderDBIn;
    public PrintWriter orderDBOut;

    public ClientController(ClientApplication app) throws IOException {
        this.app = app;

        usersDBClient = new Socket("localhost", 8080);
        usersDBIn = new BufferedReader(new InputStreamReader(usersDBClient.getInputStream()));
        usersDBOut = new PrintWriter(usersDBClient.getOutputStream(), true);

        orderDBClient = new Socket("localhost", 8081);
        orderDBIn = new BufferedReader(new InputStreamReader(orderDBClient.getInputStream()));
        orderDBOut = new PrintWriter(orderDBClient.getOutputStream(), true);
    }

    public void onLoginButton(ActionEvent event) {
        try{
            usersDBOut.println("login");
            usersDBOut.println(app.usernameTextField.getText());
            usersDBOut.println(app.passwordTextField.getText());

            String verification;
            while((verification = usersDBIn.readLine()) != null){
                if(verification.equalsIgnoreCase("fail")) {
                    app.alert.setText("login failed try again");
                    return;
                }

                else if(verification.equalsIgnoreCase("success")) {
                    app.startHome();
                    return;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onRegisterButton(ActionEvent event) {
        try{
            usersDBOut.println("register");
            usersDBOut.println(app.usernameTextField.getText());
            usersDBOut.println(app.passwordTextField.getText());

            String verification;
            while((verification = usersDBIn.readLine()) != null){
                if(verification.equalsIgnoreCase("fail"))
                    app.alert.setText("register failed try again");

                else if(verification.equalsIgnoreCase("success")) {
                    app.startHome();
                    return;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSubmitOrderButton(ActionEvent event) {
        try {
            String entitledName = app.enterEntitledName.getText();
            String description = app.description.getText();

            System.out.println("Client: Sending entitledName: " + entitledName);
            System.out.println("Client: Sending description: " + description);

            if (entitledName == null || entitledName.isEmpty() || description == null || description.isEmpty()) {
                app.homeAlert.setText("Both entitled name and description are required.");
                return;
            }

            orderDBOut.println("order"); // Notify server of order operation
            orderDBOut.println(entitledName); // Send entitled name
            orderDBOut.println(description); // Send order description

            String message = orderDBIn.readLine(); // Read response
            System.out.println("Client: Received response: " + message);

            if ("success".equalsIgnoreCase(message)) {
                app.homeAlert.setText("Order placed successfully");
            } else if ("fail".equalsIgnoreCase(message)) {
                app.homeAlert.setText("Order cannot be placed");
            } else {
                app.homeAlert.setText("Unexpected server response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            app.homeAlert.setText("An error occurred while placing the order.");
        }
    }

}