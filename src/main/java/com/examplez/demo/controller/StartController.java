package com.examplez.demo.controller;

import com.examplez.demo.model.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the initial menu view (menu-view.fxml).
 * <p>
 * Currently only handles starting a brand-new game: it creates a fresh
 * {@link Game} model with both players and switches the scene to the
 * ship placement view.
 */
public class StartController {

    /**
     * Text field where the player enters their name.
     */
    @FXML
    private TextField playerNameField;

    /** Label
     * Button that starts a new game.
     */
    @FXML
    private Button InitialButton;

    @FXML
    private Label labelName;


    /**
     * Handles the "Start Game" action. Validates that the player name is not empty,
     * then transitions to the ship placement view with a new game.
     *
     * @param event the action event triggered by clicking the start button
     * @throws IOException if the placement view FXML cannot be loaded
     */
    @FXML
    public void handleStartGame(ActionEvent event) throws IOException {
        String playerName = playerNameField.getText().trim();
        if (!playerName.isEmpty()) {
            String typeOfUser=askTypeOfUser();
            changeGameView(event,typeOfUser);
        }
        labelName.setText("First , identify yourself command !");

    }

    /**
     * Handles the "Load Game" action. (Currently a placeholder for future implementation.)
     *
     * @param event the action event triggered by clicking the load button
     * @throws IOException if an error occurs during the load process
     */
    @FXML
    public void handleLoadGame(ActionEvent event) throws IOException {
        // Not yet implemented
    }

    /**
     * Opens a choice dialog that asks the human player to assign a value to an
     * ace card.
     *
     * @return selected ace value, or {@code 1} when the dialog is dismissed
     */

    public String askTypeOfUser() {
        ChoiceDialog<String> dialog =
                new ChoiceDialog<>("Player", List.of("Player", "Verificator"));
        dialog.setTitle("Ace");
        dialog.setHeaderText("Choose the value of the Ace");
        dialog.setContentText("Value:");
        return dialog.showAndWait().orElse("Player");
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
    private void changeGameView(ActionEvent event ,String typeOfUser) throws IOException {
        String fxml = "/com/examplez/demo/place-ships-view.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        PlacementController controller = loader.getController();
        Game newGame = new Game();
        String playerName = playerNameField.getText().trim();

        controller.initGame(newGame, playerName , typeOfUser);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 920));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("placement");
        stage.show();
    }
}