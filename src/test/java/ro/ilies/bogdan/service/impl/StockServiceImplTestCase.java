package ro.ilies.bogdan.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ro.ilies.bogdan.model.Stock;
import ro.ilies.bogdan.model.Trade;
import ro.ilies.bogdan.repository.stock.StockEntityManager;
import ro.ilies.bogdan.repository.stock.impl.InMemoryStockEntityManager;
import ro.ilies.bogdan.repository.trade.impl.InMemoryTradeEntityManager;
import ro.ilies.bogdan.service.exception.DuplicateStockException;
import ro.ilies.bogdan.service.exception.NoStockFoundException;
import ro.ilies.bogdan.service.exception.NoTradeFoundException;
import ro.ilies.bogdan.service.exception.StockDoesNotExistException;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class StockServiceImplTestCase {

    private static final int PRECISION_DIGITS = 5;

    private StockServiceImpl stockService;
    @Captor
    private ArgumentCaptor<Trade> tradeArgumentCaptor;
    @Captor
    private ArgumentCaptor<Stock> stockArgumentCaptor;
    private StockEntityManager stockEntityManager;
    private InMemoryTradeEntityManager tradeEntityManager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        stockService = new StockServiceImpl(getInitializedStockEntityManager(), getInitializedTradeEntityManager());
    }

    private InMemoryTradeEntityManager getInitializedTradeEntityManager() {
        tradeEntityManager = Mockito.spy(new InMemoryTradeEntityManager());
        return tradeEntityManager;
    }

    private StockEntityManager getInitializedStockEntityManager() {
        StockEntityManager stockEntityManager = new InMemoryStockEntityManager();
        stockEntityManager.store(new Stock("TEA",
                Stock.Type.COMMON,
                BigDecimal.ZERO,
                null,
                BigDecimal.valueOf(100)));
        stockEntityManager.store(new Stock("POP",
                Stock.Type.COMMON,
                BigDecimal.valueOf(8),
                null,
                BigDecimal.valueOf(100)));
        stockEntityManager.store(new Stock("ALE",
                Stock.Type.COMMON,
                BigDecimal.valueOf(23),
                null,
                BigDecimal.valueOf(60)));
        stockEntityManager.store(new Stock("GIN",
                Stock.Type.PREFERRED, BigDecimal.valueOf(8),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(100)));
        stockEntityManager.store(new Stock("JOE",
                Stock.Type.COMMON,
                BigDecimal.valueOf(13),
                null,
                BigDecimal.valueOf(250)));
        this.stockEntityManager = Mockito.spy(stockEntityManager);
        return this.stockEntityManager;
    }

    @Test
    public void thatDividendYieldIsCorrectlyComputedForCommonStock() {
        BigDecimal dividendYield = stockService.dividendYield("POP", BigDecimal.valueOf(100));
        assertThat(dividendYield, equalTo(BigDecimal.valueOf(0.08d).setScale(PRECISION_DIGITS)));
    }

    @Test
    public void thatDividendYieldIsCorrectlyComputedForPreferredStock() {
        BigDecimal dividendYield = stockService.dividendYield("GIN", BigDecimal.valueOf(100));
        assertThat(dividendYield, equalTo(BigDecimal.valueOf(2d).setScale(PRECISION_DIGITS)));
    }

    @Test
    public void thatWhenInvalidStockSymbolProvidedThenDividendYieldThrowsException() {
        expectedException.expect(StockDoesNotExistException.class);
        expectedException.expectMessage("No stock found for symbol: FAKE");
        stockService.dividendYield("FAKE", BigDecimal.valueOf(100));
    }

    @Test
    public void thatWhenNullMarketPriceProvidedThenDividendYieldThrowsException() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Market price cannot be null.");
        stockService.dividendYield("GIN", null);
    }

    @Test
    public void thatWhenNullStockSymbolProvidedThenDividendYieldThrowsException() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Stock symbol cannot be null.");
        stockService.dividendYield(null, BigDecimal.ONE);
    }

    @Test
    public void thatWhenInvalidMarketPriceProvidedThenDividendYieldThrowsException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Market price cannot be less than or equal to 0.");
        stockService.dividendYield("GIN", BigDecimal.ZERO);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Market price cannot be less than or equal to 0.");
        stockService.dividendYield("GIN", BigDecimal.valueOf(-1));
    }

    @Test
    public void thatPERatioIsCorrectlyComputedForCommonStock() {
        BigDecimal peRatio = stockService.PERatio("POP", BigDecimal.valueOf(800));
        assertThat(peRatio, equalTo(BigDecimal.valueOf(100).setScale(PRECISION_DIGITS)));
    }

    @Test
    public void thatPERatioIsCorrectlyComputedForPreferredStock() {
        BigDecimal peRatio = stockService.PERatio("GIN", BigDecimal.valueOf(800));
        assertThat(peRatio, equalTo(BigDecimal.valueOf(100).setScale(PRECISION_DIGITS)));
    }

    @Test
    public void thatWhenInvalidStockSymbolProvidedThenPERatioThrowsException() {
        expectedException.expect(StockDoesNotExistException.class);
        expectedException.expectMessage("No stock found for symbol: FAKE");
        stockService.PERatio("FAKE", BigDecimal.valueOf(100));
    }

    @Test
    public void thatWhenNullMarketPriceProvidedThenPERatioThrowsException() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Market price cannot be null.");
        stockService.PERatio("GIN", null);
    }

    @Test
    public void thatWhenNullStockSymbolProvidedThenPERatioThrowsException() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Stock symbol cannot be null.");
        stockService.PERatio(null, BigDecimal.ONE);
    }

    @Test
    public void thatWhenInvalidMarketPriceProvidedThenPERatioThrowsException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Market price cannot be less than or equal to 0.");
        stockService.PERatio("GIN", BigDecimal.ZERO);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Market price cannot be less than or equal to 0.");
        stockService.PERatio("GIN", BigDecimal.valueOf(-1));
    }

    @Test
    public void thatTradeCanBeAddedForAStock() {
        Trade trade = new Trade("ALE", System.currentTimeMillis(), 20, Trade.Type.BUY, BigDecimal.valueOf(2000));
        stockService.addTrade(trade);

        verify(tradeEntityManager, times(1)).store(tradeArgumentCaptor.capture());
        assertThat(tradeArgumentCaptor.getValue(), equalTo(trade));
    }

    @Test
    public void thatWhenTradeIsAddedToInexistentStockThenAddTradeThrowsException() {
        Trade trade = new Trade("FAKE", System.currentTimeMillis(), 20, Trade.Type.BUY, BigDecimal.valueOf(2000));
        expectedException.expect(StockDoesNotExistException.class);
        expectedException.expectMessage("No stock found for symbol: FAKE");
        stockService.addTrade(trade);
    }

    @Test
    public void thatWhenTradeWithInvalidSharesQuantityIsAddedThenAddTradeThrowsException() {
        Trade trade = new Trade("ALE", System.currentTimeMillis(), -1, Trade.Type.BUY, BigDecimal.valueOf(2000));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares quantity for trade cannot be less than or equal to 0.");
        stockService.addTrade(trade);

        trade = new Trade("ALE", System.currentTimeMillis(), 0, Trade.Type.BUY, BigDecimal.valueOf(2000));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares quantity for trade cannot be less than or equal to 0.");
        stockService.addTrade(trade);
    }

    @Test
    public void thatWhenTradeWithInvalidPriceIsAddedThenAddTradeThrowsException() {
        Trade trade = new Trade("ALE", System.currentTimeMillis(), 10, Trade.Type.BUY, BigDecimal.valueOf(-1));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Price for trade cannot be less than or equal to 0.");
        stockService.addTrade(trade);
    }

    @Test
    public void thatWhenTradeWithTimestampInTheFutureIsAddedThenAddTradeThrowsException() {
        Trade trade = new Trade("ALE", System.currentTimeMillis() + 2*60*1000, 10, Trade.Type.BUY, BigDecimal.valueOf(100));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Timestamp for trade cannot be in the future.");
        stockService.addTrade(trade);
    }

    @Test
    public void thatVolumeWeightedStockPriceForPast15MinutesIsCorrectlyComputed() {
        long startTimestamp = System.currentTimeMillis();
        Trade tradeOlderThan15Minutes = new Trade("ALE", startTimestamp - 16*60*1000, 20, Trade.Type.BUY, BigDecimal.valueOf(2000));
        Trade trade14MinutesOld = new Trade("ALE", startTimestamp - 14*60*1000, 10, Trade.Type.BUY, BigDecimal.valueOf(2000));
        Trade trade10MinutesOld = new Trade("ALE", startTimestamp - 10*60*1000, 25, Trade.Type.SELL, BigDecimal.valueOf(1000));
        Trade trade5MinutesOld = new Trade("ALE", startTimestamp - 5*60*1000, 20, Trade.Type.BUY, BigDecimal.valueOf(2500));

        stockService.addTrade(tradeOlderThan15Minutes);
        stockService.addTrade(trade14MinutesOld);
        stockService.addTrade(trade10MinutesOld);
        stockService.addTrade(trade5MinutesOld);

        BigDecimal volumeWeightedStockPrice = stockService.volumeWeightedStockPriceAfter("ALE", startTimestamp - 15*60*1000);
        assertThat(volumeWeightedStockPrice, equalTo(BigDecimal.valueOf(1727.27273d).setScale(PRECISION_DIGITS)));
    }

    @Test
    public void thatWhenInvalidStockSymbolProvidedThenVolumeWeightedStockPriceThrowsException() {
        long startTimestamp = System.currentTimeMillis();
        Trade trade5MinutesOld = new Trade("ALE", startTimestamp - 5*60*1000, 20, Trade.Type.BUY, BigDecimal.valueOf(2500));

        stockService.addTrade(trade5MinutesOld);

        expectedException.expect(StockDoesNotExistException.class);
        expectedException.expectMessage("No stock found for symbol: NOT_A_STOCK_SYMBOL");
        stockService.volumeWeightedStockPriceAfter("NOT_A_STOCK_SYMBOL", startTimestamp - 15*60*1000);
    }

    @Test
    public void thatWhenNullStockSymbolProvidedThenVolumeWeightedStockPriceThrowsException() {
        long startTimestamp = System.currentTimeMillis();
        Trade trade5MinutesOld = new Trade("ALE", startTimestamp - 5*60*1000, 20, Trade.Type.BUY, BigDecimal.valueOf(2500));

        stockService.addTrade(trade5MinutesOld);

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Stock symbol cannot be null.");
        stockService.volumeWeightedStockPriceAfter(null, startTimestamp - 15*60*1000);
    }

    @Test
    public void thatWhenNoTradeAfterGivenTimeThenVolumeWeightedStockPriceThrowsException() {
        expectedException.expect(NoTradeFoundException.class);
        expectedException.expectMessage("No trade has been made after given timestamp.");
        stockService.volumeWeightedStockPriceAfter("ALE", System.currentTimeMillis() - 15*60*1000);
    }

    @Test
    public void thatGBCEAllShareIndexIsCorrectlyComputed() {
        BigDecimal GBCEAllShareIndex = stockService.GBCEAllShareIndex();
        assertThat(GBCEAllShareIndex, equalTo(BigDecimal.valueOf(108.44718d).setScale(PRECISION_DIGITS)));
    }

    @Test
    public void thatWhenNoStockAvailableThenGBCEAllShareIndexThrowsException() {
        stockService = new StockServiceImpl(new InMemoryStockEntityManager(), new InMemoryTradeEntityManager());
        expectedException.expect(NoStockFoundException.class);
        expectedException.expectMessage("No stock registered yet.");
        stockService.GBCEAllShareIndex();
    }

    @Test
    public void thatStockCanBeRegistered() {
        Stock stock = new Stock("NEW_SYMBOL", Stock.Type.COMMON,
                BigDecimal.valueOf(8), null, BigDecimal.valueOf(100));
        stockService.register(stock);

        verify(stockEntityManager).store(stockArgumentCaptor.capture());
        assertThat(stockArgumentCaptor.getValue(), equalTo(stock));
    }

    @Test
    public void thatWhenDuplicateStockIsRegisteredThenRegisterThrowsException() {
        Stock stock = new Stock("NEW_SYMBOL", Stock.Type.COMMON,
                BigDecimal.valueOf(8), null, BigDecimal.valueOf(100));
        stockService.register(stock);

        expectedException.expect(DuplicateStockException.class);
        expectedException.expectMessage("Stock with symbol: NEW_SYMBOL already exists.");
        stockService.register(stock);
    }

}
