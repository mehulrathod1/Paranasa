package com.ni.parnasa.activities.professional.adapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.Payment_Via;
import com.ni.parnasa.activities.professional.Invoice;
import com.ni.parnasa.activities.professional.JobBreakUp;
import com.ni.parnasa.activities.professional.datamodel.Job_data;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.PrefsUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class Jobs_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private Activity activity;
    private LayoutInflater inflater;
    List<Job_data> data = Collections.emptyList();
    Activity activity;
    View view;
//    StorageReference storageRef;
//    FirebaseStorage storage;
    String icon;
   String mountain_name,mountain_icon,mountain_des,m_list_icon;
    public static   ArrayList<String> m_list_image = new ArrayList<String>();
    ProgressDialog progressDialog;
    PrefsUtil prefsUtil;
    String st_ac_rj,st_job_id;

    // create constructor to innitilize context and data sent from Splash
    public Jobs_adapter(Activity activity, List<Job_data> data) {
        if (activity!=null) {

            this.activity = activity;
            inflater = LayoutInflater.from(activity);
            this.data = data;
        }
    }
    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.item_jobs, parent, false);
        MyHolder holder = new MyHolder(view);
        final Job_data dataProvider = data.get(viewType);


        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder handler = (MyHolder) holder;
        final Job_data dataProvider = data.get(position);
//        storage=FirebaseStorage.getInstance();
//        storageRef = storage.getReference();
        progressDialog=new ProgressDialog(activity);
        prefsUtil =new PrefsUtil(activity);


        handler.tv_jobtodo.setText("Job To Do: "+dataProvider.JobToDo);

        handler.tv_assignto.setText("Assigne to: "+dataProvider.AssigneTo);
        handler.tv_completeddate.setText(dataProvider.CompletedDate);
        handler.tv_customer.setText("Customer: "+dataProvider.Customer);
        handler.tv_datetodo.setText(dataProvider.DateToDo);
        handler.tv_remark.setText("Remarks: "+dataProvider.Remarks);
        handler.tv_jobstatus.setText("Job Status: "+dataProvider.JobStatus);
        if(dataProvider.JobStatus.equals("Completed"))
        {

            handler.bt_viewinvoice.setText(" View Invoice");
            handler.bt_viewinvoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(activity, Invoice.class);
                    intent.putExtra("id",dataProvider.Id);
                    activity.startActivity(intent);
                }
            });


            if (prefsUtil.GetUserType().equals("customer")) {
                handler.bt_pay.setVisibility(View.VISIBLE);
                handler.bt_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog cal = new Dialog(activity);
                        //cal.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                        Window window = cal.getWindow();
                        WindowManager.LayoutParams wlp = window.getAttributes();
                        wlp.gravity =  Gravity.CENTER;
                        wlp.width = ActionBar.LayoutParams.MATCH_PARENT;
                        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);


                        cal.setCanceledOnTouchOutside(false);
                        cal.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        cal.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        cal.setContentView(R.layout.job_breakup);
                        ImageView close=(ImageView)cal.findViewById(R.id.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cal.show();
                            }
                        });
                        LinearLayout Paypal=(LinearLayout)cal.findViewById(R.id.paypal);
                        LinearLayout Cash=(LinearLayout)cal.findViewById(R.id.cash);
                        LinearLayout Others=(LinearLayout)cal.findViewById(R.id.others);
                        Paypal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent1=new Intent(activity, Payment_Via.class);
                                intent1.putExtra("choose","You Choose to Pay with PayPal");
                                intent1.putExtra("payto","To Paypal id \"info@gmail.com\" \n of the professional");
                                activity.startActivity(intent1);
                            }
                        });
                        Cash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent1=new Intent(activity,Payment_Via.class);
                                intent1.putExtra("choose","You Choose to Pay cash");
                                intent1.putExtra("payto","Cash to the professional");
                                activity.startActivity(intent1);
                            }
                        });
                        Others.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent1=new Intent(activity,Payment_Via.class);
                                intent1.putExtra("choose","You Choose to Pay Cash");
                                intent1.putExtra("payto","Cash to the professional");
                                activity.startActivity(intent1);
                            }
                        });
                        cal.show();




                    }
                });
            }
            else {

            }


            if (dataProvider.customer_rating.equals("0")) {
                if (prefsUtil.GetUserType().equals("customer")) {
                    handler.bt_rate.setText("Rate Professional");
                }else {
                    handler.bt_rate.setText("Rate Customer");
                }
                handler.bt_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Intent intent=new Intent(activity, AddRatingActivity.class);
                        intent.putExtra("via","Sole Professional");
                        intent.putExtra("id",dataProvider.Id);
                        intent.putExtra("assign_to_user_id",dataProvider.assign_to_user_id);
                        activity.startActivity(intent);*/

                    }
                });
            }else {
                handler.bt_rate.setVisibility(View.GONE);
            }
            //rate user
        }
        else if (dataProvider.JobStatus.equals("Accepted"))
        {
            if (prefsUtil.GetUserType().equals("customer"))
            {
                handler.bt_viewinvoice.setVisibility(View.GONE);
                handler.bt_rate.setVisibility(View.GONE);

            }
            else {

                handler.bt_viewinvoice.setText("Job Done");
                handler.bt_viewinvoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent intent=new Intent(activity, JobBreakUp.class);
                       intent.putExtra("customer_id",dataProvider.customer_id);
                       intent.putExtra("job_id",dataProvider.Id);
                       intent.putExtra("user_code",dataProvider.user_code);
                       activity.startActivity(intent);
                    }
                });
                handler.bt_rate.setVisibility(View.GONE);
            }


        }
        else if (dataProvider.JobStatus.equals("Waiting"))
        {
            if (prefsUtil.GetUserType().equals("customer"))
            {
                handler.bt_viewinvoice.setVisibility(View.GONE);
                handler.bt_rate.setVisibility(View.GONE);


            }else {
                st_job_id = dataProvider.Id;

                handler.bt_viewinvoice.setText("Accept");
                handler.bt_viewinvoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                        //final AlertDialog alert = builder.create();
                        builder.setTitle(R.string.app_name);
                        // builder.setIcon(R.drawable.pickme_serviceapp_logo1);
                        builder.setMessage("Are you sure you want to accept this job?");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        st_ac_rj = "Accepted";
                                        new AcceptedRejected().execute();

                                    }
                                });

                        final android.app.AlertDialog alert = builder.create();
                        alert.show();


                    }
                });

                // reject job
                handler.bt_rate.setText("Reject");
                handler.bt_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                        //final AlertDialog alert = builder.create();
                        builder.setTitle(R.string.app_name);
                        // builder.setIcon(R.drawable.pickme_serviceapp_logo1);
                        builder.setMessage("Are you sure you want to reject this job?");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        st_ac_rj = "Rejected";
                                        new AcceptedRejected().execute();

                                    }
                                });

                        final android.app.AlertDialog alert = builder.create();
                        alert.show();



                    }
                });
            }


        }
        else if (dataProvider.JobStatus.equals("Rejected"))
        {

            if (prefsUtil.GetUserType().equals("customer"))
            {
                handler.bt_viewinvoice.setVisibility(View.GONE);
                handler.bt_rate.setVisibility(View.GONE);

            }
            else {
                handler.bt_viewinvoice.setText("X");
                handler.bt_viewinvoice.setBackgroundColor(Color.WHITE);

                handler.bt_viewinvoice.setTextColor(Color.RED);
                handler.bt_rate.setVisibility(View.GONE);
            }


        }
        else {

            handler.bt_viewinvoice.setVisibility(View.GONE);
            handler.bt_rate.setVisibility(View.GONE);

        }






    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyHolder extends RecyclerView.ViewHolder{

        // View v;


        TextView tv_jobtodo,tv_customer,tv_assignto,tv_jobstatus,tv_remark,tv_datetodo,tv_completeddate;
        Button bt_viewinvoice,bt_rate,bt_pay;

        public MyHolder(View row) {
            super(row);

            tv_jobtodo=(TextView) row.findViewById(R.id.jobto_do);
            tv_customer=(TextView) row.findViewById(R.id.customer);
            tv_assignto=(TextView) row.findViewById(R.id.assigneto);
            tv_jobstatus=(TextView) row.findViewById(R.id.job_status);
            tv_remark=(TextView) row.findViewById(R.id.remark);
            tv_datetodo=(TextView) row.findViewById(R.id.datetodo);
            tv_completeddate=(TextView) row.findViewById(R.id.completeddate);
            bt_viewinvoice=(Button)row.findViewById(R.id.viewinvoice);
            bt_rate=(Button)row.findViewById(R.id.rate_cus);
            bt_pay=(Button)row.findViewById(R.id.pay_prof);


        }

    }




    public  class AcceptedRejected extends AsyncTask<String,Void,String> {

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
                request_main.put("job_status",st_ac_rj);
                request_main.put("job_id",st_job_id);

                Log.v("sent", String.valueOf(request_main));



                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "All_jobs/dispatch_job")
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

                Toast.makeText(activity, "Network Error !", Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Log.v("service_return", String.valueOf(jsonObject));
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");


                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                    //final AlertDialog alert = builder.create();
                    builder.setTitle(R.string.app_name);
                    // builder.setIcon(R.drawable.pickme_serviceapp_logo1);
                    builder.setMessage(message);
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    new YourJobsActivity.Jobs_lst().execute();


                                }
                            });

                    final android.app.AlertDialog alert = builder.create();
                    alert.show();




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
