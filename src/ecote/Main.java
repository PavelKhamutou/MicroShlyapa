package ecote;

import java.io.*;

public class Main {


    public static void main(String[] args) throws IOException {
        MacroGenerator m = Singleton.getInstance("test", "testout");
        m.read();
        m.show();


    }

}
