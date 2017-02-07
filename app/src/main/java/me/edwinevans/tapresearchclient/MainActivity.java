package me.edwinevans.tapresearchclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


// Demonstrates using a TapResearch API
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String API_TOKEN = "f47e5ce81688efee79df771e9f9e9994";
    private static String USER_ID = "codetest123";
    private static String mGaid = "";
    private boolean mHasOffer = false;
    private String mOfferUrl = "";

    // don't check for offer if we checked this recently
    private static final int OFFER_CACHE_TIMEOUT_SECONDS = 30;
    private static final String SHARED_PREF_OFFER_LAST_CHECKED = "offer_last_checked";
    private static final String SHARED_PREF_OFFER_URL = "offer_url"; // cached

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncGetGaid.execute(MainActivity.this, new AsyncGetGaid.ResponseHandler() {
            @Override
            public void OnGotGaid(String gaid) {
                mGaid = gaid;
                TapResearchClient.configure(API_TOKEN, gaid, USER_ID);
                if (!getOfferFromCacheIfPossible()) {
                    getOffer();
                    storeOfferInCache();
                }
            }
        });


        findViewById(R.id.take_survey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGaid.isEmpty()) {
                    // NOTE: Should be rare since there is time between when activity is loaded
                    // and the user presses button but we need to specify how to deal with this.
                    // Perhaps we only show button if GAID and offer is available.
                    Log.d(TAG, "GAID unknown");
                    Toast.makeText(MainActivity.this, "GAID unavailabe. This is just a sample",
                            Toast.LENGTH_LONG).show();
                }
                if (mHasOffer) {
                    showOffer();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).
                            setMessage(getString(R.string.no_survey_available));
                    builder.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // will get dismissed
                                }
                            });
                    builder.create().show();
                }
            }
        });
    }

    public void getOffer()  {
        // Using runOnUiThread because the loopj HTTP request handler needs to be created
        // from a looper thread. The actual HTTP request will be executed on a background thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TapResearchClient.getOffer(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            mHasOffer = response.getBoolean("has_offer");
                            mOfferUrl = response.getString("offer_url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                                          Throwable throwable) {
                        Log.e(TAG, "Failed to get offer." +
                                " Status code: " + String.valueOf(statusCode) +
                                " Exception: " + throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject response) {
                        Log.e(TAG, "Failed to get offer. +" +
                                " Status code: " + String.valueOf(statusCode) +
                                " Response: " + response +
                                " Exception: " + throwable);
                    }

                });
            }
        });
    }

    private void showOffer() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", mOfferUrl);
        startActivity(intent);
    }

    private long getTimestampInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private boolean getOfferFromCacheIfPossible() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        long lastChecked = prefs.getLong(SHARED_PREF_OFFER_LAST_CHECKED, 0);
        if (lastChecked == 0) {
            return false;
        }
        long timestamp = getTimestampInSeconds();
        if (timestamp - lastChecked <= OFFER_CACHE_TIMEOUT_SECONDS) {
            String url = prefs.getString(SHARED_PREF_OFFER_URL, null);
            if (url != null && !url.isEmpty()) {
                mHasOffer = true;
                mOfferUrl = url;
                Log.d(TAG, "Loaded offer URL from cache: " + mOfferUrl);
                return true;
            }
        }
        return false;
    }

    private void storeOfferInCache() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(SHARED_PREF_OFFER_LAST_CHECKED, getTimestampInSeconds());
        editor.putString(SHARED_PREF_OFFER_URL, mOfferUrl);
        editor.apply();
    }
}
