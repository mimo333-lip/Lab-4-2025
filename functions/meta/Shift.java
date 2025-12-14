package functions.meta;

import functions.Function;

public class Shift implements Function {
    private Function f;
    private double shiftX, shiftY;
    
    public Shift(Function f, double shiftX, double shiftY) {
        this.f = f;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }
    
    @Override
    public double getLeftDomainBorder() {
        return f.getLeftDomainBorder() + shiftX;
    }
    
    @Override
    public double getRightDomainBorder() {
        return f.getRightDomainBorder() + shiftX;
    }
    
    @Override
    public double getFunctionValue(double x) {
        double originalX = x - shiftX;
        if (originalX < f.getLeftDomainBorder() || originalX > f.getRightDomainBorder()) {
            return Double.NaN;
        }
        return f.getFunctionValue(originalX) + shiftY;
    }
}