package ecote.Exceptions;

public class NotEnoughParameters extends Exception {
    public String getMessage(String name, int paramsFromDeffinition, int paramsFromCall, String mps) {
        return "Error!: Not enough parameters:\n\t\tThe deffinition of macros <" + name + "> requires <" + paramsFromDeffinition + "> parameters\n\t\tbut <" +
                paramsFromCall + "> found. Missing parameters will be substituted with <" + mps + ">";
    }

}
