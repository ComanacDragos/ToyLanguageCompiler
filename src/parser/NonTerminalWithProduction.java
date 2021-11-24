package parser;

import grammar.NonTerminal;

import java.util.Objects;

public class NonTerminalWithProduction extends NonTerminal {
    Long productionId;
    public NonTerminalWithProduction(String representation, Long productionId) {
        super(representation);
        this.productionId = productionId;
    }

    public Long getProductionId() {
        return productionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), productionId);
    }

    @Override
    public String toString() {
        return getRepresentation() + "." + productionId;
    }
}
