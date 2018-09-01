package ro.ilies.bogdan.repository.stock.impl;

import ro.ilies.bogdan.model.Stock;
import ro.ilies.bogdan.repository.stock.StockEntityManager;
import ro.ilies.bogdan.repository.exception.AlreadyExistsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by bogdan-ilies on 24.02.2018.
 */
public class InMemoryStockEntityManager implements StockEntityManager {

    private Map<String, Stock> stocks;

    public InMemoryStockEntityManager() {
        stocks = new HashMap<>();
    }

    @Override
    public void store(Stock stock) {
        throwExceptionIfStockAlreadyExists(stock);
        stocks.put(stock.getStockSymbol(), stock);
    }

    private void throwExceptionIfStockAlreadyExists(Stock stock) {
        if (stocks.containsKey(stock.getStockSymbol())) {
            throw new AlreadyExistsException("A stock with same symbol already exists.");
        }
    }

    @Override
    public Optional<Stock> retrieve(String stockSymbol) {
        return Optional.ofNullable(stocks.get(stockSymbol));
    }

    @Override
    public List<Stock> retrieveAll() {
        return stocks.values().stream().collect(Collectors.toList());
    }
}
