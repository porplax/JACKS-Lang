package jacks;

import jacks.ast.PrintVisitor;
import jacks.ast.Tree;
import jacks.codegen.Builtin;
import jacks.codegen.CodeGeneration;
import jacks.lexer.Lexer;
import jacks.parser.Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
/*
* namespace extends Example
*
* @override
* def hello() -> String
*   return "Hello World!"
* end def
* */
public class Main {
    public static String filename;
    static String outputFilename;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java -jar jacks.jar <file> <output>");
            System.exit(0);
        }

        filename = args[0];
        outputFilename = args[1];

        File file = new File(filename);

        // Get the class name from the file name.
        String className = filename.substring(filename.lastIndexOf('/') + 1, filename.indexOf('.'));

        Lexer lexer = new Lexer();
        Parser parser = new Parser(lexer);
        CodeGeneration generator = new CodeGeneration(className);
        Builtin.generator = generator;

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            for(String line; (line = br.readLine()) != null; ) {
                lexer.scan(line);
            }
        }
        //System.out.println(lexer.tokens);

        parser.parse();
        Tree.Node body = parser.getProgramNode();

        //System.out.println(SymbolTableManager.getSymbolTable());
        //body.accept(new PrintVisitor());
        body.accept(generator);

        generator.end(outputFilename);
        System.out.println("JACKS!\n" + outputFilename + " generated");
        /*

        while (true) {
            System.out.print("jacks> ");
            repl = scanner.nextLine();

            if (repl.equals("exit")) {
                break;
            }

            lexer.scan(repl);
            System.out.println(lexer.tokens);

            parser.parse();
            Tree.Node body = parser.getProgramNode();

            body.accept(new PrintVisitor());
            body.accept(generator);

            System.out.println(SymbolTableManager.getSymbolTable());
            body.children.clear();
            lexer.tokens.clear();
        }

        generator.end("/home/ihavecandy/Projects/Java/jacks/tests/playground.class");

         */
    }
}