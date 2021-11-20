module application {

        requires javafx.fxml;
        requires javafx.controls;
        requires java.sql;
//        requires mysql.connector.java;
        opens application to javafx.graphics;
        exports application;
    exports application.model;
    opens application.model to javafx.graphics;
}