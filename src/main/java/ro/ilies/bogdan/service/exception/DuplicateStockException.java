package ro.ilies.bogdan.service.exception;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
public class DuplicateStockException extends BusinessException {
    public DuplicateStockException(String message) {
        super(message);
    }
}
