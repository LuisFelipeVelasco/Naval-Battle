package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.InvalidPositionException;

import java.util.List;

/**Represents any player on the game*/
public abstract  class Player {
    protected List<Ship> ships;
    /**Board of the player on the game*/
    Board board;
    Player(List<Ship> ships){
        this.ships=ships;
    }
    /**@return board of the player*/
    public Board getBoard(){return board;}

    /**@return ships of the player*/
    public List<Ship> getShips(){return ships;}

    /**
     * Creates this player's board and fleet. Each subclass implements
     * this differently: a human player starts with an empty board ready
     * for manual placement, while the machine player places its fleet
     * randomly right away.
     * @param sizeBoard width and height of the board
     */
    public abstract void createBoard(int sizeBoard);

    /**
     * Take a ship out from the list of ships to the board of the player
     * @param row row chosen
     * @param column column chosen
     * @param shipSelected ship chosen
     * @param horizontal orientation chosen
     * @throws InvalidPositionException when the position isn't valid
     */
    public void placeShipOnBoard(int row, int column,Ship shipSelected,boolean horizontal) throws InvalidPositionException {
        for(Ship ship:ships){
            if(shipSelected==ship){
                ships.remove(shipSelected);
                break;
            }
        }
        board.placeShip(row,column,shipSelected,horizontal);
    }

    /**
     * Verify if the player placed all the ships
     * @return {@code  true} when the list of ships doesn't have ships
     */
    public boolean isFleetFullyPlaced(){
        System.out.println(ships.size());
        return ships.isEmpty();
    }

    /**
     * Sets or restores the player's board (used when loading a saved game session).
     *
     * @param board the restored board instance to assign
     */
    public void setBoard(Board board) {
        this.board = board;
    }

}
