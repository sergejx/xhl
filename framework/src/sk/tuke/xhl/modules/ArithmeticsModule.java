package sk.tuke.xhl.modules;

import sk.tuke.xhl.core.GenericModule;
import sk.tuke.xhl.core.Producer;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticsModule extends GenericModule {

    @Element(name = "+")
    public Producer<Double> plus(Producer<Double> arg1, Producer<Double> arg2) {
        return new ArithmeticsProducer(ArithmOperation.ADD, arg1, arg2);
    }

    @Element(name = "-")
    public Producer<Double> minus(Producer<Double> arg1, Producer<Double> arg2) {
        return new ArithmeticsProducer(ArithmOperation.SUB, arg1, arg2);
    }

    @Element(name = "*")
    public Producer<Double> multiply(Producer<Double> arg1, Producer<Double> arg2) {
        return new ArithmeticsProducer(ArithmOperation.MUL, arg1, arg2);
    }

    @Element(name = "/")
    public Producer<Double> divide(Producer<Double> arg1, Producer<Double> arg2) {
        return new ArithmeticsProducer(ArithmOperation.DIV, arg1, arg2);
    }

    public enum ArithmOperation {
        ADD, SUB, MUL, DIV
    }

    public static class ArithmeticsProducer implements Producer<Double> {
        public final List<Producer<Double>> operands = new ArrayList<>();
        public final ArithmOperation operation;


        public ArithmeticsProducer(ArithmOperation operation,
                                   Producer<Double> op1, Producer<Double> op2) {
            this.operation = operation;
            operands.add(op1);
            operands.add(op2);
        }

        @Override
        public Double toValue() {
            double[] args = new double[operands.size()];
            for (int i = 0; i < operands.size(); i++)
                args[i] = operands.get(i).toValue();
            double result;
            switch (operation) {
            case ADD:
                result = 0;
                for (double val : args) {
                    result += val;
                }
                return result;
            case SUB:
                result = args.length > 1 ? args[0] : - args[0];
                for (int i = 1; i < args.length; i++) {
                    result -= args[i];
                }
                return result;
            case MUL:
                result = 1;
                for (double val : args) {
                    result *= val;
                }
                return result;
            case DIV:
                result = args.length > 1 ? args[0] : 1 / args[0];
                for (int i = 1; i < args.length; i++) {
                    result /= args[i];
                }
                return result;
            default:
                return null;
            }
        }

        @Override
        public String toCode() {
            return null;
        }
    }
}
