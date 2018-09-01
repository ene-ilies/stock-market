package ro.ilies.bogdan.repository.stock;

import ro.ilies.bogdan.model.Stock;

import java.util.List;
import java.util.Optional;

/**
 * Created by bogdan-ilies on 24.02.2018.
 */
public interface StockEntityManager {

    void store(Stock stock);

    Optional<Stock> retrieve(String stockSymbol);

    List<Stock> retrieveAll();
}
