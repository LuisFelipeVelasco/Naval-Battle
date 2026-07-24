package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.InvalidPositionException;

import java.util.List;

/**
 * Abstract base class representing any player in the game (human or machine).
 * <p>
 * A player has a fleet of ships and a board. Subclasses must implement
 * the {@link #createBoard(int)} method to define how the board is initialized
 * and how ships are placed (manually by the user or automatically by the AI).
 */
public abstract class Player {

    /**
     * The list of ships belonging to this player.
     */
    protected List<Ship> ships;

    /**
     * The board on which the player's ships are placed and attacks are recorded.
     */
    Board board;

    /**
     * Constructs a new player with the given fleet of ships.
     *
     * @param ships the list of ships that this player owns
     */
    Player(List<Ship> ships) {
        this.ships = ships;
    }

    /**
     * Returns this player's board.
     *
     * @return the board object
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns this player's fleet of ships.
     *
     * @return the list of ships
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * Creates this player's board and fleet. Each subclass implements
     * this differently: a human player starts with an empty board ready
     * for manual placement, while the machine player places its fleet
     * randomly right away.
     *
     * @param sizeBoard the width and height of the square board
     */
    public abstract void createBoard(int sizeBoard);

    /**
     * Takes a ship from the player's fleet and places it on the board at the
     * specified position with the given orientation. The ship is removed from
     * the fleet list upon successful placement.
     *
     * @param row           the starting row index for placement
     * @param column        the starting column index for placement
     * @param shipSelected  the ship to be placed
     * @param horizontal    {@code true} for horizontal placement, {@code false} for vertical
     * @throws InvalidPositionException if the chosen position is invalid (off board or overlapping)
     */
    public void placeShipOnBoard(int row, int column, Ship shipSelected, boolean horizontal)
            throws InvalidPositionException {
        for (Ship ship : ships) {
            if (shipSelected == ship) {
                ships.remove(shipSelected);
                break;
            }
        }
        board.placeShip(row, column, shipSelected, horizontal);
    }

    /**
     * Checks whether all ships in the fleet have been placed on the board.
     *
     * @return {@code true} if the fleet list is empty (all ships placed),
     *         {@code false} otherwise
     */
    public boolean isFleetFullyPlaced() {
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