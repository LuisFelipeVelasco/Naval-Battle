package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    /**
     * it executes before any test
     * it creates a blank board of 10x10 in order to avoid interferences between tests.
     */
    @BeforeEach
    void setUp(){
        board = new Board();
    }

    @Test
    void newBlankBoard(){
        // no cells with ships at the start
        for (int row = 0; row < 10; row++){
            for (int column = 0; column < 10; column++){
                assertFalse(board.isShipOnCell(row, column),
                        "row (" + row + "," + column + ") shouldnt have a ship");
            }
        }
    }

    @Test
    void correctCellsOnHorizontalShip() throws UnvalidPositionException {
        Ship frigata = new Ship("frigata", 1);
        board.placeShip(0, 0, frigata, true);

        assertTrue(board.isShipOnCell(0, 0));
        assertTrue(board.containsShip(frigata));
    }

    @Test
    void aHorizontalShipMustBeAllHisCells() throws UnvalidPositionException {
        Ship submarine = new Ship("submarine", 3);
        board.placeShip(2, 4, submarine, true);

        assertTrue(board.isShipOnCell(2, 4));
        assertTrue(board.isShipOnCell(2, 5));
        assertTrue(board.isShipOnCell(2, 6));

        // la celda inmediatamente después del barco NO debería estar ocupada
        assertFalse(board.isShipOnCell(2, 7));
    }

    @Test
    void correctCellsOnVeerticalShip() throws UnvalidPositionException {
        Ship destructor = new Ship("destructor", 2);
        board.placeShip(3, 3, destructor, false);

        assertTrue(board.isShipOnCell(3, 3));
        assertTrue(board.isShipOnCell(4, 3));
        assertFalse(board.isShipOnCell(5, 3));
    }

    @Test
    void exceptionInShipOffBoard(){
        Ship carrier = new Ship("carrier", 4);

        // in the column 8, a ship of 4 size is off board
        assertThrows(UnvalidPositionException.class, () ->
                board.placeShip(0, 8, carrier, true));
    }

    @Test
    void noShipOverOtherShip() throws UnvalidPositionException {
        Ship submarine = new Ship("submarine", 3);
        Ship destructor = new Ship("destructor", 2);

        board.placeShip(5, 5, submarine, true); // ocupa (5,5), (5,6), (5,7)

        // desstructor cannot be in the cell, because the submarine is already there
        assertThrows(UnvalidPositionException.class, () ->
                board.placeShip(5, 6, destructor, true));
    }
    @Test
    void attackingAnEmptyCellReturnsWater(){
        String result = board.attackCell(0, 0);

        assertEquals(Board.WATER, result);
        assertTrue(board.isCellAlreadyAttacked(0, 0));
    }

    @Test
    void attackingAShipCellReturnsHitWhenShipStillAfloat() throws UnvalidPositionException {
        Ship destructor = new Ship("destructor", 2);
        board.placeShip(1, 1, destructor, true); // ocupa (1,1) y (1,2)

        String result = board.attackCell(1, 1);

        assertEquals(Board.HIT, result);
        assertTrue(destructor.isShipAfloat()); // still afloat, only 1 of 2 cells hit
    }

    @Test
    void attackingTheLastCellOfAShipReturnsSunked() throws UnvalidPositionException {
        Ship frigata = new Ship("frigata", 1); // a frigata only has 1 cell
        board.placeShip(2, 2, frigata, true);

        String result = board.attackCell(2, 2);

        assertEquals(Board.SUNKED, result);
        assertFalse(frigata.isShipAfloat());
    }

    @Test
    void sinkingAMultiCellShipMarksAllItsCellsAsSunked() throws UnvalidPositionException {
        Ship destructor = new Ship("destructor", 2);
        board.placeShip(3, 0, destructor, true); // ocupa (3,0) y (3,1)

        board.attackCell(3, 0); // first hit, ship still afloat
        board.attackCell(3, 1); // second hit, ship is now sunk

        assertEquals(Board.SUNKED, board.getBoard().get(3).get(0).getState());
        assertEquals(Board.SUNKED, board.getBoard().get(3).get(1).getState());
    }

    @Test
    void attackingAnAlreadyAttackedCellThrowsException(){
        board.attackCell(4, 4); // first shot: water

        assertThrows(AlreadyAttackedException.class, () ->
                board.attackCell(4, 4)); // second shot on the same cell
    }
    
}
