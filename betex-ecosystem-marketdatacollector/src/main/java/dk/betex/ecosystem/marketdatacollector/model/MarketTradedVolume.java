package dk.betex.ecosystem.marketdatacollector.model;

import java.io.Serializable;
import java.util.List;

import org.jcouchdb.document.BaseDocument;
import org.svenson.JSONTypeHint;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;

/**
 * Represents traded volume at each price on all of the runners in a particular market.
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolume extends BaseDocument implements Serializable {

	private long marketId;
	private List<RunnerTradedVolume> runnerTradedVolume;
	/** Epoch time in milliseconds. */
	private long timestamp;

	public MarketTradedVolume() {
	}

	public MarketTradedVolume(long marketId, List<RunnerTradedVolume> runnerTradedVolume, long timestamp) {
		this.marketId = marketId;
		this.runnerTradedVolume = runnerTradedVolume;
		this.timestamp = timestamp;
	}

	public long getMarketId() {
		return marketId;
	}

	public void setMarketId(long marketId) {
		this.marketId = marketId;
	}

	public List<RunnerTradedVolume> getRunnerTradedVolume() {
		return runnerTradedVolume;
	}

	@JSONTypeHint(RunnerTradedVolume.class)
	public void setRunnerTradedVolume(List<RunnerTradedVolume> runnerTradedVolume) {
		this.runnerTradedVolume = runnerTradedVolume;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**Returns total traded volume on market.*/
	public double getTotalTradedVolume() {
		double totalTradedVolume=0;
		
		for(RunnerTradedVolume tradedVolume: this.runnerTradedVolume) {
			for(PriceTradedVolume priceTradedVolume: tradedVolume.getPriceTradedVolume()) {
				totalTradedVolume+=priceTradedVolume.getTradedVolume();
			}
		}
		
		return totalTradedVolume;
	}
	
	public RunnerTradedVolume getRunnerTradedVolume(long selectionId) {
		for(RunnerTradedVolume volume: runnerTradedVolume) {
			if(volume.getSelectionId()==selectionId) return volume;
		}
		return null;
	}

	@Override
	public String toString() {
		return "MarketTradedVolume [marketId=" + marketId + ", runnerTradedVolume=" + runnerTradedVolume
				+ ", timestamp=" + timestamp + "]";
	}
}
