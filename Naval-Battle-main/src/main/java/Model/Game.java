package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a single Naval Battle match.
 * <p>
 * A {@code Game} owns both participants (the {@link PlayerHuman} and the
 * {@link PlayerMachine}) and is responsible for the game-level logic
 * that involves both of them at once, such as tracking whose turn it is
 * and resolving attacks. It is the object passed from one controller to
 * the next (placement -> play) so that both phases operate on the same
 * shared state.
 * <p>
 * Note: several methods below are still placeholders (empty bodies) and
 * are meant to be completed as the shooting phase is implemented.
 */
public class Game {
     PlayerHuman playerHuman;
     PlayerMachine playerMachine;
    int currentTurn;

    /**
     * Starts the match. (Not yet implemented.)
     */
    public void startGame(){

    }

    /**
     * Returns the ships involved in the match. (Not yet implemented.)
     */
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


    /**
     * Creates the machine player and assigns it to this game.
     * Does not create its board or fleet yet; that is done separately
     * through {@link PlayerMachine#createBoard()}.
     */
    public void createMachinePlayer(){
        this.playerMachine = new PlayerMachine();
    }

    /**
     * Creates the human player and assigns it to this game.
     * Does not create its board or fleet yet; that is done separately
     * through {@link PlayerHuman#createBoard()}.
     */
    public void createHumanPlayer(){
        this.playerHuman = new PlayerHuman();
    }

    /**
     * Processes an attack made by the human player. (Not yet implemented.)
     */
    private void processPlayerAttack(){

    }

    /**
     * Updates whose turn it currently is. (Not yet implemented.)
     */
    private void setCurrentTurn(){

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
     * Returns the last attack performed by the machine player. (Not yet implemented.)
     */
    private void getLastAttackByMachine(){

    }

    /**
     * Returns the last attack performed by the human player. (Not yet implemented.)
     */
    private void getLastAttackByHuman(){
          playerMachine.receiveAttackOnShip();
    }


}
