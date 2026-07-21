package Model;

import java.util.Random;

/**
 * Represents the machine (computer-controlled) opponent.
 * <p>
 * Unlike {@link PlayerHuman}, a machine player builds its board and
 * places its whole fleet automatically and randomly, without any user
 * interaction, as soon as {@link #createBoard()} is called.
 */
public class PlayerMachine extends Player{

    /**
     * Creates this player's board, builds the standard fleet and places
     * every ship on the board at random valid positions.
     */
    @Override
    public void createBoard() {
        this.board= new Board();
        this.ships = createShipLIst();
        placeRandomShips();
    }

    /**
     * Places every ship of the fleet on the board at a random position
     * and orientation. If a randomly chosen position turns out to be
     * invalid (off the board or overlapping another ship), a new random
     * position is tried until the ship can be placed successfully.
     */
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
