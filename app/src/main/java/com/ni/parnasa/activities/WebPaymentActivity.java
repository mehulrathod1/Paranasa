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


public class WebPaymentActivity extends AppCompatActivity {

    private Context mContext;
    private WebView web_for_membership;
    private ProgressBar progressBar;

    private String PAYPAL_LINK = "", SUCCESS_URL = "success", CANCEL_URL = "http://pickmeapp.ncryptedprojects.com/cancel";
    private boolean isFromRegistrationFee = false;

    // For cancel :- http://pickmeapp.ncryptedprojects.com/cancel
    // For Success :- http://pickmeapp.ncryptedprojects.com/success?action=success&amt=30.28&cc=USD&cm=8&item_name=PayPal%20test&item_number=23&st=Completed&tx=40V59048JG585640Y
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_payment);

        mContext = WebPaymentActivity.this;

        try {
            isFromRegistrationFee = getIntent().getBooleanExtra("isFromRegistrationFee", false);
            PAYPAL_LINK = getIntent().getStringExtra("paypalUrl");
            /*SUCCESS_URL = getIntent().getStringExtra("successUrl");
            CANCEL_URL = getIntent().getStringExtra("cancelUrl");*/
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

        /**
         * While subscription plan payment at sing-up of profession
         */
        // http://pickmeapp.ncryptedprojects.com/api/subscriptionPayments/paypalCallback?status=cancel&token=EC-13600764GL9494445
        // https://pickmeapp.co/dev_v2/api/subscriptionPayments/paypalCallback?status=success&action=success&user_id=2200&token=EC-4PK26204AN013641C&ba_token=BA-7E7165577S2666644

        // isFromRegistrationFee == true
        // https://pickmeapp.co/dev_v2/register_success?action=success&token=1589967668&platform=app&u=MjE5Nw==
        // https://pickmeapp.co/dev_v2/register_cancel?action=cancel&platform=app&u=MjIwMA==
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("ZZZ", "onPageFinished" + url);

            if (isFromRegistrationFee) {
                if (url.contains("action=cancel")) {
                    Toast.makeText(mContext, "Transaction canceled", Toast.LENGTH_LONG).show();
                    finish();
                } else if (url.contains("action=success")) {
                    Toast.makeText(mContext, "Transaction success", Toast.LENGTH_LONG).show();

                    String sub = url.substring(url.indexOf("token=") + 6);
                    sub = sub.substring(0, sub.indexOf("&"));

                    Intent intent = new Intent();
                    intent.putExtra("token", sub.trim());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            } else {
                if (url.contains("status=cancel")) {
                    Toast.makeText(mContext, "Transaction canceled", Toast.LENGTH_LONG).show();
                    finish();
                } else if (url.contains("status=success")) {
                    Toast.makeText(mContext, "Transaction success", Toast.LENGTH_LONG).show();

                    String sub = url.substring(url.indexOf("token=") + 6);
                    sub = sub.substring(0, sub.indexOf("&"));
                    Intent intent = new Intent();
                    intent.putExtra("token", sub.trim());
                    setResult(Activity.RESULT_OK, intent);
                    finish();

                }
            }

            /*if (url.contains("status=cancel")) {
                Toast.makeText(mContext, "Transaction canceled", Toast.LENGTH_LONG).show();
                finish();
            } else if (url.contains(SUCCESS_URL)) {
                Toast.makeText(mContext, "Transaction success", Toast.LENGTH_LONG).show();

                String sub = url.substring(url.indexOf("token=") + 6);
                if (isFromRegistrationFee) {
                    sub = sub.substring(0, sub.indexOf("&"));
                }

                Intent intent = new Intent();
                intent.putExtra("token", sub.trim());
                setResult(Activity.RESULT_OK, intent);
                finish();

            } *//*else if (url.equals(CANCEL_URL)) {
                Toast.makeText(mContext, "Transaction canceled", Toast.LENGTH_LONG).show();
                finish();
            }*/
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            progressBar.setVisibility(View.GONE);

            Log.e("ZZZ", "onStart:" + url);

           /* if (url.contains(SUCCESS_URL)) {
                Toast.makeText(mContext, "Transaction success", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();
            } else if (url.contains(CANCEL_URL)) {
                Toast.makeText(mContext, "Transaction canceled", Toast.LENGTH_LONG).show();
                finish();
            }*/
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
