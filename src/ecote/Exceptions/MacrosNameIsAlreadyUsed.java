package ecote.Exceptions;

public class MacrosNameIsAlreadyUsed extends Exception {
    public String getMessage(String macroName, int line){
        return "Warning!: Incorrect macro name. Line<" + line + ">:\n\t\t  <" + macroName + "> has been already used!";
    }
}
