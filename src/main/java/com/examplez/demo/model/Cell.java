package com.examplez.demo.model;

/**
 * Representation of a cell in the board of a player
 */
public class Cell {
    /** the state that has the cell (no attacked  , attacked  or sunken )*/
    String state;
    /** the typeOfShip that has the cell (submarine, destroyer, aircraft carrier, frigate ,no ship)*/
    String typeOfShip;
    /** the id that has the ship that is on the cell (-1 water , other - a ship)*/
    int idOfShip;

    /**
     * Constructor of the cell
     * @param state describe the state of the cell
     * @param typeOfShip describe the typeOfShip on the cell
     *
     */
    Cell(String state, String typeOfShip , int idOfShip ){
        this.state=state;
        this.typeOfShip=typeOfShip;
        this.idOfShip=idOfShip;
    }

    /**
     * set a newState to the attribute state
     * @param newState describe the new state of the cell
     * @throws IllegalArgumentException if newState is null or different of "no attacked" , "attacked"  or "sunken"
     */
    void setState(String newState){
        if(newState==null){
            throw new IllegalArgumentException(
                    "newState cannot be null" );
        }
        else if(!(newState.equals("no attacked") || newState.equals("attacked") || newState.equals("sunken"))){
            throw new IllegalArgumentException(
                "invalid state" + newState );
        }
        state=newState;
    }

    /**
     * @return the state of the cell
     */
    String getState(){return state;}

    /**
     * @return the typeOfShip of the cell
     */
    String getTypeOfShip(){return typeOfShip;}

    /**
     * @return the id of the ship on the cell
     */
    int getIdOfShip(){return idOfShip;}

}
