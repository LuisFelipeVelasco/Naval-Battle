package com.examplez.demo.model;

/**
 * Representation of a cell in the board of a player
 */
public class Cell {
    /** the state that has the cell (no attacked  , attacked  or sunken )*/
    String state;
    /** ship on the cell */
    Ship ship;

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
     * Returns the id's ship occupying this cell.
     *
     * @return the id's ship, or {@code null} if the cell is empty
     */
    public int getIdOfShip(){
        return (ship != null) ? ship.getId() : -1;
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
     * Returns the size of the ship occupying this cell.
     *
     * @return the sizes' type, or {@code null} if the cell is empty
     */
    public int getSizeOfShip(){
        return (ship != null) ? ship.getSize() : null;
    }
}
