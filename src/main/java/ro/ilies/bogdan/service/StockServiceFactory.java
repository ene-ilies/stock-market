package ro.ilies.bogdan.service;

import ro.ilies.bogdan.repository.stock.impl.InMemoryStockEntityManager;
import ro.ilies.bogdan.repository.trade.impl.InMemoryTradeEntityManager;
import ro.ilies.bogdan.service.impl.StockServiceImpl;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
public class StockServiceFactory {

    public static StockService getStockService() {
        return new StockServiceImpl(new InMemoryStockEntityManager(), new InMemoryTradeEntityManager());
    }
}
