package com.ni.parnasa.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.databinding.ActivityJobRateBinding;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.ni.parnasa.utils.PrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class JobRateActivity extends AppCompatActivity {

    ImageView img_back;
    ProgressDialog progressDialog;
    PrefsUtil prefsUtil;
    EditText et_basicrate, et_hourrate, edt_tax_rate;
    Button bt_save, btn_cancel;
    String st_basicrate, st_hourrate, st_taxrate;

    private ActivityJobRateBinding binding;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_rate);

        mContext = JobRateActivity.this;

        prefsUtil = new PrefsUtil(JobRateActivity.this);

        init();

        binding.txtCurrencyBasicRate.setText(PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));
        binding.txtCurrencyHourRate.setText(PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));

        et_basicrate = (EditText) findViewById(R.id.edt_basic_rate);
        et_hourrate = (EditText) findViewById(R.id.edt_hour_rate);
        edt_tax_rate = (EditText) findViewById(R.id.edt_tax_rate);
        bt_save = (Button) findViewById(R.id.btn_save);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        progressDialog = new ProgressDialog(JobRateActivity.this);


        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                st_basicrate = et_basicrate.getText().toString().trim();
                st_hourrate = et_hourrate.getText().toString().trim();
                st_taxrate = edt_tax_rate.getText().toString().trim();


                Log.e("jamsedpurtata", "onClick: " + st_basicrate + st_hourrate + st_taxrate);
                Toast.makeText(getApplicationContext(), ""+st_basicrate + st_hourrate + st_taxrate, Toast.LENGTH_SHORT).show();
                if (isValidAllField()) {

                    new apiCallForSetRate().execute();
                }
            }
        });

        new GetRate().execute();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public boolean isValidAllField() {

        if (st_basicrate.equals("")) {
            Toast.makeText(this, "Please enter basic rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Float.parseFloat(st_basicrate) <= 0) {
            Toast.makeText(this, "Invalid basic rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (st_hourrate.equals("")) {
            Toast.makeText(this, "Please enter hour rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Float.parseFloat(st_hourrate) <= 0) {
            Toast.makeText(this, "Invalid hour rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (st_taxrate.equals("")) {
            Toast.makeText(this, "Please enter tax rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Float.parseFloat(st_taxrate) <= 0) {
            Toast.makeText(this, "Invalid tax rate", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void init() {
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(clickListener);

        setupAdMob();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.img_back:
                    finish();
                    break;
            }
        }
    };


    public class apiCallForSetRate extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
            progressDialog.setMessage(getString(R.string.please_wait));
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
                request_main.put("user_id", prefsUtil.GetUserID());
                request_main.put("basic_rate", st_basicrate);
                request_main.put("hour_rate", st_hourrate);
                request_main.put("tax_rate", st_taxrate);

                MyLogs.e("URL", BaseUrl.URL + "Job_rates/add_rate");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "Job_rates/add_rate")
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

                Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_LONG).show();

            } else {

                try {

                    JSONObject jsonObject = new JSONObject(s);
                    MyLogs.e("Response", jsonObject.toString());
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equalsIgnoreCase("Yes")) {
//                        JSONObject data = jsonObject.getJSONObject("data");
//                        String jobrate_id = data.getString("jobrate_id");

                        AlertDialog.Builder builder = new AlertDialog.Builder(JobRateActivity.this);
                        builder.setTitle(R.string.app_name);
                        builder.setMessage(message);
                        builder.setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                        builder.create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class GetRate extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
            progressDialog.setMessage(getString(R.string.please_wait));
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
                request_main.put("user_id", prefsUtil.GetUserID());

                MyLogs.e("URL", BaseUrl.URL + "Job_rates/get_rate");
                MyLogs.e("signup", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "Job_rates/get_rate")
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

                Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);

                    MyLogs.e("signupreturn", String.valueOf(jsonObject));

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("Yes")) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        String jobrate_id = data.getString("jobrate_id");

                        String hour_rate = data.getString("hour_rate");
                        String tax_rate = data.getString("tax_rate");
                        String basic_rate = data.getString("basic_rate");

                        et_basicrate.setText(basic_rate);
                        et_hourrate.setText(hour_rate);
                        edt_tax_rate.setText(tax_rate);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }
}
