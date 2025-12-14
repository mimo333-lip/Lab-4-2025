package functions;

import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {
    private FunctionPoint[] points;
    private int pointCount;
    
    private static final long serialVersionUID = 1L;
    
    // Конструктор для Externalizable
    public ArrayTabulatedFunction() {
        points = new FunctionPoint[10];
        pointCount = 0;
    }
    
    // Задание 1: новый конструктор
    public ArrayTabulatedFunction(FunctionPoint[] points) throws IllegalArgumentException {
        if (points.length < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Points must be sorted by X in ascending order");
            }
        }
        
        this.pointCount = points.length;
        this.points = new FunctionPoint[pointCount + 10];
        
        for (int i = 0; i < pointCount; i++) {
            this.points[i] = (FunctionPoint) points[i].clone();
        }
    }
    
    public ArrayTabulatedFunction(double leftX, double rightX, int pointCount) throws IllegalArgumentException {
        if (leftX >= rightX || pointCount < 2) {
            throw new IllegalArgumentException("Invalid parameters: leftX must be less than rightX and pointCount >= 2");
        }
        
        this.pointCount = pointCount;
        this.points = new FunctionPoint[pointCount + 10];
        
        double step = (rightX - leftX) / (pointCount - 1);
        for (int i = 0; i < pointCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }
    
    public ArrayTabulatedFunction(double[] xValues, double[] yValues) throws IllegalArgumentException {
        if (xValues.length < 2 || xValues.length != yValues.length) {
            throw new IllegalArgumentException("Arrays must have same length and at least 2 elements");
        }
        
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException("X values must be strictly increasing");
            }
        }
        
        this.pointCount = xValues.length;
        this.points = new FunctionPoint[pointCount + 10];
        
        for (int i = 0; i < pointCount; i++) {
            points[i] = new FunctionPoint(xValues[i], yValues[i]);
        }
    }
    
    private void checkIndex(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
    }
    
    private int findPlaceForX(double x) {
        for (int i = 0; i < pointCount; i++) {
            if (FunctionPoint.equals(points[i].getX(), x)) {
                return -1;
            }
            if (points[i].getX() > x) {
                return i;
            }
        }
        return pointCount;
    }
    
    // Реализация интерфейса Function (Задание 2)
    @Override
    public double getLeftDomainBorder() {
        return points[0].getX();
    }
    
    @Override
    public double getRightDomainBorder() {
        return points[pointCount - 1].getX();
    }
    
    @Override
    public double getFunctionValue(double x) {
        if (x < points[0].getX() || x > points[pointCount - 1].getX()) {
            return Double.NaN;
        }
        
        for (int i = 0; i < pointCount - 1; i++) {
            if (x >= points[i].getX() && x <= points[i + 1].getX()) {
                double x1 = points[i].getX();
                double y1 = points[i].getY();
                double x2 = points[i + 1].getX();
                double y2 = points[i + 1].getY();
                
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        
        return Double.NaN;
    }
    
    @Override
    public int getPointCount() {
        return pointCount;
    }
    
    @Override
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        checkIndex(index);
        return points[index].getX();
    }
    
    @Override
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        checkIndex(index);
        return points[index].getY();
    }
    
    @Override
    public void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        checkIndex(index);
        
        if ((index > 0 && x <= points[index - 1].getX()) || 
            (index < pointCount - 1 && x >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException(x);
        }
        
        points[index].setX(x);
    }
    
    @Override
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        checkIndex(index);
        points[index].setY(y);
    }
    
    @Override
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        checkIndex(index);
        return (FunctionPoint) points[index].clone();
    }
    
    @Override
    public void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        checkIndex(index);
        
        double x = point.getX();
        if ((index > 0 && x <= points[index - 1].getX()) || 
            (index < pointCount - 1 && x >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException(x);
        }
        
        points[index] = (FunctionPoint) point.clone();
    }
    
    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int position = findPlaceForX(point.getX());
        
        if (position == -1) {
            throw new InappropriateFunctionPointException(point.getX());
        }
        
        if (pointCount >= points.length) {
            FunctionPoint[] newArray = new FunctionPoint[points.length * 2];
            for (int i = 0; i < pointCount; i++) {
                newArray[i] = points[i];
            }
            points = newArray;
        }
        
        for (int i = pointCount; i > position; i--) {
            points[i] = points[i - 1];
        }
        
        points[position] = (FunctionPoint) point.clone();
        pointCount++;
    }
    
    @Override
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        checkIndex(index);
        
        if (pointCount < 3) {
            throw new IllegalStateException("Cannot delete point: minimum 3 points required");
        }
        
        for (int i = index; i < pointCount - 1; i++) {
            points[i] = points[i + 1];
        }
        
        points[pointCount - 1] = null;
        pointCount--;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointCount; i++) {
            if (i > 0) sb.append(", ");
            sb.append(points[i]);
        }
        sb.append("}");
        return sb.toString();
    }
    
    // Externalizable implementation (Задание 9)
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointCount);
        for (int i = 0; i < pointCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointCount = in.readInt();
        points = new FunctionPoint[pointCount + 10];
        for (int i = 0; i < pointCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }
}