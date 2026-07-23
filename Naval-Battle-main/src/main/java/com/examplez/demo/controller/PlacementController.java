package com.examplez.demo.controller;

import com.examplez.demo.model.*;
import com.examplez.demo.model.exceptions.InvalidPositionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Map;
import java.util.HashMap;

import java.io.IOException;

/**
 * Controller for the ship placement view (PlaceShips-View.fxml).
 * <p>
 * Handles the whole placement phase for the human player: drawing the
 * empty 10x10 board, letting the player drag ships from the pending
 * fleet list and drop them on the board, rotating the current
 * orientation, and, once the whole fleet is placed, triggering the
 * machine's own (random) placement and switching to the play view.
 */
public class PlacementController {
    private static final Map<String, Image> SHIP_IMAGES = new HashMap<>();
    static {
        SHIP_IMAGES.put("carrier", new Image(PlacementController.class.getResourceAsStream("/Images/carrier.png")));
        SHIP_IMAGES.put("submarine", new Image(PlacementController.class.getResourceAsStream("/Images/submarine.png")));
        SHIP_IMAGES.put("destructor", new Image(PlacementController.class.getResourceAsStream("/Images/destructor.png")));
        SHIP_IMAGES.put("frigate", new Image(PlacementController.class.getResourceAsStream("/Images/frigate.png")));
    }
    private static final double CELL_SIZE = 35;
    private static final double GAP = 2;
    @FXML private GridPane boardGrid;
    @FXML private ListView<Ship> pendingShipsListView;
    @FXML private Button rotateButton;

    @FXML private Button startMatchButton;
    Game gameModel;
    private boolean horizontal = true;
    private static final int SIZE = 10;

    /**
     * JavaFX lifecycle method, called automatically right after the FXML
     * is loaded. Builds the empty board cells and disables the
     * "start match" button until the whole fleet has been placed.
     */
    @FXML
    private void initialize(){
        createCells();
        startMatchButton.setDisable(true);

    }

    /**
     * Initializes the placement phase with a freshly created game model.
     * Retrieves the human player from the game, creates its empty board
     * and fleet, and fills the pending ships list view with the fleet
     * that still needs to be placed.
     *
     * @param game the game model created by {@link StartController}
     */
    public void initGame(Game game){
        this.gameModel = game;
        game.startPlacement();
        pendingShipsListView.setItems(FXCollections.observableArrayList(gameModel.getPlayerHuman().getShips()));
        setDragFromListView();
    }

    /**
     * Creates the 100 visual cells of the board as {@link StackPane}
     * nodes, each one listening for drag-over and drag-dropped events so
     * that ships can be dropped on them.
     */
    private void createCells(){
        for (int fila = 0; fila < SIZE; fila++){
            for (int columna = 0; columna < SIZE; columna++){
                StackPane celdaVisual = new StackPane();
                Rectangle fondo = new Rectangle(35, 35);
                fondo.setFill(Color.LIGHTBLUE);
                fondo.setStroke(Color.GRAY);
                celdaVisual.getChildren().add(fondo);

                final int f = fila;
                final int c = columna;
                celdaVisual.setOnDragOver(this::onDragOver);
                celdaVisual.setOnDragDropped(evento -> onDragDropped(evento, f, c));

                boardGrid.add(celdaVisual, columna, fila);
            }
        }
    }
    /**
     * Enables dragging a ship out of the pending ships list view. The
     * currently selected ship's type is stored on the {@link Dragboard}
     * so the drop target can identify what is being dragged, although
     * the actual ship instance used for placement is read directly from
     * the list view's selection model when the drop happens.
     */
    private void setDragFromListView(){
        pendingShipsListView.setOnDragDetected(event -> {
            Ship shipSelected = pendingShipsListView.getSelectionModel().getSelectedItem();
            if (shipSelected == null) return;

            Dragboard dragboard = pendingShipsListView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(shipSelected.getType());
            dragboard.setContent(content);
            event.consume();
        });
    }

    /**
     * Toggles the orientation used for the next ship placement, between
     * horizontal and vertical.
     */
    @FXML
    private void onRotateButton(){
        horizontal = !horizontal;
    }

    /**
     * Handles the "start match" button: makes the machine player create
     * its own board and place its fleet randomly, then switches the
     * scene to the play view so the shooting phase can begin.
     *
     * @param event the action event used to obtain the current stage
     */
    @FXML
    private void onStartMatchButton(ActionEvent event){
        changePlayGameView(event);
        gameModel.startMatch();
    }

    /**
     * Accepts a drag-over event on a board cell, as long as the drag
     * gesture did not originate from the board itself.
     *
     * @param event the drag event to accept
     */
    private void onDragOver(DragEvent event){
        if (event.getGestureSource() != boardGrid){
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    /**
     * Handles dropping the currently selected ship onto the given board
     * cell: attempts to place it on the human player's board, and if
     * valid, draws the ship shape, removes it from the pending list and
     * enables the "start match" button once the whole fleet is placed.
     * If the position is invalid, shows an error instead.
     *
     * @param event  the drag-dropped event
     * @param row    the target row index
     * @param column the target column index
     */
    private void onDragDropped(DragEvent event, int row, int column){
        Ship shipSelected = pendingShipsListView.getSelectionModel().getSelectedItem();
        PlayerHuman playerHuman= gameModel.getPlayerHuman();

        if (shipSelected != null){
            try {
                playerHuman.placeShipOnBoard(row, column, shipSelected, horizontal);
                drawShip(shipSelected, row, column);
                pendingShipsListView.getItems().remove(shipSelected);

                if (playerHuman.isFleetFullyPlaced()){
                    startMatchButton.setDisable(false);
                }
            } catch (InvalidPositionException e){
                showError(e.getMessage());
            }
        }
        event.setDropCompleted(true);
        event.consume();
    }
    /**
     * Displays an error message to the user. (Not yet implemented.)
     *
     * @param message the error message to display
     */
    private void showError(String message){
        // Alert simple o Label
    }

    /**
     * Draws the given ship as a single figure spanning all the cells it
     * occupies, and adds it to the board grid on top of the empty cells.
     *
     * @param ship   the ship that was just placed
     * @param row    the ship's starting row index
     * @param column the ship's starting column index
     */
    private void drawShip(Ship ship, int row, int column){
        Node ShipFigure = createShipShape(ship, horizontal);
        GridPane.setColumnIndex(ShipFigure, column);
        GridPane.setRowIndex(ShipFigure, row);
        GridPane.setColumnSpan(ShipFigure, horizontal ? ship.getSize() : 1);
        GridPane.setRowSpan(ShipFigure, horizontal ? 1 : ship.getSize());

        boardGrid.getChildren().add(ShipFigure);
    }

    /**
     * Builds the 2D figure used to represent a ship on the board: a
     * rounded rectangle for the hull, plus a smaller centered rectangle
     * ("tower") for ships of size 2 or more, so that they can be visually
     * distinguished from a single-cell frigata.
     *
     * @param ship       the ship to build a figure for
     * @param horizontal {@code true} to build a horizontal figure, {@code false} for vertical
     * @return a node representing the ship, ready to be added to the board grid
     */
    private Node createShipShape(Ship ship, boolean horizontal){
        Image image = SHIP_IMAGES.get(ship.getType());

        double mayorLong = ship.getSize() * CELL_SIZE + (ship.getSize() - 1) * GAP;
        double minorLong = CELL_SIZE - 6;

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(mayorLong);
        imageView.setFitHeight(minorLong);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        Node shipNode = imageView;
        if (!horizontal){
            imageView.setRotate(90);
            shipNode = new Group(imageView); // Group recalcula el bounding box ya rotado
        }

        StackPane container = new StackPane(shipNode);
        container.setPickOnBounds(false);
        return container;
    }


    /**
     * Switches the current scene to the play view, passing the shared
     * game model (with both fleets already placed) to the new
     * {@link PlayController}.
     *
     * @param event the action event used to obtain the current stage
     */
    private void changePlayGameView(ActionEvent event){
        try {
            gameModel.startMatch();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/examplez/demo/PlayView.fxml"));
            Parent root = loader.load();

            PlayController controller = loader.getController();
            gameModel.startMatch();
            controller.setGameModel(gameModel);
            controller.loadBoard();



            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e){
            showError("Dont possible to load the view: " + e.getMessage());
        }
    }
}
