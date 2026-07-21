package Model;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected List<Ship> ships;
    protected Board board;

    public List<Ship> createShipLIst(){
        List<Ship> fleet = new ArrayList<>();
        fleet.add(ShipFactory.createShip("carrier"));
        for (int i = 0; i < 2; i++) fleet.add(ShipFactory.createShip("submarine"));
        for (int i = 0; i < 3; i++) fleet.add(ShipFactory.createShip("destructor"));
        for (int i = 0; i < 4; i++) fleet.add(ShipFactory.createShip("frigata"));
        return fleet;

    }
    public List<Ship> getShips(){
        return ships;
    }
    public boolean isPlayerWithShips(){
        for (Ship ship : ships){
            if (ship.isShipAfloat()){
                return true;
            }
        }
        return false;
    }

    public Board getBoard(){
        return board;
    }
    public boolean isFleetFullyPlaced(){
        for (Ship ship : ships){
            if (!board.containsShip(ship)){
                return false;
            }
        }
        return true;
    }
    public void receiveAttackOnShip(){

    }
    public abstract void createBoard();


}
