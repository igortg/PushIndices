package com.carneau.pushindices;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class QuotesParserTest {

    @Test
    public void testParse() {
        String data = readResourceAsString("usdbrl.json");
        try {
            QuotesParser parser = new QuotesParser(data);
            assertEquals(4.0939, parser.getLastPrice(), 0);
        }
        catch (JSONException e) { assert false; }

    }


    private String readResourceAsString(String resourceName) {
        InputStream stream = getClass().getResourceAsStream(resourceName);
        try {
            Scanner scan = new Scanner(stream);
            StringBuilder buffer = new StringBuilder();
            while (scan.hasNextLine()) {
                buffer.append(scan.nextLine());
            }
            return buffer.toString();
        }
        finally {
            try {
                stream.close();
            } catch (IOException e) { Log.e("Test", "Couldn't release test resources"); }
        }
    }

}