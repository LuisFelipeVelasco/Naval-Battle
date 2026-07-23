package com.examplez.demo.controller;

import com.examplez.demo.model.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the initial menu view (menu-view.fxml).
 * <p>
 * Currently only handles starting a brand-new game: it creates a fresh
 * {@link Game} model with both players and switches the scene to the
 * ship placement view.
 */
public class StartController {
    @FXML
    private TextField playerNameField;

    @FXML private Button InitialButton;


    @FXML
    public void handleStartGame(ActionEvent event) throws  IOException{

        String playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            InitialButton.setDisable(true);
        }
        changeGameView(event);
    }
    @FXML
    public void handleLoadGame(ActionEvent event) throws  IOException{

    }

    /**
     * Switches the current scene to the ship placement view, starting a
     * new match: creates a new {@link Game}, creates both the human and
     * machine players, and hands the game model over to the
     * {@link PlacementController} of the new scene.
     *
     * @param event the action event used to obtain the current {@link Stage}
     * @throws IOException if the placement view FXML cannot be loaded
     */
    private void changeGameView(ActionEvent event) throws IOException {
        String fxml = "/com/examplez/demo/place-ships-View.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        PlacementController controller = loader.getController();
        Game newGame = new Game();
        String playerName = playerNameField.getText().trim();

        controller.initGame(newGame,playerName);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root,860,650));
        stage.centerOnScreen();
        stage.setTitle("placement");
        stage.show();
    }
}
