package dk.betex.ecosystem.marketdatacollector.model;

import java.io.Serializable;
import java.util.List;

import org.jcouchdb.document.BaseDocument;
import org.svenson.JSONTypeHint;

/**Represents traded volume at each price on the given runner in a particular market.
 *
 * @author korzekwad
 *
 */
public class RunnerTradedVolume extends BaseDocument implements Serializable{

        private int selectionId;
        private List<PriceTradedVolume> priceTradedVolume;

        public RunnerTradedVolume() {}
       
        public RunnerTradedVolume(int selectionId, List<PriceTradedVolume> priceTradedVolume) {
                this.selectionId = selectionId;
                this.priceTradedVolume = priceTradedVolume;
        }

        public int getSelectionId() {
                return selectionId;
        }
        public List<PriceTradedVolume> getPriceTradedVolume() {
                return priceTradedVolume;
        }
        public void setSelectionId(int selectionId) {
			this.selectionId = selectionId;
		}
        @JSONTypeHint(PriceTradedVolume.class)
		public void setPriceTradedVolume(List<PriceTradedVolume> priceTradedVolume) {
			this.priceTradedVolume = priceTradedVolume;
		}

		@Override
        public String toString() {
                return "RunnerTradedVolume [selectionId=" + selectionId + ", priceTradedVolume=" + priceTradedVolume + "]";
        }
}

