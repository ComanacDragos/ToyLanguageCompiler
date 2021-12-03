import errors.CompilerError;
import fa.FiniteAutomaton;
import grammar.*;
import parser.Parser;
import scanner.Scanner;
import scanner.symbolTable.ComposedSymbolTableImpl;
import scanner.symbolTable.SymbolTable;
import scanner.symbolTable.SymbolTableBSTImpl;
import scanner.symbolTable.value.*;
import tests.TestParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        //testScanner();
        //testFiniteAutomaton();
        //testSymbols();
        //testGrammar();
        //testParser();
        new TestParser();
        testScannerAndParser();
        //testGenerateTree();
    }

    public static void testGenerateTree(){
        try{
            Scanner scanner = new Scanner("data/p.txt");
            Parser parser = new Parser("data/syntax.in", false);
            parser.parse(scanner.getTokens(), "data/p");
            System.out.println("Program is correct");
        }catch (CompilerError error){
            System.out.println(error.getMessage());
        }
    }

    public static void testScannerAndParser(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                System.out.print("program: ");
                try{
                    String input = reader.readLine();
                    if(input.equals("~"))
                        return;
                    Scanner scanner = new Scanner("data/" + input);
                    Parser parser = new Parser("data/syntax.in", false);
                    parser.parse(scanner.getTokens(), "data/" + input.split("\\.")[0]);
                    System.out.println("Program is correct");
                }catch (FileNotFoundException e){
                    System.out.println("Bad file");
                }
                catch (CompilerError e){
                    System.out.println(e.getMessage());
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void testParser(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Grammar file: ");
            String grammarFile = reader.readLine();
            while (true){
                System.out.print("Sequence: ");
                try{
                    String input = reader.readLine();
                    if(input.equals("~"))
                        return;
                    Parser parser = new Parser("data/" + grammarFile, false);
                    List<String> sequence = new LinkedList<>();
                    for(int i=0;i<input.length();i+=1)
                        sequence.add(String.valueOf(input.charAt(i)));
                    parser.parse(sequence, "data/sequence");
                    System.out.println("Sequence is correct");
                }catch (FileNotFoundException e){
                    System.out.println("Bad file");
                }
                catch (CompilerError e){
                    System.out.println(e.getMessage());
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void testGrammar(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Grammar File: ");
            Grammar grammar = new Grammar("data/" + reader.readLine());
            String menu = "\n1. Non terminals\n2. Terminals\n3. Starting symbol\n4. Productions\n5. Productions for non terminal\n";
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
                    case "4" -> grammar.getProductions()
                                        .values()
                                        .stream()
                                        .sorted((a, b) -> (int) (a.getId() - b.getId()))
                                        .forEach(System.out::println);
                    case "5" -> {
                       while(true){
                            try{
                                System.out.print("Non terminal: ");
                                NonTerminal nonTerminal = grammar.getNonTerminal(reader.readLine(), true);
                                grammar.getNonTerminalToProduction().get(nonTerminal)
                                        .stream()
                                        //.sorted((a, b) -> (int) (a.getId() - b.getId()))
                                        .forEach(System.out::println);
                                break;
                            }catch (CompilerError error){
                                System.out.println(error.getMessage());
                            }
                        }
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
        System.out.println(a1 instanceof  NonTerminal); // true
        System.out.println(a1 instanceof  Terminal); // false
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

            System.out.println("p.txt");
            new Scanner("data/p.txt");

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
