package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
     PlayerHuman playerHuman;
     PlayerMachine playerMachine;
    int currentTurn;
    public void startGame(){

    }
    public void getShips(){

    }
   /* void processMachineAttack(){
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


    public void createMachinePlayer(){
        this.playerMachine = new PlayerMachine();
    }
    public void createHumanPlayer(){
        this.playerHuman = new PlayerHuman();
    }

    private void processPlayerAttack(){

    }
    private void setCurrentTurn(){

    }
    public PlayerMachine getPlayerMachine(){
        return playerMachine;
    }
    public  PlayerHuman getPlayerHuman(){
        return playerHuman;
    }
    private void getLastAttackByMachine(){

    }
    private void getLastAttackByHuman(){
          playerMachine.receiveAttackOnShip();
    }


}
