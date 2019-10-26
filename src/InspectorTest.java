import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InspectorTest {

    // Method for capturing sysout taken from https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
    private PrintStream originalSystemOut;
    private ByteArrayOutputStream systemOutContent;

    private Inspector inspector = new Inspector();
    private ClassA classA = new ClassA();

    @BeforeEach
    void redirectSystemOutStream() {

        originalSystemOut = System.out;

        // given
        systemOutContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));
    }

    @AfterEach
    void restoreSystemOutStream() {
        System.setOut(originalSystemOut);
    }


    // These tests are unstable, as the order of the returned methods/fields/contructors etc is sometimes ambiguous (and I can't figure out why) so I didn't rely on these very much. However, I still left them here because of the requirement.
    @Test
    void testGetConstructorsPass() {

        inspector.getConstructors(classA.getClass());

        assertEquals("Constructor name: ClassA\n" +
                        "Constructor Modifiers: public\n" +
                        "Constructor name: ClassA\n" +
                        "Constructor Modifiers: public\n" +
                        "Constructor parameter types: \n" +
                        "int\n" +
                        "\r\n",
                systemOutContent.toString());
    }


    @Test
    void testGetMethodsPass() {

        inspector.getMethods(classA.getClass());

        assertEquals("Method name: run\n" +
                        "Method return type: void\n" +
                        "Method modifiers: public\n" +
                        "\r\n" +
                        "Method name: toString\n" +
                        "Method return type: class java.lang.String\n" +
                        "Method modifiers: public\n" +
                        "\r\n" +
                        "Method name: setVal\n" +
                        "Method return type: void\n" +
                        "Method modifiers: public\n" +
                        "Method exception(s): \n" +
                        "class java.lang.Exception\n" +
                        "Method parameter types: \n" +
                        "int\n" +
                        "\r\n" +
                        "Method name: getVal\n" +
                        "Method return type: int\n" +
                        "Method modifiers: public\n" +
                        "\r\n" +
                        "Method name: printSomething\n" +
                        "Method return type: void\n" +
                        "Method modifiers: private\n" +
                        "\r\n" +
                        "\r\n",
                systemOutContent.toString());
    }


    @Test
    void testGetClassNamePass() {

        inspector.getClassName(classA.getClass());

        assertEquals("Class name: ClassA\n",
                systemOutContent.toString());

    }


}