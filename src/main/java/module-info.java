module com.example.asterotechnologies {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.asterotechnologies to javafx.fxml;
    exports com.example.asterotechnologies;
}