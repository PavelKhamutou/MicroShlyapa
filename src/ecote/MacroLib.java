package ecote;

import java.util.*;
import ecote.Exceptions.*;

//addMacros()
//getMacros()


public class MacroLib {
    private Map<String, Macro> mcLib = new HashMap<String, Macro>();


    public void addMacro(String name, int numberIfParamiters, String[] freeText) throws MacrosNameIsAlreadyUsed {
        if(mcLib.containsKey(name)){
            mcLib.remove(name);
            mcLib.put(name, new Macro(name, numberIfParamiters, freeText));
            throw new MacrosNameIsAlreadyUsed();
        }
        else{
            mcLib.put(name, new Macro(name, numberIfParamiters, freeText));
        }
    }

    public Macro getMacros(String name) throws MacrosNotFound {
        if(mcLib.containsKey(name)){
            return mcLib.get(name);
        }
        else {
            throw new MacrosNotFound();
        }
    }



    @Override
    public String toString() {
        return "macroLib: \n" + mcLib.toString();
    }
}
