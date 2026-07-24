package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.InvalidPositionException;

import java.io.Serial;
import java.util.List;

/**
 * Representation of the human player in the game.
 * <p>
 * The human player interacts with the user through the UI. Their board is
 * initially empty, and ships are placed manually via the placement view.
 * This class extends {@link Player} and implements the board creation
 * logic that leaves ships unplaced for later manual positioning.
 */
public class PlayerHuman extends Player {

    /** Serialization identifier for persisted human players. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The name of the human player as entered by the user.
     */
    private String playerName;

    /**
     * Returns the name of the human player.
     *
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Sets the name of the human player.
     *
     * @param playerName the name to assign to this player
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Constructs a new human player with the given fleet of ships.
     *
     * @param ships the list of ships that this player owns
     */
    PlayerHuman(List<Ship> ships) {
        super(ships);
    }

    /**
     * Places a ship on this player's board at the given position and orientation.
     * This method is a convenience wrapper that delegates to the board's
     * {@link Board#placeShip} method.
     *
     * @param row        the starting row index for placement
     * @param column     the starting column index for placement
     * @param ship       the ship to place
     * @param horizontal {@code true} for horizontal placement, {@code false} for vertical
     * @throws InvalidPositionException if the placement goes off the board or overlaps another ship
     */
    public void placeShip(int row, int column, Ship ship, boolean horizontal) throws InvalidPositionException {
        board.placeShip(row, column, ship, horizontal);
    }

    /**
     * Creates an empty board of the specified size for this player.
     * The fleet is already available via the constructor, but no ships are placed
     * on the board at creation time. Ship placement is done afterward by the
     * placement controller through {@link #placeShip}.
     *
     * @param sizeBoard the width and height of the square board
     */
    @Override
    public void createBoard(int sizeBoard) {
        this.board = new Board(sizeBoard);
    }
}
