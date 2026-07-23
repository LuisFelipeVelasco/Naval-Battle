module com.examplez.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;

    opens com.examplez.demo.controller to javafx.fxml;
    exports com.examplez.demo;
    exports com.examplez.demo.model;
}