package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for any participant of a Naval Battle match (human or machine).
 * <p>
 * A {@code Player} owns a fleet of {@link Ship} instances and a
 * {@link Board} on which that fleet is placed. Common fleet-related
 * behaviour (building the standard fleet, checking whether it is fully
 * placed, checking whether the player still has ships afloat) lives here,
 * while the way each concrete player creates and populates its board is
 * left to subclasses through {@link #createBoard()}.
 */
public abstract class Player {
    protected List<Ship> ships;
    protected Board board;

    /**
     * Builds the standard 10-ship fleet used by every player: 1 carrier,
     * 2 submarines, 3 destructors and 4 frigatas.
     * <p>
     * Note: this method returns the newly created fleet but does not
     * assign it to the {@link #ships} field by itself; callers are
     * expected to do so if needed.
     *
     * @return a new list containing the 10 ships of the standard fleet
     */
    public List<Ship> createShipLIst(){
        List<Ship> fleet = new ArrayList<>();
        fleet.add(ShipFactory.createShip("carrier"));
        for (int i = 0; i < 2; i++) fleet.add(ShipFactory.createShip("submarine"));
        for (int i = 0; i < 3; i++) fleet.add(ShipFactory.createShip("destructor"));
        for (int i = 0; i < 4; i++) fleet.add(ShipFactory.createShip("frigata"));
        return fleet;

    }

    /**
     * Returns this player's fleet.
     *
     * @return the list of ships belonging to this player
     */
    public List<Ship> getShips(){
        return ships;
    }

    /**
     * Checks whether this player still has at least one ship afloat.
     * Used to determine whether the match must end (a player with no
     * ships afloat has lost).
     *
     * @return {@code true} if at least one ship in the fleet is still afloat
     */
    public boolean isPlayerWithShips(){
        for (Ship ship : ships){
            if (ship.isShipAfloat()){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns this player's board.
     *
     * @return the board on which this player's fleet is placed
     */
    public Board getBoard(){
        return board;
    }

    /**
     * Checks whether every ship in the fleet has already been placed on
     * the board. Used during the placement phase to enable the
     * "start match" action only once the whole fleet is positioned.
     *
     * @return {@code true} if all ships are present on the board
     */
    public boolean isFleetFullyPlaced(){
        for (Ship ship : ships){
            if (!board.containsShip(ship)){
                return false;
            }
        }
        return true;
    }

    /**
     * Notifies this player that one of its ships has received an attack.
     * (Not yet implemented.)
     */
    public void receiveAttackOnShip(){

    }

    /**
     * Creates this player's board and fleet. Each subclass implements
     * this differently: a human player starts with an empty board ready
     * for manual placement, while the machine player places its fleet
     * randomly right away.
     */
    public abstract void createBoard();


}
