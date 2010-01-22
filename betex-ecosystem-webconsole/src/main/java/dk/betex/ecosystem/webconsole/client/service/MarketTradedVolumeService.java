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
	 * Returns history of data for a given market, {@link MarketFunctionEnum} and  period of time. The range min/max allows to zoom in/out
	 * inside the data and to analyse given range of probabilities in more details.
	 * 
	 * @param marketId
	 * @param marketFunction
	 * @param from
	 *            Get market traded history from the given time.
	 * @param to
	 *            Get market traded history to the given time.
	 * @param limit
	 *            Max number of records to be returned by this method.
	 * @return
	 */
	public List<BioHeatMapModel> getMarketData(int marketId, MarketFunctionEnum marketFunction, long from, long to, int limit,double probMin, double probMax);
	
	/**Returns number of time stamped records in the database for the given market and {@link MarketFunctionEnum}.
	 * 
	 * @param marketId
	 * @param marketFunction
	 * @return
	 */
	public long getNumOfRecords(long marketId,MarketFunctionEnum marketFunction);
	
	/**
	 * Returns minimum and max dates for the given market and {@link MarketFunctionEnum} 
	 * 
	 * @param marketId
	 * @param marketFunction
	 * @return Element 0 - minimum date, element 1 - maximum date. Null is returned if no data for market is available.
	 */
	public List<Long> getTimeRange(long marketId,MarketFunctionEnum marketFunction);
	
	/**Get list of markets ordered by marketTime from the newest to the oldest.
	 * 
	 * @param limit Maximum number of markets to return
	 * @return
	 */
	public List<MarketInfo> getMarketInfos(int limit); 

}
