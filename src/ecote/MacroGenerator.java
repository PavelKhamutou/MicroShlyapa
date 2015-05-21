package ecote;
import java.util.*;
import java.io.*;
import java.util.regex.*;

public class MacroGenerator {
    private String inputFile;
    private String outputFile;
    private String wholeFileAsString;
    private String withOutDefinithinAsString;
    private MacroLib mcLib = new MacroLib();
    private String regexForMacroDefinition = "#\\w+\\((\\s*(&[1-9])\\s*,?)+\\s*\\)\\s*\\{(\\s*\\w+\\s*&[1-9])+\\s*\\}";
    private String regexForMacroCall = "\\$\\w+\\(\\s*(\\w*\\s*,?)+\\s*\\)";
    private List<String> macroDefinitionsList = new ArrayList<String>();
    private List<String> macroCallsAsList = new ArrayList<String>();

    public MacroGenerator(String inputFile, String outputFile){
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }
    public void start() throws IOException {
        wholeFileAsString = read().toString();
        findDefinition();
//        System.out.println(macroDefinitionsList.toString());
//       removeDefinitionFromInput();
//        System.out.println(withOutDefinithinAsString);
       mcLib.fillLib(macroDefinitionsList);
        System.out.println(mcLib.toString());
        searchForMacroCalls();
        System.out.println(macroCallsAsList.toString());
        macroCall();


    }
    public StringBuilder read() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String s;
        StringBuilder sb = new StringBuilder();
        while((s = in.readLine()) != null)
            sb.append(s + "\n");
        in.close();
        return sb; //the whole file as a stringBuilder
    }
    public void write(String text) throws IOException {
        PrintWriter out = new PrintWriter(new File(outputFile));
        out.println(text);
        out.close();
    }
    public void findDefinition() throws IOException {
        Pattern checkRegex = Pattern.compile(regexForMacroDefinition);
        Matcher regexMatcher = checkRegex.matcher( wholeFileAsString );
        while ( regexMatcher.find() ){
            if (regexMatcher.group().length() != 0){
                macroDefinitionsList.add(regexMatcher.group());
                System.out.println( regexMatcher.group() );
                System.out.println( "Start Index: " + regexMatcher.start());
                System.out.println( "Start Index: " + regexMatcher.end());
            }
        }
    }

    public void removeDefinitionFromInput() throws IOException {
        Pattern replace = Pattern.compile(regexForMacroDefinition);
        Matcher regexMatcher = replace.matcher(wholeFileAsString);
        StringBuilder sb = new StringBuilder(wholeFileAsString);
        int difference = 0;
        while(regexMatcher.find()){
            if (regexMatcher.group().length() != 0) {
                sb.delete(regexMatcher.start() - difference, (regexMatcher.end() + 1) - difference);
                difference = difference + ((regexMatcher.end() + 1) - regexMatcher.start());
            }
        }
        withOutDefinithinAsString = sb.toString();
    }

    public void searchForMacroCalls(){
        Pattern checkRegex = Pattern.compile(regexForMacroCall);
        Matcher regexMatcher = checkRegex.matcher( wholeFileAsString );
        while ( regexMatcher.find() ){
            if (regexMatcher.group().length() != 0){
                macroCallsAsList.add(regexMatcher.group());
                System.out.println( regexMatcher.group() );
                System.out.println( "Start Index: " + regexMatcher.start());
                System.out.println( "Start Index: " + regexMatcher.end());
            }
        }
    }

    private void macroCall(){
        String macros = macroCallsAsList.get(0);
        String name = macros.substring(1, macros.indexOf('('));
        String[] parameters = macros.substring(macros.indexOf('(')+1, macros.indexOf(')')).split(",");
        int numberOfParameters = macros.substring(macros.indexOf('(')+1, macros.indexOf(')')).trim().split(",").length;
        System.out.println(numberOfParameters);
        System.out.println(name);
        System.out.println(Arrays.toString(parameters));
        System.out.println();
        if(mcLib.search(name)){
            String[] freeText = mcLib.getMacro(name).getFreeText();
            for(int i = 0; i < numberOfParameters; i++){
                System.out.println(freeText[i] + parameters[i]);
            }
        }
    }


}
