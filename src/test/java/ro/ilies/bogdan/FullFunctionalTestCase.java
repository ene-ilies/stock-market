package ro.ilies.bogdan;

import org.junit.Test;
import ro.ilies.bogdan.model.Stock;
import ro.ilies.bogdan.model.Trade;
import ro.ilies.bogdan.service.StockService;
import ro.ilies.bogdan.service.StockServiceFactory;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
public class FullFunctionalTestCase {

    private static final int PRECISION_DIGITS = 5;

    @Test
    public void fullFunctionalMarket() {
        long startTimestamp = System.currentTimeMillis();

        StockService stockService = StockServiceFactory.getStockService();
        populateWithTestStocks(stockService);
        populateWithTestTrades(stockService, startTimestamp);

        // a.1. Given a market price as input, calculate the dividend yield
        BigDecimal dividendYield = stockService.dividendYield("GIN", BigDecimal.valueOf(100));
        assertThat(dividendYield, equalTo(BigDecimal.valueOf(2d).setScale(PRECISION_DIGITS)));

        // a.2. Given a market price as input, calculate the P/E Ratio
        BigDecimal peRatio = stockService.PERatio("POP", BigDecimal.valueOf(800));
        assertThat(peRatio, equalTo(BigDecimal.valueOf(100).setScale(PRECISION_DIGITS)));

        // a.3. Record a trade, with timestamp, quantity of shares, buy or sell indicator and trade price
        stockService.addTrade(new Trade("POP", startTimestamp - 5*60*1000, 20,
                Trade.Type.BUY, BigDecimal.valueOf(2000)));

        // a.4. Calculate Volume Weighted Stock Price based on trades in past 15 minutes
        BigDecimal volumeWeightedStockPrice = stockService.volumeWeightedStockPriceAfter("ALE", startTimestamp - 15*60*1000);
        assertThat(volumeWeightedStockPrice, equalTo(BigDecimal.valueOf(1727.27273d).setScale(PRECISION_DIGITS)));

        // b. Calculate the GBCE All Share Index using the geometric mean of prices for all stocks
        BigDecimal GBCEAllShareIndex = stockService.GBCEAllShareIndex();
        assertThat(GBCEAllShareIndex, equalTo(BigDecimal.valueOf(108.44718d).setScale(PRECISION_DIGITS)));
    }

    private void populateWithTestTrades(StockService stockService, long startTimestamp) {
        stockService.addTrade(new Trade("ALE", startTimestamp - 16*60*1000, 20,
                Trade.Type.BUY, BigDecimal.valueOf(2000)));
        stockService.addTrade(new Trade("ALE", startTimestamp - 14*60*1000, 10,
                Trade.Type.BUY, BigDecimal.valueOf(2000)));
        stockService.addTrade(new Trade("ALE", startTimestamp - 10*60*1000, 25,
                Trade.Type.SELL, BigDecimal.valueOf(1000)));
        stockService.addTrade(new Trade("ALE", startTimestamp - 5*60*1000, 20,
                Trade.Type.BUY, BigDecimal.valueOf(2500)));
    }

    private void populateWithTestStocks(StockService stockService) {
        stockService.register(new Stock("TEA",
                Stock.Type.COMMON,
                BigDecimal.ZERO,
                null,
                BigDecimal.valueOf(100)));
        stockService.register(new Stock("POP",
                Stock.Type.COMMON,
                BigDecimal.valueOf(8),
                null,
                BigDecimal.valueOf(100)));
        stockService.register(new Stock("ALE",
                Stock.Type.COMMON,
                BigDecimal.valueOf(23),
                null,
                BigDecimal.valueOf(60)));
        stockService.register(new Stock("GIN",
                Stock.Type.PREFERRED, BigDecimal.valueOf(8),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(100)));
        stockService.register(new Stock("JOE",
                Stock.Type.COMMON,
                BigDecimal.valueOf(13),
                null,
                BigDecimal.valueOf(250)));
    }
}
