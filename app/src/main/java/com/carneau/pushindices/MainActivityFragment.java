package com.carneau.pushindices;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchQuotes();
    }

    protected void fetchQuotes() {
        FetchQuotesTask task = new FetchQuotesTask();
        task.execute("usdbrl=x");
    }


    public class FetchQuotesTask extends AsyncTask<String, Void, double[]> {

        private static final String TAG = "FetchQuotesTask";
        protected static final String YQL_URL = "https://query.yahooapis.com/v1/public/yql";
        protected static final String YQL_ENV = "store://datatables.org/alltableswithkeys";

        @Override
        protected double[] doInBackground(String... params) {
            String responseData = requestQuoteData(params[0]);
            double[] prices = new double[params.length];

            try {
                QuotesParser parser = new QuotesParser(responseData);
                prices[0] = parser.getLastPrice();
            }
            catch (JSONException e) { Log.e(TAG, String.format("Couldn't read response: ", e.getMessage())); }
            return prices;
        }

        @Override
        protected void onPostExecute(double[] prices) {
            super.onPostExecute(prices);
            Log.v(TAG, String.format("Dolar = %.2f", prices[0]));
        }

        protected String requestQuoteData(String symbol) {
            HttpURLConnection conn = null;
            String jsonData = "";

            String yql = String.format("select * from yahoo.finance.quotes where symbol = '%s'", symbol);
            Uri.Builder builder = Uri.parse(YQL_URL).buildUpon();
            builder
                    .appendQueryParameter("q", yql)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("env", YQL_ENV);

            try {
                URL url = new URL(builder.toString());
                Log.v(TAG, url.toString());

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                if (conn.getResponseCode() == 200) {
                    jsonData = readFromStream(conn.getInputStream());
                }
                else {
                    Log.e(TAG, String.format("Request Error: %s", conn.getResponseMessage()));
                }
            }
            catch (IOException e) {
                Log.e(TAG, "Error");
            }
            finally {
                if (conn != null)
                    conn.disconnect();
            }
            return jsonData;
        }


        /**
         * Build a string from an InputStream
         */
        protected String readFromStream(InputStream stream) {
            Scanner responseScanner = new Scanner(stream);
            StringBuilder responseBuilder = new StringBuilder();
            while (responseScanner.hasNextLine()) {
                responseBuilder.append(responseScanner.nextLine());
            }
            return responseBuilder.toString();
        }
    }
}
