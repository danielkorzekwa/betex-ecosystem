package dk.betex.ecosystem.marketdatacollector.model;

import java.io.Serializable;
import java.util.List;

import org.jcouchdb.document.BaseDocument;
import org.svenson.JSONTypeHint;

/**Represents traded volume at each price on all of the runners in a particular market.
 * 
 * @author korzekwad
 *
 */
public class MarketTradedVolume extends BaseDocument implements Serializable{

        private int marketId;
        private List<RunnerTradedVolume> runnerTradedVolume;
        /** Epoch time in milliseconds.*/
		private long timestamp;

        public MarketTradedVolume() {}
        
        public MarketTradedVolume(int marketId,List<RunnerTradedVolume> runnerTradedVolume, long timestamp) {
                this.marketId = marketId;
                this.runnerTradedVolume = runnerTradedVolume;
				this.timestamp = timestamp;
        }

		public int getMarketId() {
			return marketId;
		}
		public void setMarketId(int marketId) {
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

		@Override
		public String toString() {
			return "MarketTradedVolume [marketId=" + marketId + ", runnerTradedVolume=" + runnerTradedVolume
					+ ", timestamp=" + timestamp + "]";
		}
}
