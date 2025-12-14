import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;
import java.text.DecimalFormat;

public class Main {
    private static final DecimalFormat df = new DecimalFormat("#.####");
    
    public static void main(String[] args) {
        System.out.println("=== Lab Work #4 ===");
        
        try {
            testBasicFunctions();
            testTabulation();
            testFunctionCombinations();
            testTextIO();
            testBinaryIO();
            testSerialization();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBasicFunctions() throws IOException {
        System.out.println("\n1. Testing basic functions:");
        
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        System.out.println("Sin and Cos from 0 to PI with step 0.1:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("  sin(%.1f) = %6s, cos(%.1f) = %6s%n", 
                x, df.format(sin.getFunctionValue(x)), 
                x, df.format(cos.getFunctionValue(x)));
        }
    }
    
    private static void testTabulation() throws IOException {
        System.out.println("\n2. Testing tabulation:");
        
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        
        System.out.println("Tabulated Sin and Cos (10 points):");
        for (int i = 0; i < 10; i++) {
            System.out.printf("  Point %d: sin(%s)=%s, cos(%s)=%s%n",
                i,
                df.format(tabSin.getPointX(i)), df.format(tabSin.getPointY(i)),
                df.format(tabCos.getPointX(i)), df.format(tabCos.getPointY(i)));
        }
        
        System.out.println("\nComparison of exact and tabulated values:");
        for (double x = 0; x <= Math.PI; x += 0.5) {
            System.out.printf("  x=%.1f: sin=%.4f(tab=%.4f), cos=%.4f(tab=%.4f)%n",
                x, sin.getFunctionValue(x), tabSin.getFunctionValue(x),
                cos.getFunctionValue(x), tabCos.getFunctionValue(x));
        }
    }
    
    private static void testFunctionCombinations() throws IOException {
        System.out.println("\n3. Testing function combinations:");
        
        Sin sin = new Sin();
        Cos cos = new Cos();
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        
        // sin²(x) + cos²(x) ≈ 1
        Function sinSquared = Functions.power(tabSin, 2);
        Function cosSquared = Functions.power(tabCos, 2);
        Function sum = Functions.sum(sinSquared, cosSquared);
        
        System.out.println("sin²(x) + cos²(x) (should be ≈ 1):");
        for (double x = 0; x <= Math.PI; x += 0.2) {
            System.out.printf("  x=%.1f: %.6f%n", x, sum.getFunctionValue(x));
        }
        
        // Test with different number of points
        System.out.println("\nEffect of point count on accuracy:");
        for (int points : new int[]{5, 10, 20, 50}) {
            TabulatedFunction tabSin2 = TabulatedFunctions.tabulate(sin, 0, Math.PI, points);
            TabulatedFunction tabCos2 = TabulatedFunctions.tabulate(cos, 0, Math.PI, points);
            Function sin2 = Functions.power(tabSin2, 2);
            Function cos2 = Functions.power(tabCos2, 2);
            Function sum2 = Functions.sum(sin2, cos2);
            
            double error = 0;
            int count = 0;
            for (double x = 0; x <= Math.PI; x += 0.1) {
                double value = sum2.getFunctionValue(x);
                if (!Double.isNaN(value)) {
                    error += Math.abs(value - 1.0);
                    count++;
                }
            }
            error /= count;
            System.out.printf("  %2d points: average error = %.6f%n", points, error);
        }
    }
    
    private static void testTextIO() throws IOException {
        System.out.println("\n4. Testing text input/output:");
        
        // Create tabulated exponential function
        Exp exp = new Exp();
        TabulatedFunction expFunc = TabulatedFunctions.tabulate(exp, 0, 10, 11);
        
        // Write to file
        try (FileWriter writer = new FileWriter("exp_function.txt")) {
            TabulatedFunctions.writeTabulatedFunction(expFunc, writer);
            System.out.println("Function written to exp_function.txt");
        }
        
        // Read from file
        try (FileReader reader = new FileReader("exp_function.txt")) {
            TabulatedFunction readExpFunc = TabulatedFunctions.readTabulatedFunction(reader);
            
            System.out.println("Comparison of original and read function:");
            System.out.println("  x | original | read      | difference");
            System.out.println("  --------------------------------------");
            for (double x = 0; x <= 10; x += 1) {
                double original = expFunc.getFunctionValue(x);
                double read = readExpFunc.getFunctionValue(x);
                System.out.printf("  %2.0f | %8.4f | %9.4f | %8.6f%n",
                    x, original, read, Math.abs(original - read));
            }
        }
        
        // Show file content
        System.out.println("\nContent of exp_function.txt:");
        try (BufferedReader br = new BufferedReader(new FileReader("exp_function.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("  " + line);
            }
        }
    }
    
    private static void testBinaryIO() throws IOException {
        System.out.println("\n5. Testing binary input/output:");
        
        // Create tabulated logarithm
        Log log = new Log(Math.E);
        TabulatedFunction logFunc = TabulatedFunctions.tabulate(log, 1, 10, 10);
        
        // Write to binary file
        try (FileOutputStream fos = new FileOutputStream("log_function.bin")) {
            TabulatedFunctions.outputTabulatedFunction(logFunc, fos);
            System.out.println("Function written to log_function.bin");
        }
        
        // Read from binary file
        try (FileInputStream fis = new FileInputStream("log_function.bin")) {
            TabulatedFunction readLogFunc = TabulatedFunctions.inputTabulatedFunction(fis);
            
            System.out.println("Comparison of original and read function:");
            System.out.println("  x | original | read      | difference");
            System.out.println("  --------------------------------------");
            for (double x = 1; x <= 10; x += 1) {
                double original = logFunc.getFunctionValue(x);
                double read = readLogFunc.getFunctionValue(x);
                System.out.printf("  %2.0f | %8.4f | %9.4f | %8.6f%n",
                    x, original, read, Math.abs(original - read));
            }
        }
        
        // Show file size
        File file = new File("log_function.bin");
        System.out.printf("\nBinary file size: %d bytes%n", file.length());
    }
    
    private static void testSerialization() throws IOException, ClassNotFoundException {
        System.out.println("\n6. Testing serialization:");
        
        // Create complex function: ln(exp(x)) = x
        Function logOfExp = Functions.composition(new Exp(), new Log(Math.E));
        TabulatedFunction tabulated = TabulatedFunctions.tabulate(logOfExp, 0, 10, 11);
        
        // 1. Serialization via Serializable
        System.out.println("a) Serializable:");
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("function_serializable.ser"))) {
            oos.writeObject(tabulated);
            System.out.println("  Object serialized to function_serializable.ser");
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("function_serializable.ser"))) {
            TabulatedFunction deserialized = (TabulatedFunction) ois.readObject();
            
            System.out.println("  Deserialized function:");
            for (double x = 0; x <= 10; x += 1) {
                System.out.printf("    x=%.0f: %.4f (expected: %.4f)%n", 
                    x, deserialized.getFunctionValue(x), x);
            }
        }
        
        File serFile = new File("function_serializable.ser");
        System.out.printf("  File size: %d bytes%n", serFile.length());
        
        // 2. Serialization via Externalizable (ArrayTabulatedFunction)
        System.out.println("\nb) Externalizable (ArrayTabulatedFunction):");
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 10, 11);
        for (int i = 0; i < 11; i++) {
            arrayFunc.setPointY(i, i); // f(x) = x
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("function_externalizable.ser"))) {
            oos.writeObject(arrayFunc);
            System.out.println("  Object serialized to function_externalizable.ser");
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("function_externalizable.ser"))) {
            ArrayTabulatedFunction deserialized = (ArrayTabulatedFunction) ois.readObject();
            
            System.out.println("  Deserialized function:");
            for (int i = 0; i < deserialized.getPointCount(); i++) {
                System.out.printf("    Point %d: (%s, %s)%n",
                    i, 
                    df.format(deserialized.getPointX(i)),
                    df.format(deserialized.getPointY(i)));
            }
        }
        
        File extFile = new File("function_externalizable.ser");
        System.out.printf("  File size: %d bytes%n", extFile.length());
        
        // Compare file sizes
        System.out.println("\nFile size comparison:");
        System.out.printf("  Serializable: %d bytes%n", serFile.length());
        System.out.printf("  Externalizable: %d bytes%n", extFile.length());
        System.out.printf("  Difference: %d bytes%n", serFile.length() - extFile.length());
    }
}