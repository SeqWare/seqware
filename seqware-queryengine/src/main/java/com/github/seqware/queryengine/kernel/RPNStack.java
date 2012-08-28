package com.github.seqware.queryengine.kernel;


import com.github.seqware.queryengine.model.Tag;
import java.io.Serializable;
import java.util.*;

/**
 * A stack of arguments and operations in Reverse Polish Notation (RPN, http://en.wikipedia.org/wiki/Reverse_Polish_notation).
 *
 * @author jbaran
 */
public class RPNStack implements Serializable {

    private List<Object> stack;
    private Map<Parameter, Integer> parameters = new HashMap<Parameter, Integer>();

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

    public abstract static class Parameter implements Serializable {
        private String name;

        protected Parameter(String name) {
            this.name = name;
        }

        public final String getName() {
            return name;
        }

        public String getUniqueName() {
            return this.getClass().getName() + "://" + name;
        }

        @Override
        public int hashCode() {
            return this.getUniqueName().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Parameter))
                return false;

            return this.getUniqueName().equals(((Parameter)o).getUniqueName());
        }
    }

    /**
     * Represents attributes of features that are passed as parameter.
     */
    public static class FeatureAttribute extends Parameter {
        public FeatureAttribute(String name) {
            super(name);
        }
    }

    /**
     * Represents tags whose occurrence is probed.
     */
    public static class TagOccurrence extends Parameter {
        public TagOccurrence(String name) {
            super(name);
        }
    }

    /**
     * Represents tags whose occurrence is tested -- including the tags children!
     */
    public static class TagHierarchicalOccurrence extends Parameter {
        private String tagSetRowKey;

        public TagHierarchicalOccurrence(String name, String tagSetRowKey) {
            super(name);
        }
    }

    /**
     * Represents tag/value pairs that should be present.
     */
    public static class TagValuePresence extends Parameter {
        private Tag.ValueType type;
        private Object value;

        public TagValuePresence(String name, Tag.ValueType type, Object value) {
            super(name);

            this.type = type;
            this.value = value;
        }

        public final String getUniqueName() {
            return this.getClass().getName() + "://" + this.getName() + "#" + getValue();
        }

        public Tag.ValueType getType() {
            return this.type;
        }

        public Object getValue() {
            return this.value;
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
            } else if (argument instanceof Parameter) {
                this.parameters.put((Parameter)argument, i);
            } else
                throw new UnsupportedOperationException("An RPNStack can only be populated with Constant, Parameter, or Operation instances. You provided a " + argument.getClass().getName());
        }
    }

    /**
     * Sets a parameter to a concrete value.
     *
     * @param parameter Parameter whose value is to be set.
     * @param value Value of the parameter.
     */
    public void setParameter(Parameter parameter, Object value) {
        this.stack.set(this.parameters.get(parameter), value);
    }

    /**
     * Returns the parameters of the argument stack, i.e., everything that is not a constant and considered to be a variable.
     * @return
     */
    public Set<Parameter> getParameters() {
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
        if (a == null || b == null){
            return a == null && b == null;
        }
        return a.equals(b);
    }
}
