package Model;

import java.util.List;
import java.util.ArrayList;

public class Board {

    private List<List<Cell>> board;
    private static final int SizeList = 10;

    public static final String BLANK = "blank";
    public static final String SHIP = "ship";
    public static final String WATER = "water";
    public static final String HIT = "hit";
    public static final String SUNKED = "sunked";
    public Board(){
        board = new ArrayList<>();
        for (int row = 0; row < SizeList; row++){
            List<Cell> rowCells = new ArrayList<>();
            for (int column = 0; column < SizeList; column++){
                rowCells.add(new Cell());
            }
            board.add(rowCells);
        }

    }
    public List<List<Cell>> getBoard(){
        return board;
    }
    public boolean isShipOnCell(int fila, int columna){
        return board.get(fila).get(columna).getShip() != null;
    }
    public boolean isCellAlreadyAttacked(int fila, int columna){
        String estado = board.get(fila).get(columna).getState();
        return estado.equals(WATER) || estado.equals(HIT) || estado.equals(SUNKED);

    }
    public boolean containsShip(Ship ship){
        for (List<Cell> row : board){
            for (Cell cell : row){
                if (cell.getShip() == ship){
                    return true; // it compares by reference not by content
                }
            }
        }
        return false;
    }
    public boolean isValidPlacement(int fila, int columna, int size, boolean horizontal){
        for (int i = 0; i < size; i++){
            int f = horizontal ? fila : fila + i;
            int c = horizontal ? columna + i : columna;

            if (f >= SizeList || c >= SizeList){
                return false; // it goes off from the board
            }
            if (isShipOnCell(f, c)){
                return false; // there is already a ship on that position
            }
        }
        return true;
    }
    public void placeShip(int row, int column, Ship ship, boolean horizontal)
            throws UnvalidPositionException {

        if (!isValidPlacement(row, column, ship.getSize(), horizontal)){
            throw new UnvalidPositionException(row, column);
        }

        for (int i = 0; i < ship.getSize(); i++){
            int f = horizontal ? row : row + i;
            int c = horizontal ? column + i : column;

            Cell cell = board.get(f).get(c);
            cell.setShip(ship);
            cell.setState(SHIP);
        }
    }
}
