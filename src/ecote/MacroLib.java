package ecote;

import java.util.*;

public class MacroLib {
    private List<Macro> macroLib = new ArrayList<Macro>();

    public void fillLib(List<String> listOfMacroses){
        for(String s: listOfMacroses)
            addMacro(s);
    }

    public boolean search(String name) {
        for(Macro m: macroLib){
            if(m.getName().equals(name))
                return true;
        }
        return false;
    }

    public Macro getMacro(String macroName){
        for(Macro m: macroLib){
            if(m.getName().equals(macroName)){
                return m;
            }
        }
        return null; //here i will work with excaptions
    }


    /*while (it.hasNext()) {
  User user = it.next();
  if (user.getName().equals("John Doe")) {
    it.remove();
  }
}*/

    private void addMacro(String textMacro) {
        String name = textMacro.substring(1, textMacro.indexOf('('));
        String[] freeText = textMacro.substring(textMacro.indexOf('{')+1, textMacro.indexOf('}')).split("&[1-9]");
        int numberOfParameters = textMacro.substring(textMacro.indexOf('(')+1, textMacro.indexOf(')')).trim().split("&[1-9]").length;
        Macro m = new Macro(name, numberOfParameters, freeText);
        macroLib.add(m);

    }

    @Override
    public String toString() {
        return "macroLib: \n" + macroLib.toString();
    }
}
