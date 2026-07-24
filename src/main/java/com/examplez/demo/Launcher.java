package com.examplez.demo;

import javafx.application.Application;

/** Starts the modular JavaFX application from a conventional main method. */
public class Launcher {
    /**
     * Delegates launch to JavaFX.
     *
     * @param args startup arguments
     */
    public static void main(String[] args) {
        Application.launch(GameLauncher.class, args);
    }
}
