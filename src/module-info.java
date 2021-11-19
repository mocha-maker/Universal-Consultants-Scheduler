module application {

        requires javafx.fxml;
        requires javafx.controls;
        opens application to javafx.graphics;
        exports application;
        }