package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.InvalidPositionException;

import java.util.List;

/**Representation of the user */
public class PlayerHuman extends Player{
    PlayerHuman(List<Ship> ships){
        super(ships);
    }
    /**
     * Places a ship on this player's board at the given position and
     * orientation.
     * @param row        starting row index
     * @param column     starting column index
     * @param ship       the ship to place
     * @param horizontal {@code true} for a horizontal ship, {@code false} for vertical
     * @throws InvalidPositionException if the placement goes off the board or overlaps another ship
     */
    public void placeShip(int row, int column, Ship ship, boolean horizontal) throws InvalidPositionException {
        board.placeShip(row, column, ship, horizontal);
    }
    /**
     * Creates an empty board and the standard fleet for this player,
     * without placing any ship on the board yet. Ship placement is done
     * afterward by the placement controller through {@link #placeShip}.
     * @param sizeBoard height and with of the board
     */
    @Override
    public void createBoard(int sizeBoard) {
        this.board=new Board(sizeBoard);
    }
}
