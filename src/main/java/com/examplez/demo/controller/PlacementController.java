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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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

    /**
     * Map that stores ship type images for visual representation on the board.
     */
    private static final Map<String, Image> SHIP_IMAGES = new HashMap<>();
    /**
     * Map that stores ship card images for visual representation on the board.
     */
    private static final Map<String, Image> CARD_SHIP_IMAGES = new HashMap<>();

    static {
        SHIP_IMAGES.put("carrier", new Image(PlacementController.class.getResourceAsStream("/Images/carrier.png")));
        SHIP_IMAGES.put("submarine", new Image(PlacementController.class.getResourceAsStream("/Images/submarine.png")));
        SHIP_IMAGES.put("destructor", new Image(PlacementController.class.getResourceAsStream("/Images/destructor.png")));
        SHIP_IMAGES.put("frigate", new Image(PlacementController.class.getResourceAsStream("/Images/frigate.png")));
    }

    static {
        CARD_SHIP_IMAGES.put("carrier", new Image(PlacementController.class.getResourceAsStream("/cards/carrier.png")));
        CARD_SHIP_IMAGES.put("submarine", new Image(PlacementController.class.getResourceAsStream("/cards/submarine.png")));
        CARD_SHIP_IMAGES.put("destructor", new Image(PlacementController.class.getResourceAsStream("/cards/destructor.png")));
        CARD_SHIP_IMAGES.put("frigate", new Image(PlacementController.class.getResourceAsStream("/cards/frigate.png")));
    }
    /** Alternating fill colour for even board cells. */
    private static final Color CELL_A =
            Color.web("#315B79", 0.72);
    /** Alternating fill colour for odd board cells. */
    private static final Color CELL_B =
            Color.web("#3A6782", 0.68);
    /** Border colour applied to every board cell. */
    private static final Color CELL_STROKE =
            Color.web("#78C8DE", 0.60);

    /**
     * The size of each cell in pixels.
     */
    private static final double CELL_SIZE = 35;

    /**
     * The gap between cells in pixels.
     */
    private static final double GAP = 2;

    /**
     * Grid pane that represents the game board.
     */
    @FXML
    private GridPane boardGrid;

    /**
     * List view that displays ships pending placement.
     */
    @FXML
    private ListView<Ship> pendingShipsListView;

    /**
     * Button to rotate the ship orientation.
     */
    @FXML
    private Button rotateButton;

    /**
     * Button to start the match after all ships are placed.
     */
    @FXML
    private Button startMatchButton;

    @FXML
    private Label orientationLabel;

    /** Label that identifies the commander during fleet deployment. */
    @FXML
    private Label commanderLabel;

    /**
     * The game model instance.
     */
    private Game gameModel;

    /**
     * Flag indicating whether the current orientation is horizontal.
     */
    private boolean horizontal = true;

    /** Visibility mode selected for the match. */
    private String typeOfUser;

    /**
     * The size of the game board (10x10).
     */
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
     * @param playerName the name of the human player
     */
    public void initGame(Game game, String playerName ,String typeOfUser){
        this.gameModel = game;
        this.typeOfUser=typeOfUser;
        gameModel.startPlacement(playerName);
        commanderLabel.setText("COMMANDER: " + playerName.toUpperCase() + " // DEPLOY YOUR FLEET");
        pendingShipsListView.setItems(FXCollections.observableArrayList(gameModel.getPlayerHuman().getShips()));
        configureShipListView();
        setDragFromListView();
    }

    private void configureShipListView() {

        pendingShipsListView.setCellFactory(list -> new ListCell<Ship>() {

            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Ship ship, boolean empty) {
                super.updateItem(ship, empty);

                if (empty || ship == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Image image = CARD_SHIP_IMAGES.get(ship.getType());

                imageView.setImage(image);
                imageView.setPreserveRatio(true);

                setGraphic(imageView);
                setText(null);
            }
        });
    }

    /**
     * Creates the 100 visual cells of the board as {@link StackPane}
     * nodes, each one listening for drag-over and drag-dropped events so
     * that ships can be dropped on them.
     */
    private void createCells(){
        for (int row = 0; row < SIZE; row++){
            for (int column = 0; column < SIZE; column++){
                StackPane visualCell = new StackPane();
                Rectangle background = new Rectangle(50, 50);
                styleNormalCell(background,row,column);
                visualCell.getChildren().add(background);

                final int f = row;
                final int c = column;
                visualCell.setOnDragOver(this::onDragOver);
                visualCell.setOnDragDropped(event -> onDragDropped(event, f, c));

                boardGrid.add(visualCell, column, row);
            }
        }
    }

    /**
     * Set a style to a rectangle that represent a cell
     * @param cell rectangle passed
     * @param row row of the rectangle
     * @param column column of the rectangle
     */

    private void styleNormalCell(Rectangle cell, int row, int column) {
        cell.setFill(((row + column) & 1) == 0 ? CELL_A : CELL_B);
        cell.setStroke(CELL_STROKE);
        cell.setStrokeWidth(0.9);
        cell.setArcWidth(6);
        cell.setArcHeight(6);
        cell.setEffect(null);
        cell.setScaleX(1.0);
        cell.setScaleY(1.0);
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
        if(horizontal) orientationLabel.setText("↔");
        else orientationLabel.setText("↕");
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
        gameModel.startMatch();
        changePlayGameView(event);
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
                    rotateButton.setDisable(true);
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
     * Displays an invalid-placement message to the commander.
     *
     * @param message the error message to display
     */
    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Fleet Position");
        alert.setHeaderText("The ship cannot be deployed there.");
        alert.setContentText(message);
        alert.showAndWait();
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
     * distinguished from a single-cell frigate.
     *
     * @param ship       the ship to build a figure for
     * @param horizontal {@code true} to build a horizontal figure, {@code false} for vertical
     * @return a node representing the ship, ready to be added to the board grid
     */
    private Node createShipShape(Ship ship, boolean horizontal){
        Image image = SHIP_IMAGES.get(ship.getType());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        Node shipNode = imageView;
        if (!horizontal){
            imageView.setRotate(90);
            shipNode = new Group(imageView);
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
        try{
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/com/examplez/demo/play-match-view.fxml"));

            Parent root = loader.load();

            PlayController controller = loader.getController();

            controller.startSession(gameModel, typeOfUser);

            Stage stage =
                    (Stage)((Node)event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));

            stage.show();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
