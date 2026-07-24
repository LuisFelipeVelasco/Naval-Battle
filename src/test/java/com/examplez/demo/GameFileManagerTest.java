package com.examplez.demo;

import com.examplez.demo.model.Board;
import com.examplez.demo.model.Game;
import com.examplez.demo.model.Ship;
import com.examplez.demo.model.ShipFactory;
import com.examplez.demo.storage.GameFileManager;
import com.examplez.demo.storage.GameState;
import com.examplez.demo.storage.exceptions.GameLoadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Tests exact active-session persistence through {@link GameFileManager}. */
class GameFileManagerTest {

    /** Temporary directory used to isolate each save-file test. */
    private Path temporaryDirectory;

    /** Creates a private save location before each test. */
    @BeforeEach
    void setUp() throws Exception {
        temporaryDirectory = Files.createTempDirectory("naval-battle-test-");
        System.setProperty(GameFileManager.SAVE_DIRECTORY_PROPERTY, temporaryDirectory.toString());
    }

    /** Removes persisted test state after each test. */
    @AfterEach
    void tearDown() throws Exception {
        GameFileManager.deleteGame();
        System.clearProperty(GameFileManager.SAVE_DIRECTORY_PROPERTY);
        Files.deleteIfExists(temporaryDirectory);
    }

    /** Verifies nickname, turn, ships and attacked states survive serialization. */
    @Test
    void savesAndRestoresTheCompleteActiveMatch() throws Exception {
        Game game = createActiveGame();
        Board enemyBoard = game.getPlayerMachine().getBoard();
        int[] waterTarget = findWaterTarget(enemyBoard);
        enemyBoard.attackCell(waterTarget[0], waterTarget[1]);
        GameFileManager.saveGame(new GameState(game, false, "Verifier"));

        GameState restored = GameFileManager.loadGame();

        assertTrue(GameFileManager.hasValidSave());
        assertFalse(restored.isPlayerTurn());
        assertEquals("Verifier", restored.getUserType());
        assertEquals("Captain Java", restored.getNickname());
        assertEquals(Board.WATER, restored.getGame().getPlayerMachine().getBoard().getStateOfCell(waterTarget[0], waterTarget[1]));
        assertEquals(game.getPlayerHuman().getBoard().getCells().size(), restored.getGame().getPlayerHuman().getBoard().getCells().size());
    }

    /** Verifies a missing snapshot cannot be loaded. */
    @Test
    void rejectsLoadingWhenNoSnapshotExists() {
        assertThrows(GameLoadException.class, GameFileManager::loadGame);
        assertFalse(GameFileManager.hasValidSave());
    }

    /** Builds a complete legal game used by persistence tests. */
    private Game createActiveGame() throws Exception {
        Game game = new Game();
        game.startPlacement("Captain Java");
        Ship frigate = game.getPlayerHuman().getShips().get(9);
        game.getPlayerHuman().placeShipOnBoard(0, 0, frigate, true);
        game.startMatch();
        return game;
    }

    /**
     * Finds an empty target guaranteed to become a miss.
     *
     * @param board board to inspect
     * @return row and column of a water target
     */
    private int[] findWaterTarget(Board board) {
        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                if (board.getCells().get(row).get(column).getShip() == null) {
                    return new int[]{row, column};
                }
            }
        }
        throw new IllegalStateException("The board has no water target.");
    }
}
