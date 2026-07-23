package com.examplez.demo;

import com.examplez.demo.model.Board;
import com.examplez.demo.model.Ship;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object that represents the stage of the game's state in this turn.
 * Stores the human player's and machine's boards, along with their respective ship collections.
 *
 * @see java.io.Serializable
 */
public class GameState implements Serializable {

    /** The human player's board state. */
    private final Board playerBoard;

    /** The machine player's board state. */
    private final Board playerMachineBoard;

    /** The list of ships belonging to the human player. */
    private final List<Ship> playerHumanShips;

    /** The list of ships belonging to the machine player. */
    private final List<Ship> playerMachineShips;

    /**
     * Constructs a new {@code GameState} containing all necessary data to restore a game session.
     *
     * @param playerBoard        the {@link Board} instance for the human player.
     * @param playerMachineBoard the {@link Board} instance for the machine player.
     * @param playerShips        the list of {@link Ship} objects owned by the human player.
     * @param machineShips       the list of {@link Ship} objects owned by the machine player.
     */
    public GameState(Board playerBoard, Board playerMachineBoard,
                     List<Ship> playerShips, List<Ship> machineShips) {
        this.playerBoard = playerBoard;
        this.playerMachineBoard = playerMachineBoard;
        this.playerHumanShips = playerShips;
        this.playerMachineShips = machineShips;
    }

    /**
     * Gets the human player's board.
     *
     * @return the human player's {@link Board}.
     */
    public Board getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Gets the machine player's board.
     *
     * @return the machine player's {@link Board}.
     */
    public Board getPlayerMachineBoard() {
        return playerMachineBoard;
    }

    /**
     * Gets the list of ships for the human player.
     *
     * @return a {@link List} of {@link Ship} objects owned by the human player.
     */
    public List<Ship> getPlayerHumanShips() {
        return playerHumanShips;
    }

    /**
     * Gets the list of ships for the machine player.
     *
     * @return a {@link List} of {@link Ship} objects owned by the machine player.
     */
    public List<Ship> getPlayerMachineShips() {
        return playerMachineShips;
    }
}