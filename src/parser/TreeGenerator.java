package parser;

import errors.CompilerError;
import grammar.*;

import java.io.*;
import java.util.*;

public class TreeGenerator {
    Node root;
    Long nextId = 0L;
    Deque<NonTerminalWithProduction> productionStack;
    Grammar grammar;

    public TreeGenerator(Deque<Symbol> workingStack, Grammar grammar){
        this.productionStack = new ArrayDeque<>();
        while(!workingStack.isEmpty()){
            Symbol symbol = workingStack.removeLast();
            if(symbol instanceof NonTerminalWithProduction){
                NonTerminalWithProduction nonTerminalWithProduction = (NonTerminalWithProduction) symbol;
                productionStack.addLast(nonTerminalWithProduction);
            }
        }
        this.grammar = grammar;
    }

    public void generateTree(){
        NonTerminalWithProduction nonTerminalWithProduction = productionStack.pop();
        this.root = new Node(grammar.getNonTerminal(nonTerminalWithProduction.getRepresentation(), true),
                grammar.getProductions().get(nonTerminalWithProduction.getProductionId()).getRhs());
    }

    public void add(NonTerminal value, List<Symbol> children){
        if(Objects.isNull(root))
            root = new Node(value, children);
        else{
            Deque<Node> stack = new ArrayDeque<>();
            root.getChildren().forEach(stack::push);
            while (!stack.isEmpty()){
                Node node = stack.pop();
                if(node.getValue() instanceof NonTerminal){
                    NonTerminal nonTerminal = (NonTerminal) node.getValue();
                    if(node.getChildren().size() == 0){
                        if(!nonTerminal.equals(value))
                            throw new CompilerError("Values do not match: " + value + " != " + nonTerminal);
                        Deque<Node> reverseStack = new ArrayDeque<>();
                        children.stream().map(Node::new).forEach(newNode -> {
                            node.getChildren().add(newNode);
                            reverseStack.push(node);
                        });
                        while (!reverseStack.isEmpty())
                            stack.push(reverseStack.pop());
                        break;
                    }
                }
            }
        }
    }

    public void toFile(String outputDir){
        new File(outputDir).mkdirs();
        try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputDir + "/PT.csv")))){
            writer.println("id,value,father,right-sibling");
            constructTableRecursive(root, null, -1).stream()
                    .sorted(Comparator.comparingLong(a -> Long.parseLong(a.split(",")[0])))
                    .forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> constructTableRecursive(Node currentNode, Node fatherNode, Integer positionRelativeToFather){
        Long father = -1L, rightSibling = -1L;
        if(!Objects.isNull(fatherNode)){
            father = fatherNode.getId();
            if(fatherNode.getChildren().size()-1 > positionRelativeToFather)
                rightSibling = fatherNode.getChildren().get(positionRelativeToFather + 1).getId();
        }
        String representation = currentNode.getId() + "," + currentNode.getValue() + "," + father + "," + rightSibling;
        List<String> representations = new LinkedList<>();
        representations.add(representation);
        for(int i=0;i<currentNode.getChildren().size();i++)
            representations.addAll(constructTableRecursive(currentNode.getChildren().get(i), currentNode, i));
        return representations;
    }

    public class Node{
        Long id = nextId++;
        Symbol value;
        List<Node> children;

        public Node(Symbol value, List<Symbol> symbols) {
            this.value = value;
            this.children = new LinkedList<>();
            symbols.forEach(symbol ->{
                if(symbol instanceof Terminal)
                    children.add(new Node(symbol));
                else{
                    NonTerminal nonTerminal = (NonTerminal)symbol;
                    NonTerminalWithProduction nonTerminalWithProduction = productionStack.pop();
                    if(!nonTerminal.getRepresentation().equals(nonTerminalWithProduction.getRepresentation()))
                        throw new CompilerError("Values do not match: " + nonTerminalWithProduction.getRepresentation() + " != " + nonTerminal);
                    children.add(new Node(nonTerminal,
                            grammar.getProductions().get(nonTerminalWithProduction.getProductionId()).getRhs()));
                }
            });
        }

        public Node(Symbol value) {
            this.value = value;
            this.children = new ArrayList<>();
        }

        public Symbol getValue() {
            return value;
        }

        public List<Node> getChildren() {
            return children;
        }

        public Long getId() {
            return id;
        }
    }
}
