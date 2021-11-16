package com.ni.parnasa.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Context mContext;

    private PrefsUtil prefsUtil;
    private int REQ_CODE_FINGERPRINT = 87;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = SplashActivity.this;
        prefsUtil = new PrefsUtil(mContext);

        handler.sendEmptyMessageDelayed(100, 2500);

//        MyLogs.e("TAG", "ROOT URL :" + BaseUrl.URL);

        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    MyLogs.e("TAG Splash screen ", "FCM TOKEN :: " + task.getResult().getToken());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        generateKeyHash();
    }

    private void generateKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                MyLogs.e("TAG", "HashKey " + Base64.encodeToString(md.digest(), Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            MyLogs.e("Name not found", e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            MyLogs.e("Error", e.getMessage());
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int id = msg.what;

//            MyLogs.e("TAG", "TOKEN : " + FirebaseInstanceId.getInstance().getToken());
//            MyLogs.e("TAG", "is Finger : " + prefsUtil.isRequiredFingerPrint());

            if (id == 100) {
                if (prefsUtil.getLoggdInstaus()) {
                    /*if (prefsUtil.isRequiredFingerPrint()) {
                        startActivityForResult(new Intent(mContext, FingerprintAuthActivity.class), REQ_CODE_FINGERPRINT);
                    } else {
                        if (prefsUtil.GetUserType().equals("customer")) {
                            Intent intent = new Intent(SplashActivity.this, CustomerHomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, ProfessionalHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }*/
                    if (prefsUtil.GetUserType().equals("customer")) {
                        Intent intent = new Intent(SplashActivity.this, CustomerHomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, ProfessionalHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Intent i = new Intent(SplashActivity.this, UserType.class);
                    i.putExtra("value", "value");
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        }
    };

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FINGERPRINT && resultCode == Activity.RESULT_OK) {
            if (prefsUtil.GetUserType().equals("customer")) {
                Intent intent = new Intent(SplashActivity.this, CustomerHomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, ProfessionalHomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }*/
}





