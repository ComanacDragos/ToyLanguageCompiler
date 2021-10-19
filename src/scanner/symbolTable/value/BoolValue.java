package scanner.symbolTable.value;

public class BoolValue implements Value{
    Boolean baseValue;

    public BoolValue(Boolean baseValue) {
        this.baseValue = baseValue;
    }

    public Boolean getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Boolean baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public String toString(){
        return baseValue.toString();
    }
}
