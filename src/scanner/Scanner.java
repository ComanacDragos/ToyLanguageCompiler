package scanner;

import errors.LexicalError;
import scanner.symbolTable.SymbolTable;
import scanner.symbolTable.SymbolTableBSTImpl;
import scanner.symbolTable.Type;
import scanner.symbolTable.TypeFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Scanner {
    List<String> programLines;
    Map<String, Integer> tokenEncode = new HashMap<>();
    List<String> tokens = new ArrayList<>();
    List<Integer> tokensPositionInSymbolTable = new ArrayList<>();
    List<Integer> tokensLines = new ArrayList<>();
    SymbolTable symbolTable = new SymbolTableBSTImpl();
    Map<Type, String> patterns =Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Type.ID, "^[a-zA-Z]([a-zA-Z0-9_]){0,255}$"),
            new AbstractMap.SimpleEntry<>(Type.INT, "^[+-]?([1-9][0-9]*)|0$"),
            new AbstractMap.SimpleEntry<>(Type.CHAR, "^'[a-zA-Z0-9_]'$"),
            new AbstractMap.SimpleEntry<>(Type.BOOL, "^true|false$"),
            new AbstractMap.SimpleEntry<>(Type.STRING, "^\"[a-zA-Z0-9_\\s]*\"$"),
            new AbstractMap.SimpleEntry<>(Type.FLOAT, "^[+-]?(([1-9][0-9]*)|0)\\.([0-9][0-9]*)$")
    );

    public Scanner(String program){
        programLines = readFile(program);
        readFile("data/token.in").forEach(token -> tokenEncode.put(token.trim(), tokenEncode.size() + 1));
        String separators = "[><!+\\-*/%=;\\[\\]{}(),^]";
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
                                && (nextToken.matches(patterns.get(Type.INT))
                                    || nextToken.matches(patterns.get(Type.FLOAT)));
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

    private void processToken(String token, Integer line){
        if(!token.equals("id") && !token.equals("constant") && tokenEncode.containsKey(token)){
            tokens.add(token);
            tokensLines.add(line);
            tokensPositionInSymbolTable.add(-1);
            return;
        }
        for(Map.Entry<Type, String> entry:patterns.entrySet()){
            if(token.matches(entry.getValue())){
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

    public List<String> readFile(String file){
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }

    public void writeToFile(String file, String content){
        try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))){
            writer.println(content);
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
}
