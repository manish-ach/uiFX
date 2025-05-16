module com.eternal.uifx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens com.eternal.uifx to javafx.fxml;
    exports com.eternal.uifx;
}