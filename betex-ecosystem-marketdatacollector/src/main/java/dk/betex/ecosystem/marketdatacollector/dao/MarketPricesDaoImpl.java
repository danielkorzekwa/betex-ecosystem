package dk.betex.ecosystem.marketdatacollector.dao;

import java.util.Arrays;
import java.util.List;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Options;
import org.jcouchdb.document.BaseDocument;
import org.jcouchdb.document.ViewAndDocumentsResult;
import org.jcouchdb.document.ViewResult;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;

/**
 * Data access object that represents volume of unmatched bets for all prices on all runners in a market at the given
 * point of time.
 * 
 * @author korzekwad
 * 
 */
public class MarketPricesDaoImpl implements MarketPricesDao{

	private final Database database;

	public MarketPricesDaoImpl(Database database) {
		this.database = database;
	}
	
	/**
	 * Add timestamped record of market prices to database.
	 * 
	 */
	public void add(MarketPrices marketPrices) {
		database.createDocument(marketPrices);
	}

	/**
	 * Returns history of market prices for a given market and period of time.
	 * 
	 * @param marketId
	 * @param from
	 *            Epoch time in milliseconds from 01.01.1970
	 * @param to
	 *            Epoch time in milliseconds from 01.01.1970
	 * @param limit
	 *            Maximum number of records to be returned. Useful for pagination.
	 * @return
	 */
	public ViewAndDocumentsResult<BaseDocument,MarketPrices> get(long marketId, long from, long to, int limit) {
		Options options = new Options().startKey(Arrays.asList(marketId, from)).endKey(Arrays.asList(marketId, to));
		options.limit(limit);
		ViewAndDocumentsResult<BaseDocument,MarketPrices> marketPrices = database.queryViewAndDocuments("marketprices/byMarketIdAndTimestamp",
				BaseDocument.class,MarketPrices.class, options, null);
		return marketPrices;
	}

	/**
	 * Returns number of timestamped marketPrices records in the database for the given market
	 * 
	 * @param marketId
	 * @return
	 */
	public long getNumOfRecords(long marketId) {
		Options options = new Options();
		options.group(true);
		options.key(marketId);
		ViewResult<Long> queryView = database.queryView("marketprices/countRecordsByMarketId", Long.class, options, null);

		if (queryView.getRows().size() == 1) {
			return ((Long) queryView.getRows().get(0).getValue()).longValue();
		} else {
			return 0;
		}
	}

	/**
	 * Returns minimum and max dates for history of market prices
	 * 
	 * @param marketId
	 * @return Element 0 - minimum date, element 1 - maximum date. Null is returned if no data for market
	 */
	public List<Long> getTimeRange(long marketId) {
		Options options = new Options();
		options.group(true);
		options.key(marketId);
		ViewResult<List> queryView = database.queryView("marketprices/getTimeRangeByMarketId", List.class, options , null);
		
		if(queryView.getRows().size()==1) {	
		return queryView.getRows().get(0).getValue();
		}
		else {
			return null;
		}
	}
}
