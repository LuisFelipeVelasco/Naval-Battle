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

/**
 * Controller class for the final screen/view of the application.
 * Handles the display of the game winner and provides navigation
 * options to start a new game or close the application.
 */
public class FinalController {

    @FXML
    Label WinnerLabel;

    @FXML
    Button newGame;

    @FXML
    Button closeGame;

    @FXML
    Label statusLabel;

    /**
     * Handles the event when the "New Game" button is selected.
     * Loads the main menu view and transitions the current stage to it.
     *
     * @param event The action event triggered by clicking the new game button
     * @throws IOException If the FXML file for the menu view cannot be loaded
     */
    @FXML
    private void onClickedSelected(ActionEvent event) throws IOException {
        String fxml = "/com/examplez/demo/menu-view.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1100, 720));
        stage.centerOnScreen();

        stage.show();
    }

    /**
     * Handles the event when the "Close Game" button is selected.
     * Exits the JavaFX application gracefully.
     *
     * @param event The action event triggered by clicking the close game button
     * @throws IOException If an I/O error occurs during the exit process
     */
    @FXML
    private void onCloseGameSelected(ActionEvent event) throws IOException {
        Platform.exit();
    }

    /**
     * Sets the winner's name to be displayed on the final screen.
     *
     * @param winner The name of the winning player or a message indicating the result
     */
    public void setWinner(String winner) {
        WinnerLabel.setText(winner);
    }

    /**
     * Sets the player status to be displayed on the final screen.
     *
     * @param status The status of the command
     */
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

}