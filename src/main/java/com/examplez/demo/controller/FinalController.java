package com.examplez.demo.controller;

import com.examplez.demo.model.Game;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class FinalController {

    @FXML
    Label WinnerLabel;
    @FXML
    Button newGame;
    @FXML
    Button closeGame;
    @FXML
    private void onClickedSelected(ActionEvent event) throws IOException {
        String fxml = "/com/examplez/demo/menu-view.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        StartController controller = loader.getController();


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root,860,650));
        stage.centerOnScreen();

        stage.show();
    }
    @FXML
    private void onCloseGameSelected(ActionEvent event) throws IOException{
        Platform.exit();
    }

    public void setWinner(String winner) {
        WinnerLabel.setText( winner);
    }
}
