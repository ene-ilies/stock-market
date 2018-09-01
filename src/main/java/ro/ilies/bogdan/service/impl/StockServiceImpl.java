package ro.ilies.bogdan.service.impl;

import ch.obermuhlner.math.big.BigDecimalMath;
import ro.ilies.bogdan.model.Stock;
import ro.ilies.bogdan.model.Trade;
import ro.ilies.bogdan.repository.exception.AlreadyExistsException;
import ro.ilies.bogdan.repository.stock.StockEntityManager;
import ro.ilies.bogdan.repository.trade.TradeEntityManager;
import ro.ilies.bogdan.service.StockService;
import ro.ilies.bogdan.service.exception.DuplicateStockException;
import ro.ilies.bogdan.service.exception.NoStockFoundException;
import ro.ilies.bogdan.service.exception.NoTradeFoundException;
import ro.ilies.bogdan.service.exception.StockDoesNotExistException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
public class StockServiceImpl implements StockService {
    private static final int PRECISION_DIGITS = 5;

    private final StockEntityManager stockEntityManager;
    private final TradeEntityManager tradeEntityManager;

    public StockServiceImpl(StockEntityManager stockEntityManager, TradeEntityManager tradeEntityManager) {
        this.stockEntityManager = stockEntityManager;
        this.tradeEntityManager = tradeEntityManager;
    }

    @Override
    public BigDecimal dividendYield(String stockSymbol, BigDecimal marketPrice) {
        checkMarketPricePreconditions(marketPrice);
        Stock stockForGivenSymbol = retrieveStockOrThrowException(stockSymbol);
        BigDecimal dividendYield;
        if (stockForGivenSymbol.getType() == Stock.Type.COMMON) {
            dividendYield = stockForGivenSymbol.getLastDividend()
                    .divide(marketPrice, PRECISION_DIGITS, BigDecimal.ROUND_UP);
        } else {
            dividendYield = stockForGivenSymbol.getFixedDividendInPercent()
                    .multiply(stockForGivenSymbol.getParValue())
                    .divide(marketPrice, PRECISION_DIGITS, BigDecimal.ROUND_UP);
        }
        return dividendYield;
    }

    private Stock retrieveStockOrThrowException(String stockSymbol) {
        Objects.requireNonNull(stockSymbol, "Stock symbol cannot be null.");
        return stockEntityManager.retrieve(stockSymbol)
                .orElseThrow(() -> new StockDoesNotExistException("No stock found for symbol: " + stockSymbol));
    }

    @Override
    public BigDecimal PERatio(String stockSymbol, BigDecimal marketPrice) {
        checkMarketPricePreconditions(marketPrice);
        Stock stockForGivenSymbol = retrieveStockOrThrowException(stockSymbol);
        return marketPrice.divide(stockForGivenSymbol.getLastDividend(), PRECISION_DIGITS, BigDecimal.ROUND_UP);
    }

    @Override
    public void addTrade(Trade trade) {
        checkTradePreconditions(trade);
        tradeEntityManager.store(trade);
    }

    @Override
    public BigDecimal volumeWeightedStockPriceAfter(String stockSymbol, long timestamp) {
        retrieveStockOrThrowException(stockSymbol);
        List<Trade> tradesAfterGivenTime = tradeEntityManager.retrieveAfter(stockSymbol, timestamp);
        if (tradesAfterGivenTime.isEmpty()) {
            throw new NoTradeFoundException("No trade has been made after given timestamp.");
        }
        BigDecimal numerator = tradesAfterGivenTime.stream()
                .map(trade -> trade.getPrice()
                        .multiply(BigDecimal.valueOf(trade.getSharesQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int denominator = tradesAfterGivenTime.stream()
                .map(Trade::getSharesQuantity)
                .reduce(0, (x, y) -> x + y);
        return numerator.divide(BigDecimal.valueOf(denominator), PRECISION_DIGITS, BigDecimal.ROUND_UP);
    }

    @Override
    public BigDecimal GBCEAllShareIndex() {
        List<Stock> allStocks = stockEntityManager.retrieveAll();
        if (allStocks.isEmpty()) {
            throw new NoStockFoundException("No stock registered yet.");
        }
        BigDecimal products = BigDecimal.ONE;
        products = allStocks.stream()
                .map(Stock::getParValue)
                .reduce(BigDecimal.ONE, BigDecimal::multiply);
        return BigDecimalMath.pow(products,
                BigDecimal.valueOf(1d/allStocks.size()),
                new MathContext(10, RoundingMode.UP)).setScale(PRECISION_DIGITS, BigDecimal.ROUND_UP);
    }

    @Override
    public void register(Stock stock) {
        try {
            stockEntityManager.store(stock);
        } catch (AlreadyExistsException e) {
            throw new DuplicateStockException("Stock with symbol: " + stock.getStockSymbol() + " already exists.");
        }
    }

    private void checkTradePreconditions(Trade trade) {
        retrieveStockOrThrowException(trade.getStockSymbol());
        if (trade.getSharesQuantity() <= 0) {
            throw new IllegalArgumentException("Shares quantity for trade cannot be less than or equal to 0.");
        }
        if (trade.getType() == null) {
            throw new IllegalArgumentException("Type for trade cannot null.");
        }
        if (BigDecimal.ONE.compareTo(trade.getPrice()) == 1) {
            throw new IllegalArgumentException("Price for trade cannot be less than or equal to 0.");
        }
        if (System.currentTimeMillis() < trade.getTimestamp()) {
            throw new IllegalArgumentException("Timestamp for trade cannot be in the future.");
        }
    }

    private void checkMarketPricePreconditions(BigDecimal marketPrice) {
        Objects.requireNonNull(marketPrice, "Market price cannot be null.");
        if (BigDecimal.ONE.compareTo(marketPrice) == 1) {
            throw new IllegalArgumentException("Market price cannot be less than or equal to 0.");
        }
    }
}
