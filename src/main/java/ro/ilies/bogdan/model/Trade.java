package ro.ilies.bogdan.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by bogdan-ilies on 25.02.2018.
 */
public class Trade {
    private final String stockSymbol;
    private final long timestamp;
    private final int sharesQuantity;
    private final BigDecimal price;
    private final Type type;

    public Trade(String stockSymbol, long timestamp, int sharesQuantity, Type type, BigDecimal price) {
        Objects.requireNonNull(stockSymbol, "Stock symbol cannot be null.");
        Objects.requireNonNull(type, "Trade's type cannot be null.");
        Objects.requireNonNull(price, "Trade's price cannot be null.");
        this.stockSymbol = stockSymbol;
        this.timestamp = timestamp;
        this.sharesQuantity = sharesQuantity;
        this.type = type;
        this.price = price;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSharesQuantity() {
        return sharesQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        if (timestamp != trade.timestamp) return false;
        if (sharesQuantity != trade.sharesQuantity) return false;
        if (!price.equals(trade.price)) return false;
        return type == trade.type;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + sharesQuantity;
        result = 31 * result + price.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public enum Type {
        BUY,
        SELL;
    }
}
