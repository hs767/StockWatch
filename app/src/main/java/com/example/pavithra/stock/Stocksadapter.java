package com.example.pavithra.stock;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jerrysun on 4/2/2017.
 */

public class Stocksadapter extends RecyclerView.Adapter<stockviewholder>{

        private List<Stock> stockList;
        private MainActivity mainAct;

        public Stocksadapter(List<Stock> stockList, MainActivity ma) {
            this.stockList = stockList;
            mainAct = ma;
        }

        @Override
        public stockviewholder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stock_list_row, parent, false);

            itemView.setOnClickListener((View.OnClickListener) mainAct);
            itemView.setOnLongClickListener((View.OnLongClickListener) mainAct);

            return new stockviewholder(itemView);
        }

        @Override
        public void onBindViewHolder(stockviewholder holder, int position) {
           Stock stock = stockList.get(position);
            holder.symbol.setText(stock.getSymbol());
            holder.company.setText(stock.getName());
            holder.price.setText(stock.getPrice());
            if(stock.getPricechange()>0){
                holder.pricechange.setText("▲"+(Double.toString(stock.getPricechange())));
                    holder.symbol.setTextColor(Color.GREEN);
                    holder.company.setTextColor(Color.GREEN);
                    holder.price.setTextColor(Color.GREEN);
                    holder.pricechange.setTextColor(Color.GREEN);
                    holder.perchange.setTextColor(Color.GREEN);


                }else{
                holder.pricechange.setText("▼"+(Double.toString(stock.getPricechange())));
                holder.symbol.setTextColor(Color.RED);
                holder.company.setTextColor(Color.RED);
                holder.price.setTextColor(Color.RED);
                holder.pricechange.setTextColor(Color.RED);
                holder.perchange.setTextColor(Color.RED);
                }

            holder.perchange.setText("("+(stock.getPerchange())+"%)");
        }

        @Override
        public int getItemCount() {
            return stockList.size();
        }

    }




