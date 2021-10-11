import entry.Value;

public class SymbolTableNode {
    Integer position;
    Value value;
    SymbolTableNode leftChild;
    SymbolTableNode rightChild;

    public SymbolTableNode(Integer position, Value entry) {
        this.position = position;
        this.value = entry;
    }

    public Integer getPosition() {
        return position;
    }

    public Value getValue() {
        return value;
    }

    public SymbolTableNode getLeftChild() {
        return leftChild;
    }

    public SymbolTableNode getRightChild() {
        return rightChild;
    }

    public void setLeftChild(SymbolTableNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(SymbolTableNode rightChild) {
        this.rightChild = rightChild;
    }
}
