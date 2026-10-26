/**
 * A shy symbol: every time it is selected in the wheel it switches between
 * visible and invisible A white square marks it, so a hidden one can be told
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class ShySymbol extends Symbol {

    private boolean shown = true;

    /**
     * Creates a shy symbol with the given color, visible at the start
     *
     * @param color CSS name of the color
     */
    public ShySymbol(String color) {
        super(color);
    }

    /** Tells whether the symbol is visible right now */
    @Override
    public boolean isShown() {
        return shown;
    }

    /** A shy symbol is marked in white */
    @Override
    public String getMarkColor() {
        return "white";
    }

    /** Switches between visible and invisible */
    @Override
    public void onSelected() {
        shown = !shown;
    }
}