package dk.betex.ecosystem.marketdatacollector.dao;

import org.jcouchdb.document.ViewResult;

import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;

/**
 * Data access object for market details: market name, selection names, market time.
 * 
 * @author korzekwad
 * 
 */
public interface MarketDetailsDao {

	void addMarketDetails(MarketDetails marketDetails);

	MarketDetails getMarketDetails(long marketId);

	/**
	 * Get market details for markets ordered by market time from newest to oldest.
	 * 
	 * @param limit
	 *            Maximum number of markets to returned.
	 */
	ViewResult<MarketDetails> getMarketDetails(int limit);
}
