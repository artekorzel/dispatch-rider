package dtp.jade.transport;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Evaluator;
import gnu.jel.Library;

public class Calculator {

    public static double calculate(String expr) {
        return Double.parseDouble(calculateValue(expr));
    }

    public static boolean calculateBoolExpr(String expr) {
        return Boolean.parseBoolean(calculateValue(expr));
    }

    @SuppressWarnings("rawtypes")
    private static String calculateValue(String expr) {

        // Set up library
        Class[] staticLib = new Class[]{Math.class};
        Library lib = new Library(staticLib, null, null, null, null);
        try {
            lib.markStateDependent("random", null);
        } catch (CompilationException e) {
            // Can't be also
        }

        // Compile
        CompiledExpression expr_c = null;
        try {
            expr_c = Evaluator.compile(expr, lib);
        } catch (CompilationException ce) {
            System.err.print("--- COMPILATION ERROR :");
            System.err.println(ce.getMessage());
            System.err.print("                       ");
            System.err.println(expr);
            int column = ce.getColumn(); // Column, where error was found
            for (int i = 0; i < column + 23 - 1; i++)
                System.err.print(' ');
            System.err.println('^');
        }
        ;

        Object result = null;
        if (expr_c != null) {

            // Evaluate (Can do it now any number of times FAST !!!)
            try {
                result = expr_c.evaluate(null);
            } catch (Throwable e) {
                System.err.println("Exception emerged from JEL compiled"
                        + " code (IT'S OK) :");
                System.err.print(e);           //fixme
            }
            ;

            // Print result
        }

        return result.toString();
    }

    public static void main(String[] args) {
        int param = 2;
        String expr = "k/4";
        expr = expr.replace("k", Double.toString(param));
        System.out.println(Calculator.calculate(expr));
    }
}
