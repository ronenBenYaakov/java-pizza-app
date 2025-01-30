package org.example.demo8;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientApplicationTester extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);

        Button client1Button = new Button("Launch Client 1");
        client1Button.setOnAction(e -> launchClient("Client 1"));

        Button client2Button = new Button("Launch Client 2");
        client2Button.setOnAction(e -> launchClient("Client 2"));

        Button client3Button = new Button("Launch Client 3");
        client3Button.setOnAction(e -> launchClient("Client 3"));

        root.getChildren().addAll(client1Button, client2Button, client3Button);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Client Application Tester");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void launchClient(String clientName) {
        Stage clientStage = new Stage();
        ClientApplication clientApp = new ClientApplication();

        try {
            // Initialize and show the client application
            clientApp.start(clientStage);
            clientStage.setTitle(clientName);
        } catch (Exception e) {
            System.err.println("Failed to launch " + clientName);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
