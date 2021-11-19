package grammar;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Production {
    public static long nextId = 0; // global id
    Long id; // local id
    NonTerminal lhs; // left-hand side of the production
    List<Symbol> rhs; // right-hand side of the production

    public Production(NonTerminal lhs, List<Symbol> rhs){
        this.rhs = rhs;
        this.lhs = lhs;
        this.id = nextId;
        nextId += 1;
    }

    public Long getId() {
        return id;
    }

    public NonTerminal getLhs() {
        return lhs;
    }

    public List<Symbol> getRhs() {
        return rhs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(lhs, that.lhs) && Objects.equals(rhs, that.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }

    @Override
    public String toString() {
        return id + ". " + lhs + " -> " + rhs.stream()
                                        .map(Symbol::toString)
                                        .collect(Collectors.joining(" "));
    }
}
