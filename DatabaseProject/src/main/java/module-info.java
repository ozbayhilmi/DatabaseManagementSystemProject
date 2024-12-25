module com.example.databaseproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.databaseproject to javafx.fxml;
    exports com.example.databaseproject;
}