package com.ni.parnasa.activities.professional;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.professional.datamodel.Job_data;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class JobBreakUp extends AppCompatActivity {
    EditText et_JobBookingId, et_basicrate, et_rate, et_parts, et_part_rate, et_discount, et_total, et_tax, et_tax_percen;
    String st_customer_id, st_job_id, st_user_code;
    PrefsUtil prefsUtil;
    EditText et_wo_time;
    ProgressDialog progressDialog;
    static final int TIME_DIALOG_ID = 1111;
    int Total_price = 0;
    String Working_hours;

    private int hour, st_grandtotal;
    private int minute;
    Button bt_senttocus;
    String st_parts_dicers, st_parts_rate, st_discount, st_taxrate, st_total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_break_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        et_JobBookingId = (EditText) findViewById(R.id.jobbookingid);
        et_basicrate = (EditText) findViewById(R.id.basicrate);
        et_wo_time = (EditText) findViewById(R.id.wo_time);
        et_rate = (EditText) findViewById(R.id.rate);
        et_parts = (EditText) findViewById(R.id.parts);
        et_part_rate = (EditText) findViewById(R.id.part_rate);
        et_discount = (EditText) findViewById(R.id.discount);
        et_total = (EditText) findViewById(R.id.total);
        et_tax = (EditText) findViewById(R.id.tax);
        et_tax_percen = (EditText) findViewById(R.id.tax_percen);
        bt_senttocus = (Button) findViewById(R.id.senttocus);
        Intent intent = getIntent();
        st_customer_id = intent.getStringExtra("customer_id");
        st_job_id = intent.getStringExtra("job_id");
        st_user_code = intent.getStringExtra("user_code");

        prefsUtil = new PrefsUtil(JobBreakUp.this);
        progressDialog = new ProgressDialog(JobBreakUp.this);
        et_JobBookingId.setText(st_user_code + st_job_id);
        et_basicrate.setText(prefsUtil.getJobRate());
        et_tax.setText(prefsUtil.getTaxRate());


        et_wo_time.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialog(TIME_DIALOG_ID);
                return false;
            }
        });

        et_part_rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_part_rate.getText().toString().equals("")) {
                    et_total.setText(Total_price + "");

                    int tax_rate = Integer.parseInt(prefsUtil.getTaxRate());
                    float tax_rate_ = Total_price * tax_rate;
                    et_tax_percen.setText(tax_rate_ / 100 + "");

                } else {
                    float part_rate = Float.parseFloat(et_part_rate.getText().toString());
                    et_total.setText((int) (Total_price + part_rate) + "");
                    //Total_price= (int) (Total_price + part_rate);
                    int tax_rate = Integer.parseInt(prefsUtil.getTaxRate());
                    float tax_rate_ = (Total_price + part_rate) * tax_rate;
                    et_tax_percen.setText(tax_rate_ / 100 + "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (et_discount.getText().toString().equals("")) {
                    et_total.setText(Total_price + "");

                    int tax_rate = Integer.parseInt(prefsUtil.getTaxRate());
                    float tax_rate_ = Total_price * tax_rate;

                    et_tax_percen.setText(tax_rate_ / 100 + "");


                } else {
                    float discount_rate = Float.parseFloat(et_discount.getText().toString());
                    et_total.setText((int) (Total_price - discount_rate) + "");

                    //Total_price= (int) (Total_price - discount_rate);
                    int tax_rate = Integer.parseInt(prefsUtil.getTaxRate());
                    float tax_rate_ = (Total_price - discount_rate) * tax_rate;

                    et_tax_percen.setText(tax_rate_ / 100 + "");


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bt_senttocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                st_parts_dicers = et_parts.getText().toString();
                st_parts_rate = et_part_rate.getText().toString();
                st_discount = et_discount.getText().toString();
                st_taxrate = et_tax_percen.getText().toString();
                st_total = et_total.getText().toString();
                if (!st_total.equals("") && !st_discount.equals("")) {
                    st_grandtotal = Integer.parseInt(st_total + st_discount);

                }
                if (st_total.equals("")) {

                    Toast.makeText(getApplicationContext(), "Please select working hours.", Toast.LENGTH_LONG).show();
                } else {
                    new Jobs_lst().execute();
                }


            }
        });


    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:

                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);

        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour = hourOfDay;
            minute = minutes;

            updateTime(hour, minute);

        }

    };

    private static String utilTime(int value) {

        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        et_wo_time.setText(aTime);
        Working_hours = aTime;
        int mintsss = Integer.parseInt(minutes);
        int hourlyrate = Integer.parseInt(prefsUtil.getHourRate());
        System.out.println("hr" + hours + "min" + mintsss + "hourlyrate" + hourlyrate);
        int hour_cal = hours * hourlyrate;
        int min_cal = (hourlyrate * mintsss / 60);
        System.out.println("hour_cal" + hour_cal + "min_cal" + min_cal);

        et_rate.setText(hour_cal + min_cal + "");//12-hourly rate
        Total_price = Integer.parseInt(et_basicrate.getText().toString()) + Integer.parseInt(et_rate.getText().toString());
        et_total.setText(Total_price + "");
        int tax_rate = Integer.parseInt(prefsUtil.getTaxRate());
        double rate_tax = (Total_price * tax_rate);
        System.out.println("Total_price" + Total_price + "tax_rate" + tax_rate);
        et_tax_percen.setText(rate_tax / 100 + "");

    }

    public class Jobs_lst extends AsyncTask<String, Void, String> {
        List<Job_data> job_data = new ArrayList<>();

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
                request_main.put("job_id", st_job_id);
                request_main.put("job_booking_id", st_user_code + st_job_id);
                request_main.put("customer_id", st_customer_id);
                request_main.put("basic_rate", prefsUtil.getJobRate());
                request_main.put("working_hour", Working_hours);
                request_main.put("working_hour_rate", prefsUtil.getHourRate());
                request_main.put("parts_dicers", st_parts_dicers);
                request_main.put("parts_dicers_rate", st_parts_rate);
                request_main.put("discount", st_discount);
                request_main.put("tax", prefsUtil.getTaxRate());
                request_main.put("tax_rate", st_taxrate);
                request_main.put("total", st_total);
                request_main.put("grand_total", st_grandtotal);


                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "allinvoices/create_invoice")
                        .post(body)
                        .build();


                okhttp3.Response response = client.newCall(request).execute();
                Response1 = response.body().string();
                return Response1;

            } catch (IOException e) {

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
                    Log.v("service_return", String.valueOf(jsonObject));
                    String status = jsonObject.getString("status");
                    if (status.equals("Yes")) {
                        String message = jsonObject.getString("message");

                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(JobBreakUp.this);
                        //final AlertDialog alert = builder.create();
                        builder.setTitle(R.string.app_name);
                        // builder.setIcon(R.drawable.pickme_serviceapp_logo1);
                        builder.setMessage(message);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
//                                        new YourJobsActivity.Jobs_lst().execute();


                                    }
                                });

                        final android.app.AlertDialog alert = builder.create();
                        alert.show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
