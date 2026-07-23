package com.examplez.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**Representation of the flow of the game*/
public class Game {
    /** player that represents the user */
    PlayerHuman playerHuman;
    /** player that  represents the rival of the user*/
    PlayerMachine playerMachine;

    /**Number of ships at the beginning of the game*/
    int MaximumNumberOfShips=10;
    /**size of the squared - board*/
    int sizeBoard=10;

    public void startPlacement(){
        createHumanPlayer();
        playerHuman.createBoard(sizeBoard);
    }

    public void startMatch(){
        createMachinePlayer();
        playerMachine.createBoard(sizeBoard);
    }

    /**
     * Creates the machine player and assigns it to this game.
     */
   public void createMachinePlayer(){
        this.playerMachine = new PlayerMachine(createShipLIst());
    }

    /**
     * Creates the human player and assigns it to this game.
     */
   public void createHumanPlayer(){
        this.playerHuman = new PlayerHuman(createShipLIst());
    }

    /**
     * Builds the standard  fleet used by every player
     * @return a new list containing the  ships of the standard fleet
     */
    public List<Ship> createShipLIst(){
        List<Ship> fleet = new ArrayList<>();
        int idOfShip=0;
        fleet.add(ShipFactory.createShip(idOfShip,"carrier"));
        for (int i = 0; i < 2; i++) {
            idOfShip+=1;
            fleet.add(ShipFactory.createShip(idOfShip,"submarine"));
        }
        for (int i = 0; i < 3; i++) {
            idOfShip+=1;
            fleet.add(ShipFactory.createShip(idOfShip,"destructor"));
        }
        for (int i = 0; i < 4; i++) {
            idOfShip+=1;
            fleet.add(ShipFactory.createShip(idOfShip,"frigate"));
        }
        return fleet;

    }

    /** choose a random cell to attack the human player
     * verify if the cell selected has a ship , in that case :
     * sink the ship if is possible
     * register the hit on the ship
     * */
   public void processMachineAttack(){
        Random random= new Random();
        List<List<Integer>> validCellsToAttack = new ArrayList<>(List.of());
        for(int i=0; i<sizeBoard;i++){
            for(int j=0; j<sizeBoard; j++){
                if(isCellValidToAttack(playerHuman,i,j)){
                    validCellsToAttack.add(List.of(i,j));
                }
            }
        }
        List<Integer> randomCellToAttack= validCellsToAttack.get(random.nextInt(0, validCellsToAttack.size()));
        int rowCellToAttack = randomCellToAttack.get(0);
        int columnCellToAttack = randomCellToAttack.get(1);
        Board humanBoard= playerHuman.getBoard();
        humanBoard.attackCell(rowCellToAttack,columnCellToAttack);
        if(humanBoard.isShipOnCell(rowCellToAttack,columnCellToAttack)){
            if (humanBoard.isShipSunken(rowCellToAttack,columnCellToAttack)){
                humanBoard.sinkShip(rowCellToAttack,columnCellToAttack);
            }
        }
    }
    /**
     *verify if the cell selected is valid to attack
     * @param player the currentPlayerToAttack
     * @param row    the row of the cell selected
     * @param column the column of the cell selected
     * @return {@code true} when the cell wasn't already attacked
     */
    boolean isCellValidToAttack(Player player ,int row, int column){
        return !player.getBoard().isCellAlreadyAttacked(row,column);
    }

    /**
     * Returns the machine player of this game.
     *
     * @return the machine player, or {@code null} if it has not been created yet
     */
    public PlayerMachine getPlayerMachine(){
        return playerMachine;
    }

    /**
     * Returns the human player of this game.
     *
     * @return the human player, or {@code null} if it has not been created yet
     */
    public  PlayerHuman getPlayerHuman(){
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
