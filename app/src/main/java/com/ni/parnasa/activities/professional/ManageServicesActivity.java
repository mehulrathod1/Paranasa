package com.ni.parnasa.activities.professional;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ni.parnasa.R;
import com.ni.parnasa.adapters.ManageServiceAdapter;
import com.ni.parnasa.pojos.ManageServicePojo;
import com.ni.parnasa.utils.MultiLanguageUtils;

public class ManageServicesActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private ImageView imgBack, imgAddService;
    private RecyclerView rvServices;
    private ManageServiceAdapter adapter;

    private List<ManageServicePojo> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        mContext = ManageServicesActivity.this;

        init();

    }

    private void init() {
        imgBack = findViewById(R.id.imgBack);
        imgAddService = findViewById(R.id.imgAddService);
        rvServices = findViewById(R.id.rvServices);

        rvServices.setLayoutManager(new LinearLayoutManager(mContext));
        list = new ArrayList<>();

        fillList();

        adapter = new ManageServiceAdapter(mContext, list);
        rvServices.setAdapter(adapter);

        imgBack.setOnClickListener(this);
        imgAddService.setOnClickListener(this);
    }

    private void fillList() {

        for (int i = 0; i < 6; i++) {
            List<String> lst = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                lst.add("keywod " + j);
            }
            list.add(new ManageServicePojo("Service name " + i, "$15/hr", lst));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == imgAddService) {
            startActivity(new Intent(mContext, AddServiceActivity.class));
        }
    }
}
