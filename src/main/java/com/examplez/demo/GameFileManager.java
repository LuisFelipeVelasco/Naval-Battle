package com.examplez.demo;

import java.io.*;

/**
 * Utility class responsible for managing persistent game storage operations.
 *
 * <p><b>Design Pattern - Facade (Structural):</b> This class acts as a Facade that provides
 * a simplified, unified interface to handle persistent storage. It hides the complexity of
 * managing two separate I/O subsystems:
 * <ul>
 *   <li><b>Binary Serialization Subsystem:</b> Handles complex state persistence using
 *       {@link ObjectOutputStream} and {@link ObjectInputStream} for {@link GameState}.</li>
 *   <li><b>Flat Text File Subsystem:</b> Handles lightweight game metrics (turn, sunk ships,
 *       player nickname) using {@link BufferedWriter} and {@link BufferedReader} formatted as CSV.</li>
 * </ul>
 * Controllers (clients) interact exclusively with this Facade, remaining entirely decoupled
 * from the underlying file structures, parsing rules, and I/O exception handling.</p>
 *
 * <p>This class cannot be instantiated.</p>
 */
public class GameFileManager {

    /** Path to the file storing the serialized {@link GameState} object. */
    private static final String SERIALIZED_FILE = "lastGamingSession.ser";

    /** Path to the text file storing game statistics (turn, ships sunk, nickname). */
    private static final String STATISTICS_FILE = "lastStatisticsGame.txt";

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if instantiation is attempted.
     */
    private GameFileManager() {
        throw new UnsupportedOperationException("GameFileManager is a utility class and cannot be instantiated.");
    }

    /**
     * Saves the current state of the game, the active turn,ships sunk count,
     * and human player's nickname to persistent storage.
     *
     * @param state                the {@link GameState} to be serialized.
     * @param currentTurn          the integer representing the active turn number.
     * @param shipsSunkPlayerHuman the number of ships sunk by the human player.
     * @param nicknamePlayerHuman  the nickname of the human player.
     * @throws GameSaveException   if an I/O error occurs while writing to disk.
     */
    public static void saveGame(GameState state, int currentTurn, int shipsSunkPlayerHuman, String nicknamePlayerHuman) {

        // Save the state of Game into a serializable file
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(SERIALIZED_FILE))) {
            output.writeObject(state);
        } catch (IOException e) {
            throw new GameSaveException("Error writing game state to file.", e);
        }

        // Save current statistics formatted as CSV (turn,shipsSunk,nickname)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATISTICS_FILE, false))) {
            String dataLine = String.format("%d,%d,%s", currentTurn, shipsSunkPlayerHuman, nicknamePlayerHuman);
            writer.write(dataLine);
            writer.newLine();
        } catch (IOException e) {
            throw new GameSaveException("Error writing game statistics to file.", e);
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
        String[] data = readStatisticsData();
        try {
            return Integer.parseInt(data[0].trim());
        } catch (NumberFormatException e) {
            throw new GameLoadException("Unable to parse the saved turn number.", e);
        }
    }

    /**
     * Reads and returns the human player's nickname from the saved statistics file.
     *
     * @return the saved player nickname as a {@String}.
     * @throws GameLoadException if no saved record exists or if data is corrupt.
     */
    public static String loadNicknamePlayerHuman() {
        String[] data = readStatisticsData();
        return data[2].trim();
    }

    /**
     * Reads and returns the number of ships sunk by the human player from the saved statistics file.
     *
     * @return the number of sunk ships as an integer.
     * @throws GameLoadException if no saved record exists or if the value cannot be parsed.
     */
    public static int loadNumberShipsSunkPlayerHuman() {
        String[] data = readStatisticsData();
        try {
            return Integer.parseInt(data[1].trim());
        } catch (NumberFormatException e) {
            throw new GameLoadException("Unable to parse the human player's sunk ships count.", e);
        }
    }

    /**
     * Private helper method that reads the statistics file and splits the CSV line into tokens.
     * Centralizes reading logic and exception handling.
     *
     * @return an array of {@String} containing the split values: [turn, shipsSunk, nickname].
     * @throws GameLoadException if the file is missing, empty, or improperly formatted.
     */
    private static String[] readStatisticsData() {
        if (!isAGameSaved()) {
            throw new GameLoadException("No previous saved game record exists.", null);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(STATISTICS_FILE))) {
            String line = reader.readLine();
            if (line == null || line.isBlank()) {
                throw new GameLoadException("The statistics file is empty or corrupted.", null);
            }

            String[] tokens = line.split(",", -1);
            if (tokens.length < 3) {
                throw new GameLoadException("Corrupted statistics file structure.", null);
            }

            return tokens;

        } catch (IOException e) {
            throw new GameLoadException("Unable to read the saved game statistics file.", e);
        }
    }

    /**
     * Deletes the serialized state file and the text statistics file from disk.
     */
    public static void deleteGame() {
        File savedGame = new File(SERIALIZED_FILE);
        File statisticsFile = new File(STATISTICS_FILE);

        if (savedGame.exists()) {
            savedGame.delete();
        }
        if (statisticsFile.exists()) {
            statisticsFile.delete();
        }
    }

    /**
     * Checks whether both the serialized game state file and the turn text file exist.
     *
     * @return {@code true} if both save files exist on disk; {@code false} otherwise.
     */
    public static boolean isAGameSaved() {
        File savedGame = new File(SERIALIZED_FILE);
        File statisticsFile = new File(STATISTICS_FILE);
        return savedGame.exists() && statisticsFile.exists();
    }
}