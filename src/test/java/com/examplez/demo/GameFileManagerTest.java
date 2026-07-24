package com.examplez.demo;

import com.examplez.demo.model.Board;
import com.examplez.demo.storage.GameFileManager;
import com.examplez.demo.storage.GameState;
import com.examplez.demo.storage.exceptions.GameLoadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GameFileManager} class.
 * Ensures proper functionality for saving, loading, checking, and deleting game sessions
 * along with game statistics (turn, sunk ships count, and player nickname).
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
    @DisplayName("Should successfully save and restore game state, turn, ships sunk, and nickname")
    void testSaveAndLoadAllData() {
        Board playerBoard = new Board(10);
        Board machineBoard = new Board(10);
        GameState initialState = new GameState(playerBoard, machineBoard);

        int expectedTurn = 5;
        int expectedShipsSunk = 3;
        String expectedNickname = "CaptainJava";

        GameFileManager.saveGame(initialState, expectedTurn, expectedShipsSunk, expectedNickname);

        assertTrue(GameFileManager.isAGameSaved(), "The save files should exist on disk after saving.");

        GameState loadedState = GameFileManager.loadGame();
        int loadedTurn = GameFileManager.loadTurn();
        int loadedShipsSunk = GameFileManager.loadNumberShipsSunkPlayerHuman();
        String loadedNickname = GameFileManager.loadNicknamePlayerHuman();

        assertNotNull(loadedState, "The restored GameState should not be null.");
        assertEquals(expectedTurn, loadedTurn, "The restored turn number must match the saved value.");
        assertEquals(expectedShipsSunk, loadedShipsSunk, "The restored sunk ships count must match the saved value.");
        assertEquals(expectedNickname, loadedNickname, "The restored nickname must match the saved value.");
    }

    @Test
    @DisplayName("Should throw GameLoadException when trying to load data without save files")
    void testLoadWithoutSaveFiles() {
        assertAll("Ensure all load methods throw GameLoadException when save files are missing",
                () -> assertThrows(GameLoadException.class, GameFileManager::loadGame,
                        "Loading GameState without a save file should throw GameLoadException."),
                () -> assertThrows(GameLoadException.class, GameFileManager::loadTurn,
                        "Loading turn without a save file should throw GameLoadException."),
                () -> assertThrows(GameLoadException.class, GameFileManager::loadNumberShipsSunkPlayerHuman,
                        "Loading sunk ships count without a save file should throw GameLoadException."),
                () -> assertThrows(GameLoadException.class, GameFileManager::loadNicknamePlayerHuman,
                        "Loading nickname without a save file should throw GameLoadException.")
        );
    }

    @Test
    @DisplayName("Should successfully delete saved game files and update status")
    void testDeleteGame() {
        Board playerBoard = new Board(10);
        Board machineBoard = new Board(10);
        GameState dummyState = new GameState(playerBoard, machineBoard);

        GameFileManager.saveGame(dummyState, 1, 0, "TestPlayer");
        assertTrue(GameFileManager.isAGameSaved(), "Save files should exist prior to deletion.");

        GameFileManager.deleteGame();

        assertFalse(GameFileManager.isAGameSaved(), "The save files should no longer exist after calling deleteGame().");
    }
}