package com.ni.parnasa.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.PaymentActivity;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;

import androidx.appcompat.app.AppCompatActivity;

public class DispatchActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView img_back;
    public static final int PAYPAL_REQUEST_CODE = 123;

    private Context mContext;
    Button bt_save;
    /*private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);

        mContext = DispatchActivity.this;

        init();
    }

    private void init() {
        img_back = (ImageView) findViewById(R.id.img_back);
        bt_save = findViewById(R.id.btn_save);
        img_back.setOnClickListener(this);

        setupAdMob();
    }


    @Override
    public void onClick(View v) {
        if (v == img_back) {
            onBackPressed();
        } else if (v == bt_save) {
            getPayment();
        }
    }

    /*View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.img_back:
                    finish();
                    break;
            }
        }
    };*/


    private void getPayment() {

        /**Update with paypal web*/

        /*Intent intent = new Intent(DispatchActivity.this,WebPaymentActivity.class);
        intent.putExtra("paypalUrl","");
        startActivity(intent);*/

        //Getting the amount from editText
        String paymentAmount = "200";
        //Log.v("priceeee",Price);

        //Creating a paypalpayment
       /* PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "Package Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);
*/
        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
//                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
//                if (confirm != null) {
                try {
                    //Getting the payment details
//                        String paymentDetails = confirm.toJSONObject().toString(4);
//                        Log.i("paymentExample", paymentDetails);

                       /* new Payment().execute();
                        //Starting a new activity for the payment details and also putting the payment details with intent
                        Intent intent=new Intent(this,Search_recruiter.class);
                        // startActivity(new Intent(this, Search_recruiter.class)
                        intent.putExtra("PaymentDetails", paymentDetails);
                        intent.putExtra("PaymentAmount", paymentAmount);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        prefsUtil.setPaidStatus(true);*/

                } catch (Exception e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
//                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            }/* else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }*/
        }
    }

    /*@Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }*/

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
