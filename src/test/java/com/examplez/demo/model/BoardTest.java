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
    @DisplayName("Coloca un barco horizontal en una posicion valida")
    void placesShipHorizontally() {
        Ship ship = ShipFactory.createShip(1, "destructor"); // size 2
        assertDoesNotThrow(() -> board.placeShip(0, 0, ship, true));

        assertEquals(Board.SHIP, board.getStateOfCell(0, 0));
        assertEquals(Board.SHIP, board.getStateOfCell(0, 1));
    }

    @Test
    @DisplayName("Coloca un barco vertical en una posicion valida")
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
    @DisplayName("Rechaza un barco horizontal que se sale del tablero")
    void rejectsHorizontalOffBoard() {
        Ship ship = ShipFactory.createShip(1, "carrier"); // size 4
        assertThrows(InvalidPositionException.class,
                () -> board.placeShip(0, 8, ship, true));
    }

    @Test
    @DisplayName("Rechaza un barco vertical que se sale del tablero")
    void rejectsVerticalOffBoard() {
        Ship ship = ShipFactory.createShip(1, "carrier"); // size 4
        assertThrows(InvalidPositionException.class,
                () -> board.placeShip(8, 0, ship, false));
    }

    // ---------------------------------------------------------------
    // Overlap detection
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Rechaza superposicion directa entre dos barcos")
    void rejectsDirectOverlap() throws InvalidPositionException {
        Ship first = ShipFactory.createShip(1, "submarine"); // size 3, row 0 cols 0-2
        board.placeShip(0, 0, first, true);

        Ship second = ShipFactory.createShip(2, "destructor"); // size 2, would overlap at (0,1)-(0,2)
        assertThrows(InvalidPositionException.class,
                () -> board.placeShip(0, 1, second, true));
    }

    // ---------------------------------------------------------------
    // Regression test: row/column swap bug
    // ---------------------------------------------------------------
    // Bug: isValidPlacement called isShipOnCell(currentColumn, currentRow)
    // instead of isShipOnCell(currentRow, currentColumn). Because the board
    // is square, this didn't throw an IndexOutOfBounds, but silently checked
    // the wrong (transposed) cell. This meant that placing a ship near rows
    // 0-2 could make later, completely unrelated placements be rejected
    // (false positive) or, in other cases, allow real overlaps to slip
    // through (false negative), because the check landed on a mirrored cell
    // across the diagonal instead of the real target cell.

    @Test
    @DisplayName("Colocar un barco en filas 0-2 no debe bloquear un barco no relacionado mas abajo")
    void placingShipInTopRowsDoesNotBlockUnrelatedShipBelow() throws InvalidPositionException {
        // Ship near the top-left corner, rows 0-2 (vertical, column 0)
        Ship topShip = ShipFactory.createShip(1, "submarine"); // size 3
        board.placeShip(0, 0, topShip, false); // occupies (0,0) (1,0) (2,0)

        // Completely unrelated ship, far from the first one.
        Ship otherShip = ShipFactory.createShip(2, "destructor"); // size 2
        assertDoesNotThrow(() -> board.placeShip(5, 5, otherShip, true));

        assertEquals(Board.SHIP, board.getStateOfCell(5, 5));
        assertEquals(Board.SHIP, board.getStateOfCell(5, 6));
    }

    @Test
    @DisplayName("La transposicion de una celda no debe generar falso solapamiento")
    void transposedCellDoesNotCauseFalseOverlap() throws InvalidPositionException {
        // Place a ship at (1, 0)-(1,1) horizontal (size 2).
        Ship shipA = ShipFactory.createShip(1, "destructor");
        board.placeShip(1, 0, shipA, true); // occupies (1,0) and (1,1)

        // With the old bug, checking cell (0,1) [row=0, col=1] would have
        // consulted board.get(1).get(0) (transposed), which IS occupied by
        // shipA, causing a false "already a ship" rejection here even
        // though (0,1) is actually free.
        Ship shipB = ShipFactory.createShip(2, "frigate"); // size 1
        assertDoesNotThrow(() -> board.placeShip(0, 1, shipB, true));
        assertEquals(Board.SHIP, board.getStateOfCell(0, 1));
    }

    @Test
    @DisplayName("Un solapamiento real en la franja de filas 0-2 sigue siendo detectado")
    void realOverlapNearTopRowsIsStillDetected() throws InvalidPositionException {
        Ship shipA = ShipFactory.createShip(1, "submarine"); // size 3, vertical at col 2
        board.placeShip(0, 2, shipA, false); // occupies (0,2) (1,2) (2,2)

        Ship shipB = ShipFactory.createShip(2, "destructor"); // size 2, horizontal, would hit (1,1)-(1,2)
        assertThrows(InvalidPositionException.class,
                () -> board.placeShip(1, 1, shipB, true));
    }

    // ---------------------------------------------------------------
    // isValidPlacement direct checks (package-private, same package)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("isValidPlacement devuelve true para una posicion libre valida")
    void isValidPlacementReturnsTrueForFreeSpot() {
        assertTrue(board.isValidPlacement(4, 4, 3, true));
    }

    @Test
    @DisplayName("isValidPlacement devuelve false cuando se sale del tablero")
    void isValidPlacementReturnsFalseWhenOffBoard() {
        assertFalse(board.isValidPlacement(9, 9, 2, true));
        assertFalse(board.isValidPlacement(9, 9, 2, false));
    }

    @Test
    @DisplayName("isValidPlacement devuelve false cuando hay solapamiento real")
    void isValidPlacementReturnsFalseOnRealOverlap() throws InvalidPositionException {
        Ship ship = ShipFactory.createShip(1, "submarine");
        board.placeShip(3, 3, ship, true); // occupies (3,3)-(3,5)

        assertFalse(board.isValidPlacement(3, 4, 2, true));
    }
}