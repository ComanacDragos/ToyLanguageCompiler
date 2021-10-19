package scanner.symbolTable.value;

public interface Value {
    String toString();

    default Integer compareTo(Value otherValue){
        return this.toString().compareTo(otherValue.toString());
    }
}
