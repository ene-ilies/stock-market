package ro.ilies.bogdan.repository.stock.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import ro.ilies.bogdan.model.Stock;
import ro.ilies.bogdan.repository.exception.AlreadyExistsException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Created by bogdan-ilies on 24.02.2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryStockEntityManagerTestCase {

    @InjectMocks
    private InMemoryStockEntityManager stockEntityManager;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void thatStockCanBeStored() {
        Stock newStock = new Stock("new_symbol",
                Stock.Type.COMMON,
                BigDecimal.valueOf(0d),
                null,
                BigDecimal.valueOf(100d));
        stockEntityManager.store(newStock);
    }

    @Test
    public void thatStockWithSameSymbolCanNotBeStored() {
        Stock newStock = new Stock("new_symbol",
                Stock.Type.COMMON,
                BigDecimal.valueOf(0d),
                null,
                BigDecimal.valueOf(100d));
        stockEntityManager.store(newStock);
        expectedException.expect(AlreadyExistsException.class);
        expectedException.expectMessage("A stock with same symbol already exists.");
        stockEntityManager.store(newStock);
    }

    @Test
    public void thatStockCanBeRetrievedByStockSymbol() {
        Stock newStock = new Stock("new_symbol",
                Stock.Type.COMMON,
                BigDecimal.valueOf(0d),
                null,
                BigDecimal.valueOf(100d));
        stockEntityManager.store(newStock);
        Optional<Stock> retrievedStock = stockEntityManager.retrieve(newStock.getStockSymbol());
        assertThat(retrievedStock.get(), equalTo(newStock));
    }

    @Test
    public void thatNoStockIsRetrievedWhenNonExistingSymbolProvided() {
        Stock newStock = new Stock("new_symbol",
                Stock.Type.COMMON,
                BigDecimal.valueOf(0d),
                null,
                BigDecimal.valueOf(100d));
        stockEntityManager.store(newStock);

        Optional<Stock> retrievedStock = stockEntityManager.retrieve("nonExistingSymbol");
        assertThat(retrievedStock.isPresent(), is(false));
    }

    @Test
    public void thatRetrieveAllReturnsAllStocks() {
        Stock newStock = new Stock("new_symbol",
                Stock.Type.COMMON,
                BigDecimal.valueOf(0d),
                null,
                BigDecimal.valueOf(100d));
        Stock newStock1 = new Stock("new_symbol1",
                Stock.Type.COMMON,
                BigDecimal.valueOf(0d),
                null,
                BigDecimal.valueOf(100d));
        stockEntityManager.store(newStock);
        stockEntityManager.store(newStock1);

        List<Stock> stocks = stockEntityManager.retrieveAll();

        assertThat(stocks, hasSize(2));
        assertThat(stocks, hasItems(newStock, newStock1));
    }

    @Test
    public void thatWhenNoStockIsAddedRetrieveAllReturnsEmptyList() {
        List<Stock> stocks = stockEntityManager.retrieveAll();

        assertThat(stocks, notNullValue());
        assertThat(stocks, empty());
    }
}
