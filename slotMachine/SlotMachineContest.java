import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Solver for Problem I (Slot Machine) of the 2025 ICPC World Finals.
 * It plays the role of the contestant: the only information it gets from the
 * machine is the number of distinct visible symbols, and the only way it acts on
 * it is by spinning a wheel. It uses only {@code SlotMachine(n)},
 * {@code spin(wheel, steps)} and {@code distinctSymbols()}.
 * The machine stays invisible while solving.
 *
 * The strategy has three phases:
 * 1) make all n visible symbols different, 2) find which wheel shows which symbol,
 * 3) move every wheel to the symbol of wheel 1.
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 * @version 20/09/26
 */
public class SlotMachineContest {

    private SlotMachine machine;
    private int n;
    private int distinct;
    private ArrayList<int[]> actions;

    /** order[t], for t = 1..n-1: the wheel whose symbol is t positions after wheel 1's. */
    private int[] order;

    /**
     * Creates a solver with no solution computed yet.
     */
    public SlotMachineContest() {
        actions = new ArrayList<>();
    }

    /**
     * Solves the contest for a new random invisible machine of n wheels and n symbols.
     * If n is outside [{@link SlotMachine#MIN_WHEELS}, {@link SlotMachine#MAX_WHEELS}],
     * no machine is created and no actions are returned.
     *
     * @param n number of wheels and of symbols.
     * @return the actions that lead to a jackpot: one row {wheel, steps} per action,
     *         in the order they were made.
     */
    public int[][] solve(int n) {
        if (n < SlotMachine.MIN_WHEELS || n > SlotMachine.MAX_WHEELS) {
            return new int[0][];
        }

        this.actions = new ArrayList<>();
        this.n = n;
        this.machine = new SlotMachine(n);
        this.order = new int[n];
        this.distinct = machine.distinctSymbols();

        if (makeAllDistinct() || findOrder() || align()) {
            return actions.toArray(new int[0][]);
        }

        JOptionPane.showMessageDialog(
            null, 
            "El solver termino sin encontrar un jackpot.", 
            "Solver Error", 
            JOptionPane.ERROR_MESSAGE
        );

        return new int[0][];
    }

    /**
     * Returns the machine used by the last {@link #solve(int)}, in the state it was left.
     *
     * @return the machine, or {@code null} if there is no solution computed.
     */
    public SlotMachine getMachine() {
        return machine;
    }

    /**
     * The only gate to the machine: spins a wheel, records the action and reads
     * the number of distinct symbols. An action with 0 steps is neither made nor recorded.
     *
     * @param wheel 1-based wheel.
     * @param steps positions to rotate (may be negative).
     * @return {@code true} if the machine reached a jackpot (a single distinct symbol).
     */
    private boolean move(int wheel, int steps) {
        if (steps == 0) return false;
        machine.spin(wheel, steps);
        actions.add(new int[]{wheel, steps});
        distinct = machine.distinctSymbols();
        return distinct == 1;
    }

    /**
     * Phase 1: makes the n visible symbols all different. Wheel 1 stays still. Each
     * other wheel is turned through its whole cycle, one step at a time, and then
     * left where the number of distinct symbols was the highest: there it shows a
     * symbol that no other wheel shows.
     *
     * @return {@code true} if the jackpot was reached along the way.
     */
    private boolean makeAllDistinct() {
        for (int wheel = 2; wheel <= n; wheel++) {
            if (distinct == n) return false;
            int bestDistinct = distinct;
            int bestStep = 0;

            for (int step = 1; step < n; step++) {
                if (move(wheel, 1)) return true;
                if (distinct > bestDistinct) {
                    bestDistinct = distinct;
                    bestStep = step;
                }
            }
            if (move(wheel, bestStep - (n - 1))) return true;
        }
        return false;
    }

    /**
     * Phase 2: starting with all symbols different, finds {@code order[t]} for every t.
     * Turning wheel 1 one step makes it repeat a symbol; the wheel that, moved one
     * step back, makes all symbols different again is the one that showed it.
     * When it ends, wheel 1 shows its original symbol + (n-1) and wheel order[t]
     * shows the original symbol of wheel 1 + (t-1).
     *
     * @return {@code true} if the jackpot was reached along the way.
     */
    private boolean findOrder() {
        boolean[] known = new boolean[n + 1];
        known[1] = true;

        for (int t = 1; t < n; t++) {
            if (move(1, 1)) return true;
            int wheel = locate(known);
            if (wheel == 0) return true;

            if (wheel < 0) {
                JOptionPane.showMessageDialog(
                    null, 
                    "Ninguna rueda coincide con algun simbolo " + t, 
                    "Phase 2 Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
            known[wheel] = true;
            order[t] = wheel;
        }
        return false;
    }

    /**
     * Tries each wheel not yet known: moves it one step back and, if all symbols
     * become different, it is the one searched; otherwise puts it back.
     *
     * @param known known[w] is true if wheel w already has its place in the order.
     * @return the wheel found, 0 if the jackpot was reached while testing,
     *         or -1 if no wheel matched (it should never happen).
     */
    private int locate(boolean[] known) {
        for (int wheel = 2; wheel <= n; wheel++) {
            if (!known[wheel]) {
                if (move(wheel, -1)) return 0;
                if (distinct == n) return wheel;
                if (move(wheel, 1)) return 0;
            }
        }
        return -1;
    }

    /**
     * Phase 3: moves every wheel order[t] forward (n - t) steps, so that it shows
     * the symbol of wheel 1. The last action leaves a single distinct symbol.
     *
     * @return {@code true} if the jackpot was reached.
     */
    private boolean align() {
        for (int t = 1; t < n; t++) {
            if (move(order[t], n - t)) return true;
        }
        return false;
    }
    
    /**
     * Solves the contest for a new random machine and then shows the solution:
     * the machine is first rewound, still invisible, to its initial configuration
     * (undoing the actions in reverse order), then made visible, and finally the
     * actions are replayed one by one so each spin is seen step by step.
     * The machine is left visible, showing the jackpot. If n is outside the contest
     * range, nothing is done.
     * The animation lasts in proportion to the number of actions: it is comfortable
     * for small machines (n up to about 10), and for n = 50 it works but takes many minutes.
     *
     * @param n number of wheels and of symbols.
     */
    public void simulate(int n) {
        int[][] solution = solve(n);
        if (machine == null) return;
        rewind(solution);
        machine.makeVisible();
        replay(solution);
    }
    
    /**
     * Undoes the given actions, last to first, leaving the machine as it was
     * before the first one.
     *
     * @param solution the actions to undo, as returned by {@link #solve(int)}.
     */
    private void rewind(int[][] solution) {
        for (int i = solution.length - 1; i >= 0; i--) {
            machine.spin(solution[i][0], -solution[i][1]);
        }
    }
    
    /**
     * Makes the given actions again, first to last.
     *
     * @param solution the actions to replay, as returned by {@link #solve(int)}.
     */
    private void replay(int[][] solution) {
        for (int[] action : solution) {
            machine.spin(action[0], action[1]);
        }
    }
}