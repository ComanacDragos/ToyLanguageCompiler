import value.*;

import java.util.Arrays;
import java.util.List;

/*
VAR H,F,K,A: INTEGER;
BEGIN
READ(H);
READ(Y);
K:=H+Y;
IF K>5 THEN BEGIN
A:=K+H
END
ELSE
A:=K+Y;
WRITE(A)

END.
 */

public class Main {
    public static void main(String[] args) {
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
