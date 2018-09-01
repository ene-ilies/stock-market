package ro.ilies.bogdan.repository.trade.impl;

import ro.ilies.bogdan.model.Trade;
import ro.ilies.bogdan.repository.trade.TradeEntityManager;

import java.util.*;

/**
 * Created by bogdan-ilies on 24.02.2018.
 */
public class InMemoryTradeEntityManager implements TradeEntityManager {

    private final Map<String, TreeMap<Long, Trade>> tradesByStockSymbol;

    public InMemoryTradeEntityManager() {
        this.tradesByStockSymbol = new TreeMap<>();
    }

    @Override
    public void store(Trade trade) {
        TreeMap<Long, Trade> trades = tradesByStockSymbol.get(trade.getStockSymbol());
        if (trades == null) {
            trades = new TreeMap<>();
            tradesByStockSymbol.put(trade.getStockSymbol(), trades);
        }
        trades.put(trade.getTimestamp(), trade);
    }

    @Override
    public List<Trade> retrieveAfter(String stockSymbol, long timestamp) {
        TreeMap<Long, Trade> trades = tradesByStockSymbol.get(stockSymbol);
        if (trades == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(trades.tailMap(timestamp)
                .values());
    }
}
