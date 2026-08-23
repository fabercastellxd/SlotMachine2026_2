
/**
 * Write a description of class SlotMachine here.
 * 
 * @author (Mateo) 
 * @version (22/08/26)
 */

import java.util.ArrayList;
import javax.swing.JOptionPane;

public class SlotMachine {
    public static final int MIN_WHEELS = 3;
    public static final int MAX_WHEELS = 50;

    // Posicion por defecto de nuevo Rectangle 
    private static final int X = 70;
    private static final int Y = 15;

    // Constantes de diseño  pantalla
    private static final int WIDTH_WHEEL = 40;
    private static final int ESPACIO_WHEEL = 10;
    private static final int MARGEN = 15;

    private static final int HEIGHT_TOPE  = 20;
    private static final int HEIGHT_MEDIO = 150;
    private static final int HEIGHT_BASE  = 20;
    private static final int Y_INICIAL  = 50;
    private static final int MARGEN_CANVAS = 30;
    
    // Dimension del canva
    private static final int ANCHO_CANVAS = anchoMedio(MAX_WHEELS) + 2 * MARGEN_CANVAS;
    private static final int ALTO_CANVAS  = Y_INICIAL + HEIGHT_TOPE + HEIGHT_MEDIO + HEIGHT_BASE + MARGEN_CANVAS;
    private static final int CENTRO_X = ANCHO_CANVAS / 2;
    
    // Componentes de diseño de la pantalla
    private Rectangle rectanguloTope;
    private Rectangle rectanguloMedio;
    private Rectangle rectanguloBase;
    
    //// Sobresaliente de Tope y Base respecto al ancho del medio
    private static final int SALIENTE_TOPE = 20;
    private static final int SALIENTE_BASE = 20;

    // Lista de objetos Wheel
    private ArrayList<Wheel> listaRuedas;

    // Anchos dinamicos (cambian con la cantidad de ruedas por eso no son "final")
    private int anchoMedio;
    private int anchoTope;
    private int anchoBase;
    private static int anchoMedio(int numRuedas){
    return (2 * MARGEN) + (numRuedas * WIDTH_WHEEL) + ((numRuedas - 1) * ESPACIO_WHEEL);
    }
    
    public SlotMachine() {
        
        Canvas.getCanvas(ANCHO_CANVAS, ALTO_CANVAS);   

        listaRuedas = new ArrayList<>();

        rectanguloTope  = new Rectangle();
        rectanguloMedio = new Rectangle();
        rectanguloBase  = new Rectangle();

        actualizarAnchos(MIN_WHEELS);

        // Ajustar dimensiones iniciales
        rectanguloTope.changeSize(HEIGHT_TOPE, anchoTope);
        rectanguloMedio.changeSize(HEIGHT_MEDIO, anchoMedio);
        rectanguloBase.changeSize(HEIGHT_BASE, anchoBase);

        // Colores de la carcasa
        rectanguloTope.changeColor("gold");
        rectanguloMedio.changeColor("gold");
        rectanguloBase.changeColor("gold");

        // Ubicar en el canvas desde el eje central
        ubicarEnEje(rectanguloTope, anchoTope, Y_INICIAL);
        ubicarEnEje(rectanguloMedio, anchoMedio, Y_INICIAL + HEIGHT_TOPE);
        ubicarEnEje(rectanguloBase, anchoBase, Y_INICIAL + HEIGHT_TOPE + HEIGHT_MEDIO);

        rectanguloTope.makeVisible();
        rectanguloMedio.makeVisible();
        rectanguloBase.makeVisible();

        // Inicializar las primeras MIN_WHEELS ruedas
        int yRueda = Y_INICIAL + HEIGHT_TOPE + 15;
        for (int i = 1; i <= MIN_WHEELS; i++) {
            listaRuedas.add(new Wheel(xRueda(i), yRueda));
        }
    }
    
    

    /**
     * Recalcula la proporcion de los 3 rectangulos de la maquina
    */
    private void actualizarAnchos(int nRuedas) {
    anchoMedio = anchoMedio(nRuedas);
    anchoTope  = anchoMedio + 2 * SALIENTE_TOPE;   
    anchoBase  = anchoMedio + 2 * SALIENTE_BASE;   
    }

    /**
     * Posiciona un rectangulo centrado en "CENTRO_X" 
     */
    private void ubicarEnEje(Rectangle r, int width, int y) {
        int xDestino = CENTRO_X - (width / 2);
        r.moveHorizontal(xDestino - X);
        r.moveVertical(y - Y);
    }

    /** 
     * Calcula la coordenada X  para ubicar la rueda del indice especificado (empieza en 1) 
     */
    public int xRueda(int indice) {
        int xInicioMedio = CENTRO_X - (anchoMedio / 2);
        return xInicioMedio + MARGEN + (indice - 1) * (WIDTH_WHEEL + ESPACIO_WHEEL);
    }

    /** Reajusta el tamaño de la pantalla y centra todas las ruedas existentes */
    private void redimension(int oldWidthMedio, int oldHeightBase) {
        int despMedioTope = -(anchoMedio - oldWidthMedio) / 2;
        int despBase        = -(anchoBase - oldHeightBase) / 2;

        rectanguloMedio.changeSize(HEIGHT_MEDIO, anchoMedio);
        rectanguloMedio.moveHorizontal(despMedioTope);

        rectanguloTope.changeSize(HEIGHT_TOPE, anchoTope);
        rectanguloTope.moveHorizontal(despMedioyTope);

        rectanguloBase.changeSize(HEIGHT_BASE, anchoBase);
        rectanguloBase.moveHorizontal(despBase);

        for (Wheel rueda : listaRuedas) {
            rueda.moverHorizontal(despMedioyTope);
        }
    }

    public void addWheel() {
        if (listaRuedas.size() < MAX_WHEELS) {
            int anchoViejoMedio = anchoMedio;
            int anchoViejoBase  = anchoBase;

            actualizarAnchos(listaRuedas.size() + 1);
            redimension(anchoViejoMedio, anchoViejoBase);

            int yRueda = Y_INICIAL + HEIGHT_TOPE + 15;
            int xNueva = xRueda(listaRuedas.size() + 1);
            listaRuedas.add(new Wheel(xNueva, yRueda));
        } else {
            JOptionPane.showMessageDialog(null,
                "No puedes agregar más ruedas. Máximo permitido: " + MAX_WHEELS,
                "Límite Máximo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    public void delWheel() {
        if (listaRuedas.size() > MIN_WHEELS) {
            Wheel ultima = listaRuedas.remove(listaRuedas.size() - 1);
            ultima.makeInvisible();

            int anchoViejoMedio = anchoMedio;
            int anchoViejoBase  = anchoBase;

            actualizarAnchos(listaRuedas.size());
            redimension(anchoViejoMedio, anchoViejoBase);
        } else {
            JOptionPane.showMessageDialog(null,
                "No puedes eliminar más ruedas. Mínimo permitido: " + MIN_WHEELS,
                "Límite Mínimo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    public int getWheels() {
        return listaRuedas.size();
    }
    
}