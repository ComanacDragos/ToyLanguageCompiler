package value;

public class StringValue implements Value {
    String baseValue;

    public StringValue(String baseValue) {
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
        return "\"" + baseValue + "\"";
    }
}
