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
    private int readChar;
    private char[] pdc = {hash, openingBracket, closingBracker, openingCurveBracket, closingCurveBracket, dollar, ampersant};
    private List<Character> preDeffinedChars = new ArrayList<Character>();

    FileReader inputStream = null;
    FileWriter outputStream = null;


    public MacroGenerator(String inputFile, String outputFile){
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        for(char c: pdc)
            preDeffinedChars.add(c);
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
//                        callMacros();
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
        System.out.println("get in");
        while ((char)getChar() != openingBracket){
            name += (char)readChar;
        }
        System.out.println("name: " + name);


        int numberIfParamiters = 0;

        while ((char)getChar() != closingBracker){
            if((char)readChar == ampersant){
                numberIfParamiters++;
            }
        }
        System.out.println("numberIfParamiters: " + numberIfParamiters);

        while((char)getChar() != openingCurveBracket) {} //scroll till {
        System.out.println("{: " + (char)readChar);

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
        System.out.println(Arrays.toString(freeText));


    }

    private int getChar() throws IOException {
        return (readChar = inputStream.read());
    }

    public void show() {
        System.out.println("i am alive!");
        System.out.println(preDeffinedChars.toString());
    }

}
