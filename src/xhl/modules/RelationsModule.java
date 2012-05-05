package xhl.modules;

import java.util.Map;

import xhl.core.Builder;
import xhl.core.GenericModule;

import com.google.common.collect.ImmutableMap;

import static xhl.modules.RelationsModule.RelationOperation.*;

public class RelationsModule extends GenericModule {

    @Function(name=">")
    public Builder<Boolean> gt(Builder<Double> arg1, Builder<Double> arg2) {
        return new RelationBuilder(GT, arg1, arg2);
    }

    @Function(name="<")
    public Builder<Boolean> lt(Builder<Double> arg1, Builder<Double> arg2) {
        return new RelationBuilder(LT, arg1, arg2);
    }

    @Function(name="=")
    public Builder<Boolean> eq(Builder<Double> arg1, Builder<Double> arg2) {
        return new RelationBuilder(EQ, arg1, arg2);
    }

    @Function(name=">=")
    public Builder<Boolean> gteq(Builder<Double> arg1, Builder<Double> arg2) {
        return new RelationBuilder(GTEQ, arg1, arg2);
    }

    @Function(name="<=")
    public Builder<Boolean> lteq(Builder<Double> arg1, Builder<Double> arg2) {
        return new RelationBuilder(LTEQ, arg1, arg2);
    }

    public enum RelationOperation {
        GT, LT, EQ, GTEQ, LTEQ
    }

    static class RelationBuilder implements Builder<Boolean> {
        public Builder<Double> operand1;
        public Builder<Double> operand2;
        public RelationOperation operation;

        private static final Map<RelationOperation, String> opcodes =
            ImmutableMap.of(GT, ">", LT, "<", EQ, "==", GTEQ, ">=", LTEQ, "<=");

        public RelationBuilder(RelationOperation operation,
                Builder<Double> op1, Builder<Double> op2) {
            this.operation = operation;
            operand1 = op1;
            operand2 = op2;
        }

        @Override
        public Boolean toValue() {
            double arg1 = operand1.toValue();
            double arg2 = operand2.toValue();;
            switch (operation) {
            case GT:
                return arg1 > arg2;
            case LT:
                return arg1 < arg2;
            case EQ:
                return arg1 == arg2;
            case GTEQ:
                return arg1 >= arg2;
            case LTEQ:
                return arg1 <= arg2;
            default:
                return null;
            }
        }

        @Override
        public String toCode() {
            String op = opcodes.get(operation);
            return "(" + operand1.toCode() + op + operand2.toCode() + ")";
        }
    }
}
