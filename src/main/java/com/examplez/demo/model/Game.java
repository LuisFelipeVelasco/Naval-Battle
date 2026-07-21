package com.examplez.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**Representation of the flow of the game*/
public class Game {
    /** player that represents the user */
    PlayerHuman playerHuman;
    /** player that  represents the rival of the user*/
    PlayerMachine playerMachine;

    /** choose a random cell to attack the human player
     * verify if the cell selected has a ship , in that case :
     * sink the ship if is possible
     * register the hit on the ship
     * */
    void processMachineAttack(){
        List<List<Integer>> validCellsToAttack = new ArrayList<>(List.of());
        for(int i=0; i<=10;i++){
            for(int j=0; j<=10; j++){
                if(isCellValidToAttack(playerHuman,i,j)){
                    validCellsToAttack.add(List.of(i,j));
                }
            }
        }
        List<Integer> randomCellToAttack= validCellsToAttack.get(ThreadLocalRandom.current().nextInt(0, validCellsToAttack.size()));
        int rowCellToAttack = randomCellToAttack.get(0);
        int columnCellToAttack = randomCellToAttack.get(1);
        Board humanBoard= playerHuman.getBoard();
        humanBoard.attackCell(rowCellToAttack,columnCellToAttack);
        if(humanBoard.isShipOnCell(rowCellToAttack,columnCellToAttack)){
            if (humanBoard.isShipSunken(rowCellToAttack,columnCellToAttack)){
                humanBoard.sinkShip(rowCellToAttack,columnCellToAttack);
            }
            int idOfShipAttacked= humanBoard.getIdOfShipOnCell(rowCellToAttack,columnCellToAttack);
            playerHuman.receiveAttackOnShip(idOfShipAttacked);
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
}
