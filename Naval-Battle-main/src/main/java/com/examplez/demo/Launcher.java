package com.examplez.demo;

import javafx.application.Application;

/**
 * Plain Java entry point of the application.
 * <p>
 * This indirection (a class without an {@code Application} subclass as
 * the main class) is a common workaround for running JavaFX applications
 * packaged with Maven, avoiding module-path issues that can occur when
 * launching an {@code Application} subclass directly.
 */
public class Launcher {

    /**
     * Launches the JavaFX application, delegating to {@link GameLauncher}.
     *
     * @param args command-line arguments, forwarded to the JavaFX runtime
     */
    public static void main(String[] args) {
        Application.launch(GameLauncher.class, args);
    }
}
