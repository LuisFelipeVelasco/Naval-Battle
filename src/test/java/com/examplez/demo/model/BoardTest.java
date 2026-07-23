package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.InvalidPositionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Board}, focused on ship placement logic
 * ({@code isValidPlacement} / {@code placeShip}).
 */
class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10);
    }

    // ---------------------------------------------------------------
    // Basic valid placements
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Places a ship horizontally in a valid position")
    void placesShipHorizontally() {
        Ship ship = ShipFactory.createShip(1, "destructor"); // size 2
        assertDoesNotThrow(() -> board.placeShip(0, 0, ship, true));

        assertEquals(Board.SHIP, board.getStateOfCell(0, 0));
        assertEquals(Board.SHIP, board.getStateOfCell(0, 1));
    }

    @Test
    @DisplayName("Places a ship vertically in a valid position")
    void placesShipVertically() {
        Ship ship = ShipFactory.createShip(1, "submarine"); // size 3
        assertDoesNotThrow(() -> board.placeShip(2, 2, ship, false));

        assertEquals(Board.SHIP, board.getStateOfCell(2, 2));
        assertEquals(Board.SHIP, board.getStateOfCell(3, 2));
        assertEquals(Board.SHIP, board.getStateOfCell(4, 2));
    }

    // ---------------------------------------------------------------
    // Off-board placements
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Rejects a horizontal ship placed outside the board")
    void rejectsHorizontalOffBoard() {
        Ship ship = ShipFactory.createShip(1, "carrier"); // size 4
        assertThrows(InvalidPositionException.class,
                () -> board.placeShip(0, 8, ship, true));
    }

    @Test
    @DisplayName("Rejects a vertical ship placed outside the board")
    void rejectsVerticalOffBoard() {
        Ship ship = ShipFactory.createShip(1, "carrier"); // size 4
        assertThrows(InvalidPositionException.class,
                () -> board.placeShip(8, 0, ship, false));
    }

    // ---------------------------------------------------------------
    // Overlap detection
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Rejects direct overlap between two ships")
    void rejectsDirectOverlap() throws InvalidPositionException {
        Ship first = ShipFactory.createShip(1, "submarine"); // size 3, row 0 cols 0-2
        board.placeShip(0, 0, first, true);

        Ship second = ShipFactory.createShip(2, "destructor"); // size 2, overlaps at (0,1)-(0,2)
        assertThrows(InvalidPositionException.class,
                () -> board.placeShip(0, 1, second, true));
    }

    // ---------------------------------------------------------------
    // Regression test: row/column swap bug
    // ---------------------------------------------------------------
    // Bug: isValidPlacement called isShipOnCell(currentColumn, currentRow)
    // instead of isShipOnCell(currentRow, currentColumn). Because the board
    // is square, this didn't throw an IndexOutOfBoundsException, but silently
    // checked the wrong (transposed) cell. This caused unrelated placements
    // to be rejected (false positives) or real overlaps to be accepted
    // (false negatives).

    @Test
    @DisplayName("Placing a ship in the top rows should not block an unrelated ship below")
    void placingShipInTopRowsDoesNotBlockUnrelatedShipBelow() throws InvalidPositionException {

        Ship topShip = ShipFactory.createShip(1, "submarine");
        board.placeShip(0, 0, topShip, false); // occupies (0,0) (1,0) (2,0)

        Ship otherShip = ShipFactory.createShip(2, "destructor");

        assertDoesNotThrow(() -> board.placeShip(5, 5, otherShip, true));

        assertEquals(Board.SHIP, board.getStateOfCell(5, 5));
        assertEquals(Board.SHIP, board.getStateOfCell(5, 6));
    }

    @Test
    @DisplayName("A transposed cell must not cause a false overlap")
    void transposedCellDoesNotCauseFalseOverlap() throws InvalidPositionException {

        Ship shipA = ShipFactory.createShip(1, "destructor");
        board.placeShip(1, 0, shipA, true); // occupies (1,0) and (1,1)

        // With the old bug, checking (0,1) incorrectly inspected (1,0),
        // causing a false overlap detection.
        Ship shipB = ShipFactory.createShip(2, "frigate");

        assertDoesNotThrow(() -> board.placeShip(0, 1, shipB, true));
        assertEquals(Board.SHIP, board.getStateOfCell(0, 1));
    }

    @Test
    @DisplayName("A real overlap near the top rows is still detected")
    void realOverlapNearTopRowsIsStillDetected() throws InvalidPositionException {

        Ship shipA = ShipFactory.createShip(1, "submarine");
        board.placeShip(0, 2, shipA, false); // occupies (0,2) (1,2) (2,2)

        Ship shipB = ShipFactory.createShip(2, "destructor");

        assertThrows(InvalidPositionException.class,
                () -> board.placeShip(1, 1, shipB, true));
    }

    // ---------------------------------------------------------------
    // isValidPlacement direct checks
    // ---------------------------------------------------------------

    @Test
    @DisplayName("isValidPlacement returns true for a valid free position")
    void isValidPlacementReturnsTrueForFreeSpot() {
        assertTrue(board.isValidPlacement(4, 4, 3, true));
    }

    @Test
    @DisplayName("isValidPlacement returns false when the ship goes off the board")
    void isValidPlacementReturnsFalseWhenOffBoard() {
        assertFalse(board.isValidPlacement(9, 9, 2, true));
        assertFalse(board.isValidPlacement(9, 9, 2, false));
    }

    @Test
    @DisplayName("isValidPlacement returns false when ships overlap")
    void isValidPlacementReturnsFalseOnRealOverlap() throws InvalidPositionException {

        Ship ship = ShipFactory.createShip(1, "submarine");
        board.placeShip(3, 3, ship, true); // occupies (3,3)-(3,5)

        assertFalse(board.isValidPlacement(3, 4, 2, true));
    }
}