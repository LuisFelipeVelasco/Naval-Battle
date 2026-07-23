package com.examplez.demo.model.exceptions;

/**
 * Unchecked exception thrown when an attack is attempted on a cell that
 * has already been attacked before.
 * <p>
 * this is an unchecked exception: the UI is expected to prevent clicking on already-attacked
 * cells, so reaching this exception normally signals a programming
 * error rather than an expected user mistake.
 */
public class AlreadyAttackedException extends RuntimeException {
    public AlreadyAttackedException(int row , int column) {
        super("Cell already attacked at row " + row + ", column " + column);
    }
}
