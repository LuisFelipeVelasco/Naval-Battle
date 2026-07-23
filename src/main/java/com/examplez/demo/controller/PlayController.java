package com.examplez.demo.controller;

import com.examplez.demo.model.*;
import com.examplez.demo.model.exceptions.InvalidPositionException;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.IOException;
/*
package com.examplez.demo.controller;
import com.examplez.demo.model.Ship;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import com.examplez.demo.model.Board;
import com.examplez.demo.model.Cell;
import com.examplez.demo.model.Game;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.List;
 */

/**
 * Controller for the play view (PlayView.fxml), where the shooting
 * phase of the match takes place.
 * <p>
 * Manages two boards: the main board (the machine's territory, where
 * the human player shoots) and the position board (the human player's
 * own territory, shown in observation mode only).
 */
public class PlayController {
    @FXML
    Label turnLabel;
    private static final Map<String, Image> SHIP_IMAGES = new HashMap<>();

    static{
        SHIP_IMAGES.put("carrier",
                new Image(PlayController.class.getResourceAsStream("/cards/carrier.png")));

        SHIP_IMAGES.put("submarine",
                new Image(PlayController.class.getResourceAsStream("/cards/submarine.png")));

        SHIP_IMAGES.put("destructor",
                new Image(PlayController.class.getResourceAsStream("/cards/destructor.png")));

        SHIP_IMAGES.put("frigate",
                new Image(PlayController.class.getResourceAsStream("/cards/frigate.png")));
    }
    private static final double CELL_SIZE = 35;
    private static final double GAP = 2;
    private boolean playerTurn= true;
    @FXML private GridPane mainBoardGrid;
    @FXML private GridPane positionBoardGrid;

    private StackPane[][] mainCells = new StackPane[10][10];
    private StackPane[][] positionCells = new StackPane[10][10];

    /**
     * JavaFX lifecycle method, called automatically right after the FXML
     * is loaded. Builds the empty visual cells for both boards.
     */
    @FXML public void initialize(){

        createCells(mainBoardGrid, mainCells, true);
        createCells(positionBoardGrid, positionCells, false);
    }
    private Game gameModel;

    /**
     * Injects the shared game model into this controller. Must be called
     * before {@link #loadBoard()} so that both boards can be drawn from
     * the actual placement made during the previous phase.
     *
     * @param gameModel the game model, with both fleets already placed
     */
    public void setGameModel(Game gameModel){
        this.gameModel = gameModel;
    }


    /**
     * Creates the 100 visual cells of a board as {@link StackPane} nodes
     * and adds them to the given grid.
     *
     * @param grid       the grid pane to fill with cells
     * @param cells      the matrix where the created cells are stored for later access
     * @param allowClick reserved for enabling click handling on this board (not yet wired up)
     */
    private void createCells(GridPane grid, StackPane[][] cells, boolean allowClick){
        for (int row = 0; row < 10; row++){
            for (int column = 0; column < 10; column++){
                StackPane VisualCell = new StackPane();
                Rectangle background = new Rectangle(35, 35);
                background.setFill(Color.LIGHTBLUE);
                background.setStroke(Color.GRAY);
                VisualCell.getChildren().add(background);

                if (allowClick){
                    final int f = row, c = column;
                    VisualCell.setOnMouseClicked(event -> onCellClicked(f, c));
                }

                cells[row][column] = VisualCell;
                grid.add(VisualCell, column, row);
            }
        }
    }
    private Node createShipShape(Ship ship, boolean horizontal){
        javafx.scene.image.Image image = SHIP_IMAGES.get(ship.getType());

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
            shipNode = new Group(imageView);
        }

        StackPane container = new StackPane(shipNode);
        container.setPickOnBounds(false);
        return container;
    }
    /**
     * Handles a click on the main board (machine's territory). Processes
     * the attack, redraws the board, checks for victory, and either lets
     * the player shoot again (on hit/sunk) or passes the turn to the machine.
     *
     * @param row    the row index clicked
     * @param column the column index clicked
     */
    private void onCellClicked(int row, int column){

        if (!playerTurn) return;


        Board machineBoard = gameModel.getPlayerMachine().getBoard();
        if (machineBoard.isCellAlreadyAttacked(row, column)) return;
        machineBoard.attackCell(row, column);

        String result=machineBoard.getStateOfCell(row,column);
        if (result.equals(Board.HIT)&&  machineBoard.isShipSunken(row,column)){
            machineBoard.sinkShip(row,column);
            result=Board.SUNKEN;

        }
        drawMainBoard();

        if (!machineBoard.isBoardWithShips()){

            changeFinalView("¡You Win!");
            return;
        }

        if (result.equals(Board.WATER)){
            playerTurn = false;
            turnLabel.setText("Turno: Máquina");
           machineTurnLoop();
        }
        // if HIT or SUNKED, the player keeps shooting (nothing else to do)
    }

    /**
     * Announces the winner of the match. (Placeholder — will later
     * transition to a FinalController view.)
     */
    private void showWinner(String winner){

    }
    /**
     * Draws the initial state of both boards from the game model. Must
     * be called once, right after {@link #setGameModel(Game)}, so the UI
     * reflects whatever state already exists in the model (e.g. a
     * freshly started match with both fleets placed).
     */
    public void loadBoard(){
        drawMainBoard();
        drawPositionBoard();
        drawPlayerShips();
        // it runs gameModel.getPlayerHuman().getBoard() y gameModel.getPlayerMachine().getBoard()
        // and draw the cells already initialized on the gridpane
    }

    /**
     * Paints the main board (the machine's territory) based on the
     * current state of the machine player's board: only the results of
     * shots already taken (water, hit, sunked) are reflected; ships that
     * have not been hit remain hidden.
     */
    private void drawMainBoard(){
        List<List<Cell>> board = gameModel.getPlayerMachine().getBoard().getCells();
        for (int row = 0; row < 10; row++){
            for (int column = 0; column < 10; column++){
                String state = board.get(row).get(column).getState();
                Rectangle background = (Rectangle) mainCells[row][column].getChildren().get(0);

                switch (state){
                    case Board.WATER -> background.setFill(Color.BLUE);      // marca de agua (X) se añadiría aparte
                    case Board.HIT -> background.setFill(Color.ORANGE);
                    case Board.SUNKEN -> background.setFill(Color.DARKRED);
                    default -> background.setFill(Color.LIGHTBLUE); // VACIO o BARCO oculto: se ve igual que el agua
                }
            }
        }
    }
    /**
     * Paints the position board (the human player's own territory)
     * based on the current state of the human player's board: own ships
     * are shown, along with any shots the machine has already made
     * against them.
     */
    private void drawPositionBoard(){
        List<List<Cell>> board = gameModel.getPlayerHuman().getBoard().getCells();

        for (int row = 0; row < 10; row++){
            for (int column = 0; column < 10; column++){
                String state = board.get(row).get(column).getState();
                Rectangle background = (Rectangle) positionCells[row][column].getChildren().get(0);

                switch (state){

                    case Board.WATER -> background.setFill(Color.BLUE);

                    case Board.HIT -> drawHitMarker(row,column);

                    case Board.SUNKEN -> drawSunkenMarker(row,column);

                    default -> background.setFill(Color.LIGHTBLUE);
                }
            }
        }
    }
    private void drawPlayerShips() {

        List<List<Cell>> board = gameModel.getPlayerHuman().getBoard().getCells();

        for (int row = 0; row < 10; row++) {

            for (int column = 0; column < 10; column++) {

                Cell cell = board.get(row).get(column);

                if (cell.getShip() == null) {
                    continue;
                }

                Ship ship = cell.getShip();

                // ¿Es el inicio del barco?

                boolean sameUp =
                        row > 0 &&
                                board.get(row - 1).get(column).getShip() == ship;

                boolean sameLeft =
                        column > 0 &&
                                board.get(row).get(column - 1).getShip() == ship;

                if (sameUp || sameLeft) {
                    continue;
                }

                // Detectar orientación

                boolean horizontal =
                        column + 1 < 10 &&
                                board.get(row).get(column + 1).getShip() == ship;

                Node figure = createShipShape(ship, horizontal);

                GridPane.setRowIndex(figure, row);
                GridPane.setColumnIndex(figure, column);

                GridPane.setColumnSpan(
                        figure,
                        horizontal ? ship.getSize() : 1);

                GridPane.setRowSpan(
                        figure,
                        horizontal ? 1 : ship.getSize());

                positionBoardGrid.getChildren().add(figure);
            }
        }
    }
    private void drawHitMarker(int row,int column){

        StackPane cell = positionCells[row][column];

        Label mark = new Label("✕");

        mark.setStyle("""
        -fx-font-size:22;
        -fx-font-weight:bold;
        -fx-text-fill:red;
        """);

        if(cell.getChildren().size()==1){

            cell.getChildren().add(mark);
        }
    }
    private void drawSunkenMarker(int row,int column){

        StackPane cell = positionCells[row][column];

        Label mark = new Label("☠");

        mark.setStyle("""
        -fx-font-size:18;
        -fx-text-fill:black;
        -fx-font-weight:bold;
        """);

        if(cell.getChildren().size()==1){

            cell.getChildren().add(mark);
        }
    }


    private void machineTurnLoop(){
    PauseTransition pause = new PauseTransition(Duration.millis(600));
    pause.setOnFinished(event -> {
        turnLabel.setText("Turno: maquina");
        gameModel.processMachineAttack(); // sigue siendo void, sin tocarla
        drawPositionBoard();

        Board humanBoard = gameModel.getPlayerHuman().getBoard();

        if (!humanBoard.isBoardWithShips()){

            changeFinalView("¡machine Wins!");
            return;
        }

        String lastResult = humanBoard.getStateLastCellAttacked();
        if (lastResult.equals(Board.WATER)){

            playerTurn = true; // se devuelve el turno al jugador
            turnLabel.setText("Turno: Jugador");
        } else {
            machineTurnLoop(); // la máquina sigue disparando (HIT o SUNKEN)
        }
    });
    pause.play();
}private void changeFinalView(String winner){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/examplez/demo/FinalView.fxml"));
            Parent root = loader.load();

            FinalController controller = loader.getController();

            controller.setWinner(winner);

            Stage stage = (Stage) mainBoardGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





