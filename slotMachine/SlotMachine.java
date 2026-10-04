import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.Random;
import java.util.HashSet;
/**
 * Represents a slot machine simulator
 * The machine manages a dynamic set of wheels (between {@link #MIN_WHEELS} and {@link #MAX_WHEELS}),
 * a collection of distinct symbols (represented as colors), graphical elements (top, middle, and base rectangles),
 * and game state operations such as spinning, jackpot verification, and visibility toggling
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 * @version 12/09/26
 */
public class SlotMachine {

    /**
     * Minimum number of wheels permitted in the slot machine
     */
    public static final int MIN_WHEELS = 3;

    /**
     * Maximum number of wheels permitted in the slot machine
     */
    public static final int MAX_WHEELS = 50;

    private static final int DEFAULT_X = 70;
    private static final int DEFAULT_Y = 15;
    private static final int WHEEL_WIDTH = 40;
    private static final int WHEEL_SPACING = 10;
    private static final int MARGIN = 15;

    private static final int TOP_HEIGHT = 20;
    private static final int MIDDLE_HEIGHT = 150;
    private static final int BASE_HEIGHT = 20;
    private static final int INITIAL_Y = 50;
    private static final int CANVAS_MARGIN = 30;

    private static final int CANVAS_WIDTH = middleWidth(MAX_WHEELS) + 2 * CANVAS_MARGIN;
    private static final int CANVAS_HEIGHT = INITIAL_Y + TOP_HEIGHT + MIDDLE_HEIGHT + BASE_HEIGHT + CANVAS_MARGIN;
    private static final int CENTER_X = CANVAS_WIDTH / 2;

    private static final int TOP_OVERHANG = 20;
    private static final int BASE_OVERHANG = 20;

    private Rectangle topRectangle;
    private Rectangle middleRectangle;
    private Rectangle baseRectangle;

    private ArrayList<Wheel> wheelList;
    private ArrayList<String> symbols;
    private ArrayList<String> symbolTypes;
    
    private boolean ok;

    private int middleWidth;
    private int topWidth;
    private int baseWidth;

    private boolean visible;

    /**
     * Calculates the width of the middle section housing the given number of wheels
     *
     * @param numWheels The number of wheels
     * @return The calculated width in pixels
     */
    private static int middleWidth(int numWheels) {
        return (2 * MARGIN) + (numWheels * WHEEL_WIDTH) + ((numWheels - 1) * WHEEL_SPACING);
    }

    /**
     * Constructs a new SlotMachine that starts visible, as usual
     */
    public SlotMachine() {
        this(true);
    }

    /**
     * Constructs and initializes a new SlotMachine instance
     * Sets up the canvas, initializes default wheels ({@link #MIN_WHEELS}), builds the machine structure
     * (top, middle, base), and renders all components visibly if requested
     *
     * @param startVisible whether the canvas window should be shown immediately;
     *                     pass false to run the machine fully invisible from the
     *                     start (used by unit tests, so the window never appears
     *                     at all, not even briefly)
     */
    public SlotMachine(boolean startVisible) {
        Canvas.getCanvas(CANVAS_WIDTH, CANVAS_HEIGHT, startVisible);
        visible = startVisible;

        wheelList = new ArrayList<>();
        symbols = new ArrayList<>();
        symbolTypes = new ArrayList<>();
        ok = true;

        topRectangle = new Rectangle();
        middleRectangle = new Rectangle();
        baseRectangle = new Rectangle();

        updateWidths(MIN_WHEELS);

        topRectangle.changeSize(TOP_HEIGHT, topWidth);
        middleRectangle.changeSize(MIDDLE_HEIGHT, middleWidth);
        baseRectangle.changeSize(BASE_HEIGHT, baseWidth);

        topRectangle.changeColor("gold");
        middleRectangle.changeColor("gold");
        baseRectangle.changeColor("gold");

        placeOnAxis(topRectangle, topWidth, INITIAL_Y);
        placeOnAxis(middleRectangle, middleWidth, INITIAL_Y + TOP_HEIGHT);
        placeOnAxis(baseRectangle, baseWidth, INITIAL_Y + TOP_HEIGHT + MIDDLE_HEIGHT);

        if (startVisible) {
            topRectangle.makeVisible();
            middleRectangle.makeVisible();
            baseRectangle.makeVisible();
        }

        int yWheel = INITIAL_Y + TOP_HEIGHT + 15;
        for (int i = 1; i <= MIN_WHEELS; i++) {
            wheelList.add(createWheel("normal", wheelX(i), yWheel));     
        }
    }
    
    /**
     * Recalculates the dimensions for the middle, top, and base sections of the machine
     * based on the specified number of wheels
     *
     * @param numWheels The current number of wheels
     */
    private void updateWidths(int numWheels) {
        middleWidth = middleWidth(numWheels);
        topWidth = middleWidth + 2 * TOP_OVERHANG;
        baseWidth = middleWidth + 2 * BASE_OVERHANG;
    }

    /**
     * Positions a given rectangle centered along the horizontal axis {@code CENTER_X} at coordinate {@code y}
     *
     * @param r The rectangle to position
     * @param width The current width of the rectangle
     * @param y The Y-coordinate for the rectangle
     */
    private void placeOnAxis(Rectangle r, int width, int y) {
        int xDest = CENTER_X - (width / 2);
        r.moveHorizontal(xDest - DEFAULT_X);
        r.moveVertical(y - DEFAULT_Y);
    }

    /**
     * Computes the horizontal coordinate (X) for a wheel based on its 1-indexed position
     *
     * @param index The 1-based index of the wheel
     * @return The X-coordinate where the wheel should be placed
     */
    public int wheelX(int index) {
        int xStartMiddle = CENTER_X - (middleWidth / 2);
        return xStartMiddle + MARGIN + (index - 1) * (WHEEL_WIDTH + WHEEL_SPACING);
    }

    /**
     * Resizes the housing rectangles and repositions all existing wheels to keep them centered
     *
     * @param oldMiddleWidth The previous width of the middle section
     * @param oldBaseWidth The previous width of the base section
     */
    private void resizeStructure(int oldMiddleWidth, int oldBaseWidth) {
        int middleTopOffset = -(middleWidth - oldMiddleWidth) / 2;
        int baseOffset = -(baseWidth - oldBaseWidth) / 2;

        middleRectangle.changeSize(MIDDLE_HEIGHT, middleWidth);
        middleRectangle.moveHorizontal(middleTopOffset);

        topRectangle.changeSize(TOP_HEIGHT, topWidth);
        topRectangle.moveHorizontal(middleTopOffset);

        baseRectangle.changeSize(BASE_HEIGHT, baseWidth);
        baseRectangle.moveHorizontal(baseOffset);

        for (Wheel wheel : wheelList) {
            wheel.moveHorizontal(middleTopOffset);
        }
    }

    /**
     * Adds {@code pos} normal wheels to the slot machine, all at once
     * Same as {@code addWheel("normal", pos)}
     *
     * @param pos The number of wheels to add
     */
    public void addWheel(int pos) {
        addWheel("normal", pos);
    }

    /**
     * Adds {@code pos} new wheels of the given type, all at once
     * The whole operation fails (nothing is added) if the type is unknown or if the
     * resulting total would exceed {@link #MAX_WHEELS} When it fails and the machine
     * is visible, the user is warned with a dialog
     *
     * @param type the type of the new wheels: "normal", "lefty" or "rebel"
     * @param pos  The number of wheels to add
     */
    public void addWheel(String type, int pos) {
        if (pos < 1) {
            ok = false;
            return;
        }
        if (!isWheelType(type)) {
            showMessage("El tipo de rueda " + type + " no existe", "Error");
            ok = false;
            return;
        }
        if (wheelList.size() + pos > MAX_WHEELS) {
            showMessage("No puedes agregar " + pos + " ruedas (hay " + wheelList.size() +
                ", el maximo es " + MAX_WHEELS + ")", "Limite Maximo");
            ok = false;
            return;
        }
        for (int i = 0; i < pos; i++) {
            addOneWheel(type);
        }
        ok = true;
    }
    
    /**
     * Creates a symbol of the given type A new object is created every time, so
     * each wheel has its own copy To add a new type of symbol, add it here and in
     * {@link #isSymbolType(String)}
     *
     * @param type  the type of symbol: "normal", "ephemeral", "shy" or "joker"
     * @param color the color of the symbol
     * @return the new symbol, or null if the type is unknown
     */
    private Symbol createSymbol(String type, String color) {
        if (type.equals("normal")) {
            return new Symbol(color);
        }
        if (type.equals("ephemeral")) {
            return new EphemeralSymbol(color);
        }
        if (type.equals("shy")) {
            return new ShySymbol(color);
        }
        if (type.equals("joker")) {
            return new JokerSymbol(color);
        }
        return null;
    }

    /**
     * Tells whether a type of symbol exists
     *
     * @param type the type of symbol
     * @return true if the machine knows how to create that type
     */
    private boolean isSymbolType(String type) {
        if (type == null) {
            return false;
        }
        return type.equals("normal") || type.equals("ephemeral")
            || type.equals("shy") || type.equals("joker");
    }

    /**
     * Adds a single wheel of the given type at the end of the machine, populated
     * with all existing symbols, each wheel with its own copy of every symbol
     * Does not validate the type nor the limits: {@link #addWheel(String, int)}
     * is responsible for that
     *
     * @param type the type of the new wheel
     */
    private void addOneWheel(String type) {
        int oldMiddleWidth = middleWidth;
        int oldBaseWidth = baseWidth;

        updateWidths(wheelList.size() + 1);
        resizeStructure(oldMiddleWidth, oldBaseWidth);

        int yWheel = INITIAL_Y + TOP_HEIGHT + 15;
        int xNew = wheelX(wheelList.size() + 1);

        Wheel newWheel = createWheel(type, xNew, yWheel);
        for (int i = 0; i < symbols.size(); i++) {
            newWheel.addSymbol(i, createSymbol(symbolTypes.get(i), symbols.get(i)));
        }
        wheelList.add(newWheel);
    }
    
    /**
     * Creates a wheel of the given type, drawn only if the machine is visible
     * To add a new type of wheel, add it here and in {@link #isWheelType(String)}
     *
     * @param type the type of wheel: "normal", "lefty" or "rebel"
     * @param x    X-coordinate of the wheel
     * @param y    Y-coordinate of the wheel
     * @return the new wheel, or null if the type is unknown
     */
    private Wheel createWheel(String type, int x, int y) {
        if (type.equals("normal")) {
            return new Wheel(x, y, visible);
        }
        if (type.equals("lefty")) {
            return new LeftyWheel(x, y, visible);
        }
        if (type.equals("rebel")) {
            return new RebelWheel(x, y, visible);
        }
        return null;
    }

    /**
     * Tells whether a type of wheel exists
     *
     * @param type the type of wheel
     * @return true if the machine knows how to create that type
     */
    private boolean isWheelType(String type) {
        if (type == null) {
            return false;
        }
        return type.equals("normal") || type.equals("lefty") || type.equals("rebel");
    }
    
    
    /**
     * Returns the wheel to the left of the given one
     *
     * @param index 0-based index of a wheel
     * @return the wheel on its left, or null if it is the first one
     */
    private Wheel leftOf(int index) {
        if (index > 0) {
            return wheelList.get(index - 1);
        }
    
        return null;
    }
    
    /**
     * Removes the last pos wheels from the slot machine, all at once
     * The whole operation fails (nothing is removed) if the resulting total would go
     * below MIN_WHEELS, or if any of those wheels refuses to be deleted
     * When it fails and the machine is visible, the user is warned with a dialog
     *
     * @param pos The number of wheels to remove
     */
    public void delWheel(int pos) {
        if (pos < 1) {
            ok = false;
            return;
        }
        if (wheelList.size() - pos < MIN_WHEELS) {
            showMessage("No puedes eliminar " + pos + " ruedas (hay " + wheelList.size() +
                ", el minimo es " + MIN_WHEELS + ")", "Limite Minimo");
            ok = false;
            return;
        }
        for (int i = wheelList.size() - pos; i < wheelList.size(); i++) {
            if (!wheelList.get(i).canBeDeleted()) {
                showMessage("La rueda " + (i + 1) + " no se deja eliminar", "Error");
                ok = false;
                return;
            }
        }
        for (int i = 0; i < pos; i++) {
            delOneWheel();
        }
        ok = true;
    }

    /**
     * Removes the last wheel of the machine
     * Does not validate limits: {@link #delWheel(int)} is responsible for that
     */
    private void delOneWheel() {
        Wheel last = wheelList.remove(wheelList.size() - 1);
        last.makeInvisible();

        int oldMiddleWidth = middleWidth;
        int oldBaseWidth = baseWidth;
        updateWidths(wheelList.size());
        resizeStructure(oldMiddleWidth, oldBaseWidth);
    }

    /**
     * Returns the total number of wheels currently present in the slot machine
     *
     * @return The number of wheels
     */
    public int getWheels() {
        return wheelList.size();
    }

    /**
     * Adds a new normal symbol (color) at the specified position and inserts it
     * across all wheels Same as {@code addSymbol("normal", pos, color)}
     *
     * @param pos 1-based target insertion index for the symbol
     * @param color The name of the color symbol to add
     */
    public void addSymbol(int pos, String color) {
        addSymbol("normal", pos, color);
    }

    /**
     * Adds a new symbol of the given type at the specified position and inserts it
     * across all wheels Fails if the type is unknown or if the color already exists
     * in the machine; then {@link #ok()} will return {@code false}
     *
     * @param type the type of the new symbol: "normal", "ephemeral" or "shy"
     * @param pos 1-based target insertion index for the symbol
     * @param color The name of the color symbol to add
     */
    public void addSymbol(String type, int pos, String color) {
        if (!isSymbolType(type)) {
            showMessage("El tipo de simbolo " + type + " no existe", "Error");
            ok = false;
            return;
        }
        if (symbols.contains(color)) {
            ok = false;
            return;
        }
        int index = clamPos(pos, symbols.size() + 1) - 1;
        symbols.add(index, color);
        symbolTypes.add(index, type);
        for (Wheel wheel : wheelList) {
            wheel.addSymbol(index, createSymbol(type, color));
        }
        ok = true;
    }

    /**
     * Removes a symbol (color) from the slot machine and from every wheel
     * If the symbol is not found, the operation fails and {@link #ok()} will return {@code false}
     *
     * @param color The name of the color symbol to remove
     */
    public void delSymbol(String color) {
        int index = symbols.indexOf(color);
        if (index == -1) {
            ok = false;
            return;
        }
        symbols.remove(index);
        symbolTypes.remove(index);
        for (Wheel wheel : wheelList) {
            wheel.delSymbol(color);
        }
        ok = true;
    }

    /**
     * Clamps a position value to ensure it lies strictly within the range {@code [1, max]}
     *
     * @param pos The raw position input
     * @param max The maximum allowable value
     * @return The clamped position within {@code [1, max]}
     */
    private int clamPos(int pos, int max) {
        if (pos < 1) return 1;
        if (pos > max) return max;
        return pos;
    }

    /**
     * Manually sets the visible symbol on a specific wheel
     * Checks if the symbol is valid and triggers jackpot verification
     * If the symbol does not exist and the machine is visible, the user is warned with a dialog
     *
     * @param wheel 1-based index of the target wheel
     * @param symbol The color symbol to display
     */
    public void placeSymbol(int wheel, String symbol) {
        if (!symbols.contains(symbol)) {
            ok = false;
            showMessage("El simbolo " + symbol + " no existe", "Error");
            return;
        }
        int index = clamPos(wheel, wheelList.size()) - 1;
        wheelList.get(index).setSymbol(symbol);
        checkJackPot();
        ok = true;
    }

    /**
     * Randomly spins a specific wheel a random number of steps (1 to 15)
     * and checks for a jackpot condition Fails if the wheel is locked
     *
     * @param wheel 1-based index of the wheel to spin
     */
    public void spin(int wheel) {
        if (wheelList.isEmpty() || symbols.isEmpty()) {
            ok = false;
            return;
        }
        int index = clamPos(wheel, wheelList.size()) - 1;
        Wheel target = wheelList.get(index);
        if (target.isLocked()) {
            ok = false;
            return;
        }
        Random r = new Random();
        int turns = r.nextInt(15) + 1;
        target.spin(leftOf(index), turns, visible);
        checkJackPot();
        ok = true;
    }

    /**
     * Randomly spins all unlocked wheels, from left to right, and checks for a
     * jackpot condition
     */
    public void spin() {
        if (wheelList.isEmpty() || symbols.isEmpty()) {
            ok = false;
            return;
        }
        Random r = new Random();
        for (int i = 0; i < wheelList.size(); i++) {
            Wheel w = wheelList.get(i);
            if (!w.isLocked()) {
                w.spin(leftOf(i), r.nextInt(15) + 1, visible);
            }
        }
        checkJackPot();
        ok = true;
    }

    /**
     * Returns an array of all registered symbols in their insertion order
     *
     * @return An array of symbol strings
     */
    public String[] symbols() {
        ok = true;
        return symbols.toArray(new String[0]);
    }

    /**
     * Returns how many different symbols are currently visible across all wheels,
     * as the friend in the contest would see them This is not the catalog size:
     * for that use {@code symbols().length} A wheel that shows a joker does not
     * add a symbol; if there are only jokers, the answer is 1
     *
     * @return number of distinct visible symbols (0 if none is shown)
     */
    public int distinctSymbols() {
        HashSet<String> visibleNow = new HashSet<>();
        boolean anyWild = false;
        for (Wheel w : wheelList) {
            String s = w.getVisibleSymbol();
            if (s != null) {
                if (w.isWild()) {
                    anyWild = true;
                } else {
                    visibleNow.add(s);
                }
            }
        }
        ok = true;
        if (visibleNow.isEmpty() && anyWild) {
            return 1;
        }
        return visibleNow.size();
    }

    /**
     * Returns the array of visible symbols across all wheels from left to right
     *
     * @return An array containing the visible symbol string of each wheel
     */
    public String[] configuration() {
        String[] conf = new String[wheelList.size()];
        for (int i = 0; i < wheelList.size(); i++) {
            conf[i] = wheelList.get(i).getVisibleSymbol();
        }
        ok = true;
        return conf;
    }

    /**
     * Determines whether the current configuration constitutes a jackpot: all the
     * wheels that do not show a joker show the same symbol A joker counts as any
     * color, so a machine that shows only jokers is also a jackpot
     *
     * @return {@code true} if there is a jackpot; {@code false} otherwise
     */
    public boolean isJackpot() {
        if (wheelList.isEmpty() || symbols.isEmpty()) {
            ok = false;
            return false;
        }
        String first = null;
        for (int i = 0; i < wheelList.size(); i++) {
            Wheel w = wheelList.get(i);
            String color = w.getVisibleSymbol();
            if (color == null) {
                ok = true;
                return false;
            }
            if (!w.isWild()) {
                if (first == null) {
                    first = color;
                } else if (!first.equals(color)) {
                    ok = true;
                    return false;
                }
            }
        }
        ok = true;
        return true;
    }

    /**
     * Verifies the jackpot status and changes the color of the top and base housing:
     * magenta on jackpot win, gold otherwise
     */
    private void checkJackPot() {
        if (isJackpot()) {
            topRectangle.changeColor("magenta");
            baseRectangle.changeColor("magenta");
        } else {
            topRectangle.changeColor("gold");
            baseRectangle.changeColor("gold");
        }
    }

    /**
     * Makes all graphical components of the slot machine visible and shows
     * the canvas window if it was hidden
     */
    public void makeVisible() {
        visible = true;
        topRectangle.makeVisible();
        middleRectangle.makeVisible();
        baseRectangle.makeVisible();
        for (Wheel w : wheelList) {
            w.makeVisible();
        }
        Canvas.getCanvas().setVisible(true);
        ok = true;
    }
    
    /**
     * Hides all graphical components of the slot machine and the canvas window
     * From now on, operations draw nothing and never wait
     */
    public void makeInvisible() {
        visible = false;
        Canvas.getCanvas().setVisible(false);
        topRectangle.makeInvisible();
        middleRectangle.makeInvisible();
        baseRectangle.makeInvisible();
        for (Wheel w : wheelList) {
            w.makeInvisible();
        }
        ok = true;
    }

    /**
     * Exits the slot machine application
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * Returns the status of the last executed operation
     *
     * @return {@code true} if the last operation succeeded, {@code false} otherwise
     */
    public boolean ok() {
        return ok;
    }

    /**
     * Swaps the currently visible symbols between two wheels
     * Fails if either wheel is locked or refuses to be swapped
     *
     * @param wheel1 1-based index of the first wheel
     * @param wheel2 1-based index of the second wheel
     */
    public void swap(int wheel1, int wheel2) {
        if (wheelList.isEmpty()) {
            ok = false;
            return;
        }
        int i1 = clamPos(wheel1, wheelList.size()) - 1;
        int i2 = clamPos(wheel2, wheelList.size()) - 1;
        Wheel w1 = wheelList.get(i1);
        Wheel w2 = wheelList.get(i2);

        if (w1.isLocked() || w2.isLocked()) {
            ok = false;
            return;
        }
        if (!w1.canBeSwapped() || !w2.canBeSwapped()) {
            showMessage("Una de las ruedas no se deja intercambiar", "Error");
            ok = false;
            return;
        }

        String s1 = w1.getVisibleSymbol();
        String s2 = w2.getVisibleSymbol();
        w1.setSymbol(s2);
        w2.setSymbol(s1);
        checkJackPot();
        ok = true;
    }

        /**
     * Locks a wheel, protecting it from any spin or swap operation
     * Fails if the wheel refuses to be locked
     *
     * @param wheel 1-based index of the wheel to lock
     */
    public void lock(int wheel) {
        if (wheelList.isEmpty()) {
            ok = false;
            return;
        }
        int index = clamPos(wheel, wheelList.size()) - 1;
        Wheel target = wheelList.get(index);
        if (!target.canBeLocked()) {
            showMessage("La rueda " + (index + 1) + " no se deja bloquear", "Error");
            ok = false;
            return;
        }
        target.lock();
        ok = true;
    }

    /**
     * Unlocks a wheel, letting it spin or swap normally again
     *
     * @param wheel 1-based index of the wheel to unlock
     */
    public void unlock(int wheel) {
        if (wheelList.isEmpty()) {
            ok = false;
            return;
        }
        int index = clamPos(wheel, wheelList.size()) - 1;
        wheelList.get(index).unlock();
        ok = true;
    }

    /**
     * Rotates a specific wheel by an exact number of steps (not random)
     * Steps may be negative (rotate backwards) or very large: they are reduced
     * modulo the number of symbols If the machine is visible, the rotation is
     * shown step by step Fails if the wheel is locked
     *
     * @param wheel 1-based index of the wheel to spin
     * @param steps number of positions to rotate
     */
    public void spin(int wheel, int steps) {
        if (wheelList.isEmpty() || symbols.isEmpty()) {
            ok = false;
            return;
        }
        int index = clamPos(wheel, wheelList.size()) - 1;
        Wheel target = wheelList.get(index);
        if (target.isLocked()) {
            ok = false;
            return;
        }
        target.spin(leftOf(index), steps, visible);
        checkJackPot();
        ok = true;
    }

    /**
     * Leaves the machine showing exactly the given configuration: wheel i displays
     * setSymbols[i-1] Fails entirely if the array length doesn't match the current
     * number of wheels, or if any given symbol isn't in the machine's catalog
     * Locked wheels keep their current symbol, ignoring what the array says for them
     *
     * @param setSymbols desired visible symbol for each wheel, left to right
     */
    public void spin(String[] setSymbols) {
        if (setSymbols == null || setSymbols.length != wheelList.size()) {
            ok = false;
            return;
        }
        for (String s : setSymbols) {
            if (!symbols.contains(s)) {
                ok = false;
                return;
            }
        }
        for (int i = 0; i < wheelList.size(); i++) {
            if (!wheelList.get(i).isLocked()) {
                wheelList.get(i).setSymbol(setSymbols[i]);
            }
        }
        checkJackPot();
        ok = true;
    }
    // CIclo 3
    /** Colors used as symbols by {@link #SlotMachine(int)}: 50 different CSS color names */
    private static final String[] PALETTE = {
        "red", "blue", "green", "yellow", "orange", "purple", "pink", "brown", "cyan", "lime",
        "navy", "teal", "maroon", "olive", "coral", "salmon", "crimson", "indigo", "violet", "turquoise",
        "tan", "khaki", "orchid", "plum", "tomato", "chocolate", "sienna", "peru", "skyblue", "steelblue",
        "royalblue", "dodgerblue", "slateblue", "seagreen", "forestgreen", "limegreen", "springgreen",
        "chartreuse", "darkorange", "hotpink", "deeppink", "firebrick", "darkred", "darkgreen",
        "darkblue", "aquamarine", "lavender", "beige", "mediumpurple", "goldenrod"
    };
    /**
     * Constructs an invisible machine with n wheels and n different symbols, as in the
     * contest Every wheel has the same symbols in the same order, and the initial
     * configuration is random and never a jackpot (so there are always at least two
     * different visible symbols)
     * If n is outside [{@link #MIN_WHEELS}, {@link #MAX_WHEELS}] it is adjusted to the
     * nearest valid value and {@link #ok()} returns {@code false}
     *
     * @param n number of wheels and of symbols
     */
    public SlotMachine(int n) {
        this(false);
        int size = Math.max(MIN_WHEELS, Math.min(n, MAX_WHEELS));
        if (size > MIN_WHEELS) {
            addWheel(size - MIN_WHEELS);
        }
        for (int i = 1; i <= size; i++) {
            addSymbol(i, PALETTE[i - 1]);
        }
        randomizeConfiguration();
        ok = (size == n);
    }
    
    /**
     * Leaves every wheel showing a random symbol, repeating until the result
     * is not a jackpot
     */
    private void randomizeConfiguration() {
        Random r = new Random();
        String[] target = new String[wheelList.size()];
        do {
            for (int i = 0; i < target.length; i++) {
                target[i] = symbols.get(r.nextInt(symbols.size()));
            }
            spin(target);
        } while (isJackpot());
    }
    
    /**
     * Shows a warning dialog to the user, but only if the machine is visible
     * An invisible machine (unit tests, the contest solver) never opens dialogs
     *
     * @param message text of the warning
     * @param title   title of the dialog window
     */
    private void showMessage(String message, String title) {
        if (visible) {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
        }
    }
}