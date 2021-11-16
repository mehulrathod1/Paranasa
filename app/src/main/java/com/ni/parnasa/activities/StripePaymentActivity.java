package com.ni.parnasa.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;

public class StripePaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Stripe stripe;

    //    private CardInputWidget cardInputWidget;
    private CardMultilineWidget cardMultiWidget;
    private Button btnPay;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        mContext = StripePaymentActivity.this;

        stripe = new Stripe(mContext, getString(R.string.publishable_key_for_stripe));

//        cardInputWidget = findViewById(R.id.cardInputWidget);
        cardMultiWidget = findViewById(R.id.cardMultiWidget);
        progress = findViewById(R.id.progress);

        btnPay = findViewById(R.id.btnPay);

        btnPay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnPay) {
            Card card = cardMultiWidget.getCard();
            if (card != null) {
                if (card.validateCard()) {
                    if (card.validateNumber()) {
                        progress.setVisibility(View.VISIBLE);

                        stripe.createToken(card, new ApiResultCallback<Token>() {
                            @Override
                            public void onSuccess(@NotNull Token token) {
                                progress.setVisibility(View.GONE);
                                try {
                                    Log.e("ZZZZ", "ID " + token.getId());
                                    Log.e("ZZZZ", "TYPE " + token.getType());
//                                    Log.e("ZZZZ", "BANK " + token.getBankAccount().getBankName());
//                                    Log.e("ZZZZ", "HOLDER NAME " + token.getBankAccount().getAccountHolderName());
//                                    Log.e("ZZZZ", "ACCOUNT NUMBER " + token.getBankAccount().getAccountNumber());
//                                    Log.e("ZZZZ", "CURRENCY " + token.getBankAccount().getCurrency());
                                    Intent data = new Intent();
                                    data.putExtra("Id", token.getId());
                                    setResult(Activity.RESULT_OK, data);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(@NotNull Exception error) {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(mContext, "Invalid card number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Invalid card", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Card is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_OK);
    }
}
