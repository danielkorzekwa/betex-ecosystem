package dk.betex.ecosystem.marketdatacollector.marketservice;

import java.util.List;

/**Provides list of betting exchange markets*/
public interface MarketService {

	/**Returns list of betting exchange market ids.
	 * 
	 * @return
	 */
	List<Long> getMarketIds();
}
