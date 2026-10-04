import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Shared unit tests for cycle 4 (collective class) They only use the types
 * named in the cycle and operations of the design diagram
 */
public class SlotMachineCC4Test {

    /** Should not: let a rebel wheel be locked */
    @Test
    public void accordingPmSgShouldNotLockARebelWheel() {
        SlotMachine machine = new SlotMachine(5);
        machine.addWheel("rebel", 1);
        assertTrue(machine.ok());
        int rebel = machine.configuration().length;
        machine.lock(rebel);
        assertFalse(machine.ok());
    }

    /** Should not: let a rebel wheel be swapped; the configuration must not change */
    @Test
    public void accordingPmSgShouldNotSwapARebelWheel() {
        SlotMachine machine = new SlotMachine(5);
        machine.addWheel("rebel", 1);
        assertTrue(machine.ok());
        String[] before = machine.configuration();
        machine.swap(1, before.length);
        assertFalse(machine.ok());
        assertArrayEquals(before, machine.configuration());
    }

    /** Should: a lefty wheel copy the state of the wheel on its left when it spins */
    @Test
    public void accordingPmSgShouldMakeALeftyWheelCopyItsLeftWheel() {
        SlotMachine machine = new SlotMachine(5);
        machine.addWheel("lefty", 1);
        assertTrue(machine.ok());
        int lefty = machine.configuration().length;
        machine.spin(lefty - 1, 1);            // the wheel on its left changes
        machine.spin(lefty, 1);
        assertTrue(machine.ok());
        String[] conf = machine.configuration();
        assertEquals(conf[lefty - 2], conf[lefty - 1]);
    }
}