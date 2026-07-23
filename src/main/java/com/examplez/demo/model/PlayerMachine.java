package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.InvalidPositionException;

import java.util.List;
import java.util.Random;


/**Representation of the user's rival */
public class PlayerMachine extends  Player{
    PlayerMachine(List<Ship> ships){
        super(ships);
    }
    /**
     * Creates this player's board, builds the standard fleet and places
     * every ship on the board at random valid positions.
     */
    @Override
    public void createBoard(int sizeBoard) {
        this.board= new Board(sizeBoard);
        placeRandomShips(sizeBoard);
    }
    /**
     * Places every ship of the fleet on the board at a random position
     * and orientation. If a randomly chosen position turns out to be
     * invalid (off the board or overlapping another ship), a new random
     * position is tried until the ship can be placed successfully.
     *  @param sizeBoard height and with of the board
     */
    private void placeRandomShips(int sizeBoard){
        Random random= new Random();
        for (Ship ship: ships){
            boolean placed=false;
            while (!placed){
                int row= random.nextInt(sizeBoard);
                int column= random.nextInt(sizeBoard);
                boolean horizontal= random.nextBoolean();
                try {
                    board.placeShip(row,column,ship,horizontal);
                    placed=true;
                }catch (InvalidPositionException e){
                    // it trys another position
                }
            }
        }
    }

}
