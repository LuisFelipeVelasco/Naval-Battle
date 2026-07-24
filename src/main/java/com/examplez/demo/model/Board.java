package com.examplez.demo.model;

import java.io.Serializable;
import com.examplez.demo.model.exceptions.AlreadyAttackedException;
import com.examplez.demo.model.exceptions.InvalidPositionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a player's board for the Battleship game.
 * <p>
 * The board is a square grid where ships are placed and attacks are recorded.
 * Each cell can be in one of several states: blank, ship, water, hit, or sunken.
 *
 * <p>Implements {@link Serializable} to support game persistence
 * within GameState.</p>
 *
 * @see java.io.Serializable
 */
public class Board implements Serializable {

    /**
     * The matrix of cells that makes up the board.
     */
    List<List<Cell>> board;

    /**
     * The size of the board (number of rows/columns). Stored statically,
     * but should ideally be an instance field.
     */
    private static int SizeList;

    /**
     * State of a cell that has never been used (no ship, no shot).
     */
    public static final String BLANK = "blank";

    /**
     * State of a cell that currently holds part of a ship.
     */
    public static final String SHIP = "ship";

    /**
     * State of a cell that was shot at and had no ship on it.
     */
    public static final String WATER = "water";

    /**
     * State of a cell that was shot at and hit part of a ship that is still afloat.
     */
    public static final String HIT = "hit";

    /**
     * State of a cell that was shot at and hit part of a ship that has been sunk.
     */
    public static final String SUNKEN = "sunken";
    /** Counter for ships sunk on this board. */
    private int numberShipsSunk;

    /**
     * The coordinates (row, column) of the last attacked cell, stored as a list.
     */
    private List<Integer> positionLastCellAttacked;

    /**
     * Creates a new empty square board of the given size.
     * Every cell starts in the {@link #BLANK} state, with no ship assigned.
     *
     * @param sizeBoard the size of the board (number of rows and columns)
     */
    public Board(int sizeBoard) {
        SizeList = sizeBoard;
        board = new ArrayList<>();
        for (int row = 0; row < SizeList; row++) {
            List<Cell> rowCells = new ArrayList<>();
            for (int column = 0; column < SizeList; column++) {
                rowCells.add(new Cell());
            }
            board.add(rowCells);
        }
    }

    /**
     * Checks whether a specific cell has already been attacked.
     *
     * @param row    the row index of the cell
     * @param column the column index of the cell
     * @return {@code true} if the cell has been attacked (state is water, hit, or sunken),
     *         {@code false} otherwise
     */
    public boolean isCellAlreadyAttacked(int row, int column) {
        String state = board.get(row).get(column).getState();
        return state.equals(WATER) || state.equals(HIT) || state.equals(SUNKEN);
    }

    /**
     * Checks whether there is a ship on the given cell.
     *
     * @param row    the row index
     * @param column the column index
     * @return {@code true} if the cell contains a ship, {@code false} otherwise
     */
    boolean isShipOnCell(int row, int column) {
        return !(board.get(row).get(column).getShip() == null);
    }

    /**
     * Validates whether a ship of the given size could be placed starting at
     * (row, column) with the given orientation, without going off the board
     * or overlapping another ship.
     *
     * @param row        starting row index
     * @param column     starting column index
     * @param size       the number of cells the ship occupies
     * @param horizontal {@code true} for a horizontal ship, {@code false} for vertical
     * @return {@code true} if the placement is valid, {@code false} otherwise
     */
    public boolean isValidPlacement(int row, int column, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int currentRow = horizontal ? row : row + i;
            int currentColumn = horizontal ? column + i : column;
            if (currentRow >= SizeList || currentColumn >= SizeList) {
                return false; // goes off the board
            }
            if (isShipOnCell(currentRow, currentColumn)) {
                return false; // there is already a ship in that position
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
     * @param horizontal {@code true} for horizontal placement, {@code false} for vertical
     * @throws InvalidPositionException if the placement goes off the board or overlaps another ship
     */
    public void placeShip(int row, int column, Ship ship, boolean horizontal) throws InvalidPositionException {
        if (!isValidPlacement(row, column, ship.getSize(), horizontal)) {
            throw new InvalidPositionException(row, column);
        }

        for (int i = 0; i < ship.getSize(); i++) {
            int currentRow = horizontal ? row : row + i;
            int currentColumn = horizontal ? column + i : column;
            Cell cell = board.get(currentRow).get(currentColumn);
            cell.setShip(ship);
            cell.setState(SHIP);
        }
    }

    /**
     * Attacks the specified cell. If the cell is already attacked, an exception is thrown.
     * The state of the cell is updated to either {@link #WATER} or {@link #HIT}
     * depending on whether a ship is present, and the position is stored as the last attacked cell.
     *
     * @param row    the row index of the target cell
     * @param column the column index of the target cell
     * @throws AlreadyAttackedException if the cell has already been attacked
     */
    public void attackCell(int row, int column) {
        if (isCellAlreadyAttacked(row, column)) {
            throw new AlreadyAttackedException(row, column);
        }
        Cell cellAttacked = board.get(row).get(column);
        positionLastCellAttacked = List.of(row, column);
        if (cellAttacked.getShip() == null) {
            cellAttacked.setState(WATER);
            return;
        }
        cellAttacked.setState(HIT);
    }

    /**
     * Determines whether the ship on the specified cell is completely sunken.
     * A ship is sunken if all its cells have been hit (i.e., none are still in the {@link #SHIP} state).
     *
     * @param row    the row index of a cell belonging to the ship
     * @param column the column index of a cell belonging to the ship
     * @return {@code true} if the ship is sunken, {@code false} if at least one cell is still afloat
     */
    public boolean isShipSunken(int row, int column) {
        int idOfShip = getIdOfShipOnCell(row, column);
        for (int i = 0; i < SizeList; i++) {
            for (int j = 0; j < SizeList; j++) {
                Cell currentCell = board.get(i).get(j);
                if (currentCell.getIdOfShip() == idOfShip) {
                    if (currentCell.getState().equals(SHIP)) return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks whether the board still contains any ships that are afloat (not fully sunken).
     * A ship is considered present if any cell is in the {@link #SHIP} or {@link #HIT} state.
     *
     * @return {@code true} if there is at least one ship cell that is not sunken,
     *         {@code false} if no ships remain (all are sunken or never existed)
     */
    public boolean isBoardWithShips() {
        for (int i = 0; i < SizeList; i++) {
            for (int j = 0; j < SizeList; j++) {
                Cell currentCell = board.get(i).get(j);
                if (currentCell.getState().equals(SHIP) || currentCell.getState().equals(HIT)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Marks all cells belonging to the ship on the specified cell as {@link #SUNKEN}.
     * This method does not check if the ship is actually sunken; it forces the state change.
     *
     * @param row    the row index of a cell belonging to the ship
     * @param column the column index of a cell belonging to the ship
     */
    public void sinkShip(int row, int column) {
        int idOfShip = getIdOfShipOnCell(row, column);
        for (int i = 0; i < SizeList; i++) {
            for (int j = 0; j < SizeList; j++) {
                Cell currentCell = board.get(i).get(j);
                if (currentCell.getIdOfShip() == idOfShip) {
                    currentCell.setState(SUNKEN);
                }
            }
        }
       numberShipsSunk++;
    }

    /**
     * Retrieves the unique ID of the ship placed on the specified cell.
     *
     * @param row    the row index
     * @param column the column index
     * @return the ID of the ship, or a default value if no ship is present (handled by Cell)
     */
    int getIdOfShipOnCell(int row, int column) {
        return board.get(row).get(column).getIdOfShip();
    }

    /**
     * Returns the current state of the specified cell.
     *
     * @param row    the row index
     * @param column the column index
     * @return the state string (one of the constants: BLANK, SHIP, WATER, HIT, SUNKEN)
     */
    public String getStateOfCell(int row, int column) {
        return board.get(row).get(column).getState();
    }

    /**
     * Returns the entire board matrix.
     *
     * @return a list of lists representing the board cells
     */
    public List<List<Cell>> getCells() {
        return board;
    }

    /**
     * Returns the coordinates of the last cell that was attacked.
     *
     * @return a list containing the row and column of the last attacked cell
     */
    public List<Integer> getPositionLastCellAttacked() {
        return positionLastCellAttacked;
    }

    /**
     * Returns the state of the last cell that was attacked.
     *
     * @return the state string of the last attacked cell
     */
    public String getStateLastCellAttacked() {
        int row = positionLastCellAttacked.get(0);
        int column = positionLastCellAttacked.get(1);
        return getStateOfCell(row, column);
    }

    /**
     * Gets the total number of ships sunk on this board.
     *
     * @return the number of sunk ships.
     */
    public int getNumberShipsSunk() { return numberShipsSunk; }
}