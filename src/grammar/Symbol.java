package grammar;

import java.util.Objects;

public class Symbol {
    protected String representation;

    protected Symbol(String representation){
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    @Override
    public String toString() {
        return representation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(representation, symbol.representation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(representation);
    }
}
