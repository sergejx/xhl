package xhl.modules;

import xhl.core.GenericModule;

public class ArithmeticsModule extends GenericModule {

    @Function(name = "+")
    public Object plus(Double... args) {
        double result = 0;
        for (double val : args) {
            result += val;
        }
        return result;
    }

    @Function(name = "-")
    public Object minus(Double... args) {
        double result = args.length > 1 ? args[0] : - args[0];
        for (int i = 1; i < args.length; i++) {
            result -= args[i];
        }
        return result;
    }

    @Function(name = "*")
    public Object multiply(Double... args) {
        double result = 1;
        for (double val : args) {
            result *= val;
        }
        return result;
    }

    @Function(name = "/")
    public Object divide(Double... args) {
        double result = args.length > 1 ? args[0] : 1 / args[0];
        for (int i = 1; i < args.length; i++) {
            result /= args[i];
        }
        return result;
    }
}
