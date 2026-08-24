/**
 * Write a description of class Wheel here.
 * 
 * @author (Mateo) 
 * @version (23/08/26)
 */
import java.util.ArrayList;

public class Wheel {
    private static final int HEIGHT = 120;
    private static final int WIDTH = 40;

    // Posicion por defecto de rueda nueva
    private static final int DEFAULT_X = 70;
    private static final int DEFAULT_Y = 15;

    private Rectangle wheel;
    private ArrayList<String> symbols;
    private int currentIndex = -1;

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
     * Permite mover la rueda horizontalmente cuando la maquina se expande/contrae 
     */
    public void moveHorizontal(int distance) {
        wheel.moveHorizontal(distance);
    }

    /** 
     * Oculta la rueda antes de eliminarla del canvas
     */
    public void makeInvisible() {
        wheel.makeInvisible();
    }
    
    /**
     * Agrega un simbolo a una rueda en la posicion indicada
     */
    public void addSymbol(int index, String color){
        symbols.add(index, color);
        if(currentIndex == -1) currentIndex = 0;
        showCurrentSymbol();
    }
    
    /**
     * Elimina un simbolo de una rueda
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
     * Repinta la rueda con el color actual del simbolo
     */
    private void showCurrentSymbol(){
        wheel.changeColor(currentIndex == -1 ? "gray" : symbols.get(currentIndex));
    }
    
    /**
     * Fija el simbolo que una rueda muestra actualmente
     */
    public void setSymbol(String color){
        int index = symbols.indexOf(color);
        if(index == -1) return;
        currentIndex = index;
        showCurrentSymbol();
    }

    // Metodos para ciclo 2 y 3 
    /**
     * Hace visible la rueda
     */
    public void makeVisible() {
        wheel.makeVisible();
    }
    /**
     * rota el indcie del simbolo actual saltando x posiciones y se calcuale de forma circualra (manejando valores negativos sin problema)
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
     * Retorna el color del simbolo acutal
     */
    public String getVisibleSymbol(){
        if(currentIndex == -1 || symbols.isEmpty()){
            return null; 
        } return symbols.get(currentIndex);
    }
    /**
     * Retorna el indicador de la posicion actual dentro de la rueda
     */
    public int getIndicator(){
        return currentIndex +1;
    }
    /**
     * Retorna la cantidad de simbolos
     */
    public int size(){
        return symbols.size();
    }
}