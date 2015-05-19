package ecote;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class Main {

    /*public static String read(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String s;
        StringBuilder sb = new StringBuilder();
        while((s = in.readLine()) != null)
            sb.append(s + "\n");
        in.close();
        return sb.toString();
    }

    public static void write(String fileName, String text) throws IOException{
        PrintWriter out = new PrintWriter(new File(fileName));
        out.println(text);
        out.close();
    }*/

    public static void main(String[] args) throws IOException {
        MacroGenerator m = new MacroGenerator("test", "testout");
        m.start();

        /*String s = read("test");

        String regex = "#\\w+\\((\\s*(&[1-9])\\s*,?)+\\s*\\)\\s*\\{(\\s*\\w+\\s*&[1-9])+\\s*\\}";
//        {text1 &1 text1 &2 }
//        {text2 &1 text2 &2
//            text2 &3}
//        String regex = "\\{(\\s*\\w+\\s*&[1-9])+\\s*\\}";
        regexChecker(regex, s);*/

    }
/*
    public static void regexChecker(String theRegex, String str2Check) throws IOException {
        Pattern checkRegex = Pattern.compile(theRegex);
        Matcher regexMatcher = checkRegex.matcher( str2Check );
        String s = "";
        while ( regexMatcher.find() ){
            if (regexMatcher.group().length() != 0){
                s += regexMatcher.group() + "\n";
                System.out.println( regexMatcher.group() );
                System.out.println( "Start Index: " + regexMatcher.start());
                System.out.println( "Start Index: " + regexMatcher.end());
            }
        }
        System.out.println();
        write("testout", s);
    }*/
}
