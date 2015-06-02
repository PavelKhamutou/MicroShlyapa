package ecote.Exceptions;

public class NotEnoughParameters extends Exception {
    public String getMessage(String name, int paramsFromDeffinition, int paramsFromCall, String mps, int line) {
        return "Warning!: Not enough parameters. Line<" + line + ">:\n\t\tThe definition of macros <" + name + "> requires <" + paramsFromDeffinition + "> parameters\n\t\tbut <" +
                paramsFromCall + "> found. Missing parameters will be substituted with <" + mps + ">.";
    }

}
