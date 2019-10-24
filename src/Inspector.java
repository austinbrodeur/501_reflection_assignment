import java.lang.reflect.*;
import java.util.Hashtable;


public class Inspector {
    int depth = 0;

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive);
    }


    private void inspectClass(Class c, Object obj, boolean recursive) {
        while (c != null) { // Get info on super classes until the base class object it reached
            outputAllDetails(c, obj, recursive);
            c = c.getSuperclass();
            depth = depth + 1;
        }
    }


    private void outputAllDetails(Class c, Object obj, boolean recursive) { // Gets all details on the current class other than the name (as those are already displayed)
        printBreak();
        getClassName(c);
        getConstructors(c, recursive);
        getMethods(c, recursive);
        //getInterfaces(c);
        getFields(c, obj, recursive);
    }


    private void getClassName(Class c) {
        String className = c.getName();
        if (c.isInterface()) {
            printWithTabs(depth, "Class name: " + className);
        }
        else {
            printWithTabs(depth, "Interface name: " + className);
        }
    }


    private void getInterfaces(Class c, int depth) {
        Class[] interfaces = c.getInterfaces();

        for (Class ci : interfaces) {
            getClassName(c);
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


                printWithTabs(depth, "Constructor name: " + constructorName);
                printWithTabs(depth, "Constructor Modifiers: " + constructorMods);

                constructorParams = ci.getParameters();

                if (constructorParams.length != 0) {
                    printWithTabs(depth, "Constructor parameter types: ");
                    displayParameters(constructorParams, recursive);
                }
            }
        } catch (Exception e) {
            printWithTabs(depth,"Exception getting constructor: ");
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
            mi.setAccessible(true);
            methodName = mi.getName();
            methodReturnType = mi.getReturnType().toString();
            methodExceptions = mi.getExceptionTypes();
            methodParams = mi.getParameters();

            modifier = mi.getModifiers();
            methodMods = Modifier.toString(modifier);

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
                displayParameters(methodParams, recursive);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void getFields(Class c, Object obj, boolean recursive) {
        Field[] fields = c.getDeclaredFields();
        Field singleField;
        String fieldName;
        String fieldType;
        String fieldMods;
        Object value;
        int modifier;

        try {
            for (Field fi : fields) {
                modifier = fi.getModifiers();
                fieldMods = Modifier.toString(modifier);
                fieldName = fi.getName();
                singleField = c.getDeclaredField(fi.getName());
                singleField.setAccessible(true);
                fieldType = fi.getType().toString();
                value = singleField.get(obj);

                printWithTabs(depth, "Field name: " + fieldName);
                printWithTabs(depth, "Field type: " + fieldType);
                printWithTabs(depth, "Field modifiers: " + fieldMods);
                try {
                    printWithTabs(depth, "Field contents: " + value.toString());
                }
                catch (NullPointerException n) {
                    printWithTabs(depth, "Empty field");
                }
            }
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Error finding or accessing fields: ");
            e.printStackTrace();
        }
        System.out.println();
    }


    private void displayParameters(Parameter[] params, boolean recursive) {
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
