package dk.betex.ecosystem.marketdatacollector;

public enum DbNames {

	MARKET_TRADED_VOLUME("market_traded_volume"),
	MARKET_DETAILS("market_details");
	
	private final String dbName;

	private DbNames(String dbName) {
		this.dbName = dbName; 
	}

	public String getDbName() {
		return dbName;
	}	
}
