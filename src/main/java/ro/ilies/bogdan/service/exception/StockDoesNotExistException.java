package ro.ilies.bogdan.service.exception;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
public class StockDoesNotExistException extends BusinessException {
    public StockDoesNotExistException(String message) {
        super(message);
    }
}
