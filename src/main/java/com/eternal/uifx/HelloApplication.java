package com.eternal.uifx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Eternal Application");
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(480);
        showLoginScreen();
    }

    public static void showLoginScreen() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-screen.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1440, 900);
        scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
        primaryStage.setTitle("Login - Eternal Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMainScreen() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-screen.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1440, 900);
        scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
        primaryStage.setTitle("Dashboard - Eternal Application");
        primaryStage.setScene(scene);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}