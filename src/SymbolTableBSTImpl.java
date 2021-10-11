import entry.Value;

import java.util.Objects;

public class SymbolTableBSTImpl implements SymbolTable{
    int nextPosition = 0;
    SymbolTableNode root;

    @Override
    public Integer getPosition(Value value) {
        if(Objects.isNull(root)){
            root = new SymbolTableNode(nextPosition, value);
            nextPosition += 1;
            return nextPosition - 1;
        }
        SymbolTableNode current = root;
        SymbolTableNode parent;
        Integer difference;
        do {
            difference = value.compareTo(current.getValue());
            parent = current;
            if(difference<0){
                current = current.getLeftChild();
            }
            if(difference>0)
                current = current.getRightChild();
        } while (Objects.nonNull(current) && difference != 0);

        if(Objects.isNull(current)){
            if(difference<0){
                parent.setLeftChild(new SymbolTableNode(nextPosition, value));
            }else{
                parent.setRightChild(new SymbolTableNode(nextPosition, value));
            }
            nextPosition += 1;
            return nextPosition-1;
        }
        return parent.getPosition();
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
