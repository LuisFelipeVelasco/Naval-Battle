package Model;

/**
 * Unchecked exception thrown when an attack is attempted on a cell that
 * has already been attacked before.
 * <p>
 * Unlike {@link UnvalidPositionException}, this is an unchecked
 * exception: the UI is expected to prevent clicking on already-attacked
 * cells, so reaching this exception normally signals a programming
 * error rather than an expected user mistake.
 */
public class AlreadyAttackedException extends RuntimeException {

    /**
     * Creates a new exception describing the cell that was attacked twice.
     *
     * @param row    the row index of the cell
     * @param column the column index of the cell
     */
    public AlreadyAttackedException(int row, int column) {
        super("Cell already attacked at row " + row + ", column " + column);
    }
}
