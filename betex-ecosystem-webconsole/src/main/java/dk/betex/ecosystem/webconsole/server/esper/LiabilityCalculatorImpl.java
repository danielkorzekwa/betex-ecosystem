package dk.betex.ecosystem.webconsole.server.esper;

import java.util.List;
import java.util.Map;

/**Calculates market liability based on bets and probabilities.
 * 
 * @author korzekwad
 *
 */
public class LiabilityCalculatorImpl implements LiabilityCalculator{

	@Override
	public double calculateLiability(List<Bet> bets, Map<Long, MarketProb> marketProbs) {
		double liability=0;
		for(Bet bet: bets) {
			double prob = marketProbs.get(bet.getMarketId()).getRunnerProbs().get(bet.getSelectionId());
			liability += bet.getBetSize()*bet.getBetPrice()*prob - bet.getBetSize();
		}
		return liability;
	}

	
	

}
