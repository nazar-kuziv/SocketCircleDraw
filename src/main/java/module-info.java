module com.example.kol_2_2023 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.kol_2_2023 to javafx.fxml;
    exports com.example.kol_2_2023;
}