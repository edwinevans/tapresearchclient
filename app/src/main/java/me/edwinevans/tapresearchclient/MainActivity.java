package me.edwinevans.tapresearchclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;


// Demonstrates using a TapReasearch API
public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    static String mGaid = ""; // "d93ffa86-a970-4b06-8cbe-f6de3d87b406"
    boolean mHasOffer = false;
    String mOfferUrl = "";

    public void getOffers()  {
        // Using runOnUiThread because the loopj HTTP request handler needs to be created
        // from a looper thread. The actual HTTP request will be executed on a background thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // NOTE: In real version I'd store the params info in the TapResearchClient class
                // if needed for other calls
                RequestParams params = new RequestParams();
                params.put("device_identifier", mGaid);
                params.put("api_token", "f47e5ce81688efee79df771e9f9e9994");
                params.put("user_identifier", "codetest123");
                TapResearchClient.getOffers(params, new JsonHttpResponseHandler() {
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
                        Log.e(TAG, "Failed to get offers. Exception: " + throwable);
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncGetGaid.execute(MainActivity.this, new AsyncGetGaid.ResponseHandler() {
            @Override
            public void OnGotGaid(String gaid) {
                mGaid = gaid;
                getOffers();
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

    private void showOffer() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", mOfferUrl);
        startActivity(intent);
    }
}