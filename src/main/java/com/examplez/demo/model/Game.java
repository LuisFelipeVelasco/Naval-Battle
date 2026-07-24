package com.examplez.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representation of the flow of the game.
 * <p>
 * This class manages the entire game lifecycle: initial placement of ships for the human player,
 * random placement for the machine player, and the turn-based attack logic. It holds references
 * to both players and coordinates their actions.
 */
public class Game {

    /**
     * The human player.
     */
    PlayerHuman playerHuman;

    /**
     * The machine (AI) player.
     */
    PlayerMachine playerMachine;

    /**
     * The maximum number of ships each player starts with (total across all ship types).
     */
    int MaximumNumberOfShips = 10;

    /**
     * The size of the square board (number of rows and columns).
     */
    int sizeBoard = 10;

    /**
     * Initializes the placement phase for the human player.
     * Creates the human player, sets their name, and creates an empty board for them.
     *
     * @param playerName the name of the human player
     */
    public void startPlacement(String playerName) {
        createHumanPlayer();
        playerHuman.setPlayerName(playerName);
        playerHuman.createBoard(sizeBoard);
    }

    /**
     * Starts the match after both players' fleets are placed.
     * Creates the machine player and gives them a board with randomly placed ships.
     */
    public void startMatch() {
        createMachinePlayer();
        playerMachine.createBoard(sizeBoard);
    }

    /**
     * Creates the machine player and assigns it to this game.
     * The machine player is initialized with a standard fleet.
     */
    public void createMachinePlayer() {
        this.playerMachine = new PlayerMachine(createShipLIst());
    }

    /**
     * Creates the human player and assigns it to this game.
     * The human player is initialized with a standard fleet.
     */
    public void createHumanPlayer() {
        this.playerHuman = new PlayerHuman(createShipLIst());
    }

    /**
     * Builds the standard fleet used by every player.
     * The fleet consists of: 1 carrier, 2 submarines, 3 destroyers, and 4 frigates.
     *
     * @return a new list containing the ships of the standard fleet
     */
    public List<Ship> createShipLIst() {
        List<Ship> fleet = new ArrayList<>();
        int idOfShip = 0;
        fleet.add(ShipFactory.createShip(idOfShip, "carrier"));
        for (int i = 0; i < 2; i++) {
            idOfShip += 1;
            fleet.add(ShipFactory.createShip(idOfShip, "submarine"));
        }
        for (int i = 0; i < 3; i++) {
            idOfShip += 1;
            fleet.add(ShipFactory.createShip(idOfShip, "destructor"));
        }
        for (int i = 0; i < 4; i++) {
            idOfShip += 1;
            fleet.add(ShipFactory.createShip(idOfShip, "frigate"));
        }
        return fleet;
    }

    /**
     * Processes a single attack by the machine player on the human player's board.
     * <p>
     * The machine selects a random un-attacked cell from the human's board,
     * attacks it, and if a ship is hit, checks whether that ship is now sunken
     * and marks it as such if needed.
     */
    public void processMachineAttack() {
        Random random = new Random();
        List<List<Integer>> validCellsToAttack = new ArrayList<>(List.of());
        for (int i = 0; i < sizeBoard; i++) {
            for (int j = 0; j < sizeBoard; j++) {
                if (isCellValidToAttack(playerHuman, i, j)) {
                    validCellsToAttack.add(List.of(i, j));
                }
            }
        }
        List<Integer> randomCellToAttack = validCellsToAttack.get(random.nextInt(0, validCellsToAttack.size()));
        int rowCellToAttack = randomCellToAttack.get(0);
        int columnCellToAttack = randomCellToAttack.get(1);
        Board humanBoard = playerHuman.getBoard();
        humanBoard.attackCell(rowCellToAttack, columnCellToAttack);
        if (humanBoard.isShipOnCell(rowCellToAttack, columnCellToAttack)) {
            if (humanBoard.isShipSunken(rowCellToAttack, columnCellToAttack)) {
                humanBoard.sinkShip(rowCellToAttack, columnCellToAttack);
            }
        }
    }

    /**
     * Checks whether a given cell on a player's board is valid to attack.
     * A cell is valid if it has not been attacked before.
     *
     * @param player the player whose board is being checked
     * @param row    the row index of the cell
     * @param column the column index of the cell
     * @return {@code true} if the cell has not been attacked yet, {@code false} otherwise
     */
    boolean isCellValidToAttack(Player player, int row, int column) {
        return !player.getBoard().isCellAlreadyAttacked(row, column);
    }

    /**
     * Returns the machine player of this game.
     *
     * @return the machine player, or {@code null} if it has not been created yet
     */
    public PlayerMachine getPlayerMachine() {
        return playerMachine;
    }

    /**
     * Returns the human player of this game.
     *
     * @return the human player, or {@code null} if it has not been created yet
     */
    public PlayerHuman getPlayerHuman() {
        return playerHuman;
    }

    /**
     * Restores the human and machine boards from a loaded game state.
     *
     * @param humanBoard   the loaded human board
     * @param machineBoard the loaded machine board
     */
    public void restoreBoards(Board humanBoard, Board machineBoard) {
        this.playerHuman.setBoard(humanBoard);
        this.playerMachine.setBoard(machineBoard);
    }

}