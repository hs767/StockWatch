package com.example.pavithra.stock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jerrysun on 4/2/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the DatabaseHandler schema, you must increment the DatabaseHandler version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";
    // DB Table Name
    private static final String TABLE_NAME = "StockTable";
    ///DB Columns
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "Company";
    private static final String PRICE = "Price";
    private static final String PRICECHANGE = "PriceChange";
    private static final String PERCHANGE = "PerChange";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null, " +
                    PRICE + " TEXT not null, " +
                    PRICECHANGE + " TEXT not null, " +
                    PERCHANGE + " INT not null)";

    private static SQLiteDatabase database;

    private static DatabaseHandler instance;

    public static DatabaseHandler getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHandler(context);
        return instance;
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public ArrayList<Stock> loadStocks() {

        Log.d(TAG, "loadCountries: LOADING COUNTRY DATA FROM DB");
        ArrayList<Stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL, COMPANY, PRICE, PRICECHANGE, PERCHANGE}, // The columns to return
                null,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String stocksym = cursor.getString(0);
                String company = cursor.getString(1);
                String price = cursor.getString(2);
                double pricechange = cursor.getDouble(3);
                String perchange = cursor.getString(4);
                stocks.add(new Stock(stocksym, company, price, pricechange, perchange));
                cursor.moveToNext();
            }
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }

        return stocks;
    }

    public static void addstock(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getName());
        values.put(PRICE, stock.getPrice());
        values.put(PRICECHANGE, stock.getPricechange());
        values.put(PERCHANGE, stock.getPerchange());

        deletestock(stock.getSymbol());
        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "Stock: " + key);
    }

    public static void deletestock(String name) {
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{name});
    }

    public static void shutDown() {
        database.close();
    }
}

