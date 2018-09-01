package ro.ilies.bogdan.repository.trade;

import ro.ilies.bogdan.model.Trade;

import java.util.List;

/**
 * Created by bogdan-ilies on 24.02.2018.
 */
public interface TradeEntityManager {
    void store(Trade trade);

    List<Trade> retrieveAfter(String stockSymbol, long timestamp);
}
