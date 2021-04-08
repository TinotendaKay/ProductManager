package com.learn.app;

import java.io.Console;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class JavaIO {

    public static void main(String[] args) {
        //   scannerFunc();


        FileSystem fs = FileSystems.getDefault();
        fs.getFileStores().forEach(s -> System.out.println(s.type() + ' ' + s.name()));
        fs.getRootDirectories().forEach(path -> System.out.println(path));
        String separator = fs.getSeparator();
        System.out.println("Separator:: " + separator);
        Path path = Paths.get("");
        Path of = Path.of("");

        Console console = System.console();
        if (console == null) {
            System.out.println("Console is not supported.");
            return;
        }

        PrintWriter writer = console.writer();
        String text = null;
        System.out.println("To quit the program, type 'exit'");
        System.out.println("Type value and press enter:");
        while (!(text = console.readLine()).equalsIgnoreCase("exit")) {
            writer.println("Echo: " + text);
        }


    }

    private static void scannerFunc() {
        Scanner sc = new Scanner(System.in);
        PrintStream out = System.out;
        String text = null;
        System.out.println("To quit the program, type 'exit'");
        System.out.println("Type value and press enter:");

        while (!(text = sc.nextLine()).equalsIgnoreCase("exit")) {
            out.println("Echo " + text);
        }
    }
}
