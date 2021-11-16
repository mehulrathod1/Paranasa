package com.ni.parnasa.activities.professional;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.adapters.ChipsAdapter;
import com.ni.parnasa.utils.ApplicationController;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AddServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView imgBack;
    private TextView txtTitle, txtAddKeyword;
    private EditText edtServiceName, edtPrice, edtServiceKeyword;
    private Button btnSave;
    private RecyclerView rvKeyword;

    private List<String> listKeyword;
    private ChipsAdapter adapter;

    private boolean isFromEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        mContext = AddServiceActivity.this;

        init();
    }

    private void init() {
        imgBack = findViewById(R.id.imgBack);
        txtTitle = findViewById(R.id.txtTitle);
        edtServiceName = findViewById(R.id.edtServiceName);
        edtPrice = findViewById(R.id.edtPrice);
        edtServiceKeyword = findViewById(R.id.edtServiceKeyword);
        txtAddKeyword = findViewById(R.id.txtAddKeyword);
        btnSave = findViewById(R.id.btnSave);
        rvKeyword = findViewById(R.id.rvKeyword);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        rvKeyword.setLayoutManager(flexboxLayoutManager);

        listKeyword = new ArrayList<>();
        adapter = new ChipsAdapter(mContext, listKeyword, true);
        rvKeyword.setAdapter(adapter);

        Intent intent = getIntent();
        isFromEdit = intent.getBooleanExtra("isFromEdit", false);

        if (isFromEdit) {
            txtTitle.setText(R.string.edit_service);
            edtServiceName.setText(intent.getStringExtra("serviceName"));
            edtPrice.setText(intent.getStringExtra("servicePrice"));
            listKeyword.addAll(((ApplicationController) getApplication()).keywordList);
            adapter.notifyDataSetChanged();
        } else {
            txtTitle.setText(R.string.add_service);
        }

        imgBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        txtAddKeyword.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        ((ApplicationController) getApplication()).keywordList.clear();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == txtAddKeyword) {
            String keyword = edtServiceKeyword.getText().toString().trim();
            if (!keyword.equals("")) {
                listKeyword.add(keyword);
                adapter.notifyDataSetChanged();
                edtServiceKeyword.setText("");
            } else {
                Toast.makeText(mContext, R.string.msg_keyword_validation, Toast.LENGTH_SHORT).show();
            }
        } else if (v == btnSave) {
            String strServiceName = edtServiceName.getText().toString().trim();
            String strPrice = edtPrice.getText().toString().trim();

            /*if (isValidAllField(strServiceName, strPrice)) {
                apiCall();
            }*/
        }
    }

    private void apiCall() {
        Toast.makeText(mContext, "api call", Toast.LENGTH_SHORT).show();

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        /*for (int i = 0; i < listKeyword.size(); i++) {

        }*/

    }

    public boolean isValidAllField(String strServiceName, String strPrice) {
        if (strServiceName.equals("")) {
            Toast.makeText(mContext, R.string.msg_service_name, Toast.LENGTH_SHORT).show();
            return false;
        } else if (strPrice.equals("")) {
            Toast.makeText(mContext, R.string.msg_service_price_validation, Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (!strPrice.equals("")) {
            float price = Float.parseFloat((strPrice));
            if (price <= 0) {
                Toast.makeText(mContext, R.string.msg_invalid_price, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }*/ else
            return false;
    }
}
