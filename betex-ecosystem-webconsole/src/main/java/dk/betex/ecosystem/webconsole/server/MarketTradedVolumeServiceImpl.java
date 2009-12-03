package dk.betex.ecosystem.webconsole.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import dk.betex.ecosystem.webconsole.client.model.MarketTradedVolume;
import dk.betex.ecosystem.webconsole.client.model.PriceTradedVolume;
import dk.betex.ecosystem.webconsole.client.model.RunnerTradedVolume;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.BetFairUtil;
import dk.bot.betfairservice.DefaultBetFairServiceFactoryBean;
import dk.bot.betfairservice.model.BFMarketTradedVolume;
import dk.bot.betfairservice.model.LoginResponse;

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
	public MarketTradedVolume getMarketTradedVolume(int marketId) {
		BFMarketTradedVolume bfMarketTradedVolume = betfairService.getMarketTradedVolume(marketId);
		MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume);

		return marketTradedVolume;
	}

	@Override
	public MarketTradedVolume getMarketTradedVolumeForAllPrices(int marketId) {
		BFMarketTradedVolume bfMarketTradedVolume = betfairService.getMarketTradedVolume(marketId);
		MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume);

		MarketTradedVolume normalizedMarketTradedVolume = MarketTradedVolumeFactory
				.createNormalizedAsProbs(marketTradedVolume);
		return normalizedMarketTradedVolume;
	}

}
