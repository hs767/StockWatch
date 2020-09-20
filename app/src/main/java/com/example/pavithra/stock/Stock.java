package com.example.pavithra.stock;

import java.io.Serializable;

/**
 * Created by jerrysun on 4/2/2017.
 */

public class Stock {

        private String symbol;
        private String name;
        private String price;
        private double pricechange;
        private String perchange;

        public Stock(String symbol, String name, String price, double pricechange, String perchange) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
            this.pricechange = pricechange;
            this.perchange = perchange;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public double getPricechange() {
            return pricechange;
        }

        public String getPerchange() {
            return perchange;
        }
    }
