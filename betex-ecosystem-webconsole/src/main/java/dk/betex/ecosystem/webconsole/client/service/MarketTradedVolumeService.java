package dk.betex.ecosystem.webconsole.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import dk.betex.ecosystem.webconsole.client.model.HeatMapModel;

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
	public HeatMapModel getMarketTradedVolume(int marketId);

	/**
	 * Returns history of traded volume for a given market.
	 * 
	 * @param marketId
	 * @return
	 */
	public List<HeatMapModel> getMarketTradedVolumeHistory(int marketId);

}
