package dk.betex.ecosystem.marketdatacollector.dao;

import java.io.File;
import java.util.Arrays;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Options;
import org.jcouchdb.document.ViewResult;
import org.jcouchdb.util.CouchDBUpdater;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;

/**
 * Data access object for a market traded volume on a betting exchange. Stores data in the Apache CouchDB.
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolumeDaoImpl implements MarketTradedVolumeDao {

	private final Database database;

	public MarketTradedVolumeDaoImpl(Database database) {
		this.database = database;

		/** Update design docs in database*/
		try {
			File designDocsLocation;
			designDocsLocation = new File(this.getClass().getClassLoader().getResource("designdocs").toURI());
			CouchDBUpdater updater = new CouchDBUpdater();
			updater.setDatabase(database);
			updater.setDesignDocumentDir(designDocsLocation);
			updater.updateDesignDocuments();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Add time stamped record of market traded volume to the database.
	 * 
	 */
	@Override
	public void addMarketTradedVolume(MarketTradedVolume marketTradedVolume) {
		database.createDocument(marketTradedVolume);
	}

	/**
	 * Returns a history of market traded volume for a given market and period of time.
	 * 
	 * @param marketId
	 * @param from
	 * @param to
	 */
	@Override
	public ViewResult<MarketTradedVolume> getMarketTradedVolume(long marketId, long from, long to) {
		Options options = new Options().startKey(Arrays.asList(marketId, from)).endKey(Arrays.asList(marketId, to));
		ViewResult<MarketTradedVolume> marketTradedVolume = database.queryView("default/byMarketId",
				MarketTradedVolume.class, options, null);
		return marketTradedVolume;
	}
	
	@Override
	public long getNumOfRecords(long marketId) {
		Options options = new Options();
		options.group(true);
		options.key(marketId);
		ViewResult<Long> queryView = database.queryView("default/countRecordsByMarketId", Long.class, options , null);
		
		if(queryView.getRows().size()==1) {
		return ((Long)queryView.getRows().get(0).getValue()).longValue();
		}
		else {
			return 0;
		}
	}

}
