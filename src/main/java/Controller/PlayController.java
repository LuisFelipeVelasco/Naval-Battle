package Controller;
import javafx.scene.shape.Rectangle;
import Model.Board;
import Model.Cell;
import Model.Game;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import java.awt.*;
import java.util.List;

public class PlayController {
    @FXML private GridPane mainBoardGrid;
    @FXML private GridPane positionBoardGrid;

    private StackPane[][] mainCells = new StackPane[10][10];
    private StackPane[][] positionCells = new StackPane[10][10];
    @FXML public void initialize(){
        createCells(mainBoardGrid, mainCells, true);
        createCells(positionBoardGrid, positionCells, false);
    }
    private Game gameModel;

    public void setGameModel(Game gameModel){
        this.gameModel = gameModel;

    }
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
                   // VisualCell.setOnMouseClicked(event -> onCellClicked(f, c));
                }

                cells[row][column] = VisualCell;
                grid.add(VisualCell, column, row);
            }
        }
    }
    public void loadBoard(){
        drawMainBoard();
        drawPositionBoard();
        // recorre gameModel.getPlayerHuman().getBoard() y gameModel.getPlayerMachine().getBoard()
        // y pinta las celdas correspondientes en los GridPane ya creados por initialize()
    }
    private void drawMainBoard(){
        List<List<Cell>> board = gameModel.getPlayerMachine().getBoard().getBoard();

        for (int row = 0; row < 10; row++){
            for (int column = 0; column < 10; column++){
                String state = board.get(row).get(column).getState();
                Rectangle background = (Rectangle) mainCells[row][column].getChildren().get(0);

                switch (state){
                    case Board.WATER -> background.setFill(Color.LIGHTBLUE);      // marca de agua (X) se añadiría aparte
                    case Board.HIT -> background.setFill(Color.ORANGE);
                    case Board.SUNKED -> background.setFill(Color.DARKRED);
                    default -> background.setFill(Color.LIGHTBLUE); // VACIO o BARCO oculto: se ve igual que el agua
                }
            }
        }
    }
    private void drawPositionBoard(){
        List<List<Cell>> board = gameModel.getPlayerHuman().getBoard().getBoard();

        for (int row = 0; row < 10; row++){
            for (int column = 0; column < 10; column++){
                String state = board.get(row).get(column).getState();
                Rectangle background = (Rectangle) positionCells[row][column].getChildren().get(0);

                switch (state){
                    case Board.SHIP -> background.setFill(Color.DARKSLATEGRAY);
                    case Board.WATER -> background.setFill(Color.LIGHTBLUE);
                    case Board.HIT -> background.setFill(Color.ORANGE);
                    case Board.SUNKED -> background.setFill(Color.DARKRED);
                    default -> background.setFill(Color.LIGHTBLUE); // VACIO
                }
            }
        }
    }


}
