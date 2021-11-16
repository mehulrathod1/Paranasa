package com.ni.parnasa.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.models.convertedCurrency.ConvertedCurrencyResponse;
import com.google.gson.Gson;
//import com.service.pickme.R;
//import com.service.pickme.models.convertedCurrency.ConvertedCurrencyResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ConvertCurrencyAPI {

    private Activity mActivity;
    private ProgressDialog progressDialog;
    private String BASE_URL = "https://xecdapi.xe.com/v1/convert_from.json/?to=";
    private String amount;
    private OnCompletedListener onCompletedListener;
    private String to,from;

    public ConvertCurrencyAPI(Activity mActivity, String amount,String to, String from, OnCompletedListener onCompletedListener) {
        this.mActivity = mActivity;
        progressDialog = new ProgressDialog(mActivity);
        this.amount = amount;
        this.onCompletedListener = onCompletedListener;
        this.to = to;
        this.from = from;
//        new ConvertCurrency().execute();
    }

    public class ConvertCurrency extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
            progressDialog.setMessage(mActivity.getString(R.string.please_wait));
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

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                BASE_URL = BASE_URL + to + "&from="+from+"&amount=" + amount;
                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .method("GET", null)
                        .addHeader("Authorization", "Basic ZGlwdGkzNTcxNzY3Mzk6YWZvYnY1bTgxODEwZ3ZxNWI4MWhucHFzN3Q=")
                        .build();

                okhttp3.Response response = client.newCall(request).execute();
                Response1 = response.body().string();
                return Response1;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return Response1;
        }

        @Override
        protected void onPostExecute(String s) {
            // swipeRefreshLayout.setRefreshing(false);
            progressDialog.dismiss();

            if (s == null) {
                Toast.makeText(mActivity, R.string.msg_network_error, Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Gson gson = new Gson();
                    Log.e("TAG", "onPostExecute: currencyConvert:: "+s );
                    Log.e("TAG", "onPostExecute: currencyConvert:: URL:: "+BASE_URL );
                    ConvertedCurrencyResponse convertedCurrencyResponse = gson.fromJson(s, ConvertedCurrencyResponse.class);
                    if(convertedCurrencyResponse != null && !convertedCurrencyResponse.getTo().isEmpty())
                    onCompletedListener.onSuccessListener(convertedCurrencyResponse.getTo().get(0).getMid());
                } catch (JSONException e) {
                    e.printStackTrace();
                    onCompletedListener.onFailureResponse(e.getMessage());
                }
            }
        }
    }

    public interface OnCompletedListener {
        public void onSuccessListener(String convertedCurrency);

        public void onFailureResponse(String error);
    }
}
