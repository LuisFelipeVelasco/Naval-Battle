package Model;

public class PlayerHuman extends Player{
    @Override
    public void createBoard() {
        this.board=new Board();

        this.ships=createShipLIst();
    }
    public void placeShip(int row, int column, Ship ship, boolean horizontal)
            throws UnvalidPositionException {
        board.placeShip(row, column, ship, horizontal);
    }
}
