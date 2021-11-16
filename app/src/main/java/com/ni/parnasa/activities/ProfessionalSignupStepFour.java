package com.ni.parnasa.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.tmpPojos.SubscriptionFeePojo;
import com.ni.parnasa.tmpPojos.SubscriptionFeePojoItem;
import com.ni.parnasa.tmpPojos.SubscriptionPojo;
import com.ni.parnasa.tmpPojos.SubscriptionPojoItem;
import com.ni.parnasa.tmpPojos.SubscriptionStripePojo;
import com.ni.parnasa.tmpPojos.SubscriptionStripePojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FingerPrintPrefUtil;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.SaveDataUtility;

public class ProfessionalSignupStepFour extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private String userId, fName, lName, gender, profilePic, userRole = "", is_monthly = "n", plan_type = "silver";
    private boolean isFromSocial;
    private String st_paymentmode, strMonthlyFee = "", strYearlyFee = "";
    private int REQ_CODE_PAYPAL = 56, REQ_CODE_STRIPE = 57, sub_plan_id;
    private boolean isYearly = false;

    private RadioButton rdbSilverMonthly, rdbSilverYearly, rdbSilvertype, rdbGoldtype;
    private TextView lblBasicMin;
    private Button btnSave;
    private RadioGroup rdbGrbSilver, rdbGrbPlantype;

    private SubscriptionFeePojoItem subscriptionFeePojoItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_signup_step_four);

        mContext = ProfessionalSignupStepFour.this;

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        fName = intent.getStringExtra("fName");
        lName = intent.getStringExtra("lName");
        gender = intent.getStringExtra("gender");
        profilePic = intent.getStringExtra("profilePic");
        userRole = intent.getStringExtra("userRole");
        isFromSocial = intent.getBooleanExtra("isFromSocial", false);

        MyLogs.e("iddddd",userId);
        initViews();

        apiCallForGetSilverSubscriptionFeeDetail();

        rdbSilvertype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    rdbGoldtype.setChecked(false);
                    apiCallForGetSilverSubscriptionFeeDetail();
                }
            }
        });

        rdbGoldtype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    rdbSilvertype.setChecked(false);
                    apiCallForGetSilverSubscriptionFeeDetail();
                }
            }
        });

    }

    private void initViews() {
        rdbSilverMonthly = findViewById(R.id.rdbSilverMonthly);
        rdbSilverYearly = findViewById(R.id.rdbSilverYearly);
        lblBasicMin = findViewById(R.id.lblBasicMin);
        btnSave = findViewById(R.id.btn_savepay);
        rdbGrbSilver = findViewById(R.id.rdbGrbSilver);

        rdbSilvertype = findViewById(R.id.rdbSilver);
        rdbGoldtype = findViewById(R.id.rdbGold);
        rdbGrbPlantype = findViewById(R.id.rdbGrbPlantype);

        btnSave.setOnClickListener(this);

        rdbSilvertype.setChecked(true);

        rdbSilverMonthly.setChecked(true);
    }

    private void apiCallForGetSilverSubscriptionFeeDetail() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(userId);

        new ParseJSON(mContext, BaseUrl.getSubscriptionFeeDetail, param, value, SubscriptionFeePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                if (status) {
                    subscriptionFeePojoItem = ((SubscriptionFeePojo) obj).getSubscriptionFeePojoItem();

                    if (rdbSilvertype.isChecked()) {
                        strMonthlyFee = subscriptionFeePojoItem.getMonthlySubscriptionFeeSilver();
                        strYearlyFee = subscriptionFeePojoItem.getYearlySubscriptionFeeSilver();

                        lblBasicMin.setText(getString(R.string.currency) + subscriptionFeePojoItem.getMonthlySubscriptionFeeSilver() + " " + getString(R.string.basic_with_minimum));

                        rdbSilverMonthly.setText(getString(R.string.monthly) + " - " + getString(R.string.currency) + subscriptionFeePojoItem.getMonthlySubscriptionFeeSilver() + " " + getString(R.string.for_one) + " + " + subscriptionFeePojoItem.getFreeTrial() + " " + getString(R.string.months_free));
                        rdbSilverYearly.setText(getString(R.string.yearly) + " - " + getString(R.string.currency) + subscriptionFeePojoItem.getYearlySubscriptionFeeSilver() + " " + getString(R.string.offer_for) + " + " + subscriptionFeePojoItem.getFreeTrial() + " " + getString(R.string.months_free));

                    } else if (rdbGoldtype.isChecked()) {

                        strMonthlyFee = subscriptionFeePojoItem.getMonthlySubscriptionFeeGold();
                        strYearlyFee = subscriptionFeePojoItem.getYearlySubscriptionFeeGold();

                        lblBasicMin.setText(getString(R.string.currency) + subscriptionFeePojoItem.getMonthlySubscriptionFeeGold() + " " + getString(R.string.basic_with_minimum));

                        rdbSilverMonthly.setText(getString(R.string.monthly) + " - " + getString(R.string.currency) + subscriptionFeePojoItem.getMonthlySubscriptionFeeGold() + " " + getString(R.string.for_one) + " + " + subscriptionFeePojoItem.getFreeTrial() + " " + getString(R.string.months_free));
                        rdbSilverYearly.setText(getString(R.string.yearly) + " - " + getString(R.string.currency) + subscriptionFeePojoItem.getYearlySubscriptionFeeGold() + " " + getString(R.string.offer_for) + " + " + subscriptionFeePojoItem.getFreeTrial() + " " + getString(R.string.months_free));
                    }

                    sub_plan_id = Integer.parseInt(subscriptionFeePojoItem.getSubfeeId());

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btnSave) {

            if (rdbGrbSilver.getCheckedRadioButtonId() == R.id.rdbSilverMonthly && rdbGrbPlantype.getCheckedRadioButtonId() == R.id.rdbSilver) {
                isYearly = false;
                is_monthly = "y";
                plan_type = "silver";
            }
            if (rdbGrbSilver.getCheckedRadioButtonId() == R.id.rdbSilverMonthly && rdbGrbPlantype.getCheckedRadioButtonId() == R.id.rdbGold) {
                isYearly = false;
                is_monthly = "y";
                plan_type = "gold";
            } else if (rdbGrbSilver.getCheckedRadioButtonId() == R.id.rdbSilverYearly && rdbGrbPlantype.getCheckedRadioButtonId() == R.id.rdbGold) {
                isYearly = true;
                is_monthly = "n";
                plan_type = "gold";

            } else if (rdbGrbSilver.getCheckedRadioButtonId() == R.id.rdbSilverYearly && rdbGrbPlantype.getCheckedRadioButtonId() == R.id.rdbSilver) {
                isYearly = true;
                is_monthly = "n";
                plan_type = "silver";
            }

            if (subscriptionFeePojoItem != null) {
                if (isYearly && plan_type.equals("silver")) {
                    openDialogForPaymentOption(subscriptionFeePojoItem.getYearlySubscriptionFeeSilver());
                } else if (isYearly && plan_type.equals("gold")) {
                    openDialogForPaymentOption(subscriptionFeePojoItem.getYearlySubscriptionFeeGold());
                } else if (!isYearly && plan_type.equals("silver")) {
                    openDialogForPaymentOption(subscriptionFeePojoItem.getMonthlySubscriptionFeeSilver());

                } else if (!isYearly && plan_type.equals("gold")) {
                    openDialogForPaymentOption(subscriptionFeePojoItem.getMonthlySubscriptionFeeGold());
                }
            } else {
                Toast.makeText(mContext, R.string.validation_msg_of_subscription, Toast.LENGTH_SHORT).show();
                apiCallForGetSilverSubscriptionFeeDetail();
            }
        }
    }

    private void openDialogForPaymentOption(String planPrice) {

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
        lblMsg.setText(getString(R.string.dialog_msg_one) + " " + getString(R.string.currency) + planPrice + " " + getString(R.string.dialog_msg_two));
        txtButtonPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                apiCallForGetPaypalLink();


//                Log.e("log", "onClick: " + BaseUrl.authorised_key);
//                Log.e("log", "onClick: " + userId);
//                Log.e("log", "onClick: " + sub_plan_id);
//                Log.e("log", "onClick: " + is_monthly);
//                Log.e("log", "onClick: " + plan_type);

            }
        });

        txtButtonStripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                apiCallForGetStripeLink();

                Log.e("log", "onClick: " + BaseUrl.authorised_key);
                Log.e("log", "onClick: " + userId);
                Log.e("log", "onClick: " + sub_plan_id);
                Log.e("log", "onClick: " + is_monthly);
                Log.e("log", "onClick: " + plan_type);

            }
        });
        dialog.show();
    }

    private void apiCallForGetPaypalLink() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        /*param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());*/

        param.add("user_id");
        value.add(userId);

        param.add("subcription_plan_id");
        value.add("" + sub_plan_id);

        param.add("is_monthly");
        value.add("" + is_monthly);

        param.add("plan_type");
        value.add("" + plan_type);

        new ParseJSON(mContext, BaseUrl.subscriptionPayments, param, value, SubscriptionPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {


                if (status) {

                    SubscriptionPojoItem pojoItem = ((SubscriptionPojo) obj).getSubscriptionPojoItem();
                    Intent intent = new Intent(mContext, WebPaymentActivity.class);
                    intent.putExtra("paypalUrl", pojoItem.getWebviewUrl());
                    startActivityForResult(intent, REQ_CODE_PAYPAL);

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apiCallForGetStripeLink() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        /*param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());*/

        param.add("user_id");
        value.add(userId);

        param.add("subcription_plan_id");
        value.add("" + sub_plan_id);

        param.add("is_monthly");
        value.add("" + is_monthly);

        param.add("plan_type");
        value.add("" + plan_type);

        new ParseJSON(mContext, BaseUrl.subscriptionPaymentsFromStripe, param, value, SubscriptionStripePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                if (status) {
                    SubscriptionStripePojoItem pojoItem = ((SubscriptionStripePojo) obj).getSubscriptionStripePojoItem();
                    Intent intent = new Intent(mContext, WebPaymentActivity.class);
                    intent.putExtra("paypalUrl", pojoItem.getStripeUrl());
                    startActivityForResult(intent, REQ_CODE_STRIPE);

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
                apiCallForCompletionOfStep(data.getStringExtra("token"), "paypal");
            } else {
                Toast.makeText(mContext, R.string.some_thing_wrong, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQ_CODE_STRIPE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                apiCallForCompletionOfStep(data.getStringExtra("token"), "stripe");
            } else {
                Toast.makeText(mContext, R.string.some_thing_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void apiCallForCompletionOfStep(String transactionToken, String subscription_pay_mode) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("language");
        value.add("EN");

        param.add("user_id");
        value.add(userId);

        param.add("paypal_email");
        value.add("");

        param.add("payment_mode");
        value.add("");

        param.add("member_ship_type");
        value.add("");

        param.add("subscription_pay_mode");
        value.add(subscription_pay_mode); //paypal, stipe

        param.add("subscription_is_yearly");
        value.add(isYearly + "");

        if (subscription_pay_mode.equalsIgnoreCase("stripe")) {
            param.add("subscription_stripe_token");
            value.add(transactionToken);
        } else {
            param.add("paypal_agreement_token");
            value.add(transactionToken);
        }

        new ParseJSON(mContext, BaseUrl.signupStepFive, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    if (!isFromSocial) {
                        /** For new way fingerprint auth
                         * default isCompletedSignup values false but after completion of signup process it is true
                         * and we can check isCompletedSignup flag in login activity if it is true then display emailId prefix in edittext otherwise not
                         * */
                        FingerPrintPrefUtil.with(mContext).write(BaseUrl.isCompletedSignup, true);
                    }

                    gotoDashboard(false);

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method call after finger print process completed whether it is required or not
     */
    private void gotoDashboard(boolean isRequiredFingerAuth) {

        SaveDataUtility utility = new SaveDataUtility(mContext);
        utility.saveData(userId, fName, lName, gender, profilePic, userRole, isRequiredFingerAuth, true);

        Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
