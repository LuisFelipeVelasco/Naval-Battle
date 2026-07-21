package Model;

/**
 * Represents a single ship of the fleet (e.g. carrier, submarine,
 * destructor or frigata).
 * <p>
 * A ship keeps track of its type, its fixed size (how many cells it
 * occupies on the board) and how many hits it has received so far.
 * Instances are typically created through {@link ShipFactory} rather
 * than directly, so that the correct size is always assigned for each type.
 */
public class Ship {
    private String type;
    private int size;
    private int numberOfHitsOnShip;

    /**
     * Creates a new ship with zero hits received.
     *
     * @param type the ship's type (e.g. "carrier", "submarine")
     * @param size the number of cells this ship occupies on the board
     */
    public Ship(String type, int size){
        this.type = type;
        this.size = size;
        this.numberOfHitsOnShip = 0;
    }

    /**
     * Returns the type of this ship.
     *
     * @return the ship's type
     */
    public String getType(){
        return type;
    }

    /**
     * Returns how many cells this ship occupies on the board.
     *
     * @return the ship's size
     */
    public int getSize(){
        return size;
    }

    /**
     * Returns how many hits this ship has received so far.
     *
     * @return the current number of hits
     */
    public int getNumberOfHitsOnShip(){
        return numberOfHitsOnShip;
    }

    /**
     * Registers a new hit on this ship. Called every time one of its
     * cells is attacked.
     */
    public void increaseHitOnShip(){
        numberOfHitsOnShip++;
    }

    /**
     * Indicates whether this ship is still afloat, i.e. whether it has
     * received fewer hits than its total size.
     *
     * @return {@code true} if the ship has not been fully sunk yet
     */
    public boolean isShipAfloat(){
        return numberOfHitsOnShip < size;
    }
}
