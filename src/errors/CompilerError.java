package errors;

public class CompilerError extends RuntimeException {
    protected String message;

    public CompilerError() {
    }

    public CompilerError(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
