package value;

public class CharValue implements Value{
    Character baseValue;

    public CharValue(Character baseValue) {
        this.baseValue = baseValue;
    }

    public Character getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Character baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public String toString() {
        return "'" + baseValue + "'";
    }
}
