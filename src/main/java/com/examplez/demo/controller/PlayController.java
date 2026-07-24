package com.examplez.demo.controller;
import com.examplez.demo.storage.GameFileManager;
import com.examplez.demo.storage.GameState;

import com.examplez.demo.model.*;
import com.examplez.demo.model.exceptions.InvalidPositionException;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

/**
 * Controller for the play view (play-match-view.fxml), where the shooting
 * phase of the match takes place.
 * <p>
 * Manages two boards: the main board (the machine's territory, where
 * the human player shoots) and the position board (the human player's
 * own territory, shown in observation mode only).
 */
public class PlayController {

    @FXML
    Label turnLabel;

    /**
     * Map that stores ship type images for visual representation on the board.
     */
    private static final Map<String, Image> SHIP_IMAGES = new HashMap<>();

    static {
        SHIP_IMAGES.put("carrier",
                new Image(PlayController.class.getResourceAsStream("/Images/carrier.png")));

        SHIP_IMAGES.put("submarine",
                new Image(PlayController.class.getResourceAsStream("/Images/submarine.png")));

        SHIP_IMAGES.put("destructor",
                new Image(PlayController.class.getResourceAsStream("/Images/destructor.png")));

        SHIP_IMAGES.put("frigate",
                new Image(PlayController.class.getResourceAsStream("/Images/frigate.png")));
    }
    /**color of one cell.*/
    private static final Color CELL_A =
            Color.web("#315B79", 0.72);
    /**color of one cell.*/
    private static final Color CELL_B =
            Color.web("#3A6782", 0.68);
    /**border of one cell.*/
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
     * Flag indicating whether it is the human player's turn.
     */
    private boolean playerTurn = true;

    /**
     * Grid pane that displays the machine's board (the main board where the player shoots).
     */
    @FXML
    private GridPane mainBoardGrid;

    /**
     * Grid pane that displays the human player's own board (for observation).
     */
    @FXML
    private GridPane positionBoardGrid;

    /**
     * Matrix of StackPane cells for the main board.
     */
    private StackPane[][] mainCells = new StackPane[10][10];

    /**
     * Matrix of StackPane cells for the position board.
     */
    private StackPane[][] positionCells = new StackPane[10][10];

    /**
     * The game model instance.
     */
    private Game gameModel;

    /**Type of user selected by the user*/
    private String typeOfUser;


    /**
     * JavaFX lifecycle method, called automatically right after the FXML
     * is loaded. Builds the empty visual cells for both boards.
     */
    @FXML
    public void initialize() {
        createCells(mainBoardGrid, mainCells, true);
        createCells(positionBoardGrid, positionCells, false);
    }

    /**
     * Injects the shared game model into this controller. Must be called
     * before {@link #loadBoard()} so that both boards can be drawn from
     * the actual placement made during the previous phase.
     *
     * @param gameModel the game model, with both fleets already placed
     */
    public void setGameModel(Game gameModel) {
        this.gameModel = gameModel;
    }

    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    /**
     * Creates the 100 visual cells of a board as {@link StackPane} nodes
     * and adds them to the given grid.
     *
     * @param grid       the grid pane to fill with cells
     * @param cells      the matrix where the created cells are stored for later access
     * @param allowClick {@code true} to enable click handling on this board; {@code false} otherwise
     */
    private void createCells(GridPane grid, StackPane[][] cells, boolean allowClick) {
        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                StackPane VisualCell = new StackPane();
                Rectangle background = new Rectangle(50, 50);
                styleNormalCell(background,row,column);
                VisualCell.getChildren().add(background);

                if (allowClick) {
                    final int f = row, c = column;
                    VisualCell.setOnMouseClicked(event -> onCellClicked(f, c));
                }

                cells[row][column] = VisualCell;
                grid.add(VisualCell, column, row);
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
     * Builds the 2D figure used to represent a ship on the board.
     *
     * @param ship       the ship to build a figure for
     * @param horizontal {@code true} to build a horizontal figure, {@code false} for vertical
     * @return a node representing the ship, ready to be added to the board grid
     */
    private Node createShipShape(Ship ship, boolean horizontal) {
        Image image = SHIP_IMAGES.get(ship.getType());

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        imageView.setMouseTransparent(true);

        Node shipNode = imageView;

        if (!horizontal) {
            imageView.setRotate(90);
            shipNode = new Group(imageView);
        }

        StackPane container = new StackPane(shipNode);

        container.setMouseTransparent(true);
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
    private void onCellClicked(int row, int column) {
        if (!playerTurn) return;

        Board machineBoard = gameModel.getPlayerMachine().getBoard();
        if (machineBoard.isCellAlreadyAttacked(row, column)) return;
        machineBoard.attackCell(row, column);

        String result = machineBoard.getStateOfCell(row, column);
        if (result.equals(Board.HIT) && machineBoard.isShipSunken(row, column)) {
            machineBoard.sinkShip(row, column);
            result = Board.SUNKEN;
        }
        drawMainBoard();

        if (!machineBoard.isBoardWithShips()) {
            changeFinalView("YOU WIN!","COMMAND CHANNEL SECURE");
            return;
        }

        // Auto save progress after human move
        autoSaveGame();

        if (result.equals(Board.WATER)) {
            playerTurn = false;
            turnLabel.setText("TURN: MACHINE");
            machineTurnLoop();
        }
        // if HIT or SUNKEN, the player keeps shooting (nothing else to do)
    }


    /**
     * Draws the initial state of both boards from the game model. Must
     * be called once, right after {@link #setGameModel(Game)}, so the UI
     * reflects whatever state already exists in the model (e.g. a
     * freshly started match with both fleets placed).
     */
    public void loadBoard() {
        drawMainBoard();
        drawPositionBoard();
        drawPlayerShips();
        if(typeOfUser.equals("Verificator")) drawMachineShips();
    }

    /**
     * Paints the main board (the machine's territory) based on the
     * current state of the machine player's board: only the results of
     * shots already taken (water, hit, sunk) are reflected; ships that
     * have not been hit remain hidden.
     */
    private void drawMainBoard() {
        List<List<Cell>> board = gameModel.getPlayerMachine().getBoard().getCells();
        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                String state = board.get(row).get(column).getState();
                Rectangle background = (Rectangle) mainCells[row][column].getChildren().get(0);

                switch (state) {
                    case Board.WATER -> background.setFill(Color.web("#181e30"));      // watermark (X) would be added separately
                    case Board.HIT -> drawHitMarker(row,column,mainBoardGrid);
                    case Board.SUNKEN -> drawSunkenMarker(row,column,mainBoardGrid);
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
    private void drawPositionBoard() {
        List<List<Cell>> board = gameModel.getPlayerHuman().getBoard().getCells();

        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                String state = board.get(row).get(column).getState();
                Rectangle background = (Rectangle) positionCells[row][column].getChildren().get(0);

                switch (state) {
                    case Board.WATER -> background.setFill(Color.web("#181E30"));
                    case Board.HIT -> drawHitMarker(row, column,positionBoardGrid);
                    case Board.SUNKEN -> drawSunkenMarker(row, column,positionBoardGrid);
                }
            }
        }
    }

    /**
     * Draws the human player's own ships on the position board.
     * Only the starting cell of each ship is used to place the entire figure.
     */
    private void drawPlayerShips() {
        List<List<Cell>> board = gameModel.getPlayerHuman().getBoard().getCells();

        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                Cell cell = board.get(row).get(column);
                if (cell.getShip() == null) {
                    continue;
                }

                Ship ship = cell.getShip();

                // Is this the start of the ship?
                boolean sameUp = row > 0 && board.get(row - 1).get(column).getShip() == ship;
                boolean sameLeft = column > 0 && board.get(row).get(column - 1).getShip() == ship;

                if (sameUp || sameLeft) {
                    continue;
                }

                // Detect orientation
                boolean horizontal = column + 1 < 10 && board.get(row).get(column + 1).getShip() == ship;

                Node figure = createShipShape(ship, horizontal);

                GridPane.setRowIndex(figure, row);
                GridPane.setColumnIndex(figure, column);

                GridPane.setColumnSpan(figure, horizontal ? ship.getSize() : 1);
                GridPane.setRowSpan(figure, horizontal ? 1 : ship.getSize());

                positionBoardGrid.getChildren().add(figure);
            }
        }
    }

    /**
     * Draws the  machine player's own ships on the position board.
     * Only the starting cell of each ship is used to place the entire figure.
     */
    private void drawMachineShips() {
        List<List<Cell>> board = gameModel.getPlayerMachine().getBoard().getCells();

        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                Cell cell = board.get(row).get(column);
                if (cell.getShip() == null) {
                    continue;
                }

                Ship ship = cell.getShip();

                // Is this the start of the ship?
                boolean sameUp = row > 0 && board.get(row - 1).get(column).getShip() == ship;
                boolean sameLeft = column > 0 && board.get(row).get(column - 1).getShip() == ship;

                if (sameUp || sameLeft) {
                    continue;
                }

                // Detect orientation
                boolean horizontal = column + 1 < 10 && board.get(row).get(column + 1).getShip() == ship;

                Node figure = createShipShape(ship, horizontal);

                GridPane.setRowIndex(figure, row);
                GridPane.setColumnIndex(figure, column);

                GridPane.setColumnSpan(figure, horizontal ? ship.getSize() : 1);
                GridPane.setRowSpan(figure, horizontal ? 1 : ship.getSize());

                mainBoardGrid.getChildren().add(figure);
            }
        }
    }


    /**
     * Draws a hit marker (✕) on the given cell of the position board.
     *
     * @param row    the row index
     * @param column the column index
     */
    private void drawHitMarker(int row, int column, GridPane board) {
        removeMarker(board, row, column);

        Label mark = new Label("✕");
        mark.setId("boardMark");
        mark.setMouseTransparent(true);

        mark.setStyle("""
        -fx-font-size: 30px;
        -fx-font-weight: bold;
        -fx-text-fill: orange;
        """);

        board.add(mark, column, row);
        mark.toFront();
    }

    /**
     * Draws a sunk marker (☠) on the given cell of the position board.
     *
     * @param row    the row index
     * @param column the column index
     */
    private void drawSunkenMarker(int row, int column, GridPane board) {
        removeMarker(board, row, column);

        Label mark = new Label("☠");
        mark.setId("boardMark");
        mark.setMouseTransparent(true);

        mark.setStyle("""
        -fx-font-size: 30px;
        -fx-text-fill: #ff657b;
        -fx-font-weight: bold;
        """);

        board.add(mark, column, row);
        mark.toFront();
    }

    /**
     * Executes the machine's turn loop. The machine attacks, the board is updated,
     * and if the attack is a hit or a sunk, the machine continues; otherwise the
     * turn passes back to the player.
     */
    private void machineTurnLoop() {
        PauseTransition pause = new PauseTransition(Duration.millis(1000));
        pause.setOnFinished(event -> {
            turnLabel.setText("TURN: MACHINE");
            gameModel.processMachineAttack(); // still void, unchanged
            drawPositionBoard();

            Board humanBoard = gameModel.getPlayerHuman().getBoard();

            if (!humanBoard.isBoardWithShips()) {
                changeFinalView("MACHINE WINS!" , "COMMAND CHANNEL LOST");
                return;
            }

            // Auto save progress after machine move
            autoSaveGame();

            String lastResult = humanBoard.getStateLastCellAttacked();
            if (lastResult.equals(Board.WATER)) {
                playerTurn = true; // turn returns to the player
                turnLabel.setText("TURN: PLAYER");
            } else {
                machineTurnLoop(); // machine keeps shooting (HIT or SUNKEN)
            }
        });
        pause.play();
    }

    /**
     * Switches the scene to the final view, passing the winner message.
     *
     * @param winner the winner message to display
     */
    private void changeFinalView(String winner , String status) {
        // Delete persistent save files when the game ends
        GameFileManager.deleteGame();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/examplez/demo/FinalView.fxml"));
            Parent root = loader.load();

            FinalController controller = loader.getController();
            controller.setWinner(winner);
            controller.setStatus(status);

            Stage stage = (Stage) mainBoardGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeMarker(GridPane board, int row, int column) {
        board.getChildren().removeIf(node -> {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeColumn = GridPane.getColumnIndex(node);

            int currentRow = nodeRow == null ? 0 : nodeRow;
            int currentColumn = nodeColumn == null ? 0 : nodeColumn;

            return node instanceof Label label
                    && "boardMark".equals(label.getId())
                    && currentRow == row
                    && currentColumn == column;
        });
    }

    /**
     * Helper method that captures current match data and auto-saves it to disk.
     */
    private void autoSaveGame() {
        if (gameModel == null) return;

        try {
            Board humanBoard = gameModel.getPlayerHuman().getBoard();
            Board machineBoard = gameModel.getPlayerMachine().getBoard();

            // Current state of the Boards
            GameState currentState = new GameState(humanBoard, machineBoard);

            int currentTurn = playerTurn ? 1 : 0; // 1 = Human turn, 0 = Machine turn
            int shipsSunkByHuman = machineBoard.getNumberShipsSunk(); // Ships sunk on machine's board

            //TASK: Once the getNickname() method is implemented in the PlayerHuman class, uncomment these line of code.

            //String nickname = gameModel.getPlayerHuman().getNickname();
            //GameFileManager.saveGame(currentState, currentTurn, shipsSunkByHuman, nickname);
        } catch (Exception e) {
            System.err.println("Error automatic saving game: " + e.getMessage());
        }
    }

    /**
     * Load the turn where a game was saved.
     * */
    public void restoreLoadedTurn(int turnLoad){
        playerTurn = turnLoad == 1;
    }
}