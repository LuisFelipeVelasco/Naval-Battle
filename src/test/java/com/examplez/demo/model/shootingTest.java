package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.AlreadyAttackedException;
import com.examplez.demo.model.exceptions.InvalidPositionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the shooting phase of the game:
 * attacking cells, sinking ships, tracking the last
 * attacked position, machine attacks and complete
 * gameplay simulations.
 */
class ShootingTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10);
    }

    // -------------------------------------------------
    // Board.attackCell
    // -------------------------------------------------

    @Test
    @DisplayName("Attacking an empty cell changes it to WATER")
    void attackingEmptyCellResultsInWater() {

        board.attackCell(3,3);

        assertEquals(Board.WATER, board.getStateOfCell(3,3));
    }

    @Test
    @DisplayName("Attacking a ship changes the cell to HIT")
    void attackingShipCellResultsInHit() throws InvalidPositionException {

        Ship ship = ShipFactory.createShip(1,"frigate");

        board.placeShip(4,4,ship,true);

        board.attackCell(4,4);

        assertEquals(Board.HIT, board.getStateOfCell(4,4));
    }

    @Test
    @DisplayName("Attacking the same cell twice throws an exception")
    void attackingSameCellTwiceThrowsException(){

        board.attackCell(1,1);

        assertThrows(AlreadyAttackedException.class,
                () -> board.attackCell(1,1));
    }

    // -------------------------------------------------
    // Ship sinking
    // -------------------------------------------------

    @Test
    @DisplayName("A ship is not sunk until every section is hit")
    void shipIsNotSunkUntilEveryCellIsHit() throws InvalidPositionException {

        Ship ship = ShipFactory.createShip(1,"destructor");

        board.placeShip(0,0,ship,true);

        board.attackCell(0,0);

        assertFalse(board.isShipSunken(0,0));

        board.attackCell(0,1);

        assertTrue(board.isShipSunken(0,1));
    }

    @Test
    @DisplayName("sinkShip marks every ship cell as SUNKEN")
    void sinkShipMarksWholeShipAsSunken() throws InvalidPositionException {

        Ship ship = ShipFactory.createShip(1,"submarine");

        board.placeShip(2,2,ship,false);

        board.attackCell(2,2);
        board.attackCell(3,2);
        board.attackCell(4,2);

        board.sinkShip(4,2);

        assertEquals(Board.SUNKEN, board.getStateOfCell(2,2));
        assertEquals(Board.SUNKEN, board.getStateOfCell(3,2));
        assertEquals(Board.SUNKEN, board.getStateOfCell(4,2));
    }

    @Test
    @DisplayName("Board contains no ships after every ship has sunk")
    void boardContainsNoShipsAfterFleetIsDestroyed() throws InvalidPositionException {

        Ship ship = ShipFactory.createShip(1,"frigate");

        board.placeShip(7,7,ship,true);

        assertTrue(board.isBoardWithShips());

        board.attackCell(7,7);
        board.sinkShip(7,7);

        assertFalse(board.isBoardWithShips());
    }

    // -------------------------------------------------
    // Last attacked cell
    // -------------------------------------------------

    @Test
    @DisplayName("The last attacked position is stored correctly")
    void lastAttackedPositionIsTrackedCorrectly() throws InvalidPositionException {

        Ship ship = ShipFactory.createShip(1,"frigate");

        board.placeShip(6,6,ship,true);

        board.attackCell(6,6);

        assertEquals(List.of(6,6),
                board.getPositionLastCellAttacked());

        assertEquals(Board.HIT,
                board.getStateLastCellAttacked());

        board.attackCell(0,0);

        assertEquals(List.of(0,0),
                board.getPositionLastCellAttacked());

        assertEquals(Board.WATER,
                board.getStateLastCellAttacked());
    }

    @Test
    @DisplayName("The last attacked cell becomes SUNKEN after sinking the ship")
    void lastAttackedStateBecomesSunken() throws InvalidPositionException {

        Ship ship = ShipFactory.createShip(1,"frigate");

        board.placeShip(8,8,ship,true);

        board.attackCell(8,8);

        board.sinkShip(8,8);

        assertEquals(Board.SUNKEN,
                board.getStateLastCellAttacked());
    }

    // -------------------------------------------------
    // Machine attacks
    // -------------------------------------------------

    @Test
    @DisplayName("Machine never attacks the same cell twice")
    void machineNeverRepeatsAnAttack() throws InvalidPositionException {

        Game game = new Game();

        game.startPlacement("Player");

        PlayerHuman human = game.getPlayerHuman();

        human.placeShip(0,0,
                ShipFactory.createShip(1,"carrier"),
                true);

        for(int i=0;i<100;i++){

            assertDoesNotThrow(game::processMachineAttack);
        }
    }

    @Test
    @DisplayName("Machine eventually sinks the player's fleet")
    void machineEventuallyDestroysFleet() throws InvalidPositionException {

        Game game = new Game();

        game.startPlacement("Player");

        PlayerHuman human = game.getPlayerHuman();

        human.placeShip(0,0,
                ShipFactory.createShip(1,"destructor"),
                true);

        human.placeShip(5,5,
                ShipFactory.createShip(2,"submarine"),
                false);

        Board board = human.getBoard();

        int attempts=0;

        while(board.isBoardWithShips() && attempts<100){

            game.processMachineAttack();

            attempts++;
        }

        assertFalse(board.isBoardWithShips());
    }

    // -------------------------------------------------
    // Machine placement
    // -------------------------------------------------

    @Test
    @DisplayName("Machine places its complete fleet without overlap")
    void machinePlacesFleetCorrectly(){

        Game game = new Game();

        game.startMatch();

        PlayerMachine machine = game.getPlayerMachine();

        assertNotNull(machine.getBoard());

        assertTrue(machine.getBoard().isBoardWithShips());

        int shipCells=0;

        for(List<Cell> row : machine.getBoard().getCells()){

            for(Cell cell : row){

                if(cell.getState().equals(Board.SHIP)){

                    shipCells++;
                }
            }
        }

        assertEquals(20,shipCells);
    }

    // -------------------------------------------------
    // Complete simulation
    // -------------------------------------------------

    @Test
    @DisplayName("Human sinks the entire machine fleet")
    void fullSimulationHumanWins(){

        Game game = new Game();

        game.startMatch();

        Board machine = game.getPlayerMachine().getBoard();

        for(int row=0;row<10 && machine.isBoardWithShips();row++){

            for(int column=0;column<10 && machine.isBoardWithShips();column++){

                if(machine.isCellAlreadyAttacked(row,column))
                    continue;

                machine.attackCell(row,column);

                if(machine.getStateOfCell(row,column).equals(Board.HIT)
                        && machine.isShipSunken(row,column)){

                    machine.sinkShip(row,column);
                }
            }
        }

        assertFalse(machine.isBoardWithShips());
    }
}