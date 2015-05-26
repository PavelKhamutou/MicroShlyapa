package ecote.Exceptions;

public class MacrosNotFound extends Exception {
    public String getMessage(String macroName){
        return "Error!: Macros not found:\n\t\tMacros: <" + macroName + "> not found in the library!";
    }
}
