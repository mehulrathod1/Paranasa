package com.ni.parnasa.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.ni.parnasa.utils.PrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AddRatingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private EditText et_comment;
    private RatingBar rb_rate;
    private Button bt_addtofav, bt_submit, btnCashPayment;
    private ImageView img_back;
    private TextView txtMsg;
    private LinearLayout linlayCashPayment;

    private ProgressDialog progressDialog;

    private String st_comment, st_id, st_assign_to_user_id, jobId = "", secondUserId = "", ROLE = "", tip = "", total = "";
    private float st_rating;
    private boolean isFromCashPayment, isNeedToHideFavoriteBtn;
    private String professionalReview = "";

    private boolean isSubmitedReview = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rating);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        mContext = AddRatingActivity.this;

        Intent intent = getIntent();
        /*String From = intent.getStringExtra("Sole Professional");
        st_id = intent.getStringExtra("id");
        st_assign_to_user_id = intent.getStringExtra("assign_to_user_id");*/

        jobId = getIntent().getStringExtra("jobId");
        secondUserId = intent.getStringExtra("secondUserId");
        professionalReview = intent.getStringExtra("professionalReview");
        isFromCashPayment = intent.getBooleanExtra("isFromCashPayment", false);
        isNeedToHideFavoriteBtn = intent.getBooleanExtra("isNeedToHideFavoriteBtn", false);
        st_rating = intent.getIntExtra("ratting", 5);

        if (isFromCashPayment) {
            tip = getIntent().getStringExtra("tip");
            total = getIntent().getStringExtra("total");

            MyLogs.w("TAG", "total :" + total + "|");
            MyLogs.w("TAG", "tip :" + tip + "|");
            MyLogs.w("TAG", "jobId :" + jobId);
        }

        initViews();

    }

    private void initViews() {
        prefsUtil = new PrefsUtil(mContext);
        et_comment = (EditText) findViewById(R.id.comment);
        rb_rate = (RatingBar) findViewById(R.id.rate);
        bt_addtofav = (Button) findViewById(R.id.addtofav);
        bt_submit = (Button) findViewById(R.id.submit);
        btnCashPayment = (Button) findViewById(R.id.btnCashPayment);
        img_back = findViewById(R.id.img_back);
        linlayCashPayment = findViewById(R.id.linlayCashPayment);
        txtMsg = findViewById(R.id.txtMsg);


        if (isFromCashPayment) {
            linlayCashPayment.setVisibility(View.VISIBLE);
            btnCashPayment.setText(total);
        } else {
            linlayCashPayment.setVisibility(View.GONE);
        }

        progressDialog = new ProgressDialog(mContext);
        progressDialog = new ProgressDialog(mContext);

        ROLE = prefsUtil.getRole();

        if (ROLE.equalsIgnoreCase("Customer")) {
            txtMsg.setText(R.string.rating_msg_two);
        } else {
            txtMsg.setText(R.string.rating_msg_two_for_customer);
        }

        if (!isNeedToHideFavoriteBtn) {
            if (!ROLE.equalsIgnoreCase("Customer")) {
                bt_addtofav.setText(R.string.add_cus_to_fav);
            } else {
                bt_addtofav.setText(R.string.add_prof_to_fav);
            }
        } else {
            bt_addtofav.setVisibility(View.GONE);
        }

        btnCashPayment.setOnClickListener(this);
        bt_addtofav.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        img_back.setOnClickListener(this);

        /*if (!professionalReview.equals("")) {
            bt_submit.setText(R.string.submited_review);
            isSubmitedReview = true;
        } else {
            bt_submit.setText(R.string.submit_review);
            isSubmitedReview = false;
        }*/

        if (st_rating > 0)
            rb_rate.setRating(st_rating);

        et_comment.setText(professionalReview);

        setupAdMob();
    }

    @Override
    public void onClick(View v) {
        if (v == btnCashPayment) {

            apiCallForPayment();

        } else if (v == bt_addtofav) {

            apiCallForAddToFavorite();

        } else if (v == bt_submit) {
            if (!isSubmitedReview) {
                st_rating = rb_rate.getRating();
                st_comment = et_comment.getText().toString().trim();

                if (st_rating == 0) {
                    Toast.makeText(mContext, getString(R.string.rating_validation), Toast.LENGTH_LONG).show();
                } else if (st_comment.equals("")) {
                    Toast.makeText(mContext, getString(R.string.review_validation), Toast.LENGTH_LONG).show();
                } else {
                    apiCallForSubmitReview(st_comment, st_rating);
                }
            } else {
                Toast.makeText(mContext, R.string.msg_review_submitted, Toast.LENGTH_SHORT).show();
            }
        } else if (v == img_back) {
            onBackPressed();
        }
    }

    private void apiCallForSubmitReview(final String review, final float rating) {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(jobId);

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("rating");
        value.add(String.valueOf(rating));

        param.add("review");
        value.add(review);

        new ParseJSON(mContext, BaseUrl.addJobReviewAndRating, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.putExtra("rating", rating);
                    intent.putExtra("review", review);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void apiCallForAddToFavorite() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("favorite_user_id");
        value.add(secondUserId);

        /*if (isFavorited) {
            param.add("unfavorite_user_id");
            value.add(list.get(position).getUserId());
        } else {
            param.add("favorite_user_id");
            value.add(list.get(position).getUserId());
        }*/

//        new ParseJSON(mContext, (isFavorited ? BaseUrl.unfavoriteUser : BaseUrl.favoriteUser), param, value, CommonPojo.class, new ParseJSON.OnResultListner() {

        new ParseJSON(mContext, BaseUrl.favoriteUser, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    bt_addtofav.setVisibility(View.GONE);
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apiCallForPayment() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(jobId);

        param.add("tip");
        value.add(tip.equals("") ? "0" : tip);

        param.add("payment_type");
        value.add("Cash");

        param.add("currency_code");
        value.add(PrefUtils.with(mContext).readString(PrefConstant.COUNTRY_CURRENCY_CODE));

       /* MyLogs.w("TAG", "PARAM " + param.toString());
        MyLogs.w("TAG", "VALUE " + value.toString());

        relBottomButton.setVisibility(View.GONE);
        txtPayBy.setVisibility(View.GONE);
        edtTip.setVisibility(View.GONE);*/

        new ParseJSON(mContext, BaseUrl.payInvoice, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;

                    linlayCashPayment.setVisibility(View.GONE);
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class Add_rating extends AsyncTask<String, Void, String> {

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
                request_main.put("rate_to_user", st_assign_to_user_id);
                request_main.put("rating", st_rating);
                request_main.put("comment", st_comment);
                request_main.put("job_id", st_id);

                MyLogs.e("URL", BaseUrl.URL + "user/add_ratings");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "user/add_ratings")
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

                Toast.makeText(mContext, "Network Error !", Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Log.v("service_return", String.valueOf(jsonObject));
                    String message = jsonObject.getString("message");

                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                    //final AlertDialog alert = builder.create();
                    builder.setTitle(R.string.app_name);
                    //builder.setIcon(R.drawable.);
                    builder.setMessage(message);
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
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

    private void setupAdMob() {

        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_professional_rating_ad_id),linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }

}
