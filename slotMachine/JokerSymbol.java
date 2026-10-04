/**
 * A joker symbol: it counts as any color for the jackpot A gold square
 * marks it
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class JokerSymbol extends Symbol {

    /**
     * Creates a joker symbol with the given color
     *
     * @param color CSS name of the color
     */
    public JokerSymbol(String color) {
        super(color);
    }

    /** A joker is wild */
    @Override
    public boolean isWild() {
        return true;
    }

    /** A joker is marked in gold */
    @Override
    public String getMarkColor() {
        return "gold";
    }
}