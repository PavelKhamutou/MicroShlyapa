package ecote.Exceptions;


public class IllegalMacroCall extends Exception {
    private String msg = "";
    public IllegalMacroCall(){}
    public IllegalMacroCall(String msg){ this.msg = msg;}
    public String getMessage(String macroCall, int line){
        return "Error!: Something wrong with macro call. Line<" + line + ">: \n\t\t" + macroCall + "\n\t\t" + msg;
    }
}
