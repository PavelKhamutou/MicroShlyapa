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
    //private static final char DASH = 45;                        // -

    private static final String MISSING_PARAMS_STRING = " ";

    private String macroDefOrCallToCheck = "";
    private int currentLine = 1;
    private int startLine;
    private static final MacroLib macLib = new MacroLib();

    private int readChar;
    private int prevReadChar = 0;

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
                        if(keyChars() || readChar == AMPERSAND){
                            errorLogging("Warning!: The key character \'"+(char)readChar+"\' is used in free text in line <" + currentLine + ">.");
                        }
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
            //System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }


    private void addCharToLogString(){
        macroDefOrCallToCheck += (char)readChar;
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
            if(readChar == EOL){
                throw new IllegalMacroDefinition("You must not have EOL in macro name.");
            }
            if(!validCharForName()){
                char tmp = (char)readChar;
                finishCopingDefinition();
                throw new IllegalMacroDefinition("Illegal name. Possible causes: \n\t\t\t1) You have forgotten opening bracket.\n\t\t\t2) You have used improper character \'" + tmp +
                        "\'.\n" + "\t\tList of proper characters: [a-z][A-Z][0-9][_]");
            }
            name += (char)readChar;
        }
        addCharToLogString();



        List<Integer> parameterAsDigit = new ArrayList<Integer>();

        while (getChar() != CLOSING_BRACKET){
            addCharToLogString();

            if(readChar != SPACE && readChar != EOL && readChar != AMPERSAND){
                char tmp = (char)readChar;
                finishCopingDefinition();
                throw new IllegalMacroDefinition("Illegal parameter list. Improper character: \'" + tmp + "\'.");
            }

            if(readChar == AMPERSAND) {
                while(Character.isDigit(getChar())){
                    addCharToLogString();
                    parameterAsDigit.add(Character.getNumericValue(readChar));
                }

                if(parameterAsDigit.size() == 0){
                    addCharToLogString();
                    char tmp = (char)readChar;
                    finishCopingDefinition();
                    throw new IllegalMacroDefinition("Ampersand is not followed by digits. Instead it is followed by \'" + tmp + "\'.");
                }


                defParamValue.add(getDigits(parameterAsDigit));
                parameterAsDigit.clear();


                if(readChar != SPACE && readChar != EOL && readChar != COMMA && readChar != CLOSING_BRACKET){
                    addCharToLogString();
                    char tmp = (char)readChar;
                    finishCopingDefinition();
                    throw new IllegalMacroDefinition("Illegal character in definition: \'" + tmp + "\'\n\t\t\'&\' may be followed by space, comma or EOL.\n\t\t" +
                            "Possible cause: You have forgotten \')\'.");
                }

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

                if(readChar != COMMA && readChar == OPENING_CURVE_BRACKET){
                    finishCopingDefinition();
                    throw new IllegalMacroDefinition("There is no closing bracket.");
                }

                if(readChar != COMMA){
                    char tmp = (char)readChar;
                    finishCopingDefinition();
                    throw new IllegalMacroDefinition("Comma separator is not found! Instead \'" + tmp + "\' is found.");
                }

            } // end of read==&
        }



        while(getChar() != OPENING_CURVE_BRACKET) {
            addCharToLogString();
            if(!(readChar == SPACE || readChar == EOL)){
                finishCopingDefinition();
                throw new IllegalMacroDefinition("Probable causes:\n\t\t\tMissing \'{\'.\n\t\t\tThere is text between \')\' and \'{\'.");
            }
        }
        addCharToLogString();


        while(getChar() != CLOSING_CURVE_BRACKET){
            if(readChar == -1){
                errorLogging("Error: File reaches its end:\n\t\tThere ware not found any \'}\' for macrodefinition in line <" + startLine + ">.");
                throw new IOException("Error: File reaches its end:\n\t\tThere ware not found any \'}\' for macrodefinition in line <" + startLine + ">.");
            }
            addCharToLogString();
            //if(!validCharForName() && readChar != AMPERSAND && readChar != SPACE && readChar != EOL){
            if(keyChars()){
                char tmp = (char)readChar;
                finishCopingDefinition();
                throw new IllegalMacroDefinition("Illegal character: " + tmp);
            }
            if(readChar == AMPERSAND){
                if(prevReadChar != SPACE && prevReadChar != EOL){
                    finishCopingDefinition();
                    throw new IllegalMacroDefinition("Using \'&\' in improper place. It must have space/EOL before and digit after.");
                }
                freeText.add(freeTextParam);
                freeTextParam = "";

                while(Character.isDigit(getChar())){
                    addCharToLogString();
                    parameterAsDigit.add(Character.getNumericValue(readChar));
                }

                if(parameterAsDigit.size() == 0){
                    addCharToLogString();
                    char tmp = (char)readChar;
                    finishCopingDefinition();
                    throw new IllegalMacroDefinition("Ampersand is not followed by digits. Instead it is followed by \'" + tmp + "\'.");
                }



                if(readChar == SPACE || readChar == EOL || readChar == CLOSING_CURVE_BRACKET){

                    bodyParamValue.add(getDigits(parameterAsDigit));
                    parameterAsDigit.clear();

                    if(readChar == CLOSING_CURVE_BRACKET){
                        break;
                    }
                    addCharToLogString();
                    freeTextParam += (char)readChar;
                }
                else{
                    addCharToLogString();
                    char tmp = (char)readChar;
                    finishCopingDefinition();
                    throw new IllegalMacroDefinition("Using wrong body structure. After \'&\' must be a space/EOL but you have: \'"+tmp+"\'");
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
                    throw new IllegalMacroDefinition("Parameter's values are not equal in the body and the definition! Definition: "
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



    private void callMacros() throws IOException, IllegalMacroCall {
        macroDefOrCallToCheck = Character.toString((char)readChar);
        String macroName = "";
        List<String> paramsList = new ArrayList<String>();
        String parameter = "";



        while(getChar() != OPENING_BRACKET) {
            addCharToLogString();
            if(!validCharForName()){
                char tmp = (char)readChar;
                finishCopingCall();
                throw new IllegalMacroCall("Illegal name. Possible causes:\n\t\t\t1) You have forgotten opening bracket.\n\t\t\t2) You have used improper character \'" + tmp +
                "\'.\n" + "\t\tList of proper characters: [a-z][A-Z][0-9][_]");
            }
            macroName += (char)readChar;

        }
        addCharToLogString();



        while(getChar() != CLOSING_BRACKET) {
            if(readChar == -1){
                errorLogging("Error: File reaches its end:\n\t\tThere ware not found any \')\' for macrocall in line <" + startLine + ">. \n\t\t" + macroDefOrCallToCheck);
                throw new IOException("Error: File reaches its end:\n\t\tThere ware not found any \')\' for macrocall in line <" + startLine + ">. \n\t\t" + macroDefOrCallToCheck);
            }
            addCharToLogString();
            if(readChar == COMMA){
                paramsList.add(parameter);
                parameter = "";
            }
            else{
                //if(!validCharForName() && readChar != SPACE && readChar != EOL){
                if(keyChars()){
                    char tmp = (char)readChar;
                    finishCopingCall();
                    throw new IllegalMacroCall("Illegal character in macrocall: \'" + tmp + "\'.");
                }
                parameter += (char)readChar;
            }
        }
        addCharToLogString();
        paramsList.add(parameter);




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

    private boolean keyChars(){
        return readChar == HASH || readChar == DOLLAR || readChar == OPENING_BRACKET || readChar == CLOSING_BRACKET
                || readChar == OPENING_CURVE_BRACKET || readChar == CLOSING_CURVE_BRACKET;
    }



    private void finishCopingDefinition() throws IOException {
        while(getChar() != CLOSING_CURVE_BRACKET){
            if(readChar == -1){
                errorLogging("Error: File reaches its end:\n\t\tThere ware not found any \'}\' for macrodefinition in line <" + startLine + ">.");
                throw new IOException("Error: File reaches its end:\n\t\tThere ware not found any \'}\' for macrodefinition in line <" + startLine + ">.");
            }
            addCharToLogString();
        }
        addCharToLogString();
    }

    private void finishCopingCall() throws IOException {
        while(getChar() != CLOSING_BRACKET){
            if(readChar == -1){
                errorLogging("Error: File reaches its end:\n\t\tThere ware not found any \')\' for macrocall in line <" + startLine + ">. \n\t\t" + macroDefOrCallToCheck);
                throw new IOException("Error: File reaches its end:\n\t\tThere ware not found any \')\' for macrocall in line <" + startLine + ">. \n\t\t" + macroDefOrCallToCheck);
            }
            addCharToLogString();
        }
        addCharToLogString();
    }

    private int getChar() throws IOException {
        prevReadChar = readChar;


        if(prevReadChar == EOL){
            currentLine++;
        }

        if(prevReadChar == 0){
            prevReadChar = EOL;
        }
        //System.out.println(readChar);
        return (readChar = inputStream.read());
    }

    private void errorLogging(String errorMessage) throws IOException {
        System.err.println(errorMessage);
        logStream.write(errorMessage + "\n");
    }

}
