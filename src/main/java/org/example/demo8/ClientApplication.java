package org.example.demo8;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    //MAIN
    public Stage primaryStage;
    public ClientController controller;

    //Start Screen
    public VBox startRoot;
    public Text welcomeText;
    public TextField usernameTextField;
    public TextField passwordTextField;
    public Button login;
    public Button register;
    public Text alert;

    //Home Screen
    public VBox homeRoot;
    public Text welcomeHome;
    public TextField enterEntitledName;
    public TextArea description;
    public Button submit;
    public Text homeAlert;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        controller = new ClientController(this);

        setUpStartScreen();
    }

    private void setUpStartScreen() {
        startRoot = new VBox(10);
        startRoot.setAlignment(Pos.CENTER);

        welcomeText = new Text("welcome to ronen's pizza");

        usernameTextField = new TextField();
        usernameTextField.setPromptText("username:");

        passwordTextField = new PasswordField();
        passwordTextField.setPromptText("password:");

        login = new Button("login");
        login.setOnAction(controller::onLoginButton);

        register = new Button("register");
        register.setOnAction(controller::onRegisterButton);

        alert = new Text("");

        startRoot.getChildren().addAll(
                welcomeText, usernameTextField, passwordTextField,
                login, register, alert
        );

        primaryStage.setScene(new Scene(startRoot, 420, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void startHome() {
        homeRoot = new VBox(10);
        homeRoot.setAlignment(Pos.CENTER);

        welcomeHome = new Text("feel more then free to place your order");

        enterEntitledName = new TextField();
        enterEntitledName.setPromptText("you real full name: ");

        description = new TextArea();
        description.setPrefHeight(200);
        description.setPrefWidth(200);
        description.setPromptText("order description: ");

        submit = new Button("submit order");
        submit.setOnAction(controller::onSubmitOrderButton);

        homeAlert = new Text();

        homeRoot.getChildren().addAll(enterEntitledName, description, submit, homeAlert);
        primaryStage.setScene(new Scene(homeRoot, 420, 300));
        primaryStage.show();
    }
}