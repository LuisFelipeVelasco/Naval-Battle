package Model;

public class ShipFactory {
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
