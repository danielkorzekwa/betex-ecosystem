package dk.betex.ecosystem.marketdatacollector.dao;

import java.util.List;

import org.jcouchdb.document.ViewResult;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;

/**
 * Data access object for a market traded volume on a betting exchange.
 * 
 * @author korzekwad
 * 
 */
public interface MarketTradedVolumeDao {

	/**
	 * Add timestamped record of market traded volume to database.
	 * 
	 */
	public void addMarketTradedVolume(MarketTradedVolume marketTradedVolume);

	/**
	 * Returns history of market traded volume for a given market and period of time.
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
	public ViewResult<MarketTradedVolume> getMarketTradedVolume(long marketId, long from, long to, int limit);

	/**
	 * Returns number of timestamped traded volume records in the database for the given market
	 * 
	 * @param marketId
	 * @return
	 */
	public long getNumOfRecords(long marketId);

	/**
	 * Returns minimum and max dates for history of market traded volume
	 * 
	 * @param marketId
	 * @return Element 0 - minimum date, element 1 - maximum date. Null is returned if no data for market
	 */
	public List<Long> getTimeRange(long marketId);
}
