package Model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a 10x10 Naval Battle board made of {@link Cell} objects.
 * <p>
 * A {@code Board} is responsible for storing the state of every cell
 * (blank, ship, water, hit or sunked), validating and placing ships on it,
 * and answering queries about its current state (e.g. whether a cell was
 * already attacked or whether a given ship is present on the board).
 * <p>
 * This class belongs to the Model layer of the MVC architecture and has
 * no dependency on JavaFX or any other UI framework.
 */
public class Board {

    private List<List<Cell>> board;
    private static final int SizeList = 10;

    /** State of a cell that has never been used (no ship, no shot). */
    public static final String BLANK = "blank";
    /** State of a cell that currently holds part of a ship. */
    public static final String SHIP = "ship";
    /** State of a cell that was shot at and had no ship on it. */
    public static final String WATER = "water";
    /** State of a cell that was shot at and hit part of a ship that is still afloat. */
    public static final String HIT = "hit";
    /** State of a cell that was shot at and hit part of a ship that has been sunk. */
    public static final String SUNKED = "sunked";

    /**
     * Creates a new empty 10x10 board.
     * Every cell starts in the {@link #BLANK} state, with no ship assigned.
     */
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

    /**
     * Returns the raw matrix of cells that make up this board.
     *
     * @return a 10x10 list of lists of {@link Cell}
     */
    public List<List<Cell>> getBoard(){
        return board;
    }

    /**
     * Checks whether a ship (sunk or not) occupies the given cell.
     *
     * @param fila    the row index (0-9)
     * @param columna the column index (0-9)
     * @return {@code true} if a ship is present on that cell
     */
    public boolean isShipOnCell(int fila, int columna){
        return board.get(fila).get(columna).getShip() != null;
    }

    /**
     * Checks whether the given cell has already received a shot.
     *
     * @param fila    the row index (0-9)
     * @param columna the column index (0-9)
     * @return {@code true} if the cell's state is {@link #WATER}, {@link #HIT} or {@link #SUNKED}
     */
    public boolean isCellAlreadyAttacked(int fila, int columna){
        String estado = board.get(fila).get(columna).getState();
        return estado.equals(WATER) || estado.equals(HIT) || estado.equals(SUNKED);

    }

    /**
     * Checks whether the given ship instance has been placed anywhere on this board.
     * Comparison is done by reference, not by content, since two ships of the
     * same type are still different instances.
     *
     * @param ship the ship instance to look for
     * @return {@code true} if at least one cell references this exact ship
     */
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

    /**
     * Validates whether a ship of the given size could be placed starting at
     * (fila, columna) with the given orientation, without going off the board
     * or overlapping another ship.
     *
     * @param fila      starting row index
     * @param columna   starting column index
     * @param size      number of cells the ship occupies
     * @param horizontal {@code true} for a horizontal ship, {@code false} for vertical
     * @return {@code true} if the placement is valid
     */
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

    /**
     * Places the given ship on the board starting at (row, column), in the
     * given orientation. All cells occupied by the ship are updated to
     * reference this ship and switch to the {@link #SHIP} state.
     *
     * @param row        starting row index
     * @param column     starting column index
     * @param ship       the ship to place
     * @param horizontal {@code true} for a horizontal ship, {@code false} for vertical
     * @throws UnvalidPositionException if the placement goes off the board or overlaps another ship
     */
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
    public String attackCell(int row, int column){
        if (isCellAlreadyAttacked(row,column)){
            throw new AlreadyAttackedException(row, column);
        }
        Cell cell = board.get(row).get(column);
        Ship ship = cell.getShip();

        if (ship == null){
            cell.setState(WATER);
            return WATER;
        }

        ship.increaseHitOnShip();

        if (!ship.isShipAfloat()){
            markShipAsSunked(ship);
            return SUNKED;
        }
        cell.setState(HIT);
        return HIT;

    }
    private void markShipAsSunked(Ship ship){
        for (List<Cell> row : board){
            for (Cell cell : row){
                if (cell.getShip() == ship){
                    cell.setState(SUNKED);
                }
            }
        }
    }
}
