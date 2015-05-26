package ecote;
import ecote.Exceptions.*;

import java.util.*;
import java.io.*;


public class MacroGenerator {

    private String inputFile;
    private String outputFile;

    private static final char HASH = '#';
    private static final char OPENING_BRACKET = '(';
    private static final char CLOSING_BRACKET = ')';
    private static final char OPENING_CURVE_BRACKET = '{';
    private static final char CLOSING_CURVE_BRACKET = '}';
    private static final char DOLLAR = '$';
    private static final char AMPERSANT = '&';
    private static final char COMMA = ',';
    private static final char EOF = '\n';
    private static final String MISSING_PARAMS_STRING = "MISSING_PARAMETER";

    private String macroDefToCheck = "";
    private String regexForMacroDefinition = "#\\w+\\((\\s*(&[1-9])\\s*,?)+\\s*\\)\\s*\\{(\\s*\\w+\\s*&[1-9])+\\s*\\}";

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
                        try {
                            readMacros();
                        } catch (IllegalMacrosDefinition e) {
                            System.err.println(e.getMessage(macroDefToCheck));
                        }
                    }
                    else if((char)readChar == DOLLAR && prevReadChar == EOF) {
                        callMacros();

                    }
                    else{
                        outputStream.write(readChar);
                    }
                } //end of while
                checkIfAllMacrosesUsed();
            } finally {
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /*private boolean containsChars(String str){

        if(str.contains(Character.toString(HASH)) || str.contains(Character.toString(OPENING_BRACKET)) ||
            str.contains(Character.toString(CLOSING_BRACKET)) || str.contains(Character.toString(OPENING_CURVE_BRACKET)) ||
            str.contains(Character.toString(CLOSING_CURVE_BRACKET)) || str.contains(Character.toString(DOLLAR)) ||
            str.contains(Character.toString(AMPERSANT)) || str.contains(Character.toString(COMMA))) {
            return false;
        }
        return true;

    }*/

    private void createStringToCheckDeffinition(){
        macroDefToCheck += (char)readChar;
    }

    private void readMacros() throws IOException, IllegalMacrosDefinition {

        macroDefToCheck = Character.toString((char)readChar);

        List<Integer> defParamValue = new ArrayList<Integer>();
        List<Integer> bodyParamValue = new ArrayList<Integer>();
        List<String> freeText = new ArrayList<String>();

        String freeTextParam = "";
        String name = "";

        while ((char)getChar() != OPENING_BRACKET){
            name += (char)readChar;
            createStringToCheckDeffinition();
        }
        createStringToCheckDeffinition();




        while ((char)getChar() != CLOSING_BRACKET){
            createStringToCheckDeffinition();
            if((char)readChar == AMPERSANT) {
                defParamValue.add(Character.getNumericValue(getChar()));
                createStringToCheckDeffinition();
            }
        }
        createStringToCheckDeffinition();



        while((char)getChar() != OPENING_CURVE_BRACKET) {
            createStringToCheckDeffinition();
        }
        createStringToCheckDeffinition();



        while((char)getChar() != CLOSING_CURVE_BRACKET){
            createStringToCheckDeffinition();
            if((char)readChar == AMPERSANT){
                freeText.add(freeTextParam);
                freeTextParam = "";
                bodyParamValue.add(Character.getNumericValue(getChar()));
                createStringToCheckDeffinition();
            }
            else {
                freeTextParam += (char)readChar;
            }
        }
        createStringToCheckDeffinition();

        //all checking starts here ->
        if(!macroDefToCheck.matches(regexForMacroDefinition) || defParamValue.size() != bodyParamValue.size()){
            throw new IllegalMacrosDefinition();
        }
        else {
            for(int i = 0; i < defParamValue.size(); i++){
                if(defParamValue.get(i) != bodyParamValue.get(i)){
                    throw new IllegalMacrosDefinition("Parameter list if not equal in body and definition!");
                }
            }
        }

        addMacrosToLib(name, defParamValue.size(), freeText.toArray(new String[freeText.size()]));

    }


    private void callMacros() throws IOException {
        String macroName = "";
        List<String> paramsList = new ArrayList<String>();
        String parameter = "";
        while((char)getChar() != OPENING_BRACKET) {
            macroName += (char)readChar;
        }
        while((char)getChar() != CLOSING_BRACKET) {
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
                int diff = countDifference(callNumberParameters, mcNumberParameters);
                for(int k = 0, l = 0; k < callNumberParameters; k++, l++){
                    if(k >= mcNumberParameters){
                        l = 0;
                        outputStream.write(freeText[l].toCharArray());
                        outputStream.write(paramsList.get(k).toCharArray());
                    }
                    else {
                        outputStream.write(freeText[l].toCharArray());
                        outputStream.write(paramsList.get(k).toCharArray());
                    }
                }
            } catch (NotEnoughParameters e) {
                System.err.println(e.getMessage(m.getName(), m.getNumberOfParameters(), paramsList.size(), MISSING_PARAMS_STRING));

                for(int k = 0; k < mcNumberParameters; k++){
                    if(k >= callNumberParameters){
                        outputStream.write(freeText[k].toCharArray());
                        outputStream.write((MISSING_PARAMS_STRING + "_#" + k).toCharArray());
                    }
                    else {
                        outputStream.write(freeText[k].toCharArray());
                        outputStream.write(paramsList.get(k).toCharArray());
                    }
                }
            }
        } catch (MacrosNotFound e) {
            System.err.println(e.getMessage(macroName));
        }

    }

    private int countDifference(int paramsFromCall, int paramsFromDeffinition) throws NotEnoughParameters {
        int difference = paramsFromCall - paramsFromDeffinition;
        if(difference < 0){
            throw new NotEnoughParameters();
        }
        else {
            return difference;
        }
    }

    private void addMacrosToLib(String macroName, int numberIfParamiters, String[] freeText) {
        try {
            macLib.addMacro(macroName, numberIfParamiters, freeText);
        } catch (MacrosNameIsAlreadyUsed e){
            System.err.println(e.getMessage(macroName));
        }
    }

    private void checkIfAllMacrosesUsed(){
        List<Macro> unusedMacro = new ArrayList<Macro>(macLib.unusedMacroses());
        for(Macro m: unusedMacro){
            System.err.println("Warning!: Macros <" + m.getName() + "> has been declared but never used!");
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
