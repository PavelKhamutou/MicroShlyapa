package ecote;
import ecote.Exceptions.*;

import java.util.*;
import java.io.*;


public class MacroGenerator {

    private String inputFile;
    private String outputFile;

    private static final char HASH = '#';
    private static final char openingBracket = '(';
    private static final char closingBracker = ')';
    private static final char openingCurveBracket = '{';
    private static final char closingCurveBracket = '}';
    private static final char dollar = '$';
    private static final char ampersant = '&';
    private static final char COMMA = ',';
    private static final char EOF = '\n';
    private static final String MISSING_PARAMS_STRING = "MISSING_PARAMETER_";


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
                    if((char)readChar == HASH && prevReadChar == EOF){
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

        while((char)getChar() != openingCurveBracket /*&& ((char)readChar) == ' ' || ((char)readChar) == '\n'*/) {} //scroll till {

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
        List<String> paramsList = new ArrayList<String>();
        String parameter = "";
        while((char)getChar() != openingBracket) {
            macroName += (char)readChar;
        }
        while((char)getChar() != closingBracker) {
            if((char)readChar == COMMA){
                paramsList.add(parameter);
                parameter = "";
            }
            else{
                parameter += (char)readChar;
            }
        }
        paramsList.add(parameter);

        try {
            Macro m = macLib.getMacros(macroName);
            String[] freeText = m.getFreeText();
            int mcNumberParameters = m.getNumberOfParameters();
            int callNumberParameters = paramsList.size();

            try {
                int diff = countDifference(callNumberParameters, m.getNumberOfParameters());
                for(int k = 0; k < callNumberParameters; k++){
                    if(k >= mcNumberParameters){
                        outputStream.write(freeText[k - diff].toCharArray());
                        outputStream.write(paramsList.get(k).toCharArray());
                    }
                    else {
                        outputStream.write(freeText[k].toCharArray());
                        outputStream.write(paramsList.get(k).toCharArray());
                    }
                }
            } catch (NotEnoughParamiters e) {
                System.err.println("Error!: Not enough parameters:");
                System.err.println("\t\tThe deffinition of macros <" + m.getName() + "> requires <" + m.getNumberOfParameters() + "> parameters");
                System.err.println("\t\tbut <" + paramsList.size() + "> found. Missing parameters will be substituted with <" + MISSING_PARAMS_STRING + ">");
                for(int k = 0; k < mcNumberParameters; k++){
                    if(k >= callNumberParameters){
                        outputStream.write(freeText[k].toCharArray());
                        outputStream.write((MISSING_PARAMS_STRING + k).toCharArray());
                    }
                    else {
                        outputStream.write(freeText[k].toCharArray());
                        outputStream.write(paramsList.get(k).toCharArray());
                    }
                }
            }
        } catch (MacrosNotFound macrosNotFound) {
            System.err.println("Error!: Macros not found:\n\t\tMacros: <" + macroName + "> not found in the library");
        }

    }

    private int countDifference(int paramsFromCall, int paramsFromDeffinition) throws NotEnoughParamiters {
        int difference = paramsFromCall - paramsFromDeffinition;
        if(difference < 0){
            throw new NotEnoughParamiters();
        }
        else {
            return difference;
        }
    }

    private void addMacrosToLib(String name, int numberIfParamiters, String[] freeText) {
        try {
            macLib.addMacro(name, numberIfParamiters, freeText);
        } catch (MacrosNameIsAlreadyUsed e){
            System.err.println("Warning!: Incorrect macro name:\n\t\t  <" + name + "> has been already used");
        }
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
