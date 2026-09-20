import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashSet;
import java.awt.Color;
import java.time.Duration;
/**
 * Unit test cases for cycle 3: the SlotMachine(n) constructor and the contest solver.
 * Every machine created with SlotMachine(n) is invisible.
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class SlotMachineContestTest {

    /** Should: create exactly n wheels. */
    @Test
    public void constructorWithNShouldCreateNWheels() {
        SlotMachine machine = new SlotMachine(5);
        assertEquals(5, machine.getWheels());
        assertTrue(machine.ok());
    }

    /** Should: register n symbols, all different. */
    @Test
    public void constructorWithNShouldCreateNDistinctSymbols() {
        SlotMachine machine = new SlotMachine(5);
        String[] catalog = machine.symbols();
        assertEquals(5, catalog.length);
        assertEquals(5, new HashSet<>(Arrays.asList(catalog)).size());
    }

    /** Should not: start in a jackpot; the contest guarantees k > 1. */
    @Test
    public void constructorWithNShouldNeverStartInJackpot() {
        for (int i = 0; i < 10; i++) {
            SlotMachine machine = new SlotMachine(3);
            assertTrue(machine.distinctSymbols() > 1);
            assertFalse(machine.isJackpot());
        }
    }

    /** Should not: build fewer than MIN_WHEELS wheels; it adjusts and reports it. */
    @Test
    public void constructorWithNShouldAdjustWhenBelowMinimum() {
        SlotMachine machine = new SlotMachine(2);
        assertEquals(SlotMachine.MIN_WHEELS, machine.getWheels());
        assertFalse(machine.ok());
    }

    /** Should: support the contest maximum, n = 50. */
    @Test
    public void constructorWithNShouldSupportContestMaximum() {
        SlotMachine machine = new SlotMachine(50);
        assertEquals(50, machine.getWheels());
        assertEquals(50, machine.symbols().length);
        assertTrue(machine.ok());
    }
    
    /** Should: show the window when made visible and hide it again when made invisible. */
    @Test
    public void makeVisibleAndMakeInvisibleShouldShowAndHideTheWindow() {
        SlotMachine machine = new SlotMachine(3);
        assertFalse(Canvas.getCanvas().isVisible());
        machine.makeVisible();
        assertTrue(Canvas.getCanvas().isVisible());
        machine.makeInvisible();
        assertFalse(Canvas.getCanvas().isVisible());
    }
    
    /** Should: give every symbol of the largest machine a known and different color. */
    @Test
    public void allSymbolsOfLargestMachineShouldHaveKnownDifferentColors() {
        SlotMachine machine = new SlotMachine(50);
        HashSet<Color> colors = new HashSet<>();
        for (String symbol : machine.symbols()) {
            Color c = Canvas.colorFor(symbol);
            assertNotNull(c, symbol + " is not a known color");
            colors.add(c);
        }
        assertEquals(50, colors.size());
    }
    /** Should: rotate backwards when steps are negative. */
    @Test
    public void spinWithNegativeStepsShouldRotateBackwards() {
        SlotMachine machine = new SlotMachine(3);   // symbols: red, blue, green
        machine.placeSymbol(1, "red");
        machine.spin(1, -1);
        assertEquals("green", machine.configuration()[0]);
        assertTrue(machine.ok());
    }
    
    /** Should: handle the contest's largest step (10^9) quickly and correctly. */
    @Test
    public void spinWithHugeStepsShouldBeFastAndCorrect() {
        SlotMachine machine = new SlotMachine(3);
        machine.placeSymbol(1, "red");
        assertTimeout(Duration.ofSeconds(1), () -> machine.spin(1, 1_000_000_000));
        assertEquals("blue", machine.configuration()[0]);   // 10^9 mod 3 = 1
    }
    
    /** Should not: change the wheel when steps is a multiple of the symbol count. */
    @Test
    public void spinWithMultipleOfSymbolCountShouldLeaveWheelUnchanged() {
        SlotMachine machine = new SlotMachine(3);
        machine.placeSymbol(1, "red");
        machine.spin(1, 3);
        assertEquals("red", machine.configuration()[0]);
        assertTrue(machine.ok());
    }
    
    /** Should not: open a dialog when addWheel fails on an invisible machine. */
    @Test
    public void addWheelShouldNotOpenDialogWhenInvisible() {
        SlotMachine machine = new SlotMachine(3);
        assertTimeoutPreemptively(Duration.ofSeconds(2),
            () -> machine.addWheel(SlotMachine.MAX_WHEELS));
        assertFalse(machine.ok());
    }
    
    /** Should not: open a dialog when delWheel fails on an invisible machine. */
    @Test
    public void delWheelShouldNotOpenDialogWhenInvisible() {
        SlotMachine machine = new SlotMachine(3);
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> machine.delWheel(1));
        assertFalse(machine.ok());
    }
    
    /** Should not: open a dialog when placeSymbol fails on an invisible machine. */
    @Test
    public void placeSymbolShouldNotOpenDialogWhenInvisible() {
        SlotMachine machine = new SlotMachine(3);
        assertTimeoutPreemptively(Duration.ofSeconds(2),
            () -> machine.placeSymbol(1, "notacolor"));
        assertFalse(machine.ok());
    }
    
    /** Should: leave the machine in a jackpot for every size from 3 to 12. */
    @Test
    public void solveShouldEndInJackpotForSmallAndMediumSizes() {
        SlotMachineContest contest = new SlotMachineContest();
        for (int n = 3; n <= 12; n++) {
            contest.solve(n);
            assertTrue(contest.getMachine().isJackpot(), "n = " + n);
        }
    }
    
    /** Should: win from many different random starts, including the tricky n = 3. */
    @Test
    public void solveShouldWinFromManyRandomStarts() {
        SlotMachineContest contest = new SlotMachineContest();
        for (int i = 0; i < 100; i++) {
            contest.solve(3);
            assertTrue(contest.getMachine().isJackpot());
            contest.solve(4);
            assertTrue(contest.getMachine().isJackpot());
        }
    }
    
    /** Should: solve the contest's largest machine, n = 50, in reasonable time. */
    @Test
    public void solveShouldSolveContestMaximumQuickly() {
        SlotMachineContest contest = new SlotMachineContest();
        assertTimeout(Duration.ofSeconds(10), () -> contest.solve(50));
        assertTrue(contest.getMachine().isJackpot());
    }
    
    /** Should not: use more than 10 000 actions, nor more than the designed bound (n-1)(2n+1). */
    @Test
    public void solveShouldRespectActionLimits() {
        SlotMachineContest contest = new SlotMachineContest();
        for (int n : new int[]{3, 10, 25, 50}) {
            int[][] actions = contest.solve(n);
            assertTrue(actions.length <= (n - 1) * (2 * n + 1), "n = " + n);
            assertTrue(actions.length <= 10000, "n = " + n);
        }
    }
    
    /** Should: return well-formed actions: wheel between 1 and n, and steps different from zero. */
    @Test
    public void solveShouldReturnWellFormedActions() {
        SlotMachineContest contest = new SlotMachineContest();
        int n = 6;
        for (int[] action : contest.solve(n)) {
            assertEquals(2, action.length);
            assertTrue(action[0] >= 1 && action[0] <= n);
            assertNotEquals(0, action[1]);
        }
    }
    
    /** Should not: solve sizes outside the contest range; it returns no actions. */
    @Test
    public void solveShouldReturnNoActionsWhenSizeIsOutOfRange() {
        SlotMachineContest contest = new SlotMachineContest();
        assertEquals(0, contest.solve(2).length);
        assertEquals(0, contest.solve(51).length);
        assertEquals(0, contest.solve(-1).length);
    }
    
    /** Should: keep the machine invisible while solving. */
    @Test
    public void solveShouldKeepMachineInvisible() {
        SlotMachineContest contest = new SlotMachineContest();
        contest.solve(5);
        assertFalse(Canvas.getCanvas().isVisible());
    }
    
    /**
     * Should: end visible and in a jackpot after simulating. Replaying the actions
     * only leads to a jackpot if the rewind restored the exact initial configuration,
     * so this also checks the round trip.
     */
    @Test
    public void simulateShouldEndVisibleInJackpot() {
        SlotMachineContest contest = new SlotMachineContest();
        contest.simulate(3);
        SlotMachine machine = contest.getMachine();
        assertTrue(Canvas.getCanvas().isVisible());
        assertTrue(machine.isJackpot());
        machine.makeInvisible();   // hide the window again for the rest of the tests
    }
    
    /** Should not: simulate a size outside the contest range; no machine is created. */
    @Test
    public void simulateShouldDoNothingWhenSizeIsOutOfRange() {
        SlotMachineContest contest = new SlotMachineContest();
        contest.simulate(2);
        assertNull(contest.getMachine());
    }
    
    @Test
    public void visibleSpinShouldEndOnCorrectSymbolInBothDirections() {
        SlotMachine machine = new SlotMachine(5);   // red, blue, green, yellow, orange
        machine.placeSymbol(1, "red");
        machine.makeVisible();
        machine.spin(1, 1);    // forward 1
        assertEquals("blue", machine.configuration()[0]);
        machine.spin(1, -1);   // backward 1
        assertEquals("red", machine.configuration()[0]);
        machine.spin(1, 4);    // 4 forward = 1 backward
        assertEquals("orange", machine.configuration()[0]);
        machine.spin(1, 3);    // 3 forward = 2 backward
        assertEquals("green", machine.configuration()[0]);
        machine.makeInvisible();
    }

    /** Should: take the short way: one step back on 50 symbols must not animate 49 steps forward. */
    @Test
    public void visibleBackwardSpinShouldTakeTheShortWay() {
        SlotMachine machine = new SlotMachine(50);
        machine.makeVisible();
        assertTimeout(Duration.ofSeconds(5), () -> machine.spin(1, -1)); 
    }
}