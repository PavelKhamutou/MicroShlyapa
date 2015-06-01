package ecote;
import ecote.Exceptions.*;

import java.util.*;
import java.io.*;
import java.lang.Math;


public class MacroGenerator {

    private String inputFile;
    private String outputFile;
    private String logFile;

    private static final char HASH = 35;                        // #
    private static final char OPENING_BRACKET = 40;             // (
    private static final char CLOSING_BRACKET = 41;             // )
    private static final char OPENING_CURVE_BRACKET = 123;      // {
    private static final char CLOSING_CURVE_BRACKET = 125;      // }
    private static final char DOLLAR = 36;                      // $
    private static final char AMPERSAND = 38;                   // &
    private static final char COMMA = 44;                       // ,
    private static final char EOL = 10;                         // \n
    private static final char UNDERSCORE = 95;                  // _
    private static final char SPACE = 32;                       // ' '

    private static final String MISSING_PARAMS_STRING = " ";

    private String macroDefOrCallToCheck = "";
    private int currentLine = 1;
    private int startLine;
    private static final MacroLib macLib = new MacroLib();

    private int readChar;
    private int prevReadChar;

    FileReader inputStream = null;
    FileWriter outputStream = null;
    FileWriter logStream = null;


    public MacroGenerator(String inputFile, String outputFile, String logFile){
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.logFile = logFile;
    }

    public void read() throws IOException {
        try {

            inputStream = new FileReader(inputFile);
            outputStream = new FileWriter(outputFile);
            logStream = new FileWriter(logFile);

            try {
                while(getChar() != -1) {
                    if(readChar == HASH && prevReadChar == EOL){
                        try {
                            startLine = currentLine;
                            readMacros();
                        } catch (IllegalMacroDefinition e) {
                            errorLogging(e.getMessage(macroDefOrCallToCheck, startLine));
                        }
                    }
                    else if(readChar == DOLLAR && prevReadChar == EOL) {
                        try {
                            startLine = currentLine;
                            callMacros();
                        } catch (IllegalMacroCall e) {
                            errorLogging(e.getMessage(macroDefOrCallToCheck, startLine));
                        }
                    }
                    else{
                        outputStream.write(readChar);
                    }
                } //end of while
                checkIfAllMacrosesUsed();
            } finally {
                inputStream.close();
                outputStream.close();
                logStream.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }


    private void addCharToLogString(){
        macroDefOrCallToCheck += (char)readChar;
    }

    private void finishCopingText() throws IOException {
        while(getChar() != CLOSING_CURVE_BRACKET){
            if(readChar == -1){
                throw new IOException("Error: File reaches its end:\n\t\tThere ware not found any \'}\' for macrodefinition in line <" + startLine + ">.");
            }
            addCharToLogString();
        }
        addCharToLogString();
    }


    private void readMacros() throws IOException, IllegalMacroDefinition {

        macroDefOrCallToCheck = Character.toString((char)readChar);

        List<Integer> defParamValue = new ArrayList<Integer>();
        List<Integer> bodyParamValue = new ArrayList<Integer>();
        List<String> freeText = new ArrayList<String>();
        String freeTextParam = "";
        String name = "";


        while (getChar() != OPENING_BRACKET) {
            addCharToLogString();
            if(!validCharForName()){
                finishCopingText();
                throw new IllegalMacroDefinition("Illegal name.");
            }
            name += (char)readChar;
        }
        addCharToLogString();


        /*TODO
            Write code which will be able to find and save digits that are grater then 9.
         */
        List<Integer> parameterAsDigit = new ArrayList<Integer>();

        while (getChar() != CLOSING_BRACKET){
            addCharToLogString();

            if(readChar != SPACE && readChar != EOL && readChar != AMPERSAND){
                finishCopingText();
                throw new IllegalMacroDefinition("Illegal parameter list.");
            }

            if(readChar == AMPERSAND) {
                while(Character.isDigit(getChar())){
                    addCharToLogString();
                    parameterAsDigit.add(Character.getNumericValue(readChar));
                }

                if(parameterAsDigit.size() == 0){
                    addCharToLogString();
                    finishCopingText();
                    throw new IllegalMacroDefinition("Ampersand is not followed by digits.");
                }


                defParamValue.add(getDigits(parameterAsDigit));
                parameterAsDigit.clear();

                do {
                    if(readChar == COMMA){
                        break;
                    }

                    if (readChar == CLOSING_BRACKET){
                        break;
                    }
                    addCharToLogString();
                } while (getChar() == SPACE || readChar == EOL);


                addCharToLogString();

                if(readChar == CLOSING_BRACKET){
                    break;
                }

                if(readChar != COMMA){
                    finishCopingText();
                    throw new IllegalMacroDefinition("Comma separator is not found!");
                }

            } // end of read==&
        }



        while(getChar() != OPENING_CURVE_BRACKET) {
            addCharToLogString();
            if(!(readChar == SPACE || readChar == EOL)){
                finishCopingText();
                throw new IllegalMacroDefinition("Probable causes:\n\t\t\tMissing \'{\'.\n\t\t\tThere is text between \')\' and \'{\'.");
            }
        }
        addCharToLogString();

        /*TODO
            Solve problem if closing curve bracket is missing.
            Write code which will be able to find and save digits that are grater then 9.
         */

        while(getChar() != CLOSING_CURVE_BRACKET){
            addCharToLogString();
            if(!validCharForName() && readChar != AMPERSAND && readChar != SPACE && readChar != EOL){
                finishCopingText();
                throw new IllegalMacroDefinition("Something wrong in body block.");
            }
            if(readChar == AMPERSAND){
                if(prevReadChar != SPACE && prevReadChar != EOL){
                    finishCopingText();
                    throw new IllegalMacroDefinition("Using \'&\' in improper place. It must have space before and digit after.");
                }
                freeText.add(freeTextParam);
                freeTextParam = "";

                while(Character.isDigit(getChar())){
                    addCharToLogString();
                    parameterAsDigit.add(Character.getNumericValue(readChar));
                }

                if(parameterAsDigit.size() == 0){
                    addCharToLogString();
                    finishCopingText();
                    throw new IllegalMacroDefinition("Ampersand is not followed by digits.");
                }

                addCharToLogString();

                if(readChar == SPACE || readChar == EOL || readChar == CLOSING_CURVE_BRACKET){

                    bodyParamValue.add(getDigits(parameterAsDigit));
                    parameterAsDigit.clear();

                    if(readChar == CLOSING_CURVE_BRACKET){
                        break;
                    }
                    freeTextParam += (char)readChar;
                }
                else{
                    finishCopingText();
                    throw new IllegalMacroDefinition("Using wrong body structure.");
                }

            }
            else {
                freeTextParam += (char)readChar;
            }
        }

        addCharToLogString();
        if(!freeTextParam.matches("\\s*")){
            errorLogging("Warning!: In macrodefinition <" + name + "> line <" + startLine + ">:\n\t\tFree text <" + freeTextParam + "> after last parameter will be ignored!");
        }


        if(defParamValue.size() != bodyParamValue.size()){
            throw new IllegalMacroDefinition("Parameter list is not equal.");
        }
        else {
            for(int i = 0; i < defParamValue.size(); i++){
                if(!defParamValue.get(i).equals(bodyParamValue.get(i))) {
                    throw new IllegalMacroDefinition("Parameter's values are not equal in body and definition! Definition: "
                            + defParamValue.toString() + " Body: " + bodyParamValue.toString());
                }
            }
        }

        addMacrosToLib(name, defParamValue.size(), freeText.toArray(new String[freeText.size()]));

    }



    private int getDigits(List<Integer> list){
        int power = 0;
        int digit = 0;
        for(int i = list.size() - 1; i > -1; i--){
            digit = digit + list.get(i) * (int)Math.pow(10, power++);
        }
        return digit;
    }


    //TODO redo callMacros function.

    private void callMacros() throws IOException, IllegalMacroCall {
        macroDefOrCallToCheck = Character.toString((char)readChar);
        String macroName = "";
        List<String> paramsList = new ArrayList<String>();
        String parameter = "";

        /*TODO
            All the same as in readMacros().
         */


        while((char)getChar() != OPENING_BRACKET) {
            macroName += (char)readChar;
            addCharToLogString();
        }
        addCharToLogString();



        while((char)getChar() != CLOSING_BRACKET) {
            addCharToLogString();
            if((char)readChar == COMMA){
                paramsList.add(parameter);
                parameter = "";
            }
            else{
                parameter += (char)readChar;
            }
        }
        addCharToLogString();
        paramsList.add(parameter);


        String regexForMacroCall = "\\$\\w+\\(\\s*(\\w*\\s*,?)+\\s*\\)";

        if(!macroDefOrCallToCheck.matches(regexForMacroCall)){
            throw new IllegalMacroCall();
        }

        try {
            Macro m = macLib.getMacros(macroName);
            String[] freeText = m.getFreeText();

            int mcNumberParameters = m.getNumberOfParameters();
            int callNumberParameters = paramsList.size();

            try {

                int startingPointIfCounterZero = countDifference(callNumberParameters, mcNumberParameters);
                int counter = callNumberParameters % mcNumberParameters;
                int startingPoint = callNumberParameters - counter;

                for(int k = 0; k < mcNumberParameters; k++){
                    outputStream.write(freeText[k].toCharArray());
                    if(mcNumberParameters == callNumberParameters){
                        outputStream.write(paramsList.get(k).toCharArray());
                    }
                    else if(counter == 0){
                        outputStream.write(paramsList.get(startingPointIfCounterZero++).toCharArray());
                    }
                    else{
                        if(startingPoint != callNumberParameters) {
                            outputStream.write(paramsList.get(startingPoint++).toCharArray());
                        }
                        else {
                            outputStream.write(paramsList.get(k).toCharArray());
                        }
                    }
                }
            } catch (NotEnoughParameters e) {

                errorLogging(e.getMessage(m.getName(), m.getNumberOfParameters(), paramsList.size(), MISSING_PARAMS_STRING, startLine));

                for(int k = 0; k < mcNumberParameters; k++){
                    outputStream.write(freeText[k].toCharArray());
                    if(k >= callNumberParameters){
                        outputStream.write((MISSING_PARAMS_STRING).toCharArray());
                    }
                    else {
                        outputStream.write(paramsList.get(k).toCharArray());
                    }
                }
            }
        } catch (MacrosNotFound e) {
            errorLogging(e.getMessage(macroName, startLine));
        }

    }

    private int countDifference(int paramsFromCall, int paramsFromDefinition) throws NotEnoughParameters {
        int difference = paramsFromCall - paramsFromDefinition;
        if(difference < 0){
            throw new NotEnoughParameters();
        }
        return difference;
    }

    private void addMacrosToLib(String macroName, int numberIfParamiters, String[] freeText) throws IOException {
        try {
            macLib.addMacro(macroName, numberIfParamiters, freeText);
        } catch (MacrosNameIsAlreadyUsed e){
            errorLogging(e.getMessage(macroName, startLine));
        }
    }

    private void checkIfAllMacrosesUsed() throws IOException {
        List<Macro> unusedMacro = new ArrayList<Macro>(macLib.unusedMacroses());
        for(Macro m: unusedMacro){
            errorLogging("Warning!: Macros <" + m.getName() + "> has been declared but never used!");
        }
    }

    private boolean validCharForName(){
        /*
            a-zA-Z_0-9
            ASCII:
                65 - 90 => [A-Z]
                97 - 122 => [a-z]
                48 - 57 => [0-9]
         */
        return ((readChar >= 65 && readChar <= 90) || (readChar >= 97 && readChar <= 122) ||
                (readChar >= 48 && readChar <= 57) || readChar == UNDERSCORE);
    }


    private int getChar() throws IOException {
        prevReadChar = readChar;

        if((char)prevReadChar == '\n'){
            currentLine++;
        }
        //System.out.println(readChar);
        return (readChar = inputStream.read());
    }

    private void errorLogging(String errorMessage) throws IOException {
        System.err.println(errorMessage);
        logStream.write(errorMessage + "\n");
    }

}
