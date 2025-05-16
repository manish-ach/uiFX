package com.eternal.uifx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class HelloController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private String url;
    private String dbUser;
    private String dbPassword;

    public HelloController() {
        loadDatabaseConfig();
    }

    private void loadDatabaseConfig() {
        Properties prop = new Properties();
        String configFilePath = "config.properties";

        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            prop.load(fis);

            // Get properties values
            url = prop.getProperty("db.url");
            dbUser = prop.getProperty("db.user");
            dbPassword = prop.getProperty("db.password");

        } catch (IOException e) {
            System.err.println("Failed to load database configuration: " + e.getMessage());
            url = "jdbc:mysql://localhost:3306/db_java";
            dbUser = "";
            dbPassword = "";
        }
    }

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty");
            statusLabel.getStyleClass().remove("status-label-success");
            statusLabel.getStyleClass().add("status-label-error");
            return;
        }

        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }

            // Connect to database
            Connection con = DriverManager.getConnection(url, dbUser, dbPassword);

            String sql = "SELECT * FROM tb_usrdata WHERE name=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                statusLabel.setText("Login successful!");
                statusLabel.getStyleClass().remove("status-label-error");
                statusLabel.getStyleClass().add("status-label-success");
            } else {
                statusLabel.setText("Invalid username or password.");
                statusLabel.getStyleClass().remove("status-label-success");
                statusLabel.getStyleClass().add("status-label-error");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            statusLabel.getStyleClass().remove("status-label-success");
            statusLabel.getStyleClass().add("status-label-error");
            e.printStackTrace();
        }
    }
}