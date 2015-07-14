package com.gotwingm.my.meditation;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;


public class PrivateBrowser extends Activity {

    WebView mWebView;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_browser_layout);

        title = (TextView) findViewById(R.id.browserTitle);
        mWebView = (WebView) findViewById(R.id.webView);

        title.setText(getIntent().getStringExtra("title"));
        mWebView.loadUrl(getIntent().getStringExtra("url"));

    }

}
