package dk.betex.ecosystem.marketdatacollector.task;

import java.util.List;

/** Get market traded volume from Betfair betting exchange and store it in a database.
 *  Get market details and store it in a database.
 * 
 * @author korzekwad
 *
 */
public interface StoreMarketTradedVolumeTask {

	/**Get market traded volume from Betfair betting exchange and store it in a database.
	 * Get market details and store it in a database.
	 * 
	 * @param marketIds List of markets that the market data is retrieved for and stored in a database.
	 */
	public void execute(List<Long> marketIds);
	
}
