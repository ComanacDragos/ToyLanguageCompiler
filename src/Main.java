import errors.CompilerError;
import scanner.Scanner;
import scanner.symbolTable.ComposedSymbolTableImpl;
import scanner.symbolTable.SymbolTable;
import scanner.symbolTable.SymbolTableBSTImpl;
import scanner.symbolTable.value.*;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        testScanner();
    }

    public static void testScanner(){
        try{
            System.out.println("p1");
            new Scanner("data/p1.txt");
            System.out.println("p2");
            new Scanner("data/p2.txt");
            System.out.println("p3");
            new Scanner("data/p3.txt");
            System.out.println("p_err");
            new Scanner("data/p_err.txt");
        }catch (CompilerError e){
            System.out.println(e);
        }

    }

    public static void testSymbolTable(String[] args) {

        List<Value> tokens1 = Arrays.asList(
                new IdentifierValue("H"),
                new IdentifierValue("F"),
                new IdentifierValue("K"),
                new IdentifierValue("A"),
                new IdentifierValue("H"),
                new IdentifierValue("Y"),
                new IdentifierValue("K"),
                new IdentifierValue("H"),
                new IdentifierValue("Y"),
                new IdentifierValue("K"),
                new IntValue(5),
                new IdentifierValue("A"),
                new IdentifierValue("K"),
                new IdentifierValue("H"),
                new IdentifierValue("A"),
                new IdentifierValue("H"),
                new IdentifierValue("Y"),
                new IdentifierValue("A")
        );
        List<Value> tokens2 = Arrays.asList(
                new BoolValue(false),
                new IdentifierValue("a"),
                new IntValue(2),
                new BoolValue(true),
                new StringValue("bbb"),
                new CharValue('b'),
                new CharValue('c'),
                new BoolValue(false),
                new IdentifierValue("a"),
                new IntValue(2),
                new BoolValue(true),
                new StringValue("bbb"),
                new CharValue('b'),
                new CharValue('c'),
                new IdentifierValue("b"),
                new IdentifierValue("c")
        );

        runOneSymbolTable(tokens1);
        runOneSymbolTable(tokens2);
        runComposedSymbolTable(tokens1);
        runComposedSymbolTable(tokens2);
    }


    public static void runOneSymbolTable(List<Value> tokens){
        SymbolTable oneSymbolTable = new SymbolTableBSTImpl();
        run(oneSymbolTable, tokens);
    }

    public static void runComposedSymbolTable(List<Value> tokens){
        SymbolTable composedSymbolTable = new ComposedSymbolTableImpl(
                new SymbolTableBSTImpl(),
                new SymbolTableBSTImpl()
        );
        run(composedSymbolTable, tokens);
    }

    public static void run(SymbolTable symbolTable, List<Value> tokens){
        tokens.forEach(token -> {
            Integer position = symbolTable.getPosition(token);
            System.out.println(token + " --> " + position);
        });
        System.out.println(symbolTable);
        System.out.println();
    }
}
