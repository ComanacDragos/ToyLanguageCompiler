import entry.Value;

public interface SymbolTable {
    /*
    Returns the position of the entry if the entry exists,
     otherwise it inserts the entry and returns the new position
     */
    Integer getPosition(Value value);
}
