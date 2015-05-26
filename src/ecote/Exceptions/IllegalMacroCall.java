package ecote.Exceptions;


public class IllegalMacroCall extends Exception {
    public String getMessage(String macroCall, int line){
        return "Error!: Something wrong with macro call. Line<" + line + ">: \n\t\t" + macroCall;
    }
}
