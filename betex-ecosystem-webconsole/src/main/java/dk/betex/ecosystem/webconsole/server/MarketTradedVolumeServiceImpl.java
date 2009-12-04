package dk.betex.ecosystem.webconsole.server;

import javax.servlet.ServletException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.util.MathUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import dk.betex.ecosystem.webconsole.client.model.HeatMapModel;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.DefaultBetFairServiceFactoryBean;
import dk.bot.betfairservice.model.BFMarketDetails;
import dk.bot.betfairservice.model.BFMarketTradedVolume;

/**
 * Returns traded volume at each price on all of the runners in a particular market.
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolumeServiceImpl extends RemoteServiceServlet implements MarketTradedVolumeService {

	private DefaultBetFairServiceFactoryBean betfairServiceFactoryBean;
	private BetFairService betfairService;

	@Override
	public void init() throws ServletException {

		betfairServiceFactoryBean = new DefaultBetFairServiceFactoryBean();

		/**Login*/
		String user = System.getenv("dk.betex.ecosystem.webconsole.bfUser");
		if(user==null) throw new IllegalStateException("System property is not set: dk.betex.ecosystem.webconsole.bfUser");
		String pass = System.getenv("dk.betex.ecosystem.webconsole.bfPass");
		if(pass==null) throw new IllegalStateException("System property is not set: dk.betex.ecosystem.webconsole.bfPass");
		betfairServiceFactoryBean.setUser(user);
		betfairServiceFactoryBean.setPassword(pass);
		betfairServiceFactoryBean.setProductId(82);
		betfairServiceFactoryBean.login();
		try {
			betfairService = (BetFairService) betfairServiceFactoryBean.getObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public HeatMapModel getMarketTradedVolume(int marketId) {
		try {
		BFMarketTradedVolume bfMarketTradedVolume = betfairService.getMarketTradedVolume(marketId);
		BFMarketDetails marketDetails = betfairService.getMarketDetails(marketId);
		
		HeatMapModel marketTradedVolume = MarketTradedVolumeFactory
				.createHeatMap(bfMarketTradedVolume);
		
		/**Replace selectionId with selectionName*/
		for(int i=0;i<marketTradedVolume.getxAxisLabels().length;i++) {
			int selectionId = Integer.parseInt(marketTradedVolume.getxAxisLabels()[i]);
			String selectionName = marketDetails.getSelectionName(selectionId);
			marketTradedVolume.getxAxisLabels()[i]=selectionName;
		}
		
		/**Change probabilities to prices*/
		for(int i=0;i<marketTradedVolume.getyAxisLabels().length;i++) {
			double price = Double.parseDouble(marketTradedVolume.getyAxisLabels()[i])/100d;
			marketTradedVolume.getyAxisLabels()[i] = "" + MathUtils.round(1d/price,2);
		}
		
		return marketTradedVolume;
		}
		catch(Exception e) {
			LogFactory.getLog(this.getClass()).error("Can't get market traded volume for market: " + marketId,e);
			throw new RuntimeException(e);
		}
	}

}
