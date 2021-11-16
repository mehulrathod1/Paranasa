package com.ni.parnasa.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.MapForCustomerActivity;
import com.ni.parnasa.activities.customer.TrackingActivity;
import com.ni.parnasa.activities.customer.ViewInvoiceActivity;
import com.ni.parnasa.activities.customer.ViewInvoiceNewActivity;
import com.ni.parnasa.activities.professional.MapForProfessionalActivity;
import com.ni.parnasa.models.JobDetailPojo;
import com.ni.parnasa.models.JobDetailPojoItem;
import com.ni.parnasa.models.JobInvoiceDetail;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyDateUtil;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class JobDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private ImageView imgBack, imgProfile;
    private TextView txtUserName, txtJobBookingId, txtServiceName, txtJobDateAndTime,
            txtJobStartAt, txtJobLocation, txtDetail, txtButtonTrack, txtButton1, txtButton2, txtJobCompletedAt, txtBtnViewInMap;
    private TextView txtJobStatus, txtJobDetail, txtJobRemarks, txtJobReview;
    private RatingBar ratingProfile, ratingGiven;
    private RelativeLayout relBottomButton;
    private LinearLayout linlayJobReview, linlayJobStartAt, linlayJobCompletedAt;
    private RelativeLayout cardOfUser;

    private String jobId = "", ROLE = "", JOB_STATUS = "", JOB_COMPLETE_TIME = "", secondUserId = "";
    private int TAB_NO = -1, REQ_CODE_CREATE_INVOICE = 42;
    private long elapsedTime = 0;
    private boolean isRequiredConfirmation;
    private String professionalReview = "";
    private int rattingGloble = 0;
    private String reviewGloble = "";
    private String paymentStatus = "";


    private LocationManager locationManager;
    private Location locationCurrent;
    private double lat_job = 0.0, lng_job = 0.0;
    private boolean isOnTheWay = false;
    private String strCurentAddress = "";
    private JobInvoiceDetail invoiceDetails;
    private JobDetailPojoItem jobDetailPojoItemGlobal = null;
    private BroadcastReceiver receiver;
    private String bestProvider = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        mContext = JobDetailActivity.this;
        prefsUtil = new PrefsUtil(mContext);

        jobId = getIntent().getStringExtra("jobId");
        TAB_NO = getIntent().getIntExtra("tabNo", -1);
        isRequiredConfirmation = getIntent().getBooleanExtra("isRequiredConfirmation", false); // this is handle for notification while cash payment confirmation

        ROLE = prefsUtil.getRole();

        init();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                apiCallForGetJobDetail();
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        bestProvider = LocationManager.NETWORK_PROVIDER;
        locationCurrent = locationManager.getLastKnownLocation(bestProvider);
        locationManager.requestLocationUpdates(bestProvider, 5000, 0, locationListener);

    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == txtButtonTrack) {
            if (locationCurrent != null) {
                if (jobDetailPojoItemGlobal != null) {
                    Intent action = new Intent(mContext, TrackingActivity.class);
                    action.putExtra("rideId", jobDetailPojoItemGlobal.getRideDetail().getRideId());
                    action.putExtra("jobId", jobId);
                    action.putExtra("professionalId", jobDetailPojoItemGlobal.getJobProfessionalDetail().getProfessionalId());
                    action.putExtra("customerId", jobDetailPojoItemGlobal.getJobCustomerDetail().getCustomerId());
                    action.putExtra("ride_start_lat", jobDetailPojoItemGlobal.getRideDetail().getRideStartLat());
                    action.putExtra("ride_start_lng", jobDetailPojoItemGlobal.getRideDetail().getRideStartLng());
                    action.putExtra("ride_end_lat", jobDetailPojoItemGlobal.getRideDetail().getRideEndLat());
                    action.putExtra("ride_end_lng", jobDetailPojoItemGlobal.getRideDetail().getRideEndLng());
                    action.putExtra("service_icon_url", jobDetailPojoItemGlobal.getJobProfessionalDetail().getServiceIcon());
                    startActivity(action);
                } else {
                    Toast.makeText(mContext, R.string.ride_info_not_found, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
            }
        } else if (v == txtBtnViewInMap) {

            if (ROLE.equalsIgnoreCase("Customer")) {
                Intent intent = new Intent(mContext, MapForCustomerActivity.class);
                intent.putExtra("jobId", jobId);
                intent.putExtra("tabNo", TAB_NO);
                startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, MapForProfessionalActivity.class);
                intent.putExtra("jobId", jobId);
                intent.putExtra("tabNo", TAB_NO);
                startActivity(intent);
            }
        } else if (v == txtButton1) {

            String strText = txtButton1.getText().toString().trim();
            if (strText.equalsIgnoreCase(getString(R.string.accept))) {
                apiCallForUpdateJobStatus("accept");
            } else if (strText.equalsIgnoreCase(getString(R.string.start_job))) {
                apiCallForUpdateJobStatus("startJob");
            } else if (strText.equalsIgnoreCase(getString(R.string.on_the_way))) {
                if (locationCurrent != null) {
                    try {
                        new GeoCoderHelper(JobDetailActivity.this, locationCurrent.getLatitude(), locationCurrent.getLongitude(), new GeoCoderHelper.onGetAddress() {
                            @Override
                            public void onSuccess(String address, String city, String country) {
                                strCurentAddress = address;
                            }

                            @Override
                            public void onFail() {
                                MyLogs.e("onFail", "Fail to fetch address from lat lng");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    apiCallForUpdateProfessionalStatus("onTheWay");
                } else {
                    Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                }
            } else if (strText.equalsIgnoreCase(getString(R.string.create_invoice))) {
                Intent intent = new Intent(mContext, CreateInvoiceActivity.class);
                intent.putExtra("jobId", jobId);
                intent.putExtra("jobHours", (JOB_COMPLETE_TIME.equals("") ? "00:00" : JOB_COMPLETE_TIME));
                startActivityForResult(intent, REQ_CODE_CREATE_INVOICE);
            } else if (strText.equalsIgnoreCase(getString(R.string.view_invoice)) || strText.equalsIgnoreCase(getString(R.string.make_payment))) {

                Intent intent;
                if(paymentStatus.equalsIgnoreCase("Paid")) {
                    intent = new Intent(mContext, ViewInvoiceNewActivity.class);
                    intent.putExtra("jobId", jobId);
                    intent.putExtra("invoiceDetail", jobDetailPojoItemGlobal.getJobInvoiceDetail());
                }else {

                    intent = new Intent(mContext, ViewInvoiceActivity.class);
                    intent.putExtra("jobId", jobId);
                    intent.putExtra("secondUserId", secondUserId);
                    intent.putExtra("professionalReview", reviewGloble);
                    intent.putExtra("rating", rattingGloble);
                    intent.putExtra("buttonName", strText);
                    intent.putExtra("paymentStatus", paymentStatus);
                    if (ROLE.equalsIgnoreCase("Customer")) {
                        intent.putExtra("isNeedToHideFavoriteBtn", jobDetailPojoItemGlobal.getJobProfessionalDetail().getCustomer_favorited_professional());
                    } else {
                        intent.putExtra("isNeedToHideFavoriteBtn", jobDetailPojoItemGlobal.getJobCustomerDetail().getProfessional_favorited_customer());
                    }
                }
                startActivity(intent);
            }

        } else if (v == txtButton2) {
            String strText = txtButton2.getText().toString().trim();

            if (strText.equalsIgnoreCase(getString(R.string.reject))) {
                apiCallForUpdateJobStatus("reject");
            } else if (strText.equalsIgnoreCase(getString(R.string.navigate_me))) {
                if (locationCurrent != null) {
                    String mapUrl = "http://maps.google.com/maps?saddr="
                            + locationCurrent.getLatitude() + "," + locationCurrent.getLongitude() +
                            "&daddr=" + lat_job + "," + lng_job;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                }
            } else if (strText.equalsIgnoreCase(getString(R.string.give_rating)) || strText.equalsIgnoreCase(getString(R.string.edit_review))) {

                Intent intent = new Intent(mContext, AddRatingActivity.class);
                intent.putExtra("jobId", jobId);
                intent.putExtra("secondUserId", secondUserId);
                intent.putExtra("professionalReview", reviewGloble);
                intent.putExtra("ratting", rattingGloble);
                if (ROLE.equalsIgnoreCase("Customer")) {
                    intent.putExtra("isNeedToHideFavoriteBtn", jobDetailPojoItemGlobal.getJobProfessionalDetail().getCustomer_favorited_professional());
                } else {
                    intent.putExtra("isNeedToHideFavoriteBtn", jobDetailPojoItemGlobal.getJobCustomerDetail().getProfessional_favorited_customer());
                }
                startActivityForResult(intent, 56);

            } else if (strText.equalsIgnoreCase(getString(R.string.complete_job))) {

                apiCallForUpdateProfessionalStatus("free");

                apiCallForUpdateJobStatus("completeJob");

            } /*else if (strText.equalsIgnoreCase(getString(R.string.on_the_way))) {

                if (locationCurrent != null) {

                    try {
                        new GeoCoderHelper(JobDetailActivity.this, locationCurrent.getLatitude(), locationCurrent.getLongitude(), new GeoCoderHelper.onGetAddress() {
                            @Override
                            public void onSuccess(String address, String city, String country) {
                                strCurentAddress = address;
                            }

                            @Override
                            public void onFail() {
                                MyLogs.e("onFail", "Fail to fetch address from lat lng");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    apiCallForUpdateProfessionalStatus("onTheWay");
                } else {
                    Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                }
            }*/

        } else if (v == txtJobLocation) {

            if (locationCurrent != null) {
                Intent action = new Intent(mContext, MapWithPolylineActivity.class);
                action.putExtra("cur_lat", String.valueOf(locationCurrent.getLatitude()));
                action.putExtra("cur_lng", String.valueOf(locationCurrent.getLongitude()));
                action.putExtra("lat_job", String.valueOf(lat_job));
                action.putExtra("lng_job", String.valueOf(lng_job));
                startActivityForResult(action, 57);
            } else {
                Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
            }
        } else if (v == cardOfUser) {
            if (jobDetailPojoItemGlobal != null) {
                Intent intent = new Intent(mContext, ProfileNewActivity.class);
                if (ROLE.equalsIgnoreCase("Customer")) {
                    intent.putExtra("userId", jobDetailPojoItemGlobal.getJobProfessionalDetail().getProfessionalId());
                } else {
                    intent.putExtra("userId", jobDetailPojoItemGlobal.getJobCustomerDetail().getCustomerId());
                }
                startActivity(intent);
            } else {
                Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        }
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            locationCurrent = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void init() {
        cardOfUser = findViewById(R.id.cardOfUser);
        imgBack = findViewById(R.id.imgBack);
        imgProfile = findViewById(R.id.imgProfile);
        txtUserName = findViewById(R.id.txtUserName);
        txtDetail = findViewById(R.id.txtDetail);
        ratingProfile = findViewById(R.id.ratingProfile);
        txtJobBookingId = findViewById(R.id.txtJobBookingId);
        txtServiceName = findViewById(R.id.txtServiceName);
        txtJobDateAndTime = findViewById(R.id.txtJobDateAndTime);
        txtJobStartAt = findViewById(R.id.txtJobStartAt);
        txtJobLocation = findViewById(R.id.txtJobLocation);
        relBottomButton = findViewById(R.id.relBottomButton);
        txtButtonTrack = findViewById(R.id.txtButtonTrack);
        txtButton1 = findViewById(R.id.txtButton1);
        txtButton2 = findViewById(R.id.txtButton2);
//        txtSingleButton = findViewById(R.id.txtSingleButton);
        txtJobStatus = findViewById(R.id.txtJobStatus);
        txtJobDetail = findViewById(R.id.txtJobDetail);
        txtJobRemarks = findViewById(R.id.txtJobRemarks);
        txtJobCompletedAt = findViewById(R.id.txtJobCompletedAt);
        txtBtnViewInMap = findViewById(R.id.txtBtnViewInMap);

        linlayJobReview = findViewById(R.id.linlayJobReview);
        linlayJobStartAt = findViewById(R.id.linlayJobStartAt);
        linlayJobCompletedAt = findViewById(R.id.linlayJobCompletedAt);

        ratingGiven = findViewById(R.id.ratingGiven);
        txtJobReview = findViewById(R.id.txtJobReview);

        if (!ROLE.equalsIgnoreCase("Customer")) {
            txtDetail.setText(R.string.customer_detail);
        } else {
            txtDetail.setText(R.string.professional_detail);
        }

        imgBack.setOnClickListener(this);
        txtButtonTrack.setOnClickListener(this);
        txtButton1.setOnClickListener(this);
        txtButton2.setOnClickListener(this);
        txtJobLocation.setOnClickListener(this);
        txtBtnViewInMap.setOnClickListener(this);
        cardOfUser.setOnClickListener(this);

        if (isRequiredConfirmation) {
            openDialogForCashConfirmation();
        }

        setupAdMob();
    }


    private void openDialogForCashConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.cash_payment_conform);
        builder.setMessage(R.string.cash_payment_conform_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                apiCallForConfirmPayment(dialog);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void apiCallForConfirmPayment(final DialogInterface dialog) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(jobId);



        new ParseJSON(mContext, BaseUrl.cashPaymentConfirmation, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {

            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    dialog.dismiss();
                    apiCallForGetJobDetail();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apiCallForUpdateJobStatus(final String type) {

        // type = accept | reject | startJob | completeJob

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(jobId);

        String strUrl = "";

        if (type.equalsIgnoreCase("accept")) {
            strUrl = BaseUrl.jobStatusAccept;
        } else if (type.equalsIgnoreCase("reject")) {
            strUrl = BaseUrl.jobStatusReject;
        } else if (type.equalsIgnoreCase("startJob")) {
            strUrl = BaseUrl.jobStatusStart;
        } else if (type.equalsIgnoreCase("completeJob")) {
            strUrl = BaseUrl.jobStatusEnd;
        }

        new ParseJSON(mContext, strUrl, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    CommonPojo pojo = (CommonPojo) obj;
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();

                    if (type.equalsIgnoreCase("accept")) {

                        /*txtJobStatus.setText(R.string.accept);
                        txtButton1.setVisibility(View.VISIBLE);
                        txtButton2.setVisibility(View.GONE);

                        txtButton1.setText(R.string.on_the_way);*/

                        apiCallForGetJobDetail();

                        Intent intent = new Intent("onTabRefresh");
                        intent.putExtra("refreshTabNo", TAB_NO);
                        sendBroadcast(intent);

                    } else if (type.equalsIgnoreCase("reject")) {
                        txtJobStatus.setText(R.string.rejected);
                        relBottomButton.setVisibility(View.GONE);

                        Intent intent = new Intent("onTabRefresh");
                        intent.putExtra("refreshTabNo", TAB_NO);
                        sendBroadcast(intent);

                    } else if (type.equalsIgnoreCase("startJob")) {

                        Intent intent = new Intent("onTabRefresh");
                        intent.putExtra("refreshTabNo", TAB_NO);
                        sendBroadcast(intent);

                        stopTimerOfUpdateLatLng();

                        apiCallForGetJobDetail();

                    } else if (type.equalsIgnoreCase("completeJob")) {

                        Intent intent = new Intent("onTabRefresh");
                        intent.putExtra("refreshTabNo", TAB_NO);
                        sendBroadcast(intent);

                        stopTimer();

                        apiCallForGetJobDetail();
                    }
                }
            }
        });
    }

    private void apiCallForGetJobDetail() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(jobId);

        new ParseJSON(mContext, BaseUrl.getJobDetail, param, value, JobDetailPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    JobDetailPojoItem pojo = ((JobDetailPojo) obj).getJobDetailPojoItem();

                    String strImagePath = "";

                    txtJobBookingId.setText(pojo.getJobId());
                    txtServiceName.setText(pojo.getService());
                    txtJobStatus.setText(pojo.getJobStatus());
                    txtJobLocation.setText(pojo.getJobLocation().getCustomerAddress());
                    txtJobDetail.setText(pojo.getJob());
                    txtJobRemarks.setText(pojo.getRemarks());

                    JOB_STATUS = pojo.getJobStatus();
                    paymentStatus = pojo.getJobInvoiceDetail().getPaymentStatus();
                    invoiceDetails = pojo.getJobInvoiceDetail();
                    professionalReview = pojo.getJobRate().getJobRateCustomer().getReviewForCustomer();

                    if (ROLE.equalsIgnoreCase("Customer")) {
                        rattingGloble = pojo.getJobRate().getJobRateProfessional().getProfessionalRating();
                        reviewGloble = pojo.getJobRate().getJobRateProfessional().getReviewForProfessional();
                    } else {
                        rattingGloble = pojo.getJobRate().getJobRateCustomer().getCustomerRating();
                        reviewGloble = pojo.getJobRate().getJobRateCustomer().getReviewForCustomer();
                    }

                    try {
                        lat_job = Double.parseDouble(pojo.getJobLocation().getLat());
                        lng_job = Double.parseDouble(pojo.getJobLocation().getLng());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    jobDetailPojoItemGlobal = pojo;

                    if (ROLE.equalsIgnoreCase("Customer")) {
                        secondUserId = pojo.getJobProfessionalDetail().getProfessionalId();

                        if (JOB_STATUS.equalsIgnoreCase("Accepted") && pojo.getProfessionalStatusForJob().equalsIgnoreCase("on_the_way") && pojo.isArrived()) {
                            txtButtonTrack.setVisibility(View.GONE);
                            txtBtnViewInMap.setVisibility(View.GONE);

                        } else if (JOB_STATUS.equalsIgnoreCase("Accepted") && pojo.getProfessionalStatusForJob().equalsIgnoreCase("on_the_way")) {
                            txtButtonTrack.setVisibility(View.VISIBLE);
                            txtBtnViewInMap.setVisibility(View.GONE);
//                            jobDetailPojoItemGlobal = pojo;
                        } else if (JOB_STATUS.equalsIgnoreCase("Completed") || JOB_STATUS.equalsIgnoreCase("Rejected")) {
                            txtButtonTrack.setVisibility(View.GONE);
                            txtBtnViewInMap.setVisibility(View.GONE);
                        } else {
                            txtButtonTrack.setVisibility(View.GONE);
                            txtBtnViewInMap.setVisibility(View.VISIBLE);
                        }

                        txtUserName.setText(pojo.getJobProfessionalDetail().getProfessionalName());
                        strImagePath = pojo.getJobProfessionalDetail().getProfessionalProfile();

                        try {
                            if (pojo.getJobCustomerDetail().getCustomerAverageRating() == 0) {
                                ratingProfile.setRating(5f);
                            } else {
                                ratingProfile.setRating(pojo.getJobCustomerDetail().getCustomerAverageRating());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        secondUserId = pojo.getJobCustomerDetail().getCustomerId();

                        txtUserName.setText(pojo.getJobCustomerDetail().getCustomerName());
                        strImagePath = pojo.getJobCustomerDetail().getCustomerProfile();
                        try {
                            if (pojo.getJobCustomerDetail().getCustomerAverageRating() == 0) {
                                ratingProfile.setRating(5f);
                            } else {
                                ratingProfile.setRating(pojo.getJobCustomerDetail().getCustomerAverageRating());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Glide.with(mContext).asBitmap()
                            .load(strImagePath)
                            .into(imgProfile);

                    /** Manage Date of job*/
                    manageJobDateStatus(pojo);

                    /**
                     * Manage button as per user and job status */
                    if (ROLE.equalsIgnoreCase("Customer")) {
                        if (JOB_STATUS.equalsIgnoreCase("completed")) {

                            relBottomButton.setVisibility(View.VISIBLE);
                            txtButton1.setVisibility(View.GONE);
//                            txtButton2.setVisibility(View.GONE);

                            int proRating = pojo.getJobRate().getJobRateProfessional().getProfessionalRating();

                            String job_booking_id = pojo.getJobInvoiceDetail().getJobBookingId();
                            String paymentStatus = pojo.getJobInvoiceDetail().getPaymentStatus();
                            String paymentmethod = pojo.getJobInvoiceDetail().getPaymentMethod();

                            if (!job_booking_id.equals("")) {
                                if (paymentStatus.equalsIgnoreCase("pending") && paymentmethod.equalsIgnoreCase("")) {
                                    txtButton1.setVisibility(View.VISIBLE);
                                    txtButton1.setText(R.string.make_payment);
                                }

                                if (paymentStatus.equalsIgnoreCase("paid")) {
                                    txtButton1.setVisibility(View.VISIBLE);
                                    txtButton1.setText(R.string.view_invoice);
                                }
                            }

                            if (proRating != 0) {

                                linlayJobReview.setVisibility(View.VISIBLE);
                                ratingGiven.setVisibility(View.VISIBLE);
                                txtJobReview.setText(pojo.getJobRate().getJobRateProfessional().getReviewForProfessional());
                                ratingGiven.setRating(pojo.getJobRate().getJobRateProfessional().getProfessionalRating());

                                txtButton2.setVisibility(View.VISIBLE);
                                txtButton2.setText(R.string.edit_review);

                            } else {
                                txtButton2.setVisibility(View.VISIBLE);
                                txtButton2.setText(R.string.give_rating);
                            }
                        }
                    } else {
                        manageProfessionalDetail(pojo);
                    }
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void manageJobDateStatus(JobDetailPojoItem pojo) {
        try {
            txtJobDateAndTime.setText(MyDateUtil.getInstance().formateDateTime(pojo.getJobDate()));
        } catch (ParseException e) {
            e.printStackTrace();
            txtJobDateAndTime.setText(pojo.getJobDate());
        }

        if (JOB_STATUS.equalsIgnoreCase("Completed")) {
            linlayJobStartAt.setVisibility(View.VISIBLE);
            linlayJobCompletedAt.setVisibility(View.VISIBLE);

            try {
                txtJobStartAt.setText(MyDateUtil.getInstance().formateDateTime(pojo.getAssignDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                txtJobStartAt.setText(pojo.getAssignDate());
            }
            try {
                txtJobCompletedAt.setText(MyDateUtil.getInstance().formateDateTime(pojo.getCompletedDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                txtJobCompletedAt.setText(pojo.getCompletedDate());
            }

        } else if (JOB_STATUS.equalsIgnoreCase("On Going")) {
            linlayJobStartAt.setVisibility(View.VISIBLE);
            linlayJobCompletedAt.setVisibility(View.GONE);

            try {
                txtJobStartAt.setText(MyDateUtil.getInstance().formateDateTime(pojo.getAssignDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                txtJobStartAt.setText(pojo.getAssignDate());
            }

        } else {
            linlayJobStartAt.setVisibility(View.GONE);
            linlayJobCompletedAt.setVisibility(View.GONE);
        }
    }

    /**
     * Must has to be call when professional login
     */
    private void manageProfessionalDetail(JobDetailPojoItem pojo) {

        if (JOB_STATUS.equalsIgnoreCase("Waiting")) {

            txtBtnViewInMap.setVisibility(View.VISIBLE);
            txtBtnViewInMap.setText(R.string.view_in_map);

        } else if (JOB_STATUS.equalsIgnoreCase("Accepted") && pojo.getProfessionalStatusForJob().equalsIgnoreCase("on_the_way")) {

            txtBtnViewInMap.setVisibility(View.VISIBLE);
            txtBtnViewInMap.setText(R.string.view_in_map);

        } else if (JOB_STATUS.equalsIgnoreCase("Accepted")) {

            txtBtnViewInMap.setVisibility(View.VISIBLE);
            txtBtnViewInMap.setText(R.string.view_in_map);

        } else if (JOB_STATUS.equalsIgnoreCase("On Going")) {

            txtBtnViewInMap.setVisibility(View.VISIBLE);
            txtBtnViewInMap.setText(R.string.view_in_map);

        } else if (JOB_STATUS.equalsIgnoreCase("Rejected")) {
            relBottomButton.setVisibility(View.GONE);
            txtBtnViewInMap.setVisibility(View.GONE);

        } else if (JOB_STATUS.equalsIgnoreCase("Completed")) {

            txtBtnViewInMap.setVisibility(View.GONE);

            int customerRating = pojo.getJobRate().getJobRateCustomer().getCustomerRating();
            String jobBookingId = pojo.getJobInvoiceDetail().getJobBookingId();

            if (customerRating == 0 && jobBookingId.equals("")) {
                relBottomButton.setVisibility(View.VISIBLE);
                txtButton1.setVisibility(View.VISIBLE);
                txtButton2.setVisibility(View.VISIBLE);
                txtButton1.setText(R.string.create_invoice);
                txtButton2.setText(getString(R.string.give_rating));

            } else if (customerRating != 0 && jobBookingId.equals("")) {
                relBottomButton.setVisibility(View.VISIBLE);
                txtButton1.setVisibility(View.VISIBLE);
                txtButton2.setVisibility(View.VISIBLE);
                txtButton1.setText(R.string.create_invoice);
                txtButton2.setText(getString(R.string.edit_review));

            } else if (customerRating == 0 && !jobBookingId.equals("")) {
                relBottomButton.setVisibility(View.VISIBLE);
                txtButton1.setVisibility(View.VISIBLE);
                txtButton2.setVisibility(View.VISIBLE);
                txtButton1.setText(R.string.view_invoice);
                txtButton2.setText(getString(R.string.give_rating));

            } else if (customerRating != 0 && !jobBookingId.equals("")) {
                linlayJobReview.setVisibility(View.VISIBLE);
                ratingGiven.setVisibility(View.VISIBLE);

                relBottomButton.setVisibility(View.VISIBLE);
                txtButton1.setVisibility(View.VISIBLE);
                txtButton2.setVisibility(View.VISIBLE);
                txtButton2.setText(getString(R.string.edit_review));

                txtButton1.setText(R.string.view_invoice);
                txtJobReview.setText(pojo.getJobRate().getJobRateCustomer().getReviewForCustomer());
                ratingGiven.setRating(customerRating);

            } else {
                linlayJobReview.setVisibility(View.VISIBLE);
                ratingGiven.setVisibility(View.VISIBLE);

                txtButton1.setVisibility(View.VISIBLE);
                txtButton1.setText(R.string.view_invoice);
                txtButton2.setVisibility(View.VISIBLE);
                txtButton2.setText(getString(R.string.edit_review));

                txtJobReview.setText(pojo.getJobRate().getJobRateCustomer().getReviewForCustomer());
                ratingGiven.setRating(customerRating);
            }

            String strJobCompletedDate = pojo.getCompletedDate();
            String strJobAssignDate = pojo.getAssignDate();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date jobCompletedDate = format.parse(strJobCompletedDate);
                Date jobAssignDate = format.parse(strJobAssignDate);
                startTimer(jobAssignDate, jobCompletedDate, false);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            /** This is for confirm cash payment*/
            if (pojo.getJobInvoiceDetail().getPaymentMethod().equalsIgnoreCase("Cash") && pojo.getJobInvoiceDetail().getPaymentStatus().equalsIgnoreCase("Pending")) {
                openDialogForCashConfirmation();
            }

        } else {
            Toast.makeText(mContext, "Unknown job status found", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler2 = new Handler();

    Runnable runnable2 = new Runnable() {
        public void run() {
            if (jobDetailPojoItemGlobal != null)
                apiCallForUpdateLatLng();
        }
    };

    private void startTimerForUpdateLatLng() {
        handler2.post(runnable2);
    }

    private void apiCallForUpdateLatLng() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("latitude");
        value.add(jobDetailPojoItemGlobal.getRideDetail().getRideStartLat());

        param.add("longitude");
        value.add(jobDetailPojoItemGlobal.getRideDetail().getRideStartLng());

        param.add("job_id");
        value.add(jobId);

        param.add("ride_id");
        value.add(jobDetailPojoItemGlobal.getRideDetail().getRideId());

        param.add("currentLat");
        value.add(String.valueOf(locationCurrent.getLatitude()));

        param.add("currentLong");
        value.add(String.valueOf(locationCurrent.getLongitude()));

        new ParseJSON(mContext, BaseUrl.trackingUpdateLatLng, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    handler2.postDelayed(runnable2, 5000);
                } else {
                    MyLogs.e("TAG", "Error" + obj.toString());
                }
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            updateTimer();
        }
    };

    private void updateTimer() {

        elapsedTime = elapsedTime + 1000;
//        MyLogs.e("ZZZ", "elapsedTime " + elapsedTime);
        long second = (elapsedTime / 1000) % 60;
        long minute = (elapsedTime / (1000 * 60)) % 60;
        long hour = (elapsedTime / (1000 * 60 * 60)) % 60;
//        String time = String.format("%02d:%02d:%02d", hour, minute, second);

        String formating = (hour <= 9 ? "0" + hour : String.valueOf(hour)) + ":" + (minute <= 9 ? "0" + minute : String.valueOf(minute)) + ":" + (second <= 9 ? "0" + second : String.valueOf(second));

        txtButton1.setText(formating);

//        txtButton1.setText(hour + ":" + minute + ":" + second);

        handler.postDelayed(runnable, 1000);
    }

    private void startTimer(Date assignDateTime, Date serverDateTime, boolean wantToStartTimer) {
        //milliseconds
        long different = serverDateTime.getTime() - assignDateTime.getTime();
//        MyLogs.w("TAG", "Diff " + different + " wantToStartTimer " + wantToStartTimer);

        if (different >= 0) {

            elapsedTime = different;
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            /*long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;*/

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

//            MyLogs.w("TIME DIFFERENCE ", "HH MM SS : " + elapsedHours + " " + elapsedMinutes + " " + elapsedSeconds);

            if (wantToStartTimer) {
                handler.post(runnable);
            } else {
                JOB_COMPLETE_TIME = elapsedHours + ":" + elapsedMinutes;
                MyLogs.w("TAG", "TOTAL JOB HOURS " + JOB_COMPLETE_TIME);
            }
        } else {
            Toast.makeText(mContext, "Invalid date", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopTimer() {
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopTimerOfUpdateLatLng() {
        try {
            handler2.removeCallbacks(runnable2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_CREATE_INVOICE && resultCode == Activity.RESULT_OK) {
            txtButton1.setVisibility(View.GONE);
        } else if (requestCode == 56 && resultCode == Activity.RESULT_OK) {
            /*linlayJobReview.setVisibility(View.VISIBLE);
            ratingGiven.setVisibility(View.VISIBLE);
            ratingGiven.setRating(data.getFloatExtra("rating", 0.0f));
            txtJobReview.setText(data.getStringExtra("review"));

            if (!ROLE.equalsIgnoreCase("Customer")) {
                relBottomButton.setVisibility(View.GONE);
            } else {
                txtButton1.setVisibility(View.VISIBLE);
                txtButton2.setVisibility(View.GONE);
                txtButton1.setText("View Invoice");
            }*/
        }
    }

    private void apiCallForUpdateProfessionalStatus(final String statusType) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("professional_id");
        value.add(prefsUtil.GetUserID());

        param.add("job_id");
        value.add(jobId);

        if (statusType.equalsIgnoreCase("onTheWay")) {

            param.add("location");
            value.add(strCurentAddress);

            param.add("lat");
            value.add(String.valueOf(locationCurrent.getLatitude()));

            param.add("long");
            value.add(String.valueOf(locationCurrent.getLongitude()));
        }

        String strUrl = "";

        if (statusType.equalsIgnoreCase("occupied")) {
            strUrl = BaseUrl.profStatusOccupy;
        } else if (statusType.equalsIgnoreCase("onTheWay")) {
            strUrl = BaseUrl.profStatusOnTheWay;
        } else {
            strUrl = BaseUrl.profStatusFree;
        }

        new ParseJSON(mContext, strUrl, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    if (statusType.equalsIgnoreCase("onTheWay")) {
                        /*isOnTheWay = true;
                        txtButton1.setVisibility(View.VISIBLE);
                        txtButton2.setVisibility(View.VISIBLE);
                        txtButton1.setText(R.string.start_job);
                        txtButton2.setText(R.string.navigate_me);

                        startTimerForUpdateLatLng();*/

                        apiCallForGetJobDetail();
                    }

                    CommonPojo pojo = (CommonPojo) obj;
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_job_detail_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        BaseUrl.isJobDetailOpen = true;
        BaseUrl.jobIdTmp = jobId;
        registerReceiver(receiver, new IntentFilter("refreshJob"));

        apiCallForGetJobDetail();
    }

    @Override
    protected void onPause() {
        BaseUrl.isJobDetailOpen = false;
        BaseUrl.jobIdTmp = "";

        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        BaseUrl.isJobDetailOpen = false;
        BaseUrl.jobIdTmp = "";

        stopTimer();

        stopTimerOfUpdateLatLng();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        super.onDestroy();
    }
}
