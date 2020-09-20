package com.example.pavithra.stock;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jerrysun 4/2/2017.
 */

public class stockviewholder extends RecyclerView.ViewHolder {

        public TextView symbol;
        public TextView company;
        public TextView price;
        public TextView pricechange;
        public TextView perchange;

        public stockviewholder(View view) {
            super(view);
            symbol = (TextView) view.findViewById(R.id.stocksymbol);
            company = (TextView) view.findViewById(R.id.company);
            price = (TextView) view.findViewById(R.id.price);
            pricechange = (TextView) view.findViewById(R.id.pricechange);
            perchange = (TextView) view.findViewById(R.id.perchange);
        }

    }
