package com.examplez.demo.controller;

import com.examplez.demo.storage.GameFileManager;
import com.examplez.demo.storage.GameState;
import com.examplez.demo.model.Game;
import javafx.application.Platform;
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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    @FXML Button loadGameButton;

    @FXML Label statusLabel;


    /**
     * Handles the "Start Game" action. Validates that the player name is not empty,
     * then transitions to the ship placement view with a new game.
     *
     * @param event the {@link ActionEvent} triggered by clicking the start new game button
     * @throws IOException if the placement view FXML cannot be loaded
     */
    @FXML
    public void handleStartGame(ActionEvent event) throws IOException {
        if (GameFileManager.isAGameSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Overwrite save file");
            alert.setHeaderText("A previously saved game exists.");
            alert.setContentText("If you start a new game, you will lose your saved game progress. Do you wish to continue?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // El usuario canceló la acción
            }
            // Si confirma, se elimina la partida anterior
            GameFileManager.deleteGame();
        }

        String playerName = playerNameField.getText().trim();
        if (!playerName.isEmpty()) {
            String typeOfUser=askTypeOfUser();
            changeGameView(event,typeOfUser);
        }
        labelName.setText("First , identify yourself command !");

    }

    /**
     * Handles the explicit user click on the "LOAD GAME" button.
     * <p>
     * Verifies that a valid save file is present and delegates the session restoration
     * process to transition directly to the battle scene.
     * </p>
     *
     * @param event the {@link ActionEvent} triggered by clicking the load game button
     * @throws IOException if an error occurs while loading the target FXML view
     */
    @FXML
    public void handleLoadGame(ActionEvent event) throws  IOException{
        if(GameFileManager.isAGameSaved()){
            loadSavedGameAndTransition(event);
        }
    }

    /**
     * JavaFX lifecycle method. Runs automatically after the FXML layout has been loaded.
     * <p>
     * Checks if a save file exists on disk to update the UI controls (enabling/disabling
     * the load button and updating status labels). If a saved game is found, it schedules
     * an automatic confirmation dialog using {@link Platform#runLater(Runnable)}.
     * </p>
     */
    @FXML
    public void initialize(){
        boolean hasSavedGame = GameFileManager.isAGameSaved();

        if (loadGameButton != null) loadGameButton.setDisable(!hasSavedGame);

        if (statusLabel != null) {
            statusLabel.setText(hasSavedGame ? "SAVED CAMPAIGN DETECTED" : "NO SAVED CAMPAIGN DETECTED");
        }

        // If a saved game exists, automatically prompt the user via an AlertBox.
        if (hasSavedGame) {
            Platform.runLater(this::promptLoadSavedGameOnStartup);
        }
    }

    /**
     * Prompts the player with a confirmation dialog on startup when an existing saved game is detected.
     * <p>
     * Displays custom buttons allowing the player to choose between loading their last saved campaign
     * or remaining on the main menu to start a fresh match.
     * </p>
     */
    private void promptLoadSavedGameOnStartup() {
        String savedNickname = GameFileManager.loadNicknamePlayerHuman();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Saved game detected");
        alert.setHeaderText("You have a battle underway, Commander " + savedNickname + "!");
        alert.setContentText("Do you want to resume your last saved session or start a new game?");

        ButtonType buttonLoad = new ButtonType("Load Game");
        ButtonType buttonNew = new ButtonType("New Game");

        alert.getButtonTypes().setAll(buttonLoad, buttonNew);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonLoad) {
            loadSavedGameAndTransition(null);
        }
    }

    /**
     * Restores the saved game session state (boards, turn count, and nicknames)
     * and transitions directly to the battle combat view ({@code PlayView.fxml}).
     *
     * @param event the {@link ActionEvent} originating from a manual button click,
     *              or {@code null} if invoked programmatically from the startup dialog prompt
     */
    private void loadSavedGameAndTransition(ActionEvent event) {
        try {
            GameState loadedState = GameFileManager.loadGame();
            String nickname = GameFileManager.loadNicknamePlayerHuman();

            Game gameModel = new Game();
            gameModel.restoreBoards(loadedState.getPlayerBoard(), loadedState.getPlayerMachineBoard());

            //TASK: descomentar esta línea de código una vez se implementen los setter y getters para nickname en Player
            //gameModel.getPlayerHuman().setNickname(nickname);

            String fxml = "/com/examplez.demo/PlayView.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            PlayController controller = loader.getController();
            controller.restoreLoadedTurn(GameFileManager.loadTurn());
            controller.setGameModel(gameModel);
            controller.loadBoard();

            Stage stage;
            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) loadGameButton.getScene().getWindow();
            }

            stage.setScene(new Scene(root, 860, 650));
            stage.centerOnScreen();
            stage.setTitle("Naval Battle - Combat");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error loading the saved game.");
            errorAlert.showAndWait();
        }
    }

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