package com.examplez.demo.model;

import java.io.Serializable;
import com.examplez.demo.model.exceptions.AlreadyAttackedException;
import com.examplez.demo.model.exceptions.InvalidPositionException;

import java.util.ArrayList;
import java.util.List;

/** Representation of a player's board
 *
 * <p>Implements {@link Serializable} to support game persistence
 * within GameState.</p>
 *
 * @see java.io.Serializable
 * */
public class Board implements Serializable {
    /** matrix that represents the board*/
    List<List<Cell>> board;

    private static int SizeList;
    /** State of a cell that has never been used (no ship, no shot). */
    public static final String BLANK = "blank";
    /** State of a cell that currently holds part of a ship. */
    public static final String SHIP = "ship";
    /** State of a cell that was shot at and had no ship on it. */
    public static final String WATER = "water";
    /** State of a cell that was shot at and hit part of a ship that is still afloat. */
    public static final String HIT = "hit";
    /** State of a cell that was shot at and hit part of a ship that has been sunk. */
    public static final String SUNKEN = "sunken";

    private List<Integer> positionLastCellAttacked;

    /**
     * Creates a new empty 10x10 board.
     * Every cell starts in the {@link #BLANK} state, with no ship assigned.
     */
    public Board(int sizeBoard){
        SizeList=sizeBoard;
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
     *verify if a cell was attacked
     * @param row row of cell chosen to verify
     * @param column column of cell chosen to verify
     * @return  {@code true} when the cell was attacked
     */
    public boolean isCellAlreadyAttacked(int row, int column){
        String state= board.get(row).get(column).getState();
        return state.equals(WATER) || state.equals(HIT) || state.equals(SUNKEN);
    }

    /**
     * Verify if the cell chosen has a ship
     * @param row row of cell chosen to verify
     * @param column column of cell chosen to verify
     * @return {@code true} when the cell has a ship
     */
    boolean isShipOnCell(int row , int column){
        return !(board.get(row).get(column).getShip()==null);
    }

    /**
     * Validates whether a ship of the given size could be placed starting at
     * (row, column) with the given orientation, without going off the board
     * or overlapping another ship.
     *
     * @param row      starting row index
     * @param column   starting column index
     * @param size      number of cells the ship occupies
     * @param horizontal {@code true} for a horizontal ship, {@code false} for vertical
     * @return {@code true} if the placement is valid
     */
    public boolean isValidPlacement(int row, int column, int size, boolean horizontal){
        for (int i = 0; i < size; i++){
            int currentRow = horizontal ? row : row + i;
            int currentColumn = horizontal ? column + i : column;
            if (currentRow >= SizeList || currentColumn >= SizeList){
                return false; // it goes off from the board
            }
            if (isShipOnCell(currentRow, currentColumn)){
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
     * @throws InvalidPositionException if the placement goes off the board or overlaps another ship
     */
    public void placeShip(int row, int column, Ship ship, boolean horizontal) throws InvalidPositionException {
        if (!isValidPlacement(row, column, ship.getSize(), horizontal)){
            throw new InvalidPositionException(row, column);
        }

        for (int i = 0; i < ship.getSize(); i++){
            int currentRow = horizontal ? row : row + i;
            int currentColumn = horizontal ? column + i : column;
            Cell cell = board.get(currentRow).get(currentColumn);
            cell.setShip(ship);
            cell.setState(SHIP);
        }
    }
    /**
     * change the state of the cell chosen to attacked and modify positionLastCellAttacked
     * @param row row of cell chosen
     * @param column column of cell chosen
     */
    public void attackCell(int row, int column){

        if (isCellAlreadyAttacked(row,column)){
            throw new AlreadyAttackedException(row, column);
        }
        Cell cellAttacked= board.get(row).get(column);
        positionLastCellAttacked=List.of(row,column);
        if(cellAttacked.getShip()==null){
            cellAttacked.setState(WATER);
            return;
        }
        cellAttacked.setState(HIT);
    }
    /**Verify if the ship on the cell chosen is sunken
     * @param row row of cell chosen to verify
     * @param column column of cell chosen to verify
     * @return {@code false} when at least one cell with the ship is no attacked
     */
    public boolean isShipSunken(int row , int column){
        int idOfShip = getIdOfShipOnCell(row,column);
        for(int i=0;i<SizeList;i++){
            for(int j=0;j<SizeList;j++){
                Cell currentCell=board.get(i).get(j);
                if(currentCell.getIdOfShip()==idOfShip){
                    if(currentCell.getState().equals(SHIP)) return false;
                }
            }
        }
        return true;
    }

    /**
     * Iterate each cell on the board to see if it has at least one ship afloat
     * @return {@code true} when it finds a cell with a ship or with a ship attacked
     */

    public boolean isBoardWithShips(){
        for(int i=0;i<SizeList;i++){
            for(int j=0;j<SizeList;j++){
                Cell currentCell=board.get(i).get(j);
                if(currentCell.getState().equals(SHIP)|| currentCell.getState().equals(HIT)){
                    return true;
                }
            }
        }
        return false;
    }

    /**change the state of the cell chosen for sunken
     * @param row row of cell chosen
     * @param column column of cell
     */
   public void sinkShip(int row , int column){
        int idOfShip = getIdOfShipOnCell(row,column);
        for(int i=0;i<SizeList;i++){
            for(int j=0;j<SizeList;j++){
                Cell currentCell=board.get(i).get(j);
                if(currentCell.getIdOfShip()==idOfShip){
                    currentCell.setState(SUNKEN);
                }
            }
        }
    }

    /**
     * @param row row of cell chosen
     * @param column column of cell chosen
     * @return id of ship on the cell chosen
     */
    int getIdOfShipOnCell(int row , int column){
        return board.get(row).get(column).getIdOfShip();
    }

    /**
     * @param row ow of cell chosen
     * @param column column of cell chosen
     * @return State of the cell chosen
     */
    public String getStateOfCell(int row,int column){
        return board.get(row).get(column).getState();
    }

    /**
     * @return the board
     */

    public List<List<Cell>>  getCells(){
        return board;
    }

    /**@return the position of the last cell attacked*/
    public List<Integer> getPositionLastCellAttacked(){return positionLastCellAttacked;}
    /**@return state of the last cell attacked*/
    public String getStateLastCellAttacked(){
        int row = positionLastCellAttacked.get(0);
        int column= positionLastCellAttacked.get(1);
        return getStateOfCell(row,column);
    }
}
