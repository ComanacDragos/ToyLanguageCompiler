package entry;

public class FloatValue implements Value{
    Double baseValue;

    public FloatValue(Double baseValue) {
        this.baseValue = baseValue;
    }

    public Double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Double baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public String toString() {
        return baseValue.toString();
    }
}
