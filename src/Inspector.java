import java.lang.reflect.*;


public class Inspector {
    private int depth = 0;
    private boolean rec = false;

    public void inspect(Object obj, boolean recursive) {
        rec = recursive;
        Class c = obj.getClass();
        inspectClass(c, obj);
    }


    public void inspectClass(Class c, Object obj) {
        while (c != null) { // Get info on super classes until the base class object is reached
            outputAllDetails(c, obj);
            c = c.getSuperclass();
            depth++;
        }
    }


    public void outputAllDetails(Class c, Object obj) {
        getIfArray(c, obj);
        getClassName(c);
        getInterfaces(c);
        getConstructors(c);
        getMethods(c);
        getFields(c, obj);
    }

    public void outputAllDetailsInterface(Class c) {
        getInterfaces(c);
        getClassName(c);
        getConstructors(c);
        getMethods(c);
    }


    public void getClassName(Class c) {
        String className = c.getName();
        if (c.isInterface()) {
            printWithTabs("Interface name: " + className);
        }
        else {
            printWithTabs("Class name: " + className);
        }
    }


    public void getInterfaces(Class c) {
        Class[] interfaces = c.getInterfaces();
        for (Class ci : interfaces) {
            outputAllDetailsInterface(ci);
            depth++;
        }
    }


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
                    }

                } catch(Exception e){
                    printWithTabs("Exception getting constructor: ");
                    e.printStackTrace();
                }
            }
            System.out.println();
    }


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
                        checkIfRecursive(value);
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


    public void checkIfRecursive(Object obj) {
        try {
            Class c = obj.getClass();
            if (!c.isPrimitive()) {
                if (rec) {
                    c = obj.getClass();
                    inspect(c, false);
                } else {
                    printWithTabs(Integer.toString(System.identityHashCode(obj)));
                }
            } else {
                printWithTabs(c.toString());
            }
        }
        catch (NullPointerException e) {
        }
    }


    public void displayParameters(Parameter[] params) {
        String paramType;
        for (Parameter pi : params) {
            paramType = pi.getType().toString();
            printWithTabs(paramType);
        }
    }


    public void getIfArray(Class c, Object obj) {
        Class<?> comp = c.getComponentType();
        if (comp != null) {
            int length = Array.getLength(obj);
            printWithTabs("Array name: " + comp.getName());
            printWithTabs("Array type: " + comp.getTypeName());
            printWithTabs("Array length: " + length);
            printWithTabs("Array contents: ");
            for (int i = 0; i < length; i++) {
                Object arrayElement = Array.get(obj, i);
                getIfArray(comp, arrayElement);
                checkIfRecursive(arrayElement);
                try {
                    printWithTabs(arrayElement.toString());
                }
                catch (NullPointerException e) {
                    return;
                }
            }
        }
    }


    public void printWithTabs(String printString) {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
        System.out.print(printString + "\n");
    }
}
