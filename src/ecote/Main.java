package ecote;

import java.io.*;


public class Main {


    public static void main(String[] args) throws IOException {

        if(args.length != 3){
            System.err.println("You should provied 3 file name as input parameters!");
            System.err.println("#1: file with macrogenerator [MUST EXIST]!");
            System.err.println("#2: file for output!");
            System.err.println("#3: file for logs!");
            System.exit(1);
        }

        try{
            MacroGenerator m = Singleton.getInstance(args[0], args[1], args[2]);
            m.read();
        }catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Please give input file names!");
        }catch (RuntimeException e){
            System.err.println("Please provide correct file names!");
        }

        System.out.println("Finish!");

    }

}
