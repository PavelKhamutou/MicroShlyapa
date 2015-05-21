package ecote;

public class Singleton {
    private static MacroGenerator instance;

    private Singleton() {}

    public static MacroGenerator getInstance(String inputFile, String outputFile) {
        if (instance == null) {
            instance = new MacroGenerator(inputFile, outputFile);
        }
        return instance;
    }
}
