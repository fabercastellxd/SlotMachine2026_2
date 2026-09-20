import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test cases for the whole slotMachine project (cycles 1 and 2).
 * Every test runs with the canvas window fully hidden ({@code new SlotMachine(false)}),
 * so no window ever appears during the test run.
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class SlotMachineC2Test {

    private SlotMachine machine;

    @BeforeEach
    public void setUp() {
        machine = new SlotMachine(false);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }

    // ---------- Requirement 1: create a slot machine ----------

    /** Should: start with exactly MIN_WHEELS wheels. */
    @Test
    public void constructorShouldStartWithMinimumWheels() {
        assertEquals(SlotMachine.MIN_WHEELS, machine.getWheels());
    }

    // ---------- Requirement 2: add / delete wheels ----------

    /** Should: add the exact number of wheels requested. */
    @Test
    public void addWheelShouldAddExactAmountRequested() {
        int before = machine.getWheels();
        machine.addWheel(5);
        assertEquals(before + 5, machine.getWheels());
        assertTrue(machine.ok());
    }

    /** Should not: add wheels beyond MAX_WHEELS; nothing should change. */
    @Test
    public void addWheelShouldFailWhenExceedingMaximum() {
        int before = machine.getWheels();
        machine.addWheel(SlotMachine.MAX_WHEELS);
        assertEquals(before, machine.getWheels());
        assertFalse(machine.ok());
    }

    /** Should: remove the exact number of wheels requested. */
    @Test
    public void delWheelShouldRemoveExactAmountRequested() {
        machine.addWheel(5);
        int before = machine.getWheels();
        machine.delWheel(2);
        assertEquals(before - 2, machine.getWheels());
        assertTrue(machine.ok());
    }

    /** Should not: remove wheels below MIN_WHEELS; nothing should change. */
    @Test
    public void delWheelShouldFailWhenGoingBelowMinimum() {
        int before = machine.getWheels();
        machine.delWheel(before); // would leave 0 wheels
        assertEquals(before, machine.getWheels());
        assertFalse(machine.ok());
    }

    // ---------- Requirement 3: add / delete symbols ----------

    /** Should: register a new color in the machine's catalog. */
    @Test
    public void addSymbolShouldRegisterNewColor() {
        machine.addSymbol(1, "yellow");
        assertEquals(4, machine.symbols().length);
        assertTrue(machine.ok());
    }

    /** Should not: register a color that already exists. */
    @Test
    public void addSymbolShouldFailWhenColorAlreadyExists() {
        int before = machine.symbols().length;
        machine.addSymbol(1, "red");
        assertFalse(machine.ok());
        assertEquals(before, machine.symbols().length);
    }

    /** Should: remove an existing color from the catalog. */
    @Test
    public void delSymbolShouldRemoveExistingColor() {
        machine.delSymbol("red");
        assertEquals(2, machine.symbols().length);
        assertTrue(machine.ok());
    }

    /** Should not: remove a color that was never registered. */
    @Test
    public void delSymbolShouldFailWhenColorDoesNotExist() {
        int before = machine.symbols().length;
        machine.delSymbol("purple");
        assertFalse(machine.ok());        
        assertEquals(before, machine.symbols().length);
    }

    /** Should: manually set the visible symbol of a specific wheel. */
    @Test
    public void placeSymbolShouldSetVisibleSymbolOnTargetWheel() {
        machine.placeSymbol(2, "green");
        assertEquals("green", machine.configuration()[1]);
        assertTrue(machine.ok());
    }

    /** Should not: accept a symbol that isn't in the machine's catalog. */
    @Test
    public void placeSymbolShouldFailWithUnknownSymbol() {
        machine.placeSymbol(1, "purple");
        assertFalse(machine.ok());
    }

    // ---------- Requirement 9: swap ----------

    /** Should: exchange the visible symbols of two wheels. */
    @Test
    public void swapShouldExchangeVisibleSymbols() {
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.swap(1, 2);
        String[] config = machine.configuration();
        assertEquals("blue", config[0]);
        assertEquals("red", config[1]);
        assertTrue(machine.ok());
    }

    /** Should not: exchange symbols if either wheel is locked. */
    @Test
    public void swapShouldFailWhenAWheelIsLocked() {
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.lock(1);
        machine.swap(1, 2);
        assertFalse(machine.ok());
        String[] config = machine.configuration();
        assertEquals("red", config[0]);
        assertEquals("blue", config[1]);
    }

    // ---------- Requirement 10: lock / unlock ----------

    /** Should not: allow a locked wheel to spin. */
    @Test
    public void lockShouldPreventWheelFromSpinning() {
        machine.placeSymbol(1, "red");
        machine.lock(1);
        machine.spin(1, 1);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    /** Should: allow a wheel to spin again after being unlocked. */
    @Test
    public void unlockShouldAllowWheelToSpinAgain() {
        machine.placeSymbol(1, "red");
        machine.lock(1);
        machine.unlock(1);
        machine.spin(1, 1);
        assertEquals("blue", machine.configuration()[0]);
        assertTrue(machine.ok());
    }

    // ---------- Requirement 11: spin(wheel, steps) ----------

    /** Should: advance exactly the requested number of steps. */
    @Test
    public void spinWithStepsShouldAdvanceExactAmount() {
        machine.placeSymbol(1, "red");
        machine.spin(1, 1);
        assertEquals("blue", machine.configuration()[0]);
        assertTrue(machine.ok());
    }

    /** Should: wrap around circularly after the last symbol. */
    @Test
    public void spinWithStepsShouldWrapAroundCircularly() {
        machine.placeSymbol(1, "green");
        machine.spin(1, 1);
        assertEquals("red", machine.configuration()[0]);
        assertTrue(machine.ok());
    }

    /** Should not: spin when the machine has no symbols registered. */
    @Test
    public void spinWithStepsShouldFailWhenNoSymbolsExist() {
        SlotMachine empty = new SlotMachine(false);
        empty.spin(1, 1);
        assertFalse(empty.ok());
    }

    // ---------- Requirement 12: spin(setSymbols) ----------

    /** Should: leave the machine exactly in the requested configuration. */
    @Test
    public void spinWithConfigurationShouldSetExactSymbols() {
        machine.spin(new String[]{"red", "blue", "green"});
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.configuration());
        assertTrue(machine.ok());
    }

    /** Should not: accept an array whose length doesn't match the wheel count. */
    @Test
    public void spinWithConfigurationShouldFailWithWrongLength() {
        machine.spin(new String[]{"red", "blue"});
        assertFalse(machine.ok());
    }

    /** Should not: accept a symbol that isn't in the catalog. */
    @Test
    public void spinWithConfigurationShouldFailWithUnknownSymbol() {
        machine.spin(new String[]{"red", "blue", "purple"});
        assertFalse(machine.ok());
    }

    /** Should: keep locked wheels unchanged, ignoring what the array requests for them. */
    @Test
    public void spinWithConfigurationShouldRespectLockedWheels() {
        machine.placeSymbol(2, "blue");
        machine.lock(2);
        machine.spin(new String[]{"red", "green", "green"});
        assertEquals("blue", machine.configuration()[1]);
        assertTrue(machine.ok());
    }

    // ---------- Queries: symbols / distinctSymbols / configuration / isJackpot ----------

    /** Should: return every registered symbol, in insertion order. */
    @Test
    public void symbolsShouldReturnCatalogInInsertionOrder() {
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.symbols());
    }

    /** Should: return the total count of registered symbols. */
    @Test
    public void symbolsLengthShouldMatchCatalogSize() {
    assertEquals(3, machine.symbols().length);
    }

    /** Should: return the visible symbol of every wheel, left to right. */
    @Test
    public void configurationShouldReturnVisibleSymbolPerWheel() {
        machine.spin(new String[]{"red", "blue", "green"});
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.configuration());
    }

    /** Should: report a jackpot when all wheels show the same symbol. */
    @Test
    public void isJackpotShouldBeTrueWhenAllWheelsMatch() {
        machine.spin(new String[]{"red", "red", "red"});
        assertTrue(machine.isJackpot());
    }

    /** Should not: report a jackpot when wheels show different symbols. */
    @Test
    public void isJackpotShouldBeFalseWhenWheelsDiffer() {
        machine.spin(new String[]{"red", "blue", "green"});
        assertFalse(machine.isJackpot());
    }

    /** Should not: report a jackpot when the machine has no symbols yet. */
    @Test
    public void isJackpotShouldBeFalseWithNoSymbols() {
        SlotMachine empty = new SlotMachine(false);
        assertFalse(empty.isJackpot());
        assertFalse(empty.ok());
    }

    // ---------- Requirement 7: visibility ----------

    /** Should: succeed when toggling the machine's visibility. */
    @Test
    public void makeVisibleAndMakeInvisibleShouldSucceed() {
        machine.makeVisible();
        assertTrue(machine.ok());
        machine.makeInvisible();
        assertTrue(machine.ok());
    }
    
    // ----------- Marathon tests ----------------
    /** Should: count only the symbols visible right now, not the catalog. */
    @Test
    public void distinctSymbolsShouldCountOnlyVisibleSymbols() {
        assertEquals(1, machine.distinctSymbols()); 
    }
    
    /** Should: count one per different visible symbol. */
    @Test
    public void distinctSymbolsShouldCountEachDifferentVisibleSymbol() {
        machine.spin(new String[]{"red", "blue", "green"});
        assertEquals(3, machine.distinctSymbols());
    }
    
    /** Should not: count anything when no symbols exist. */
    @Test
    public void distinctSymbolsShouldBeZeroWithNoSymbols() {
        SlotMachine empty = new SlotMachine(false);
        assertEquals(0, empty.distinctSymbols());
    }    
    // Note: exit() is intentionally not tested here, since it calls
    // System.exit(0) directly and would terminate the whole JVM running
    // the test suite, aborting every other test. It is instead covered
    // by a manual acceptance test.
}