module application {

        requires javafx.fxml;
        requires javafx.controls;
        requires java.sql;
        requires mysql.connector.java;
        opens application to javafx.graphics, javafx.fxml, java.sql, mysql.connector.java;
        opens application.model to javafx.base;
        opens application.controller to javafx.fxml, javafx.controls;
}