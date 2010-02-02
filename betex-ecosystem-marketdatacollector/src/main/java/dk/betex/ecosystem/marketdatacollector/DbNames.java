package dk.betex.ecosystem.marketdatacollector;

public enum DbNames {

	MARKET_DETAILS("market_details"),
	MARKET_PRICES("market_prices");
	
	private final String dbName;

	private DbNames(String dbName) {
		this.dbName = dbName; 
	}

	public String getDbName() {
		return dbName;
	}	
}

