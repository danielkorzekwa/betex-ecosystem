package dk.betex.ecosystem.marketdatacollector.dao;

import org.jcouchdb.document.ViewResult;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;

/** Data access object for a market traded volume on a betting exchange.
 * 
 * @author korzekwad
 *
 */
public interface MarketTradedVolumeDao {

	/**Add timestamped record of market traded volume to database.
	 * 
	 */
	public void addMarketTradedVolume(MarketTradedVolume marketTradedVolume);
	
	/**Returns history of market traded volume for a given market and period of time.
	 * 
	 * @param marketId
	 * @param from Epoch time in milliseconds from 01.01.1970
	 * @param to Epoch time in milliseconds from 01.01.1970
	 * @return
	 */
	public ViewResult<MarketTradedVolume> getMarketTradedVolume(long marketId, long from, long to);
}
