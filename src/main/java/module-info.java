module com.examplez.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;

    opens com.examplez.demo.controller to javafx.fxml;
    exports com.examplez.demo;
    exports com.examplez.demo.model;
    exports com.examplez.demo.storage;
    exports com.examplez.demo.storage.exceptions;
}