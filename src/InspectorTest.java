import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class InspectorTest {
    // Code for capturing sysout taken from: https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private Inspector inspector = new Inspector();

    @Test
    void testGetConstructorsPass() {
        ClassA classA = new ClassA();

        inspector.getConstructors(classA.getClass());

        assertEquals("Class name: ClassA\nConstructor name: ClassA\nConstructor Modifiers: public\nConstructor name: ClassA\nConstructor Modifiers: public\nConstructor parameter types: \nint",
                outContent.toString());
    }

}