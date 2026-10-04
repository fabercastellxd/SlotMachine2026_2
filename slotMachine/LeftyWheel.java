/**
 * A lefty wheel: when it spins, instead of rotating it copies the symbol that
 * the wheel on its left is showing If it has no wheel on its left, it spins
 * like a normal wheel A small black triangle under its window tells it apart
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class LeftyWheel extends Wheel {

    private static final int MARK_SIZE = 12;
    private static final int MARK_GAP = 2;
    private static final int TRIANGLE_X = 140;
    private static final int TRIANGLE_Y = 15;

    private Triangle mark;

    /**
     * Constructs a lefty wheel at the given coordinates
     *
     * @param x       X-coordinate of the wheel
     * @param y       Y-coordinate of the wheel
     * @param visible whether the wheel is drawn from the start
     */
    public LeftyWheel(int x, int y, boolean visible) {
        super(x, y, visible);
        mark = new Triangle();
        mark.changeSize(MARK_SIZE, 2 * MARK_SIZE);
        mark.changeColor("black");
        mark.moveHorizontal(x + WIDTH / 2 - TRIANGLE_X);
        mark.moveVertical(y + HEIGHT + MARK_GAP - TRIANGLE_Y);
        if (visible) {
            mark.makeVisible();
        }
    }

        /**
     * Copies the visible symbol of the wheel on its left Without a left wheel,
     * it rotates like a normal wheel Either way, its symbols are told that the
     * wheel spun
     *
     * @param left    the wheel on the left, or null if this is the first one
     * @param steps   number of positions to rotate (ignored if there is a left wheel)
     * @param animate whether to show the scroll animation (only when rotating)
     */
    @Override
    public void spin(Wheel left, int steps, boolean animate) {
        if (left == null) {
            super.spin(left, steps, animate);
            return;
        }
        notifySpin();
        String symbol = left.getVisibleSymbol();
        if (symbol != null) {
            setSymbol(symbol);
        }
    }
    /** Moves the wheel and its mark */
    @Override
    public void moveHorizontal(int distance) {
        super.moveHorizontal(distance);
        mark.moveHorizontal(distance);
    }

    /** Shows the wheel and its mark */
    @Override
    public void makeVisible() {
        super.makeVisible();
        mark.makeVisible();
    }

    /** Hides the wheel and its mark */
    @Override
    public void makeInvisible() {
        super.makeInvisible();
        mark.makeInvisible();
    }
}