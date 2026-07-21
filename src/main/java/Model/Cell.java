package Model;

public class Cell {
    String state;
     private Ship ship;
   public Cell(String state, Ship ship){
        this.state=state;
        this.ship=ship;
    }
    public Cell() {
        this.state = "blank";
        this.ship = null;
    }
    public void setState(String newState){
        state=newState;
    }
    public String getState(){
        return state;
    }
   public Ship getShip(){
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }
    public String getTypeOfShip(){
        return (ship != null) ? ship.getType() : null;
    }

}
