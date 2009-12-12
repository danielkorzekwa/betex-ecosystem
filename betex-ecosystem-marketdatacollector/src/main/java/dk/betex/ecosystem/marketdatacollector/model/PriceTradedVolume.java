package dk.betex.ecosystem.marketdatacollector.model;

import java.io.Serializable;

/**
 * Represents traded volume for the given price on the given runner in a particular market.
 *
 * @author korzekwad
 *
 */
public class PriceTradedVolume implements Serializable{

        private double price;
        private double tradedVolume;

        public PriceTradedVolume() {
        }
       
        /**
         *
         * @param price
         * @param tradedVolume
         *            The total amount matched for the given price
         */
        public PriceTradedVolume(double price, double tradedVolume) {
                this.price = price;
                this.tradedVolume = tradedVolume;
        }

        public double getPrice() {
                return price;
        }

        public double getTradedVolume() {
                return tradedVolume;
        }

        @Override
        public String toString() {
                return "PriceTradedVolume [price=" + price + ", tradedVolume=" + tradedVolume + "]";
        }
}

