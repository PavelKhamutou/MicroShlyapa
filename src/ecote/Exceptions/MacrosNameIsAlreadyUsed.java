package ecote.Exceptions;

public class MacrosNameIsAlreadyUsed extends Exception {
    public String getMessage(String macroName, int line){
        return "Warning!: Line<" + line + ">: Name <" + macroName + "> has been already used! Now it will redefined.";
    }
}
