import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * Casos de prueba de unidad del Ciclo 2: intercambio de ruedas (req. 9),
 * fijar/soltar rueda (req. 10), rotación exacta (req. 11) y configuración
 * forzada (req. 12). Todas las pruebas corren en modo invisible.
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class SlotMachineC2Test {

    private SlotMachine machine;

    @BeforeEach
    public void setUp() {
        machine = new SlotMachine();
        machine.makeInvisible();
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }

    // ---------- Requisito 9: swap ----------

    /** Que debería hacer: intercambiar los símbolos visibles de dos ruedas. */
    @Test
    public void swapShouldExchangeVisibleSymbols() {
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.swap(1, 2);
        String[] config = machine.configuration();
        assertEquals("blue", config[0]);
        assertEquals("red", config[1]);
        assertTrue(machine.ok());
    }

    /** Que NO debería hacer: intercambiar si una de las ruedas está bloqueada. */
    @Test
    public void swapShouldFailWhenAWheelIsLocked() {
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.lock(1);
        machine.swap(1, 2);
        boolean result = machine.ok();
        String[] config = machine.configuration();
        
        assertFalse(result);
        assertEquals("red", config[0]);
        assertEquals("blue", config[1]);
    }

    // ---------- Requisito 10: lock / unlock ----------

    /** Que NO debería hacer: una rueda bloqueada no debe girar. */
    @Test
    public void lockShouldPreventWheelFromSpinning() {
        machine.placeSymbol(1, "red");
        machine.lock(1);
        machine.spin(1, 1);
        
        boolean result = machine.ok();
        String[] config = machine.configuration();
        
        assertFalse(result);
        assertEquals("red",config[0]);

    }

    /** Que debería hacer: tras desbloquear, la rueda vuelve a girar normalmente. */
    @Test
    public void unlockShouldAllowWheelToSpinAgain() {
        machine.placeSymbol(1, "red");
        machine.lock(1);
        machine.unlock(1);
        machine.spin(1, 1);
        assertEquals("blue", machine.configuration()[0]);
        assertTrue(machine.ok());
    }

    // ---------- Requisito 11: spin(wheel, steps) ----------

    /** Que debería hacer: avanzar exactamente la cantidad de pasos indicada. */
    @Test
    public void spinWithStepsShouldAdvanceExactAmount() {
        machine.placeSymbol(1, "red");
        machine.spin(1, 1);
        assertEquals("blue", machine.configuration()[0]);
        assertTrue(machine.ok());
    }

    /** Que debería hacer: la rotación es circular (vuelve al inicio tras el último símbolo). */
    @Test
    public void spinWithStepsShouldWrapAroundCircularly() {
        machine.placeSymbol(1, "green");
        machine.spin(1, 1);
        assertEquals("red", machine.configuration()[0]);
        assertTrue(machine.ok());
    }

    /** Que NO debería hacer: girar si la máquina no tiene símbolos registrados. */
    @Test
    public void spinWithStepsShouldFailWhenNoSymbolsExist() {
        SlotMachine empty = new SlotMachine();
        empty.makeInvisible();
        empty.spin(1, 1);
        assertFalse(empty.ok());
    }

    // ---------- Requisito 12: spin(setSymbols) ----------

    /** Que debería hacer: dejar la máquina exactamente en la configuración pedida. */
    @Test
    public void spinWithConfigurationShouldSetExactSymbols() {
        machine.spin(new String[]{"red", "blue", "green"});
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.configuration());
        assertTrue(machine.ok());
    }

    /** Que NO debería hacer: aceptar un arreglo con longitud distinta al número de ruedas. */
    @Test
    public void spinWithConfigurationShouldFailWithWrongLength() {
        machine.spin(new String[]{"red", "blue"});
        assertFalse(machine.ok());
    }

    /** Que NO debería hacer: aceptar un símbolo que no existe en el catálogo. */
    @Test
    public void spinWithConfigurationShouldFailWithUnknownSymbol() {
        machine.spin(new String[]{"red", "blue", "purple"});
        assertFalse(machine.ok());
    }

    /** Que debería hacer: respetar las ruedas bloqueadas, ignorando lo pedido para ellas. */
    @Test
    public void spinWithConfigurationShouldRespectLockedWheels() {
        machine.placeSymbol(2, "blue");
        machine.lock(2);
        machine.spin(new String[]{"red", "green", "green"});
        assertEquals("blue", machine.configuration()[1]);
        assertTrue(machine.ok());
    }
}