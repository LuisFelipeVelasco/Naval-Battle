package Controller;

import Model.*;
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

import java.io.IOException;
import java.util.List;
public class PlacementController {
    private static final double CELL_SIZE = 35;
    private static final double GAP = 2;
    @FXML private GridPane boardGrid;
    @FXML private ListView<Ship> pendingShipsListView;
    @FXML private Button rotateButton;

    @FXML private Button startMatchButton;
    Game gameModel;
    private PlayerHuman playerHuman;
    private boolean horizontal = true;
    private static final int SIZE = 10;

    @FXML
    private void initialize(){
        createCells();
        startMatchButton.setDisable(true);

    }
    public void initGame(Game game){
        this.gameModel = game;
        this.playerHuman = game.getPlayerHuman();
        playerHuman.createBoard(); // tablero vacío + flota creada, sin colocar

        pendingShipsListView.setItems(FXCollections.observableArrayList(playerHuman.getShips()));
        setDragFromListView();
    }
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
    private void setDragFromListView(){
        pendingShipsListView.setOnDragDetected(event -> {
            Ship selected = pendingShipsListView.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            Dragboard dragboard = pendingShipsListView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent contenido = new ClipboardContent();
            contenido.putString(selected.getType());
            dragboard.setContent(contenido);
            event.consume();
        });
    }
    @FXML
    private void onRotateButton(){
        horizontal = !horizontal;
    }
    @FXML
    private void onStartMatchButton(ActionEvent event){

        gameModel.getPlayerMachine().createBoard(); // coloca flota de la máquina en silencio
        // cambiar a PlayView.fxml pasando gameModel al PlayController
        changePlayGameView(event);
    }
    private void onDragOver(DragEvent event){
        if (event.getGestureSource() != boardGrid){
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }
    private void onDragDropped(DragEvent event, int row, int column){
        Ship selected = pendingShipsListView.getSelectionModel().getSelectedItem();

        if (selected != null){
            try {
                playerHuman.getBoard().placeShip(row, column, selected, horizontal);
                drawShip(selected, row, column);
                pendingShipsListView.getItems().remove(selected);

                if (playerHuman.isFleetFullyPlaced()){
                    startMatchButton.setDisable(false);
                }
            } catch (UnvalidPositionException e){
                showError(e.getMessage());
            }
        }
        event.setDropCompleted(true);
        event.consume();
    }
    private void showError(String message){
        // Alert simple o Label
    }
    private void drawShip(Ship ship, int row, int column){
        Node ShipFigure = createShipShape(ship, horizontal);

        GridPane.setColumnIndex(ShipFigure, column);
        GridPane.setRowIndex(ShipFigure, row);
        GridPane.setColumnSpan(ShipFigure, horizontal ? ship.getSize() : 1);
        GridPane.setRowSpan(ShipFigure, horizontal ? 1 : ship.getSize());

        boardGrid.getChildren().add(ShipFigure);
    }
    private Node createShipShape(Ship ship, boolean horizontal){
        int longShip = ship.getSize();
        double mayorLong = longShip * CELL_SIZE + (longShip - 1) * GAP;
        double minorLong = CELL_SIZE - 6;

        double width  = horizontal ? mayorLong : minorLong;
        double height   = horizontal ? minorLong : mayorLong;

        Rectangle casco = new Rectangle(width, height);
        casco.setArcWidth(16);
        casco.setArcHeight(16);
        casco.setFill(Color.web("#5c6b73"));
        casco.setStroke(Color.web("#2c3539"));
        casco.setStrokeWidth(1.5);

        Group grupo = new Group(casco);

        if (longShip >= 2){
            double widthTower = horizontal ? CELL_SIZE * 0.5 : minorLong * 0.6;
            double heightTowe  = horizontal ? minorLong * 0.6 : CELL_SIZE * 0.5;

            Rectangle tower = new Rectangle(widthTower, heightTowe );
            tower.setArcWidth(8);
            tower.setArcHeight(8);
            tower.setFill(Color.web("#8a9ba5"));
            tower.setLayoutX((width- widthTower) / 2);
            tower.setLayoutY((height - heightTowe ) / 2);
            grupo.getChildren().add(tower);
        }

        StackPane container = new StackPane(grupo);
        container.setPickOnBounds(false);
        return container;
    }


    private void createThreadOfShips(){

    }
    private void selectShip(){

    }


    private void updateBoard(){

    }
    private void changePlayGameView(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PlayView.fxml"));
            Parent root = loader.load();

            PlayController controller = loader.getController();
            controller.setGameModel(gameModel);
            controller.loadBoard();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e){
            showError("Dont possible to load the view: " + e.getMessage());
        }
    }
    private void showBoard(){

    }
}
