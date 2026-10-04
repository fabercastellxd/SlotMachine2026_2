import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test cases for cycle 4: types of wheels and of symbols
 * Every test runs with an invisible machine
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class SlotMachineC4Test {

    private SlotMachine machine;

    @BeforeEach
    public void setUp() {
        machine = new SlotMachine(false);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }

    // ---------- Mini ciclo-1 - tipos de wheels----------

    /** Should: add normal wheels when the type is "normal" */
    @Test
    public void addWheelWithNormalTypeShouldAddWheels() {
        machine.addWheel("normal", 2);
        assertTrue(machine.ok());
        assertEquals(SlotMachine.MIN_WHEELS + 2, machine.getWheels());
    }

    /** Should: give the new wheels every symbol of the catalog */
    @Test
    public void addWheelWithTypeShouldGiveNewWheelsAllSymbols() {
        machine.addWheel("normal", 1);
        machine.placeSymbol(4, "green");
        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[3]);
    }

    /** Should not: add anything when the type is unknown */
    @Test
    public void addWheelShouldFailWithUnknownType() {
        machine.addWheel("flying", 2);
        assertFalse(machine.ok());
        assertEquals(SlotMachine.MIN_WHEELS, machine.getWheels());
    }

    /** Should not: add anything when the type is null */
    @Test
    public void addWheelShouldFailWithNullType() {
        machine.addWheel(null, 1);
        assertFalse(machine.ok());
        assertEquals(SlotMachine.MIN_WHEELS, machine.getWheels());
    }

    /** Should: a normal wheel accepts being locked, swapped and deleted*/
    @Test
    public void normalWheelShouldAcceptLockSwapAndDelete() {
        Wheel wheel = new Wheel(0, 0, false);
        assertTrue(wheel.canBeLocked());
        assertTrue(wheel.canBeSwapped());
        assertTrue(wheel.canBeDeleted());
    }
        // ----------Mini ciclo 2: lefty and rebel wheels----------

    /** Should: add wheels of every type */
    @Test
    public void addWheelShouldAddMixedTypes() {
        machine.addWheel("lefty", 2);
        assertTrue(machine.ok());
        machine.addWheel("rebel", 1);
        assertTrue(machine.ok());
        assertEquals(SlotMachine.MIN_WHEELS + 3, machine.getWheels());
    }

    /** Should: a lefty wheel copies the symbol of its left wheel instead of rotating */
    @Test
    public void leftyShouldCopyLeftWheelWhenSpun() {
        machine.addWheel("lefty", 1);          // wheel 4
        machine.placeSymbol(3, "blue");
        machine.placeSymbol(4, "red");
        machine.spin(4, 2);                    // 2 steps would leave it on green
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[3]);
    }

    /** Should: a lefty wheel follows the new symbol of its left wheel when all wheels spin */
    @Test
    public void leftyShouldFollowLeftWheelWhenAllWheelsSpin() {
        machine.addWheel("lefty", 1);
        for (int i = 0; i < 10; i++) {
            machine.spin();
            String[] conf = machine.configuration();
            assertEquals(conf[2], conf[3]);
        }
    }

    /** Should: a lefty wheel can be locked, and then it does not spin */
    @Test
    public void leftyShouldAcceptLockAndThenNotSpin() {
        machine.addWheel("lefty", 1);
        machine.lock(4);
        assertTrue(machine.ok());
        machine.spin(4, 1);
        assertFalse(machine.ok());
    }

    /** Should: copy the visible symbol of the left wheel, whatever the steps */
    @Test
    public void leftyWheelShouldCopyVisibleSymbolOfLeftWheel() {
        Wheel left = new Wheel(0, 0, false);
        left.addSymbol(0, "red");
        left.addSymbol(1, "blue");
        left.setSymbol("blue");
        LeftyWheel lefty = new LeftyWheel(0, 0, false);
        lefty.addSymbol(0, "red");
        lefty.addSymbol(1, "blue");
        lefty.spin(left, 2, false);
        assertEquals("blue", lefty.getVisibleSymbol());
    }

    /** Should: rotate like a normal wheel when there is no wheel on its left */
    @Test
    public void leftyWheelShouldRotateNormallyWithoutLeftWheel() {
        LeftyWheel lefty = new LeftyWheel(0, 0, false);
        lefty.addSymbol(0, "red");
        lefty.addSymbol(1, "blue");
        lefty.spin(null, 1, false);
        assertEquals("blue", lefty.getVisibleSymbol());
    }

    /** Should not: let a rebel wheel be locked */
    @Test
    public void rebelShouldRefuseLock() {
        machine.addWheel("rebel", 1);
        machine.lock(4);
        assertFalse(machine.ok());
    }

    /** Should: a rebel wheel spins normally, even after refusing to be locked */
    @Test
    public void rebelShouldStillSpinAfterRefusingLock() {
        machine.addWheel("rebel", 1);
        machine.placeSymbol(4, "red");
        machine.lock(4);
        assertFalse(machine.ok());
        machine.spin(4, 1);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[3]);
    }

    /** Should not: let a rebel wheel be swapped, in either order */
    @Test
    public void rebelShouldRefuseSwap() {
        machine.addWheel("rebel", 1);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(4, "blue");
        machine.swap(1, 4);
        assertFalse(machine.ok());
        machine.swap(4, 1);
        assertFalse(machine.ok());
        String[] conf = machine.configuration();
        assertEquals("red", conf[0]);
        assertEquals("blue", conf[3]);
    }

    /** Should: normal wheels still swap when a rebel wheel exists */
    @Test
    public void normalWheelsShouldStillSwapWhenARebelExists() {
        machine.addWheel("rebel", 1);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.swap(1, 2);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
    }

    /** Should not: delete a rebel wheel */
    @Test
    public void rebelShouldRefuseDelete() {
        machine.addWheel("rebel", 1);          // wheel 4, the last one
        machine.addWheel("normal", 1);         // wheel 5
        machine.delWheel(1);                   // removes wheel 5
        assertTrue(machine.ok());
        assertEquals(4, machine.getWheels());
        machine.delWheel(1);                   // wheel 4 is rebel
        assertFalse(machine.ok());
        assertEquals(4, machine.getWheels());
    }

    /** Should not: delete anything if any of the wheels to remove is rebel */
    @Test
    public void delWheelShouldFailWhenAnyWheelToRemoveIsRebel() {
        machine.addWheel("rebel", 1);
        machine.addWheel("normal", 1);
        machine.delWheel(2);                   // wheels 4 (rebel) and 5
        assertFalse(machine.ok());
        assertEquals(5, machine.getWheels());
    }

    /** Should not: a rebel wheel accepts being locked, swapped or deleted */
    @Test
    public void rebelWheelShouldRefuseLockSwapAndDelete() {
        RebelWheel rebel = new RebelWheel(0, 0, false);
        assertFalse(rebel.canBeLocked());
        assertFalse(rebel.canBeSwapped());
        assertFalse(rebel.canBeDeleted());
    }
    
        //----------Mini cicl-3 -simbolos como objetoss----------

    /** Should: a symbol knows its color */
    @Test
    public void symbolShouldKnowItsColor() {
        Symbol symbol = new Symbol("red");
        assertEquals("red", symbol.getColor());
    }

    /** Should: a wheel works with symbol objects: show, rotate and set by color */
    @Test
    public void wheelShouldWorkWithSymbolObjects() {
        Wheel wheel = new Wheel(0, 0, false);
        wheel.addSymbol(0, new Symbol("red"));
        wheel.addSymbol(1, new Symbol("blue"));
        assertEquals("red", wheel.getVisibleSymbol());
        wheel.rotate(1, false);
        assertEquals("blue", wheel.getVisibleSymbol());
        wheel.setSymbol("red");
        assertEquals("red", wheel.getVisibleSymbol());
    }

    /** Should: a wheel removes a symbol by its color */
    @Test
    public void wheelShouldRemoveSymbolByColor() {
        Wheel wheel = new Wheel(0, 0, false);
        wheel.addSymbol(0, new Symbol("red"));
        wheel.addSymbol(1, new Symbol("blue"));
        wheel.delSymbol("red");
        assertEquals("blue", wheel.getVisibleSymbol());
        assertEquals(1, wheel.size());
    }

    /** Should: register a symbol of the normal type */
    @Test
    public void addSymbolWithNormalTypeShouldRegisterColor() {
        machine.addSymbol("normal", 1, "yellow");
        assertTrue(machine.ok());
        assertEquals(4, machine.symbols().length);
    }

    /** Should: insert the symbol in the requested position */
    @Test
    public void addSymbolWithTypeShouldKeepRequestedPosition() {
        machine.addSymbol("normal", 1, "yellow");
        assertTrue(machine.ok());
        assertEquals("yellow", machine.symbols()[0]);
    }

    /** Should not: add anything when the type of symbol is unknown */
    @Test
    public void addSymbolShouldFailWithUnknownType() {
        machine.addSymbol("flying", 1, "yellow");
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    /** Should not: add anything when the type of symbol is null */
    @Test
    public void addSymbolShouldFailWithNullType() {
        machine.addSymbol(null, 1, "yellow");
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    /** Should not: add a color that already exists, whatever its type */
    @Test
    public void addSymbolWithTypeShouldFailWhenColorAlreadyExists() {
        machine.addSymbol("normal", 1, "red");
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    /** Should: a wheel added after the symbols has every symbol of the catalog */
    @Test
    public void wheelsAddedAfterSymbolsShouldHaveThemAll() {
        machine.addSymbol("normal", 2, "yellow");
        machine.addWheel("normal", 1);
        machine.placeSymbol(4, "yellow");
        assertTrue(machine.ok());
        assertEquals("yellow", machine.configuration()[3]);
    }

    /** Should: keep working when a wheel is added after deleting a symbol */
    @Test
    public void wheelsAddedAfterDeletingASymbolShouldStillWork() {
        machine.delSymbol("red");
        assertTrue(machine.ok());
        machine.addWheel("normal", 1);
        assertTrue(machine.ok());
        machine.placeSymbol(4, "blue");
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[3]);
        machine.placeSymbol(4, "red");
        assertFalse(machine.ok());
    }
        //----------Mini ciclo-4-ephemeral and shy symbols----------

    /** Should: a normal symbol is full size, shown, without mark, and never changes */
    @Test
    public void normalSymbolShouldNeverChange() {
        Symbol symbol = new Symbol("red");
        assertEquals(100, symbol.getSizePercent());
        assertTrue(symbol.isShown());
        assertNull(symbol.getMarkColor());
        symbol.onSpin();
        symbol.onSelected();
        assertEquals(100, symbol.getSizePercent());
        assertTrue(symbol.isShown());
    }

    /** Should: an ephemeral symbol shrinks with every spin and keeps its color */
    @Test
    public void ephemeralSymbolShouldShrinkWithEachSpin() {
        Symbol symbol = new EphemeralSymbol("red");
        assertEquals(100, symbol.getSizePercent());
        symbol.onSpin();
        assertEquals(80, symbol.getSizePercent());
        symbol.onSpin();
        assertEquals(60, symbol.getSizePercent());
        assertEquals("red", symbol.getColor());
        assertTrue(symbol.isShown());
    }

    /** Should not: an ephemeral symbol gets smaller than a dot */
    @Test
    public void ephemeralSymbolShouldStopAtADot() {
        Symbol symbol = new EphemeralSymbol("red");
        for (int i = 0; i < 10; i++) {
            symbol.onSpin();
        }
        assertEquals(0, symbol.getSizePercent());
        assertTrue(symbol.isShown());
    }

    /** Should: a shy symbol switches between visible and invisible when selected */
    @Test
    public void shySymbolShouldToggleEachTimeItIsSelected() {
        Symbol symbol = new ShySymbol("red");
        assertTrue(symbol.isShown());
        symbol.onSelected();
        assertFalse(symbol.isShown());
        symbol.onSelected();
        assertTrue(symbol.isShown());
        symbol.onSpin();
        assertTrue(symbol.isShown());
        assertEquals(100, symbol.getSizePercent());
    }

    /** Should: the three types of symbols be told apart by their mark */
    @Test
    public void symbolTypesShouldHaveDifferentMarks() {
        Symbol ephemeral = new EphemeralSymbol("red");
        Symbol shy = new ShySymbol("red");
        assertNull(new Symbol("red").getMarkColor());
        assertNotNull(ephemeral.getMarkColor());
        assertNotNull(shy.getMarkColor());
        assertNotEquals(ephemeral.getMarkColor(), shy.getMarkColor());
    }

    /** Should: a spin shrinks the ephemeral symbols of the wheel that spins */
    @Test
    public void wheelSpinShouldShrinkItsEphemeralSymbols() {
        Wheel wheel = new Wheel(0, 0, false);
        wheel.addSymbol(0, new EphemeralSymbol("red"));
        wheel.addSymbol(1, new Symbol("blue"));
        wheel.spin(null, 1, false);
        assertEquals("blue", wheel.getVisibleSymbol());
        wheel.setSymbol("red");
        assertEquals(80, wheel.getCurrentSymbol().getSizePercent());
    }

    /** Should not: a spin shrinks the symbols of another wheel */
    @Test
    public void wheelSpinShouldNotShrinkSymbolsOfOtherWheels() {
        Wheel a = new Wheel(0, 0, false);
        Wheel b = new Wheel(0, 0, false);
        a.addSymbol(0, new EphemeralSymbol("red"));
        b.addSymbol(0, new EphemeralSymbol("red"));
        a.spin(null, 1, false);
        assertEquals(80, a.getCurrentSymbol().getSizePercent());
        assertEquals(100, b.getCurrentSymbol().getSizePercent());
    }

    /** Should: a shy symbol hides when the wheel moves and stops on it */
    @Test
    public void wheelShouldToggleShySymbolWhenItLandsOnIt() {
        Wheel wheel = new Wheel(0, 0, false);
        wheel.addSymbol(0, new ShySymbol("red"));
        wheel.addSymbol(1, new Symbol("blue"));
        assertTrue(wheel.getCurrentSymbol().isShown());
        wheel.spin(null, 1, false);
        wheel.spin(null, 1, false);
        assertEquals("red", wheel.getVisibleSymbol());
        assertFalse(wheel.getCurrentSymbol().isShown());
    }

    /** Should: a shy symbol switches every time it is set on the wheel */
    @Test
    public void wheelShouldToggleShySymbolWhenItIsSet() {
        Wheel wheel = new Wheel(0, 0, false);
        wheel.addSymbol(0, new ShySymbol("red"));
        wheel.addSymbol(1, new Symbol("blue"));
        wheel.setSymbol("red");
        assertFalse(wheel.getCurrentSymbol().isShown());
        wheel.setSymbol("red");
        assertTrue(wheel.getCurrentSymbol().isShown());
    }

    /** Should not: a spin that does not move the wheel select the symbol again */
    @Test
    public void wheelShouldNotSelectWhenTheSpinDoesNotMoveIt() {
        Wheel wheel = new Wheel(0, 0, false);
        wheel.addSymbol(0, new ShySymbol("red"));
        wheel.addSymbol(1, new Symbol("blue"));
        wheel.spin(null, 2, false);
        assertTrue(wheel.getCurrentSymbol().isShown());
    }

    /** Should: a hidden shy symbol still report its color */
    @Test
    public void hiddenShySymbolShouldStillReportItsColor() {
        Wheel wheel = new Wheel(0, 0, false);
        wheel.addSymbol(0, new ShySymbol("red"));
        wheel.setSymbol("red");
        assertFalse(wheel.getCurrentSymbol().isShown());
        assertEquals("red", wheel.getVisibleSymbol());
    }

    /** Should: register an ephemeral symbol in the machine */
    @Test
    public void addSymbolWithEphemeralTypeShouldRegisterColor() {
        machine.addSymbol("ephemeral", 4, "yellow");
        assertTrue(machine.ok());
        assertEquals(4, machine.symbols().length);
        assertEquals("yellow", machine.symbols()[3]);
    }

    /** Should: register a shy symbol in the machine */
    @Test
    public void addSymbolWithShyTypeShouldRegisterColor() {
        machine.addSymbol("shy", 4, "yellow");
        assertTrue(machine.ok());
        assertEquals(4, machine.symbols().length);
        assertEquals("yellow", machine.symbols()[3]);
    }

    /** Should: hidden shy symbols still count as their color for the jackpot */
    @Test
    public void hiddenShySymbolsShouldStillCountForTheJackpot() {
        machine.addSymbol("shy", 4, "yellow");
        machine.placeSymbol(1, "yellow");
        machine.placeSymbol(2, "yellow");
        machine.placeSymbol(3, "yellow");
        assertTrue(machine.isJackpot());
        assertEquals(1, machine.distinctSymbols());
        assertEquals("yellow", machine.configuration()[0]);
    }

    /** Should: an ephemeral symbol does not change where a spin leaves the wheel */
    @Test
    public void ephemeralSymbolShouldNotChangeTheResultOfASpin() {
        machine.addSymbol("ephemeral", 4, "yellow");
        machine.placeSymbol(1, "yellow");
        machine.spin(1, 1);
        assertTrue(machine.ok());
        assertEquals("red", machine.configuration()[0]);
        machine.spin(1, 4);
        assertTrue(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }
    
        // ----------Mini ciclo-5-Joker symbol----------

    /** Should: only the joker is wild, and it is otherwise a normal symbol */
    @Test
    public void onlyJokerSymbolShouldBeWild() {
        assertFalse(new Symbol("red").isWild());
        assertFalse(new EphemeralSymbol("red").isWild());
        assertFalse(new ShySymbol("red").isWild());
        Symbol joker = new JokerSymbol("yellow");
        assertTrue(joker.isWild());
        assertEquals("yellow", joker.getColor());
        assertEquals(100, joker.getSizePercent());
        assertTrue(joker.isShown());
    }

    /** Should: the joker be told apart from the other types by its mark */
    @Test
    public void jokerMarkShouldDifferFromOtherTypes() {
        Symbol joker = new JokerSymbol("yellow");
        assertNotNull(joker.getMarkColor());
        assertNotEquals(joker.getMarkColor(), new EphemeralSymbol("red").getMarkColor());
        assertNotEquals(joker.getMarkColor(), new ShySymbol("red").getMarkColor());
    }

    /** Should: a wheel know when it shows a joker */
    @Test
    public void wheelShouldKnowWhenItShowsAJoker() {
        Wheel wheel = new Wheel(0, 0, false);
        assertFalse(wheel.isWild());
        wheel.addSymbol(0, new Symbol("red"));
        wheel.addSymbol(1, new JokerSymbol("yellow"));
        assertFalse(wheel.isWild());
        wheel.setSymbol("yellow");
        assertTrue(wheel.isWild());
    }

    /** Should: register a joker symbol in the machine */
    @Test
    public void addSymbolWithJokerTypeShouldRegisterColor() {
        machine.addSymbol("joker", 4, "yellow");
        assertTrue(machine.ok());
        assertEquals(4, machine.symbols().length);
        assertEquals("yellow", machine.symbols()[3]);
    }

    /** Should: a joker complete a jackpot when the other wheels match */
    @Test
    public void jokerShouldCompleteAJackpot() {
        machine.addSymbol("joker", 4, "yellow");
        machine.spin(new String[]{"red", "yellow", "red"});
        assertTrue(machine.isJackpot());
    }

    /** Should not: a joker make a jackpot when the other wheels differ */
    @Test
    public void jokerShouldNotMakeAJackpotWhenOtherColorsDiffer() {
        machine.addSymbol("joker", 4, "yellow");
        machine.spin(new String[]{"red", "yellow", "blue"});
        assertFalse(machine.isJackpot());
    }

    /** Should: a machine that shows only jokers be a jackpot */
    @Test
    public void wheelsWithOnlyJokersShouldBeAJackpot() {
        machine.addSymbol("joker", 4, "yellow");
        machine.spin(new String[]{"yellow", "yellow", "yellow"});
        assertTrue(machine.isJackpot());
    }

    /** Should: a joker not count as a distinct symbol */
    @Test
    public void jokerShouldNotCountInDistinctSymbols() {
        machine.addSymbol("joker", 4, "yellow");
        machine.spin(new String[]{"red", "yellow", "red"});
        assertEquals(1, machine.distinctSymbols());
        machine.spin(new String[]{"red", "yellow", "blue"});
        assertEquals(2, machine.distinctSymbols());
    }

    /** Should: a machine that shows only jokers count as one distinct symbol */
    @Test
    public void onlyJokersShouldCountAsOneDistinctSymbol() {
        machine.addSymbol("joker", 4, "yellow");
        machine.spin(new String[]{"yellow", "yellow", "yellow"});
        assertEquals(1, machine.distinctSymbols());
    }

    /** Should: a spin that lands on a joker complete the jackpot */
    @Test
    public void spinShouldCompleteJackpotWhenItLandsOnAJoker() {
        machine.addSymbol("joker", 4, "yellow");
        machine.placeSymbol(1, "blue");
        machine.placeSymbol(2, "blue");
        assertFalse(machine.isJackpot());
        machine.spin(3, 3);                    // red + 3 steps = the joker
        assertTrue(machine.isJackpot());
    }

    /** Should: a wheel added after the joker also has it */
    @Test
    public void wheelsAddedAfterAJokerShouldHaveIt() {
        machine.addSymbol("joker", 4, "yellow");
        machine.addWheel("normal", 1);
        machine.placeSymbol(1, "blue");
        machine.placeSymbol(2, "blue");
        machine.placeSymbol(3, "blue");
        assertFalse(machine.isJackpot());
        machine.placeSymbol(4, "yellow");
        assertTrue(machine.isJackpot());
    }
        //----------Mini ciclo-6-mixed machine----------

    /** Adds one symbol of each new type and one wheel of each new type */
    private void addAllTypes() {
        machine.addSymbol("ephemeral", 4, "yellow");
        machine.addSymbol("shy", 5, "pink");
        machine.addSymbol("joker", 6, "purple");
        machine.addWheel("lefty", 1);          // wheel 4
        machine.addWheel("rebel", 1);          // wheel 5
    }

    /** Should: a machine mixing every type of wheel and symbol work as a whole */
    @Test
    public void mixedMachineShouldWorkWithAllTypes() {
        addAllTypes();
        assertTrue(machine.ok());
        assertEquals(5, machine.getWheels());
        assertEquals(6, machine.symbols().length);
        machine.spin(3, 1);                    // red + 1 = blue
        machine.spin(4, 2);                    // lefty copies wheel 3 (2 steps would be green)
        machine.spin(5, 1);                    // rebel spins normally
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "blue", "blue", "blue"},
            machine.configuration());
        assertFalse(machine.isJackpot());
        machine.placeSymbol(1, "purple");
        machine.placeSymbol(2, "purple");
        assertTrue(machine.isJackpot());
        assertEquals(1, machine.distinctSymbols());
    }

    /** Should not: the rebel wheel be locked, swapped or deleted, in a mixed machine */
    @Test
    public void mixedMachineShouldKeepRefusingWhatTheRebelRefuses() {
        addAllTypes();
        machine.lock(5);
        assertFalse(machine.ok());
        machine.swap(1, 5);
        assertFalse(machine.ok());
        machine.delWheel(1);                   // the last wheel is the rebel
        assertFalse(machine.ok());
        assertEquals(5, machine.getWheels());
        machine.lock(4);                       // the lefty accepts it
        assertTrue(machine.ok());
        machine.swap(1, 2);                    // normal wheels still swap
        assertTrue(machine.ok());
    }

    /** Should: one wheel with every type of symbol keep the state of each symbol */
    @Test
    public void wheelWithEveryTypeOfSymbolShouldKeepEachState() {
        Wheel wheel = new Wheel(0, 0, false);
        wheel.addSymbol(0, new Symbol("red"));
        wheel.addSymbol(1, new EphemeralSymbol("yellow"));
        wheel.addSymbol(2, new ShySymbol("pink"));
        wheel.addSymbol(3, new JokerSymbol("purple"));
        wheel.spin(null, 2, false);            // yellow shrinks to 80; lands on pink, which hides
        assertEquals("pink", wheel.getVisibleSymbol());
        assertFalse(wheel.getCurrentSymbol().isShown());
        wheel.spin(null, 2, false);            // yellow shrinks to 60; lands on red
        assertEquals("red", wheel.getVisibleSymbol());
        wheel.setSymbol("yellow");
        assertEquals(60, wheel.getCurrentSymbol().getSizePercent());
        wheel.setSymbol("pink");               // selected again: it shows again
        assertTrue(wheel.getCurrentSymbol().isShown());
        wheel.setSymbol("purple");
        assertTrue(wheel.isWild());
    }

    /** Should: a visible mixed machine give the same results as an invisible one */
    @Test
    public void mixedMachineShouldGiveSameResultsWhenVisible() {
        addAllTypes();
        machine.makeVisible();
        try {
            machine.spin(3, 1);
            machine.spin(4, 2);
            machine.spin(5, 1);
            assertTrue(machine.ok());
            assertArrayEquals(new String[]{"red", "red", "blue", "blue", "blue"},
                machine.configuration());
            machine.placeSymbol(1, "purple");
            machine.placeSymbol(2, "purple");
            assertTrue(machine.isJackpot());
        } finally {
            machine.makeInvisible();
        }
    }
}