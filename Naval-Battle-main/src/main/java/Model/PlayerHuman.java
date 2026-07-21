package Model;

/**
 * Represents the human player.
 * <p>
 * Unlike {@link PlayerMachine}, a human player's board starts empty and
 * its fleet is placed manually, ship by ship, through the placement
 * phase controller (drag-and-drop in the UI).
 */
public class PlayerHuman extends Player{

    /**
     * Creates an empty board and the standard fleet for this player,
     * without placing any ship on the board yet. Ship placement is done
     * afterwards by the placement controller through {@link #placeShip}.
     */
    @Override
    public void createBoard() {
        this.board=new Board();

        this.ships=createShipLIst();
    }

    /**
     * Places a ship on this player's board at the given position and
     * orientation.
     *
     * @param row        starting row index
     * @param column     starting column index
     * @param ship       the ship to place
     * @param horizontal {@code true} for a horizontal ship, {@code false} for vertical
     * @throws UnvalidPositionException if the placement goes off the board or overlaps another ship
     */
    public void placeShip(int row, int column, Ship ship, boolean horizontal)
            throws UnvalidPositionException {
        board.placeShip(row, column, ship, horizontal);
    }
}
