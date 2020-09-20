package com.example.pavithra.stock;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.Calendar;
import org.joda.time.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private List<Stock> stockList = new ArrayList<>();
    private Stocksadapter mAdapter;
    private SwipeRefreshLayout swiper;
    private String apiKey = "&apikey=3TY7WSX6VDCP6LDC";

    public ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        mAdapter = new Stocksadapter(stockList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        DatabaseHandler d = new DatabaseHandler(this);
        stockList.clear();
         List<Stock> stklist = d.loadStocks();

        if(isNetworkAvailable()) {
            for (int i=0;i<stklist.size();i++){
                new StockDownloadTask (this).execute(stklist.get(i).getSymbol(),stklist.get(i).getName());
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();

           for (int i=0;i<stklist.size();i++){
               stockList.add(stklist.get(i));
               mAdapter.notifyDataSetChanged();
           }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_add, menu);
        return true;
    }

    public static String userinput;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addstock:
                Log.d(TAG, "onOptionsItemSelected: Outside If Start");
                if(isNetworkAvailable()) {
                    Log.d(TAG, "onOptionsItemSelected: Inside");
                    /*LayoutInflater inflater = LayoutInflater.from(this);
                    final View view = inflater.inflate(R.layout.dialog, null);*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Please enter the Stock symbol:");
                    builder.setTitle("Add Stock");

                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);

                    builder.setView(et);

                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            userinput = et.getText().toString();

                                Log.d(TAG, "onClick: Outside If"+userinput);

                                    Log.d(TAG, "onClick: Before call to async ");
                                new asynctask(MainActivity.this).execute(userinput);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
                    builder.setTitle("No Network Connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        DatabaseHandler d = new DatabaseHandler(this);
        d.shutDown();
        super.onDestroy();
    }

    public void doRefresh() {
        if (isNetworkAvailable()) {
             ArrayList<Stock> list = new ArrayList<>();
            DatabaseHandler d = new DatabaseHandler(this);
            stockList.clear();
            list=d.loadStocks();
            for(int i=0;i<list.size();i++){
                new StockDownloadTask (this).execute(list.get(i).getSymbol(),list.get(i).getName());
            }

                    mAdapter.notifyDataSetChanged();
            Log.d(TAG,"Refreshing");
                    swiper.setRefreshing(false);
        } else {
            swiper.setRefreshing(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please check your internet connection");
            builder.setTitle("No Internet");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    @Override
    public void onClick(View v) {

        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);
        String sym = s.getSymbol();
        String url = "http://www.marketwatch.com/investing/Stock/" + sym;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete?");
        builder.setTitle("Delete Stock");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseHandler.getInstance(MainActivity.this).deletestock(stockList.get(pos).getSymbol());
                stockList.remove(pos);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


        return true;
    }

    public void newSymbol(String symb, String comp) {
        StockDownloadTask sdk = new StockDownloadTask (this);
        sdk.execute(symb, comp);
    }

    public boolean there(Stock s) {

        for (int i = 0; i < stockList.size(); i++) {
            if (s.getSymbol().equals(stockList.get(i).getSymbol())) {
                return true;
            }
        }
        return false;
    }

    public void addStock(Stock stock) {
        Log.d(TAG, "addStock: Beginning");

        try {
            if (stock.getSymbol() == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No data for the Stock symbol");
                builder.setTitle("Data not found");
                AlertDialog dialog = builder.create();
                dialog.show();

            } else if(stockList.size()==0){
                Log.d(TAG, "addStock: Inside Else");
                    stockList.add(stock);
                    DatabaseHandler.addstock(stock);
                    mAdapter.notifyDataSetChanged();

            } else if (!there(stock)) {

                stockList.add(stock);
                Collections.sort(stockList, new Comparator<Stock>() {
                    @Override
                    public int compare(Stock o1, Stock o2) {
                        return o1.getSymbol().compareToIgnoreCase(o2.getSymbol());
                    }
                });
                DatabaseHandler.addstock(stock);

                mAdapter.notifyDataSetChanged();

                } else if(there(stock)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Stock already in the DatabaseHandler");
                    builder.setTitle("Duplicate");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    ///////////
    public class asynctask extends AsyncTask<String, Void, List<String>> {

        private MainActivity mainActivity;
        private int count;

        //private final String dataURL = "http://stocksearchapi.com/api/?api_key=93ce545bbdd4dff6fc99936a3bf71bb6c4e38b33&search_text=";
        private final String dataURL = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=";

        public asynctask(MainActivity ma) {
            mainActivity = ma;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Fetching Data");
            pd.show();
        }


        @Override
        protected void onPostExecute(List<String> s) {
            super.onPostExecute(s);

            if (pd.isShowing()) {
                pd.dismiss();
            }

            try {
                if(s == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setMessage("No data for the Stock symbol");
                    builder.setTitle("Symbol not found");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                final String[] sArray =  s.toArray(new String[s.size()]);
                if (sArray.length == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setMessage("No data for the Stock symbol");
                    builder.setTitle("Symbol not found");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if(sArray.length > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle("Select from the list");
                    builder.setItems(sArray, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String[] str_array = sArray[which].split("-");
                            String sym = str_array[0];
                            String comp = str_array[1];
                            mainActivity.newSymbol(sym, comp);
                        }
                    });
                    builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                } else if (sArray.length == 1) {
                    String[] str_array = sArray[0].split("-");
                    String symb = str_array[0];
                    String comp = str_array[1];
                    mainActivity.newSymbol(symb, comp);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }


        @Override
        protected List<String> doInBackground(String... params) {
            List<String> sblist = new ArrayList<>();
            Uri dataUri = Uri.parse(dataURL+params[0]+apiKey);
            //Uri dataUri = Uri.parse(dataURL);

            String urlToUse = dataUri.toString();

            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlToUse);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                sblist.addAll(parseJSON(sb.toString()));
                return sblist;
            } catch (Exception e) {
                return null;
            }
        }


        private ArrayList<String> parseJSON(String s) {

            ArrayList<String> symcomlist = new ArrayList<>();
            try {
                s = s.replace("\n", "");
                JSONObject temp = new JSONObject(s.replace("//",""));
                //JSONArray jObjMain = new JSONArray(s.replace("//",""));
                JSONArray jObjMain = temp.getJSONArray("bestMatches");
                count = jObjMain.length();

                for (int i = 0; i < jObjMain.length(); i++) {
                    JSONObject jCountry = jObjMain.getJSONObject(i);
                    //String symbol = jCountry.getString("company_symbol");
                    //String name = jCountry.getString("company_name");
                    String symbol = jCountry.getString("1. symbol");
                    String name = jCountry.getString("2. name");
                    String symcom = symbol + "-" + name;
                    symcomlist.add(symcom);
                }
                return symcomlist;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    //////////
    class StockDownloadTask extends AsyncTask<String, Void, String> {

        private MainActivity mainActivity;
        private Stock stockdata;
        //private final String dataURL = "http://finance.google.com/finance/info?client=ig&q=";
        private final String dataURL = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=";
        private static final String TAG = "AsyncStockDataLoader";

        public StockDownloadTask (MainActivity ma) {
            mainActivity = ma;
        }

        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Downloading Data");
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {

            if (pd.isShowing()) {
                pd.dismiss();
            }

            try {
                mainActivity.addStock(stockdata);
            } catch (Exception e){
                e.printStackTrace();
            }

        }


        @Override
        protected String doInBackground(String... params) {
            String uri2 = "&interval=5min";
            DateTime date = new DateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime oneDayAgo = date.minusDays(1);
            String d = date.toString(fmt);
            String pd = oneDayAgo.toString(fmt);

            Uri dataUri = Uri.parse(dataURL+params[0]+uri2+apiKey);
            //Uri dataUri = Uri.parse(dataURL);
            String urlToUse = dataUri.toString();
            String jsonstring;
            Log.d(TAG, "doInBackground: " + urlToUse);

            StringBuilder sb = new StringBuilder();
            try{
                URL url = new URL(urlToUse);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                jsonstring = sb.toString().replace("//","");
                //jsonstring  = jsonstring.replace("\n", "");

                JSONObject jObj = new JSONObject(jsonstring);
                String symbol = params[0];
                String name = params[1];
                JSONObject datas = (JSONObject) jObj.get("Time Series (5min)");
                JSONArray ds = datas.names();

                int count = 0;
                while (count <= ds.length()) {
                    String temp = (String) ds.get(count);
                    if (temp.contains(pd)) {
                        break;
                    } else {
                        count++;
                        continue;
                    }
                }

                JSONObject tdata = (JSONObject) datas.get((String) ds.get(0));
                JSONObject pdata = (JSONObject) datas.get((String) ds.get(count));

                double cPrice = tdata.getDouble("4. close");
                double pPrice = pdata.getDouble("4. close");
                String percentage = String.format("%.2f", ((cPrice-pPrice)*100/pPrice));
                stockdata = new Stock (symbol, name, String.format("%.2f", cPrice), Double.parseDouble(String.format("%.2f", cPrice-pPrice)), percentage);

                /*for (int i = 0; i < jarray.length(); i++) {

                    JSONObject jCountry = jarray.getJSONObject(i);
                    String ticker = jCountry.getString("t");
                    String price = jCountry.getString("l");
                    String pricechange = jCountry.getString("c");
                    double prcchange = Double.parseDouble(pricechange);
                    String perchange = jCountry.getString("cp");

                    stockdata = new Stock(symbol, name, "test", 1.00, "test");

                }*/

                return jsonstring;
            } catch (Exception e) {
                e.printStackTrace();
            }
            //stockdata = new Stock("MSFT", "Microsoft", "test", 1.00, "test");
            return null;
        }
    }
}
