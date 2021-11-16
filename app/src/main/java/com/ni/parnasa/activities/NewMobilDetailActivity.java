package com.ni.parnasa.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.MultiLanguageUtils;

public class NewMobilDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private ImageView img_back;
    private EditText edtMobile;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mobil_detail);

        mContext = NewMobilDetailActivity.this;

        img_back = findViewById(R.id.img_back);
        edtMobile = findViewById(R.id.edtMobile);
        btnSubmit = findViewById(R.id.btnSubmit);

        img_back.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == img_back)
            onBackPressed();
        else if (v == btnSubmit) {
            String strMobile = edtMobile.getText().toString().trim();
            if (!strMobile.equals("")) {
                Intent intent = new Intent();
                intent.putExtra("newMobile", strMobile);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(mContext, R.string.validation_new_mobile_msg, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
