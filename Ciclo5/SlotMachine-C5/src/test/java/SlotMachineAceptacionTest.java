import static org.junit.jupiter.api.Assertions.*;
import javax.swing.JOptionPane;
import org.junit.jupiter.api.Test;

/**
 * Acceptance test of the simulator: shows a visible machine that mixes every
 * type of wheel and of symbol, spins it slowly and asks the user to confirm
 * that the types can be told apart
 * It needs a person to answer, so it is excluded from "mvn test" and must be
 * run by hand from VS Code (Testing view)
 *
 * @author Mateo Sanchez
 * @author Maria Angelica Perez
 */
public class SlotMachineAceptacionTest {

    private static final int PAUSE = 1200;

    /** Should: show and spin a mixed machine whose types the user can recognize */
    @Test
    public void deberiaMostrarTodosLosTiposDeRuedasYSimbolos() throws InterruptedException {
        SlotMachine machine = new SlotMachine(true);

        machine.addSymbol(1, "red");
        machine.addSymbol("ephemeral", 2, "blue");
        machine.addSymbol("shy", 3, "green");
        machine.addSymbol("joker", 4, "purple");
        assertTrue(machine.ok());

        machine.addWheel("lefty", 1);
        assertTrue(machine.ok());
        machine.addWheel("rebel", 1);
        assertTrue(machine.ok());
        assertEquals(SlotMachine.MIN_WHEELS + 2, machine.getWheels());
        Thread.sleep(PAUSE);

        for (int i = 0; i < 5; i++) {
            machine.spin();
            assertTrue(machine.ok());
            Thread.sleep(PAUSE);
        }

        machine.lock(1);
        machine.swap(2, 3);
        assertTrue(machine.ok());
        Thread.sleep(PAUSE);

        int answer = JOptionPane.showConfirmDialog(null,
            "¿Se distinguen las ruedas lefty y rebel y los símbolos ephemeral, shy y joker?",
            "Prueba de aceptación", JOptionPane.YES_NO_OPTION);
        machine.makeInvisible();
        assertEquals(JOptionPane.YES_OPTION, answer);
    }
}
