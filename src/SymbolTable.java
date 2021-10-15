import value.Value;

public interface SymbolTable {
    /*
    Returns the position of the value if the value exists,
     otherwise it inserts the value and returns the new position
     */
    Integer getPosition(Value value);
}
