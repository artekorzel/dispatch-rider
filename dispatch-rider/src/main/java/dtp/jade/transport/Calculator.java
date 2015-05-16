package dtp.jade.transport;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Evaluator;
import gnu.jel.Library;
import org.apache.log4j.Logger;

public class Calculator {

    private static Logger logger = Logger.getLogger(Calculator.class);

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
            logger.error("--- COMPILATION ERROR :" + ce.getMessage() + "                       " + expr, ce);
            int column = ce.getColumn(); // Column, where error was found
            for (int i = 0; i < column + 23 - 1; i++)
                logger.error(' ');
            logger.error('^');
        }

        Object result = null;
        if (expr_c != null) {

            // Evaluate (Can do it now any number of times FAST !!!)
            try {
                result = expr_c.evaluate(null);
            } catch (Throwable e) {
                logger.error("Exception emerged from JEL compiled"
                        + " code (IT'S OK) :", e);
            }

            // Print result
        }

        return result.toString();
    }

    public static void main(String[] args) {
        int param = 2;
        String expr = "k/4";
        expr = expr.replace("k", Double.toString(param));
        logger.info(Calculator.calculate(expr));
    }
}
