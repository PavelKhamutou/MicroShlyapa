package ecote.Exceptions;


public class IllegalMacrosDefinition extends Exception {
    private String msg = "";
    public IllegalMacrosDefinition(){}
    public IllegalMacrosDefinition(String msg){
        this.msg = msg;
    }
    public String getMessage(String macros){
        return "Error!: Something wrong with macro definition: \n\t\t" + macros + "\n\t\t" + msg;
    }
}
