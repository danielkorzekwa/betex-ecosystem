package dk.betex.ecosystem.marketdatacollector.model;

import java.io.Serializable;

import org.jcouchdb.document.BaseDocument;

/**Data model for market details.
 * 
 * @author korzekwad
 *
 */
public class MarketDetailsRunner implements Serializable{

	private int selectionId;
	private String selectionName;
	public int getSelectionId() {
		return selectionId;
	}
	public void setSelectionId(int selectionId) {
		this.selectionId = selectionId;
	}
	public String getSelectionName() {
		return selectionName;
	}
	public void setSelectionName(String selectionName) {
		this.selectionName = selectionName;
	}
	@Override
	public String toString() {
		return "MarketDetailsRunner [selectionId=" + selectionId + ", selectionName=" + selectionName + "]";
	}	
	
	
}
