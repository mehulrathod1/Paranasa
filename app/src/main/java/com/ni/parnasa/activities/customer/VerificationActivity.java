package com.ni.parnasa.activities.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.FingerprintAuthActivity;
import com.ni.parnasa.activities.NewMobilDetailActivity;
import com.ni.parnasa.activities.SignupProfessionalNewActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.UserLoginPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FingerPrintPrefUtil;
import com.ni.parnasa.utils.JsonToPojoUtils;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.ni.parnasa.utils.SaveDataUtility;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class VerificationActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private LinearLayout ll_profetionlayout;
    private ProgressDialog progressDialog;
    private EditText et_sms, et_email;
    private ImageView img_back;
    private TextView txtResendSms, txtResendEmail;
    private Button btnSubmitSms, btnSubmitEmail;

    private String st_v_code, what, userRole = "";
    private int REQ_CODE_FINGER = 87, REQ_CODE_MOBILE = 78;

    private UserLoginPojoItem loginPojo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        mContext = VerificationActivity.this;

        what = getIntent().getStringExtra("what");

        if (what.equalsIgnoreCase("customer")) {
            userRole = "Customer";
        } else if (what.equalsIgnoreCase("worker")) {
            userRole = "Agency";
        } else if (what.equalsIgnoreCase("company")) {
            userRole = "Sole Professional";
        } else {
            userRole = "";
        }

        MyLogs.w("TAG", "ROLE - " + userRole + " WHAT - " + what);

        /*final String name = intent.getStringExtra("name");
        final String gender = intent.getStringExtra("gender");
        final String email = intent.getStringExtra("email");
        final String phone = intent.getStringExtra("phnumber");*/

        prefsUtil = new PrefsUtil(mContext);
        progressDialog = new ProgressDialog(this);

        img_back = (ImageView) findViewById(R.id.img_back);
        et_sms = (EditText) findViewById(R.id.v_sms);
        et_email = (EditText) findViewById(R.id.v_email);
        ll_profetionlayout = (LinearLayout) findViewById(R.id.profetionlayout);
        btnSubmitSms = (Button) findViewById(R.id.btn_submit_code);
        btnSubmitEmail = (Button) findViewById(R.id.sumit_email);
        txtResendSms = findViewById(R.id.txtResendSms);
        txtResendEmail = findViewById(R.id.txtResendEmail);

        /*if (prefsUtil.GetUserType().equals("customer")) {
            ll_profetionlayout.setVisibility(View.GONE);
        } else {
            ll_profetionlayout.setVisibility(View.VISIBLE);
        }*/

        txtResendSms.setOnClickListener(this);
        txtResendEmail.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btnSubmitSms.setOnClickListener(this);
        btnSubmitEmail.setOnClickListener(this);

       /* Sumit_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (what.equals("company")) {
                    Intent intent = new Intent(VerificationActivity.this, ProfileDetailsOne.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("phnumber", phone);
                    intent.putExtra("name",name);
                    intent.putExtra("gender",gender);
                    intent.putExtra("email",email);
                    startActivity(intent);
                    finish();
                }
                else if (what.equals("worker")){
                    Intent intent = new Intent(VerificationActivity.this, ProfessionalHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(VerificationActivity.this, CustomerHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });*/

    }

    @Override
    public void onClick(View v) {
        if (v == txtResendSms) {
            openAlertForConfirResendMobile();
//            apiCallForResendVerification("mobile");
        } else if (v == txtResendEmail) {
            apiCallForResendVerification("email", "");
        } else if (v == btnSubmitSms) {
            st_v_code = et_sms.getText().toString().trim();
            if (st_v_code.equals("")) {
                Toast.makeText(mContext, R.string.verification_coe_validation, Toast.LENGTH_LONG).show();
            } else {
                new apiCallForVerification().execute();
            }
        } else if (v == btnSubmitEmail) {
            st_v_code = et_email.getText().toString().trim();
            if (st_v_code.equals("")) {
                Toast.makeText(mContext, R.string.verification_coe_validation, Toast.LENGTH_LONG).show();
            } else {
                new apiCallForVerification().execute();
            }
        } else if (v == img_back) {
            finish();
        }
    }

    private void openAlertForConfirResendMobile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.confirmation);
        builder.setMessage(R.string.conform_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // api call for resend
                apiCallForResendVerification("mobile", "");
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // go to new screen
                startActivityForResult(new Intent(mContext, NewMobilDetailActivity.class), REQ_CODE_MOBILE);
            }
        });
        builder.create().show();
    }


    private void apiCallForResendVerification(String type, String newMobile) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("role_type");
        value.add(userRole);

        param.add("sendcode_type");
        value.add(type);

        param.add("mobile_number");
        value.add(newMobile);

        new ParseJSON(mContext, BaseUrl.resendVerificationCode, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                if (status) {

                    CommonPojo pojo = (CommonPojo) obj;

                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void openAlertForFingerprintAuth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(R.string.fingerprint_title);
        builder.setMessage(R.string.fingerprint_msg);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, FingerprintAuthActivity.class);
                startActivityForResult(intent, REQ_CODE_FINGER);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                gotoDashboard(false);
            }
        });

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FINGER && resultCode == Activity.RESULT_OK) {
            gotoDashboard(true);
        } else if (requestCode == REQ_CODE_MOBILE && resultCode == Activity.RESULT_OK) {
            apiCallForResendVerification("mobile", data.getStringExtra("newMobile"));
        }
    }

    private void gotoDashboard(boolean isReqFinger) {
        /** Save user data in preference and move to dashboard */

        if (loginPojo != null) {

            String user_id = loginPojo.getUserId();
            String first_name = loginPojo.getFirstName();
            String last_name = loginPojo.getLastName();
            String logo_image = loginPojo.getLogoImage();
            String gender = loginPojo.getGender();

            prefsUtil.Set_UserID(user_id);
            prefsUtil.setUserGender(gender);
            prefsUtil.setUserName(first_name + " " + last_name);
            prefsUtil.setUserPic(logo_image);
            prefsUtil.setRole(loginPojo.getRole());
            prefsUtil.setUserLoginDetail(loginPojo.toString());

            /*if (isReqFinger)
                prefsUtil.setRequiredFingerPrint(true);
            else {
                prefsUtil.setRequiredFingerPrint(false);
            }*/
            prefsUtil.SetIsloogedIn(true);

            if (what.equals("worker")) {
                Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, CustomerHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    public class apiCallForVerification extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String Response1 = null;
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            try {

                JSONObject request_main = new JSONObject();
                request_main.put("authorised_key", BaseUrl.authorised_key);
                request_main.put("user_id", prefsUtil.GetUserID());
                request_main.put("verification_code", st_v_code);

                MyLogs.e("URL", BaseUrl.URL + "User/verify");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "User/verify")
                        .post(body)
                        .build();

                okhttp3.Response response = client.newCall(request).execute();
                Response1 = response.body().string();
                return Response1;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return Response1;
        }

        @Override
        protected void onPostExecute(String s) {
            // swipeRefreshLayout.setRefreshing(false);
            progressDialog.dismiss();

            if (s == null) {
                Toast.makeText(getApplicationContext(), "Network Error !", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    MyLogs.e("verifyreturn", String.valueOf(jsonObject));
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("Yes")) {

                        JSONObject data = jsonObject.getJSONObject("data");

                        JsonToPojoUtils utils = new JsonToPojoUtils();
                        loginPojo = (UserLoginPojoItem) utils.getPojo(String.valueOf(data), UserLoginPojoItem.class);

                        if (what.equalsIgnoreCase("customer")) {

                            FingerPrintPrefUtil.with(mContext).write(BaseUrl.isCompletedSignup, true);

                            SaveDataUtility utility = new SaveDataUtility(mContext);
                            utility.saveData(prefsUtil.GetUserID(),
                                    data.getString("first_name"),
                                    data.getString("last_name"),
                                    data.getString("gender"),
                                    data.getString("logo_image"),
                                    data.getString("role"),
                                    false,
                                    true);

                            Intent intent = new Intent(mContext, CustomerHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            /*Intent intent = new Intent(mContext, SignupCustomerNewActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("userId", data.getString("user_id"));
                            intent.putExtra("userRole", "Customer");
                            intent.putExtra("userName", data.getString("first_name") + " " + data.getString("last_name"));
                            intent.putExtra("gender", data.getString("gender"));
                            intent.putExtra("profilePic", data.getString("logo_image"));
                            startActivity(intent);*/
                        } else if (what.equalsIgnoreCase("company")) {
                            Intent intent = new Intent(mContext, SignupProfessionalNewActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("userId", data.getString("user_id"));
                            intent.putExtra("userRole", "Sole Professional");
                            intent.putExtra("userName", data.getString("first_name") + " " + data.getString("last_name"));
                            intent.putExtra("gender", data.getString("gender"));
                            intent.putExtra("profilePic", data.getString("logo_image"));
                            intent.putExtra("mobile", data.getString("mobile_number"));
                            startActivity(intent);
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        //final AlertDialog alert = builder.create();
                        builder.setTitle(R.string.app_name);
                        // builder.setIcon(R.drawable.pickme_serviceapp_logo1);
                        builder.setMessage(message);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
