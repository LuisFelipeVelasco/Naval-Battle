package com.examplez.demo.model;

/**
 * Factory responsible for creating {@link Ship} instances with the
 * correct size for each ship type, following the Naval Battle rules.
 * <p>
 * This class implements the creational Factory design pattern, so that
 * the rest of the code never has to remember or repeat the size that
 * corresponds to each ship type.
 */
public final class ShipFactory {

    /** Prevents construction of this static factory. */
    private ShipFactory() {
        throw new UnsupportedOperationException("ShipFactory cannot be instantiated.");
    }

    /**
     * Creates a new {@link Ship} of the given type with the specified ID,
     * and with the size that corresponds to that type according to the
     * game rules.
     *
     * @param id   the unique identifier to assign to the new ship
     * @param type the ship type: "carrier", "submarine", "destructor", or "frigate"
     * @return a new {@link Ship} instance with the appropriate size for its type
     * @throws IllegalArgumentException if the given type is not one of the valid ship types
     */
    public static Ship createShip(Integer id, String type) {
        return switch (type) {
            case "carrier" -> new Ship(id, 4, type);
            case "submarine" -> new Ship(id, 3, type);
            case "destructor" -> new Ship(id, 2, type);
            case "frigate" -> new Ship(id, 1, type);
            default -> throw new IllegalArgumentException("Invalid type of ship : " + type);
        };
    }
}
