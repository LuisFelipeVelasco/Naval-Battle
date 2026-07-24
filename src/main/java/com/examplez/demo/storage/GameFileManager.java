package com.examplez.demo.storage;

import com.examplez.demo.storage.exceptions.GameLoadException;
import com.examplez.demo.storage.exceptions.GameSaveException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * Facade responsible for persistent match storage.
 *
 * <p>A single binary snapshot is written through a temporary file and then
 * moved into place. This prevents the model, nickname and turn information
 * from becoming desynchronized if the application closes during a write.</p>
 */
public final class GameFileManager {

    /** System property that optionally overrides the save directory. */
    public static final String SAVE_DIRECTORY_PROPERTY = "navalbattle.save.directory";

    /** Default directory name created inside the current user's home folder. */
    private static final String DEFAULT_DIRECTORY_NAME = ".naval-battle";

    /** Name of the active-session file. */
    private static final String SAVE_FILE_NAME = "active-session.sav";

    /** Name of the temporary file used during atomic writes. */
    private static final String TEMP_FILE_NAME = "active-session.tmp";

    /** Name of the legacy serialized file removed during cleanup. */
    private static final String LEGACY_SERIALIZED_FILE = "lastGamingSession.ser";

    /** Name of the legacy text file removed during cleanup. */
    private static final String LEGACY_STATISTICS_FILE = "lastStatisticsGame.txt";

    /**
     * Prevents construction of this utility class.
     */
    private GameFileManager() {
        throw new UnsupportedOperationException("GameFileManager cannot be instantiated.");
    }

    /**
     * Saves a complete game snapshot.
     *
     * @param state valid snapshot to persist
     * @throws GameSaveException if the target directory or file cannot be written
     */
    public static synchronized void saveGame(GameState state) {
        Objects.requireNonNull(state, "The game state cannot be null.");
        if (!state.isValid()) {
            throw new GameSaveException("The game state is incomplete and cannot be saved.", null);
        }

        Path directory = getSaveDirectory();
        Path temporaryFile = directory.resolve(TEMP_FILE_NAME);
        Path saveFile = directory.resolve(SAVE_FILE_NAME);

        try {
            Files.createDirectories(directory);
            try (ObjectOutputStream output =
                         new ObjectOutputStream(Files.newOutputStream(temporaryFile))) {
                output.writeObject(state);
                output.flush();
            }
            replaceSaveFile(temporaryFile, saveFile);
        } catch (IOException exception) {
            deleteQuietly(temporaryFile);
            throw new GameSaveException("The active match could not be saved.", exception);
        }
    }

    /**
     * Loads and validates the active game snapshot.
     *
     * @return valid persisted snapshot
     * @throws GameLoadException if no save exists or the file is invalid
     */
    public static synchronized GameState loadGame() {
        Path saveFile = getSaveFile();
        if (!Files.isRegularFile(saveFile)) {
            throw new GameLoadException("No saved match was found.", null);
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(saveFile))) {
            Object value = input.readObject();
            if (!(value instanceof GameState state) || !state.isValid()) {
                throw new GameLoadException("The saved match is invalid or incompatible.", null);
            }
            return state;
        } catch (GameLoadException exception) {
            throw exception;
        } catch (IOException | ClassNotFoundException | RuntimeException exception) {
            throw new GameLoadException("The saved match could not be loaded.", exception);
        }
    }

    /**
     * Checks whether a complete, readable and compatible saved match exists.
     *
     * @return {@code true} when {@link #loadGame()} can restore a match
     */
    public static synchronized boolean hasValidSave() {
        try {
            loadGame();
            return true;
        } catch (GameLoadException exception) {
            return false;
        }
    }

    /**
     * Backward-compatible alias for {@link #hasValidSave()}.
     *
     * @return {@code true} when a valid saved match exists
     */
    public static boolean isAGameSaved() {
        return hasValidSave();
    }

    /**
     * Deletes the active match and any files created by the previous storage format.
     *
     * @throws GameSaveException if an existing save file cannot be deleted
     */
    public static synchronized void deleteGame() {
        try {
            Files.deleteIfExists(getSaveFile());
            Files.deleteIfExists(getSaveDirectory().resolve(TEMP_FILE_NAME));
            Files.deleteIfExists(Path.of(LEGACY_SERIALIZED_FILE));
            Files.deleteIfExists(Path.of(LEGACY_STATISTICS_FILE));
        } catch (IOException exception) {
            throw new GameSaveException("The saved match could not be deleted.", exception);
        }
    }

    /**
     * Returns the directory used for saved matches.
     *
     * @return configured or default save directory
     */
    public static Path getSaveDirectory() {
        String configuredDirectory = System.getProperty(SAVE_DIRECTORY_PROPERTY);
        if (configuredDirectory != null && !configuredDirectory.isBlank()) {
            return Path.of(configuredDirectory).toAbsolutePath().normalize();
        }
        return Path.of(System.getProperty("user.home"), DEFAULT_DIRECTORY_NAME)
                .toAbsolutePath()
                .normalize();
    }

    /**
     * Returns the complete path of the active save file.
     *
     * @return active-session file path
     */
    public static Path getSaveFile() {
        return getSaveDirectory().resolve(SAVE_FILE_NAME);
    }

    /**
     * Moves a completed temporary snapshot into the active save location.
     *
     * @param temporaryFile fully written temporary file
     * @param saveFile      final active-session path
     * @throws IOException if neither an atomic nor regular replacement succeeds
     */
    private static void replaceSaveFile(Path temporaryFile, Path saveFile) throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    saveFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    temporaryFile,
                    saveFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    /**
     * Attempts to remove a temporary file without masking the original failure.
     *
     * @param file file to remove
     */
    private static void deleteQuietly(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
            // The original save exception remains the relevant failure.
        }
    }
}
