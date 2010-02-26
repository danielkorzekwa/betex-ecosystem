package dk.betex.ecosystem.webconsole.client.service;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**Market information data model
 * 
 * @author korzekwad
 *
 */
public class MarketInfo implements IsSerializable{

	private long marketId;
	private String menuPath;
	private String marketName;
	private Date marketTime;
	
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public long getMarketId() {
		return marketId;
	}
	public void setMarketId(long marketId) {
		this.marketId = marketId;
	}
	public String getMenuPath() {
		return menuPath;
	}
	public void setMenuPath(String menuPath) {
		this.menuPath = menuPath;
	}
	public Date getMarketTime() {
		return marketTime;
	}
	public void setMarketTime(Date marketTime) {
		this.marketTime = marketTime;
	}
}
