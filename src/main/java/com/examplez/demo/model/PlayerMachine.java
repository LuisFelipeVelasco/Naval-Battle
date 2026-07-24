package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.InvalidPositionException;

import java.io.Serial;
import java.util.List;
import java.util.Random;

/**
 * Representation of the user's rival (the machine/AI player).
 * <p>
 * The machine player's fleet is placed automatically on the board at random
 * positions during board creation. Unlike the human player, the machine does
 * not require manual interaction for ship placement.
 */
public class PlayerMachine extends Player {

    /** Serialization identifier for persisted machine players. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new machine player with the given fleet of ships.
     *
     * @param ships the list of ships that this player owns
     */
    PlayerMachine(List<Ship> ships) {
        super(ships);
    }

    /**
     * Creates this player's board of the specified size and places every
     * ship in the fleet at random valid positions.
     *
     * @param sizeBoard the width and height of the square board
     */
    @Override
    public void createBoard(int sizeBoard) {
        this.board = new Board(sizeBoard);
        placeRandomShips(sizeBoard);
    }

    /**
     * Places every ship of the fleet on the board at a random position
     * and orientation. For each ship, a random position and orientation
     * are generated repeatedly until a valid placement is found.
     * If a randomly chosen position is invalid (off the board or overlapping
     * another ship), a new random position is tried until the ship can be
     * placed successfully.
     *
     * @param sizeBoard the width and height of the board (used to bound random coordinates)
     */
    private void placeRandomShips(int sizeBoard) {
        Random random = new Random();
        for (Ship ship : ships) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(sizeBoard);
                int column = random.nextInt(sizeBoard);
                boolean horizontal = random.nextBoolean();
                try {
                    board.placeShip(row, column, ship, horizontal);
                    placed = true;
                } catch (InvalidPositionException e) {
                    // try another position
                }
            }
        }
    }
}
