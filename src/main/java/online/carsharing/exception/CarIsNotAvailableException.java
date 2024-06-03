package online.carsharing.exception;

public class CarIsNotAvailableException extends RuntimeException {
    public CarIsNotAvailableException(String message) {
        super(message);
    }
}
