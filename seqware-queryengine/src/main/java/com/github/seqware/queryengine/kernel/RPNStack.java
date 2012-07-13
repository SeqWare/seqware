package com.github.seqware.queryengine.kernel;


import java.util.*;

/**
 * A stack of arguments and operations in Reverse Polish Notation (RPN, http://en.wikipedia.org/wiki/Reverse_Polish_notation).
 *
 * @author jbaran
 */
public class RPNStack {

    private List<Object> stack;
    private Map<Object, Integer> parameters = new HashMap<Object, Integer>();

    /**
     * Operations for combining query constraints.
     */
    public enum Operation {
        /**
         * Takes two truth value arguments that both need to evaluate to true for the result to be true.
         */
        AND,

        /**
         * Takes two truth value arguments where one of it needs to evaluate to true for the result to be true.
         */
        OR,

        /**
         * Takes one truth value argument and inverts its value.
         */
        NOT,

        /**
         * Takes two arguments of any kind and compares whether they are equal.
         */
        EQUAL
    }

    public static class Constant {
        private final Object constant;

        public Constant(Object constant) {
            this.constant = constant;
        }

        public Object getValue() {
            return this.constant;
        }
    }

    public RPNStack(Object ... arguments) {
        this.stack = Arrays.asList(arguments);

        for (int i = 0; i < arguments.length; i++) {
            Object argument = arguments[i];

            if (argument instanceof Constant) {
                this.stack.set(i, ((Constant)argument).getValue());
            } else if (argument instanceof Operation) {
                // Do nothing.
            } else // Has to be a parameter now...
                this.parameters.put(argument, i);
        }
    }

    /**
     * Sets a parameter to a concrete value.
     *
     * @param parameter Parameter whose value is to be set.
     * @param value Value of the parameter.
     */
    public void setParameter(Object parameter, Object value) {
        this.stack.set(this.parameters.get(parameter), value);
    }

    /**
     * Returns the parameters of the argument stack, i.e., everything that is not a constant and considered to be a variable.
     * @return
     */
    public Set<Object> getParameters() {
        return this.parameters.keySet();
    }

    /**
     * RPN evaluation.
     *
     * @return Evaluation result when interpreting the stack contents in RPN.
     */
    public Object evaluate() {
        List<Object> rpnStack = new LinkedList<Object>(this.stack);
        List<Object> operationArguments = new LinkedList<Object>();

        while (!rpnStack.isEmpty()) {
            Object object = rpnStack.remove(0);

            if (object instanceof Operation) {
                Operation op = (Operation)object;
                Object a, b;

                switch (op) {
                case AND:
                    b = operationArguments.remove(0);
                    a = operationArguments.remove(0);
                    operationArguments.add(0, this.and(a, b));
                    break;
                case OR:
                    b = operationArguments.remove(0);
                    a = operationArguments.remove(0);
                    operationArguments.add(0, this.or(a, b));
                    break;
                case NOT:
                    a = operationArguments.remove(0);
                    operationArguments.add(0, this.not(a));
                    break;
                case EQUAL:
                    b = operationArguments.remove(0);
                    a = operationArguments.remove(0);
                    operationArguments.add(0, this.equal(a, b));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported operation: " + op);
                }
            } else
                operationArguments.add(0, object);
        }

        return operationArguments.remove(0);
    }

    /**
     * Carries out a Boolean AND.
     */
    private boolean and(Object a, Object b) {
        if (a instanceof Boolean && b instanceof Boolean)
            return ((Boolean)a) && ((Boolean)b);

        return false;
    }

    /**
     * Carries out a Boolean OR.
     */
    private boolean or(Object a, Object b) {
        if (a instanceof Boolean && b instanceof Boolean)
            return ((Boolean)a) || ((Boolean)b);

        return false;
    }

    /**
     * Carries out a Boolean NOT.
     */
    private boolean not(Object a) {
        if (a instanceof Boolean)
            return !((Boolean)a);

        return false;
    }

    /**
     * Carries out an exact match.
     */
    private boolean equal(Object a, Object b) {
        return a.equals(b);
    }
}
