package xhl.modules;

import java.util.Map;

import xhl.core.Producer;
import xhl.core.GenericModule;

import com.google.common.collect.ImmutableMap;

import static xhl.modules.LogicsModule.LogicsOperation.*;

public class LogicsModule extends GenericModule {
    @Function(name="&")
    public Producer<Boolean> and(Producer<Boolean> arg1, Producer<Boolean> arg2) {
        return new LogicsProducer(AND, arg1, arg2);
    }

    @Function(name="|")
    public Producer<Boolean> or(Producer<Boolean> arg1, Producer<Boolean> arg2) {
        return new LogicsProducer(OR, arg1, arg2);
    }

    public Producer<Boolean> not(Producer<Boolean> arg1, Producer<Boolean> arg2) {
        return new LogicsProducer(NOT, arg1, arg2);
    }

    public enum LogicsOperation {
        AND, OR, NOT
    }

    public static class LogicsProducer implements Producer<Boolean> {
        public Producer<Boolean> operand1;
        public Producer<Boolean> operand2;
        public LogicsOperation operation;

        private static final Map<LogicsOperation, String> opcodes =
                ImmutableMap.of(AND, "&&", OR, "||", NOT, "!");

        public LogicsProducer(LogicsOperation operation,
                              Producer<Boolean> op1, Producer<Boolean> op2) {
            this.operation = operation;
            operand1 = op1;
            operand2 = op2;
        }

        @Override
        public Boolean toValue() {
            boolean arg1 = operand1.toValue();
            boolean arg2 = operand2.toValue();;
            switch (operation) {
            case AND:
                return arg1 && arg2;
            case OR:
                return arg1 || arg2;
            case NOT:
                return !arg1;
            default:
                return null;
            }
        }

        @Override
        public String toCode() {
            if (operation == NOT)
                return "(!" + operand1.toCode() + ")";
            else {
                String op = opcodes.get(operation);
                return "(" + operand1.toCode() + op + operand2.toCode() + ")";
            }
        }
    }
}
