import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.Random;

/**
 * Represents a slot machine simulator.
 * The machine manages a dynamic set of wheels (between {@link #MIN_WHEELS} and {@link #MAX_WHEELS}),
 * a collection of distinct symbols (represented as colors), graphical elements (top, middle, and base rectangles),
 * and game state operations such as spinning, jackpot verification, and visibility toggling.
 * 
 * @author Mateo
 * @author Maria Angelica
 * @version 22/08/26
 */
 
public class SlotMachine {
    
    /**
     * Minimum number of wheels permitted in the slot machine.
     */
     
    public static final int MIN_WHEELS = 3;

    /**
     * Maximum number of wheels permitted in the slot machine.
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

    private Rectangle topRectangle;
    private Rectangle middleRectangle;
    private Rectangle baseRectangle;
    private static final int TOP_OVERHANG = 20;
    private static final int BASE_OVERHANG = 20;
    private ArrayList<Wheel> wheelList;
    private ArrayList<String> symbols;
    private boolean ok;
    private int middleWidth;
    private int topWidth;
    private int baseWidth;

    /**
     * Calculates the width of the middle section housing the given number of wheels.
     * 
     * @param numWheels The number of wheels.
     * @return The calculated width in pixels.
     */
     
    private static int middleWidth(int numWheels) {
        return (2 * MARGIN) + (numWheels * WHEEL_WIDTH) + ((numWheels - 1) * WHEEL_SPACING);
    }

    /**
     * Constructs and initializes a new SlotMachine instance.
     * Sets up the canvas, initializes default wheels ({@link #MIN_WHEELS}), builds the machine structure
     * (top, middle, base), and renders all components visibly.
     */
     
    public SlotMachine() {
        Canvas.getCanvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        wheelList = new ArrayList<>();
        symbols = new ArrayList<>();
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

        topRectangle.makeVisible();
        middleRectangle.makeVisible();
        baseRectangle.makeVisible();

        int yWheel = INITIAL_Y + TOP_HEIGHT + 15;
        for (int i = 1; i <= MIN_WHEELS; i++) {
            wheelList.add(new Wheel(wheelX(i), yWheel));
        }
    }

    /**
     * Recalculates the dimensions for the middle, top, and base sections of the machine
     * based on the specified number of wheels.
     * 
     * @param numWheels The current number of wheels.
     */
     
    private void updateWidths(int numWheels) {
        middleWidth = middleWidth(numWheels);
        topWidth = middleWidth + 2 * TOP_OVERHANG;
        baseWidth = middleWidth + 2 * BASE_OVERHANG;
    }

    /**
     * Positions a given rectangle centered along the horizontal axis {@code CENTER_X} at coordinate {@code y}.
     * 
     * @param r The rectangle to position.
     * @param width The current width of the rectangle.
     * @param y The Y-coordinate for the rectangle.
     */
     
    private void placeOnAxis(Rectangle r, int width, int y) {
        int xDest = CENTER_X - (width / 2);
        r.moveHorizontal(xDest - DEFAULT_X);
        r.moveVertical(y - DEFAULT_Y);
    }

    /** 
     * Computes the horizontal coordinate (X) for a wheel based on its 1-indexed position.
     * 
     * @param index The 1-based index of the wheel.
     * @return The X-coordinate where the wheel should be placed.
     */
     
    public int wheelX(int index) {
        int xStartMiddle = CENTER_X - (middleWidth / 2);
        return xStartMiddle + MARGIN + (index - 1) * (WHEEL_WIDTH + WHEEL_SPACING);
    }

    /** 
     * Resizes the housing rectangles and repositions all existing wheels to keep them centered.
     * 
     * @param oldMiddleWidth The previous width of the middle section.
     * @param oldBaseWidth The previous width of the base section.
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
     * Adds a new wheel to the slot machine.
     * If the machine has not reached {@link #MAX_WHEELS}, expands the structure, instantiates
     * the new wheel, and populates it with all currently registered symbols.
     * 
     * @param pos Desired insertion position (currently appends to the end).
     */
     
    public void addWheel(int pos) {
        if (wheelList.size() < MAX_WHEELS) {
            int oldMiddleWidth = middleWidth;
            int oldBaseWidth = baseWidth;

            updateWidths(wheelList.size() + 1);
            resizeStructure(oldMiddleWidth, oldBaseWidth);

            int yWheel = INITIAL_Y + TOP_HEIGHT + 15;
            int xNew = wheelX(wheelList.size() + 1);
            
            Wheel newWheel = new Wheel(xNew, yWheel);
            for(int i = 0; i < symbols.size(); i++){
                newWheel.addSymbol(i, symbols.get(i));
            }
            wheelList.add(newWheel);
            
        } else {
            JOptionPane.showMessageDialog(null,
                "No puedes agregar mas ruedas. Maximo permitido: " + MAX_WHEELS,
                "Limite Maximo",
                JOptionPane.WARNING_MESSAGE);
        }   
    }

    /**
     * Removes a wheel from the slot machine.
     * As long as the number of wheels is strictly greater than {@link #MIN_WHEELS}, removes the last wheel,
     * hides it, and adjusts the structure size accordingly.
     * 
     * @param pos Position of the wheel to delete.
     */
     
    public void delWheel(int pos) {
        if (wheelList.size() > MIN_WHEELS) {
            Wheel last = wheelList.remove(wheelList.size() - 1);
            last.makeInvisible();

            int oldMiddleWidth = middleWidth;
            int oldBaseWidth = baseWidth;

            updateWidths(wheelList.size());
            resizeStructure(oldMiddleWidth, oldBaseWidth);
        } else {
            JOptionPane.showMessageDialog(null,
                "No puedes eliminar mas ruedas. Minimo permitido: " + MIN_WHEELS,
                "Limite Minimo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Returns the total number of wheels currently present in the slot machine.
     * 
     * @return The number of wheels.
     */
     
    public int getWheels() {
        return wheelList.size();
    }

    /**
     * Adds a new symbol (color) at the specified position and inserts it across all wheels.
     * If the color already exists in the machine, the operation fails and {@link #ok()} will return {@code false}.
     * 
     * @param pos 1-based target insertion index for the symbol.
     * @param color The name of the color symbol to add.
     */
     
    public void addSymbol(int pos, String color){
        if(symbols.contains(color)){
            ok = false;
            return;
        }
        int index = clamPos(pos, symbols.size() + 1) -1;
        symbols.add(index, color);
        for(Wheel wheel : wheelList){
            wheel.addSymbol(index, color);
        }
        ok = true;
    }

    /**
     * Removes a symbol (color) from the slot machine and from every wheel.
     * If the symbol is not found, the operation fails and {@link #ok()} will return {@code false}.
     * 
     * @param color The name of the color symbol to remove.
     */
     
    public void delSymbol(String color){
        int index = symbols.indexOf(color);
        if(index == -1) {
            ok = false; return;
        }
        symbols.remove(index);
        for (Wheel wheel : wheelList){
            wheel.delSymbol(color);
        }
        ok = true;
    }

    /**
     * Clamps a position value to ensure it lies strictly within the range {@code [1, max]}.
     * 
     * @param pos The raw position input.
     * @param max The maximum allowable value.
     * @return The clamped position within {@code [1, max]}.
     */
     
    private int clamPos(int pos, int max){
        if(pos < 1 ) return 1;
        if(pos > max) return max;
        return pos;
    }
    
    /**
     * Manually sets the visible symbol on a specific wheel.
     * Checks if the symbol is valid and triggers jackpot verification.
     * 
     * @param wheel 1-based index of the target wheel.
     * @param symbol The color symbol to display.
     */
     
    public void placeSymbol(int wheel, String symbol){
        if(!symbols.contains(symbol)){
            ok = false;
            JOptionPane.showMessageDialog(null, "El simbolo " + symbol + "no existe", "Error", JOptionPane.WARNING_MESSAGE); 
            return;
        }
        int index = clamPos(wheel, wheelList.size())-1;
        wheelList.get(index).setSymbol(symbol);
        checkJackPot();
        ok = true;
    }

    /**
     * Randomly spins a specific wheel by rotating it a random number of steps (1 to 15)
     * and checks for a jackpot condition.
     * 
     * @param wheel 1-based index of the wheel to spin.
     */
     
    public void spin(int wheel) {
        if(wheelList.isEmpty() || symbols.isEmpty()){
            ok = false;
            return;
        }
        int index = clamPos(wheel, wheelList.size()) -1;
        Random r = new Random();
        int turns = r.nextInt(15)+1;
        wheelList.get(index).rotate(turns);
        checkJackPot();
        ok = true;
    }

    /**
     * Randomly spins all wheels in the slot machine and checks for a jackpot condition.
     */
     
    public void spin() {
        if (wheelList.isEmpty() || symbols.isEmpty()){
            ok = false;
            return;
        }
        Random r = new Random();
        for(Wheel i : wheelList){
            int turns = r.nextInt(15)+1;
            i.rotate(turns);
        }
        checkJackPot();
        ok = true;
    }

    /**
     * Returns an array of all registered symbols in their insertion order.
     * 
     * @return An array of symbol strings.
     */
     
    public String[] symbols(){
        ok = true;
        return symbols.toArray(new String[0]);
    }
    
    /**
     * Returns the total count of distinct symbols registered in the machine.
     * 
     * @return The number of distinct symbols.
     */
     
    public int distinctSymbols() {
        ok = true;
        return symbols.size();
    }

    /**
     * Returns the array of visible symbols across all wheels from left to right.
     * 
     * @return An array containing the visible symbol string of each wheel.
     */
     
    public String[] configuration() {
        String[] conf = new String[wheelList.size()];
        for(int i = 0; i < wheelList.size(); i++){
            conf[i] = (String) wheelList.get(i).getVisibleSymbol();
        }
        ok = true;
        return conf;
    }

    /**
     * Determines whether the current configuration constitutes a jackpot (all visible symbols match).
     * 
     * @return {@code true} if all wheels display identical non-null symbols; {@code false} otherwise.
     */
     
    public boolean isJackpot() {
        if (wheelList.isEmpty() || symbols.isEmpty()){
            ok = false;
            return false;
        }
        String[] conf = configuration();
        String first = conf[0];
        if(first == null){
            ok = true;
            return false;
        }
        for(int i = 0; i < conf.length; i++){
            if(conf[i] == null || !conf[i].equals(first)){
                ok = true;
                return false;
            }
        }
        ok = true;
        return true;
    }

    /**
     * Verifies the jackpot status and changes the color of the top and base housing:
     * magenta on jackpot win, gold otherwise.
     */
     
    private void checkJackPot(){
        if(isJackpot()){
            topRectangle.changeColor("magenta");
            baseRectangle.changeColor("magenta");
        } else{
            topRectangle.changeColor("gold");
            baseRectangle.changeColor("gold");
        }
    }

    /**
     * Makes all graphical components of the slot machine visible on the canvas.
     */
     
    public void makeVisible(){
        topRectangle.makeVisible();
        middleRectangle.makeVisible();
        baseRectangle.makeVisible();
        for(Wheel i : wheelList){
            i.makeVisible();
        }
        ok = true;
    }
    
    /**
     * Hides all graphical components of the slot machine from the canvas.
     */
     
    public void makeInvisible() {
        topRectangle.makeInvisible();
        middleRectangle.makeInvisible();
        baseRectangle.makeInvisible();
        for(Wheel i : wheelList){
            i.makeInvisible();
        }
        ok = true;
    }

    /**
     * Exits the slot machine application.
     */
     
    public void exit() {
        System.exit(0);
    }

    /**
     * Returns the status of the last executed operation.
     * 
     * @return {@code true} if the last operation succeeded, {@code false} otherwise.
     */
     
    public boolean ok() {
        return ok;
    }
}