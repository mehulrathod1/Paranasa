package com.ni.parnasa.activities.professional.owncompany;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.ProfessionalSignupStepFour;
import com.ni.parnasa.activities.WebPaymentActivity;
import com.ni.parnasa.models.country.CountryResponse;
import com.ni.parnasa.models.currencies.CurrencySymbolResponse;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.PayPapForFeePojo;
import com.ni.parnasa.pojos.PayPapForFeePojoItem;
import com.ni.parnasa.pojos.RegistrationFeePojo;
import com.ni.parnasa.pojos.RegistrationFeePojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.CommonUtils;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ProfileDetailsThree extends AppCompatActivity {

    private Context mContext;

    private Button bt_save;
    private ImageView img_back;
    private CheckBox cb_cash, cb_paypal, cb_wallet, cb_other;
    private EditText et_paypal_id;
    private CheckBox cb_gold;
    private ProgressDialog progressDialog;
    private RadioButton rdbSilverMonthly, rdbSilverYearly;
    private RadioGroup rdbGrbPayment, rdbGrbSilver;
    private TextView lblBasicMin, lblOfferSilver, txtRegistrationFee;

    private ArrayList<String> checkitem = new ArrayList<>();

    private String st_cash, st_paypal, st_wallet, st_others, strPaypalEmail = "", st_silver_gold;
    private String st_paymentmode, strMonthlyFee = "", strYearlyFee = "";

    private String userId, fName, lName, gender, profilePic, userRole = "", deviceAuthToken;
    private int REQ_CODE_PAYPAL = 99, REQ_CODE_STRIPE = 100;
    private boolean isFromSocial;
    private String registrationFee = "0", isRegistrationFeeZero = "yes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details_three);

        mContext = ProfileDetailsThree.this;

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        fName = intent.getStringExtra("fName");
        lName = intent.getStringExtra("lName");
        gender = intent.getStringExtra("gender");
        profilePic = intent.getStringExtra("profilePic");
        userRole = intent.getStringExtra("userRole");
        isFromSocial = intent.getBooleanExtra("isFromSocial", false);

        initViews();

        apiCallForGetRegistrationFeeDetail();
    }

    private void initViews() {
        progressDialog = new ProgressDialog(mContext);

        img_back = (ImageView) findViewById(R.id.img_back);
        cb_cash = (CheckBox) findViewById(R.id.cash);
        cb_paypal = (CheckBox) findViewById(R.id.paypal);
        cb_wallet = (CheckBox) findViewById(R.id.wallet);
        cb_other = (CheckBox) findViewById(R.id.other);
        et_paypal_id = (EditText) findViewById(R.id.paypal_id);
        rdbGrbPayment = findViewById(R.id.rdbGrbPayment);
        rdbGrbSilver = findViewById(R.id.rdbGrbSilver);
        rdbSilverMonthly = findViewById(R.id.rdbSilverMonthly);
        rdbSilverYearly = findViewById(R.id.rdbSilverYearly);
//        cb_silver = (CheckBox) findViewById(R.id.silver);
        cb_gold = (CheckBox) findViewById(R.id.gold);
        bt_save = (Button) findViewById(R.id.btn_savepay);
        lblBasicMin = findViewById(R.id.lblBasicMin);
        lblOfferSilver = findViewById(R.id.lblOfferSilver);
        txtRegistrationFee = findViewById(R.id.txtRegistrationFee);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkitem.clear();
                strPaypalEmail = et_paypal_id.getText().toString().trim();

                if (cb_cash.isChecked()) {
                    checkitem.add("cash");
                }
                if (cb_paypal.isChecked()) {
                    checkitem.add("paypal");
                }
                if (cb_wallet.isChecked()) {
                    checkitem.add("wallet");
                }
                if (cb_other.isChecked()) {
                    checkitem.add("other");
                }

                if (isValidAllField()) {
                    st_paymentmode = checkitem.toString();
                    st_paymentmode = st_paymentmode.replaceAll("\\[", "").replaceAll("\\]", "");
                    st_paymentmode = st_paymentmode.replaceAll(" ", "");

                    MyLogs.e("TAG", "accept : " + st_paymentmode);

                    if (!registrationFee.equals("")) {
                        if (!registrationFee.equals("0")) {
                            isRegistrationFeeZero = "no";
                            openDialogForPaymentFeeOption(registrationFee);
                        } else {
                            isRegistrationFeeZero = "yes";
                            apiCallForCompletionOfPayment("", "");
                        }
                    } else {
                        Toast.makeText(mContext, R.string.validation_registration_fee_msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (lastLocation != null) {
            new GeoCoderHelper(ProfileDetailsThree.this, lastLocation.getLatitude(), lastLocation.getLongitude(), new GeoCoderHelper.onGetAddress() {
                public void onSuccess(String address, String city, String country) {
                    String countryCurrencyAbbreviation = "USD";
                    String countryCurrencySymbol = "$";
                    String jsonCountry = CommonUtils.loadJSONFromAsset(ProfileDetailsThree.this, "countries.json");
                    String jsonCurrency = CommonUtils.loadJSONFromAsset(ProfileDetailsThree.this, "countryCurrency.json");

                    Gson gsonCountry = new Gson();
                    CountryResponse countryResponse = gsonCountry.fromJson(jsonCountry, CountryResponse.class);

                    Gson gsonCountryCurrency = new Gson();
                    CurrencySymbolResponse currencySymbolResponse = gsonCountryCurrency.fromJson(jsonCurrency, CurrencySymbolResponse.class);

                    List<CountryResponse.CountryItem> countryItems = countryResponse.getCountries().getCountry();

                    for (CountryResponse.CountryItem countryItem : countryItems) {
                        if (countryItem.getCountryName().equalsIgnoreCase(country)) {
                            countryCurrencyAbbreviation = countryItem.getCurrencyCode();
                            PrefUtils.with(ProfileDetailsThree.this).write(PrefConstant.COUNTRY_CURRENCY_CODE, countryItem.getCurrencyCode());
                            break;
                        }
                    }

                    String currencySymbol = "";
                    for (CurrencySymbolResponse.CurrenciesItem currency : currencySymbolResponse.getCurrencies()) {

                        Log.e("TAG", "onSuccess: "+currency.getAbbreviation() );
                        if (Build.VERSION.SDK_INT >= 24) {
                            currencySymbol = String.valueOf(Html.fromHtml(currency.getSymbol(), Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            currencySymbol = String.valueOf(Html.fromHtml(currency.getSymbol()));
                        }

                        Log.e("TAG", "onSuccess: "+currencySymbol );
                        if (countryCurrencyAbbreviation.equalsIgnoreCase(currency.getAbbreviation())) {
                            countryCurrencySymbol = currencySymbol;
                            PrefUtils.with(ProfileDetailsThree.this).write(PrefConstant.COUNTRY_CURRENCY_SYMBOL, countryCurrencySymbol);
                            break;
                        }
                    }

                    Log.e("TAG", "onSuccess: country::  "+country+" countryCurrencySymbol:: "+countryCurrencySymbol+ " countryCurrencyAbbreviation:: "+countryCurrencyAbbreviation );

                }

                @Override
                public void onFail() {
                    Toast.makeText(mContext, "can't fetch location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Unable to fetch address from current location", Toast.LENGTH_SHORT).show();
        }
    }

    private void apiCallForGetRegistrationFeeDetail() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(userId);

        new ParseJSON(mContext, BaseUrl.getRegisterFeeDetail, param, value, RegistrationFeePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    RegistrationFeePojoItem item = ((RegistrationFeePojo) obj).getRegistrationFeePojoItem();
                    registrationFee = item.getRegisterFee();
                    String code = PrefUtils.with(ProfileDetailsThree.this).readString(PrefConstant.COUNTRY_CURRENCY_SYMBOL);
                    txtRegistrationFee.setText(code + registrationFee);
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openDialogForPaymentFeeOption(String planPrice) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_for_payment_option);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        TextView txtPlanPrice = dialog.findViewById(R.id.planPrice);
        TextView lblMsg = dialog.findViewById(R.id.lblMsg);
        TextView txtButtonPaypal = dialog.findViewById(R.id.txtButtonPaypal);
        TextView txtButtonStripe = dialog.findViewById(R.id.txtButtonStripe);

        txtPlanPrice.setText(getString(R.string.currency) + planPrice);
//        lblMsg.setText(getString(R.string.dialog_msg_one) + " " + getString(R.string.currency) + planPrice + " " + getString(R.string.dialog_msg_two));
        lblMsg.setText(getString(R.string.registration_fee_msg));

        txtButtonPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                apiCallForGetPaypalLinkOfRegistrationFee();
            }
        });

        txtButtonStripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                apiCallForGetStripelLinkOfRegistrationFee();
            }
        });

        dialog.show();
    }

    public boolean isValidAllField() {

        if (checkitem.size() == 0) {
            Toast.makeText(mContext, R.string.msg_payment_mode_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (cb_paypal.isChecked()) {
            if (strPaypalEmail.equals("")) {
                Toast.makeText(mContext, R.string.msg_paypal_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void apiCallForGetStripelLinkOfRegistrationFee() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(userId);

        new ParseJSON(mContext, BaseUrl.registerPayments, param, value, PayPapForFeePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    PayPapForFeePojoItem pojoItem = ((PayPapForFeePojo) obj).getPayPapForFeePojoItem();

                    Intent intent = new Intent(mContext, WebPaymentActivity.class);
                    intent.putExtra("isFromRegistrationFee", true);
                    intent.putExtra("paypalUrl", pojoItem.getStripeUrl());
                    startActivityForResult(intent, REQ_CODE_STRIPE);
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void apiCallForGetPaypalLinkOfRegistrationFee() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(userId);

        new ParseJSON(mContext, BaseUrl.registerPayments, param, value, PayPapForFeePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    PayPapForFeePojoItem pojoItem = ((PayPapForFeePojo) obj).getPayPapForFeePojoItem();

                    Intent intent = new Intent(mContext, WebPaymentActivity.class);
                    intent.putExtra("isFromRegistrationFee", true);
                    intent.putExtra("paypalUrl", pojoItem.getPaypalUrl());
                    startActivityForResult(intent, REQ_CODE_PAYPAL);
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_PAYPAL && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                apiCallForCompletionOfPayment(data.getStringExtra("token"), "paypal");
            } else {
                Toast.makeText(mContext, R.string.some_thing_wrong, Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == REQ_CODE_STRIPE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                apiCallForCompletionOfPayment(data.getStringExtra("token"), "stripe");
            } else {
                Toast.makeText(mContext, R.string.some_thing_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void apiCallForCompletionOfPayment(String transactionToken, String registration_pay_mode) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("language");
        value.add("EN");

        param.add("user_id");
        value.add(userId);

        param.add("no_employee");
        value.add("");

        param.add("role");
        value.add(userRole);

        param.add("paypal_email");
        value.add(et_paypal_id.getText().toString());

        param.add("payment_mode");
        value.add(st_paymentmode);

        param.add("register_fee");
        value.add(registrationFee);

        param.add("registration_pay_mode");
        value.add(registration_pay_mode);

        if (registration_pay_mode.equalsIgnoreCase("stripe")) {
            param.add("registration_stripe_token");
            value.add(transactionToken);
        } else {
            param.add("paypal_agreement_token");
            value.add(transactionToken);
        }

        param.add("isRegisterFeeZero");
        value.add(isRegistrationFeeZero);

        new ParseJSON(mContext, BaseUrl.signupStepFoure, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    gotoNextScreen();

                    /*if (!isFromSocial) {
                     *//** For new way fingerprint auth
                     * default isCompletedSignup values false but after completion of signup process it is true
                     * and we can check isCompletedSignup flag in login activity if it is true then display emailId prefix in edittext otherwise not
                     * *//*
                        FingerPrintPrefUtil.with(mContext).write(BaseUrl.isCompletedSignup, true);
                    }
                    gotoDashboard(false);*/
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void gotoNextScreen() {

        Intent intent = new Intent(mContext, ProfessionalSignupStepFour.class);
        intent.putExtra("userId", userId);
        intent.putExtra("fName", fName);
        intent.putExtra("lName", lName);
        intent.putExtra("gender", gender);
        intent.putExtra("profilePic", profilePic);
        intent.putExtra("userRole", userRole);
        intent.putExtra("isFromSocial", isFromSocial);
        startActivity(intent);
        finish();
    }

    /**
     * This method call after finger print process completed whether it is required or not
     */
    /*private void gotoDashboard(boolean isRequiredFingerAuth) {

        SaveDataUtility utility = new SaveDataUtility(mContext);
        utility.saveData(userId, fName, lName, gender, profilePic, userRole, isRequiredFingerAuth, true);

        Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }*/
}
