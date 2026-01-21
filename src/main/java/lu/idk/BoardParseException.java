package lu.idk;

class BoardParseException extends Exception {

    public BoardParseException(int line, String message) {
        super("Error on line " + line + ": " + message);
    }

    public BoardParseException(int line, String message, Throwable error) {
        super("Error on line " + line + ": " + message, error);
    }
}
