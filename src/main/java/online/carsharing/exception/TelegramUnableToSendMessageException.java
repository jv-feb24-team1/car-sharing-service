package online.carsharing.exception;

public class TelegramUnableToSendMessageException extends RuntimeException {
    public TelegramUnableToSendMessageException(String message) {
        super(message);
    }
}
