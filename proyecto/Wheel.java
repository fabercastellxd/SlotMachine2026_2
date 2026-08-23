
/**
 * Write a description of class Wheel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Wheel {
    private static final int HEIGHT  = 120;
    private static final int WIDTH = 40;

    // Posicion por defecto de rueda nueva nuevo
    private static final int X = 70;
    private static final int Y = 15;

    private Rectangle wheel;

    public Wheel(int x, int y) {
        wheel = new Rectangle();
        wheel.changeSize(HEIGHT, WIDTH);
        wheel.changeColor("gray"); 

        wheel.moveHorizontal(x - X);
        wheel.moveVertical(y - Y);

        wheel.makeVisible();
    }

    /** Permite mover la rueda horizontalmente cuando la máquina se expande/contrae. */
    public void moverHorizontal(int distancia) {
        wheel.moveHorizontal(distancia);
    }

    /** Oculta la rueda antes de eliminarla del canvas. */
    public void makeInvisible() {
        wheel.makeInvisible();
    }
}