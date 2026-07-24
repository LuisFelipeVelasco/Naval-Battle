package com.examplez.demo.controller;

import com.examplez.demo.model.Board;
import com.examplez.demo.model.Cell;
import com.examplez.demo.model.Game;
import com.examplez.demo.model.Ship;
import com.examplez.demo.storage.GameFileManager;
import com.examplez.demo.storage.GameState;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls the combat screen and preserves every active-match value after a save.
 */
public class PlayController {

    /** Number of rows and columns in each tactical board. */
    private static final int BOARD_SIZE = 10;
    /** Side length of a visual board cell. */
    private static final double CELL_SIZE = 50.0;
    /** Images used to display the four ship types. */
    private static final Map<String, Image> SHIP_IMAGES = new HashMap<>();

    static {
        SHIP_IMAGES.put("carrier", new Image(PlayController.class.getResourceAsStream("/Images/carrier.png")));
        SHIP_IMAGES.put("submarine", new Image(PlayController.class.getResourceAsStream("/Images/submarine.png")));
        SHIP_IMAGES.put("destructor", new Image(PlayController.class.getResourceAsStream("/Images/destructor.png")));
        SHIP_IMAGES.put("frigate", new Image(PlayController.class.getResourceAsStream("/Images/frigate.png")));
    }

    /** Label that describes whose turn is active. */
    @FXML private Label turnLabel;
    /** Label that identifies the active commander. */
    @FXML private Label commanderLabel;
    /** Grid that represents the enemy board. */
    @FXML private GridPane mainBoardGrid;
    /** Grid that represents the human player's board. */
    @FXML private GridPane positionBoardGrid;
    /** Visual cells for enemy attacks. */
    private final StackPane[][] mainCells = new StackPane[BOARD_SIZE][BOARD_SIZE];
    /** Visual cells for the player's fleet. */
    private final StackPane[][] positionCells = new StackPane[BOARD_SIZE][BOARD_SIZE];
    /** Complete model for the current active match. */
    private Game gameModel;
    /** Visibility mode selected for the match. */
    private String typeOfUser = "Player";
    /** Whether the human player owns the next turn. */
    private boolean playerTurn = true;
    /** Prevents a close event from saving a finished match. */
    private boolean matchFinished;

    /** Creates the two visual tactical boards after FXML injection. */
    @FXML
    public void initialize() {
        createCells(mainBoardGrid, mainCells, true);
        createCells(positionBoardGrid, positionCells, false);
    }

    /**
     * Starts a newly created match after fleet placement.
     *
     * @param game complete game model with both fleets placed
     * @param userType selected visibility mode
     */
    public void startSession(Game game, String userType) {
        this.gameModel = game;
        this.typeOfUser = normalizeUserType(userType);
        this.playerTurn = true;
        this.matchFinished = false;
        refreshScreen();
        autoSaveGame();
        installCloseSaveHandler();
    }

    /**
     * Restores an exact, serialized active session without rebuilding boards or ships.
     *
     * @param state validated complete saved session
     */
    public void restoreSession(GameState state) {
        this.gameModel = state.getGame();
        this.typeOfUser = normalizeUserType(state.getUserType());
        this.playerTurn = state.isPlayerTurn();
        this.matchFinished = false;
        refreshScreen();
        installCloseSaveHandler();
        if (!playerTurn) {
            Platform.runLater(this::machineTurnLoop);
        }
    }

    /**
     * Creates all empty cell containers for one board.
     *
     * @param grid target grid pane
     * @param cells target visual-cell matrix
     * @param clickable whether clicking a cell should fire at the enemy
     */
    private void createCells(GridPane grid, StackPane[][] cells, boolean clickable) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                StackPane cell = new StackPane();
                Rectangle background = new Rectangle(CELL_SIZE, CELL_SIZE);
                styleNormalCell(background, row, column);
                cell.getChildren().add(background);
                if (clickable) {
                    final int targetRow = row;
                    final int targetColumn = column;
                    cell.setOnMouseClicked(event -> onCellClicked(targetRow, targetColumn));
                }
                cells[row][column] = cell;
                grid.add(cell, column, row);
            }
        }
    }

    /**
     * Restores the base colour of one board rectangle.
     *
     * @param rectangle target rectangle
     * @param row row index
     * @param column column index
     */
    private void styleNormalCell(Rectangle rectangle, int row, int column) {
        rectangle.setFill(Color.web(((row + column) % 2 == 0) ? "#356d8b" : "#2e617e", 0.88));
        rectangle.setStroke(Color.web("#8fd9eb", 0.70));
        rectangle.setStrokeWidth(0.9);
        rectangle.setArcWidth(6);
        rectangle.setArcHeight(6);
    }

    /**
     * Processes a valid player attack on the enemy board.
     *
     * @param row selected row
     * @param column selected column
     */
    private void onCellClicked(int row, int column) {
        if (matchFinished || !playerTurn || gameModel == null) {
            return;
        }
        Board enemyBoard = gameModel.getPlayerMachine().getBoard();
        if (enemyBoard.isCellAlreadyAttacked(row, column)) {
            return;
        }
        enemyBoard.attackCell(row, column);
        if (enemyBoard.getStateOfCell(row, column).equals(Board.HIT) && enemyBoard.isShipSunken(row, column)) {
            enemyBoard.sinkShip(row, column);
        }
        if (!enemyBoard.isBoardWithShips()) {
            finishMatch(gameModel.getPlayerHuman().getPlayerName().toUpperCase() + " WINS", "MISSION COMPLETE // ENEMY FLEET DESTROYED");
            return;
        }
        if (enemyBoard.getStateOfCell(row, column).equals(Board.WATER)) {
            playerTurn = false;
            updateTurnLabel();
        }
        refreshScreen();
        autoSaveGame();
        if (!playerTurn) {
            machineTurnLoop();
        }
    }

    /** Runs one delayed machine attack and continues while the machine hits a ship. */
    private void machineTurnLoop() {
        if (matchFinished || playerTurn || gameModel == null) {
            return;
        }
        PauseTransition delay = new PauseTransition(Duration.millis(700));
        delay.setOnFinished(event -> {
            if (matchFinished || playerTurn) {
                return;
            }
            gameModel.processMachineAttack();
            Board playerBoard = gameModel.getPlayerHuman().getBoard();
            if (!playerBoard.isBoardWithShips()) {
                finishMatch("MACHINE WINS", "MISSION FAILED // YOUR FLEET WAS DESTROYED");
                return;
            }
            if (playerBoard.getStateLastCellAttacked().equals(Board.WATER)) {
                playerTurn = true;
            }
            updateTurnLabel();
            refreshScreen();
            autoSaveGame();
            if (!playerTurn) {
                machineTurnLoop();
            }
        });
        delay.play();
    }

    /** Refreshes labels, ships and all recorded attack markers from the model. */
    private void refreshScreen() {
        if (gameModel == null) {
            return;
        }
        clearDynamicNodes(mainBoardGrid);
        clearDynamicNodes(positionBoardGrid);
        drawShips(gameModel.getPlayerHuman().getBoard(), positionBoardGrid);
        if ("Verifier".equalsIgnoreCase(typeOfUser)) {
            drawShips(gameModel.getPlayerMachine().getBoard(), mainBoardGrid);
        }
        drawBoardStates(gameModel.getPlayerMachine().getBoard(), mainBoardGrid, mainCells);
        drawBoardStates(gameModel.getPlayerHuman().getBoard(), positionBoardGrid, positionCells);
        commanderLabel.setText("COMMANDER: " + gameModel.getPlayerHuman().getPlayerName().toUpperCase());
        updateTurnLabel();
    }

    /**
     * Removes only ship and marker nodes while retaining the 100 cell containers.
     *
     * @param grid board that will be redrawn
     */
    private void clearDynamicNodes(GridPane grid) {
        grid.getChildren().removeIf(node -> "ship-overlay".equals(node.getUserData()) || "board-marker".equals(node.getUserData()));
    }

    /**
     * Draws every unique ship on its correct starting board cell.
     *
     * @param board model board containing ships
     * @param grid visual grid receiving the ship figures
     */
    private void drawShips(Board board, GridPane grid) {
        List<List<Cell>> cells = board.getCells();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                Ship ship = cells.get(row).get(column).getShip();
                if (ship == null || hasSameShipBefore(cells, row, column, ship)) {
                    continue;
                }
                boolean horizontal = column + 1 < BOARD_SIZE && cells.get(row).get(column + 1).getShip() == ship;
                Node figure = createShipShape(ship, horizontal);
                figure.setUserData("ship-overlay");
                GridPane.setRowIndex(figure, row);
                GridPane.setColumnIndex(figure, column);
                GridPane.setRowSpan(figure, horizontal ? 1 : ship.getSize());
                GridPane.setColumnSpan(figure, horizontal ? ship.getSize() : 1);
                grid.getChildren().add(figure);
            }
        }
    }

    /**
     * Determines whether another segment of the same ship precedes this cell.
     *
     * @param cells complete board cell matrix
     * @param row current row
     * @param column current column
     * @param ship candidate ship
     * @return true when this is not the first occupied cell of the ship
     */
    private boolean hasSameShipBefore(List<List<Cell>> cells, int row, int column, Ship ship) {
        return (row > 0 && cells.get(row - 1).get(column).getShip() == ship)
                || (column > 0 && cells.get(row).get(column - 1).getShip() == ship);
    }

    /**
     * Creates a transparent image overlay for a ship.
     *
     * @param ship ship to render
     * @param horizontal orientation of the ship
     * @return non-interactive ship overlay
     */
    private Node createShipShape(Ship ship, boolean horizontal) {
        ImageView imageView = new ImageView(SHIP_IMAGES.get(ship.getType()));
        imageView.setPreserveRatio(false);
        imageView.setMouseTransparent(true);
        if (horizontal) {
            imageView.setFitWidth(CELL_SIZE * ship.getSize());
            imageView.setFitHeight(CELL_SIZE);
            return new StackPane(imageView);
        }
        imageView.setFitWidth(CELL_SIZE * ship.getSize());
        imageView.setFitHeight(CELL_SIZE);
        imageView.setRotate(90);
        Group group = new Group(imageView);
        group.setMouseTransparent(true);
        return new StackPane(group);
    }

    /**
     * Draws non-secret state colours and attack markers for a board.
     *
     * @param board model board
     * @param grid visual grid
     * @param visualCells visual cell matrix
     */
    private void drawBoardStates(Board board, GridPane grid, StackPane[][] visualCells) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                Rectangle rectangle = (Rectangle) visualCells[row][column].getChildren().get(0);
                styleNormalCell(rectangle, row, column);
                String state = board.getStateOfCell(row, column);
                if (Board.WATER.equals(state)) {
                    rectangle.setFill(Color.web("#070b13"));
                    rectangle.setStroke(Color.web("#50616e"));
                    addMarker(grid, row, column, "·", "#8aa1ae");
                } else if (Board.HIT.equals(state)) {
                    rectangle.setFill(Color.web("#71394a"));
                    rectangle.setStroke(Color.web("#ff9ca9"));
                    addMarker(grid, row, column, "✕", "#ffd0d5");
                } else if (Board.SUNKEN.equals(state)) {
                    rectangle.setFill(Color.web("#340b18"));
                    rectangle.setStroke(Color.web("#ff657b"));
                    addMarker(grid, row, column, "☠", "#ffb0bc");
                }
            }
        }
    }

    /**
     * Adds an always-front, mouse-transparent attack marker.
     *
     * @param grid target grid
     * @param row marker row
     * @param column marker column
     * @param text marker symbol
     * @param colour marker colour
     */
    private void addMarker(GridPane grid, int row, int column, String text, String colour) {
        Label marker = new Label(text);
        marker.setMouseTransparent(true);
        marker.setUserData("board-marker");
        marker.setStyle("-fx-font-size: 27px; -fx-font-weight: bold; -fx-text-fill: " + colour + ";");
        grid.add(marker, column, row);
        marker.toFront();
    }

    /** Updates the visible turn instruction. */
    private void updateTurnLabel() {
        turnLabel.setText(playerTurn ? "YOUR TURN // SELECT A TARGET" : "MACHINE TURN // HOLD POSITION");
    }

    /** Installs the close handler after the controller has a scene and stage. */
    private void installCloseSaveHandler() {
        Platform.runLater(() -> {
            if (mainBoardGrid.getScene() != null && mainBoardGrid.getScene().getWindow() != null) {
                mainBoardGrid.getScene().getWindow().setOnCloseRequest(event -> autoSaveGame());
            }
        });
    }

    /** Saves the complete active session whenever its state changes or the window closes. */
    private void autoSaveGame() {
        if (!matchFinished && gameModel != null && gameModel.hasActiveMatch()) {
            GameFileManager.saveGame(new GameState(gameModel, playerTurn, typeOfUser));
        }
    }

    /**
     * Deletes the active save and opens the final screen after a completed match.
     *
     * @param winner visible winner message
     * @param status visible debrief status
     */
    private void finishMatch(String winner, String status) {
        matchFinished = true;
        GameFileManager.deleteGame();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/examplez/demo/FinalView.fxml"));
            Parent root = loader.load();
            FinalController controller = loader.getController();
            controller.setWinner(winner);
            controller.setStatus(status);
            controller.setNickname(gameModel.getPlayerHuman().getPlayerName());
            Stage stage = (Stage) mainBoardGrid.getScene().getWindow();
            stage.setOnCloseRequest(null);
            stage.setScene(new Scene(root, 1100, 700));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("The final screen could not be opened.", exception);
        }
    }

    /**
     * Normalizes an optional game mode.
     *
     * @param userType requested mode
     * @return nonblank mode value
     */
    private String normalizeUserType(String userType) {
        return userType == null || userType.isBlank() ? "Player" : userType;
    }
}
