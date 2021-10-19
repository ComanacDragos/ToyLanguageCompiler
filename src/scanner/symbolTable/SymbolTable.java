package scanner.symbolTable;

import scanner.symbolTable.value.Value;

public interface SymbolTable {
    /*
    Returns the position of the scanner.symbolTable.value if the scanner.symbolTable.value exists,
     otherwise it inserts the scanner.symbolTable.value and returns the new position
     */
    Integer getPosition(Value value);
}
