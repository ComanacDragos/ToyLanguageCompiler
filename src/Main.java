import errors.CompilerError;
import fa.FiniteAutomaton;
import grammar.*;
import scanner.Scanner;
import scanner.symbolTable.ComposedSymbolTableImpl;
import scanner.symbolTable.SymbolTable;
import scanner.symbolTable.SymbolTableBSTImpl;
import scanner.symbolTable.value.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //testScanner();
        //testFiniteAutomaton();
        //testSymbols();
        testGrammar();
    }

    public static void testGrammar(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            //System.out.print("Grammar File: ");
            Grammar grammar = new Grammar("data/syntax_rules_grammar.in"); //+ reader.readLine());
            String menu = "\n1. Non terminals\n2. Terminals\n3. Starting symbol\n4. Productions\n";
            while (true){
                System.out.println(menu);
                String input = reader.readLine();
                switch (input) {
                    case "~" -> {
                        return;
                    }
                    case "1" -> grammar.getNonTerminals().forEach(System.out::println);
                    case "2" -> grammar.getTerminals().forEach(System.out::println);
                    case "3" -> System.out.println(grammar.getStartingSymbol());
                    case "4" -> {
                        List<Production> productions = new LinkedList<>();
                        grammar.getTerminalToProduction().entrySet().forEach(
                            entry -> productions.addAll(entry.getValue())
                    );
                        productions.stream().sorted((a, b) -> (int) (a.getId() - b.getId())).forEach(System.out::println);
                    }
                    default -> System.out.println("Wrong key");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void testSymbols(){
        Symbol a1 = new NonTerminal("a");
        Symbol a2 = new Terminal("a");
        System.out.println(a1.equals(a2)); // false
    }

    //First input: file of FA
    //Provides the menu which allows to see the components of the FA
    //Also it allows to input sequences and to see if they are accepted or not
    public static void testFiniteAutomaton(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("FA File: ");
            FiniteAutomaton fa = new FiniteAutomaton("data/" + reader.readLine());
            String menu = "1. States\n2. Alphabet\n3. Initial state\n4. Final states\n5. Transitions\n6. Accept sequence";
            while (true){
                    System.out.println(menu);
                    String input = reader.readLine();
                    switch (input){
                        case "~" -> {
                            return;
                        }
                        case "1" -> System.out.println(fa.getStates());
                        case "2"-> System.out.println(fa.getAlphabet());
                        case "3" -> System.out.println(fa.getInitialState());
                        case "4" -> System.out.println(fa.getFinalStates());
                        case "5" -> fa.getTransitions().entrySet().forEach(
                                entry-> System.out.println(
                                        "(" + entry.getKey().getKey() + ", " + entry.getKey().getValue() + ")" +
                                                " = " + entry.getValue())
                        );
                        case "6" -> {
                            while (true){
                                System.out.print("Sequence: ");
                                input = reader.readLine();
                                if(input.equals("~"))
                                    break;
                                System.out.println("Is accepted: " + fa.isAccepted(input));
                            }

                        }
                        default -> System.out.println("Wrong key");
                    }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
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
