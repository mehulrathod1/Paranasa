package com.ni.parnasa.activities.professional;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.CreateInvoiceActivity;
import com.ni.parnasa.activities.Modules.DirectionFinder;
import com.ni.parnasa.activities.Modules.DirectionFinderListener;
import com.ni.parnasa.activities.Modules.Route;
import com.ni.parnasa.models.JobDetailPojo;
import com.ni.parnasa.models.JobDetailPojoItem;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.tmpPojos.CurrentLocationPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MapForProfessionalActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, DirectionFinderListener {

    private Context mContext;
    private PrefsUtil prefsUtil;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Location locationCurrent;
    private Marker markerOfCustomer;
    private LatLng latLngOfProfessional = null, latLngOFCustomer = null;
//    private List<Polyline> polylineList = new ArrayList<>();

    private ImageView imgBack;
    private TextView txtButton1, txtButton2, txtButtonCompleteJob, btnOccupied, btnOnTheWay, btnFree, txtEta;
    private RelativeLayout adMobView;

    private String jobId = "", provider = "", strCurrentAddress = "", JOB_COMPLETE_TIME = "";

    private long locationUpdateInterval = 5000;
    private float locationUpdateDistance = 0, DEFAULT_ZOOM_LEVEL = 15f;
    private int TAB_NO = -1, REQ_CODE_CREATE_INVOICE = 42;
    private int UPDATE_INTERVAL;


    private JobDetailPojoItem jobDetailPojoItemGlobal;

    private Handler handlerForCustomerTimer = new Handler();
    private Runnable runnableForCustomerTimer = new Runnable() {
        @Override
        public void run() {
            apiCallForGetLocationOfAnyUser(jobDetailPojoItemGlobal.getJobCustomerDetail().getCustomerId(), true, false, true, "");
        }
    };

    private void startTimerForRefreshCustomerLocation() {
        handlerForCustomerTimer.postDelayed(runnableForCustomerTimer, (1000 * UPDATE_INTERVAL));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_for_professional);

        mContext = MapForProfessionalActivity.this;
        prefsUtil = new PrefsUtil(mContext);
        UPDATE_INTERVAL = prefsUtil.getUpdateInterval();

        jobId = getIntent().getStringExtra("jobId");
        TAB_NO = getIntent().getIntExtra("tabNo", -1);

        initViews();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = LocationManager.NETWORK_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationCurrent = locationManager.getLastKnownLocation(provider);

        locationManager.requestLocationUpdates(provider, locationUpdateInterval, locationUpdateDistance, locationListener);
    }


    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        btnOccupied = findViewById(R.id.btnOccupied);
        btnOnTheWay = findViewById(R.id.btnOnTheWay);
        btnFree = findViewById(R.id.btnFree);
        txtButton1 = findViewById(R.id.txtButton1);
        txtButton2 = findViewById(R.id.txtButton2);
        txtButtonCompleteJob = findViewById(R.id.txtButtonCompleteJob);
        adMobView = findViewById(R.id.adMobView);
        txtEta = findViewById(R.id.txtEta);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(this);
        imgBack.setOnClickListener(this);
        btnOccupied.setOnClickListener(this);
        btnOnTheWay.setOnClickListener(this);
        btnFree.setOnClickListener(this);
        txtButton1.setOnClickListener(this);
        txtButton2.setOnClickListener(this);
        txtButtonCompleteJob.setOnClickListener(this);

        setupAdMob();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);

        apiCallForGetJobDetail(true, true);
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == txtButton1) {
            String strText = txtButton1.getText().toString();

            if (strText.equalsIgnoreCase(getString(R.string.accept))) {

                apiCallForUpdateJobStatus("accept");

            } else if (strText.equalsIgnoreCase(getString(R.string.on_the_way))) {

                if (locationCurrent != null) {
                    try {
                        new GeoCoderHelper(MapForProfessionalActivity.this, locationCurrent.getLatitude(), locationCurrent.getLongitude(), new GeoCoderHelper.onGetAddress() {
                            @Override
                            public void onSuccess(String address, String city, String country) {
                                strCurrentAddress = address;
                            }

                            @Override
                            public void onFail() {
                                MyLogs.e("onFail", "Fail to fetch address from lat lng");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    apiCallForUpdateProfessionalStatus("onTheWay", true);

                } else {
                    Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                }

            } else if (strText.equalsIgnoreCase(getString(R.string.arrived))) {

                apiCallForArrived();

            } else if (strText.equalsIgnoreCase(getString(R.string.start_job))) {

                apiCallForUpdateJobStatus("startJob");

            } else if (strText.equalsIgnoreCase(getString(R.string.create_invoice))) {

                Intent intent = new Intent(mContext, CreateInvoiceActivity.class);
                intent.putExtra("jobId", jobId);
                intent.putExtra("jobHours", (JOB_COMPLETE_TIME.equals("") ? "00:00" : JOB_COMPLETE_TIME));
                startActivityForResult(intent, REQ_CODE_CREATE_INVOICE);

            }
        } else if (v == txtButton2) {
            String strText = txtButton2.getText().toString().trim();

            if (strText.equalsIgnoreCase(getString(R.string.reject))) {

                apiCallForUpdateJobStatus("reject");

            } else if (strText.equalsIgnoreCase(getString(R.string.navigate_me))) {

                openMapForNavigation();
            }
        } else if (v == txtButtonCompleteJob) {

            apiCallForUpdateJobStatus("completeJob");

        } else if (v == btnOccupied) {
            apiCallForUpdateProfessionalStatus("occupied", false);
        } else if (v == btnOnTheWay) {
            apiCallForUpdateProfessionalStatus("onTheWay", false);
        } else if (v == btnFree) {
            apiCallForUpdateProfessionalStatus("free", false);
        }
    }

    private void openMapForNavigation() {
        //String mapUrl = waze://?ll=%s,%s&navigate=yes,22.588541,70.5874565
//                final String url = String.format("waze://?ll=%f,%f&navigate=yes", latLng.latitude, latLng.longitude);

        if (locationCurrent != null) {

            String defaultMap = prefsUtil.getDefaultMap();

            if (defaultMap.equalsIgnoreCase("")) {
                openDialogForSelectMapToNavigate();
            } else {
                if (defaultMap.equalsIgnoreCase("G")) {
                    String mapUrl = "http://maps.google.com/maps?saddr=" + locationCurrent.getLatitude() + "," + locationCurrent.getLongitude() + "&daddr=" + jobDetailPojoItemGlobal.getJobLocation().getLat() + "," + jobDetailPojoItemGlobal.getJobLocation().getLng();
                    Log.e("TAG", "DirectionUrl : " + mapUrl);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                    startActivity(intent);
                } else if (defaultMap.equalsIgnoreCase("W")) {
                    if (appInstalledOrNot("com.waze")) {
//                        String mapUrl = "waze://?ll=%s,%s&navigate=yes," + jobDetailPojoItemGlobal.getJobLocation().getLat() + "," + jobDetailPojoItemGlobal.getJobLocation().getLng();
                        String mapUrl = "waze://?ll=" + jobDetailPojoItemGlobal.getJobLocation().getLat() + "," + jobDetailPojoItemGlobal.getJobLocation().getLng() + "&navigate=yes";
                        Log.e("TAG", "DirectionUrl : " + mapUrl);

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, R.string.no_waze_app_found, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
            /*if (latLngOfProfessional != null) {
                String mapUrl = "http://maps.google.com/maps?saddr=" + latLngOfProfessional.latitude + "," + latLngOfProfessional.longitude + "&daddr=" + jobDetailPojoItemGlobal.getJobLocation().getLat() + "," + jobDetailPojoItemGlobal.getJobLocation().getLng();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapUrl));
                startActivity(intent);
            }*/
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void openDialogForSelectMapToNavigate() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_for_map_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        LinearLayout linlayGMap = dialog.findViewById(R.id.linlayGMap);
        LinearLayout linlayWMap = dialog.findViewById(R.id.linlayWMap);

        final boolean isWazeInstall = appInstalledOrNot("com.waze");

        /*if (!isWazeInstall) {
            linlayWMap.setVisibility(View.GONE);
        }*/

        linlayGMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                prefsUtil.setDefaultMap("G");
                openMapForNavigation();
            }
        });

        linlayWMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWazeInstall) {
                    dialog.cancel();
                    prefsUtil.setDefaultMap("W");
                    openMapForNavigation();
                } else {
                    Toast.makeText(mContext, R.string.no_waze_app_found, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void apiCallForGetJobDetail(final boolean isRequiredToRefreshMap, final boolean isRequireToDrawPolyline) {
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
                    jobDetailPojoItemGlobal = pojo;
                    String JOB_STATUS = pojo.getJobStatus();

                    if (isRequiredToRefreshMap) {
                        googleMap.clear();

                        if (latLngOFCustomer == null) {
                            apiCallForGetLocationOfAnyUser(pojo.getJobCustomerDetail().getCustomerId(), true, isRequireToDrawPolyline, false, JOB_STATUS);
                        } else {
                            addCustomerPin(latLngOFCustomer);
                        }

                        if (pojo.isArrived()) {
                            apiCallForGetLocationOfAnyUser(pojo.getJobProfessionalDetail().getProfessionalId(), false, false, false, JOB_STATUS);
                        } else {
                            apiCallForGetLocationOfAnyUser(pojo.getJobProfessionalDetail().getProfessionalId(), false, isRequireToDrawPolyline, false, JOB_STATUS);
                        }
                    }


                    if (JOB_STATUS.equalsIgnoreCase("Waiting")) {
                        txtButton1.setVisibility(View.VISIBLE);
                        txtButton2.setVisibility(View.VISIBLE);

                        txtButton1.setText(R.string.accept);
                        txtButton2.setText(R.string.reject);
                    } else if (JOB_STATUS.equalsIgnoreCase("Accepted") && pojo.getProfessionalStatusForJob().equalsIgnoreCase("on_the_way")) {

                        if (pojo.isArrived()) {
                            txtEta.setVisibility(View.GONE);
                            txtButton1.setVisibility(View.VISIBLE);
                            txtButton2.setVisibility(View.GONE);

                            txtButton1.setText(R.string.start_job);
                        } else {
                            txtButton1.setVisibility(View.VISIBLE);
                            txtButton2.setVisibility(View.VISIBLE);

                            txtButton1.setText(R.string.arrived);
                            txtButton2.setText(R.string.navigate_me);
                        }
//                      startTimerForUpdateLatLng();

                    } else if (JOB_STATUS.equalsIgnoreCase("Accepted")) {

                        txtButton1.setVisibility(View.VISIBLE);
                        txtButton2.setVisibility(View.GONE);

                        txtButton1.setText(R.string.on_the_way);

                    } else if (JOB_STATUS.equalsIgnoreCase("On Going")) {

                        txtButton1.setVisibility(View.GONE);
                        txtButton2.setVisibility(View.GONE);
                        txtEta.setVisibility(View.GONE);
                        txtButtonCompleteJob.setVisibility(View.VISIBLE);

                        String strServerDateTime = pojo.getServerDatetime();
                        String strAssignDateTime = pojo.getAssignDate();

                        MyLogs.w("ZZZZ", "Server DateTime " + strServerDateTime);
                        MyLogs.w("ZZZZ", "Assign DateTime " + strAssignDateTime);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date serverDateTime = format.parse(strServerDateTime);
                            Date assignDateTime = format.parse(strAssignDateTime);
                            startTimer(assignDateTime, serverDateTime, true);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (JOB_STATUS.equalsIgnoreCase("Completed")) {

                        if (pojo.getJobInvoiceDetail().getJobBookingId().equalsIgnoreCase("")) {
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

                            Log.e("ZZZ","ok it work");

                            txtEta.setVisibility(View.GONE);
                            txtButtonCompleteJob.setVisibility(View.GONE);
                            txtButton1.setVisibility(View.VISIBLE);
                            txtButton2.setVisibility(View.GONE);
                            txtButton1.setText(R.string.create_invoice);
                        } else {
                            txtEta.setVisibility(View.GONE);
                            txtButton1.setVisibility(View.GONE);
                            txtButton2.setVisibility(View.GONE);
                        }
                    } else if (pojo.isArrived()) {
                        txtButton1.setVisibility(View.VISIBLE);
                        txtButton2.setVisibility(View.GONE);

                        txtButton1.setText(R.string.start_job);
                    }
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addCustomerPin(LatLng latLng) {

        if (markerOfCustomer != null) {
            markerOfCustomer.remove();
        }

        /*MarkerOptions options = new MarkerOptions();
        options.title("Customer");
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        markerOfCustomer = googleMap.addMarker(options);*/
    }

    private void apiCallForGetLocationOfAnyUser(String userId, final boolean isCustomer, final boolean isRequireToDrawPolyline, final boolean isFromTimer, final String JOB_STATUS) {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(userId);

        new ParseJSON(mContext, BaseUrl.getCurrentLocation, param, value, CurrentLocationPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    /*if (isFromTimer) {
                        handlerForCustomerTimer.postDelayed(runnableForCustomerTimer, (1000 * UPDATE_INTERVAL));
                    }*/

                    CurrentLocationPojo pojo = (CurrentLocationPojo) obj;

                    String strLat = pojo.getCurrentLocationPojoItem().getCurrentLatitude();
                    String strLng = pojo.getCurrentLocationPojoItem().getCurrentLongitude();

                    if (!strLat.equals("") && !strLng.equals("")) {

                        final LatLng latLngCommon = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));

                        if (isCustomer) {

                            latLngOFCustomer = latLngCommon;

                            addCustomerPin(latLngCommon);

                        } else {
//                            final LatLng latLngProfessional = new LatLng(Double.parseDouble(pojo.getJobProfessionalDetail().getLat()), Double.parseDouble(pojo.getJobProfessionalDetail().getLng()));
                            latLngOfProfessional = latLngCommon;
                            try {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(jobDetailPojoItemGlobal.getJobProfessionalDetail().getServiceIcon())
                                        .into(new SimpleTarget<Bitmap>(160, 160) {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                                                Bitmap bmp = Bitmap.createBitmap(160, 160, conf);
                                                Canvas canvas1 = new Canvas(bmp);

                                                Paint color = new Paint();
                                                //                                color.setTextSize(28);
                                                //                                color.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                                //                                color.setColor(Color.BLACK);
                                                canvas1.drawBitmap(resource, 0, 0, color);
                                                //                                canvas1.drawText(user_code, 15, 130, color);

                                                googleMap.addMarker(new MarkerOptions()
                                                        .title(jobDetailPojoItemGlobal.getJobProfessionalDetail().getProfessionalName())
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                                        .position(latLngCommon));
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCommon, DEFAULT_ZOOM_LEVEL));
//                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngCommon, DEFAULT_ZOOM_LEVEL));
                                            }
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (isRequireToDrawPolyline) {
                                if (!JOB_STATUS.equalsIgnoreCase("Completed")) {

                                    String origin = latLngOfProfessional.latitude + "," + latLngOfProfessional.longitude;
                                    String destination = jobDetailPojoItemGlobal.getJobLocation().getLat() + "," + jobDetailPojoItemGlobal.getJobLocation().getLng();

                                    try {
                                        new DirectionFinder(MapForProfessionalActivity.this, MapForProfessionalActivity.this, origin, destination).execute();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
//                        Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDirectionFinderStart() {
        MyLogs.w("TAG", "onDirectionFinderStart");
        /*if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }*/

        /*for (Polyline polyline : polylineList) {
            polyline.remove();
        }*/
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {
        String ETA = "";

        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(Color.BLACK)
                .width(15);

        for (Route routes : route) {
            for (int i = 0; i < routes.points.size(); i++) {
                polylineOptions.add(routes.points.get(i));
            }
            googleMap.addPolyline(polylineOptions);
            ETA = routes.duration.text;
        }

        txtEta.setText(getString(R.string.eta) + " : " + ETA);
    }

    private void startTimer(Date assignDateTime, Date serverDateTime, boolean wantToStartTimer) {
        //milliseconds
        long different = serverDateTime.getTime() - assignDateTime.getTime();
        MyLogs.w("TAG", "Diff " + different + " wantToStartTimer " + wantToStartTimer);

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

            MyLogs.e("TIME DIFFERENCE ", "HH MM SS : " + elapsedHours + " " + elapsedMinutes + " " + elapsedSeconds);

            if (wantToStartTimer) {
                handler.post(runnable);
            } else {
                JOB_COMPLETE_TIME = elapsedHours + ":" + elapsedMinutes;
                MyLogs.e("TAG", "TOTAL JOB HOURS " + JOB_COMPLETE_TIME);
            }
        } else {
            Toast.makeText(mContext, "Invalid date", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            updateTimer();
        }
    };
    private long elapsedTime = 0;

    private void updateTimer() {

        elapsedTime = elapsedTime + 1000;
//        MyLogs.e("ZZZ", "elapsedTime " + elapsedTime);
        long second = (elapsedTime / 1000) % 60;
        long minute = (elapsedTime / (1000 * 60)) % 60;
        long hour = (elapsedTime / (1000 * 60 * 60)) % 60;
//        String time = String.format("%02d:%02d:%02d", hour, minute, second);

        String formating = (hour <= 9 ? "0" + hour : String.valueOf(hour)) + ":" + (minute <= 9 ? "0" + minute : String.valueOf(minute)) + ":" + (second <= 9 ? "0" + second : String.valueOf(second));

        txtButtonCompleteJob.setText(formating + "\n" + getString(R.string.complete_job));

        handler.postDelayed(runnable, 1000);
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

                        /*Intent intent = new Intent("onTabRefresh");
                        intent.putExtra("refreshTabNo", TAB_NO);
                        sendBroadcast(intent);*/

                        apiCallForGetJobDetail(false, false);

                    } else if (type.equalsIgnoreCase("reject")) {

                        txtButton1.setVisibility(View.GONE);
                        txtButton2.setVisibility(View.GONE);
                        txtEta.setVisibility(View.GONE);

                        finish();

                    } else if (type.equalsIgnoreCase("startJob")) {

                        apiCallForGetJobDetail(true, false);

                    } else if (type.equalsIgnoreCase("completeJob")) {

                        stopTimer();
//                        finish();
                        apiCallForGetJobDetail(true, false);
                    }
                }
            }
        });
    }

    private void apiCallForUpdateProfessionalStatus(final String statusType, final boolean isFromBottomBtn) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("professional_id");
        value.add(prefsUtil.GetUserID());

        if (isFromBottomBtn) {
            param.add("job_id");
            value.add(jobId);

            param.add("lat");
            value.add(String.valueOf(locationCurrent.getLatitude()));

            param.add("long");
            value.add(String.valueOf(locationCurrent.getLongitude()));

            param.add("location");
            value.add(strCurrentAddress);
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

//                    CommonPojo pojo = (CommonPojo) obj;
//                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();

                    if (statusType.equalsIgnoreCase("onTheWay") && isFromBottomBtn) {

                        apiCallForGetJobDetail(true, true);
                    }

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void apiCallForArrived() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("job_id");
        value.add(jobId);

        new ParseJSON(mContext, BaseUrl.jobLocationArrived, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    txtEta.setVisibility(View.GONE);
                    apiCallForGetJobDetail(true, false);
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    Handler handler2 = new Handler();
    Runnable runnable2 = new Runnable() {
        public void run() {
            if (locationCurrent != null)
                apiCallForUpdateLatLng();
            else {
                if (ActivityCompat.checkSelfPermission(MapForProfessionalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MapForProfessionalActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager.requestLocationUpdates(provider, locationUpdateInterval, locationUpdateDistance, locationListener);

                handler2.postDelayed(runnable2, 5000);
            }
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

    public void stopTimerOfCustomerRefreshLocation() {
        try {
            handlerForCustomerTimer.removeCallbacks(runnableForCustomerTimer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        stopTimer();

        stopTimerOfUpdateLatLng();

        stopTimerOfCustomerRefreshLocation();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        super.onDestroy();
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

    private void setupAdMob() {
        View adContainer = findViewById(R.id.adMobView);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        param.addRule(RelativeLayout.CENTER_HORIZONTAL);

        RelativeLayout rel = (RelativeLayout) adContainer;

        AdView mAdView = new AdView(mContext);

        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.admob_unit_id));
        mAdView.setLayoutParams(param);
        rel.addView(mAdView);
//        ((RelativeLayout) adContainer).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(mContext, "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_CREATE_INVOICE && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }
}
