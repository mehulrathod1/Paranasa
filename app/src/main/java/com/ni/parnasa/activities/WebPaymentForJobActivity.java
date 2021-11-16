package com.ni.parnasa.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.MultiLanguageUtils;


public class WebPaymentForJobActivity extends AppCompatActivity {

    private Context mContext;
    private WebView web_for_membership;
    private ProgressBar progressBar;

    private String PAYPAL_LINK = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_payment);

        mContext = WebPaymentForJobActivity.this;

        try {
            PAYPAL_LINK = getIntent().getStringExtra("paypalUrl");
        } catch (Exception e) {
            e.printStackTrace();
        }

        web_for_membership = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);

        WebSettings settings = web_for_membership.getSettings();
        settings.setJavaScriptEnabled(true);
        web_for_membership.setWebViewClient(new MyWebViewClient());
        if (Build.VERSION.SDK_INT >= 21) {
            /** For ignore http to https SSL problem */
            web_for_membership.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        progressBar.setVisibility(View.VISIBLE);
        web_for_membership.loadUrl(PAYPAL_LINK);
    }

    @Override
    public void onPause() {
        web_for_membership.onPause();
        web_for_membership.pauseTimers();
        super.onPause();
    }

    @Override
    public void onResume() {
        web_for_membership.onResume();
        web_for_membership.resumeTimers();
        super.onResume();
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }



        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("ZZZ", "onPageFinished " + url);
            if (url.contains("action=cancel")) {
                Toast.makeText(mContext, "Transaction canceled", Toast.LENGTH_LONG).show();
                finish();
            } else if (url.contains("action=success")) {
                Toast.makeText(mContext, "Transaction success", Toast.LENGTH_LONG).show();

                /*String sub = url.substring(url.indexOf("token=") + 6);
                sub = sub.substring(0, sub.indexOf("&"));*/

                Intent intent = new Intent();
//                intent.putExtra("token", sub.trim());
                setResult(Activity.RESULT_OK);
                finish();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Toast.makeText(mContext, "Receive Error..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        web_for_membership.stopLoading();
        web_for_membership.setWebChromeClient(null);
        web_for_membership.setWebViewClient(null);
        web_for_membership.destroy();
        web_for_membership = null;
        super.onDestroy();
    }
}
