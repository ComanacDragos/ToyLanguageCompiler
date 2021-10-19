package scanner.symbolTable;

import scanner.symbolTable.value.Value;

import java.util.*;
import java.util.stream.Collectors;

public class SymbolTableBSTImpl implements SymbolTable {
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
        return current.getPosition();
    }

    private void completeMap(SymbolTableNode current, SymbolTableNode parent, Map<Integer, RepresentationNode> positionToValue, Boolean isLeftChild){
        if(Objects.isNull(current))
            return;
        String value = current.getValue().toString();
        Integer parentPosition = -1, siblingPosition = -1;

        if(Objects.nonNull(parent)){
            parentPosition = parent.getPosition();
            if(isLeftChild && Objects.nonNull(parent.getRightChild()))
                siblingPosition = parent.getRightChild().getPosition();
            if(!isLeftChild && Objects.nonNull(parent.getLeftChild()))
                siblingPosition = parent.getLeftChild().getPosition();
        }
        positionToValue.put(current.getPosition(), new RepresentationNode(value, parentPosition, siblingPosition));
        completeMap(current.getLeftChild(), current, positionToValue, true);
        completeMap(current.getRightChild(), current, positionToValue, false);
    }


    @Override
    public String toString() {
        Map<Integer, RepresentationNode> positionToValue = new HashMap<>();
        completeMap(root, null, positionToValue, null);

        return "position,value,parent,sibling\n" + positionToValue.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(entry -> entry.getKey() + "," + entry.getValue())
                .collect(Collectors.joining("\n"));
    }
    private static class RepresentationNode{
        String value;
        Integer parent, sibling;

        public RepresentationNode(String value, Integer parent, Integer sibling) {
            this.value = value;
            this.parent = parent;
            this.sibling = sibling;
        }

        @Override
        public String toString() {
            return value + "," + parent + "," + sibling;
        }
    }
}
