package dk.betex.ecosystem.webconsole.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.util.MathUtils;
import org.jcouchdb.db.Database;
import org.jcouchdb.document.ValueRow;
import org.jcouchdb.document.ViewResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import dk.betex.ecosystem.marketdatacollector.dao.MarketDetailsDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketDetailsDaoImpl;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDaoImpl;
import dk.betex.ecosystem.marketdatacollector.factory.MarketTradedVolumeFactory;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.service.MarketInfo;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.DefaultBetFairServiceFactoryBean;
import dk.bot.betfairservice.model.BFMarketDetails;
import dk.bot.betfairservice.model.BFMarketTradedVolume;

/**
 * Returns traded volume at each price on all of the runners in a particular market.
 * 
 * A few methods to get traded volume from betfair exchange as well as from database are available.
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolumeServiceImpl extends RemoteServiceServlet implements MarketTradedVolumeService {

	private BetFairService betfairService;
	private MarketTradedVolumeDao marketTradedVolueDao;
	private MarketDetailsDao marketDetailsDao;

	@Override
	public void init() throws ServletException {

		/** Init betfair service */
		DefaultBetFairServiceFactoryBean betfairServiceFactoryBean = new DefaultBetFairServiceFactoryBean();
		String user = System.getenv("dk.betex.ecosystem.webconsole.bfUser");
		if (user == null)
			throw new IllegalStateException("System property is not set: dk.betex.ecosystem.webconsole.bfUser");
		String pass = System.getenv("dk.betex.ecosystem.webconsole.bfPass");
		if (pass == null)
			throw new IllegalStateException("System property is not set: dk.betex.ecosystem.webconsole.bfPass");
		betfairServiceFactoryBean.setUser(user);
		betfairServiceFactoryBean.setPassword(pass);
		betfairServiceFactoryBean.setProductId(82);
		betfairServiceFactoryBean.login();
		try {
			betfairService = (BetFairService) betfairServiceFactoryBean.getObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		/** Init DAOs */
		marketTradedVolueDao = new MarketTradedVolumeDaoImpl(new Database("10.2.2.72", "market_traded_volume"));
		marketDetailsDao = new MarketDetailsDaoImpl(new Database("10.2.2.72", "market_details"));
	}

	@Override
	public BioHeatMapModel getMarketTradedVolume(int marketId) {
		try {
			BFMarketTradedVolume bfMarketTradedVolume = betfairService.getMarketTradedVolume(marketId);
			BFMarketDetails marketDetails = betfairService.getMarketDetails(marketId);

			MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume, new Date(
					System.currentTimeMillis()));

			BioHeatMapModel marketHeatMap = HeatMapModelFactory.createHeatMap(marketTradedVolume, 0, 100);

			/** Replace selectionId with selectionName */
			for (int i = 0; i < marketHeatMap.getxAxisLabels().length; i++) {
				int selectionId = Integer.parseInt(marketHeatMap.getxAxisLabels()[i]);
				String selectionName = marketDetails.getSelectionName(selectionId);
				marketHeatMap.getxAxisLabels()[i] = selectionName;
			}

			/** Change probabilities to prices */
			for (int i = 0; i < marketHeatMap.getyAxisLabels().length; i++) {
				double price = Double.parseDouble(marketHeatMap.getyAxisLabels()[i]) / 100d;
				marketHeatMap.getyAxisLabels()[i] = "" + MathUtils.round(1d / price, 2);
			}

			return marketHeatMap;
		} catch (Exception e) {
			LogFactory.getLog(this.getClass()).error("Can't get market traded volume for market: " + marketId, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns history of traded volume for a given market and period of time. The range min/max allows to zoom in/out
	 * inside the market traded volume and to analyse given range of probabilities in more details.
	 * 
	 * @param marketId
	 * @param from
	 *            Get market traded history from the given time.
	 * @param to
	 *            Get market traded history to the given time.
	 * @param limit
	 *            Max number of records to be returned by this method.
	 * @return
	 */
	public List<BioHeatMapModel> getMarketTradedVolumeHistory(int marketId, long from, long to, int limit,
			double probMin, double probMax) {

		ViewResult<MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao.getMarketTradedVolume(marketId,
				from, to, limit);
		ArrayList<BioHeatMapModel> heatMapList = new ArrayList<BioHeatMapModel>();

		for (ValueRow<MarketTradedVolume> valueRow : marketTradedVolumeList.getRows()) {
			MarketTradedVolume marketTradedVolume = valueRow.getValue();

			BioHeatMapModel marketHeatMap = HeatMapModelFactory.createHeatMap(marketTradedVolume, probMin, probMax);

			/** Change probabilities to prices */
			for (int i = 0; i < marketHeatMap.getyAxisLabels().length; i++) {
				double prob = Double.parseDouble(marketHeatMap.getyAxisLabels()[i]);
				marketHeatMap.getyAxisLabels()[i] = "" + MathUtils.round(1d / prob, 2);
			}

			heatMapList.add(marketHeatMap);
		}

		return heatMapList;
	}

	@Override
	public long getNumOfRecords(long marketId) {
		return marketTradedVolueDao.getNumOfRecords(marketId);
	}

	@Override
	public List<Long> getTimeRange(long marketId) {
		return marketTradedVolueDao.getTimeRange(marketId);
	}

	/**
	 * Get list of markets ordered by marketTime from the newest to the oldest.
	 * 
	 * @param limit
	 *            Maximum number of markets to return
	 * @return
	 */
	public List<MarketInfo> getMarketInfos(int limit) {
		ViewResult<MarketDetails> marketDetailsList = marketDetailsDao.getMarketDetailsList(limit);

		/** List if menu paths for markets */
		List<MarketInfo> marketInfos = new ArrayList<MarketInfo>();
		for (ValueRow<MarketDetails> row : marketDetailsList.getRows()) {
			MarketInfo marketInfo = new MarketInfo();
			marketInfo.setMarketId(row.getValue().getMarketId());
			marketInfo.setMenuPath(row.getValue().getMenuPath());
			marketInfo.setMarketTime(new Date(row.getValue().getMarketTime()));
			marketInfos.add(marketInfo);
		}
		return marketInfos;
	}
}
