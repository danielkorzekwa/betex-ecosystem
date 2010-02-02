package dk.betex.ecosystem.marketdatacollector.marketservice;

import java.util.Arrays;
import java.util.List;

public class OneMarketServiceImpl implements MarketService{

	private final long marketId;
	public OneMarketServiceImpl(long marketId) {
		this.marketId = marketId;
	}
	@Override
	public List<Long> getMarketIds() {
		return Arrays.asList(marketId);
	}

}
