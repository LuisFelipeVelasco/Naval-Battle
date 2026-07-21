package Model;

public class Ship {
    private String type;
    private int size;
    private int numberOfHitsOnShip;
    public Ship(String type, int size){
        this.type = type;
        this.size = size;
        this.numberOfHitsOnShip = 0;
    }
    public String getType(){
        return type;
    }

    public int getSize(){
        return size;
    }

    public int getNumberOfHitsOnShip(){
        return numberOfHitsOnShip;
    }
    public void increaseHitOnShip(){
        numberOfHitsOnShip++;
    }
    public boolean isShipAfloat(){
        return numberOfHitsOnShip < size;
    }
}
