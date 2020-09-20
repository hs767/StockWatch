package com.example.pavithra.stock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by jerrysun on 4/2/2017.
 */

public class DatabaseDetail extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Intent intent = getIntent();
            if (intent.hasExtra(Stock.class.getName())) {
                Stock s = (Stock) intent.getSerializableExtra(Stock.class.getName());

                TextView symbol = (TextView) findViewById(R.id.stocksymbol);
                symbol.setText(s.getSymbol());

                TextView company = (TextView) findViewById(R.id.company);
                company.setText(s.getName());

                TextView price = (TextView) findViewById(R.id.price);
                price.setText(s.getPrice());

                TextView pricechange = (TextView) findViewById(R.id.pricechange);
                pricechange.setText((Double.toString(s.getPricechange())));

                TextView perchange = (TextView) findViewById(R.id.perchange);
                perchange.setText(s.getPerchange());
            }

        }
    }







