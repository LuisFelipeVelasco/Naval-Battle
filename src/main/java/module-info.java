module com.examplez.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.examplez.demo to javafx.fxml;
    opens Controller to javafx.fxml;
    opens Model to javafx.fxml;

    exports com.examplez.demo;
    exports Controller;
    exports Model;
}