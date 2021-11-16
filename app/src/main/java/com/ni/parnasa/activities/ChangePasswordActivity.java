package com.ni.parnasa.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private ImageView img_back;
    private EditText edtCurrentPwd, edtNewPwd, edtConfirmPwd;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mContext = ChangePasswordActivity.this;
        prefsUtil = new PrefsUtil(this);

        img_back = findViewById(R.id.img_back);
        edtCurrentPwd = findViewById(R.id.edtCurrentPwd);
        edtNewPwd = findViewById(R.id.edtNewPwd);
        edtConfirmPwd = findViewById(R.id.edtConfirmPwd);
        btnSave = findViewById(R.id.btnSave);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strCurrentPwd = edtCurrentPwd.getText().toString().trim();
                String strNewPwd = edtNewPwd.getText().toString().trim();
                String strConfirmPwd = edtConfirmPwd.getText().toString().trim();

                if (strCurrentPwd.equals("")) {
                    Toast.makeText(mContext, R.string.msg_current_pwd_validation, Toast.LENGTH_SHORT).show();
                } else if (strNewPwd.equals("")) {
                    Toast.makeText(mContext, R.string.msg_new_pwd_validation, Toast.LENGTH_SHORT).show();
                } else if (strNewPwd.length() < 8) {
                    Toast.makeText(mContext, R.string.msg_new_pwd_minimum_required, Toast.LENGTH_SHORT).show();
                } else if (strConfirmPwd.equals("")) {
                    Toast.makeText(mContext, R.string.msg_confirm_pwd_validation, Toast.LENGTH_SHORT).show();
                } else if (!strNewPwd.equals(strConfirmPwd)) {
                    Toast.makeText(mContext, R.string.msg_pwd_not_match, Toast.LENGTH_SHORT).show();
                } else {
                    apiCallForChangePassword(strCurrentPwd, strNewPwd, strConfirmPwd);
                }
            }
        });

        setupAdMob();
    }

    private void apiCallForChangePassword(String strCurrentPwd, String strNewPwd, String strConfirmPwd) {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("old_password");
        value.add(strCurrentPwd);

        param.add("new_password");
        value.add(strNewPwd);

        param.add("confirm_password");
        value.add(strConfirmPwd);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        new ParseJSON(mContext, BaseUrl.changePassword, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_change_password_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }
}
