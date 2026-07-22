package com.examplez.demo.model;

import java.io.Serializable;

/**Representation of a ship
 *
 * <p>Implements {@link Serializable} to allow deep serialization
 * as part of the GameState saving system.</p>
 *
 * @see java.io.Serializable
 * */
public class Ship implements Serializable {
    /**ID of the ship*/
    int id;
    /**number of hits received for the ship*/
    int numberOfHits;
    /**Type of ship*/
    String type;
    /**Size of ship*/
    int size;
    /**Constructor*/
    Ship(int id , String type){
        this.id=id;
        this.type=type;
        this.numberOfHits=0;
        switch (type) {
            case "submarine" -> this.size = 2;
            case "frigate" -> this.size = 1;
            case "aircraft carrier" -> this.size = 4;
            case "destroyer" -> this.size = 3;
        }
    }
    /**@return id of the ship*/
    int getId(){return id;}
    /**add 1 to numberOfHits*/
    void increaseHitOnShip(){numberOfHits+=1;}
}
