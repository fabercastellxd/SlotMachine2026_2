/**
 * A symbol of a wheel, identified by a color This is the normal symbol:
 * it never changes The other types of symbols extend this class and override
 * its methods
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class Symbol {

    private String color;

    /**
     * Creates a symbol with the given color
     *
     * @param color CSS name of the color
     */
    public Symbol(String color) {
        this.color = color;
    }

    /**
     * Returns the color that identifies this symbol
     *
     * @return the CSS name of the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the size of the symbol as a percentage of the wheel window
     *
     * @return 100 for a normal symbol
     */
    public int getSizePercent() {
        return 100;
    }

    /**
     * Tells whether the symbol is drawn A symbol that is not shown still
     * counts as its color for the jackpot
     *
     * @return true for a normal symbol
     */
    public boolean isShown() {
        return true;
    }

    /**
     * Returns the color of the small square that tells the type of the symbol
     *
     * @return the CSS name of the color, or null if the symbol has no mark
     */
    public String getMarkColor() {
        return null;
    }

    /**
     * Called on every symbol of a wheel each time the wheel spins
     * A normal symbol does nothing
     */
    public void onSpin() {
    }

    /**
     * Called when the symbol gets selected: the wheel stops on it
     * A normal symbol does nothing
     */
    public void onSelected() {
    }
    
        /**
     * Tells whether the symbol is wild: a wild symbol counts as any color
     * when the machine checks for a jackpot
     *
     * @return false for a normal symbol
     */
    public boolean isWild() {
        return false;
    }

}