package com.examplez.demo.model;

import com.examplez.demo.model.exceptions.AlreadyAttackedException;
import com.examplez.demo.model.exceptions.InvalidPositionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the shooting phase of the game: attacking cells, sinking
 * ships, tracking the last cell attacked, and the machine's random
 * attack flow ({@link Game#processMachineAttack()}).
 */
class ShootingTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10);
    }

    // ---------------------------------------------------------------
    // Board.attackCell
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Atacar una celda sin barco la deja en WATER")
    void attackingEmptyCellResultsInWater() {
        board.attackCell(3, 3);
        assertEquals(Board.WATER, board.getStateOfCell(3, 3));
    }

    @Test
    @DisplayName("Atacar una celda con barco la deja en HIT")
    void attackingShipCellResultsInHit() throws InvalidPositionException {
        Ship ship = ShipFactory.createShip(1, "frigate"); // size 1
        board.placeShip(4, 4, ship, true);

        board.attackCell(4, 4);
        assertEquals(Board.HIT, board.getStateOfCell(4, 4));
    }

    @Test
    @DisplayName("Atacar dos veces la misma celda lanza AlreadyAttackedException")
    void attackingSameCellTwiceThrows() {
        board.attackCell(1, 1);
        assertThrows(AlreadyAttackedException.class, () -> board.attackCell(1, 1));
    }

    // ---------------------------------------------------------------
    // Board.isShipSunken / sinkShip
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Un barco no esta hundido hasta que todas sus celdas reciben HIT")
    void shipIsNotSunkUntilAllCellsHit() throws InvalidPositionException {
        Ship ship = ShipFactory.createShip(1, "destructor"); // size 2
        board.placeShip(0, 0, ship, true); // occupies (0,0) y (0,1)

        board.attackCell(0, 0);
        assertFalse(board.isShipSunken(0, 0));

        board.attackCell(0, 1);
        assertTrue(board.isShipSunken(0, 1));
    }

    @Test
    @DisplayName("sinkShip marca todas las celdas del barco como SUNKEN")
    void sinkShipMarksAllCellsAsSunken() throws InvalidPositionException {
        Ship ship = ShipFactory.createShip(1, "submarine"); // size 3
        board.placeShip(2, 2, ship, false); // occupies (2,2) (3,2) (4,2)

        board.attackCell(2, 2);
        board.attackCell(3, 2);
        board.attackCell(4, 2);
        assertTrue(board.isShipSunken(4, 2));

        board.sinkShip(4, 2);
        assertEquals(Board.SUNKEN, board.getStateOfCell(2, 2));
        assertEquals(Board.SUNKEN, board.getStateOfCell(3, 2));
        assertEquals(Board.SUNKEN, board.getStateOfCell(4, 2));
    }

    @Test
    @DisplayName("isBoardWithShips es false una vez que todos los barcos estan hundidos")
    void isBoardWithShipsBecomesFalseAfterAllSunk() throws InvalidPositionException {
        Ship ship = ShipFactory.createShip(1, "frigate"); // size 1
        board.placeShip(7, 7, ship, true);

        assertTrue(board.isBoardWithShips());

        board.attackCell(7, 7);
        assertTrue(board.isShipSunken(7, 7));
        board.sinkShip(7, 7);

        assertFalse(board.isBoardWithShips());
    }

    // ---------------------------------------------------------------
    // Board.getPositionLastCellAttacked / getStateLastCellAttacked
    // ---------------------------------------------------------------

    @Test
    @DisplayName("getPositionLastCellAttacked y getStateLastCellAttacked reflejan el ultimo ataque")
    void lastCellAttackedIsTrackedCorrectly() throws InvalidPositionException {
        Ship ship = ShipFactory.createShip(1, "frigate");
        board.placeShip(6, 6, ship, true);

        board.attackCell(6, 6);

        assertEquals(List.of(6, 6), board.getPositionLastCellAttacked());
        assertEquals(Board.HIT, board.getStateLastCellAttacked());

        board.attackCell(0, 0); // agua, en una celda distinta
        assertEquals(List.of(0, 0), board.getPositionLastCellAttacked());
        assertEquals(Board.WATER, board.getStateLastCellAttacked());
    }

    @Test
    @DisplayName("getStateLastCellAttacked refleja SUNKEN despues de hundir el barco")
    void lastCellAttackedReflectsSunkenAfterSinking() throws InvalidPositionException {
        Ship ship = ShipFactory.createShip(1, "frigate"); // size 1
        board.placeShip(8, 8, ship, true);

        board.attackCell(8, 8);
        if (board.isShipSunken(8, 8)) {
            board.sinkShip(8, 8);
        }

        assertEquals(Board.SUNKEN, board.getStateLastCellAttacked());
    }

    // ---------------------------------------------------------------
    // Game.processMachineAttack (attacks the human player's board)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("processMachineAttack nunca ataca una celda repetida ni lanza excepcion")
    void machineNeverAttacksSameCellTwice() throws InvalidPositionException {
        Game game = new Game();
        game.startPlacement(); // crea al humano y su tablero vacio

        PlayerHuman human = game.getPlayerHuman();
        Ship ship = ShipFactory.createShip(1, "carrier"); // size 4
        human.placeShip(0, 0, ship, true);

        Board humanBoard = human.getBoard();

        // Ataca las 100 celdas del tablero; nunca deberia repetir ni lanzar excepcion.
        for (int i = 0; i < 100; i++) {
            assertDoesNotThrow(game::processMachineAttack);
        }
    }

    @Test
    @DisplayName("processMachineAttack termina hundiendo toda la flota colocada")
    void machineAttackEventuallySinksFleet() throws InvalidPositionException {
        Game game = new Game();
        game.startPlacement();

        PlayerHuman human = game.getPlayerHuman();
        human.placeShip(0, 0, ShipFactory.createShip(1, "destructor"), true);  // (0,0)-(0,1)
        human.placeShip(5, 5, ShipFactory.createShip(2, "submarine"), false); // (5,5)-(7,5)

        Board humanBoard = human.getBoard();
        assertTrue(humanBoard.isBoardWithShips());

        int maxAttempts = 100; // el tablero tiene 100 celdas, como maximo
        int attempts = 0;
        while (humanBoard.isBoardWithShips() && attempts < maxAttempts) {
            game.processMachineAttack();
            attempts++;
        }

        assertFalse(humanBoard.isBoardWithShips(),
                "Toda la flota deberia quedar hundida tras atacar todas las celdas necesarias");
        assertTrue(attempts < maxAttempts,
                "No deberian hacer falta las 100 celdas para hundir 6 celdas de barco");
    }

    // ---------------------------------------------------------------
    // PlayerMachine random placement (used by Game.startMatch)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("La flota de la maquina se coloca completa y sin solapamientos al iniciar el match")
    void machinePlacesFullFleetWithoutOverlap() {
        Game game = new Game();
        game.startMatch(); // crea a la maquina y coloca su flota aleatoriamente

        PlayerMachine machine = game.getPlayerMachine();
        assertNotNull(machine.getBoard());

        // El tablero de la maquina debe tener barcos (SHIP) despues de colocarlos.
        assertTrue(machine.getBoard().isBoardWithShips());

        // La flota estandar tiene 10 barcos con 1+3+2+2+2+2+1+1+1+1 = 20 celdas de barco en total
        // (carrier=4, 2xsubmarine=3 c/u, 3xdestructor=2 c/u, 4xfrigate=1 c/u).
        int shipCellCount = 0;
        List<List<Cell>> cells = machine.getBoard().getCells();
        for (List<Cell> row : cells) {
            for (Cell cell : row) {
                if (cell.getState().equals(Board.SHIP)) {
                    shipCellCount++;
                }
            }
        }
        assertEquals(20, shipCellCount,
                "La suma de celdas ocupadas por toda la flota estandar deberia ser 20");
    }

    // ---------------------------------------------------------------
    // Full turn-based simulation (mirrors what PlayController does,
    // but purely at the model level, without JavaFX).
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Simulacion completa: humano ataca a la maquina hasta hundir toda su flota")
    void fullSimulationHumanSinksMachineFleet() {
        Game game = new Game();
        game.startMatch();

        Board machineBoard = game.getPlayerMachine().getBoard();

        int attempts = 0;
        int maxAttempts = 100;
        for (int row = 0; row < 10 && machineBoard.isBoardWithShips(); row++) {
            for (int column = 0; column < 10 && machineBoard.isBoardWithShips(); column++) {
                if (machineBoard.isCellAlreadyAttacked(row, column)) continue;

                machineBoard.attackCell(row, column);
                String result = machineBoard.getStateOfCell(row, column);

                if (result.equals(Board.HIT) && machineBoard.isShipSunken(row, column)) {
                    machineBoard.sinkShip(row, column);
                }
                attempts++;
                if (attempts >= maxAttempts) break;
            }
        }

        assertFalse(machineBoard.isBoardWithShips(),
                "Recorriendo todo el tablero, toda la flota de la maquina deberia quedar hundida");
    }
}
