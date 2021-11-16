package com.ni.parnasa.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.MultiLanguageUtils;

public class Payment_Via extends AppCompatActivity {
    TextView tv_Choose,tv_Paymode;
    String st_choose,st_paymode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_via);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_Choose=(TextView)findViewById(R.id.choose);
        tv_Paymode=(TextView)findViewById(R.id.paymode);
        Intent intent=getIntent();
        st_choose=intent.getStringExtra("choose");
        st_paymode=intent.getStringExtra("payto");
        tv_Choose.setText(st_choose);
        tv_Paymode.setText(st_paymode);


    }

}
