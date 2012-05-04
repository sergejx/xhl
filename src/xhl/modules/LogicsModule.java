package xhl.modules;

import xhl.core.Builder;
import xhl.core.GenericModule;
import static xhl.modules.LogicsModule.LogicsOperation.*;

public class LogicsModule extends GenericModule {
    @Function(name="&")
    public Builder<Boolean> and(Builder<Boolean> arg1, Builder<Boolean> arg2) {
        return new LogicsBuilder(AND, arg1, arg2);
    }

    @Function(name="|")
    public Builder<Boolean> or(Builder<Boolean> arg1, Builder<Boolean> arg2) {
        return new LogicsBuilder(OR, arg1, arg2);
    }

    public Builder<Boolean> not(Builder<Boolean> arg1, Builder<Boolean> arg2) {
        return new LogicsBuilder(NOT, arg1, arg2);
    }

    public enum LogicsOperation {
        AND, OR, NOT
    }

    static class LogicsBuilder implements Builder<Boolean> {
        public Builder<Boolean> operand1;
        public Builder<Boolean> operand2;
        public LogicsOperation operation;

        public LogicsBuilder(LogicsOperation operation,
                Builder<Boolean> op1, Builder<Boolean> op2) {
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
            // TODO Auto-generated method stub
            return null;
        }
    }
}
