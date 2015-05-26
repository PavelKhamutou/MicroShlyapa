package ecote;


import java.util.Arrays;


public class Macro {
    private String name;
    private int numberOfParameters;
    private String[] freeText;
    private boolean isUsed = false;

    public Macro(String name, int numberOfParameters, String[] freeText) {
        this.name = name;
        this.numberOfParameters = numberOfParameters;
        this.freeText = freeText.clone();
    }

    public int getNumberOfParameters() {
        return numberOfParameters;
    }

    public String[] getFreeText() {
        return freeText;
    }

    public String getName() {
        return name;
    }

    public void setUsed() { isUsed = true; }

    public boolean getUsed() { return isUsed; }

    @Override
    public String toString() {
        return "\tMacros info: \n\t\t" + "Name: " + getName() + "\n\t\t" + "numberOfParameters: " + getNumberOfParameters() + "\n\t\tfree text: " + Arrays.toString(freeText) + "\n";
    }
}
