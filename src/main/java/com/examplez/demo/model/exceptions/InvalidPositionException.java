package com.examplez.demo.model.exceptions;

/**
 * Checked exception thrown when a ship placement attempt is invalid,
 * either because it would go off the board or because it overlaps
 * another ship already placed on it.
 */
public class InvalidPositionException extends Exception {

    /**
     * Creates a new exception describing the invalid position that was attempted.
     *
     * @param row    the row index where the placement was attempted
     * @param column the column index where the placement was attempted
     */
    public InvalidPositionException(int row, int column) {
        super("Invalid Position on row " + row + ", column " + column);
    }
}