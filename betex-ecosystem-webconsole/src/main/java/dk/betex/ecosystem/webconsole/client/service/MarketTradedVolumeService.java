package dk.betex.ecosystem.webconsole.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;

/**
 * Returns traded volume at each price on all of the runners in a particular market.
 * 
 * A few methods to get traded volume from betfair exchange as well as from database are available.
 * 
 * @author korzekwad
 * 
 */
@RemoteServiceRelativePath("MarketTradedVolume")
public interface MarketTradedVolumeService extends RemoteService {

	/**
	 * Returns traded volume for all runners in a particular market grouped by prices representing 100 probabilities
	 * (0..1).
	 * 
	 * @param marketId
	 * @return
	 */
	public BioHeatMapModel getMarketTradedVolume(int marketId);

	/**
	 * Returns history of traded volume for a given market and period of time.
	 * 
	 * @param marketId
	 * @param from Get market traded history from the given time.
	 * @param to Get market traded history to the given time.
	 * @param limit Max number of records to be returned by this method.
	 * @return
	 */
	public List<BioHeatMapModel> getMarketTradedVolumeHistory(int marketId, long from, long to, int limit);
	
	/**Returns number of time stamped traded volume records in the database for the given market.
	 * 
	 * @param marketId
	 * @return
	 */
	public long getNumOfRecords(long marketId);
	
	/**
	 * Returns minimum and max dates for history of market traded volume.
	 * 
	 * @param marketId
	 * @return Element 0 - minimum date, element 1 - maximum date. Null is returned if no data for market is available.
	 */
	public List<Long> getTimeRange(long marketId);

}
