package Model;

import java.util.Random;

public class PlayerMachine extends Player{
    @Override
    public void createBoard() {
        this.board= new Board();
        createShipLIst();
        placeRandomShips();
    }
    private void placeRandomShips(){
        Random random= new Random();
        for (Ship ship: ships){
            boolean placed=false;
            while (!placed){
                int row= random.nextInt(10);
                int column= random.nextInt(10);
                boolean horizontal= random.nextBoolean();
                try {
                    board.placeShip(row,column,ship,horizontal);
                    placed=true;
                }catch (UnvalidPositionException e){
                    // it trys another position
                }
            }
        }
    }
}
