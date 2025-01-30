package org.example.demo8;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class StoreApplication extends Application {
    public Stage primaryStage;
    public StoreController controller;
    public VBox root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        controller = new StoreController(this);
        orderPanel();
    }

    public void orderPanel() throws IOException {
        root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Connect to the order database
        controller.connectToOrderDB();

        // Schedule periodic updates
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> controller.updateOrderColors());
            }
        }, 0, 1000); // Update every 1 second
    }
}
