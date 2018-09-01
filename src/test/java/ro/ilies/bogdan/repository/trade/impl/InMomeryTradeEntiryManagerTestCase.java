package ro.ilies.bogdan.repository.trade.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import ro.ilies.bogdan.model.Trade;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by bogdan-ilies on 24.02.2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class InMomeryTradeEntiryManagerTestCase {

    @InjectMocks
    private InMemoryTradeEntityManager tradeEntityManager;

    @Test
    public void thatTradesCanBeStored() {
        Trade newTrade = new Trade("STOCK1", System.currentTimeMillis(),100, Trade.Type.BUY, BigDecimal.valueOf(1000));
        tradeEntityManager.store(newTrade);
    }

    @Test
    public void thatTradesAfterSpecificTimestampCanBeRetrieved() {
        long startTimeStamp = System.currentTimeMillis();
        // 3 minutes ago
        Trade newTrade = new Trade("STOCK1", startTimeStamp - 3*60*1000,100, Trade.Type.BUY, BigDecimal.valueOf(1000));
        // 2 minutes ago
        Trade newTrade1 = new Trade("STOCK1", startTimeStamp - 2*60*1000,100, Trade.Type.SELL, BigDecimal.valueOf(1000));
        // 1 minute ago
        Trade newTrade2 = new Trade("STOCK1", startTimeStamp - 1*60*1000,100, Trade.Type.BUY, BigDecimal.valueOf(1000));

        tradeEntityManager.store(newTrade);
        tradeEntityManager.store(newTrade1);
        tradeEntityManager.store(newTrade2);

        List<Trade> tradesInPastTwoMinutes = tradeEntityManager.retrieveAfter("STOCK1", startTimeStamp - 2*60*1000);

        assertThat(tradesInPastTwoMinutes, notNullValue());
        assertThat(tradesInPastTwoMinutes, hasSize(2));
        assertThat(tradesInPastTwoMinutes, hasItems(newTrade1, newTrade2));
    }

    @Test
    public void thatWhenNoTradeExistsAfterSpecificTimestampThenRetrieveAfterReturnsEmptyList() {
        long startTimeStamp = System.currentTimeMillis();
        // 3 minutes ago
        Trade newTrade = new Trade("STOCK1", startTimeStamp - 3*60*1000,100, Trade.Type.BUY, BigDecimal.valueOf(1000));
        List<Trade> tradesInPastTwoMinutes = tradeEntityManager.retrieveAfter("STOCK1", startTimeStamp - 2*60*1000);

        assertThat(tradesInPastTwoMinutes, notNullValue());
        assertThat(tradesInPastTwoMinutes, empty());
    }

    @Test
    public void thatWhenNoTradeExistsThenRetrieveAfterRetrunsEmptyList() {
        List<Trade> tradesInPastTwoMinutes = tradeEntityManager.retrieveAfter("STOCK1", System.currentTimeMillis() - 2*60*1000);

        assertThat(tradesInPastTwoMinutes, notNullValue());
        assertThat(tradesInPastTwoMinutes, empty());
    }

    @Test
    public void thatWhenNoTradesExistsForSpecificSymbolButForOtherThenRetrieveAfterReturnsEmptyList() {
        long startTimeStamp = System.currentTimeMillis();
        // 3 minutes ago
        Trade newTrade = new Trade("STOCK1", startTimeStamp - 3*60*1000,100, Trade.Type.BUY, BigDecimal.valueOf(1000));
        List<Trade> tradesInPastTwoMinutes = tradeEntityManager.retrieveAfter("STOCK2", startTimeStamp - 2*60*1000);

        assertThat(tradesInPastTwoMinutes, notNullValue());
        assertThat(tradesInPastTwoMinutes, empty());
    }
}
