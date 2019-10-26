import java.lang.reflect.*;

/**
 * Austin Brodeur 30007952
 *
 * Note that I didn't implement recursive inspection, as it was causing a lot of instability (or at least what seemed like it) so there is no need to test for it
 */


public class Inspector {
    private int depth = 0;
    private boolean rec = false;


    /** Inspects the given object
     *
     * @param obj       Object to be inspected
     * @param recursive Enables/disables recursive mode (not used)
     */
    public void inspect(Object obj, boolean recursive) {
        rec = recursive;
        Class c = obj.getClass();
        inspectClass(c, obj);
    }


    /** Goes up the class hierarchy until no class is left and calls outputAllDetails on each class
     *
     * @param c     Class of object to be inspected
     * @param obj   Object to be inspected
     */
    public void inspectClass(Class c, Object obj) {
        while (c != null) { // Get info on super classes until the base class object is reached
            outputAllDetails(c, obj);
            c = c.getSuperclass();
            depth++;
        }
    }


    /** Outputs all the details of the class/object to be inspected
     *
     * @param c     Class of object to be inspected
     * @param obj   Object to be inspected
     */
    public void outputAllDetails(Class c, Object obj) {
        getIfArray(c, obj);
        getClassName(c);
        getConstructors(c);
        getMethods(c);
        getFields(c, obj);
        getInterfaces(c);
    }


    /** Outputs all details on interfaces only (fields/arrays not checked as they will all be empty/constant and are not typically in interfaces regardless)
     *
     * @param c Class to be inspected
     */
    public void outputAllDetailsInterface(Class c) {
        getInterfaces(c);
        getClassName(c);
        getConstructors(c);
        getMethods(c);
    }


    /** Finds all interfaces for a given class
     *
     * @param c Class to check interfaces on
     */
    public void getInterfaces(Class c) {
        Class[] interfaces = c.getInterfaces();
        for (Class ci : interfaces) {
            outputAllDetailsInterface(ci);
            depth++;
        }
    }


    /** Gets class name
     *
     * @param c Class to get name of
     */
    public void getClassName(Class c) {
        String className = c.getName();
        if (c.isInterface()) {
            printWithTabs("Interface name: " + className);
        }
        else {
            printWithTabs("Class name: " + className);
        }
    }


    /** Gets class constructors
     *
     * @param c Class to get constructors of
     */
    public void getConstructors(Class c) {
            int modifier;
            String constructorName;
            Parameter[] constructorParams;
            String constructorMods;

            Constructor[] constructors = c.getConstructors();

            if (constructors.length != 0) {
                try {
                    for (Constructor ci : constructors) { // Iterate through constructors
                        constructorName = ci.getName();
                        modifier = ci.getModifiers();
                        constructorMods = Modifier.toString(modifier);

                        printWithTabs("Constructor name: " + constructorName);
                        printWithTabs("Constructor Modifiers: " + constructorMods);

                        constructorParams = ci.getParameters();

                        if (constructorParams.length != 0) {
                            printWithTabs("Constructor parameter types: ");
                            displayParameters(constructorParams);
                        }
                        System.out.println();
                    }

                } catch(Exception e){
                    printWithTabs("Exception getting constructor: ");
                    e.printStackTrace();
                }
            }
            System.out.println();
    }


    /** Gets class methods
     *
     * @param c Class to get methods of
     */
    public void getMethods(Class c) {
        int modifier;
        String methodName;
        String methodReturnType;
        String methodMods;
        Class<?>[] methodExceptions;
        Parameter[] methodParams;

        Method[] classMethods = c.getDeclaredMethods();

        if (classMethods.length != 0) {
            for (Method mi : classMethods) {
                mi.setAccessible(true);
                methodName = mi.getName();
                methodReturnType = mi.getReturnType().toString();
                methodExceptions = mi.getExceptionTypes();
                methodParams = mi.getParameters();

                modifier = mi.getModifiers();
                methodMods = Modifier.toString(modifier);

                printWithTabs("Method name: " + methodName);
                printWithTabs("Method return type: " + methodReturnType);
                printWithTabs("Method modifiers: " + methodMods);

                if (methodExceptions.length != 0) {
                    printWithTabs("Method exception(s): ");
                    for (Class ci : methodExceptions) {
                        printWithTabs(ci.toString());
                    }
                }
                if (methodParams.length != 0) {
                    printWithTabs("Method parameter types: ");
                    displayParameters(methodParams);
                }
                System.out.println();
            }
            System.out.println();
        }
    }


    /** Gets class fields
     *
     * @param c Class to get fields of
     * @param obj Objects to get fields of
     */
    public void getFields(Class c, Object obj) {
        Field[] fields = c.getDeclaredFields();
        Field singleField;
        String fieldName;
        String fieldType;
        String fieldMods;
        Object value;
        int modifier;

        if (fields.length != 0) {
            try {
                for (Field fi : fields) {
                    modifier = fi.getModifiers();
                    fieldMods = Modifier.toString(modifier);
                    fieldName = fi.getName();
                    singleField = c.getDeclaredField(fi.getName()); // Made to get access to private fields
                    singleField.setAccessible(true);
                    fieldType = fi.getType().toString();
                    value = singleField.get(obj);

                    printWithTabs("Field name: " + fieldName);
                    printWithTabs("Field type: " + fieldType);
                    printWithTabs("Field modifiers: " + fieldMods);
                    try {
                        printWithTabs("Field contents: ");
                        getIfArray(value.getClass(), value);
                    } catch (NullPointerException n) {
                        printWithTabs("Empty field");
                    }
                    System.out.println();
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.out.println("Error finding or accessing fields: ");
                e.printStackTrace();
            }
            System.out.println();
        }
    }


    /** Lists parameters for methods and constructors
     *
     * @param params List of parameters to display
     */
    public void displayParameters(Parameter[] params) {
        String paramType;
        for (Parameter pi : params) {
            paramType = pi.getType().toString();
            printWithTabs(paramType);
        }
    }


    /** Checks if type is array and displays its information. Also displays 2d arrays. (Name is displayed before this method, so the array name is not contained here)
     *
     * @param c Array class
     * @param obj Array object
     */
    public void getIfArray(Class c, Object obj) {
        Class<?> comp = c.getComponentType();
        if (comp != null) {
            int length = Array.getLength(obj);
            printWithTabs("Array type: " + comp.getTypeName());
            printWithTabs("Array length: " + length);
            printWithTabs("Array contents: ");
            for (int i = 0; i < length; i++) {
                Object arrayElement = Array.get(obj, i);
                try {
                    getIfArray(arrayElement.getClass(), arrayElement);
                    printWithTabs(arrayElement.toString());
                }
                catch (NullPointerException e) {

                }
            }
        }
    }


    /** Prints with a given number of tab indents
     *
     * @param printString String to display
     */
    public void printWithTabs(String printString) {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
        System.out.print(printString + "\n");
    }
}
