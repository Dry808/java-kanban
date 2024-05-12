package exceptions;

public class NotAcceptableException extends RuntimeException{
    public NotAcceptableException(Exception e) {
        super(e);
    }
}
