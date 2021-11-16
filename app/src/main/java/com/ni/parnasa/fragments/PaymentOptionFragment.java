package com.ni.parnasa.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FacebookAdLoader;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PaymentOptionFragment extends Fragment {

    private PrefsUtil prefsUtil;
    private ProgressDialog progressDialog;

    private CheckBox cb_cash, cb_paypal, cb_others, cb_wallet;
    private EditText edtPaypalId;
    private Button bt_save;
    private LinearLayout linlayPaypalID;

    private ArrayList<String> checkitem = new ArrayList<String>();
    private String[] mStrings;
    private String st_paymentmode = "", strPaypalEmail = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_payment_option, container, false);

        progressDialog = new ProgressDialog(getActivity());

        if (getActivity() instanceof CustomerHomeActivity) {
            prefsUtil = ((CustomerHomeActivity) getActivity()).prefsUtil;
        } else {
            prefsUtil = ((ProfessionalHomeActivity) getActivity()).prefsUtil;
        }

        mStrings = new String[4];

        cb_cash = view.findViewById(R.id.cash);
        cb_paypal = view.findViewById(R.id.paypal);
        cb_others = view.findViewById(R.id.other);
        cb_wallet = view.findViewById(R.id.wallet);
        bt_save = view.findViewById(R.id.btn_save_p);

        edtPaypalId = view.findViewById(R.id.edtPaypalId);
        linlayPaypalID = view.findViewById(R.id.linlayPaypalID);


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
                if (!s.equals("")) {
                    st_paymentmode = s.substring(0, s.length() - 1);
                    MyLogs.w("TAG", "option checked : " + st_paymentmode);
                }

                if (cb_paypal.isChecked()) {
                    strPaypalEmail = edtPaypalId.getText().toString().trim();
                    if (strPaypalEmail.equals("")) {
                        Toast.makeText(getActivity(), R.string.msg_paypal_validation, Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(strPaypalEmail).matches()) {
                        Toast.makeText(getActivity(), R.string.msg_email_invalid, Toast.LENGTH_SHORT).show();
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

        setupAdMob(view);

        return view;
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

                Toast.makeText(getActivity(), R.string.msg_network_error, Toast.LENGTH_LONG).show();

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

                Toast.makeText(getActivity(), R.string.msg_network_error, Toast.LENGTH_LONG).show();

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

                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                        //final AlertDialog alert = builder.create();
                        builder.setTitle(R.string.app_name);
                        // builder.setIcon(R.drawable.pickme_serviceapp_logo1);
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

    private FacebookAdLoader facebookAdLoader;

    private void setupAdMob(View view) {

        /** Load Google Ad */
        LinearLayout linlayConteiner = view.findViewById(R.id.adMobView);

        new GoogleAdLoader(getActivity(), getString(R.string.admob_payment_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });

        /** Load facebook Ad */
        facebookAdLoader = new FacebookAdLoader(getActivity(), linlayConteiner, new FacebookAdLoader.CustomAdListenerFacebook() {
            @Override
            public void onError(Ad aad, AdError adError) {
                Log.e("facebookAd", "onError CODE :" + adError.getErrorCode() + " Message : " + adError.getErrorMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        if (facebookAdLoader != null) {
            facebookAdLoader.destroyAdView();
        }
        super.onDestroy();
    }
}
