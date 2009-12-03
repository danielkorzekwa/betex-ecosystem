package dk.betex.ecosystem.webconsole.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import dk.betex.ecosystem.webconsole.client.model.MarketTradedVolume;

/** Returns traded volume at each price on all of the runners in a particular market.
 * 
 * @author korzekwad
 *
 */

@RemoteServiceRelativePath( "MarketTradedVolume" )
public interface MarketTradedVolumeService extends RemoteService{

	/**Returns traded volume at each price on all of the runners in a particular market
	 * Prices with 0 traded volume are not returned.
	 * @param marketId
	 * @return
	 */
	public MarketTradedVolume getMarketTradedVolume(int marketId);
	
	/**Returns traded volume at each price on all of the runners in a particular market
	 * Prices with 0 traded volume are also returned.
	 * @param marketId
	 * @return
	 */
	public MarketTradedVolume getMarketTradedVolumeForAllPrices(int marketId);
	
	
}
