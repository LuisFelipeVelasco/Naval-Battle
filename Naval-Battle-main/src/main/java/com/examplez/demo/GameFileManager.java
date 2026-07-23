package com.examplez.demo;

import java.io.*;

/**
 * Utility class responsible for managing persistent game storage operations.
 * Handles reading, writing, checking, and deleting serializable game states
 * and plain text turn records.
 *
 * <p>This class cannot be instantiated.</p>
 */
public class GameFileManager {

    /** Path to the file storing the serialized {@link GameState} object. */
    private static final String SERIALIZED_FILE = "lastGamingSession.ser";

    /** Path to the text file storing the current turn number. */
    private static final String TURN_FILE = "lastTurnGame.txt";

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if instantiation is attempted.
     */
    private GameFileManager() {
        throw new UnsupportedOperationException("GameFileManager is a utility class and cannot be instantiated.");
    }

    /**
     * Saves the current state of the game and the current turn number to disk.
     *
     * @param state the {@link GameState} that will be serialized.
     * @param currentTurn the integer representing the active turn number.
     * @throws GameSaveException if an I/O error occurs while writing to the files.
     */
    public static void saveGame(GameState state, int currentTurn) {

        // Save the state of Game into a serializable file
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(SERIALIZED_FILE))) {
            output.writeObject(state);
        } catch (IOException e) {
            throw new GameSaveException("Error writing game state to file.", e);
        }

        // Save the current turn into a text file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TURN_FILE, false))) {
            writer.write(String.valueOf(currentTurn));
            writer.newLine();
        } catch (IOException e) {
            throw new GameSaveException("Error writing current turn to file.", e);
        }
    }

    /**
     * Loads and restores the last saved {@link GameState} object from disk.
     *
     * @return the deserialized {@link GameState} instance.
     * @throws GameLoadException if no saved game exists, if the class structure is incompatible,
     *                           or if an I/O error occurs during reading.
     */
    public static GameState loadGame() {
        if (!isAGameSaved()) {
            throw new GameLoadException("No previous saved game found.", null);
        }

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(SERIALIZED_FILE))) {
            return (GameState) input.readObject();
        } catch (ClassNotFoundException e) {
            throw new GameLoadException("Save file is incompatible or class was not found.", e);
        } catch (IOException e) {
            throw new GameLoadException("Could not read the saved game file.", e);
        }
    }

    /**
     * Reads and returns the turn number from the last saved gaming session.
     *
     * @return the saved turn number as an integer.
     * @throws GameLoadException if no saved record exists, or if the turn data cannot be read or parsed.
     */
    public static int loadTurn() {
        if (!isAGameSaved()) {
            throw new GameLoadException("No previous saved game record exists.", null);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TURN_FILE))) {
            String line = reader.readLine();
            return Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            throw new GameLoadException("Unable to read or parse the saved turn number.", e);
        }
    }

    /**
     * Deletes the serialized state file and the text turn record file from the system,
     * removing the saved game session.
     */
    public static void deleteGame() {
        File savedGame = new File(SERIALIZED_FILE);
        File lastCurrentTurn = new File(TURN_FILE);

        if (savedGame.exists()) {
            savedGame.delete();
        }
        if (lastCurrentTurn.exists()) {
            lastCurrentTurn.delete();
        }
    }

    /**
     * Checks whether both the serialized game state file and the turn text file exist.
     *
     * @return {@code true} if both save files exist on disk; {@code false} otherwise.
     */
    public static boolean isAGameSaved() {
        File savedGame = new File(SERIALIZED_FILE);
        File lastCurrentTurn = new File(TURN_FILE);
        return savedGame.exists() && lastCurrentTurn.exists();
    }
}