package ecote.Exceptions;


public class IllegalMacroDefinition extends Exception {
    private String msg = "";
    public IllegalMacroDefinition(){}
    public IllegalMacroDefinition(String msg){
        this.msg = msg;
    }
    public String getMessage(String macros, int line){
        return "Error!: Something wrong with macro definition. Line<" + line + ">:\n\t\t" + macros + "\n\t\t" + msg;
    }
}
