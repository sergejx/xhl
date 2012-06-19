package xhl.modules;

import java.util.Map;

import xhl.core.Producer;
import xhl.core.GenericModule;

import com.google.common.collect.ImmutableMap;

import static xhl.modules.RelationsModule.RelationOperation.*;

public class RelationsModule extends GenericModule {

    @Element(name=">")
    public Producer<Boolean> gt(Producer<Double> arg1, Producer<Double> arg2) {
        return new RelationProducer(GT, arg1, arg2);
    }

    @Element(name="<")
    public Producer<Boolean> lt(Producer<Double> arg1, Producer<Double> arg2) {
        return new RelationProducer(LT, arg1, arg2);
    }

    @Element(name="=")
    public Producer<Boolean> eq(Producer<Double> arg1, Producer<Double> arg2) {
        return new RelationProducer(EQ, arg1, arg2);
    }

    @Element(name=">=")
    public Producer<Boolean> gteq(Producer<Double> arg1, Producer<Double> arg2) {
        return new RelationProducer(GTEQ, arg1, arg2);
    }

    @Element(name="<=")
    public Producer<Boolean> lteq(Producer<Double> arg1, Producer<Double> arg2) {
        return new RelationProducer(LTEQ, arg1, arg2);
    }

    public enum RelationOperation {
        GT, LT, EQ, GTEQ, LTEQ
    }

    public static class RelationProducer implements Producer<Boolean> {
        public Producer<Double> operand1;
        public Producer<Double> operand2;
        public RelationOperation operation;

        private static final Map<RelationOperation, String> opcodes =
            ImmutableMap.of(GT, ">", LT, "<", EQ, "==", GTEQ, ">=", LTEQ, "<=");

        public RelationProducer(RelationOperation operation,
                                Producer<Double> op1, Producer<Double> op2) {
            this.operation = operation;
            operand1 = op1;
            operand2 = op2;
        }

        @Override
        public Boolean toValue() {
            double arg1 = operand1.toValue();
            double arg2 = operand2.toValue();
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
