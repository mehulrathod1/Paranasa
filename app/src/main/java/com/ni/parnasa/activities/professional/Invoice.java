package com.ni.parnasa.activities.professional;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Invoice extends AppCompatActivity {
    WebView webView;
    String Id;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        webView = (WebView) findViewById(R.id.invoice);
        Intent intent = getIntent();
        Id = intent.getStringExtra("id");
        progressDialog = new ProgressDialog(Invoice.this);
        new Invoice_Details().execute();


    }

    public class Invoice_Details extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }


        @Override
        protected String doInBackground(String... params) {
            String Response1 = null;

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();
            try {

                JSONObject request_main = new JSONObject();
                request_main.put("authorised_key", BaseUrl.authorised_key);
                request_main.put("job_id", Id);


                Log.v("service_send", String.valueOf(request_main));


                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "Allinvoices")
                        .post(body)
                        .build();


                okhttp3.Response response = client.newCall(request).execute();
                Response1 = response.body().string();
                return Response1;

            } catch (IOException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return Response1;
        }

        @Override
        protected void onPostExecute(String s) {
            // swipeRefreshLayout.setRefreshing(false);
            progressDialog.dismiss();

            if (s == null) {

                Toast.makeText(getApplicationContext(), "Network Error !", Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Log.v("service_return", String.valueOf(jsonObject));
                    String status = jsonObject.getString("status");
                    if (status.equals("Yes")) {
                        String data = jsonObject.getString("data");
                        webView.loadData(data, "text/html; charset=UTF-8", null);

/*
                            webView.getSettings().setLoadsImagesAutomatically(true);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                            webView.loadUrl(data);*/


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
