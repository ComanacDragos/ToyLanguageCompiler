package scanner.symbolTable;

import scanner.symbolTable.value.*;

/*
    Type factory that generates the corresponding Value class given a token and a type
 */
public class TypeFactory {
    public static Value getValue(String token, Type type){
        return switch (type){
            case ID -> new IdentifierValue(token);
            case INT -> new IntValue(Integer.parseInt(token));
            case CHAR -> new CharValue(token.charAt(1));
            case BOOL -> new BoolValue(Boolean.parseBoolean(token));
            case STRING -> new StringValue(token);
            case FLOAT -> new FloatValue(Double.parseDouble(token));
        };
    }
}
