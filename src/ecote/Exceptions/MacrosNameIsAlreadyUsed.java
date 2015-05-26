package ecote.Exceptions;

public class MacrosNameIsAlreadyUsed extends Exception {
    public String getMessage(String macroName){
        return "Warning!: Incorrect macro name:\n\t\t  <" + macroName + "> has been already used!";
    }
}
