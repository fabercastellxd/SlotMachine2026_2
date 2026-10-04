import java.util.ArrayList;

/**
 * Represents an individual wheel (reel) in a slot machine
 * Visually, the wheel is a fixed-size window on the canvas through which
 * two stacked color blocks scroll vertically to simulate a real slot reel:
 * one block holds the currently visible symbol and the other holds the
 * incoming symbol during an animated spin
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 * @version 4.0
 */
public class Wheel {
    public static final int HEIGHT = 120;
    public static final int WIDTH = 40;
    private static final int DEFAULT_X = 70;
    private static final int DEFAULT_Y = 15;

    /** Pixels moved per animation frame; smaller = smoother but slower */
    private static final int SCROLL_STEP_PX = 15;

    /** Side in pixels of the smallest size of a symbol: a dot */
    private static final int DOT_PX = 4;

    /** Side in pixels of the square that tells the type of the visible symbol */
    private static final int MARK_SIZE = 10;

    /** Distance in pixels from the corner of the window to the mark */
    private static final int MARK_OFFSET = 2;

    private Rectangle currentBlock;
    private Rectangle nextBlock;
    private Rectangle mark;
    private ArrayList<Symbol> symbols;
    private int currentIndex = -1;

    private boolean locked = false;
    private boolean visible;

    private int windowX;
    private int windowY;
    private int currentBlockY;
    private int nextBlockY;

    /**
     * Constructs a new visible Wheel at the specified coordinates on the canvas
     *
     * @param x The initial X-coordinate on the canvas
     * @param y The initial Y-coordinate on the canvas
     */
    public Wheel(int x, int y) {
        this(x, y, true);
    }

    /**
     * Constructs a new Wheel at the specified coordinates on the canvas
     * An invisible wheel draws nothing and never waits, so it costs almost
     * nothing to create and to update
     *
     * @param x       The initial X-coordinate on the canvas
     * @param y       The initial Y-coordinate on the canvas
     * @param visible Whether the wheel is drawn on the canvas from the start
     */
    public Wheel(int x, int y, boolean visible) {
        this.visible = visible;
        symbols = new ArrayList<>();
        windowX = x;
        windowY = y;

        currentBlock = new Rectangle();
        currentBlock.changeSize(HEIGHT, WIDTH);
        currentBlock.changeColor("gray");
        currentBlock.moveHorizontal(x - DEFAULT_X);
        currentBlock.moveVertical(y - DEFAULT_Y);
        currentBlockY = y;
        currentBlock.setClip(windowX, windowY, WIDTH, HEIGHT);
        if (visible) {
            currentBlock.makeVisible();
        }

        nextBlock = new Rectangle();
        nextBlock.changeSize(HEIGHT, WIDTH);
        nextBlock.changeColor("gray");
        nextBlock.moveHorizontal(x - DEFAULT_X);
        nextBlock.moveVertical(y - DEFAULT_Y);
        nextBlockY = y;
        nextBlock.setClip(windowX, windowY, WIDTH, HEIGHT);

        mark = new Rectangle();
        mark.changeSize(MARK_SIZE, MARK_SIZE);
        mark.moveHorizontal(x + MARK_OFFSET - DEFAULT_X);
        mark.moveVertical(y + MARK_OFFSET - DEFAULT_Y);
    }

    /**
     * Moves the wheel horizontally by a given distance, keeping the
     * visible window (clip) aligned with both blocks and the mark The mark is
     * drawn again at the end, so it stays on top of the block
     *
     * @param distance The distance in pixels to move horizontally
     */
    public void moveHorizontal(int distance) {
        currentBlock.moveHorizontal(distance);
        nextBlock.moveHorizontal(distance);
        mark.moveHorizontal(distance);
        windowX += distance;
        updateClip();
        showMark();
    }

    /**
     * Clips the next block to the whole window and the current block to the
     * region that its symbol occupies
     */
    private void updateClip() {
        nextBlock.setClip(windowX, windowY, WIDTH, HEIGHT);
        clipCurrentBlock();
    }

    /**
     * Clips both blocks to the whole window, as needed while they scroll
     */
    private void fullClip() {
        currentBlock.setClip(windowX, windowY, WIDTH, HEIGHT);
        nextBlock.setClip(windowX, windowY, WIDTH, HEIGHT);
    }

    /**
     * Clips the current block to the region that its symbol occupies: the whole
     * window for a normal symbol, a smaller region centered in the window for a
     * symbol that has shrunk, and nothing for a symbol that is not shown
     */
    private void clipCurrentBlock() {
        int w = WIDTH;
        int h = HEIGHT;
        if (currentIndex != -1) {
            Symbol symbol = symbols.get(currentIndex);
            if (symbol.isShown()) {
                w = Math.max(DOT_PX, WIDTH * symbol.getSizePercent() / 100);
                h = Math.max(DOT_PX, HEIGHT * symbol.getSizePercent() / 100);
            } else {
                w = 0;
                h = 0;
            }
        }
        currentBlock.setClip(windowX + (WIDTH - w) / 2, windowY + (HEIGHT - h) / 2, w, h);
    }

    /**
     * Draws the mark of the visible symbol on top of the window, or hides it
     * if the symbol has no mark or the wheel is not visible
     */
    private void showMark() {
        String markColor = null;
        if (currentIndex != -1) {
            markColor = symbols.get(currentIndex).getMarkColor();
        }
        if (markColor == null || !visible) {
            mark.makeInvisible();
        } else {
            mark.changeColor(markColor);
            mark.makeVisible();
        }
    }

    /** Hides both blocks of the wheel and its mark */
    public void makeInvisible() {
        visible = false;
        currentBlock.makeInvisible();
        nextBlock.makeInvisible();
        mark.makeInvisible();
    }

    /** Makes the visible block of the wheel visible again, with its mark */
    public void makeVisible() {
        visible = true;
        currentBlock.makeVisible();
        showMark();
    }

    /**
     * Adds a normal symbol (color) to the wheel at the specified index
     *
     * @param index The 0-based position where the symbol should be inserted
     * @param color The name of the color representing the symbol
     */
    public void addSymbol(int index, String color) {
        addSymbol(index, new Symbol(color));
    }

    /**
     * Adds a symbol to the wheel at the specified index Each wheel must receive
     * its own symbol object, never one shared with another wheel
     *
     * @param index  The 0-based position where the symbol should be inserted
     * @param symbol The symbol to add
     */
    public void addSymbol(int index, Symbol symbol) {
        symbols.add(index, symbol);
        if (currentIndex == -1) {
            currentIndex = 0;
        } else if (index <= currentIndex) {
            currentIndex++;
        }
        showCurrentSymbol();
    }

    /**
     * Removes the first symbol with the given color from the wheel
     *
     * @param color The name of the color of the symbol to remove
     */
    public void delSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) return;

        symbols.remove(index);

        if (symbols.isEmpty()) {
            currentIndex = -1;
        } else {
            if (index <= currentIndex) {
                currentIndex--;
            }
            if (currentIndex < 0) {
                currentIndex = 0;
            } else if (currentIndex >= symbols.size()) {
                currentIndex = symbols.size() - 1;
            }
        }
        showCurrentSymbol();
    }

    /**
     * Instantly repaints the window with the current symbol: its color, its size,
     * whether it is shown, and its mark There is no scroll animation Used
     * whenever the change doesn't need (or isn't allowed) to be animated
     */
    private void showCurrentSymbol() {
        String color = currentIndex == -1 ? "gray" : symbols.get(currentIndex).getColor();
        moveBlockTo(currentBlock, currentBlockY, windowY);
        currentBlockY = windowY;
        currentBlock.changeColor(color);
        nextBlock.makeInvisible();
        clipCurrentBlock();
        showMark();
    }

    private void moveBlockTo(Rectangle block, int fromY, int toY) {
        if (fromY != toY) {
            block.moveVertical(toY - fromY);
        }
    }

    /**
     * Sets the visible symbol of the wheel to the one with the given color, if it
     * exists The symbol gets selected
     *
     * @param color The name of the color of the symbol to display
     */
    public void setSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) return;
        currentIndex = index;
        symbols.get(currentIndex).onSelected();
        showCurrentSymbol();
    }

    /**
     * Looks for the symbol with the given color
     *
     * @param color The name of the color
     * @return the 0-based index of the symbol, or -1 if there is none
     */
    private int indexOfColor(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Rotates the visible symbol by shifting {@code x} positions, instantly
     * (no scroll animation) Supports both positive and negative steps
     *
     * @param x The number of positions to rotate
     */
    public void rotate(int x) {
        if (symbols.isEmpty()) return;
        currentIndex = (currentIndex + x) % symbols.size();
        if (currentIndex < 0) {
            currentIndex = (currentIndex + symbols.size()) % symbols.size();
        }
        showCurrentSymbol();
    }

    /**
     * Rotates the wheel {@code steps} positions Negative steps rotate backwards
     * The steps are first reduced modulo the number of symbols, so even a huge
     * number of steps costs at most one full turn When {@code animate} is true,
     * the wheel takes the shortest way (forward or backward) and shows it as a
     * vertical scroll, one symbol at a time If the wheel moves, the symbol it
     * stops on gets selected
     *
     * @param steps   number of positions to rotate (may be negative or very large)
     * @param animate whether to show the scroll animation for each step
     */
    public void rotate(int steps, boolean animate) {
        if (symbols.isEmpty()) return;
        int size = symbols.size();
        int forward = Math.floorMod(steps, size);
        if (animate) {
            int backward = size - forward;
            if (forward <= backward) {
                for (int i = 0; i < forward; i++) {
                    animateOneStep(1);
                }
            } else {
                for (int i = 0; i < backward; i++) {
                    animateOneStep(-1);
                }
            }
        } else {
            currentIndex = (currentIndex + forward) % size;
        }
        if (forward != 0) {
            symbols.get(currentIndex).onSelected();
        }
        showCurrentSymbol();
    }

    /**
     * Performs one full scroll cycle in the given direction, then swaps the block
     * roles so the wheel is ready for the next step Forward (+1): the next symbol
     * comes in from below while the current one exits above Backward (-1): the
     * previous symbol comes in from above while the current one exits below
     * While it scrolls, symbols are drawn at full size
     *
     * @param direction +1 to scroll to the next symbol, -1 to scroll to the previous one
     */
    private void animateOneStep(int direction) {
        fullClip();
        int previousIndex = currentIndex;
        int newIndex = Math.floorMod(currentIndex + direction, symbols.size());

        moveBlockTo(currentBlock, currentBlockY, windowY);
        currentBlockY = windowY;
        currentBlock.changeColor(previousIndex == -1 ? "gray" : symbols.get(previousIndex).getColor());

        int entryY = windowY + direction * HEIGHT;
        moveBlockTo(nextBlock, nextBlockY, entryY);
        nextBlockY = entryY;
        nextBlock.changeColor(symbols.get(newIndex).getColor());
        nextBlock.makeVisible();

        int remaining = HEIGHT;
        while (remaining > 0) {
            int delta = direction * Math.min(SCROLL_STEP_PX, remaining);
            currentBlock.moveVertical(-delta);
            currentBlockY -= delta;
            nextBlock.moveVertical(-delta);
            nextBlockY -= delta;
            remaining -= Math.abs(delta);
        }

        currentBlock.makeInvisible();
        Rectangle exited = currentBlock;
        int exitedY = currentBlockY;
        currentBlock = nextBlock;
        currentBlockY = nextBlockY;
        nextBlock = exited;
        nextBlockY = exitedY;
        nextBlock.makeInvisible();

        currentIndex = newIndex;
    }

    /**
     * Returns the color name of the currently visible symbol A symbol that is
     * not shown (a hidden shy symbol) still reports its color
     *
     * @return The color of the visible symbol, or {@code null} if no symbols exist
     */
    public String getVisibleSymbol() {
        if (currentIndex == -1 || symbols.isEmpty()) {
            return null;
        }
        return symbols.get(currentIndex).getColor();
    }

    /**
     * Returns the symbol object that the wheel is showing
     *
     * @return The current symbol, or {@code null} if no symbols exist
     */
    public Symbol getCurrentSymbol() {
        if (currentIndex == -1) {
            return null;
        }
        return symbols.get(currentIndex);
    }

    /**
     * Tells whether the symbol that the wheel is showing is wild (a joker)
     *
     * @return true if the wheel shows a wild symbol; false if it shows a normal one or none
     */
    public boolean isWild() {
        if (currentIndex == -1) {
            return false;
        }
        return symbols.get(currentIndex).isWild();
    }

    /**
     * Returns the 1-based indicator position of the currently visible symbol
     *
     * @return The 1-based index position of the visible symbol, or 0 if none
     */
    public int getIndicator() {
        return currentIndex + 1;
    }

    /**
     * Returns the total number of symbols in this wheel
     *
     * @return The number of symbols in the wheel
     */
    public int size() {
        return symbols.size();
    }

    public void lock(){
        locked = true;
    }

    public void unlock(){
        locked = false;
    }

    public boolean isLocked(){
        return locked;
    }

    /**
     * Tells whether this wheel accepts being locked A normal wheel does
     *
     * @return true if the wheel can be locked
     */
    public boolean canBeLocked() {
        return true;
    }

    /**
     * Tells whether this wheel accepts exchanging its symbol with another wheel
     * A normal wheel does
     *
     * @return true if the wheel can be swapped
     */
    public boolean canBeSwapped() {
        return true;
    }

    /**
     * Tells whether this wheel accepts being removed from the machine A normal wheel does
     *
     * @return true if the wheel can be deleted
     */
    public boolean canBeDeleted() {
        return true;
    }

    /**
     * Tells every symbol of this wheel that the wheel is spinning An ephemeral
     * symbol shrinks Subclasses that spin in their own way must call it too
     */
    public void notifySpin() {
        for (int i = 0; i < symbols.size(); i++) {
            symbols.get(i).onSpin();
        }
    }

    /**
     * Spins this wheel as part of the machine A normal wheel notifies its
     * symbols and rotates; subclasses can override it to behave differently
     * (for example, by looking at the wheel on their left)
     *
     * @param left    the wheel on the left of this one, or null if it is the first
     * @param steps   number of positions to rotate (may be negative or very large)
     * @param animate whether to show the scroll animation
     */
    public void spin(Wheel left, int steps, boolean animate) {
        notifySpin();
        rotate(steps, animate);
    }
}