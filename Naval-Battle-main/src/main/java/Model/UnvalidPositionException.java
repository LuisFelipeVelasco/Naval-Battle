package Model;

/**
 * Checked exception thrown when a ship placement attempt is invalid,
 * either because it would go off the board or because it overlaps
 * another ship already placed on it.
 * <p>
 * This is a custom checked exception: callers of
 * {@link Board#placeShip(int, int, Ship, boolean)} must either catch it
 * or declare it in their own {@code throws} clause.
 */
public class UnvalidPositionException extends Exception {

    /**
     * Creates a new exception describing the invalid position that was attempted.
     *
     * @param row    the row index where the placement was attempted
     * @param column the column index where the placement was attempted
     */
    public UnvalidPositionException(int row, int column) {
        super("Unvalid Position on row " + row + ", column " + column);
    }
}
