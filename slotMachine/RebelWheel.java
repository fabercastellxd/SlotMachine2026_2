/**
 * A rebel wheel: it refuses to be locked, swapped or deleted, but it spins
 * like a normal wheel A small black circle under its window tells it apart
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class RebelWheel extends Wheel {

    private static final int MARK_SIZE = 12;
    private static final int MARK_GAP = 2;
    /** Default X (left side) and Y of a new Circle */
    private static final int CIRCLE_X = 20;
    private static final int CIRCLE_Y = 15;

    private Circle mark;

    /**
     * Constructs a rebel wheel at the given coordinates
     *
     * @param x       X-coordinate of the wheel
     * @param y       Y-coordinate of the wheel
     * @param visible whether the wheel is drawn from the start
     */
    public RebelWheel(int x, int y, boolean visible) {
        super(x, y, visible);
        mark = new Circle();
        mark.changeSize(MARK_SIZE);
        mark.changeColor("black");
        mark.moveHorizontal(x + WIDTH / 2 - MARK_SIZE / 2 - CIRCLE_X);
        mark.moveVertical(y + HEIGHT + MARK_GAP - CIRCLE_Y);
        if (visible) {
            mark.makeVisible();
        }
    }

    /** A rebel wheel never accepts being locked */
    @Override
    public boolean canBeLocked() {
        return false;
    }

    /** A rebel wheel never accepts being swapped */
    @Override
    public boolean canBeSwapped() {
        return false;
    }

    /** A rebel wheel never accepts being deleted */
    @Override
    public boolean canBeDeleted() {
        return false;
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