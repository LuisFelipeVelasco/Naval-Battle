package com.examplez.demo.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Coordinates the complete lifecycle of a Naval Battle match.
 *
 * <p>The model owns both players, creates their fleets and processes machine
 * attacks. It is serializable so an active match can be restored without
 * reconstructing partial model objects.</p>
 */
public class Game implements Serializable {

    /** Serialization identifier for persisted games. */
    @Serial
    private static final long serialVersionUID = 2L;

    /** Number of rows and columns used by every board. */
    private static final int BOARD_SIZE = 10;

    /** Human player participating in this match. */
    private PlayerHuman playerHuman;

    /** Machine player participating in this match. */
    private PlayerMachine playerMachine;

    /**
     * Starts the human fleet-placement phase.
     *
     * @param playerName nonblank nickname entered on the menu
     */
    public void startPlacement(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("The player nickname cannot be blank.");
        }
        createHumanPlayer();
        playerHuman.setPlayerName(playerName.trim());
        playerHuman.createBoard(BOARD_SIZE);
    }

    /**
     * Creates the machine player and randomly places its complete fleet.
     */
    public void startMatch() {
        createMachinePlayer();
        playerMachine.createBoard(BOARD_SIZE);
    }

    /**
     * Creates a machine player with the standard fleet.
     */
    public void createMachinePlayer() {
        playerMachine = new PlayerMachine(createShipList());
    }

    /**
     * Creates a human player with the standard fleet.
     */
    public void createHumanPlayer() {
        playerHuman = new PlayerHuman(createShipList());
    }

    /**
     * Creates the standard ten-ship fleet.
     *
     * @return mutable list containing one carrier, two submarines, three
     *         destructors and four frigates
     */
    public List<Ship> createShipList() {
        List<Ship> fleet = new ArrayList<>();
        int shipId = 0;
        fleet.add(ShipFactory.createShip(shipId, "carrier"));
        for (int index = 0; index < 2; index++) {
            fleet.add(ShipFactory.createShip(++shipId, "submarine"));
        }
        for (int index = 0; index < 3; index++) {
            fleet.add(ShipFactory.createShip(++shipId, "destructor"));
        }
        for (int index = 0; index < 4; index++) {
            fleet.add(ShipFactory.createShip(++shipId, "frigate"));
        }
        return fleet;
    }

    /**
     * Backward-compatible alias for code that used the original misspelled name.
     *
     * @return standard fleet list
     * @deprecated use {@link #createShipList()}
     */
    @Deprecated
    public List<Ship> createShipLIst() {
        return createShipList();
    }

    /**
     * Processes one random machine attack against an unattacked human cell.
     *
     * @throws IllegalStateException if the active match is incomplete or no
     *                               unattacked cells remain
     */
    public void processMachineAttack() {
        if (playerHuman == null || playerHuman.getBoard() == null) {
            throw new IllegalStateException("A human board is required before attacking.");
        }

        List<List<Integer>> validTargets = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                if (isCellValidToAttack(playerHuman, row, column)) {
                    validTargets.add(List.of(row, column));
                }
            }
        }

        if (validTargets.isEmpty()) {
            throw new IllegalStateException("No unattacked cells remain.");
        }

        List<Integer> target = validTargets.get(new Random().nextInt(validTargets.size()));
        int row = target.get(0);
        int column = target.get(1);
        Board humanBoard = playerHuman.getBoard();

        humanBoard.attackCell(row, column);
        if (humanBoard.isShipOnCell(row, column)
                && humanBoard.isShipSunken(row, column)) {
            humanBoard.sinkShip(row, column);
        }
    }

    /**
     * Checks whether a board cell is available for attack.
     *
     * @param player player whose board will be inspected
     * @param row    target row
     * @param column target column
     * @return {@code true} when the cell has not been attacked
     */
    boolean isCellValidToAttack(Player player, int row, int column) {
        return !player.getBoard().isCellAlreadyAttacked(row, column);
    }

    /**
     * Returns the machine player.
     *
     * @return machine player, or {@code null} before the match starts
     */
    public PlayerMachine getPlayerMachine() {
        return playerMachine;
    }

    /**
     * Returns the human player.
     *
     * @return human player, or {@code null} before placement starts
     */
    public PlayerHuman getPlayerHuman() {
        return playerHuman;
    }

    /**
     * Indicates whether both players and boards are ready for combat.
     *
     * @return {@code true} when this model contains a complete active match
     */
    public boolean hasActiveMatch() {
        return playerHuman != null
                && playerHuman.getBoard() != null
                && playerMachine != null
                && playerMachine.getBoard() != null;
    }

    /**
     * Restores boards into initialized players for compatibility with older callers.
     *
     * @param humanBoard   restored human board
     * @param machineBoard restored machine board
     */
    public void restoreBoards(Board humanBoard, Board machineBoard) {
        if (playerHuman == null) {
            createHumanPlayer();
        }
        if (playerMachine == null) {
            createMachinePlayer();
        }
        playerHuman.setBoard(humanBoard);
        playerMachine.setBoard(machineBoard);
    }
}
