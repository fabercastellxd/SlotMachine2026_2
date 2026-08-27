import java.util.ArrayList;

/**
 * Represents an individual wheel (reel) in a slot machine.
 * Each wheel manages a circular list of symbols (represented as color strings)
 * and its visual representation as a rectangle displaying the currently visible symbol.
 * 
 * @author Mateo
 * @author Maria Angelica
 * @version 23/08/26
 */
 
public class Wheel {
    private static final int HEIGHT = 120;
    private static final int WIDTH = 40;
    private static final int DEFAULT_X = 70;
    private static final int DEFAULT_Y = 15;

    private Rectangle wheel;
    private ArrayList<String> symbols;
    private int currentIndex = -1;

    /**
     * Constructs a new Wheel at the specified coordinates on the canvas.
     * Initializes an empty list of symbols and sets the default visual color to gray.
     * 
     * @param x The initial X-coordinate on the canvas.
     * @param y The initial Y-coordinate on the canvas.
     */
     
    public Wheel(int x, int y) {
        wheel = new Rectangle();
        symbols = new ArrayList<>();
        wheel.changeSize(HEIGHT, WIDTH);
        wheel.changeColor("gray");

        wheel.moveHorizontal(x - DEFAULT_X);
        wheel.moveVertical(y - DEFAULT_Y);

        wheel.makeVisible();
    }

    /** 
     * Moves the wheel horizontally by a given distance.
     * Useful when resizing or repositioning the slot machine structure.
     * 
     * @param distance The distance in pixels to move horizontally (positive moves right, negative moves left).
     */
     
    public void moveHorizontal(int distance) {
        wheel.moveHorizontal(distance);
    }

    /** 
     * Hides the wheel by making its visual representation invisible on the canvas.
     */
     
    public void makeInvisible() {
        wheel.makeInvisible();
    }
    
    /**
     * Adds a symbol (color) to the wheel at the specified index.
     * If this is the first symbol added, it is automatically set as the current visible symbol.
     * 
     * @param index The 0-based position where the symbol should be inserted.
     * @param color The name or value of the color representing the symbol.
     */
     
    public void addSymbol(int index, String color){
        symbols.add(index, color);
        if(currentIndex == -1) currentIndex = 0;
        showCurrentSymbol();
    }
    
    /**
     * Removes the first occurrence of a symbol (color) from the wheel.
     * Updates the current visible index and visual display accordingly.
     * 
     * @param color The name or value of the color symbol to remove.
     */
     
    public void delSymbol(String color){
        int index = symbols.indexOf(color);
        if(index == -1) return;
        symbols.remove(index);
        if(symbols.isEmpty()) currentIndex = -1;
        else if (currentIndex >= symbols.size()) currentIndex = symbols.size() -1;
        showCurrentSymbol();
    }
    
    /**
     * Repaints the visual rectangle to reflect the current visible symbol.
     * Displays gray if no symbol is selected or available.
     */
     
    private void showCurrentSymbol(){
        wheel.changeColor(currentIndex == -1 ? "gray" : symbols.get(currentIndex));
    }
    
    /**
     * Sets the visible symbol of the wheel to the specified color if it exists in the symbol list.
     * 
     * @param color The name of the color symbol to display.
     */
     
    public void setSymbol(String color){
        int index = symbols.indexOf(color);
        if(index == -1) return;
        currentIndex = index;
        showCurrentSymbol();
    }

    /**
     * Makes the wheel visible on the canvas.
     */
     
    public void makeVisible() {
        wheel.makeVisible();
    }

    /**
     * Rotates the visible symbol by shifting {@code x} positions in the circular list.
     * Supports both positive (forward) and negative (backward) steps.
     * 
     * @param x The number of positions to rotate.
     */
     
    public void rotate(int x){
        if(symbols.isEmpty()) return;
        currentIndex = (currentIndex +x) % symbols.size();
        if(currentIndex < 0){
            currentIndex = (currentIndex + symbols.size()) % symbols.size();            
        }
        showCurrentSymbol();
    }

    /**
     * Returns the color name of the currently visible symbol.
     * 
     * @return The color of the visible symbol, or {@code null} if no symbols exist.
     */
     
    public String getVisibleSymbol(){
        if(currentIndex == -1 || symbols.isEmpty()){
            return null; 
        } return symbols.get(currentIndex);
    }

    /**
     * Returns the 1-based indicator position of the currently visible symbol within the wheel.
     * 
     * @return The 1-based index position of the visible symbol, or 0 if no symbol is selected.
     */
     
    public int getIndicator(){
        return currentIndex +1;
    }

    /**
     * Returns the total number of symbols in this wheel.
     * 
     * @return The number of symbols in the wheel.
     */
     
    public int size(){
        return symbols.size();
    }
}
