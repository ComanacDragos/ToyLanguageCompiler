package errors;

public class LexicalError extends CompilerError{
    public LexicalError(String token, Integer line){
        super("Lexical error at line: " + line + " for token: '" + token + "'");
    }
}
