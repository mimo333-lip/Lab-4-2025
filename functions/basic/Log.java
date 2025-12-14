package functions.basic;

import functions.Function;
import functions.FunctionPoint;

public class Log implements Function {
    private double base;
    
    public Log(double base) {
        if (base <= 0 || FunctionPoint.equals(base, 1)) {
            throw new IllegalArgumentException("Base must be positive and not equal to 1");
        }
        this.base = base;
    }
    
    @Override
    public double getLeftDomainBorder() {
        return 0;
    }
    
    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }
    
    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN;
        }
        return Math.log(x) / Math.log(base);
    }
}
