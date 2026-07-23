package com.examplez.demo.model;

import java.io.Serializable;

/**Representation of a ship
 *
 * <p>Implements {@link Serializable} to support game persistence
 * within GameState.</p>
 *
 * @see java.io.Serializable
 * */
public class Ship implements Serializable{
    /**ID of the ship*/
    int id;
    /**Size of ship*/
    int size;
    /**Type of ship*/
    String type;
    /**Constructor*/
    Ship(int id , int size,String type){
        this.id=id;
        this.size=size;
        this.type=type;
    }
    /**@return id of the ship*/
    int getId(){return id;}
    /**@return size of the ship*/
    public int getSize(){return size;}
    /**@return type of the ship*/
    public String getType(){return type;}

}
