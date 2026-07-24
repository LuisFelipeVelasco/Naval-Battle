package com.examplez.demo.storage;

import com.examplez.demo.model.Board;

import java.io.Serializable;

/**
 * Data Transfer Object that represents the stage of the game's state in this turn.
 * Stores the human player's and machine's boards.
 *
 * @see java.io.Serializable
 */
public class GameState implements Serializable {

    /** The human player's board state. */
    private final Board playerBoard;

    /** The machine player's board state. */
    private final Board playerMachineBoard;

    /**
     * Constructs a new {@code GameState} containing some of the necessary data to restore a game session.
     *
     * @param playerBoard        the {@link Board} instance for the human player.
     * @param playerMachineBoard the {@link Board} instance for the machine player.
     */
    public GameState(Board playerBoard, Board playerMachineBoard) {
        this.playerBoard = playerBoard;
        this.playerMachineBoard = playerMachineBoard;
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
}