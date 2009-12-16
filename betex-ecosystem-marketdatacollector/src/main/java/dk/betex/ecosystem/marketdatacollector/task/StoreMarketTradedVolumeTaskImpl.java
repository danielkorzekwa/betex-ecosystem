package dk.betex.ecosystem.marketdatacollector.task;


/** Get market traded volume from Betfair betting exchange and store it in a database.
 * 
 * @author korzekwad
 *
 */
public class StoreMarketTradedVolumeTaskImpl implements StoreMarketTradedVolumeTask{

	public StoreMarketTradedVolumeTaskImpl() {
	}
	
	/**Get market traded volume from Betfair betting exchange and store it in a database
	 * 
	 * @param marketId The market that the market traded volume is stored in a database.
	 */
	@Override
	public void execute(long marketId) {
		// TODO Auto-generated method stub
		
	}

}
