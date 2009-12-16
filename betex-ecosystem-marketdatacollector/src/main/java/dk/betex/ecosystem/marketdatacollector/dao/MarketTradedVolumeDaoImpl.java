package dk.betex.ecosystem.marketdatacollector.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Options;
import org.jcouchdb.document.ViewResult;
import org.svenson.ClassNameBasedTypeMapper;
import org.svenson.JSONParser;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;

/** Data access object for a market traded volume on a betting exchange.
 *  Stores data in the Apache CouchDB.
 * 
 * @author korzekwad
 *
 */
public class MarketTradedVolumeDaoImpl implements MarketTradedVolumeDao{

	private final Database database;

	public MarketTradedVolumeDaoImpl(Database database) {
		this.database = database;
	}
	
	/**Add time stamped record of market traded volume to the database.
	 * 
	 */
	@Override
	public void addMarketTradedVolume(MarketTradedVolume marketTradedVolume) {
		database.createDocument(marketTradedVolume);
	}

	/**Returns a history of market traded volume for a given market and period of time.
	 * 
	 * @param marketId
	 * @param from
	 * @param to
	 */
	@Override
	public ViewResult<MarketTradedVolume> getMarketTradedVolume(int marketId, long from, long to) {
		String fn = "{\"map\" : \"function(doc) {emit([doc.marketId,doc.timestamp],doc);}\"}";
		Options options = new Options().startKey(Arrays.asList(marketId,from)).endKey(Arrays.asList(marketId,to));
		ViewResult<MarketTradedVolume> marketTradedVolume = database.queryAdHocView(MarketTradedVolume.class, fn, options,null);
		return marketTradedVolume;
	}

}
