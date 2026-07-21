package com.examplez.demo.model;

import java.util.List;

/**Represents any player on the game*/
public class Player {
    /**Board of the player on the game*/
    Board board;
    /**List with the ships of the player*/
    List<Ship> ships;
    /**@return board of the player*/
    Board getBoard(){return board;}
    /**
     * attack a ship by its id
     * @param idOfShip id of the ship that is gonna receive the attack
     */
    void  receiveAttackOnShip(int idOfShip){
        for(Ship ship: ships){
            if (ship.getId()==idOfShip){
                ship.increaseHitOnShip();
            }
        }
    }
}
