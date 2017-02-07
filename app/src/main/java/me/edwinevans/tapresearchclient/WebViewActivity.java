package me.edwinevans.tapresearchclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;
import java.net.URISyntaxException;

public class WebViewActivity extends AppCompatActivity {
    private String mAbandonUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(intent.getStringExtra("url"));
        mAbandonUrl = intent.getStringExtra("abandon_url");
    }

    @Override
    public void onBackPressed() {
        // If TapResearch domain go back to previous screen. Otherwise go to
        // abandon URL
        WebView webView = (WebView) findViewById(R.id.webview);
        String webUrl = webView.getUrl();
        String domain = getDomainName(webUrl);
        if (!TextUtils.isEmpty(domain) && domain.equals("tapresearch.com")) {
            super.onBackPressed();
        }
        else {
            webView.loadUrl(mAbandonUrl);
        }
    }

    public String getDomainName(String url)  {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }}
