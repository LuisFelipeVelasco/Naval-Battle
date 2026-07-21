package com.examplez.demo.model;

/**Representation of a ship*/
public class Ship {
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
