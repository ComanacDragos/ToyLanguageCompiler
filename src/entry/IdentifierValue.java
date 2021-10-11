package entry;

public class IdentifierValue {
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
