package value;

public class IntValue implements Value{
    Integer baseValue;

    public IntValue(Integer baseValue) {
        this.baseValue = baseValue;
    }

    public Integer getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Integer baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public String toString(){
        return baseValue.toString();
    }
}
