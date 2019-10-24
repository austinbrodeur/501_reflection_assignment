import java.lang.reflect.*;


public class Inspector {


    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }


    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
        while (c != null) { // Get info on super classes until the base class object it reached
            outputAllDetails(c, recursive, depth);
            c = c.getSuperclass();
            depth = depth + 1;
        }
    }


    private void outputAllDetails(Class c, boolean recursive, int depth) { // Gets all details on the current class other than the name (as those are already displayed)
        printBreak();
        getClassName(c, depth);
        getConstructors(c, recursive, depth);
        getMethods(c, recursive, depth);
        getInterfaces(c, depth);
    }


    private void getClassName(Class c, int depth) {
        String className = c.getName();
        printWithTabs(depth, "Class / interface name: " + className);
    }


    private void getInterfaces(Class c, int depth) {
        Class[] interfaces = c.getInterfaces();

        for (Class ci : interfaces) {
            getClassName(ci, depth);
        }
    }


    private void getConstructors(Class c, boolean recursive, int depth) {
        try {
            int modifier;
            String constructorName;
            Parameter[] constructorParams;
            String constructorMods;

            Constructor[] constructors = c.getConstructors();

            for (Constructor ci : constructors) { // Iterate through constructors
                constructorName = ci.getName();
                modifier = ci.getModifiers();
                constructorMods = Modifier.toString(modifier);


                printWithTabs(depth, "Constructor name: " + constructorName);
                printWithTabs(depth, "Constructor Modifiers: " + constructorMods);

                constructorParams = ci.getParameters();

                if (constructorParams.length != 0) {
                    printWithTabs(depth, "Constructor parameter types: ");
                    displayParameters(constructorParams, recursive, depth);
                }
            }
        } catch (Exception e) {
            printWithTabs(depth,"Exception getting constructor: ");
            e.printStackTrace();
        }
        System.out.println();
    }


    private void getMethods(Class c, boolean recursive, int depth) {
        int modifier;
        String methodName;
        String methodReturnType;
        String methodMods;
        Class<?>[] methodExceptions;
        Parameter[] methodParams;

        Method[] classMethods = c.getDeclaredMethods();

        for (Method mi : classMethods) {
            methodName = mi.getName();
            methodReturnType = mi.getReturnType().toString();
            modifier = mi.getModifiers();
            methodMods = Modifier.toString(modifier);
            methodExceptions = mi.getExceptionTypes();
            methodParams = mi.getParameters();

            printWithTabs(depth, "Method name: " + methodName);
            printWithTabs(depth, "Method return type: " + methodReturnType);
            printWithTabs(depth, "Method modifiers: " + methodMods);

            if (methodExceptions.length != 0) {
                printWithTabs(depth, "Method exception(s): ");
                for (Class ci : methodExceptions) {
                    printWithTabs(depth, ci.toString());
                }
            }
            if (methodParams.length != 0) {
                printWithTabs(depth, "Method parameter types: ");
                displayParameters(methodParams, recursive, depth);
            }
            System.out.println();
        }
        System.out.println();
    }


    private void displayParameters(Parameter[] params, boolean recursive, int depth) {
        String paramType;
        for (Parameter pi : params) {
            paramType = pi.getType().toString();
            printWithTabs(depth, paramType);
        }
    }


    private void printWithTabs(int depth, String printString) {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
        System.out.print(printString + "\n");
    }


    private void printBreak() {
        System.out.println("======================");
    }
}
