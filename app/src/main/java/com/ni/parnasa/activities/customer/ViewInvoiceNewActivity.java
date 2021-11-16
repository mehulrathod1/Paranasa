package com.ni.parnasa.activities.customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ni.parnasa.R;
import com.ni.parnasa.databinding.ActivityViewInvoiceNewBinding;
import com.ni.parnasa.models.JobInvoiceDetail;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.ni.parnasa.utils.PrefsUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ViewInvoiceNewActivity extends AppCompatActivity {

    private ActivityViewInvoiceNewBinding binding;
    private ProgressDialog progressDialog;
    private Activity mActivity;
    private PrefsUtil prefsUtil;
    private JobInvoiceDetail invoiceDetail;

    private String basicRate, hourRate, partRate, discount, total, grandTotal, taxRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_invoice_new);
        initViews();
        allClickListeners();
    }

    private void allClickListeners() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        mActivity = this;
        prefsUtil = new PrefsUtil(mActivity);
        progressDialog = new ProgressDialog(mActivity);
        invoiceDetail = (JobInvoiceDetail) getIntent().getSerializableExtra("invoiceDetail");
        new CallAPIViewInvoice().execute();
    }

    public class CallAPIViewInvoice extends AsyncTask<String, Void, String> {

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
                request_main.put("device_auth_token", prefsUtil.getDeviceAuthToken());
                request_main.put("job_id", getIntent().getStringExtra("jobId"));

                MyLogs.e("URL", BaseUrl.URL + "Allinvoices");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "Allinvoices")
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

                Toast.makeText(mActivity, R.string.msg_network_error, Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    setText(jsonObject.getString("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void setText(String s) {
//        s = s.replace("$", PrefUtils.with(mActivity).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));

//        double basicRates = Double.parseDouble(invoiceDetail.getBasicRate());
//        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
//        basicRate = String.format("%.2f", basicRates);
//
//        basicRates = Double.parseDouble(invoiceDetail.getWorkingHourRate());
//        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
//        hourRate = String.format("%.2f", basicRates);
//
//        basicRates = Double.parseDouble(invoiceDetail.getPartsRate());
//        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
//        partRate = String.format("%.2f", basicRates);
//
//        basicRates = Double.parseDouble(invoiceDetail.getDiscount());
//        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
//        discount = String.format("%.2f", basicRates);
//
//        basicRates = Double.parseDouble(invoiceDetail.getTaxRate());
//        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
//        taxRate = String.format("%.2f", basicRates);
//
//        basicRates = Double.parseDouble(invoiceDetail.getTotal());
//        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
//        total = String.format("%.2f", basicRates);
//
//        basicRates = Double.parseDouble(invoiceDetail.getGrandTotal());
//        basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
//        grandTotal = String.format("%.2f", basicRates);
//

//        s = s.replace(invoiceDetail.getBasicRate(),basicRate);
//        s = s.replace(invoiceDetail.getWorkingHourRate(),hourRate);
//        s = s.replace(invoiceDetail.getPartsRate(),partRate);
//        s = s.replace(invoiceDetail.getTaxRate(),taxRate);
//        s = s.replace(invoiceDetail.getDiscount(),discount);
//        s = s.replace(invoiceDetail.getTotal(),total);
//        s = s.replace(invoiceDetail.getGrandTotal(),grandTotal);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.txtInvoice.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.txtInvoice.setText(Html.fromHtml(s));
        }

        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadDataWithBaseURL(null, s, "text/html; charset=utf-8", "UTF-8", null);


    }
}