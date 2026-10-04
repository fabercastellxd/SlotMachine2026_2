/**
 * An ephemeral symbol: with every spin of its wheel it gets smaller, until it
 * is only a dot A black square marks it
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class EphemeralSymbol extends Symbol {

    private static final int STEP_PERCENT = 20;

    private int sizePercent = 100;

    /**
     * Creates an ephemeral symbol with the given color, at full size
     *
     * @param color CSS name of the color
     */
    public EphemeralSymbol(String color) {
        super(color);
    }

    /** Returns the current size: 100 at the start, 0 when it is a dot */
    @Override
    public int getSizePercent() {
        return sizePercent;
    }

    /** An ephemeral symbol is marked in black */
    @Override
    public String getMarkColor() {
        return "black";
    }

    /** Shrinks the symbol one step, never below a dot */
    @Override
    public void onSpin() {
        sizePercent = Math.max(0, sizePercent - STEP_PERCENT);
    }
}