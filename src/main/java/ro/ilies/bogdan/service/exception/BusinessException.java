package ro.ilies.bogdan.service.exception;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
