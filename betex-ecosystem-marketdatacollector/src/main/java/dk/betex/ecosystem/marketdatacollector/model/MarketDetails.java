package dk.betex.ecosystem.marketdatacollector.model;

import java.io.Serializable;
import java.util.List;

import org.jcouchdb.document.BaseDocument;
import org.svenson.JSONTypeHint;

/** Data model for market details.
 * 
 * @author korzekwad
 *
 */
public class MarketDetails extends BaseDocument implements Serializable{

	private long marketId;
	private long marketTime;
	private long suspendTime;
	private String menuPath;
	private List<MarketDetailsRunner> runners;
	
	public long getMarketId() {
		return marketId;
	}
	public void setMarketId(long marketId) {
		this.marketId = marketId;
	}
	public long getMarketTime() {
		return marketTime;
	}
	public void setMarketTime(long marketTime) {
		this.marketTime = marketTime;
	}
	public long getSuspendTime() {
		return suspendTime;
	}
	public void setSuspendTime(long suspendTime) {
		this.suspendTime = suspendTime;
	}
	public String getMenuPath() {
		return menuPath;
	}
	public void setMenuPath(String menuPath) {
		this.menuPath = menuPath;
	}
	public List<MarketDetailsRunner> getRunners() {
		return runners;
	}
	
	@JSONTypeHint(MarketDetailsRunner.class)
	public void setRunners(List<MarketDetailsRunner> runners) {
		this.runners = runners;
	}
	@Override
	public String toString() {
		return "MarketDetails [marketId=" + marketId + ", marketTime=" + marketTime + ", menuPath=" + menuPath
				+ ", runners=" + runners + ", suspendTime=" + suspendTime + "]";
	}
	
	
	
}
