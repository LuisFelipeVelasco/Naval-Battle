package com.examplez.demo;

/**
 * Unchecked custom exception thrown when an error occurs while attempting to save game data to persistent storage.
 *
 * @see java.lang.RuntimeException
 */
public class GameSaveException extends RuntimeException {

    /**
     * Constructs a new {@code GameSaveException} with the specified detail message and cause.
     *
     * @param message the detail error message explaining the failure.
     * @param cause   the underlying cause of the exception (or {@code null} if nonexistent/unknown).
     */
    public GameSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}