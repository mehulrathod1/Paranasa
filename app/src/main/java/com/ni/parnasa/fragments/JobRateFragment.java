package com.ni.parnasa.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.databinding.FragmentJobRateBinding;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FacebookAdLoader;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.ni.parnasa.utils.PrefsUtil;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class JobRateFragment extends Fragment {

    private ProgressDialog progressDialog;
    private PrefsUtil prefsUtil;
    private EditText et_basicrate, et_hourrate, edt_tax_rate;
    private Button bt_save, btn_cancel;
    private String st_basicrate, st_hourrate, st_taxrate;

    private FragmentJobRateBinding binding;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_job_rate, container, false);
        View view = binding.getRoot();

        if (getActivity() instanceof CustomerHomeActivity) {
            prefsUtil = ((CustomerHomeActivity) getActivity()).prefsUtil;
        } else {
            prefsUtil = ((ProfessionalHomeActivity) getActivity()).prefsUtil;
        }

        et_basicrate = view.findViewById(R.id.edt_basic_rate);
        et_hourrate = view.findViewById(R.id.edt_hour_rate);
        edt_tax_rate = view.findViewById(R.id.edt_tax_rate);
        bt_save = view.findViewById(R.id.btn_save);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        progressDialog = new ProgressDialog(getActivity());

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                st_basicrate = et_basicrate.getText().toString().trim();
                st_hourrate = et_hourrate.getText().toString().trim();
                st_taxrate = edt_tax_rate.getText().toString().trim();

                MyLogs.e("newTag", st_basicrate + st_hourrate + st_taxrate);


                if (isValidAllField()) {

//                    new ConvertCurrencyAPI(mActivity, st_basicrate, "USD", PrefUtils.with(mActivity).readString(PrefConstant.COUNTRY_CURRENCY_CODE), new ConvertCurrencyAPI.OnCompletedListener() {
//                        @Override
//                        public void onSuccessListener(String convertedCurrency) {
//
//                            double currency = Double.parseDouble(convertedCurrency);
//                            st_basicrate = String.format("%.2f",currency);
//                            new ConvertCurrencyAPI(mActivity, st_hourrate, "USD", PrefUtils.with(mActivity).readString(PrefConstant.COUNTRY_CURRENCY_CODE), new ConvertCurrencyAPI.OnCompletedListener() {
//                                @Override
//                                public void onSuccessListener(String convertedCurrency) {
//
//                                    double currency = Double.parseDouble(convertedCurrency);
//                                    st_hourrate = String.format("%.2f",currency);
//                                    new apiCallForSetRate().execute();
//                                }
//
//                                @Override
//                                public void onFailureResponse(String error) {
//
//                                }
//                            });
//                        }

//                        @Override
//                        public void onFailureResponse(String error) {
//
//                        }
//                    });


    /*                double basicRates = Double.parseDouble(st_basicrate);
                    basicRates = basicRates / Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    st_basicrate = String.format("%.2f", basicRates);

                    basicRates = Double.parseDouble(st_hourrate);
                    basicRates = basicRates / Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    st_hourrate = String.format("%.2f", basicRates);
*/


                    new apiCallForSetRate().execute();
                }
            }
        });

        new GetRate().execute();

        mActivity = getActivity();
        /*btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        binding.txtCurrencyBasicRate.setText(PrefUtils.with(mActivity).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));
        binding.txtCurrencyHourRate.setText(PrefUtils.with(mActivity).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));

        setupAdMob(view);

        return view;
    }

    public boolean isValidAllField() {

        if (st_basicrate.equals("")) {
            Toast.makeText(getActivity(), "Please enter basic rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Float.parseFloat(st_basicrate) <= 0) {
            Toast.makeText(getActivity(), "Invalid basic rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (st_hourrate.equals("")) {
            Toast.makeText(getActivity(), "Please enter hour rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Float.parseFloat(st_hourrate) <= 0) {
            Toast.makeText(getActivity(), "Invalid hour rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (st_taxrate.equals("")) {
            Toast.makeText(getActivity(), "Please enter tax rate", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Float.parseFloat(st_taxrate) <= 0) {
            Toast.makeText(getActivity(), "Invalid tax rate", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

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
            final String[] Response1 = {null};

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            final OkHttpClient client = new OkHttpClient();
            try {

                final JSONObject request_main = new JSONObject();
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

                okhttp3.Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Response1[0] = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Response1[0];
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
                    MyLogs.e("Response", jsonObject.toString());
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equalsIgnoreCase("Yes")) {
//                        JSONObject data = jsonObject.getJSONObject("data");
//                        String jobrate_id = data.getString("jobrate_id");

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.app_name);
                        builder.setMessage(message);
                        builder.setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
//                                        onBackPressed();
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

                Toast.makeText(getActivity(), R.string.msg_network_error, Toast.LENGTH_LONG).show();

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


                        MyLogs.e("mydesimal", hour_rate);
                        MyLogs.e("mydesimal", tax_rate);
                        MyLogs.e("mydesimal", basic_rate);


//                        Float litersOfPetrol = Float.parseFloat(tax_rate);
//                        DecimalFormat df = new DecimalFormat("0.00");
//                        df.setMaximumFractionDigits(2);
//                        tax_rate = df.format(litersOfPetrol);


 /*                       double basicRates = Double.parseDouble(basic_rate);
                        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                        et_basicrate.setText(String.format("%.2f", basicRates));

                        basicRates = Double.parseDouble(hour_rate);
                        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                        et_hourrate.setText(String.format("%.2f", basicRates));

   */


//                        new ConvertCurrencyAPI(mActivity, basic_rate, PrefUtils.with(mActivity).readString(PrefConstant.COUNTRY_CURRENCY_CODE),"USD",new ConvertCurrencyAPI.OnCompletedListener() {
//                            @Override
//                            public void onSuccessListener(String convertedCurrency) {
//                                double currency = Double.parseDouble(convertedCurrency);
//                                convertedCurrency = String.format("%.2f",currency);
//                                et_basicrate.setText(convertedCurrency);
//                            }
//
//                            @Override
//                            public void onFailureResponse(String error) {
//                                et_basicrate.setText("0");
//                            }
//                        });
//
//                        new ConvertCurrencyAPI(mActivity, hour_rate, PrefUtils.with(mActivity).readString(PrefConstant.COUNTRY_CURRENCY_CODE),"USD",new ConvertCurrencyAPI.OnCompletedListener() {
//                            @Override
//                            public void onSuccessListener(String convertedCurrency) {
//
//                                double currency = Double.parseDouble(convertedCurrency);
//                                convertedCurrency = String.format("%.2f",currency);
//                                et_hourrate.setText(convertedCurrency);
//                            }
//
//                            @Override
//                            public void onFailureResponse(String error) {
//                                et_hourrate.setText("0");
//                            }
//                        });

                        edt_tax_rate.setText(tax_rate);
                        et_basicrate.setText(basic_rate);
                        et_hourrate.setText(hour_rate);

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

        new GoogleAdLoader(getActivity(), getString(R.string.admob_job_rate_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
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
