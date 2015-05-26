package ecote.Exceptions;

public class MacrosNotFound extends Exception {
    public String getMessage(String macroName, int line){
        return "Error!: Macros not found. Line<" + line + ">:\n\t\tMacros: <" + macroName + "> not found in the library!";
    }
}
