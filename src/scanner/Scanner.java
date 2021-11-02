package scanner;

import errors.LexicalError;
import fa.FiniteAutomaton;
import scanner.symbolTable.SymbolTable;
import scanner.symbolTable.SymbolTableBSTImpl;
import scanner.symbolTable.Type;
import scanner.symbolTable.TypeFactory;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Scanner {
    //program split by newline
    List<String> programLines;
    //map which encodes each token that can appear in the program
    Map<String, Integer> tokenEncode = new HashMap<>();
    //tokens of the program -- first column of PIF
    List<String> tokens = new ArrayList<>();
    //the position of each token in the symbol table -- second column in PIF
    List<Integer> tokensPositionInSymbolTable = new ArrayList<>();
    //the line of each token in the program -- third column in PIF
    List<Integer> tokensLines = new ArrayList<>();

    SymbolTable symbolTable = new SymbolTableBSTImpl();

    FiniteAutomaton integerFA = new FiniteAutomaton("data/FA_integer.in");
    FiniteAutomaton idFA = new FiniteAutomaton("data/FA_id.in");

    //patterns corresponding to each constant and ID
    Map<Type, String> patterns =Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Type.ID, "^[a-zA-Z]([a-zA-Z0-9_]){0,255}$"),
            new AbstractMap.SimpleEntry<>(Type.INT, "^[+-]?([1-9][0-9]*)|0$"),
            new AbstractMap.SimpleEntry<>(Type.CHAR, "^'[a-zA-Z0-9_]'$"),
            new AbstractMap.SimpleEntry<>(Type.BOOL, "^true|false$"),
            new AbstractMap.SimpleEntry<>(Type.STRING, "^\"[a-zA-Z0-9_\\s]*\"$"),
            new AbstractMap.SimpleEntry<>(Type.FLOAT, "^[+-]?(([1-9][0-9]*)|0)\\.([0-9][0-9]*)$")
    );

    //similar to patterns but each value is a function that returns true if the string is of the type in the key
    //replaces the patterns map in the implementation of the scanner
    Map<Type, Function<String, Boolean>> acceptors = Map.ofEntries(
            new AbstractMap.SimpleEntry<Type, Function<String, Boolean>>(Type.ID, (s)-> idFA.isAccepted(s)),
            new AbstractMap.SimpleEntry<Type, Function<String, Boolean>>(Type.INT, (s)-> integerFA.isAccepted(s)),
            new AbstractMap.SimpleEntry<Type, Function<String, Boolean>>(Type.CHAR, (s)->s.matches("^'[a-zA-Z0-9_]'$")),
            new AbstractMap.SimpleEntry<Type, Function<String, Boolean>>(Type.BOOL, (s)->s.matches("^true|false$")),
            new AbstractMap.SimpleEntry<Type, Function<String, Boolean>>(Type.STRING, (s)->s.matches("^\"[a-zA-Z0-9_\\s]*\"$")),
            new AbstractMap.SimpleEntry<Type, Function<String, Boolean>>(Type.FLOAT, (s)->s.matches("^[+-]?(([1-9][0-9]*)|0)\\.([0-9][0-9]*)$"))
    );

    /*
    Receives the program and outputs the FIP and SymbolTable to a directory corresponding to the program name
        - program and tokens are read from file
        - each line is split by the set of simple operators and by the white spaces that are followed
         by at least 2 quotes
        - empty lines are removed
        - look ahead is applied to create composed tokens
        - the token is processed
        - FIP and Symbol table are written to files
     */
    public Scanner(String program){
        programLines = readFile(program);
        readFile("data/token.in").forEach(token -> tokenEncode.put(token.trim(), tokenEncode.size() + 1));
        String separators = "[><!+\\-*/%=;\\[\\]{}()^]";
        for(int line=1;line<=programLines.size();line++){
            String lineStr = programLines.get(line-1);

            List<String> intermediateTokens = Arrays.stream(lineStr.split(
                "(\s(?=([^\"]*\"[^\"]*\")*[^\"]*$))|((?<="+ separators + ")|(?="+ separators +"))")
                )
                .filter(token -> !token.matches("^\s*$")
                ).collect(Collectors.toList());
            int index = 0;
            while (index != intermediateTokens.size()){
                String token = intermediateTokens.get(index).trim();
                if(token.equals("")) {
                    index++;
                    continue;
                }

                //look ahead
                if(index != intermediateTokens.size() - 1){
                    String nextToken = intermediateTokens.get(index+1);
                    boolean addNextToken = switch (token) {
                        case ">" -> nextToken.equals(">") || nextToken.equals("=");
                        case "<" -> nextToken.equals("<") || nextToken.equals("=");
                        case "!", "=" -> nextToken.equals("=");
                        default -> false;
                    };
                    if(index != 0 && (token.equals("+") || token.equals("-"))){
                        String previousToken = intermediateTokens.get(index-1);
                        addNextToken = !previousToken.equals("id")
                                && !previousToken.equals("constant")
                                && tokenEncode.containsKey(previousToken)
                                && (acceptors.get(Type.INT).apply(nextToken))
                                    || acceptors.get(Type.FLOAT).apply(nextToken);
                    }
                    if(addNextToken){
                        token += nextToken;
                        index++;
                    }
                }
                index++;
                processToken(token, line);
            }
        }

        StringBuilder fipRepresentation = new StringBuilder("token,position,line\n");
        for(int i=0;i<tokens.size();i++){
            fipRepresentation
                    .append(tokens.get(i))
                    .append(",")
                    .append(tokensPositionInSymbolTable.get(i))
                    .append(",")
                    .append(tokensLines.get(i))
                    .append("\n");
        }
        String programName = program.split("\\.")[0];
        new File(programName).mkdirs();
        writeToFile(programName + "/FIP.csv", fipRepresentation.toString());
        writeToFile(programName + "/ST.csv", symbolTable.toString()
                .replace("\"", "\"\"\"")
                .replace("'", "'''")
        );
    }

    /*
    Receives a token and a line
    PIF is represented by the 3 lists: tokens, tokensLines, tokensPositionInSymbolTable
    Classifies the token and adds it to the PIF otherwise it throws a LexicalError at the given line
        - if the token is an operator separator or reserved word it is added to the PIF with the given line
        and the position -1
        - if it is an id or a constant it is added to the PIF with the corresponding type (id or constant)
         and to the Symbol table according to the pattern that the token matches
        - otherwise a lexical error is thrown
     */
    private void processToken(String token, Integer line){
        if(!token.equals("id") && !token.equals("constant") && tokenEncode.containsKey(token)){
            tokens.add(token);
            tokensLines.add(line);
            tokensPositionInSymbolTable.add(-1);
            return;
        }
        for(Map.Entry<Type, Function<String, Boolean>> entry: acceptors.entrySet()){
            if(entry.getValue().apply(token)){
                if(entry.getKey().equals(Type.ID))
                    tokens.add("id");
                else
                    tokens.add("constant");
                tokensLines.add(line);
                tokensPositionInSymbolTable.add(symbolTable.getPosition(TypeFactory.getValue(token, entry.getKey())));
                return;
            }
        }
        throw new LexicalError(token, line);
    }

    //read the lines from a file
    public List<String> readFile(String file){
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }

    //write to a file the content
    public void writeToFile(String file, String content){
        try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))){
            writer.println(content);
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
}
