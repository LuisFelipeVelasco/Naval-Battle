package com.examplez.demo.storage;

import com.examplez.demo.model.Game;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Serializable snapshot containing every value required to resume an active match.
 *
 * <p>The complete {@link Game} model is stored so both boards, ships, attacked
 * cells, sunk counters and the player's nickname are restored exactly as they
 * were. The snapshot also records whose turn it is and the selected game mode.</p>
 */
public final class GameState implements Serializable {

    /** Serialization identifier used to detect incompatible save formats. */
    @Serial
    private static final long serialVersionUID = 2L;

    /** Current save-format version. */
    public static final int CURRENT_FORMAT_VERSION = 1;

    /** Version of the format used to create this snapshot. */
    private final int formatVersion;

    /** Complete game model at the moment the snapshot was created. */
    private final Game game;

    /** Indicates whether the human player owns the next turn. */
    private final boolean playerTurn;

    /** Game mode selected when the match was created. */
    private final String userType;

    /** Player nickname copied for validation and menu presentation. */
    private final String nickname;

    /** UTC timestamp indicating when the snapshot was created. */
    private final Instant savedAt;

    /**
     * Creates a complete snapshot of an active match.
     *
     * @param game       complete game model to persist
     * @param playerTurn {@code true} when the human player owns the next turn
     * @param userType   selected game mode
     */
    public GameState(Game game, boolean playerTurn, String userType) {
        this.game = Objects.requireNonNull(game, "The game model cannot be null.");
        this.playerTurn = playerTurn;
        this.userType = normalizeUserType(userType);
        this.nickname = validateNickname(game);
        this.savedAt = Instant.now();
        this.formatVersion = CURRENT_FORMAT_VERSION;
    }

    /**
     * Returns the complete persisted game model.
     *
     * @return restored game model
     */
    public Game getGame() {
        return game;
    }

    /**
     * Indicates whether the human player owns the next turn.
     *
     * @return {@code true} for the human turn; {@code false} for the machine turn
     */
    public boolean isPlayerTurn() {
        return playerTurn;
    }

    /**
     * Returns the selected game mode.
     *
     * @return normalized game mode
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Returns the persisted player nickname.
     *
     * @return nonblank player nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the instant when this snapshot was created.
     *
     * @return UTC save timestamp
     */
    public Instant getSavedAt() {
        return savedAt;
    }

    /**
     * Returns the save-format version.
     *
     * @return format version number
     */
    public int getFormatVersion() {
        return formatVersion;
    }

    /**
     * Checks whether the snapshot contains a supported and internally valid match.
     *
     * @return {@code true} when the snapshot can be safely resumed
     */
    public boolean isValid() {
        return formatVersion == CURRENT_FORMAT_VERSION
                && game != null
                && game.hasActiveMatch()
                && nickname != null
                && !nickname.isBlank()
                && nickname.equals(game.getPlayerHuman().getPlayerName());
    }

    /**
     * Reads and validates the nickname from the supplied game model.
     *
     * @param game game model containing the human player
     * @return validated nickname
     * @throws IllegalArgumentException if the game does not contain a nickname
     */
    private static String validateNickname(Game game) {
        if (game.getPlayerHuman() == null) {
            throw new IllegalArgumentException("The game does not contain a human player.");
        }

        String playerName = game.getPlayerHuman().getPlayerName();
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("The player nickname cannot be blank.");
        }
        return playerName.trim();
    }

    /**
     * Normalizes a potentially missing game mode.
     *
     * @param userType requested game mode
     * @return normalized game mode
     */
    private static String normalizeUserType(String userType) {
        return userType == null || userType.isBlank() ? "Player" : userType.trim();
    }
}
