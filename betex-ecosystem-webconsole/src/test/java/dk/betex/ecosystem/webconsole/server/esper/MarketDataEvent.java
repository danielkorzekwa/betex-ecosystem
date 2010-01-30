package dk.betex.ecosystem.webconsole.server.esper;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;

public class MarketDataEvent {
	
	private final MarketTradedVolume marketTradedVolume;
	private final MarketPrices marketPrices;

	public MarketDataEvent(MarketTradedVolume marketTradedVolume, MarketPrices marketPrices) {
		this.marketTradedVolume = marketTradedVolume;
		this.marketPrices = marketPrices;
	}

	public MarketTradedVolume getMarketTradedVolume() {
		return marketTradedVolume;
	}

	public MarketPrices getMarketPrices() {
		return marketPrices;
	}
	
	
}