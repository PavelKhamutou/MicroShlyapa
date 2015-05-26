package ecote;

import java.util.*;
import ecote.Exceptions.*;


public class MacroLib {
    private Map<String, Macro> mcLib = new HashMap<String, Macro>();


    public void addMacro(String name, int numberIfParameters, String[] freeText) throws MacrosNameIsAlreadyUsed {
        if(mcLib.containsKey(name)){
            mcLib.remove(name);
            mcLib.put(name, new Macro(name, numberIfParameters, freeText));
            throw new MacrosNameIsAlreadyUsed();
        }
        else{
            mcLib.put(name, new Macro(name, numberIfParameters, freeText));
        }
    }

    public Macro getMacros(String name) throws MacrosNotFound {
        if(mcLib.containsKey(name)){
            mcLib.get(name).setUsed();
            return mcLib.get(name);
        }
        else {
            throw new MacrosNotFound();
        }
    }

    public List<Macro> unusedMacroses(){
        List<Macro> unused = new ArrayList<Macro>();
        for(String key: mcLib.keySet()){
            if(!mcLib.get(key).getUsed()){
                unused.add(mcLib.get(key));
            }
        }
        return unused;
    }
/*
    @Override
    public String toString() {
        return "macroLib: \n" + mcLib.toString();
    }*/
}
