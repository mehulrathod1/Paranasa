package com.ni.parnasa.activities.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PaymentActivity extends AppCompatActivity {

    private Context mContext;
    private PrefsUtil prefsUtil;
    private ProgressDialog progressDialog;

    private ImageView imgBack;
    private CheckBox cb_cash, cb_paypal, cb_others, cb_wallet;
    private EditText edtPaypalId;
    private Button bt_save;
    private LinearLayout linlayPaypalID;

    private ArrayList<String> checkitem = new ArrayList<String>();
    private String[] mStrings;
    private String st_cash, st_paypal, st_wallet, st_others, st_paymentmode, strPaypalEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_screen);

        mContext = PaymentActivity.this;

        progressDialog = new ProgressDialog(PaymentActivity.this);
        prefsUtil = new PrefsUtil(PaymentActivity.this);

        mStrings = new String[4];

        cb_cash = findViewById(R.id.cash);
        cb_paypal = findViewById(R.id.paypal);
        cb_others = findViewById(R.id.other);
        cb_wallet = findViewById(R.id.wallet);
        bt_save = findViewById(R.id.btn_save_p);
        imgBack = findViewById(R.id.img_back);
        edtPaypalId = findViewById(R.id.edtPaypalId);
        linlayPaypalID = findViewById(R.id.linlayPaypalID);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkitem.clear();
                StringBuilder builder = new StringBuilder();

                if (cb_cash.isChecked()) {
//                    st_cash = cb_cash.getText().toString();
//                    checkitem.add("cash");
                    builder.append("cash,");
                }
                if (cb_paypal.isChecked()) {
//                    st_paypal = cb_paypal.getText().toString();
//                    checkitem.add("paypal");
                    builder.append("paypal,");
                }
                if (cb_wallet.isChecked()) {
//                    st_wallet = cb_wallet.getText().toString();
//                    checkitem.add("wallet");
                    builder.append("wallet,");
                }
                if (cb_others.isChecked()) {
//                    st_others = cb_others.getText().toString();
//                    checkitem.add("other");
                    builder.append("other,");
                }

                st_paymentmode = checkitem.toString();
//                st_paymentmode = st_paymentmode.replaceAll("\\[", "").replaceAll("\\]", "");

                String s = builder.toString();
                st_paymentmode = s.substring(0, s.length() - 1);
                MyLogs.w("TAG", "option checked : " + st_paymentmode);

                if (cb_paypal.isChecked()) {
                    strPaypalEmail = edtPaypalId.getText().toString().trim();
                    if (strPaypalEmail.equals("")) {
                        Toast.makeText(PaymentActivity.this, R.string.msg_paypal_validation, Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(strPaypalEmail).matches()) {
                        Toast.makeText(mContext, R.string.msg_email_invalid, Toast.LENGTH_SHORT).show();
                    } else {
                        new apiCallForUpdateDetail().execute();
                    }
                } else {
                    new apiCallForUpdateDetail().execute();
                }
            }
        });

        cb_paypal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linlayPaypalID.setVisibility(View.VISIBLE);
                } else {
                    linlayPaypalID.setVisibility(View.GONE);
                }
            }
        });

        new apiCallForGetDetail().execute();

        setupAdMob();
    }

    public class apiCallForGetDetail extends AsyncTask<String, Void, String> {

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

                MyLogs.e("PARAM", String.valueOf(request_main));


                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "user/get_payment_mode")
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

                        String payment_mode = data.getString("payment_mode");

                        strPaypalEmail = data.getString("paypal_email");
                        edtPaypalId.setText(strPaypalEmail);

                        List<String> myList = new ArrayList<>(Arrays.asList(payment_mode.split(",")));

                        for (int i = 0; i < myList.size(); i++) {
                            String payitem = myList.get(i).trim();

                            if (payitem.equalsIgnoreCase("Cash")) {
                                cb_cash.setChecked(true);
                            }
                            if (payitem.equalsIgnoreCase("Paypal")) {
                                cb_paypal.setChecked(true);
                                linlayPaypalID.setVisibility(View.VISIBLE);
                            }
                            if (payitem.equalsIgnoreCase("Wallet")) {
                                cb_wallet.setChecked(true);
                            }
                            if (payitem.equalsIgnoreCase("Other")) {
                                cb_others.setChecked(true);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class apiCallForUpdateDetail extends AsyncTask<String, Void, String> {

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
                request_main.put("payment_mode", st_paymentmode);
                request_main.put("paypal_email", (cb_paypal.isChecked() ? strPaypalEmail : ""));

                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "user/update_payment_mode")
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
                    Log.v("signupreturn", String.valueOf(jsonObject));
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("Yes")) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        String payment_mode = data.getString("payment_mode");

                        List<String> myList = new ArrayList<String>(Arrays.asList(payment_mode.split(",")));

                        for (int i = 0; i < myList.size(); i++) {
                            String payitem = myList.get(i);
                            System.out.println("payitem" + payitem);
                            if (payitem.equalsIgnoreCase("Cash")) {
                                cb_cash.setChecked(true);
                            }
                            if (payitem.equalsIgnoreCase("Paypal")) {
                                cb_paypal.setChecked(true);
                            }
                            if (payitem.equalsIgnoreCase("Wallet")) {
                                cb_wallet.setChecked(true);
                            }
                            if (payitem.equalsIgnoreCase("Other")) {
                                cb_others.setChecked(true);
                            }
                        }

                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PaymentActivity.this);
                        //final AlertDialog alert = builder.create();
                        builder.setTitle(R.string.app_name);

                        builder.setMessage(message);
                        builder.setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        final android.app.AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupAdMob() {

        /*View adContainer = findViewById(R.id.adMobView);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        param.addRule(RelativeLayout.CENTER_HORIZONTAL);

        RelativeLayout rel = (RelativeLayout) adContainer;

        AdView mAdView = new AdView(mContext);

        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.admob_unit_id));
        mAdView.setLayoutParams(param);
        rel.addView(mAdView);

//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(new GoogleAdLoader().getAdRequest());

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(mContext, "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });*/
    }
}
