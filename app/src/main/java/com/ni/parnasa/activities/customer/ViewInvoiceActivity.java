package com.ni.parnasa.activities.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.AddRatingActivity;
import com.ni.parnasa.activities.WebPaymentForJobActivity;
import com.ni.parnasa.pojos.ViewInvoicePojo;
import com.ni.parnasa.pojos.ViewInvoicePojoItem;
import com.ni.parnasa.tmpPojos.PaymentModePojo;
import com.ni.parnasa.tmpPojos.PaypalPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.ConvertCurrencyAPI;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.ni.parnasa.utils.PrefsUtil;

public class ViewInvoiceActivity extends AppCompatActivity implements View.OnClickListener {

    // john na completed ma 9 july
    private Context mContext;
    private PrefsUtil prefsUtil;

    private ImageView imgBack;
    private TextView txtButtonPay, txtButtonPaypal, txtJobBookingId, txtBasicRate, txtWorkingHour, txtWorkingHourRate, txtPartName, txtPartRate,
            txtTax, txtDiscount, txtTip, txtGrandTotal, txtSubTotal, lblTip, txtPayBy, txtNoPaymentOption;
    private EditText edtTip;
    private RelativeLayout relBottomButton;

    private float GRAND_TOTAL = 0.0f;
    private int REQ_CODE_PAYPAL = 68;
    private ArrayList<String> param, value;

    private String jobId = "", secondUserId = "", tip = "", paymentStatus = "";
    private String professionalReview = "", currency;
    private int rating = 0;
    private boolean isNeedToHideFavoriteBtn;
    private String strTip;
    private ViewInvoicePojoItem pojoItem;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invoice);

        mContext = ViewInvoiceActivity.this;
        mActivity = this;
        prefsUtil = new PrefsUtil(mContext);

        currency = PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL);
        jobId = getIntent().getStringExtra("jobId");
        secondUserId = getIntent().getStringExtra("secondUserId");
        professionalReview = getIntent().getStringExtra("professionalReview");
        rating = getIntent().getIntExtra("rating", 5);
        isNeedToHideFavoriteBtn = getIntent().getBooleanExtra("isNeedToHideFavoriteBtn", false);

        initViews();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        txtButtonPay = findViewById(R.id.txtButtonPay);
        txtButtonPaypal = findViewById(R.id.txtButtonPaypal);
//        txtJobId = findViewById(R.id.txtJobId);
        txtJobBookingId = findViewById(R.id.txtJobBookingId);
        txtBasicRate = findViewById(R.id.txtBasicRate);
        txtWorkingHour = findViewById(R.id.txtWorkingHour);
        txtWorkingHourRate = findViewById(R.id.txtWorkingHourRate);
        txtPartName = findViewById(R.id.txtPartName);
        txtPartRate = findViewById(R.id.txtPartRate);
        txtTax = findViewById(R.id.txtTax);
//        txtTaxRate = findViewById(R.id.txtTaxRate);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtTip = findViewById(R.id.txtTip);
//        txtTotal = findViewById(R.id.txtTotal);
        txtGrandTotal = findViewById(R.id.txtGrandTotal);
//        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);

        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtPayBy = findViewById(R.id.txtPayBy);
        txtNoPaymentOption = findViewById(R.id.txtNoPaymentOption);
        edtTip = findViewById(R.id.edtTip);
        lblTip = findViewById(R.id.lblTip);
        relBottomButton = findViewById(R.id.relBottomButton);

        imgBack.setOnClickListener(this);
        txtButtonPay.setOnClickListener(this);
        txtButtonPaypal.setOnClickListener(this);

        apiCallForGetInvoice();

        edtTip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    int tmp = Integer.parseInt(s.toString());
                    double total = (GRAND_TOTAL + tmp);
                    txtGrandTotal.setText(currency + " " +String.format("%.2f",total) );
                } else {
                    txtGrandTotal.setText(currency + " " + GRAND_TOTAL);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setupAdMob();
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == txtButtonPay) {
            Intent intent = new Intent(mContext, AddRatingActivity.class);
            intent.putExtra("jobId", jobId);
            intent.putExtra("secondUserId", secondUserId);
            intent.putExtra("isFromCashPayment", true);
            intent.putExtra("professionalReview", professionalReview);
            intent.putExtra("ratting", rating);
            intent.putExtra("isNeedToHideFavoriteBtn", isNeedToHideFavoriteBtn);
            if (paymentStatus.equalsIgnoreCase("Pending")) {
                tip = edtTip.getText().toString().trim();
            }
            intent.putExtra("tip", tip);
            intent.putExtra("total", txtGrandTotal.getText().toString().trim());
            startActivity(intent);
            finish();

        } else if (v == txtButtonPaypal) {
            apiCallForPayment("Paypal");
        }
    }

    private void apiCallForGetInvoice() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(jobId);

        new ParseJSON(mContext, BaseUrl.getInvoice, param, value, ViewInvoicePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    ViewInvoicePojoItem pojoItem = ((ViewInvoicePojo) obj).getViewInvoicePojoItem();
                    paymentStatus = pojoItem.getPaymentStatus();
                    ViewInvoiceActivity.this.pojoItem = pojoItem;

//                    txtJobId.setText(pojoItem.getJobId());
                    txtJobBookingId.setText(pojoItem.getJobBookingId());

//                    txtTax.setText(currency + " " + pojoItem.getTax());
//                    txtTaxRate.setText(pojoItem.getTaxRate());


                    if (paymentStatus.equalsIgnoreCase("Pending")) {
                        edtTip.setVisibility(View.VISIBLE);
                        txtPayBy.setVisibility(View.VISIBLE);
//                        relBottomButton.setVisibility(View.VISIBLE);

                        apiCallForGetPaymentModeOfProfessional(pojoItem.getProfessionalId());

                    } else {
                        lblTip.setText(getString(R.string.tip_msg));
                        edtTip.setVisibility(View.GONE);
                        txtPayBy.setVisibility(View.GONE);
//                        relBottomButton.setVisibility(View.GONE);
                        txtButtonPay.setVisibility(View.GONE);
                        txtButtonPaypal.setVisibility(View.GONE);
                    }

                    double basicRates = Double.parseDouble(pojoItem.getGrandTotal());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    txtSubTotal.setText(ViewInvoiceActivity.this.currency + " " + String.format("%.2f", basicRates));

                    basicRates = Double.parseDouble(pojoItem.getGrandTotal());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    txtGrandTotal.setText(ViewInvoiceActivity.this.currency + " " + String.format("%.2f", basicRates));


                    txtWorkingHour.setText(pojoItem.getWorkingHour());
                    txtPartName.setText(pojoItem.getPartsDicers());

                    basicRates = Double.parseDouble(pojoItem.getBasicRate());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    txtBasicRate.setText(ViewInvoiceActivity.this.currency + " " + String.format("%.2f", basicRates));

                    basicRates = Double.parseDouble(pojoItem.getWorkingHourRate());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    txtWorkingHourRate.setText(ViewInvoiceActivity.this.currency + " " + String.format("%.2f", basicRates));

                    basicRates = Double.parseDouble(pojoItem.getPartsDicersRate());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    txtPartRate.setText(ViewInvoiceActivity.this.currency + " " + String.format("%.2f", basicRates));

                    basicRates = Double.parseDouble(pojoItem.getDiscount());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    txtDiscount.setText(ViewInvoiceActivity.this.currency + " " + String.format("%.2f", basicRates));

                    basicRates = Double.parseDouble(pojoItem.getTaxRate());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    txtTax.setText(ViewInvoiceActivity.this.currency + " " + String.format("%.2f", basicRates));

                    basicRates = Double.parseDouble(pojoItem.getTip());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    txtTip.setText(ViewInvoiceActivity.this.currency + " " + String.format("%.2f", basicRates));

                    if (!pojoItem.getGrandTotal().equals("")) {
                        GRAND_TOTAL = Float.valueOf(pojoItem.getGrandTotal());
                    }

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void covertedCurrency() {
        new ConvertCurrencyAPI((Activity) mContext, pojoItem.getBasicRate(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                convertedCurrency = String.format("%.2f", currency);
                Log.e("TAG", "onSuccessListener: " + convertedCurrency);
                txtBasicRate.setText(ViewInvoiceActivity.this.currency + " " + convertedCurrency);
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });

        new ConvertCurrencyAPI((Activity) mContext, pojoItem.getWorkingHourRate(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                convertedCurrency = String.format("%.2f", currency);
                txtWorkingHourRate.setText(ViewInvoiceActivity.this.currency + " " + convertedCurrency);
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });

        new ConvertCurrencyAPI((Activity) mContext, pojoItem.getPartsDicersRate(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                convertedCurrency = String.format("%.2f", currency);
                txtPartRate.setText(ViewInvoiceActivity.this.currency + " " + convertedCurrency);
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });

        new ConvertCurrencyAPI((Activity) mContext, pojoItem.getTax(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                convertedCurrency = String.format("%.2f", currency);
                txtTax.setText(ViewInvoiceActivity.this.currency + " " + convertedCurrency);
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });
        new ConvertCurrencyAPI((Activity) mContext, pojoItem.getDiscount(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                convertedCurrency = String.format("%.2f", currency);
                txtDiscount.setText(ViewInvoiceActivity.this.currency + " " + convertedCurrency);
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });

        new ConvertCurrencyAPI((Activity) mContext, pojoItem.getTip().equals("") ? "0" : pojoItem.getTip(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                convertedCurrency = String.format("%.2f", currency);
                txtTip.setText(ViewInvoiceActivity.this.currency + " " + convertedCurrency);
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });
//                    txtTip.setText(currency + " " + (pojoItem.getTip().equals("") ? "0" : pojoItem.getTip()));

        new ConvertCurrencyAPI((Activity) mContext, pojoItem.getTotal(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                convertedCurrency = String.format("%.2f", currency);
                txtSubTotal.setText(ViewInvoiceActivity.this.currency + " " + convertedCurrency);
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });

        new ConvertCurrencyAPI((Activity) mContext, pojoItem.getGrandTotal(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                convertedCurrency = String.format("%.2f", currency);
                txtGrandTotal.setText(ViewInvoiceActivity.this.currency + " " + convertedCurrency);
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });

    }

    private void apiCallForGetPaymentModeOfProfessional(String professionalId) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(professionalId);

        new ParseJSON(mContext, BaseUrl.URL + "user/get_payment_mode", param, value, PaymentModePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                if (status) {

                    PaymentModePojo pojo = (PaymentModePojo) obj;

                    String strPaymentMode = pojo.getPaymentModePojoItem().getPaymentMode();

                    /*if (myList.size() == 0) {

                        if (strPaymentMode.equalsIgnoreCase("Cash")) {
                            txtButtonPay.setVisibility(View.VISIBLE);
                        } else if (strPaymentMode.equalsIgnoreCase("Paypal")) {
                            txtButtonPaypal.setVisibility(View.VISIBLE);
                        }
                    } else {*/

                    if (strPaymentMode.equals("")) {
                        txtPayBy.setVisibility(View.VISIBLE);
                        relBottomButton.setVisibility(View.VISIBLE);
                        txtNoPaymentOption.setVisibility(View.VISIBLE);
                    } else {
                        List<String> myList = new ArrayList<>(Arrays.asList(strPaymentMode.split(",")));
                        for (int i = 0; i < myList.size(); i++) {
                            String payitem = myList.get(i).trim();

                            if (payitem.equalsIgnoreCase("Cash")) {
                                txtButtonPay.setVisibility(View.VISIBLE);
                            } else if (payitem.equalsIgnoreCase("Paypal")) {
                                txtButtonPaypal.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    //}
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apiCallForPayment(final String paymentType) {

        strTip = edtTip.getText().toString().trim();

        param = new ArrayList<>();
        value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(getIntent().getStringExtra("jobId"));

//        new ConvertCurrencyAPI((Activity) mContext, strTip, "USD", PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), new ConvertCurrencyAPI.OnCompletedListener() {
//            @Override
//            public void onSuccessListener(String convertedCurrency) {
//        double currency = Double.parseDouble(convertedCurrency);
//                convertedCurrency = String.format("%.2f", currency);
//                strTip = convertedCurrency;
        param.add("tip");
        value.add(strTip.equals("") ? "0" : strTip);

        param.add("payment_type");
        value.add(paymentType);

        param.add("currency_code");
        value.add(PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE));


        new ParseJSON(mContext, BaseUrl.payInvoice, param, value, PaypalPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                if (status) {

                    PaypalPojo pojo = (PaypalPojo) obj;

                    if (paymentType.equalsIgnoreCase("Paypal")) {

                        Intent intent = new Intent(mContext, WebPaymentForJobActivity.class);
                        intent.putExtra("paypalUrl", pojo.getPaypalPojoItem().getRedirectUrl());
                        startActivityForResult(intent, REQ_CODE_PAYPAL);
                        finish();
                    } else {
                        relBottomButton.setVisibility(View.GONE);
                        txtPayBy.setVisibility(View.GONE);
                        edtTip.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//            @Override
//            public void onFailureResponse(String error) {
//
//            }
//        });
//
//
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_PAYPAL) {
            if (resultCode == Activity.RESULT_OK) {
                relBottomButton.setVisibility(View.GONE);
                txtPayBy.setVisibility(View.GONE);
                edtTip.setVisibility(View.GONE);
            } else {
                Toast.makeText(mContext, R.string.payment_canceled, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_view_invoice_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }
}
