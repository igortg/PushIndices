package com.carneau.pushindices;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by itghisi on 28/01/16.
 */
public class QuotesParser {

    private JSONObject root;

    public QuotesParser(String inputData) throws JSONException {
        root = new JSONObject(inputData);
    }


    public double getLastPrice() {
        try {
            JSONObject quote = root.getJSONObject("query").getJSONObject("results").getJSONObject("quote");
            return quote.getDouble("LastTradePriceOnly");
        }
        catch (JSONException e)
        {
            return 0.0;
        }
    }
}