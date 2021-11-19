package grammar;

import errors.CompilerError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    Set<NonTerminal> nonTerminals = new HashSet<>(); // set of non-terminals
    Set<Terminal> terminals = new HashSet<>(); // set of terminals
    NonTerminal startingSymbol; // starting symbol
    HashMap<Long, Production> productions = new HashMap<>(); // maps the id of a production to the respective production
    HashMap<NonTerminal, Set<Production>> nonTerminalToProduction = new HashMap<>(); // maps a non-terminal to all it's productions

    /*
    The file containing a grammar is processed
    First line: the non-terminals separated by space
    Second line: terminals separated by space
    Third line: starting symbol
    Remaining lines: productions: lhs is separated by rhs by ::= and | is used to separate rhs. List of symbols is separated by space
     */
    public Grammar(String file){
        List<String> lines = readFile(file);
        if(lines.size() == 0)
            throw new CompilerError("File " + file + " is empty");
        Arrays.stream(lines.get(0).split(" ")).forEach(representation -> nonTerminals.add(new NonTerminal(representation)));
        Arrays.stream(lines.get(1).split(" ")).forEach(representation -> terminals.add(new Terminal(representation)));

        startingSymbol = getNonTerminal(lines.get(2), true);

        for(int i=3; i<lines.size(); i++){
            if(lines.get(i).trim().length() == 0)
                continue;
            String[] tokens = lines.get(i).split("::=");
            NonTerminal lhs;
            try{
                lhs = getNonTerminal(tokens[0].trim(), true);
            }catch (CompilerError compilerError){
                throw new CompilerError("Grammar is not context free");
            }
            Set<Production> productions = new HashSet<>();
            Arrays.stream(tokens[1].trim().split("\\|")).forEach(productionString ->{
                List<Symbol> rhs = new LinkedList<>();
                Arrays.stream(productionString.trim().split(" ")).forEach(representation ->{
                    if(representation.equals("epsilon"))
                        rhs.add(new Epsilon());
                    else{
                        NonTerminal nonTerminal = getNonTerminal(representation, false);
                        if(Objects.isNull(nonTerminal)){
                            rhs.add(getTerminal(representation, true));
                        }else{
                            rhs.add(nonTerminal);
                        }
                    }
                });
                Production production = new Production(lhs, rhs);
                productions.add(production);
            });
            if(nonTerminalToProduction.containsKey(lhs))
                nonTerminalToProduction.get(lhs).addAll(productions);
            else
                nonTerminalToProduction.put(lhs, productions);
        }
        nonTerminalToProduction.entrySet()
                .forEach(entry -> entry.getValue()
                        .forEach(production -> productions.put(production.getId(), production)));
    }

    /*
    Returns the non-terminal with the given representation from the list of non-terminals
    If the non-terminal does not exist and throwException is true, an exception is thrown
    If the non-terminal does not exist and throwException is false null is returned
     */
    public NonTerminal getNonTerminal(String representation, boolean throwException){
        for(NonTerminal nonTerminal : nonTerminals)
            if(nonTerminal.getRepresentation().equals(representation))
                return nonTerminal;
        if(throwException)
            throw new CompilerError(representation + " is not declared in non terminal list");
        return null;
    }

    /*
    Returns the terminal with the given representation from the list of terminals
    If the terminal does not exist and throwException is true, an exception is thrown
    If the terminal does not exist and throwException is false null is returned
     */
    public Terminal getTerminal(String representation, boolean throwException){
        for(Terminal terminal : terminals)
            if(terminal.getRepresentation().equals(representation))
                return terminal;
        if(throwException)
            throw new CompilerError(representation + " is not declared in terminal list");
        return null;
    }

    public Set<NonTerminal> getNonTerminals() {
        return nonTerminals;
    }

    public Set<Terminal> getTerminals() {
        return terminals;
    }

    public NonTerminal getStartingSymbol() {
        return startingSymbol;
    }

    public HashMap<Long, Production> getProductions() {
        return productions;
    }

    public HashMap<NonTerminal, Set<Production>> getNonTerminalToProduction() {
        return nonTerminalToProduction;
    }

    //read the lines from a file
    public List<String> readFile(String file){
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
