package com.examplez.demo;

import com.examplez.demo.model.Board;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GameFileManager} class.
 * Ensures proper functionality for saving, loading, checking, and deleting game sessions.
 */
class GameFileManagerTest {

    /**
     * Cleans up any existing save files before and after each test
     * to guarantee an isolated test environment.
     */
    @BeforeEach
    @AfterEach
    void cleanUp() {
        GameFileManager.deleteGame();
    }

    @Test
    @DisplayName("Should successfully save and restore a game state and turn number")
    void testSaveAndLoadGame() {
        Board playerBoard = new Board();
        Board machineBoard = new Board();
        GameState initialState = new GameState(playerBoard, machineBoard, new ArrayList<>(), new ArrayList<>());
        int expectedTurn = 5;

        GameFileManager.saveGame(initialState, expectedTurn);

        assertTrue(GameFileManager.isAGameSaved(), "The save files should exist on disk.");

        GameState loadedState = GameFileManager.loadGame();
        int loadedTurn = GameFileManager.loadTurn();

        assertNotNull(loadedState, "The restored GameState should not be null.");
        assertEquals(expectedTurn, loadedTurn, "The restored turn number must match the saved turn number.");
    }

    @Test
    @DisplayName("Should throw GameLoadException when trying to load without save files")
    void testLoadGameWithoutSaveFile() {
        assertThrows(GameLoadException.class, GameFileManager::loadGame,
                "Loading a game when no save file exists should throw a GameLoadException.");
    }

    @Test
    @DisplayName("Should successfully delete saved game files")
    void testDeleteGame() {
        Board dummyBoard = new Board();
        GameState dummyState = new GameState(dummyBoard, dummyBoard, new ArrayList<>(), new ArrayList<>());
        GameFileManager.saveGame(dummyState, 1);

        GameFileManager.deleteGame();

        assertFalse(GameFileManager.isAGameSaved(), "The save files should no longer exist after deletion.");
    }
}