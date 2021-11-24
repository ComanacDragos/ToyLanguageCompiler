package parser;

import errors.CompilerError;
import grammar.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//descendent recursive
public class Parser {
    Grammar grammar;

    ParserState currentState = ParserState.NormalState; // current state of the parser
    int i = 0; // position of the current symbol in input sequence

    Deque<Symbol> workingStack = new ArrayDeque<>();
    Deque<Symbol> inputStack = new ArrayDeque<>();

    Boolean enableLogs;

    public Parser(String grammarFile, Boolean enableLogs){
        grammar = new Grammar(grammarFile);
        this.enableLogs = enableLogs;
    }

    public void init(){
        currentState = ParserState.NormalState;
        i = 0;
        workingStack = new ArrayDeque<>();
        inputStack = new ArrayDeque<>();
        inputStack.push(grammar.getStartingSymbol());
    }

    public void parse(List<String> sequence){
        init();
        int step = 0;
        if(enableLogs)
            printState(step);
        while(!currentState.equals(ParserState.FinalState) && !currentState.equals(ParserState.ErrorState)){
            if(currentState.equals(ParserState.NormalState)){
                if(i == sequence.size() && inputStack.isEmpty()) {
                    success();
                }
                else{
                    if(inputStack.peek() instanceof NonTerminal){
                        expand();
                    }else{
                        Terminal terminal = (Terminal) inputStack.peek();
                        if(terminal instanceof Epsilon){
                            epsilonAdvance();
                        }else{
                            if(i<sequence.size() && Objects.nonNull(terminal) && terminal.getRepresentation().equals(sequence.get(i))){
                                advance();
                            }else{
                                momentaryInsuccess();
                            }
                        }
                    }
                }
            }else{
                if(currentState.equals(ParserState.BackState))
                    if(workingStack.peek() instanceof Terminal){
                        back();
                    }else{
                        anotherTry();
                    }
            }
            if(enableLogs)
                printState(++step);
            if(step == 100){
                System.out.println("Not good");
                return;
            }
        }

        if (currentState.equals(ParserState.ErrorState)) {
            throw new CompilerError("Sequence is not accepted");
        } else {
            if(enableLogs)
                System.out.println("Sequence accepted");
        }
    }

    public void printState(int step){
        System.out.println("Step " + step);
        System.out.println(currentState);
        System.out.println(i);
        System.out.println(workingStack);
        System.out.println(inputStack);
        System.out.println();
    }

    public void expand(){
        if(enableLogs)
            System.out.println("expand");
        NonTerminal nonTerminal = (NonTerminal) inputStack.pop();
        Production firstProduction = grammar.getNonTerminalToProduction()
                .get(nonTerminal).stream().findFirst().get();
        workingStack.push(new NonTerminalWithProduction(nonTerminal.getRepresentation(), firstProduction.getId()));
        List<Symbol> rhs = firstProduction.getRhs();
        for(int i=rhs.size()-1;i>=0;i--)
            inputStack.push(rhs.get(i));
    }

    public void advance(){
        if(enableLogs)
            System.out.println("advance");
        i+=1;
        workingStack.push(inputStack.pop());
    }

    public void epsilonAdvance(){
        if(enableLogs)
            System.out.println("epsilon advance");
        workingStack.push(inputStack.pop());
    }

    public void momentaryInsuccess(){
        if(enableLogs)
            System.out.println("momentary insuccess");
        currentState = ParserState.BackState;
    }

    public void back(){
        if(enableLogs)
            System.out.println("back");
        Terminal terminal = (Terminal) workingStack.pop();
        if(!(terminal instanceof Epsilon))
            i-=1;
        inputStack.push(terminal);
    }

    public void anotherTry(){
        if(enableLogs)
            System.out.println("another try");
        NonTerminalWithProduction nonTerminalWithProduction = (NonTerminalWithProduction) workingStack.pop();
        Production nextProduction = grammar.getNextProductionForNonTerminal(nonTerminalWithProduction.getProductionId());
        if(Objects.isNull(nextProduction)){
            if(i==0 && nonTerminalWithProduction.getRepresentation().equals(grammar.getStartingSymbol().getRepresentation())){
                currentState = ParserState.ErrorState;
                return;
            }
            Production currentProduction = grammar.getProductions().get(nonTerminalWithProduction.getProductionId());
            for(int i=0;i<currentProduction.getRhs().size();i++)
                inputStack.pop();
            inputStack.push(new NonTerminal(nonTerminalWithProduction.getRepresentation()));
        }else {
            Production currentProduction = grammar.getProductions().get(nonTerminalWithProduction.getProductionId());
            for(int i=0;i<currentProduction.getRhs().size();i++)
                inputStack.pop();
            workingStack.push(
                    new NonTerminalWithProduction(nonTerminalWithProduction.getRepresentation(),
                            nextProduction.getId()));
            for(int i=nextProduction.getRhs().size()-1;i>=0;i--)
                inputStack.push(nextProduction.getRhs().get(i));
            currentState = ParserState.NormalState;
        }
    }

    public void success(){
        if(enableLogs)
            System.out.println("success");
        currentState = ParserState.FinalState;
    }

    public ParserState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ParserState currentState) {
        this.currentState = currentState;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public Deque<Symbol> getWorkingStack() {
        return workingStack;
    }

    public void setWorkingStack(Deque<Symbol> workingStack) {
        this.workingStack = workingStack;
    }

    public Deque<Symbol> getInputStack() {
        return inputStack;
    }

    public void setInputStack(Deque<Symbol> inputStack) {
        this.inputStack = inputStack;
    }

    public enum ParserState{
        NormalState,
        BackState,
        FinalState,
        ErrorState
    }
}
