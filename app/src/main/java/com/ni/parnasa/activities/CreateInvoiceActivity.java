package com.ni.parnasa.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.databinding.ActivityCreateInvoiceBinding;
import com.ni.parnasa.models.RatePojo;
import com.ni.parnasa.models.RatePojoItem;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.ConvertCurrencyAPI;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.ni.parnasa.utils.PrefsUtil;

public class CreateInvoiceActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityCreateInvoiceBinding binding;
    private Context mContext;
    private PrefsUtil prefsUtil;

    private Button btnSentToCustomer, btnCancel;
    private ImageView imgBack;
    private EditText edtBasicRate, edtHoursRate, edtWorkHours, edtPartName, edtPartRate, edtDiscount, edtTax, edtNotes;
    private String jobId = "", jobHours = "";
    private String basicRate,hourRate,partName,partRate,discount,workHours,tax,note;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_invoice);

        mContext = CreateInvoiceActivity.this;
        mActivity = this;
        prefsUtil = new PrefsUtil(mContext);

        jobId = getIntent().getStringExtra("jobId");
        jobHours = getIntent().getStringExtra("jobHours");

        initViews();

    }

    private void initViews() {
        btnSentToCustomer = findViewById(R.id.btnSentToCustomer);
        btnCancel = findViewById(R.id.btnCancel);

        imgBack = findViewById(R.id.imgBack);
        edtBasicRate = findViewById(R.id.edtBasicRate);
        edtHoursRate = findViewById(R.id.edtHoursRate);
        edtWorkHours = findViewById(R.id.edtWorkHours);
//        edtWorkMinutes = findViewById(R.id.edtWorkMinutes);
        edtPartName = findViewById(R.id.edtPartName);
        edtPartRate = findViewById(R.id.edtPartRate);
        edtDiscount = findViewById(R.id.edtDiscount);
        edtTax = findViewById(R.id.edtTax);
        edtNotes = findViewById(R.id.edtNotes);

        binding.txtCurrencyBasicRate.setText(PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));
        binding.txtCurrencyHourRate.setText(PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));
        binding.txtCurrencyDrives.setText(PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));
        binding.txtCurrencyDiscount.setText(PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL));

        if (!jobHours.equals("")) {
            String[] hm = jobHours.split(":");
            edtWorkHours.setText(hm[0] + ":" + hm[1]);
//            edtWorkMinutes.setText(hm[1]);
        }

        btnSentToCustomer.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        apiCallForGetRate();

        setupAdMob();
    }

    private void apiCallForGetRate() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        new ParseJSON(mContext, BaseUrl.getProfessionalRate, param, value, RatePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    RatePojoItem pojoItem = ((RatePojo) obj).getRatePojoItem();
                    MyLogs.e("mytxt",pojoItem.getTaxRate());

                    edtTax.setText(pojoItem.getTaxRate());
                    edtBasicRate.setText(pojoItem.getBasicRate());
                    edtHoursRate.setText(pojoItem.getHourRate());

//                    new ConvertCurrencyAPI((Activity) mContext, pojoItem.getBasicRate(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
//                        @Override
//                        public void onSuccessListener(String convertedCurrency) {
//
//                            double currency = Double.parseDouble(convertedCurrency);
//                            convertedCurrency  = String.format("%.2f",currency);
//                            edtBasicRate.setText(convertedCurrency);
//                        }
//
//                        @Override
//                        public void onFailureResponse(String error) {
//
//                        }
//                    });
//
//                    new ConvertCurrencyAPI((Activity) mContext, pojoItem.getHourRate(), PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), "USD", new ConvertCurrencyAPI.OnCompletedListener() {
//                        @Override
//                        public void onSuccessListener(String convertedCurrency) {
//                            double currency = Double.parseDouble(convertedCurrency);
//                            convertedCurrency = String.format("%.2f",currency);
//                            edtHoursRate.setText(convertedCurrency);
//                        }
//
//                        @Override
//                        public void onFailureResponse(String error) {
//
//                        }
//                    });

                    double basicRates = Double.parseDouble(pojoItem.getBasicRate());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    edtBasicRate .setText(String.format("%.2f", basicRates));

                    basicRates = Double.parseDouble(pojoItem.getHourRate());
                    basicRates = basicRates * Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                    edtHoursRate.setText(String.format("%.2f", basicRates));


                    edtTax.setText(pojoItem.getTaxRate());

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnSentToCustomer) {
            basicRate = edtBasicRate.getText().toString().trim();
            workHours = edtWorkHours.getText().toString().trim();
            hourRate = edtHoursRate.getText().toString().trim();
//            String workMinutes = edtWorkMinutes.getText().toString().trim();
            partName = edtPartName.getText().toString().trim();
            partRate = edtPartRate.getText().toString().trim();
            discount = edtDiscount.getText().toString().trim();
            tax = edtTax.getText().toString().trim();
            note = edtNotes.getText().toString().trim();

            if (isValidAllField(basicRate, workHours, "", partName, partRate, discount, tax, note, hourRate)) {

                double basicRates = Double.parseDouble(basicRate);
                basicRates = basicRates / Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                basicRate = String.format("%.2f", basicRates);

                basicRates = Double.parseDouble(hourRate);
                basicRates = basicRates / Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                hourRate = String.format("%.2f", basicRates);

                basicRates = Double.parseDouble(partRate);
                basicRates = basicRates / Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                partRate = String.format("%.2f", basicRates);

                basicRates = Double.parseDouble(discount);
                basicRates = basicRates / Double.parseDouble(PrefUtils.with(mActivity).readString(PrefConstant.CURRENT_PRICE));
                discount = String.format("%.2f", basicRates);

                apiCallForCreateInvoice(basicRate, workHours, "", partName, partRate, discount, tax, note, hourRate);

            }
        } else if (v == imgBack) {
            onBackPressed();
        } else if (v == btnCancel) {
            onBackPressed();
        }
    }

    private void convertCurrency() {
        new ConvertCurrencyAPI((Activity) mContext, basicRate, "USD", PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), new ConvertCurrencyAPI.OnCompletedListener() {
            @Override
            public void onSuccessListener(String convertedCurrency) {
                double currency = Double.parseDouble(convertedCurrency);
                basicRate = String.format("%.2f",currency);
                new ConvertCurrencyAPI((Activity) mContext, hourRate, "USD", PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), new ConvertCurrencyAPI.OnCompletedListener() {
                    @Override
                    public void onSuccessListener(String convertedCurrency) {
                        double currency = Double.parseDouble(convertedCurrency);
                        hourRate = String.format("%.2f",currency);
                        new ConvertCurrencyAPI((Activity) mContext, partRate, "USD", PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), new ConvertCurrencyAPI.OnCompletedListener() {
                            @Override
                            public void onSuccessListener(String convertedCurrency) {
                                double currency = Double.parseDouble(convertedCurrency);
                                partRate = String.format("%.2f",currency);
                                new ConvertCurrencyAPI((Activity) mContext, discount, "USD", PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE), new ConvertCurrencyAPI.OnCompletedListener() {
                                    @Override
                                    public void onSuccessListener(String convertedCurrency) {
                                        double currency = Double.parseDouble(convertedCurrency);
                                        discount = String.format("%.2f",currency);
                                        apiCallForCreateInvoice(basicRate, workHours, "", partName, partRate, discount, tax, note, hourRate);
                                    }

                                    @Override
                                    public void onFailureResponse(String error) {

                                    }
                                });
                            }

                            @Override
                            public void onFailureResponse(String error) {

                            }
                        });
                    }

                    @Override
                    public void onFailureResponse(String error) {

                    }
                });
            }

            @Override
            public void onFailureResponse(String error) {

            }
        });

    }

    private void apiCallForCreateInvoice(String basicRate, String workHours, String workMinutes, String partName, String partRate, String discount, String tax, String note, String hourRate) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(jobId);

        param.add("basic_rate");
        value.add(basicRate);

        param.add("hour_rate");
        value.add(hourRate);

        param.add("work_hours");
        value.add(workHours);
//        value.add(workHours + ":" + (workMinutes.equals("") ? "0" : workMinutes));

        param.add("parts_name");
        value.add(partName);

        param.add("parts_rate");
        value.add(partRate);

        param.add("discount");
        value.add((discount.equals("") ? "0" : discount));

        param.add("tax");
        value.add(tax);

        param.add("notes");
        value.add(note);

        new ParseJSON(mContext, BaseUrl.generateInvoice, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    setResult(Activity.RESULT_OK, null);
                    finish();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        * {
	"authorised_key":"{{PMA_AUTHORISED_KEY}}",
	"job_id":"",
	"basic_rate":"",
	"work_hours":"",
	"part_name":"",
	"parts_rate":"",
	"discount":"",
	"tax":""
}
        * */
    }

    public boolean isValidAllField(String basicRate, String workHours, String workMinutes, String partName, String partRate, String discount, String tax, String note, String hourRate) {

        if (basicRate.equals("")) {
            Toast.makeText(mContext, R.string.msg_basic_rate_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (tax.equals("")) {
            Toast.makeText(mContext, R.string.msg_hour_rate_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (workHours.equals("")) {
            Toast.makeText(mContext, R.string.msg_work_hour_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (partName.equals("")) {
            Toast.makeText(mContext, R.string.msg_part_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (partRate.equals("")) {
            Toast.makeText(mContext, R.string.msg_part_rate_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (tax.equals("")) {
            Toast.makeText(mContext, R.string.msg_tax_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_create_invoice_ad_id),linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }
}
