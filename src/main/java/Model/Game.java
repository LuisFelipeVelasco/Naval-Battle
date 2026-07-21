package Model;

import java.util.ArrayList;
import java.util.List;

public class Game {
     PlayerHuman playerHuman;
     PlayerMachine playerMachine;
    int currentTurn;
    public void startGame(){

    }
    public void getShips(){

    }


    public void createMachinePlayer(){
        this.playerMachine = new PlayerMachine();
    }
    public void createHumanPlayer(){
        this.playerHuman = new PlayerHuman();
    }
    private void processMachineTurn(){

    }
    private void isCellValidToAttack(){

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

    }


}
