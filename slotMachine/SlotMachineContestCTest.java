

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Shared unit tests for cycle 3 (collective class). 
 */
public class SlotMachineContestCTest {

    /**
     * Should not: use more than 10 000 actions. Every action must also be valid:
     * a wheel between 1 and n, and steps within the contest range (+-10^9).
     */
    @Test
    public void accordingPmSgShouldNotUseMoreThanTenThousandActions() {
        int n = 50;
        SlotMachineContest contest = new SlotMachineContest();
        int[][] actions = contest.solve(n);
        assertTrue(actions.length <= 10000);
        for (int[] action : actions) {
            assertEquals(2, action.length);
            assertTrue(action[0] >= 1 && action[0] <= n);
            assertTrue(Math.abs(action[1]) <= 1_000_000_000);
        }
    }

    /**
     * Should not: start in a jackpot. The contest guarantees that a machine of
     * n wheels starts with more than one distinct symbol.
     */
    @Test
    public void accordingPmSgShouldNotCreateMachineAlreadyInJackpot() {
        for (int n : new int[]{3, 7, 50}) {
            SlotMachine machine = new SlotMachine(n);
            assertEquals(n, machine.configuration().length);
            assertTrue(machine.distinctSymbols() > 1);
            assertFalse(machine.isJackpot());
        }
    }
}