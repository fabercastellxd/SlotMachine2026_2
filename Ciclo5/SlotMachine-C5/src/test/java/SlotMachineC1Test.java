import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test cases for cycle 1: create the machine, manage wheels and symbols,
 * spin and consult. Written in cycle 5 after studying the JaCoCo report
 * Every test runs with an invisible machine
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class SlotMachineC1Test {

    private SlotMachine machine;

    @BeforeEach
    public void setUp() {
        machine = new SlotMachine(false);
    }

    private void addThreeSymbols() {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }

    /** Should: start with the minimum number of wheels and no symbols */
    @Test
    public void newMachineShouldHaveMinimumWheelsAndNoSymbols() {
        assertEquals(SlotMachine.MIN_WHEELS, machine.getWheels());
        assertEquals(0, machine.symbols().length);
    }

    /** Should: put a symbol in the first position when the position is below one */
    @Test
    public void addSymbolShouldClampPositionBelowOne() {
        addThreeSymbols();
        machine.addSymbol(0, "yellow");
        assertTrue(machine.ok());
        assertEquals("yellow", machine.symbols()[0]);
    }

    /** Should: put a symbol in the last position when the position is too big */
    @Test
    public void addSymbolShouldClampPositionAboveSize() {
        addThreeSymbols();
        machine.addSymbol(99, "yellow");
        assertTrue(machine.ok());
        assertEquals("yellow", machine.symbols()[3]);
    }

    /** Should: remove a symbol from the catalog */
    @Test
    public void delSymbolShouldRemoveTheSymbol() {
        addThreeSymbols();
        machine.delSymbol("blue");
        assertTrue(machine.ok());
        assertFalse(Arrays.asList(machine.symbols()).contains("blue"));
    }

    /** Should not: add wheels when the amount is not positive */
    @Test
    public void addWheelShouldFailWithNonPositiveAmount() {
        machine.addWheel(0);
        assertFalse(machine.ok());
        assertEquals(SlotMachine.MIN_WHEELS, machine.getWheels());
    }

    /** Should not: delete wheels when the amount is not positive */
    @Test
    public void delWheelShouldFailWithNonPositiveAmount() {
        machine.addWheel(2);
        machine.delWheel(0);
        assertFalse(machine.ok());
        assertEquals(SlotMachine.MIN_WHEELS + 2, machine.getWheels());
    }

    /** Should: spin one wheel and leave on it a symbol of the catalog */
    @Test
    public void spinOneWheelShouldShowASymbolOfTheCatalog() {
        addThreeSymbols();
        machine.spin(2);
        assertTrue(machine.ok());
        assertTrue(Arrays.asList(machine.symbols()).contains(machine.configuration()[1]));
    }

    /** Should: spin only the chosen wheel */
    @Test
    public void spinOneWheelShouldNotMoveTheOthers() {
        addThreeSymbols();
        machine.lock(1);
        machine.lock(3);
        String[] before = machine.configuration();
        machine.spin(2);
        assertTrue(machine.ok());
        assertEquals(before[0], machine.configuration()[0]);
        assertEquals(before[2], machine.configuration()[2]);
    }

    /** Should not: spin one wheel when the machine has no symbols */
    @Test
    public void spinOneWheelShouldFailWithoutSymbols() {
        machine.spin(1);
        assertFalse(machine.ok());
    }

    /** Should not: spin one wheel that is locked */
    @Test
    public void spinOneWheelShouldFailWhenLocked() {
        addThreeSymbols();
        machine.placeSymbol(1, "red");
        machine.lock(1);
        machine.spin(1);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    /** Should not: spin all the wheels when the machine has no symbols */
    @Test
    public void spinShouldFailWithoutSymbols() {
        machine.spin();
        assertFalse(machine.ok());
    }

    /** Should not: report a jackpot when the machine has no symbols */
    @Test
    public void isJackpotShouldFailWithoutSymbols() {
        assertFalse(machine.isJackpot());
        assertFalse(machine.ok());
    }

    /** Should: report a jackpot when every wheel shows the same symbol */
    @Test
    public void isJackpotShouldBeTrueWithSameSymbol() {
        addThreeSymbols();
        for (int i = 1; i <= machine.getWheels(); i++) {
            machine.placeSymbol(i, "green");
        }
        assertTrue(machine.isJackpot());
        assertEquals(1, machine.distinctSymbols());
    }
}
