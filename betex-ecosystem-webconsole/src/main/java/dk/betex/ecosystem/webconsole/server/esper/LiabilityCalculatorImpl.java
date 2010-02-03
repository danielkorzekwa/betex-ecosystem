package dk.betex.ecosystem.webconsole.server.esper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.betex.ecosystem.webconsole.server.esper.LiabilityCalculator.MarketLiability;

/**
 * Calculates market liability based on bets and probabilities.
 * 
 * @author korzekwad
 * 
 */
public class LiabilityCalculatorImpl implements LiabilityCalculator {

	@Override
	public List<MarketLiability> calculateLiability(List<Bet> bets) {

		/** key - marketId */
		Map<Long, MarketLiability> liabilitiesMap = new HashMap<Long, MarketLiability>();

		for (Bet bet : bets) {
			
			MarketLiability marketLiability = liabilitiesMap.get(bet.getMarketId());
			if (marketLiability == null) {
				marketLiability = new MarketLiability(bet.getMarketId());
				liabilitiesMap.put(bet.getMarketId(), marketLiability);
			}
			marketLiability.addBetPayout(bet.getSelectionId(),bet.getBetSize(),bet.getBetPrice());
		}
		return new ArrayList<MarketLiability>(liabilitiesMap.values());
	}

}
