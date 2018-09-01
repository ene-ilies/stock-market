package ro.ilies.bogdan.service;

import ro.ilies.bogdan.model.Stock;
import ro.ilies.bogdan.model.Trade;

import java.math.BigDecimal;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
public interface StockService {

    BigDecimal dividendYield(String stockSymbol, BigDecimal marketPrice);

    BigDecimal PERatio(String stockSymbol, BigDecimal marketPrice);

    void addTrade(Trade trade);

    BigDecimal volumeWeightedStockPriceAfter(String stockSymbol, long timestamp);

    BigDecimal GBCEAllShareIndex();

    void register(Stock stock);
}
