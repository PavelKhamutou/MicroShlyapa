package ecote;

import java.io.*;


public class Main {


    public static void main(String[] args) throws IOException {






        String inputPath = "test_cases/input/";
        String outputPath = "test_cases/output/";
        String logPath = "test_cases/logs/";

        try{
            MacroGenerator m1 = new MacroGenerator(inputPath + "test1", outputPath + "out_test1", logPath + "log_test1");
            m1.readFile();
            /*MacroGenerator m2 = new MacroGenerator(inputPath + "test2", outputPath + "out_test2", logPath + "log_test2");
            m2.readFile();
            MacroGenerator m3 = new MacroGenerator(inputPath + "test3", outputPath + "out_test3", logPath + "log_test3");
            m3.readFile();
            MacroGenerator m4 = new MacroGenerator(inputPath + "test4", outputPath + "out_test4", logPath + "log_test4");
            m4.readFile();
            MacroGenerator m5 = new MacroGenerator(inputPath + "test5", outputPath + "out_test5", logPath + "log_test5");
            m5.readFile();
            MacroGenerator m6 = new MacroGenerator(inputPath + "test6", outputPath + "out_test6", logPath + "log_test6");
            m6.readFile();*/

        }catch (RuntimeException e){
            //System.out.println(e.getMessage());
        }

        //System.out.println("Finish!");

    }

}
