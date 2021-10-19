package scanner.symbolTable;

import scanner.symbolTable.value.IdentifierValue;
import scanner.symbolTable.value.Value;

public class ComposedSymbolTableImpl implements SymbolTable {
    SymbolTable identifierSymbolTable;
    SymbolTable constantSymbolTable;

    public ComposedSymbolTableImpl(SymbolTable identifierSymbolTable, SymbolTable constantSymbolTable) {
        this.identifierSymbolTable = identifierSymbolTable;
        this.constantSymbolTable = constantSymbolTable;
    }

    @Override
    public Integer getPosition(Value value) {
        if(value instanceof IdentifierValue)
            return identifierSymbolTable.getPosition(value);
        return constantSymbolTable.getPosition(value);
    }

    @Override
    public String toString() {
        return "{" +
                "\"identifierSymbolTable\":" + identifierSymbolTable +
                ", \"constantSymbolTable\":" + constantSymbolTable +
                '}';
    }
}
