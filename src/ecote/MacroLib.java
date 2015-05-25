package ecote;

import java.util.*;

public class MacroLib {
    private List<Macro> macroLib = new ArrayList<Macro>();

    /*while (it.hasNext()) {
  User user = it.next();
  if (user.getName().equals("John Doe")) {
    it.remove();
  }
}*/

    public void addMacro(String name, int numberIfParamiters, String[] freeText) {
        macroLib.add(new Macro(name, numberIfParamiters, freeText));
    }

    public Macro getMacros(String name) {
        return macroLib.get(0);
    }

    @Override
    public String toString() {
        return "macroLib: \n" + macroLib.toString();
    }
}
