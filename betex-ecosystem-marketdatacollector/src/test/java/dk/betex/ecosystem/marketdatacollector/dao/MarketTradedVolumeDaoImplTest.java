package dk.betex.ecosystem.marketdatacollector.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jcouchdb.db.Database;
import org.jcouchdb.document.ViewResult;
import org.junit.Before;
import org.junit.Test;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;

public class MarketTradedVolumeDaoImplTest {

	private MarketTradedVolumeDao marketTradedVolueDao;
	private Database database;

	@Before
	public void setUp() {
		database = new Database("10.2.2.72", "market_traded_volume_test");
		 marketTradedVolueDao = new MarketTradedVolumeDaoImpl(database);	 
	}
	
	
	@Test
	public void testAddGetMarketTradedVolume() {
		
		/**Add market traded volume to the couch db.*/
		MarketTradedVolume marketTradedVolume = createMarketTradedVolume();
		marketTradedVolueDao.addMarketTradedVolume(marketTradedVolume);
		
		/**Get market traded volume from the couch db and check if it's correct.*/
		ViewResult<MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao.getMarketTradedVolume(marketTradedVolume
				.getMarketId(), marketTradedVolume.getTimestamp(), marketTradedVolume.getTimestamp());
		
		assertEquals(1, marketTradedVolumeList.getRows().size());
		
		assertEquals(marketTradedVolume.getMarketId(), marketTradedVolumeList.getRows().get(0).getValue().getMarketId());
		assertEquals(marketTradedVolume.getTimestamp(), marketTradedVolumeList.getRows().get(0).getValue().getTimestamp());

		assertEquals(marketTradedVolume.getRunnerTradedVolume().size(), marketTradedVolumeList.getRows().get(0).getValue()
				.getRunnerTradedVolume().size());

		for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {
			RunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);
			RunnerTradedVolume runnerTradedVolumeDB = marketTradedVolumeList.getRows().get(0).getValue().getRunnerTradedVolume().get(
					runnerIndex);

			assertEquals(runnerTradedVolume.getSelectionId(), runnerTradedVolumeDB.getSelectionId());
			assertEquals(runnerTradedVolume.getPriceTradedVolume().size(), runnerTradedVolumeDB.getPriceTradedVolume()
					.size());

			for (int priceIndex = 0; priceIndex < runnerTradedVolume.getPriceTradedVolume().size(); priceIndex++) {
				PriceTradedVolume priceTradedVolume = runnerTradedVolume.getPriceTradedVolume().get(priceIndex);
				PriceTradedVolume priceTradedVolumeDB = runnerTradedVolumeDB.getPriceTradedVolume().get(priceIndex);

				assertEquals(priceTradedVolume.getPrice(), priceTradedVolumeDB.getPrice(), 0);
				assertEquals(priceTradedVolume.getTradedVolume(), priceTradedVolumeDB.getTradedVolume(), 0);
			}
		}

	}
	
	private MarketTradedVolume createMarketTradedVolume() {
		List<RunnerTradedVolume> runnersTradedVolume = new ArrayList<RunnerTradedVolume>();

		List<PriceTradedVolume> pricesTradedVolume = new ArrayList<PriceTradedVolume>();
		pricesTradedVolume.add(new PriceTradedVolume(2.1, 35.32));
		pricesTradedVolume.add(new PriceTradedVolume(2.2, 765.56));
		runnersTradedVolume.add(new RunnerTradedVolume(105, pricesTradedVolume));

		pricesTradedVolume = new ArrayList<PriceTradedVolume>();
		pricesTradedVolume.add(new PriceTradedVolume(3.4, 43.24));
		pricesTradedVolume.add(new PriceTradedVolume(3.6, 65.12));
		runnersTradedVolume.add(new RunnerTradedVolume(106, pricesTradedVolume));

		MarketTradedVolume marketTradedVolume = new MarketTradedVolume((int) (System.currentTimeMillis() / 1000),
				runnersTradedVolume,System.currentTimeMillis());

		return marketTradedVolume;
	}
}
