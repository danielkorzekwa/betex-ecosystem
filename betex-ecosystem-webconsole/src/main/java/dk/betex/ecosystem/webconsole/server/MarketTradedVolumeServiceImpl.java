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
import dk.betex.ecosystem.marketdatacollector.dao.MarketPricesDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketPricesDaoImpl;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDaoImpl;
import dk.betex.ecosystem.marketdatacollector.factory.MarketTradedVolumeFactory;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.webconsole.client.service.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.service.MarketFunctionEnum;
import dk.betex.ecosystem.webconsole.client.service.MarketInfo;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.betex.ecosystem.webconsole.client.service.BioHeatMapModel.HeatMapColumn;
import dk.betex.ecosystem.webconsole.client.service.BioHeatMapModel.HeatMapValue;
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
	private MarketPricesDao marketPricesDao;

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
		marketPricesDao = new MarketPricesDaoImpl(new Database("10.2.2.72", "market_prices"));
	}

	@Override
	public BioHeatMapModel getMarketTradedVolume(int marketId) {
		try {
			BFMarketTradedVolume bfMarketTradedVolume = betfairService.getMarketTradedVolume(marketId);
			BFMarketDetails marketDetails = betfairService.getMarketDetails(marketId);

			MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume, new Date(
					System.currentTimeMillis()));

			BioHeatMapModel marketHeatMap = HeatMapModelDataSourceFactory.create(marketTradedVolume);
			BioHeatMapModel ds = HeatMapModelFactory.createHeatMap(marketHeatMap, 0, 1);

			/** Replace selectionId with selectionName */
			for(HeatMapColumn column: ds.getColumns()) {
				int selectionId = Integer.parseInt(column.getLabel());
				String selectionName = marketDetails.getSelectionName(selectionId);
				column.setLabel(selectionName);
			}
			
			/** Change probabilities to prices */
			for (HeatMapColumn column : ds.getColumns()) {
				for (HeatMapValue value : column.getValues()) {
					value.setRowValue(MathUtils.round(1d / value.getRowValue(), 2));
				}
			}

			return ds;
		} catch (Exception e) {
			LogFactory.getLog(this.getClass()).error("Can't get market traded volume for market: " + marketId, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns history of data for a given market, {@link MarketFunctionEnum} and period of time. The range min/max
	 * allows to zoom in/out inside the data and to analyse given range of probabilities in more details.
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
	public List<BioHeatMapModel> getMarketData(int marketId, MarketFunctionEnum marketFunction, long from,
			long to, int limit, double probMin, double probMax) {

		ArrayList<BioHeatMapModel> heatMapList = new ArrayList<BioHeatMapModel>();

		if (marketFunction == MarketFunctionEnum.MARKET_TRADED_VOLUME) {
			ViewResult<MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao.getMarketTradedVolume(
					marketId, from, to, limit);
			for (ValueRow<MarketTradedVolume> valueRow : marketTradedVolumeList.getRows()) {
				MarketTradedVolume marketTradedVolume = valueRow.getValue();

				BioHeatMapModel ds = HeatMapModelDataSourceFactory.create(marketTradedVolume);
				BioHeatMapModel marketHeatMap = HeatMapModelFactory.createHeatMap(ds, probMin, probMax);
				heatMapList.add(marketHeatMap);
			}

		} else if (marketFunction == MarketFunctionEnum.LAST_MATCHED_PRICE) {
			ViewResult<MarketPrices> marketPricesList = marketPricesDao.get(marketId, from, to, limit);
			for (ValueRow<MarketPrices> valueRow : marketPricesList.getRows()) {
				MarketPrices marketPrices = valueRow.getValue();
				BioHeatMapModel ds = HeatMapModelDataSourceFactory.create(marketPrices);
				BioHeatMapModel marketHeatMap = HeatMapModelFactory.createHeatMap(ds, probMin, probMax);
				heatMapList.add(marketHeatMap);
			}

		} else {
			throw new IllegalArgumentException("Market function is not supported: " + marketFunction);
		}

		/** Change probabilities to prices. */
		for (BioHeatMapModel marketHeatMap : heatMapList) {
			for (HeatMapColumn column : marketHeatMap.getColumns()) {
				for (HeatMapValue value : column.getValues()) {
					value.setRowValue(MathUtils.round(1d / value.getRowValue(), 2));
				}
			}
		}

		return heatMapList;

	}

	/**
	 * Returns number of time stamped records in the database for the given market and {@link MarketFunctionEnum}.
	 * 
	 * @param marketId
	 * @param marketFunction
	 * @return
	 */
	@Override
	public long getNumOfRecords(long marketId, MarketFunctionEnum marketFunction) {
		if (marketFunction == MarketFunctionEnum.MARKET_TRADED_VOLUME) {
			return marketTradedVolueDao.getNumOfRecords(marketId);
		} else if (marketFunction == MarketFunctionEnum.LAST_MATCHED_PRICE) {
			return marketPricesDao.getNumOfRecords(marketId);
		} else {
			throw new IllegalArgumentException("Market function is not supported: " + marketFunction);
		}
	}

	/**
	 * Returns minimum and max dates for the given market and {@link MarketFunctionEnum}
	 * 
	 * @param marketId
	 * @param marketFunction
	 * @return Element 0 - minimum date, element 1 - maximum date. Null is returned if no data for market is available.
	 */
	@Override
	public List<Long> getTimeRange(long marketId, MarketFunctionEnum marketFunction) {
		if (marketFunction == MarketFunctionEnum.MARKET_TRADED_VOLUME) {
			return marketTradedVolueDao.getTimeRange(marketId);
		} else if (marketFunction == MarketFunctionEnum.LAST_MATCHED_PRICE) {
			return marketPricesDao.getTimeRange(marketId);
		} else {
			throw new IllegalArgumentException("Market function is not supported: " + marketFunction);
		}
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
