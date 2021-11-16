package com.ni.parnasa.activities.professional.owncompany;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ProfileDetailsTwo extends AppCompatActivity {

    private Context mContext;
    private PrefsUtil prefsUtil;
    private DatePicker datePicker;

    private EditText et_showdob;
    private Button bt_save;
    private ImageView img_back;
    private ProgressDialog progressDialog;
    private EditText et_address, et_city, et_country;


    private String st_address, st_city, st_country, st_dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details_two);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = ProfileDetailsTwo.this;

        progressDialog = new ProgressDialog(mContext);
        prefsUtil = new PrefsUtil(mContext);

        datePicker = (DatePicker) findViewById(R.id.datepicker);
        et_showdob = (EditText) findViewById(R.id.showdob);

        int dayy = datePicker.getDayOfMonth();
        int monthh = datePicker.getMonth() + 1;
        int yearr = datePicker.getYear();


        et_showdob.setText(dayy + " / " + monthh + " / " + yearr);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                MyLogs.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                et_showdob.setText(dayOfMonth + " / " + (month + 1) + " / " + year);
            }
        });

        et_address = (EditText) findViewById(R.id.address);
        et_city = (EditText) findViewById(R.id.city);
        et_country = (EditText) findViewById(R.id.country);
        bt_save = (Button) findViewById(R.id.btn_next);
        img_back = (ImageView) findViewById(R.id.img_back);

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                st_address = et_address.getText().toString().trim();
                st_city = et_city.getText().toString().trim();
                st_country = et_country.getText().toString().trim();
                st_dob = et_showdob.getText().toString().trim();

                new StepTWO().execute();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class StepTWO extends AsyncTask<String, Void, String> {

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
                request_main.put("address", st_address);
                request_main.put("location", st_city + "," + st_country);
                request_main.put("birthdate", st_dob);

                MyLogs.e("URL", BaseUrl.URL + "user/signup_step3");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());
                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "user/signup_step3")
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
                    MyLogs.e("stepone", String.valueOf(jsonObject));

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

                        /*Intent intent = new Intent(mContext, ProfileDetailsThree.class);
                        startActivity(intent);*/
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
