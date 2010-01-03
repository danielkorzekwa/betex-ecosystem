package dk.betex.ecosystem.webconsole.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;

/**
 * Returns traded volume for all runners in a particular market grouped by probability (0..1).
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
	 * Returns history of traded volume for a given market.
	 * 
	 * @param marketId
	 * @param limit Max number of records to be returned by this method.
	 * @return
	 */
	public List<BioHeatMapModel> getMarketTradedVolumeHistory(int marketId,int limit);
	
	/**Returns number of timestamped traded volume records in the database for the given market
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
