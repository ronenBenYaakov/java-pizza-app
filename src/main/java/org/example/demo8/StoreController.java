package org.example.demo8;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;

public class StoreController {
    private final StoreApplication app;

    public StoreController(StoreApplication app) {
        this.app = app;
    }

    public void connectToOrderDB() {
        new Thread(() -> {
            try (Socket orderDBClient = new Socket("localhost", 8081);
                 BufferedReader in = new BufferedReader(new InputStreamReader(orderDBClient.getInputStream()));
                 PrintWriter out = new PrintWriter(orderDBClient.getOutputStream(), true)) {

                // Send request to fetch orders
                out.println("get");

                // Read the orders and their respective times from the server
                JSONObject orders = new JSONObject(in.readLine());
                JSONObject ordersTimes = new JSONObject(in.readLine());

                // Log the incoming JSON data for debugging
                System.out.println("Orders: " + orders.toString());
                System.out.println("Orders Times: " + ordersTimes.toString());

                // Update the UI on the JavaFX thread
                Platform.runLater(() -> updateUIWithOrders(orders, ordersTimes));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUIWithOrders(JSONObject orders, JSONObject ordersTimes) {
        // Iterate over the orders and add them to the UI
        for (String orderId : orders.keySet()) {
            HBox orderBox = new HBox(10);
            orderBox.setAlignment(Pos.CENTER_LEFT);

            // Handle missing keys gracefully
            String orderDetails = orders.optString(orderId, "No details available");
            String orderTimeStr = ordersTimes.optString(orderId, "No time available");

            Text orderText = new Text(orderDetails);
            Text orderIdText = new Text(orderId);
            Text orderTimeText = new Text(orderTimeStr);
            orderTimeText.setFill(Color.GREEN); // Initially set to green

            // Finish button to remove the order from the list
            Button finishButton = new Button("Finish Order");
            finishButton.setOnAction(event -> app.root.getChildren().remove(orderBox));

            // Add order details and finish button to the order box
            orderBox.getChildren().addAll(orderText, orderIdText, orderTimeText, finishButton);

            // Add the order box to the root of the application
            app.root.getChildren().add(orderBox);
        }
    }

    public void updateOrderColors() {
        // Iterate through all HBoxes in the root
        for (Object box : app.root.getChildren()) {
            if (box instanceof HBox orderBox && orderBox.getChildren().get(2) instanceof Text timeText) {
                try {
                    // Extract order time from the Text node
                    String timeStr = timeText.getText();
                    LocalDateTime orderTime = LocalDateTime.parse(timeStr);

                    // Calculate elapsed time
                    Duration elapsed = Duration.between(orderTime, LocalDateTime.now());

                    // Update text color based on elapsed time
                    if (elapsed.toMillis() > 5000 && elapsed.toMillis() <= 10000) {
                        timeText.setFill(Color.YELLOW);
                    } else if (elapsed.toMillis() > 10000) {
                        timeText.setFill(Color.RED);
                    } else {
                        timeText.setFill(Color.GREEN);
                    }
                } catch (Exception e) {
                    // Handle errors such as invalid date formats
                    System.err.println("Error updating order colors: " + e.getMessage());
                }
            }
        }
    }
}
