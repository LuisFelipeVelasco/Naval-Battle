package com.examplez.demo.model;

import java.io.Serializable;
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
    /**
     *verify if a cell was attacked
     * @param row row of cell chosen to verify
     * @param column column of cell chosen to verify
     * @return  {@code true} when the cell was attacked
     */
    boolean isCellAlreadyAttacked(int row,int column){
        return !board.get(row).get(column).getState().equals("no attacked");
    }

    /**
     * Verify if the cell chosen has a ship
     * @param row row of cell chosen to verify
     * @param column column of cell chosen to verify
     * @return {@code true} when the cell has a ship
     */
    boolean isShipOnCell(int row , int column){
        return !board.get(row).get(column).getTypeOfShip().equals("no ship");
    }

    /**
     * change the state of the cell chosen to attacked
     * @param row row of cell chosen
     * @param column column of cell chosen
     */
    void attackCell(int row, int column){
        board.get(row).get(column).setState("attacked");
    }
    /**Verify if the ship on the cell chosen is sunken
     * @param row row of cell chosen to verify
     * @param column column of cell chosen to verify
     * @return {@code false} when at least one cell with the ship is no attacked
     */
    boolean isShipSunken(int row , int column){
        int idOfShip = getIdOfShipOnCell(row,column);
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                Cell currentCell=board.get(i).get(j);
                if(currentCell.getIdOfShip()==idOfShip && (i!=row && j!=column)){
                    if(currentCell.getState().equals("no attacked")) return false;
                }
            }
        }
        return true;
    }
    /**change the state of the cell chosen for sunken
     * @param row row of cell chosen
     * @param column column of cell
     */
    void sinkShip(int row , int column){
        int idOfShip = getIdOfShipOnCell(row,column);
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                Cell currentCell=board.get(i).get(j);
                if(currentCell.getIdOfShip()==idOfShip){
                    currentCell.setState("sunken");
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

}
