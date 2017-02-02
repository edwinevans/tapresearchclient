package me.edwinevans.tapresearchclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl(intent.getStringExtra("url"));
    }
}
