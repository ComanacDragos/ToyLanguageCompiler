package grammar;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Production {
    public static long nextId = 0;
    Long id;
    NonTerminal lhs;
    List<Symbol> rhs;

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
    public String toString() {
        return id + ". " + lhs + " -> " + rhs.stream()
                                        .map(Symbol::toString)
                                        .collect(Collectors.joining(" "));
    }
}
