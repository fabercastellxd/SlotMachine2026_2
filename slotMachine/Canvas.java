import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Canvas is a class to allow for simple graphical drawing on a canvas.
 * This is a modification of the general purpose Canvas, specially made for
 * the BlueJ "shapes" example. 
 *
 * @author Bruce Quig
 * @author Michael Kolling (mik)
 *
 * @version 1.6 (shapes)
 */
public class Canvas {

    private static Canvas canvasSingleton;

    public static Canvas getCanvas() {
        return getCanvas(500, 500, true);
    }

    private static final int SCREEN_MARGIN = 80;

    public static Canvas getCanvas(int width, int height) {
        return getCanvas(width, height, true);
    }

    /**
     * Nuevo constructor para manejar las dimensiones acorde a la cantidad de ruedas.
     */
    public static Canvas getCanvas(int width, int height, boolean makeVisible) {
        if (canvasSingleton == null) {
            canvasSingleton = new Canvas("BlueJ Shapes Demo", width, height, Color.white);
            canvasSingleton.setVisible(makeVisible);
        }
        return canvasSingleton;
    }

    /** CSS color names (lowercase) and their colors. */
    private static final HashMap<String, Color> CSS_COLORS = new HashMap<String, Color>();
    static {
        define("black", 0, 0, 0);          define("white", 255, 255, 255);   define("gray", 128, 128, 128);
        define("grey", 128, 128, 128);     define("gold", 255, 215, 0);      define("magenta", 255, 0, 255);
        define("red", 255, 0, 0);          define("blue", 0, 0, 255);        define("green", 0, 128, 0);
        define("yellow", 255, 255, 0);     define("orange", 255, 165, 0);    define("purple", 128, 0, 128);
        define("pink", 255, 192, 203);     define("brown", 165, 42, 42);     define("cyan", 0, 255, 255);
        define("lime", 0, 255, 0);         define("navy", 0, 0, 128);        define("teal", 0, 128, 128);
        define("maroon", 128, 0, 0);       define("olive", 128, 128, 0);     define("coral", 255, 127, 80);
        define("salmon", 250, 128, 114);   define("crimson", 220, 20, 60);   define("indigo", 75, 0, 130);
        define("violet", 238, 130, 238);   define("turquoise", 64, 224, 208); define("tan", 210, 180, 140);
        define("khaki", 240, 230, 140);    define("orchid", 218, 112, 214);  define("plum", 221, 160, 221);
        define("tomato", 255, 99, 71);     define("chocolate", 210, 105, 30); define("sienna", 160, 82, 45);
        define("peru", 205, 133, 63);      define("skyblue", 135, 206, 235); define("steelblue", 70, 130, 180);
        define("royalblue", 65, 105, 225); define("dodgerblue", 30, 144, 255); define("slateblue", 106, 90, 205);
        define("seagreen", 46, 139, 87);   define("forestgreen", 34, 139, 34); define("limegreen", 50, 205, 50);
        define("springgreen", 0, 255, 127); define("chartreuse", 127, 255, 0); define("darkorange", 255, 140, 0);
        define("hotpink", 255, 105, 180);  define("deeppink", 255, 20, 147); define("firebrick", 178, 34, 34);
        define("darkred", 139, 0, 0);      define("darkgreen", 0, 100, 0);   define("darkblue", 0, 0, 139);
        define("aquamarine", 127, 255, 212); define("lavender", 230, 230, 250); define("beige", 245, 245, 220);
        define("mediumpurple", 147, 112, 219); define("goldenrod", 218, 165, 32);
    }

    /** Registers a CSS color name with its RGB values. */
    private static void define(String name, int r, int g, int b) {
        CSS_COLORS.put(name, new Color(r, g, b));
    }

    /**
     * Returns the color that a CSS color name stands for.
     * @param colorName a CSS color name, in any letter case
     * @return the color, or {@code null} if the name is not known
     */
    public static Color colorFor(String colorName) {
        return colorName == null ? null : CSS_COLORS.get(colorName.toLowerCase());
    }

    // ----- instance part -----

    private JFrame frame;
    private CanvasPane canvas;
    private JScrollPane scroll;
    private boolean viewCentered = false;
    private Graphics2D graphic;
    private Color backgroundColour;
    private Image canvasImage;
    private List<Object> objects;
    private HashMap<Object, ShapeDescription> shapes;

    /**
     * Create a Canvas. The drawing area keeps the requested size, but the window is
     * never larger than the screen: when the drawing is wider or taller, the window
     * shows scroll bars.
     * @param title    title to appear in Canvas Frame
     * @param width    the desired width for the canvas
     * @param height   the desired height for the canvas
     * @param bgColour the desired background colour of the canvas
     */
    private Canvas(String title, int width, int height, Color bgColour) {
        frame = new JFrame();
        canvas = new CanvasPane();
        canvas.setPreferredSize(new Dimension(width, height));
        scroll = new JScrollPane(canvas);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        scroll.getViewport().setPreferredSize(new Dimension(
            Math.min(width, screen.width - SCREEN_MARGIN),
            Math.min(height, screen.height - SCREEN_MARGIN)));
        frame.setContentPane(scroll);
        frame.setTitle(title);
        backgroundColour = bgColour;
        frame.pack();
        objects = new ArrayList<Object>();
        shapes = new HashMap<Object, ShapeDescription>();
    }

    /**
     * Set the canvas visibility and brings canvas to the front of screen
     * when made visible. The first time it is shown, the view is centered
     * horizontally, which is where the machine is drawn.
     * @param visible boolean value representing the desired visibility of
     * the canvas (true or false)
     */
    public void setVisible(boolean visible) {
        if (graphic == null) {
            Dimension size = canvas.getPreferredSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D) canvasImage.getGraphics();
            graphic.setColor(backgroundColour);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        frame.setVisible(visible);
        if (visible && !viewCentered) {
            centerView();
            viewCentered = true;
        }
    }

    /**
     * Scrolls the window so the middle of the drawing area is in the middle of the window.
     */
    private void centerView() {
        JViewport viewport = scroll.getViewport();
        int x = Math.max(0, (canvas.getPreferredSize().width - viewport.getExtentSize().width) / 2);
        viewport.setViewPosition(new Point(x, 0));
    }

    /**
     * Tells whether the canvas window is currently shown on screen.
     * @return {@code true} if the window is visible.
     */
    public boolean isVisible() {
        return frame.isVisible();
    }

    public void draw(Object referenceObject, String color, Shape shape) {
        draw(referenceObject, color, shape, null);
    }

    /**
     * Draw a given shape onto the canvas, optionally clipped to a region.
     * @param referenceObject an object to define identity for this shape
     * @param color           the color of the shape
     * @param shape           the shape object to be drawn on the canvas
     * @param clip            the visible region for this shape, or null for no clipping
     */
    public void draw(Object referenceObject, String color, Shape shape, Shape clip) {
        objects.remove(referenceObject);
        objects.add(referenceObject);
        shapes.put(referenceObject, new ShapeDescription(shape, color, clip));
        redraw();
    }

    /**
     * Erase a given shape from the screen.
     * @param referenceObject the shape object to be erased 
     */
    public void erase(Object referenceObject) {
        objects.remove(referenceObject);
        shapes.remove(referenceObject);
        redraw();
    }

    /**
     * Set the foreground colour of the Canvas from a CSS color name.
     * An unknown name is drawn in black.
     * @param colorString the CSS name of the new foreground colour
     */
    public void setForegroundColor(String colorString) {
        Color color = colorFor(colorString);
        graphic.setColor(color != null ? color : Color.black);
    }

    /**
     * Wait for a specified number of milliseconds before finishing.
     * This provides an easy way to specify a small delay which can be
     * used when producing animations.
     * @param milliseconds the number 
     */
    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Redraw all shapes currently on the Canvas.
     */
    private void redraw() {
        erase();
        for (Iterator<Object> i = objects.iterator(); i.hasNext(); ) {
            shapes.get(i.next()).draw(graphic);
        }
        canvas.repaint();
    }

    /**
     * Erase the whole canvas. (Does not repaint.)
     */
    private void erase() {
        Color original = graphic.getColor();
        graphic.setColor(backgroundColour);
        Dimension size = canvas.getSize();
        graphic.fill(new java.awt.Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }

    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame.
     */
    private class CanvasPane extends JPanel {
        @Override
        public void paint(Graphics g) {
            g.drawImage(canvasImage, 0, 0, null);
        }
    }

    /************************************************************************
     * Inner class ShapeDescription - records shape details for redrawing.
     */
    private class ShapeDescription {
        private Shape shape;
        private String colorString;
        private Shape clip;

        public ShapeDescription(Shape shape, String color, Shape clip) {
            this.shape = shape;
            colorString = color;
            this.clip = clip;
        }

        public void draw(Graphics2D graphic) {
            setForegroundColor(colorString);
            Shape oldClip = graphic.getClip();
            if (clip != null) {
                graphic.setClip(clip);
            }
            graphic.draw(shape);
            graphic.fill(shape);
            graphic.setClip(oldClip);
        }
    }
}