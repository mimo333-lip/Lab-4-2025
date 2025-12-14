package functions;

import java.io.*;

public final class TabulatedFunctions {
    private TabulatedFunctions() {
        throw new AssertionError("Cannot instantiate utility class");
    }
    
    // Задание 6
    public static TabulatedFunction tabulate(Function function, 
                                            double leftX, 
                                            double rightX, 
                                            int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || 
            rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Tabulation interval is outside function domain");
        }
        
        if (pointsCount < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            xValues[i] = leftX + i * step;
            yValues[i] = function.getFunctionValue(xValues[i]);
        }
        
        return new ArrayTabulatedFunction(xValues, yValues);
    }
    
    // Задание 7: байтовые потоки
    public static void outputTabulatedFunction(TabulatedFunction function, 
                                              OutputStream out) throws IOException {
        try (DataOutputStream dataOut = new DataOutputStream(out)) {
            int pointCount = function.getPointCount();
            dataOut.writeInt(pointCount);
            
            for (int i = 0; i < pointCount; i++) {
                dataOut.writeDouble(function.getPointX(i));
                dataOut.writeDouble(function.getPointY(i));
            }
        }
    }
    
    public static TabulatedFunction inputTabulatedFunction(InputStream in) 
            throws IOException {
        try (DataInputStream dataIn = new DataInputStream(in)) {
            int pointCount = dataIn.readInt();
            
            double[] xValues = new double[pointCount];
            double[] yValues = new double[pointCount];
            
            for (int i = 0; i < pointCount; i++) {
                xValues[i] = dataIn.readDouble();
                yValues[i] = dataIn.readDouble();
            }
            
            return new ArrayTabulatedFunction(xValues, yValues);
        }
    }
    
    // Задание 7: символьные потоки
    public static void writeTabulatedFunction(TabulatedFunction function, 
                                             Writer out) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        int pointCount = function.getPointCount();
        writer.print(pointCount);
        
        for (int i = 0; i < pointCount; i++) {
            writer.print(" " + function.getPointX(i));
            writer.print(" " + function.getPointY(i));
        }
        writer.flush();
        // Не закрываем writer, чтобы не закрывать переданный out
    }
    
    public static TabulatedFunction readTabulatedFunction(Reader in) 
            throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        tokenizer.parseNumbers();
        
        // Читаем количество точек
        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Expected number of points");
        }
        int pointCount = (int) tokenizer.nval;
        
        if (pointCount < 2) {
            throw new IOException("At least 2 points required");
        }
        
        double[] xValues = new double[pointCount];
        double[] yValues = new double[pointCount];
        
        for (int i = 0; i < pointCount; i++) {
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Expected X value");
            }
            xValues[i] = tokenizer.nval;
            
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Expected Y value");
            }
            yValues[i] = tokenizer.nval;
        }
        
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}