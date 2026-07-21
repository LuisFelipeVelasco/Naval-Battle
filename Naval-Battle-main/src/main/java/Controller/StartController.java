package Controller;

import Model.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController {

    @FXML private Button InitialButton;

    @FXML
    private void onMouseClicked(ActionEvent event) throws IOException {
        changeGameView(event);
    }

    /**
     * Cambia la escena actual hacia la vista de colocación de barcos,
     * iniciando una partida nueva.
     */
    private void changeGameView(ActionEvent event) throws IOException {
        String fxml = "/com/examplez/demo/PlaceShips-View.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        PlacementController controller = loader.getController();
        Game newGame = new Game();
        newGame.createMachinePlayer();
        newGame.createHumanPlayer();
        controller.initGame(newGame);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
