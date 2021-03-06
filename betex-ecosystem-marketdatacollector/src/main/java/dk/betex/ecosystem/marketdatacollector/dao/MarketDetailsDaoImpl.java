package dk.betex.ecosystem.marketdatacollector.dao;

import java.util.Arrays;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Options;
import org.jcouchdb.document.BaseDocument;
import org.jcouchdb.document.ViewAndDocumentsResult;

import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;

/**Data access object for market details: market name, selection name, market time.
 * 
 * @author korzekwad
 *
 */
public class MarketDetailsDaoImpl implements MarketDetailsDao {

	private final Database database;

	public MarketDetailsDaoImpl(Database database) {
		this.database = database;
	}

	@Override
	public void addMarketDetails(MarketDetails marketDetails) {
		database.createDocument(marketDetails);
	}

	@Override
	public MarketDetails getMarketDetails(long marketId) {
		Options options = new Options();
		options.includeDocs(true);
		ViewAndDocumentsResult<BaseDocument, MarketDetails> viewResult = database.queryDocumentsByKeys(BaseDocument.class,MarketDetails.class, Arrays.asList("" + marketId), options, null);
		MarketDetails marketDetails = viewResult.getRows().get(0).getDocument();
		return marketDetails;
	}

	@Override
	public ViewAndDocumentsResult<BaseDocument,MarketDetails> getMarketDetailsList(int limit) {
		Options options = new Options();
		options.limit(limit);
		options.descending(true);
		ViewAndDocumentsResult<BaseDocument,MarketDetails> result = database.queryViewAndDocuments("marketdetails/byMarketTime", BaseDocument.class,MarketDetails.class, options , null);
		return result;
	}
}
