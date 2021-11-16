package com.ni.parnasa.activities.customer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.Modules.DirectionFinder;
import com.ni.parnasa.activities.Modules.DirectionFinderListener;
import com.ni.parnasa.activities.Modules.Route;
import com.ni.parnasa.models.JobDetailPojo;
import com.ni.parnasa.models.JobDetailPojoItem;
import com.ni.parnasa.tmpPojos.CurrentLocationPojo;
import com.ni.parnasa.utils.BaseUrl;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MapForCustomerActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, DirectionFinderListener {

    private Context mContext;
    private PrefsUtil prefsUtil;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Location locationCurrent;
    private Marker markerOfCustomer, markerOfProfessional;
    private LatLng latLngOfProfessional = null, latLngOfCustomer = null;
    private List<Polyline> polylineList = new ArrayList<>();
    private BroadcastReceiver receiver;

    private ImageView imgBack;
    private RelativeLayout adMobView;
    private TextView txtEta, txtTimer;

    private String jobId = "", provider = "";
    private long locationUpdateInterval = 5000;
    private float locationUpdateDistance = 0, DEFAULT_ZOOM_LEVEL = 14f;
    private int UPDATE_INTERVAL;
    private boolean isOpenFromFcmService;

    private JobDetailPojoItem jobDetailPojoItemGlobal;

    private Handler handlerForProfessionalTimer = new Handler();
    private Runnable runnableForProfessionalTimer = new Runnable() {
        @Override
        public void run() {
            if (jobDetailPojoItemGlobal != null) {
                apiCallForGetLocationOfAnyUser(jobDetailPojoItemGlobal.getJobProfessionalDetail().getProfessionalId(), false, true, true);
            } else {
                handlerForProfessionalTimer.postDelayed(runnableForProfessionalTimer, (1000 * UPDATE_INTERVAL));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_for_customer);

        mContext = MapForCustomerActivity.this;
        prefsUtil = new PrefsUtil(mContext);
        UPDATE_INTERVAL = prefsUtil.getUpdateInterval();

        jobId = getIntent().getStringExtra("jobId");
        isOpenFromFcmService = getIntent().getBooleanExtra("isOpenFromFcmService", false);
//        TAB_NO = getIntent().getIntExtra("tabNo", -1);

        initViews();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean isCompleteJobOrReject = intent.getBooleanExtra("isCompleteJobOrReject", false);
                boolean needToStopTime = intent.getBooleanExtra("needToStopTime", false);

                if (needToStopTime) {
                    stopTimerOfProfessionalRefreshLocation();
                }

                if (isCompleteJobOrReject) {
                    finish();
                } else {
                    apiCallForGetJobDetail(false);
                }
            }
        };

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
        adMobView = findViewById(R.id.adMobView);
        txtEta = findViewById(R.id.txtEta);
        txtTimer = findViewById(R.id.txtTimer);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(this);
        imgBack.setOnClickListener(this);

        setupAdMob();
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);

        if (isOpenFromFcmService) {
            apiCallForGetJobDetail(false);
        } else {
            apiCallForGetJobDetail(true);
        }
    }

    private void apiCallForGetJobDetail(final boolean isNeedToStartTimer) {
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

                    if (JOB_STATUS.equalsIgnoreCase("On Going")) {// || JOB_STATUS.equalsIgnoreCase("Completed")) {
                        googleMap.clear();
                        txtEta.setVisibility(View.INVISIBLE);
                        txtTimer.setVisibility(View.VISIBLE);

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

                        apiCallForGetLocationOfAnyUser(pojo.getJobProfessionalDetail().getProfessionalId(), false, false, false);
                        apiCallForGetLocationOfAnyUser(pojo.getJobCustomerDetail().getCustomerId(), true, false, false);
                    } else {
                        if (isNeedToStartTimer) {

                            if (!pojo.isArrived()) {
                                startTimerForRefreshProfessionalLocation();
                            } else {
                                txtEta.setVisibility(View.INVISIBLE);
                            }

                        } else if (JOB_STATUS.equalsIgnoreCase("Accepted") && pojo.getProfessionalStatusForJob().equalsIgnoreCase("on_the_way")) {

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
                            finish();
                        } else {
                            apiCallForGetLocationOfAnyUser(pojo.getJobProfessionalDetail().getProfessionalId(), false, true, false);
                        }
                    }
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startTimer(Date assignDateTime, Date serverDateTime, boolean wantToStartTimer) {

        long different = serverDateTime.getTime() - assignDateTime.getTime();
        MyLogs.w("TAG", "Diff " + different + " wantToStartTimer " + wantToStartTimer);

        if (different >= 0) {

            elapsedTime = different;
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            MyLogs.w("TIME DIFFERENCE ", "HH MM SS : " + elapsedHours + " " + elapsedMinutes + " " + elapsedSeconds);

            if (wantToStartTimer) {
                handler.post(runnable);
            } else {
                String JOB_COMPLETE_TIME = elapsedHours + ":" + elapsedMinutes;
                MyLogs.w("TAG", "TOTAL JOB HOURS " + JOB_COMPLETE_TIME);
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
        long second = (elapsedTime / 1000) % 60;
        long minute = (elapsedTime / (1000 * 60)) % 60;
        long hour = (elapsedTime / (1000 * 60 * 60)) % 60;

        String formating = (hour <= 9 ? "0" + hour : String.valueOf(hour)) + ":" + (minute <= 9 ? "0" + minute : String.valueOf(minute)) + ":" + (second <= 9 ? "0" + second : String.valueOf(second));

        txtTimer.setText(formating);

        handler.postDelayed(runnable, 1000);
    }

    public void stopTimer() {
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apiCallForGetLocationOfAnyUser(String userId, final boolean isCustomer, final boolean isRequireToDrawPolyline, final boolean isFromTimer) {

        MyLogs.w("TAG", "userId:" + userId + " isCustomer:" + isCustomer + " isRequireToDrawPolyline:" + isRequireToDrawPolyline + " isFromTimer:" + isFromTimer);

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

                    if (isFromTimer) {
                        handlerForProfessionalTimer.postDelayed(runnableForProfessionalTimer, (1000 * UPDATE_INTERVAL));
                    }

                    CurrentLocationPojo pojo = (CurrentLocationPojo) obj;

                    String strLat = pojo.getCurrentLocationPojoItem().getCurrentLatitude();
                    String strLng = pojo.getCurrentLocationPojoItem().getCurrentLongitude();

                    if (!strLat.equals("") && !strLng.equals("")) {

                        final LatLng latLngCommon = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));

                        if (isCustomer) {

                            latLngOfCustomer = latLngCommon;

                            addCustomerPin(latLngCommon);

                            if (isRequireToDrawPolyline) {

                                String origin = latLngOfProfessional.latitude + "," + latLngOfProfessional.longitude;

                                String destination = latLngOfCustomer.latitude + "," + latLngOfCustomer.longitude;

                                try {

                                    new DirectionFinder(MapForCustomerActivity.this, MapForCustomerActivity.this, origin, destination).execute();

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            latLngOfProfessional = latLngCommon;

                            if (markerOfProfessional != null) {
                                markerOfProfessional.remove();
                            }
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
                                                /*color.setTextSize(28);
                                                color.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                                color.setColor(Color.BLACK);*/
                                                canvas1.drawBitmap(resource, 0, 0, color);
//                                                canvas1.drawText(user_code, 15, 130, color);

                                                markerOfProfessional = googleMap.addMarker(new MarkerOptions()
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

                                String origin = latLngOfProfessional.latitude + "," + latLngOfProfessional.longitude;

                                String destination = "";

                                if (locationCurrent != null) {
                                    destination = locationCurrent.getLatitude() + "," + locationCurrent.getLongitude();
                                    addCustomerPin(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()));
                                } else {
                                    if (latLngOfCustomer != null) {
                                        destination = latLngOfCustomer.latitude + "," + latLngOfCustomer.longitude;
                                        addCustomerPin(new LatLng(latLngOfCustomer.latitude, latLngOfCustomer.longitude));
                                    } else {
                                        apiCallForGetLocationOfAnyUser(jobDetailPojoItemGlobal.getJobCustomerDetail().getCustomerId(), true, true, false);
                                    }
                                }

                                if (!destination.equals("")) {
                                    try {

                                        new DirectionFinder(MapForCustomerActivity.this, MapForCustomerActivity.this, origin, destination).execute();

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int markerWidth = 160, markerHeight = 160;

    public void addCustomerPin(LatLng latLng) {

        if (markerOfCustomer != null) {
            markerOfCustomer.remove();
        }

        /*MarkerOptions options = new MarkerOptions();
        options.title("Customer");
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOfCustomer = googleMap.addMarker(options);*/

        int imgResource = R.drawable.male;

        /*if (prefsUtil.getUserGender().equals("male")) {
            imgResource = R.drawable.male;
        } else {
            imgResource = R.drawable.female;
        }*/

        Glide.with(mContext).asBitmap()
                .load(imgResource)
                .into(new SimpleTarget<Bitmap>(markerWidth, markerHeight) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                        Bitmap bmp = Bitmap.createBitmap(markerWidth, markerHeight, conf);
                        Canvas canvas1 = new Canvas(bmp);
                        Paint color = new Paint();
                        color.setTextSize(28);
                        color.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        color.setColor(Color.BLACK);
                        canvas1.drawBitmap(resource, 0, 0, color);
//                            canvas1.drawText(userCode, 15, 130, color);

                        markerOfCustomer = googleMap.addMarker(new MarkerOptions()
                                .title(prefsUtil.getUserName())
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                .position(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()))
                                .anchor(0.5f, 1));
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

        for (Polyline polyline : polylineList) {
            polyline.remove();
        }
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
            polylineList.add(googleMap.addPolyline(polylineOptions));
            ETA = routes.duration.text;
        }
        txtEta.setText(getString(R.string.eta) + " : " + ETA);
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

    public void stopTimerOfProfessionalRefreshLocation() {
        try {
            handlerForProfessionalTimer.removeCallbacks(runnableForProfessionalTimer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private void startTimerForRefreshProfessionalLocation() {
        handlerForProfessionalTimer.post(runnableForProfessionalTimer);
    }

    @Override
    protected void onResume() {
        super.onResume();

        BaseUrl.isCustomerMapOpen = true;
        BaseUrl.jobIdTmp = jobId;
        registerReceiver(receiver, new IntentFilter("refreshCustomerMap"));
    }

    @Override
    protected void onPause() {
        BaseUrl.isCustomerMapOpen = false;
        BaseUrl.jobIdTmp = "";
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        BaseUrl.isCustomerMapOpen = false;
        BaseUrl.jobIdTmp = "";

        stopTimerOfProfessionalRefreshLocation();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        stopTimer();

        super.onDestroy();
    }

}
