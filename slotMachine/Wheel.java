import java.util.ArrayList;

/**
 * Represents an individual wheel (reel) in a slot machine.
 * Visually, the wheel is a fixed-size window on the canvas through which
 * two stacked color blocks scroll vertically to simulate a real slot reel:
 * one block holds the currently visible symbol and the other holds the
 * incoming symbol during an animated spin.
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 * @version 09/09/26
 */
public class Wheel {
    private static final int HEIGHT = 120;
    private static final int WIDTH = 40;
    private static final int DEFAULT_X = 70;
    private static final int DEFAULT_Y = 15;

    /** Pixels moved per animation frame; smaller = smoother but slower. */
    private static final int SCROLL_STEP_PX = 15;

    private Rectangle currentBlock;
    private Rectangle nextBlock;
    private ArrayList<String> symbols;
    private int currentIndex = -1;

    private boolean locked = false;

    private int windowX;
    private int windowY;
    private int currentBlockY;
    private int nextBlockY;

    /**
     * Constructs a new Wheel at the specified coordinates on the canvas.
     *
     * @param x The initial X-coordinate on the canvas.
     * @param y The initial Y-coordinate on the canvas.
     */
    public Wheel(int x, int y) {
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
        currentBlock.makeVisible();

        nextBlock = new Rectangle();
        nextBlock.changeSize(HEIGHT, WIDTH);
        nextBlock.changeColor("gray");
        nextBlock.moveHorizontal(x - DEFAULT_X);
        nextBlock.moveVertical(y - DEFAULT_Y);
        nextBlockY = y;
        nextBlock.setClip(windowX, windowY, WIDTH, HEIGHT);
        // nextBlock queda invisible hasta que un giro animado lo necesite
    }

    /**
     * Moves the wheel horizontally by a given distance, keeping the
     * visible window (clip) aligned with both blocks.
     *
     * @param distance The distance in pixels to move horizontally.
     */
    public void moveHorizontal(int distance) {
        currentBlock.moveHorizontal(distance);
        nextBlock.moveHorizontal(distance);
        windowX += distance;
        updateClip();
    }

    private void updateClip() {
        currentBlock.setClip(windowX, windowY, WIDTH, HEIGHT);
        nextBlock.setClip(windowX, windowY, WIDTH, HEIGHT);
    }

    /** Hides both blocks of the wheel. */
    public void makeInvisible() {
        currentBlock.makeInvisible();
        nextBlock.makeInvisible();
    }

    /** Makes the visible block of the wheel visible again. */
    public void makeVisible() {
        currentBlock.makeVisible();
    }

    /**
     * Adds a symbol (color) to the wheel at the specified index.
     *
     * @param index The 0-based position where the symbol should be inserted.
     * @param color The name or value of the color representing the symbol.
     */
    public void addSymbol(int index, String color) {
        symbols.add(index, color);
        if (currentIndex == -1) {
            currentIndex = 0;
        } else if (index <= currentIndex) {
            currentIndex++;
        }
        showCurrentSymbol();
    }

    /**
     * Removes the first occurrence of a symbol (color) from the wheel.
     *
     * @param color The name or value of the color symbol to remove.
     */
    public void delSymbol(String color) {
        int index = symbols.indexOf(color);
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
     * Instantly repaints the window with the current symbol's color,
     * with no scroll animation. Used whenever the change doesn't need
     * (or isn't allowed) to be animated.
     */
    private void showCurrentSymbol() {
        String color = currentIndex == -1 ? "gray" : symbols.get(currentIndex);
        moveBlockTo(currentBlock, currentBlockY, windowY);
        currentBlockY = windowY;
        currentBlock.changeColor(color);
        nextBlock.makeInvisible();
    }

    private void moveBlockTo(Rectangle block, int fromY, int toY) {
        if (fromY != toY) {
            block.moveVertical(toY - fromY);
        }
    }

    /**
     * Sets the visible symbol of the wheel to the specified color if it exists.
     *
     * @param color The name of the color symbol to display.
     */
    public void setSymbol(String color) {
        int index = symbols.indexOf(color);
        if (index == -1) return;
        currentIndex = index;
        showCurrentSymbol();
    }

    /**
     * Rotates the visible symbol by shifting {@code x} positions, instantly
     * (no scroll animation). Supports both positive and negative steps.
     *
     * @param x The number of positions to rotate.
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
     * Rotates the wheel forward {@code steps} positions. When {@code animate}
     * is true, each step is shown as a vertical scroll (the outgoing symbol
     * slides up and out while the incoming one slides up into view).
     *
     * @param steps   number of forward steps to rotate.
     * @param animate whether to show the scroll animation for each step.
     */
    public void rotate(int steps, boolean animate) {
        if (symbols.isEmpty()) return;
        for (int i = 0; i < steps; i++) {
            if (animate) {
                animateOneStepForward();
            } else {
                currentIndex = (currentIndex + 1) % symbols.size();
            }
        }
        if (!animate) {
            showCurrentSymbol();
        }
    }

    /**
     * Performs one full scroll cycle: brings in the next symbol from below
     * while the current one exits above, then swaps the block roles so the
     * wheel is ready for the next step.
     */
    private void animateOneStepForward() {
        int previousIndex = currentIndex;
        int newIndex = (currentIndex + 1) % symbols.size();

        moveBlockTo(currentBlock, currentBlockY, windowY);
        currentBlockY = windowY;
        currentBlock.changeColor(previousIndex == -1 ? "gray" : symbols.get(previousIndex));

        moveBlockTo(nextBlock, nextBlockY, windowY + HEIGHT);
        nextBlockY = windowY + HEIGHT;
        nextBlock.changeColor(symbols.get(newIndex));
        nextBlock.makeVisible();

        int remaining = HEIGHT;
        while (remaining > 0) {
            int delta = Math.min(SCROLL_STEP_PX, remaining);
            currentBlock.moveVertical(-delta);
            currentBlockY -= delta;
            nextBlock.moveVertical(-delta);
            nextBlockY -= delta;
            remaining -= delta;
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
     * Returns the color name of the currently visible symbol.
     *
     * @return The color of the visible symbol, or {@code null} if no symbols exist.
     */
    public String getVisibleSymbol() {
        if (currentIndex == -1 || symbols.isEmpty()) {
            return null;
        }
        return symbols.get(currentIndex);
    }

    /**
     * Returns the 1-based indicator position of the currently visible symbol.
     *
     * @return The 1-based index position of the visible symbol, or 0 if none.
     */
    public int getIndicator() {
        return currentIndex + 1;
    }

    /**
     * Returns the total number of symbols in this wheel.
     *
     * @return The number of symbols in the wheel.
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
}