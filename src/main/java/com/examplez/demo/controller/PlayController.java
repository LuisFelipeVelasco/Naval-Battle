package com.examplez.demo.controller;
import javafx.animation.PauseTransition;
import javafx.scene.shape.Rectangle;
import com.examplez.demo.model.Board;
import com.examplez.demo.model.Cell;
import com.examplez.demo.model.Game;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

/**
 * Controller for the play view (PlayView.fxml), where the shooting
 * phase of the match takes place.
 * <p>
 * Manages two boards: the main board (the machine's territory, where
 * the human player shoots) and the position board (the human player's
 * own territory, shown in observation mode only).
 */
public class PlayController {
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
            showWinner("Player");
            return;
        }

        if (result.equals(Board.WATER)){
            playerTurn = false;
           machineTurnLoop();
        }
        // if HIT or SUNKED, the player keeps shooting (nothing else to do)
    }

    /**
     * Announces the winner of the match. (Placeholder — will later
     * transition to a FinalController view.)
     */
    private void showWinner(String winner){
        System.out.println("Winner: " + winner);
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
                    case Board.WATER -> background.setFill(Color.LIGHTBLUE);      // marca de agua (X) se añadiría aparte
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
                    case Board.SHIP -> background.setFill(Color.DARKSLATEGRAY);
                    case Board.WATER -> background.setFill(Color.LIGHTBLUE);
                    case Board.HIT -> background.setFill(Color.ORANGE);
                    case Board.SUNKEN -> background.setFill(Color.DARKRED);
                    default -> background.setFill(Color.LIGHTBLUE); // VACIO
                }
            }
        }
    }


    private void machineTurnLoop(){
    PauseTransition pause = new PauseTransition(Duration.millis(600));
    pause.setOnFinished(event -> {
        gameModel.processMachineAttack(); // sigue siendo void, sin tocarla
        drawPositionBoard();

        Board humanBoard = gameModel.getPlayerHuman().getBoard();

        if (!humanBoard.isBoardWithShips()){
            showWinner("Machine");
            return;
        }

        String lastResult = humanBoard.getStateLastCellAttacked();
        if (lastResult.equals(Board.WATER)){
            playerTurn = true; // se devuelve el turno al jugador
        } else {
            machineTurnLoop(); // la máquina sigue disparando (HIT o SUNKEN)
        }
    });
    pause.play();
}



}
