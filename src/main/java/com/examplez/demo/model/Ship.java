package com.examplez.demo.model;

import java.io.Serializable;
import java.io.Serial;

/**
 * Representation of a ship in the Battleship game.
 * <p>
 * A ship has a unique identifier, a size (number of cells it occupies),
 * and a type (e.g., carrier, submarine, destructor, frigate). Ships are
 * placed on a player's board and can be attacked and sunk during the game.
 *
 * <p>Implements {@link Serializable} to support game persistence
 * within GameState.</p>
 *
 * @see java.io.Serializable
 */
public class Ship implements Serializable {

    /** Serialization identifier for persisted ships. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for this ship.
     */
    int id;

    /**
     * The size (number of cells) of this ship.
     */
    int size;

    /**
     * The type of this ship (e.g., "carrier", "submarine", "destructor", "frigate").
     */
    String type;

    /**
     * Constructs a new ship with the given ID, size, and type.
     *
     * @param id   the unique identifier for this ship
     * @param size the number of cells this ship occupies
     * @param type the type/name of this ship
     */
    Ship(int id, int size, String type) {
        this.id = id;
        this.size = size;
        this.type = type;
    }

    /**
     * Returns the unique identifier of this ship.
     *
     * @return the ship's ID
     */
    int getId() {
        return id;
    }

    /**
     * Returns the size (number of cells) of this ship.
     *
     * @return the ship's size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the type of this ship (e.g., "carrier", "submarine").
     *
     * @return the ship's type string
     */
    public String getType() {
        return type;
    }
}
