package Model;

/**
 * Represents a single cell of a {@link Board}.
 * <p>
 * A cell keeps track of its own state (one of the constants defined in
 * {@link Board}, such as {@code BLANK}, {@code SHIP}, {@code WATER},
 * {@code HIT} or {@code SUNKED}) and holds a reference to the {@link Ship}
 * occupying it, if any. Several cells belonging to the same ship all point
 * to the exact same {@link Ship} instance, which allows registering hits
 * and checking whether the ship is still afloat from any of its cells.
 */
public class Cell {
    String state;
     private Ship ship;

    /**
     * Creates a cell with an explicit state and ship reference.
     *
     * @param state the initial state of the cell
     * @param ship  the ship occupying the cell, or {@code null} if empty
     */
   public Cell(String state, Ship ship){
        this.state=state;
        this.ship=ship;
    }

    /**
     * Creates an empty cell: state {@code "blank"} and no ship assigned.
     */
    public Cell() {
        this.state = "blank";
        this.ship = null;
    }

    /**
     * Updates the state of this cell.
     *
     * @param newState the new state to assign
     */
    public void setState(String newState){
        state=newState;
    }

    /**
     * Returns the current state of this cell.
     *
     * @return the cell's state
     */
    public String getState(){
        return state;
    }

    /**
     * Returns the ship occupying this cell.
     *
     * @return the ship instance, or {@code null} if the cell is empty
     */
   public Ship getShip(){
        return ship;
    }

    /**
     * Assigns a ship to this cell.
     *
     * @param ship the ship instance to associate with this cell
     */
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    /**
     * Returns the type of the ship occupying this cell.
     *
     * @return the ship's type, or {@code null} if the cell is empty
     */
    public String getTypeOfShip(){
        return (ship != null) ? ship.getType() : null;
    }

}
