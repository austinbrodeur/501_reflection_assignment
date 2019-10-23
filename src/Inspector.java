import java.lang.reflect.*;


public class Inspector {


    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }


    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
        while (!c.equals(Object.class)) { // Get info on super classes until the base class object it reached
            outputAllDetails(c, recursive);
            c = c.getSuperclass();
        }
    }


    private void outputAllDetails(Class c, boolean recursive) { // Gets all details on the current class other than the name (as those are already displayed)
        printBreak();
        getClassName(c);
        getConstructors(c, recursive);
        getMethods(c, recursive);
        getInterfaces(c);
    }


    private void getClassName(Class c) {
        String className = c.getName();
        System.out.println("\nClass / interface name: " + className);
    }


    private void getInterfaces(Class c) {
        Class[] interfaces = c.getInterfaces();

        for (Class ci : interfaces) {
            getClassName(ci);
        }
    }


    private void getConstructors(Class c, boolean recursive) {
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

                System.out.println("\nConstructor name: " + constructorName);
                System.out.println("Constructor Modifiers: " + constructorMods);

                constructorParams = ci.getParameters();

                if (constructorParams.length != 0) {
                    System.out.println("Constructor parameter types: ");
                    displayParameters(constructorParams, recursive);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception getting constructor: ");
            e.printStackTrace();
        }
        System.out.println();
    }


    private void getMethods(Class c, boolean recursive) {
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

            System.out.println("Method name: " + methodName);
            System.out.println("Method return type: " + methodReturnType);
            System.out.println("Method modifiers: " + methodMods);

            if (methodExceptions.length != 0) {
                System.out.print("Method exception(s): ");
                for (Class ci : methodExceptions) {
                    System.out.print(ci.toString() + "\n");
                }
            }
            if (methodParams.length != 0) {
                System.out.println("Method parameter types: ");
                displayParameters(methodParams, recursive);
            }
            System.out.println();
        }
        System.out.println();
    }


    private void displayParameters(Parameter[] params, boolean recursive) {
        String paramType;
        for (Parameter pi : params) {
            paramType = pi.getType().toString();
            System.out.println(paramType);
        }
    }


    private void printBreak() {
        System.out.println("======================");
    }
}
