package com.ni.parnasa.activities.professional.owncompany;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.models.Service_data;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ProfileDetailsOne extends AppCompatActivity {

    private Context mContext;
//    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private PrefsUtil prefsUtil;

    private ArrayAdapter<Service_data> adapter;
    private ArrayList<Service_data> mylist = new ArrayList<Service_data>();

    private EditText et_pro_2, et_pro_3, et_pro_4, et_pro_5;
    private TextView tv_username, tv_email, tv_phone;
    private RadioButton rb_male, rb_female;
    private ProgressDialog progressDialog;
    private ImageView img_back;
    private Button bt_save;

    public static TextView at_profession;
    private String UserId, st_servicename, st_service_id, st_service_icon, st_keyword, st_profession_two, st_pro_three, st_pro_four, st_pro_five;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details_one);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = ProfileDetailsOne.this;

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String gender = intent.getStringExtra("gender");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phnumber");

//        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(mContext);
        prefsUtil = new PrefsUtil(mContext);

//        UserId = firebaseAuth.getCurrentUser().getUid();
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_username = (TextView) findViewById(R.id.txt_user_name);
        tv_email = (TextView) findViewById(R.id.email);
        rb_female = (RadioButton) findViewById(R.id.female);
        rb_male = (RadioButton) findViewById(R.id.male);
        tv_phone = (TextView) findViewById(R.id.phone);
        at_profession = (TextView) findViewById(R.id.profession);
        et_pro_2 = (EditText) findViewById(R.id.pro_one);
        et_pro_3 = (EditText) findViewById(R.id.pro_three);
        et_pro_4 = (EditText) findViewById(R.id.pro_four);
        et_pro_5 = (EditText) findViewById(R.id.pro_five);
        bt_save = (Button) findViewById(R.id.saveandnext);

        tv_username.setText("User Name: " + name);
        tv_email.setText("Email: " + email);
        tv_phone.setText("Phone: " + phone);

        if (gender.equals("female")) {
            rb_female.setChecked(true);
        } else if (gender.equals("male")) {
            rb_male.setChecked(true);
        }

        at_profession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent1 = new Intent(mContext, ServiceListActivity.class);
                startActivity(intent1);*/
            }
        });

        //SearchProfession();

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                st_profession_two = et_pro_2.getText().toString().trim();
                st_pro_three = et_pro_3.getText().toString().trim();
                st_pro_four = et_pro_4.getText().toString().trim();
                st_pro_five = et_pro_5.getText().toString().trim();
                new StepONE().execute();
            }
        });
    }

    public class StepONE extends AsyncTask<String, Void, String> {

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
                request_main.put("service", at_profession.getText().toString());
                request_main.put("skills", st_profession_two + "," + st_pro_three + "," + st_pro_four + "," + st_pro_five);

                MyLogs.e("URL", BaseUrl.URL + "user/signup_step2");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "user/signup_step2")
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
                    MyLogs.e("steponeResponse", String.valueOf(jsonObject));

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("Yes")) {
                        /*JSONObject data = jsonObject.getJSONObject("data");
                        String user_id = data.getString("user_id");
                        String first_name = data.getString("first_name");
                        String last_name = data.getString("last_name");
                        String company_email = data.getString("company_email");
                        String logo_image = data.getString("logo_image");
                        String mobile_number = data.getString("mobile_number");
                        String gender = data.getString("gender");
                        String lat = data.getString("lat");
                        String lng = data.getString("lng");
                        String role = data.getString("role");*/

                        Intent intent = new Intent(mContext, ProfileDetailsTwo.class);
                        startActivity(intent);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        //final AlertDialog alert = builder.create();
                        builder.setTitle(R.string.app_name);

                        builder.setMessage(message);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
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
