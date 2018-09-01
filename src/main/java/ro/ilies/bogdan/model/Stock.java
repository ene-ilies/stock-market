package ro.ilies.bogdan.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Created by bogdan-ilies on 24.02.2018.
 */
public class Stock {

    private final String stockSymbol;
    private final Type type;
    private final BigDecimal lastDividend;
    private final BigDecimal parValue;
    private final BigDecimal fixedDividendInPercent;

    public Stock(String stockSymbol, Type type, BigDecimal lastDividend, BigDecimal fixedDividendInPercent, BigDecimal parValue) {
        Objects.requireNonNull(stockSymbol, "Stock's symbol cannot be null.");
        Objects.requireNonNull(type, "Stock's type cannot be null.");
        Objects.requireNonNull(lastDividend, "Stock's lastDividend cannot be null.");
        Objects.requireNonNull(parValue, "Stock's parValue cannot be null.");
        if (type == Type.PREFERRED) {
            Objects.requireNonNull(fixedDividendInPercent, "Stock's fixedDividendInPercent cannot be null for Preferred stock.");
        }
        this.stockSymbol = stockSymbol;
        this.type = type;
        this.lastDividend = lastDividend;
        this.fixedDividendInPercent = fixedDividendInPercent;
        this.parValue = parValue;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getLastDividend() {
        return lastDividend;
    }

    public BigDecimal getParValue() {
        return parValue;
    }

    public BigDecimal getFixedDividendInPercent() {
        return fixedDividendInPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stock stock = (Stock) o;

        if (!stockSymbol.equals(stock.stockSymbol)) return false;
        if (type != stock.type) return false;
        if (lastDividend != null ? !lastDividend.equals(stock.lastDividend) : stock.lastDividend != null) return false;
        if (parValue != null ? !parValue.equals(stock.parValue) : stock.parValue != null) return false;
        return fixedDividendInPercent != null ? fixedDividendInPercent.equals(stock.fixedDividendInPercent) : stock.fixedDividendInPercent == null;
    }

    @Override
    public int hashCode() {
        int result = stockSymbol.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (lastDividend != null ? lastDividend.hashCode() : 0);
        result = 31 * result + (parValue != null ? parValue.hashCode() : 0);
        result = 31 * result + (fixedDividendInPercent != null ? fixedDividendInPercent.hashCode() : 0);
        return result;
    }

    public enum Type {
        COMMON,
        PREFERRED;
    }
}
