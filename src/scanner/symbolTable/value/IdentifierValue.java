package scanner.symbolTable.value;

public class IdentifierValue implements Value{
    String baseValue;

    public IdentifierValue(String baseValue) {
        this.baseValue = baseValue;
    }

    public String getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(String baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public String toString() {
        return baseValue;
    }
}
