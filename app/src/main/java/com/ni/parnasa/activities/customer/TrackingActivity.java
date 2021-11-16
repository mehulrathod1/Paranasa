package com.ni.parnasa.activities.customer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.ni.parnasa.tmpPojos.LocationModel;
import com.ni.parnasa.tmpPojos.ProfessionalSinglePojo;
import com.ni.parnasa.tmpPojos.ProfessionalSinglePojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.DirectionsJSONParserNew;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private Context mContext;

    private GoogleMap mMap;
    private ImageView imgBack;

    private ProgressDialog progressDialog;
    private LinearLayout relBottomView, linlayLeftArrow, linlayRightArrow;
    private ImageView imgCloseBottom, imgProfile, imgIndicateOne, imgIndicateTwo, imgIndicateThree, imgChat;
    private TextView txtEta, txtCode, txt1, txt2, txtButtonCall;
    private RatingBar ratingBar;

    private LocationManager locationManager;
    private Location locationCurrent;
    private WebSocketClient mWebSocketClient;
    private Marker marker;

    private double ride_start_lat, ride_start_lng, ride_end_lat, ride_end_lng;
    private String rideId = "", serviceIconUrl = "", professionalId = "", customerId = "";
    private int pageFlag = 0;
    private String durationEta = "";

    private ProfessionalSinglePojoItem professionalSinglePojoItem = null;

    /*---*/
    ArrayList<String> responseArray = new ArrayList<>();
    boolean animationFinished = true;
    int animationCounter = 0;
    int lastIndex = 0;
    ArrayList<LocationModel> locationArray = new ArrayList<>();
    //    String strDriverLat = "", strDriverLong = "", strPassengerLat = "", strPassengerLong = "", strCarLat = "", strCarLong = "", pathString = "", timeLapse = "";
    String strCarLat = "", strCarLong = "", pathString = "", timeLapse = "";
    MarkerOptions diverOptions, passengerOtions, carOptions;
    Marker driverMarker, passengerMarker, carMarker;
    LatLng driverPoint, passengerPoint, lastCarPoint;
    boolean FIRST_TIME = true;
    //long  rotationTime = 0;
    float currentRotation = 0;
    double lastLat, lastLong, oldBearing;

    String lastSendId = "", jobId = "";
    float totalDistance = 0;
    Location previousLatLong;
    long startTime = System.currentTimeMillis();
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        mContext = TrackingActivity.this;
        imgBack = findViewById(R.id.imgBack);
        relBottomView = findViewById(R.id.relBottomView);
        imgCloseBottom = findViewById(R.id.imgCloseBottom);
        txtEta = findViewById(R.id.txtEta);
        txtCode = findViewById(R.id.txtCode);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txtButtonCall = findViewById(R.id.txtButtonCall);
        imgProfile = findViewById(R.id.imgProfile);
        imgIndicateOne = findViewById(R.id.imtIndicateOne);
        imgIndicateTwo = findViewById(R.id.imgIndicateTwo);
        imgIndicateThree = findViewById(R.id.imgIndicateThree);
        imgChat = findViewById(R.id.imgChat);
        ratingBar = findViewById(R.id.ratingBar);
        linlayLeftArrow = findViewById(R.id.linlayLeftArrow);
        linlayRightArrow = findViewById(R.id.linlayRightArrow);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("ZZZ", "Receiver call in tracking");
                finish();
            }
        };

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        rideId = intent.getStringExtra("rideId");
        jobId = intent.getStringExtra("jobId");
        Log.e("ZZZ", "jobid in traking::" + jobId);

        professionalId = intent.getStringExtra("professionalId");
        customerId = intent.getStringExtra("customerId");
        ride_start_lat = Double.parseDouble(intent.getStringExtra("ride_start_lat"));
        ride_start_lng = Double.parseDouble(intent.getStringExtra("ride_start_lng"));
        ride_end_lat = Double.parseDouble(intent.getStringExtra("ride_end_lat"));
        ride_end_lng = Double.parseDouble(intent.getStringExtra("ride_end_lng"));
        serviceIconUrl = intent.getStringExtra("service_icon_url");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgCloseBottom.setOnClickListener(this);
        txtButtonCall.setOnClickListener(this);
        imgChat.setOnClickListener(this);
        linlayLeftArrow.setOnClickListener(this);
        linlayRightArrow.setOnClickListener(this);

        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationCurrent = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);*/

        setupAdMob();
    }

    @Override
    public void onClick(View v) {

        if (v == imgCloseBottom) {
            relBottomView.setVisibility(View.GONE);
        } else if (v == txtButtonCall) {
            if (professionalSinglePojoItem != null) {
                checkManualPermission();
            }
        } else if (v == imgChat) {
            Toast.makeText(mContext, "Chat", Toast.LENGTH_SHORT).show();
        } else if (v == linlayLeftArrow) {
            if (pageFlag > 0) {
                pageFlag--;
            }

            /*if (pageFlag == 2) {
            } else*/
            if (pageFlag == 1) {
                txt1.setText(professionalSinglePojoItem.getCompanyEmail());
                txt2.setText(professionalSinglePojoItem.getLocation());
                ratingBar.setVisibility(View.GONE);
                imgIndicateThree.setImageResource(R.drawable.gray_bg_pro);
                imgIndicateTwo.setImageResource(R.drawable.blue_bg_pro);
                imgIndicateOne.setImageResource(R.drawable.gray_bg_pro);
            } else if (pageFlag == 0) {
                txt1.setText(professionalSinglePojoItem.getFirstName() + " " + professionalSinglePojoItem.getLastName());
                txt2.setText(professionalSinglePojoItem.getMobileNumber());
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(professionalSinglePojoItem.getRating());
                imgIndicateThree.setImageResource(R.drawable.gray_bg_pro);
                imgIndicateTwo.setImageResource(R.drawable.gray_bg_pro);
                imgIndicateOne.setImageResource(R.drawable.blue_bg_pro);
            }
        } else if (v == linlayRightArrow) {
            if (pageFlag < 2) {
                pageFlag++;
            }

           /* if (pageFlag == 0) {
            } else*/
            if (pageFlag == 1) {
                txt1.setText(professionalSinglePojoItem.getCompanyEmail());
                txt2.setText(professionalSinglePojoItem.getLocation());
                ratingBar.setVisibility(View.GONE);
                imgIndicateOne.setImageResource(R.drawable.gray_bg_pro);
                imgIndicateTwo.setImageResource(R.drawable.blue_bg_pro);
                imgIndicateThree.setImageResource(R.drawable.gray_bg_pro);
            } else if (pageFlag == 2) {
                txt1.setText(professionalSinglePojoItem.getService());
                txt2.setText("");
                ratingBar.setVisibility(View.GONE);
                imgIndicateOne.setImageResource(R.drawable.gray_bg_pro);
                imgIndicateTwo.setImageResource(R.drawable.gray_bg_pro);
                imgIndicateThree.setImageResource(R.drawable.blue_bg_pro);
            }
        }
    }

    private void checkManualPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TrackingActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 58);
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + professionalSinglePojoItem.getMobileNumber()));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 58) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkManualPermission();
            } else {
                Toast.makeText(mContext, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String isProfessionalMarker = (String) marker.getTag();
        if (isProfessionalMarker != null && isProfessionalMarker.equalsIgnoreCase("professional")) {
            if (relBottomView.getVisibility() != View.VISIBLE) {
                if (professionalSinglePojoItem == null) {
                    apiCallForGetProfessionalDetail();
                } else {
                    relBottomView.setVisibility(View.VISIBLE);
                    setInitioalDataToBottom();
                }
            }
        }
        return true;
    }

    private void apiCallForGetProfessionalDetail() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(new PrefsUtil(mContext).getDeviceAuthToken());

        param.add("provider_id");
        value.add(professionalId);

        param.add("user_id");
        value.add(customerId);

        new ParseJSON(mContext, BaseUrl.getProfessionalDetail, param, value, ProfessionalSinglePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    professionalSinglePojoItem = ((ProfessionalSinglePojo) obj).getProfessionalSinglePojoItem();

                    relBottomView.setVisibility(View.VISIBLE);

                    setInitioalDataToBottom();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setInitioalDataToBottom() {
        /*if (!durationEta.equals("")) {
            txtEta.setText(getString(R.string.eta) + " : " + durationEta);
        }*/

        txtCode.setText(professionalSinglePojoItem.getUserCode());
        txt1.setText(professionalSinglePojoItem.getFirstName() + " " + professionalSinglePojoItem.getLastName());
        txt2.setText(professionalSinglePojoItem.getMobileNumber());
        imgIndicateOne.setImageResource(R.drawable.blue_bg_pro);
        imgIndicateTwo.setImageResource(R.drawable.gray_bg_pro);
        imgIndicateThree.setImageResource(R.drawable.gray_bg_pro);
        ratingBar.setRating(professionalSinglePojoItem.getRating());
        Glide.with(mContext)
                .load(professionalSinglePojoItem.getUserProfilePicture())
                .into(imgProfile);
        pageFlag = -1;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setOnMarkerClickListener(this);

//        String url = getDirectionsUrl();
//        new DownloadPolylineTaskerForDuration(mContext).execute(url);

        connectWebSocket();
    }

    private String getDirectionsUrl() {
        String str_origin = "origin=" + ride_start_lat + "," + ride_start_lng;
        String str_dest = "destination=" + ride_end_lat + "," + ride_end_lng;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(BaseUrl.SOCKET_URL);
            // uri = new URI("ws://162.144.134.38:5000");
            MyLogs.e("SOCKET", "URL : " + BaseUrl.SOCKET_URL);

            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    MyLogs.e("SOCKET", "Socket is open with msg : " + serverHandshake.getHttpStatusMessage());

                    String messageString = "";

                    lastSendId = new PrefsUtil(mContext).getLastSendId();
                    Log.e("TrackingActivity", "onOpen : " + lastSendId);

                    if (lastSendId.equals("")) {
                        messageString = rideId + "&&&" + "0";
                    } else {
                        messageString = rideId + "&&&" + lastSendId;
                    }

                    Log.e("SOCKET", "sent data : " + messageString);

                    mWebSocketClient.send(messageString);

                    drawPolyline();

                    //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                }

                @Override
                public void onMessage(String s) {
                    final String message = s;
                    MyLogs.e("SOCKET", "onMessage : " + message);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject(message);

                                if (obj.getBoolean("status")) {
                                    if (!obj.getBoolean("stop")) {
                                        responseArray.add(obj.toString());
                                        TrackingActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (animationFinished) {
                                                    if (animationCounter <= responseArray.size() - 1) {
                                                        decodeResult(responseArray.get(animationCounter));
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        mWebSocketClient.close();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    MyLogs.e("SOCKET", "onClose : " + b + " int : " + i);
                /*if (i != -1) {
                    if (!backPressed) {
                        connectWebSocket();
                    }
                }*/
                }

                @Override
                public void onError(Exception e) {
                    MyLogs.e("SOCKET", "onError : " + e.getMessage());
                    e.printStackTrace();
                }
            };

            mWebSocketClient.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }



       /* try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
//            SSLSocketFactory sf = new SSLSocketFactory(sslContext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//            Scheme https = new Scheme("https", 443, sf);
//            SSLContext sslContext = SSLContext.getDefault();
            mWebSocketClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));
            *//*try {
                sslContext.init(null, new TrustManager[]{tm}, null);
            } catch (KeyManagementException e) {
                System.out.printf("%s\n", e.getMessage());
            }*//*
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    private void decodeResult(String message) {
        lastIndex = 0;
        locationArray = new ArrayList<>();
        locationArray.clear();
        JSONObject j1;
        startTime = System.currentTimeMillis();

        try {
            j1 = new JSONObject(message);
//            strDriverLat = j1.getString("driverLat");
//            strDriverLong = j1.getString("driverLong");
//            strPassengerLat = j1.getString("passengerLat");
//            strPassengerLong = j1.getString("passengerLong");
            strCarLat = j1.getString("carLat");
            strCarLong = j1.getString("carLong");
            pathString = j1.getString("pathString");

            //Total time of via array
            timeLapse = j1.getString("timeLapse");
            lastSendId = j1.getString("lastSendId");
//            Log.e("TrackingActivity", "decodeResult : " + strCarLat + " " + strCarLong);
            //Log.e("DriverTrackingActivity", "j1.getString(lastSendId): " + j1.getString("lastSendId"));

            new PrefsUtil(mContext).setLastSendId(lastSendId);

            //via = new JSONArray("[{\"lat\":22.28592090182224,\"long\":\"70.79294214381845\"},{\"lat\":22.28628070122583,\"long\":\"70.79276536642698\"}]");

            JSONArray via = j1.getJSONArray("via");

            for (int i = 0; i < via.length(); i++) {
                LocationModel locationModal = new LocationModel();
                locationModal.setLat(via.getJSONObject(i).getString("lat"));
                locationModal.setLongitude(via.getJSONObject(i).getString("long"));

                if (i > 0) {
                    Location currentLoc = new Location("Service Provider");
                    currentLoc.setLatitude(Double.parseDouble(locationModal.getLat()));
                    currentLoc.setLongitude(Double.parseDouble(locationModal.getLongitude()));

                    Location oldLocation = new Location("Service Provider");
                    oldLocation.setLatitude(Double.parseDouble(locationArray.get(i - 1).getLat()));
                    oldLocation.setLongitude(Double.parseDouble(locationArray.get(i - 1).getLongitude()));

                    locationModal.setDistance(String.valueOf(currentLoc.distanceTo(oldLocation)));
                } else {
                    locationModal.setDistance("0");
                }
                locationArray.add(locationModal);
            }

            double lapse = Integer.parseInt(timeLapse) - 4000;
            timeLapse = String.valueOf(lapse);
            Log.e("TimeCalculation", "timeLapse: " + timeLapse);

            if (locationArray.size() > 2) {
                Location currentLoc = new Location("Service Provider");
                currentLoc.setLatitude(Double.parseDouble(locationArray.get(0).getLat()));
                currentLoc.setLongitude(Double.parseDouble(locationArray.get(0).getLongitude()));

                Location oldLocation = new Location("Service Provider");
                oldLocation.setLatitude(Double.parseDouble(locationArray.get(locationArray.size() - 1).getLat()));
                oldLocation.setLongitude(Double.parseDouble(locationArray.get(locationArray.size() - 1).getLongitude()));
                totalDistance = currentLoc.distanceTo(oldLocation);
            }

            Log.e("TrackingActivity", "Via : " + via.toString() + "size : " + via.length());

//            JSONObject responseObj = new JSONObject(responseArray.get(responseArray.size() - 1));
//            JSONArray responseVia = responseObj.getJSONArray("via");
            //Log.e("DriverTrackingActivity", "Resonse Array" + responseVia.toString());

            if (FIRST_TIME) {

                drawInitialPath();

            } else {
                if (animationCounter <= responseArray.size() - 1) {
                    if (via.length() > 0) {
                        lastCarPoint = new LatLng((Double.parseDouble(locationArray.get(lastIndex).getLat())), Double.parseDouble(locationArray.get(lastIndex).getLongitude()));
                        carMarker.remove();
                        /*carOptions = new MarkerOptions();
                        carOptions.position(lastCarPoint);
                        carOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi));
                        carOptions.rotation(currentRotation);
                        carOptions.anchor(0.5f, 0.5f);
                        carOptions.flat(true);
                        carMarker = mMap.addMarker(carOptions);*/
                        loadServiceIcon(String.valueOf(lastCarPoint.latitude), String.valueOf(lastCarPoint.longitude));
                        animationFinished = false;
                        initiateAnimation();
                    } else {
                        animationCounter++;
                        animationFinished = true;
                        if (animationCounter <= responseArray.size() - 1) {
                            decodeResult(responseArray.get(animationCounter));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initiateAnimation() {

        if (lastIndex < (locationArray.size() - 1)) {
            //animationTime = (Integer.parseInt(timeLapse)) / locationArray.size();
            //rotationTime = 200;

            rotateMarker();
        }
        // just in case via has only one value
        else {
            animationFinished = true;
            // Added as via length is one 23rd January, 12:07
            animationCounter++;
            //carMarker.setRotation(0);
        }
    }

    public void rotateMarker() {
        LatLng position = new LatLng(Double.parseDouble(locationArray.get(lastIndex + 1).getLat()), Double.parseDouble(locationArray.get(lastIndex + 1).getLongitude()));
        LatLng oldposition = new LatLng(Double.parseDouble(locationArray.get(lastIndex).getLat()), Double.parseDouble(locationArray.get(lastIndex).getLongitude()));
        double oldlat = oldposition.latitude, oldlong = oldposition.longitude;
        double newlat = position.latitude, newlong = position.longitude;
        Location prevLoc = new Location("service Provider");
        prevLoc.setLatitude(oldlat);
        prevLoc.setLongitude(oldlong);
        Location newLoc = new Location("service Provider");
        newLoc.setLatitude(newlat);
        newLoc.setLongitude(newlong);
        float bearing = prevLoc.bearingTo(newLoc);
        //updateCamera(bearing);
        //bearing = prevLoc.bearingTo(newLoc);
        if (bearing != 0.0) {
            currentRotation = bearing;
            Log.e("rotateMarker", "bearing : " + bearing);
            rotateMarker(carMarker, bearing, carMarker.getRotation());
            updateCamera(bearing);
        } else {
            //TODO: Calculate distance from two points


            LatLng newPoint = new LatLng(Double.parseDouble(locationArray.get(lastIndex + 1).getLat()), Double.parseDouble(locationArray.get(lastIndex + 1).getLongitude()));
            /*lastLat = Double.parseDouble(locationArray.get(lastIndex + 1).getLat());
            lastLong = Double.parseDouble(locationArray.get(lastIndex + 1).getLongitude());
*/
            // Calculating time for animation between two points
            updateCamera(bearing);
            if (totalDistance != 0) {
                float unitTime = Float.parseFloat(timeLapse) / totalDistance;
                float animationTimeNew = unitTime * Float.parseFloat(locationArray.get(lastIndex + 1).getDistance());
                animateMarker(newPoint, false, animationTimeNew);
            } else {
                carMarker.setPosition(newPoint);
                lastIndex++;
                if (lastIndex < (locationArray.size() - 1)) {
                    initiateAnimation();
                } else if (lastIndex == (locationArray.size() - 1)) {
                    animationCounter++;
                    animationFinished = true;
                    if (animationCounter <= responseArray.size() - 1) {
                        decodeResult(responseArray.get(animationCounter));
                    }
                }
            }
        }
    }

    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / 200);
                float rot = t * toRotation + (1 - t) * startRotation;
                marker.setRotation(-rot > 180 ? rot / 2 : rot);

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    LatLng newPoint = new LatLng(Double.parseDouble(locationArray.get(lastIndex + 1).getLat()), Double.parseDouble(locationArray.get(lastIndex + 1).getLongitude()));


                    if (totalDistance != 0) {
                        float unitTime = Float.parseFloat(timeLapse) / totalDistance;
                        float animationTimeNew = unitTime * Float.parseFloat(locationArray.get(lastIndex + 1).getDistance());
                        animateMarker(newPoint, false, animationTimeNew);
                    } else {
                        carMarker.setPosition(newPoint);
                        lastIndex++;
                        if (lastIndex < (locationArray.size() - 1)) {
                            initiateAnimation();
                        } else if (lastIndex == (locationArray.size() - 1)) {
                            animationCounter++;
                            animationFinished = true;

                            if (animationCounter <= responseArray.size() - 1) {
                                decodeResult(responseArray.get(animationCounter));
                            }
                        }
                    }
                }
            }
        });
    }

    public void animateMarker(final LatLng toPosition, final boolean hideMarke, final float animationTimeNew) {
        final Handler handler1 = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(carMarker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new LinearInterpolator();
        lastLat = startLatLng.latitude;
        lastLong = startLatLng.longitude;
        oldBearing = carMarker.getRotation();
        handler1.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / animationTimeNew);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                final Location currentLoc = new Location("");
                currentLoc.setLongitude(lng);
                currentLoc.setLatitude(lat);
                final Location lastLoc = new Location("");
                lastLoc.setLongitude(lastLong);
                lastLoc.setLatitude(lastLat);

                float distance = currentLoc.distanceTo(lastLoc);
                //Log.e("Current distance", distance + "");
                if (distance > 1.0) {
                    carMarker.setPosition(new LatLng(lat, lng));
                    lastLat = lat;
                    lastLong = lng;
                }

                /*Log.e("DriverTrackingActivity", "animateMarker : run : set pos t : "+ t + " " + animationFinished);
                carMarker.setPosition(new LatLng(lat, lng));*/

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler1.postDelayed(this, 16);
                    //[{"lat":22.28592090182224,"long":"70.79294214381845"},{"lat":22.28628070122583,"long":"70.79276536642698"}]size : 2
                } else {
                    if (hideMarke) {
                        carMarker.setVisible(false);
                    } else {
                        carMarker.setVisible(true);
                    }
                    //Log.e("last Index", lastIndex + "");
                    lastIndex++;

                    long elapsedTime = System.currentTimeMillis() - startTime;

                    if (lastIndex < (locationArray.size() - 1)) {
                        initiateAnimation();
                    } else if (lastIndex == (locationArray.size() - 1)) {
                        animationCounter++;
                        animationFinished = true;
                        Log.e("TimeCalculation", "Total elapsed http request/response time in milliseconds: " + elapsedTime);
                        //updateBounds();
                        //updateBounds();
                        if (animationCounter <= responseArray.size() - 1) {
                            decodeResult(responseArray.get(animationCounter));
                        }
                    }
                }
            }
        });
    }

    boolean first_time_animate = true;
    float firstBearing;

    public void updateCamera(float bearing) {

        /*LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(carOptions.getPosition());
        builder.include(passengerMarker.getPosition());
        LatLngBounds bounds = builder.build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50),1000,null);*/

        if (first_time_animate) {
            bearing = (bearing + 0) % 360;
//            bearing = (bearing + 180) % 360;
            firstBearing = bearing;

//            Log.e("DriverTrackingActivity", "updateCamera: getMeasuredWidth : "+findViewById(R.id.maps_fragment).getMeasuredWidth() + " getMeasuredHeight : "+findViewById(R.id.maps_fragment).getMeasuredHeight());

            /*CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(mMap.getCameraPosition().target)
                    .bearing(bearing)
                    .zoom(mMap.getCameraPosition().zoom).build();*/

            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(mMap.getCameraPosition().target)
                    .zoom(mMap.getCameraPosition().zoom).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 500, null);

            first_time_animate = false;
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(carMarker.getPosition());
            builder.include(passengerMarker.getPosition());
            final LatLngBounds bounds = builder.build();
            LatLng ne = bounds.northeast;
            LatLng sw = bounds.southwest;
            LatLng center = new LatLng((ne.latitude + sw.latitude) / 2,
                    (ne.longitude + sw.longitude) / 2);

           /* CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(carMarker.getPosition())
                    .bearing(firstBearing).zoom(18f).build();*/

            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(center)
                    .bearing(firstBearing)
                    .zoom(getBoundsZoomLevel(ne, sw, findViewById(R.id.map).getMeasuredWidth(), findViewById(R.id.map).getMeasuredHeight())).build();

       /* CameraPosition currentPlace = new CameraPosition.Builder()
                .target(bounds.getCenter())
                .bearing(bearing).zoom(map.getCameraPosition().zoom).build();*/
            //map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 500, null);

        }
    }

    public int getBoundsZoomLevel(LatLng northeast, LatLng southwest, int width, int height) {
        final float GLOBE_WIDTH = 256 * getResources().getDisplayMetrics().density; // a constant in Google's map projection
        final float ZOOM_MAX = 17;
        double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
        double lngDiff = northeast.longitude - southwest.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
        double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
        double zoom = Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX);
        Log.e("DriverTrackingActivity", "getBoundsZoomLevel: zoom :  " + zoom);
        return (int) (zoom);
    }

    private double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private double zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = .693147180559945309417;
        return (Math.log(mapPx / worldPx / fraction) / LN2);
    }

    private void drawInitialPath() {

       /* String origin = ride_start_lat + "," + ride_start_lng;
        String destination = ride_end_lat + "," + ride_end_lng;

        try {
            new DirectionFinder(TrackingActivity.this, this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        new ParserTask().execute(pathString);
    }

    public void drawPolyline() {
        String origin = ride_start_lat + "," + ride_start_lng;
        String destination = ride_end_lat + "," + ride_end_lng;

        try {
            new DirectionFinder(TrackingActivity.this, this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
//        progressDialog = ProgressDialog.show(mContext, getString(R.string.please_wait), getString(R.string.find_direction), true);
//        mMap.clear();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {
//        progressDialog.dismiss();
//        polylinePaths = new ArrayList<>();
//        originMarkers = new ArrayList<>();
//        destinationMarkers = new ArrayList<>();

        for (Route routes : route) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.BLACK)
                    .width(15);

            for (int i = 0; i < routes.points.size(); i++) {
                polylineOptions.add(routes.points.get(i));
            }

//            durationEta = routes.duration.text;

            mMap.addPolyline(polylineOptions);
        }
    }

    private void loadServiceIcon(final String strLat, final String strLong) {
        try {
            Glide.with(mContext)
                    .asBitmap()
                    .load(serviceIconUrl)
                    .into(new SimpleTarget<Bitmap>(160, 160) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                            Bitmap bmp = Bitmap.createBitmap(160, 160, conf);
                            Canvas canvas1 = new Canvas(bmp);
                            // paint defines the text color, stroke width and size
                            Paint color = new Paint();
                            color.setTextSize(28);
                            color.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            color.setColor(Color.BLACK);

                            canvas1.drawBitmap(resource, 0, 0, color);
//                            canvas1.drawText(user_code, 15, 130, color);

                            carOptions = new MarkerOptions();
                            carOptions.position(new LatLng(Double.parseDouble(strCarLat), Double.parseDouble(strCarLong)));
                            carOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                            carOptions.anchor(0.5f, 0.5f);
//                            carOptions.flat(true);
                            carMarker = mMap.addMarker(carOptions);
                            carMarker.setTag("professional");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParserNew parser = new DirectionsJSONParserNew();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (point != null) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));

                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.DKGRAY);
            }

            mMap.addPolyline(lineOptions);


            //Log.e("DriverTrackingActivity", "strCarLat : " + strCarLat + "strCarLong : " + strCarLong);
            /*driverPoint = new LatLng(Double.parseDouble(strDriverLat), Double.parseDouble(strDriverLong));
            passengerPoint = new LatLng(Double.parseDouble(strPassengerLat), Double.parseDouble(strPassengerLong));*/

            driverPoint = new LatLng(ride_start_lat, ride_start_lng);
            passengerPoint = new LatLng(ride_end_lat, ride_end_lng);

            FIRST_TIME = false;

            /*diverOptions = new MarkerOptions();
            diverOptions.position(driverPoint);
            diverOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_up_marker));*/

            passengerOtions = new MarkerOptions();
            passengerOtions.position(passengerPoint);
            passengerOtions.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_off_marker));
//            driverMarker = mMap.addMarker(diverOptions);
            passengerMarker = mMap.addMarker(passengerOtions);

            /*carOptions = new MarkerOptions();
            carOptions.position(new LatLng(Double.parseDouble(strCarLat), Double.parseDouble(strCarLong)));
            carOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi));
            carOptions.anchor(0.5f, 0.5f);
            carOptions.flat(true);
            carMarker = mMap.addMarker(carOptions);*/

            loadServiceIcon(strCarLat, strCarLong);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(Double.parseDouble(strCarLat), Double.parseDouble(strCarLong)));
//            builder.include(carOptions.getPosition());
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
            //map.animateCamera(CameraUpdateFactory.zoomTo(17));

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(new LatLng(Double.parseDouble(strCarLat), Double.parseDouble(strCarLong))).
                    zoom(17).
                    bearing(0).
                    build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            List<HashMap<String, String>> path1 = result.get(0);

            if (path1.size() > 2) {
                HashMap<String, String> currentPoint = path1.get(0);
                double lat = Double.parseDouble(currentPoint.get("lat"));
                double lng = Double.parseDouble(currentPoint.get("lng"));
                LatLng position = new LatLng(lat, lng);

                HashMap<String, String> nextPoint = path1.get(1);
                double nextLat = Double.parseDouble(nextPoint.get("lat"));
                double nextLng = Double.parseDouble(nextPoint.get("lng"));
                LatLng nextPos = new LatLng(nextLat, nextLng);

                double oldlat = position.latitude, oldlong = position.longitude;
                double newlat = nextPos.latitude, newlong = nextPos.longitude;
                Location prevLoc = new Location("service Provider");
                prevLoc.setLatitude(oldlat);
                prevLoc.setLongitude(oldlong);
                Location newLoc = new Location("service Provider");
                newLoc.setLatitude(newlat);
                newLoc.setLongitude(newlong);
                float bearing = prevLoc.bearingTo(newLoc);

                Log.e("DriverTrackingActivity", "onPostExecute: " + bearing);

                updateCamera(bearing);
//                carMarker.setRotation(bearing);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter("notifyTrackingScreen"));
        BaseUrl.isTrackingOpen = true;
        BaseUrl.jobIdForTracking = jobId;
    }

    @Override
    protected void onPause() {
        BaseUrl.isTrackingOpen = false;
        BaseUrl.jobIdForTracking = "";
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        BaseUrl.isTrackingOpen = false;
        BaseUrl.jobIdTmp = "";

        try {
            mWebSocketClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private void setupAdMob() {

        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_traking_ad_id),linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }
}
