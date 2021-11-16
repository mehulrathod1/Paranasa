package com.ni.parnasa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.LoginActivity;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;

public class ProfessionalType extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private PrefsUtil prefsUtil;

    private LinearLayout len_worker, len_company;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_type);

        mContext = ProfessionalType.this;

        len_worker = (LinearLayout) findViewById(R.id.len_worker);
        len_company = (LinearLayout) findViewById(R.id.len_company);
        img_back = (ImageView) findViewById(R.id.img_back);

        prefsUtil = new PrefsUtil(mContext);

        len_company.setOnClickListener(this);
        len_worker.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.len_worker:
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra("what", "company");
                prefsUtil.Set_professionaType("company");
                startActivity(intent);
                break;
            case R.id.len_company:
                Intent intent2 = new Intent(mContext, LoginActivity.class);
                intent2.putExtra("what", "worker");
                prefsUtil.Set_professionaType("worker");
                startActivity(intent2);
                break;
            case R.id.img_back:
                finish();
                break;
        }

    }
}
