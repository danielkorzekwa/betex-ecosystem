package dk.betex.ecosystem.marketdatacollector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jcouchdb.db.Database;
import org.jcouchdb.util.CouchDBUpdater;

/**
 * Updates couchdb views in database. Db views are read from the file system in the format defined by the
 * CouchDBUpdater(http://code.google.com/p/jcouchdb)
 * 
 * @author korzekwad
 * 
 */
public class CouchDbUpdateApp {

	public static void main(String[] args) throws IOException {
		String[] config = askForConfig();
		if(config.length!=3) {
			System.out.println("Wrong format of input data. Number of input parameters should be 3 but is " + config.length);
			System.exit(-1);
		}

		/** Update design docs in database */
		Database database = new Database(config[1].trim(), config[2].trim());
	
		File designDocsLocation = new File(config[0].trim());
		CouchDBUpdater updater = new CouchDBUpdater();
		updater.setDatabase(database);
		updater.setDesignDocumentDir(designDocsLocation);
		updater.updateDesignDocuments();
	}

	/** Returns directory path to the db views, db address and db name
	 * 
	 * @return Element 0 - directory path to the db views, 1 - db address, 2 - db name
	 * @throws IOException
	 */
	public static String[] askForConfig() throws IOException {
		System.out.println("Enter path to the couch db documents and database url in the format [db views directory path, db address, db name]");
		System.out.println("Example: ./designdocs,10.2.2.72,market_traded_volume_test");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String inputData = reader.readLine();

		return inputData.split(",");
	}

}
