package com.eternal.uifx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    @FXML
    private Label welcomeLabel;

    @FXML
    private BarChart<String, Number> activityChart;

    @FXML
    private PieChart distributionChart;

    @FXML
    private TableView<UserActivity> activityTable;

    private String url;
    private String dbUser;
    private String dbPassword;
    private String currentUsername;

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

    // This will be called when the main screen is loaded
    public void initialize() {
        // Check which scene is currently loaded
        if (welcomeLabel != null) {
            // We're in the main screen, initialize dashboard
            initializeDashboard();
        }
    }

    private void initializeDashboard() {
        // Set welcome message with username if available
        if (currentUsername != null && !currentUsername.isEmpty()) {
            welcomeLabel.setText("Welcome back, " + currentUsername + "!");
        }

        // Initialize activity chart with sample data
        if (activityChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>("Mon", 25));
            series.getData().add(new XYChart.Data<>("Tue", 30));
            series.getData().add(new XYChart.Data<>("Wed", 27));
            series.getData().add(new XYChart.Data<>("Thu", 32));
            series.getData().add(new XYChart.Data<>("Fri", 42));
            series.getData().add(new XYChart.Data<>("Sat", 35));
            series.getData().add(new XYChart.Data<>("Sun", 28));

            activityChart.getData().add(series);
        }

        // Initialize pie chart with sample data
        if (distributionChart != null) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Active", 65),
                    new PieChart.Data("Inactive", 25),
                    new PieChart.Data("New", 10)
            );
            distributionChart.setData(pieChartData);
        }

        // Initialize table with sample data if applicable
        if (activityTable != null) {
            // This would be populated with real data from the database in a real app
        }
    }

    @FXML
    protected void handleLogout() throws Exception {
        currentUsername = null;
        HelloApplication.showLoginScreen();
    }

    @FXML
    protected void handleSignUp() throws Exception {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
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

            String sql = "INSERT INTO tb_usrdata(name, password) VALUES (? , ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

            statusLabel.setText("SignUp successful!");
            statusLabel.getStyleClass().add("status-label-success");
            statusLabel.getStyleClass().remove("status-label-error");

            ps.close();
            con.close();

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            statusLabel.getStyleClass().remove("status-label-success");
            statusLabel.getStyleClass().add("status-label-error");
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleLogin() throws Exception {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

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
                // Store username for welcome message
                currentUsername = username;

                // Switch to main screen
                HelloApplication.showMainScreen();
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

    // Simple model class for activity table
    public static class UserActivity {
        private final String user;
        private final String action;
        private final String date;

        public UserActivity(String user, String action, String date) {
            this.user = user;
            this.action = action;
            this.date = date;
        }

        public String getUser() { return user; }
        public String getAction() { return action; }
        public String getDate() { return date; }
    }
}