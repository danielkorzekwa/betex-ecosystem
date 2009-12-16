package dk.betex.ecosystem.marketdatacollector.task;

/** Get market traded volume from Betfair betting exchange and store it in a database.
 * 
 * @author korzekwad
 *
 */
public interface StoreMarketTradedVolumeTask {

	/**Get market traded volume from Betfair betting exchange and store it in a database
	 * 
	 * @param marketId The market that the market traded volume is stored in a database.
	 */
	public void execute(long marketId);
	
}
