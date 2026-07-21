package Model;

/**
 * Factory responsible for creating {@link Ship} instances with the
 * correct size for each ship type, following the Naval Battle rules
 * (1 carrier of size 4, 2 submarines of size 3, 3 destructors of size 2
 * and 4 frigatas of size 1).
 * <p>
 * This class implements the creational Factory design pattern, so that
 * the rest of the code never has to remember or repeat the size that
 * corresponds to each ship type.
 */
public class ShipFactory {

    /**
     * Creates a new {@link Ship} of the given type, with the size that
     * corresponds to it according to the game rules.
     *
     * @param type the ship type: "carrier", "submarine", "destructor" or "frigata"
     * @return a new {@link Ship} instance with zero hits received
     * @throws IllegalArgumentException if the type is not one of the valid ship types
     */
    public static Ship createShip(String type) {
        switch (type) {
            case "carrier": return new Ship("carrier", 4);
            case "submarine":    return new Ship("submarine", 3);
            case "destructor":   return new Ship("destructor", 2);
            case "frigata":      return new Ship("frigata", 1);
            default: throw new IllegalArgumentException("unvalide type of ship : " + type);
        }
    }
}
