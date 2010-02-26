package dk.betex.ecosystem.marketdatacollector;

public enum ConfigEnum {

	COUCHDB_ADDRESS("10.2.4.191");
	
	private final String value;

	ConfigEnum(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
}
