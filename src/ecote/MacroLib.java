package ecote;

import java.util.*;

//addMacros()
//getMacros()


public class MacroLib {
    private Map<String, Macro> mcLib = new HashMap<>();


    public void addMacro(String name, int numberIfParamiters, String[] freeText) {
        if(mcLib.containsKey(name)){
            mcLib.remove(name);
            mcLib.put(name, new Macro(name, numberIfParamiters, freeText));
        }
        else{
            mcLib.put(name, new Macro(name, numberIfParamiters, freeText));
        }
    }

    public Macro getMacros(String name) {
        if(mcLib.containsKey(name)){
            return mcLib.get(name);
        }
        else{
            return null;
        }
    }



    @Override
    public String toString() {
        return "macroLib: \n" + mcLib.toString();
    }
}
