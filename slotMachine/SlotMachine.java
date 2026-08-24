/**
 * Write a description of class SlotMachine here.
 * 
 * @author (Mateo) 
 * @version (22/08/26)
 */

import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.Random;

public class SlotMachine {
    public static final int MIN_WHEELS = 3;
    public static final int MAX_WHEELS = 50;

    // Posicion por defecto de nuevo Rectangle 
    private static final int DEFAULT_X = 70;
    private static final int DEFAULT_Y = 15;

    // Constantes de diseño pantalla
    private static final int WHEEL_WIDTH = 40;
    private static final int WHEEL_SPACING = 10;
    private static final int MARGIN = 15;

    private static final int TOP_HEIGHT = 20;
    private static final int MIDDLE_HEIGHT = 150;
    private static final int BASE_HEIGHT = 20;
    private static final int INITIAL_Y = 50;
    private static final int CANVAS_MARGIN = 30;

    // Dimension del canvas
    private static final int CANVAS_WIDTH = middleWidth(MAX_WHEELS) + 2 * CANVAS_MARGIN;
    private static final int CANVAS_HEIGHT = INITIAL_Y + TOP_HEIGHT + MIDDLE_HEIGHT + BASE_HEIGHT + CANVAS_MARGIN;
    private static final int CENTER_X = CANVAS_WIDTH / 2;

    // Componentes de diseño de la pantalla
    private Rectangle topRectangle;
    private Rectangle middleRectangle;
    private Rectangle baseRectangle;

    // Sobresaliente de Tope y Base respecto al ancho del medio
    private static final int TOP_OVERHANG = 20;
    private static final int BASE_OVERHANG = 20;

    // Lista de objetos Wheel y symbols
    private ArrayList<Wheel> wheelList;
    private ArrayList<String> symbols;
    private boolean ok;

    // Anchos dinamicos (cambian con la cantidad de ruedas por eso no son "final")
    private int middleWidth;
    private int topWidth;
    private int baseWidth;

    private static int middleWidth(int numWheels) {
        return (2 * MARGIN) + (numWheels * WHEEL_WIDTH) + ((numWheels - 1) * WHEEL_SPACING);
    }

    public SlotMachine() {
        Canvas.getCanvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        wheelList = new ArrayList<>();
        symbols = new ArrayList<>();
        ok = true;

        topRectangle = new Rectangle();
        middleRectangle = new Rectangle();
        baseRectangle = new Rectangle();

        updateWidths(MIN_WHEELS);

        // Ajustar dimensiones iniciales
        topRectangle.changeSize(TOP_HEIGHT, topWidth);
        middleRectangle.changeSize(MIDDLE_HEIGHT, middleWidth);
        baseRectangle.changeSize(BASE_HEIGHT, baseWidth);

        // Colores de la carcasa
        topRectangle.changeColor("gold");
        middleRectangle.changeColor("gold");
        baseRectangle.changeColor("gold");

        // Ubicar en el canvas desde el eje central
        placeOnAxis(topRectangle, topWidth, INITIAL_Y);
        placeOnAxis(middleRectangle, middleWidth, INITIAL_Y + TOP_HEIGHT);
        placeOnAxis(baseRectangle, baseWidth, INITIAL_Y + TOP_HEIGHT + MIDDLE_HEIGHT);

        topRectangle.makeVisible();
        middleRectangle.makeVisible();
        baseRectangle.makeVisible();

        // Inicializar las primeras MIN_WHEELS ruedas
        int yWheel = INITIAL_Y + TOP_HEIGHT + 15;
        for (int i = 1; i <= MIN_WHEELS; i++) {
            wheelList.add(new Wheel(wheelX(i), yWheel));
        }
    }

    /**
     * MINI-CICLO I: 
     * 1. Crear una máquina tragamonedas
     * 2. Adicionar o eliminar una rueda
     * 3. Adicionar o eliminar un símbolo
     */
    
    /**
     * Recalcula la proporcion de los 3 rectangulos de la maquina
     */
    private void updateWidths(int numWheels) {
        middleWidth = middleWidth(numWheels);
        topWidth = middleWidth + 2 * TOP_OVERHANG;
        baseWidth = middleWidth + 2 * BASE_OVERHANG;
    }

    /**
     * Posiciona un rectangulo centrado en "CENTER_X" 
     */
    private void placeOnAxis(Rectangle r, int width, int y) {
        int xDest = CENTER_X - (width / 2);
        r.moveHorizontal(xDest - DEFAULT_X);
        r.moveVertical(y - DEFAULT_Y);
    }

    /** 
     * Calcula la coordenada X para ubicar la rueda del indice especificado (empieza en 1) 
     */
    public int wheelX(int index) {
        int xStartMiddle = CENTER_X - (middleWidth / 2);
        return xStartMiddle + MARGIN + (index - 1) * (WHEEL_WIDTH + WHEEL_SPACING);
    }

    /** Reajusta el tamaño de la pantalla y centra todas las ruedas existentes */
    private void resizeStructure(int oldMiddleWidth, int oldBaseWidth) {
        int middleTopOffset = -(middleWidth - oldMiddleWidth) / 2;
        int baseOffset = -(baseWidth - oldBaseWidth) / 2;

        middleRectangle.changeSize(MIDDLE_HEIGHT, middleWidth);
        middleRectangle.moveHorizontal(middleTopOffset);

        topRectangle.changeSize(TOP_HEIGHT, topWidth);
        topRectangle.moveHorizontal(middleTopOffset);

        baseRectangle.changeSize(BASE_HEIGHT, baseWidth);
        baseRectangle.moveHorizontal(baseOffset);

        for (Wheel wheel : wheelList) {
            wheel.moveHorizontal(middleTopOffset);
        }
    }

    public void addWheel(int pos) {
        if (wheelList.size() < MAX_WHEELS) {
            int oldMiddleWidth = middleWidth;
            int oldBaseWidth = baseWidth;

            updateWidths(wheelList.size() + 1);
            resizeStructure(oldMiddleWidth, oldBaseWidth);

            int yWheel = INITIAL_Y + TOP_HEIGHT + 15;
            int xNew = wheelX(wheelList.size() + 1);
            
            Wheel newWheel = new Wheel(xNew, yWheel);
            for(int i = 0; i < symbols.size(); i++){
                newWheel.addSymbol(i, symbols.get(i));
            }
            wheelList.add(newWheel);
            
        } else {
            JOptionPane.showMessageDialog(null,
                "No puedes agregar mas ruedas. Maximo permitido: " + MAX_WHEELS,
                "Limite Maximo",
                JOptionPane.WARNING_MESSAGE);
        }   
    }

    public void delWheel(int pos) {
        if (wheelList.size() > MIN_WHEELS) {
            Wheel last = wheelList.remove(wheelList.size() - 1);
            last.makeInvisible();

            int oldMiddleWidth = middleWidth;
            int oldBaseWidth = baseWidth;

            updateWidths(wheelList.size());
            resizeStructure(oldMiddleWidth, oldBaseWidth);
        } else {
            JOptionPane.showMessageDialog(null,
                "No puedes eliminar mas ruedas. Minimo permitido: " + MIN_WHEELS,
                "Limite Minimo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    public int getWheels() {
        return wheelList.size();
    }

    /**
     * Agrega un simbolo en la posicion indicada y lo establece en las demas ruedas
     * Si el color ya existe, la operacion falla
     */
    public void addSymbol(int pos, String color){
        if(symbols.contains(color)){
            ok = false;
            return;
        }
        int index = clamPos(pos, symbols.size() + 1) -1;
        symbols.add(index, color);
        for(Wheel wheel : wheelList){
            wheel.addSymbol(index, color);
        }
        ok = true;
    }

    /**
     * Elimina un simbolo de todas las ruedas
     */
    public void delSymbol(String color){
        int index = symbols.indexOf(color);
        if(index == -1) {
            ok = false; return;
        }
        symbols.remove(index);
        for (Wheel wheel : wheelList){
            wheel.delSymbol(color);
        }
        ok = true;
    }

    /**
     * Ajusta posiciones perdidas en un rango de [1, max]
     */
    private int clamPos(int pos, int max){
        if(pos < 1 ) return 1;
        if(pos > max) return max;
        return pos;
    }
    
    public void placeSymbol(int wheel, String symbol){
        if(!symbols.contains(symbol)){
            ok = false;
            JOptionPane.showMessageDialog(null, "El simbolo " + symbol + "no existe", "Error", JOptionPane.WARNING_MESSAGE); 
            return;
        }
        int index = clamPos(wheel, wheelList.size())-1;
        wheelList.get(index).setSymbol(symbol);
        checkJackPot();
        ok = true;
    }

    /**
     * MINI-CICLO II: 
     * 4. Girar las ruedas de la máquina
     * 5. Consultar los símbolos de la máquina
     * 6. Consultar si la configuración es la ganadora
     */
    
    /**
     * Gira una rueda en especifico de manera aleatoria
     */
    public void spin(int wheel) {
        if(wheelList.isEmpty() || symbols.isEmpty()){
            ok = false;
            return;
        }
        int index = clamPos(wheel, wheelList.size()) -1;
        Random r = new Random();
        int turns = r.nextInt(15)+1;
        wheelList.get(index).rotate(turns);
        checkJackPot();
        ok = true;
    }

    /**
     * Gira todas las ruedas aleatoriamente
     */
    public void spin() {
        if (wheelList.isEmpty() || symbols.isEmpty()){
            ok = false;
            return;
        }
        Random r = new Random();
        for(Wheel i : wheelList){
            int turns = r.nextInt(15)+1;
            i.rotate(turns);
        }
        checkJackPot();
        ok = true;
    }
    /**
     * Retorna los colores en el orden de la lista central
     */
    public String[] symbols(){
        ok = true;
        return symbols.toArray(new String[0]);
    }
    
    /**
     * Retorna la cantidad de simbolos diferentes
     */
    public int distinctSymbols() {
        ok = true;
        return symbols.size();
    }

    /**
     * Retorna los colores visibles en todas las ruedas (izquierda a derecha)
     */
    public String[] configuration() {
        String[] conf = new String[wheelList.size()];
        for(int i = 0; i < wheelList.size(); i++){
            conf[i] = (String) wheelList.get(i).getVisibleSymbol();
        }
        ok = true;
        return conf;
    }

    /**
     * Verifica si se hizo jackpot al coincidir todos los simbolos
     */
    public boolean isJackpot() {
        if (wheelList.isEmpty() || symbols.isEmpty()){
            ok = false;
            return false;
        }
        String[] conf = configuration();
        String first = conf[0];
        if(first == null){
            ok = true;
            return false;
        }
        for(int i = 0; i < conf.length; i++){
            if(conf[i] == null || !conf[i].equals(first)){
                ok = true;
                return false;
            }
        }
        ok = true;
        return true;
    }
    /**
     * Verifica si se hizo jackpot y cambio el color de la maquina
     */
    private void checkJackPot(){
        if(isJackpot()){
            topRectangle.changeColor("magenta");
            baseRectangle.changeColor("magenta");
        } else{
            topRectangle.changeColor("gold");
            baseRectangle.changeColor("gold");
        }
    }

    /**
     * MINI-CICLO III: 
     * 7. Hacer visible o invisible el simulador (debe poder funcionar en modo invisible)
     * 8. Terminar el simulador
     */
    
    /**
     * Hace visible todo el simulador
     */
    public void makeVisible(){
        topRectangle.makeVisible();
        middleRectangle.makeVisible();
        baseRectangle.makeVisible();
        for(Wheel i : wheelList){
            i.makeVisible();
        }
        ok = true;
    }
    
    /**
     * Hace invisible todo el simulador
     */
    public void makeInvisible() {
        topRectangle.makeInvisible();
        middleRectangle.makeInvisible();
        baseRectangle.makeInvisible();
        for(Wheel i : wheelList){
            i.makeInvisible();
        }
        ok = true;
    }

    /**
     * Termina el simulador
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * Retorna el estado de la ultima operacion
     */
    public boolean ok() {
        return ok;
    }
}