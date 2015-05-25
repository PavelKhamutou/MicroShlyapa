package ecote;
import java.util.*;
import java.io.*;


public class MacroGenerator {

    private String inputFile;
    private String outputFile;

    private static final char hash = '#';
    private static final char openingBracket = '(';
    private static final char closingBracker = ')';
    private static final char openingCurveBracket = '{';
    private static final char closingCurveBracket = '}';
    private static final char dollar = '$';
    private static final char ampersant = '&';
    private static final char comma = ',';
    private static final MacroLib macLib = new MacroLib();
    private int readChar;
    private int prevReadChar;

    FileReader inputStream = null;
    FileWriter outputStream = null;


    public MacroGenerator(String inputFile, String outputFile){
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void read(){
        try {

            inputStream = new FileReader(inputFile);
            outputStream = new FileWriter(outputFile);

            try {
                while(getChar() != -1) {
                    if((char)readChar == hash){
                        readMacros();
                    }
                    else if((char)readChar == dollar) {
                        callMacros();

                    }
                    else{
                        outputStream.write(readChar);
                    }
                } //end of while
            } finally {
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //read name
    //read params
    //read free text
    private void readMacros() throws IOException {
        String name = "";
        while ((char)getChar() != openingBracket){
            name += (char)readChar;
        }

        int numberIfParamiters = 0;

        while ((char)getChar() != closingBracker){
            if((char)readChar == ampersant){
                numberIfParamiters++;
            }
        }

        while((char)getChar() != openingCurveBracket) {} //scroll till {

        int i = 0;
        String[] freeText = new String[numberIfParamiters];
        String freeTextParam = "";

        while((char)getChar() != closingCurveBracket){
            if((char)readChar == ampersant){
                freeText[i] = freeTextParam;
                i++;
                freeTextParam = "";
                getChar();
            }
            else {
                freeTextParam += (char) readChar;
            }
        }
        addMacrosToLib(name, numberIfParamiters, freeText);

    }

    private void callMacros() throws IOException {
        String macroName = "";
        String[] paramsList = new String[3];
        String parameter = "";
        int i = 0;
        while((char)getChar() != openingBracket) {
            macroName += (char)readChar;
        }
        while((char)getChar() != closingBracker) {
            if((char)readChar == comma){
                paramsList[i] = parameter;
                i++;
                parameter = "";
            }
            else{
                parameter += (char)readChar;
            }
        }
        paramsList[i] = parameter;
        System.out.println(Arrays.toString(paramsList));
        Macro m = macLib.getMacros(macroName);
        String[] freeText = m.getFreeText();
        for(int k = 0; k < m.getNumberOfParameters(); k++) {
            outputStream.write(freeText[k].toCharArray());
            outputStream.write(paramsList[k].toCharArray());
        }
    }

    private void addMacrosToLib(String name, int numberIfParamiters, String[] freeText){
        macLib.addMacro(name, numberIfParamiters, freeText);
    }

    private int getChar() throws IOException {
        prevReadChar = readChar;
        return (readChar = inputStream.read());
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + macLib;
    }
}
