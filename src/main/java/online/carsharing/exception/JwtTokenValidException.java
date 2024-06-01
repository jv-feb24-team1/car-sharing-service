package online.carsharing.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenValidException extends AuthenticationException {
    public JwtTokenValidException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
