package com.examplez.demo;

/**
 * Unchecked custom exception thrown when an error occurs while attempting to load or restore a game session.
 *
 * @see java.lang.RuntimeException
 */
public class GameLoadException extends RuntimeException {

    /**
     * Constructs a new {@code GameLoadException} with the specified detail message and cause.
     *
     * @param message the detail error message explaining the failure.
     * @param cause   the underlying cause of the exception (or {@code null} if nonexistent/unknown).
     */
    public GameLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}