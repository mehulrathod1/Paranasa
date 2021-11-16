package com.ni.parnasa.activities.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.ChatActivity;
import com.ni.parnasa.activities.CouponsScreen;
import com.ni.parnasa.activities.EditProfileActivity;
import com.ni.parnasa.activities.FilterScreen;
import com.ni.parnasa.activities.Modules.DirectionFinder;
import com.ni.parnasa.activities.Modules.DirectionFinderListener;
import com.ni.parnasa.activities.Modules.Route;
import com.ni.parnasa.activities.ProfileActivity;
import com.ni.parnasa.activities.UserType;
import com.ni.parnasa.fragments.AccountSettingsFragment;
import com.ni.parnasa.fragments.CustomerHomeFragment;
import com.ni.parnasa.fragments.FavoritesFragment;
import com.ni.parnasa.fragments.HelpFragment;
import com.ni.parnasa.fragments.InboxFragment;
import com.ni.parnasa.fragments.YourJobFragment;
import com.ni.parnasa.pojos.ChatRoomPojo;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.ProfessionaListPojo;
import com.ni.parnasa.pojos.ProfessionaListPojoItem;
import com.ni.parnasa.tmpPojos.FilterPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.KeyboardUtils;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.NotificationHelper;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CustomerHomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, DirectionFinderListener, View.OnClickListener {

    private Context mContext;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SupportMapFragment supportMapFragment;
    public PrefsUtil prefsUtil;
    //firebase auth object
//    private FirebaseAuth firebaseAuth;
//    private FirebaseFirestore db;

    private List<FilterPojoItem> filterPojoItems;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<ProfessionaListPojoItem> professionaListPojoItems;
    private ArrayList<String> searchListItme = new ArrayList<>();
    private HashMap<String, String> map2 = new HashMap<String, String>();
    private HashMap<String, String> map3 = new HashMap<String, String>();
    private HashMap<String, String> map4 = new HashMap<String, String>();
    private HashMap<String, String> map5 = new HashMap<String, String>();
    private HashMap<String, String> maplatlng = new HashMap<String, String>();

    private ArrayAdapter<String> adapter;
    private JSONArray dataa;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private RatingBar ratingMyReview;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    //    private Toolbar toolbar;
    private View contentView;
    private RelativeLayout rel_search;
    private View mapView;
    private TextView tv_header, tv_username, txtEdit, tv_popup_name, tv_popup_number, tv_usercode;
    private RelativeLayout linlayBottomPopup;
    private ImageView iv_back, iv_forward, iv_chat, imgProfileBottomPopup, nav_profile, iv_indOne, iv_indtwo, iv_indthree, iv_close;
    private ImageView search_btn, imgSearchLocation, imgClearService, imgClearLocation;
    public ImageView imgCurrentLocation;

    public ProgressDialog progressDialog;
    private Button bt_call, btnBooking;
    private RatingBar rb_rating;
    private LinearLayout linlayLeft, linlayRight, ll_search;
    private AutoCompleteTextView autoSearchByService, autoSearchByLocation;
    private TextView txtButtonTurnOn, txtTitleWithLogo;
    private RelativeLayout layoutGpsDisable;
    private ImageView imgNav, imgTitleLogo;

    private int count = 0;
    private String st_Name, st_upic;
    private String st_name, st_number, st_email, st_location, st_service, strAddress = "", strCity = "", strCountry = "";
    private String bestProvider = "";

    private boolean isPhoneCallPermissionAllow = false, ISPROGRESS = false;
    private boolean isFromFilter = false;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION_ = 99;
    private static final float END_SCALE = 0.7f;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static int MY_PERMISSIONS_REQUEST_LOCATION = 5000;
    private static final int PLACE_PICKER_REQUEST = 1;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 78;
    private int REQ_CODE_FILTER = 79;
    private int markerWidth = 160, markerHeight = 160;
//    private int markerWidth = 120, markerHeight = 150;

    /*-----------------------------------------*/
    private LocationManager locationManager;
    private BroadcastReceiver receiver;
    private LatLng latLngSearch = null, latlngCenter = null;
    public Location locationCurrent = null;
    private Marker customerMarker = null;

    private String searchedService = "", searchedLocation = "";
    private int UPDATE_INTERVAL;
    private float default_zoom_level = 13.0f;
    private boolean isGpsProviderEnable, inProgress = false;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (locationCurrent != null) {
//                apiCallForSearch(searchedService, null, searchedLocation, true); // with search text
            } else {

                handler.postDelayed(runnable, (1000 * UPDATE_INTERVAL));

                if (ActivityCompat.checkSelfPermission(CustomerHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CustomerHomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(bestProvider, 5000, 0, myUpdateListener);
            }
        }
    };

    private Handler handlerForMap = new Handler();
    private Runnable runnableForMap = new Runnable() {
        @Override
        public void run() {
//            apiCallForSearch(searchedService, null, searchedLocation, false);
        }
    };

    GoogleMap.OnCameraMoveListener onCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            if (!inProgress) {
                inProgress = true;
//                handler.postDelayed(runnable, 1000);
                handlerForMap.postDelayed(runnableForMap, 600);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 5000, 0, myUpdateListener);

        Glide.with(mContext).asBitmap()
                .load(prefsUtil.getUserPic())
                .into(nav_profile);

        if (prefsUtil.getRating().equals("") || prefsUtil.getRating().equals("0")) {
            ratingMyReview.setRating(5f);
        } else {
            ratingMyReview.setRating(Float.parseFloat(prefsUtil.getRating()));
        }

//        startTimer();
    }

    @Override
    protected void onPause() {

//        stopTimer();

        locationManager.removeUpdates(myUpdateListener);

        super.onPause();

        //Disconnect from API onPause()
        /*if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
            mGoogleApiClient.disconnect();
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = CustomerHomeActivity.this;
        prefsUtil = new PrefsUtil(mContext);
        progressDialog = new ProgressDialog(mContext);
        UPDATE_INTERVAL = prefsUtil.getUpdateInterval();

        layoutGpsDisable = findViewById(R.id.layoutGpsDisable);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bestProvider = LocationManager.NETWORK_PROVIDER;
        isGpsProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsProviderEnable) {
            layoutGpsDisable.setVisibility(View.VISIBLE);
        }

        /** The receiver for handle GPS enable/disable screen */
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {

                    isGpsProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if (isGpsProviderEnable) {
                        layoutGpsDisable.setVisibility(View.GONE);
                    } else {
                        layoutGpsDisable.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        /** completion of receiver */

        st_Name = prefsUtil.getUserName();
        st_upic = prefsUtil.getUserPic();

        defaultSettingsRequest();

        initViews();

        CustomerHomeFragment customerHomeFragment = new CustomerHomeFragment();
        replaceFragment(customerHomeFragment, getString(R.string.home), true);

        checkPermission("location");
    }


    private void initViews() {

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        contentView = findViewById(R.id.content);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        autoSearchByLocation = findViewById(R.id.autoSearchByLocation);
        imgNav = findViewById(R.id.imgNav);
        imgTitleLogo = findViewById(R.id.imgTitleLogo);
        txtTitleWithLogo = findViewById(R.id.txtTitleWithLogo);

        autoSearchByLocation.setOnClickListener(this);

        navHeader = navigationView.getHeaderView(0);

        nav_profile = (ImageView) navHeader.findViewById(R.id.nav_profile);
        ratingMyReview = navHeader.findViewById(R.id.ratingMyReview);
        TextView txt_logout = navHeader.findViewById(R.id.txt_logout);

        txtEdit = navHeader.findViewById(R.id.txtEdit);

        iv_back = (ImageView) findViewById(R.id.back_arrow);

        linlayBottomPopup = (RelativeLayout) findViewById(R.id.service_details);
        iv_close = (ImageView) findViewById(R.id.close);
        tv_popup_name = (TextView) findViewById(R.id.header_name);
        tv_popup_number = (TextView) findViewById(R.id.header_number);
        tv_usercode = (TextView) findViewById(R.id.u_code);
        rb_rating = (RatingBar) findViewById(R.id.myRatingBar);
        imgProfileBottomPopup = findViewById(R.id.imgBottomPopup);
        linlayRight = findViewById(R.id.linlayRight);
        linlayLeft = findViewById(R.id.linlayLeft);
        search_btn = findViewById(R.id.search_btn);
        imgSearchLocation = findViewById(R.id.imgSearchLocation);
        imgClearService = findViewById(R.id.imgClear);
        imgClearLocation = findViewById(R.id.imgClearLocation);

        iv_indOne = (ImageView) findViewById(R.id.indicate_one);
        iv_indtwo = (ImageView) findViewById(R.id.indicate_two);
        iv_indthree = (ImageView) findViewById(R.id.indicate_three);

        bt_call = (Button) findViewById(R.id.call_);
        btnBooking = (Button) findViewById(R.id.btnBooking);
        iv_chat = (ImageView) findViewById(R.id.chat);

        iv_forward = (ImageView) findViewById(R.id.forward_arrow);
        autoSearchByService = (AutoCompleteTextView) findViewById(R.id.search_home);
        txtButtonTurnOn = findViewById(R.id.txtButtonTurnOn);
        txtButtonTurnOn.setOnClickListener(this);

        filterPojoItems = new ArrayList<>();

        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, searchListItme);
        autoSearchByService.setThreshold(1);
        autoSearchByService.setAdapter(adapter);

        autoSearchByService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchedService = (String) parent.getItemAtPosition(position);

                if (linlayBottomPopup.getVisibility() == View.VISIBLE) {
                    linlayBottomPopup.setVisibility(View.GONE);
                }

                KeyboardUtils.hideSoftKeyboard(CustomerHomeActivity.this);

                if (locationCurrent != null) {
//                    apiCallForSearch(searchedService, new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()), searchedLocation, false);
                } else {
                    Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        });

        autoSearchByService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    search_btn.setVisibility(View.VISIBLE);
                    imgClearService.setVisibility(View.GONE);
                } else {
                    imgClearService.setVisibility(View.VISIBLE);
                    search_btn.setVisibility(View.GONE);
                }
            }
        });

        imgClearService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoSearchByService.setText("");
                search_btn.setVisibility(View.VISIBLE);
                imgClearService.setVisibility(View.GONE);
                searchedService = "";

//                apiCallForSearch(searchedService, null, searchedLocation, false);
            }
        });

        imgClearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSearchByLocation.setText("");
                imgClearLocation.setVisibility(View.GONE);
                imgSearchLocation.setVisibility(View.VISIBLE);
                searchedLocation = "";
                latLngSearch = null;

                if (locationCurrent != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()), default_zoom_level));
                }
            }
        });


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(mContext, EditProfileActivity.class));
            }
        });

        nav_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                drawer.closeDrawers();
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(mContext, ProfileActivity.class);
//                Intent intent = new Intent(mContext, ProfileNewActivity.class);
                intent.putExtra("fromNavigation", true);
                startActivity(intent);
            }
        });

        tv_username = (TextView) navHeader.findViewById(R.id.u_name);
        tv_username.setText(st_Name);

        professionaListPojoItems = new ArrayList<>();

        setUpNavigationView();

        imgCurrentLocation = findViewById(R.id.current_location);

        /*imgCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleMap != null) {
                    if (ActivityCompat.checkSelfPermission(CustomerHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CustomerHomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    *//*Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, CustomerHomeActivity.this);*//*
//                    Criteria criteria = new Criteria();
//                    bestProvider = locationManager.getBestProvider(criteria, false);

                    locationCurrent = locationManager.getLastKnownLocation(bestProvider);

                    if (locationCurrent != null) {

                        LatLng latLng = new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude());

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, default_zoom_level));

                    } else {

                        locationManager.requestLocationUpdates(bestProvider, 5000, 0, myUpdateListener);

                        Toast.makeText(mContext, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });*/

        txt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutFromApp();
            }
        });
//        setupAdMob();
    }

    private void setUpNavigationView() {

        imgNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (drawer.isDrawerOpen(navigationView)) {
                    drawer.closeDrawer(navigationView);
                } else {
                    drawer.openDrawer(navigationView);
                }*/
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        /*toolbar.setTitle(getString(R.string.filter));
        toolbar.setNavigationIcon(R.drawable.ic_toc_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (drawer.isDrawerOpen(navigationView)) {
                                                         drawer.closeDrawer(navigationView);
                                                     } else {
                                                         drawer.openDrawer(navigationView);
                                                     }
                                                 }
                                             }
        );*/


        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                     @Override
                                     public void onDrawerSlide(View drawerView, float slideOffset) {
                                         //labelView.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);
                                         // Scale the View based on current slide offset
                                         final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                         final float offsetScale = 1 - diffScaledOffset;
                                         contentView.setScaleX(offsetScale);
                                         contentView.setScaleY(offsetScale);

                                         // Translate the View, accounting for the scaled width
                                         final float xOffset = drawerView.getWidth() * slideOffset;
                                         final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                         final float xTranslation = xOffset - xOffsetDiff;
                                         contentView.setTranslationX(xTranslation);
                                     }

                                     @Override
                                     public void onDrawerClosed(View drawerView) {
                                         //labelView.setVisibility(View.GONE);
                                     }
                                 }
        );

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setCheckable(true);
                drawer.closeDrawer(GravityCompat.START);
//                drawer.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.home:
                        replaceFragment(new CustomerHomeFragment(), getString(R.string.home), true);
//                        startActivityForResult(new Intent(mContext, FilterScreen.class), REQ_CODE_FILTER);
                        break;
                    case R.id.filter:
                        startActivityForResult(new Intent(mContext, FilterScreen.class), REQ_CODE_FILTER);
                        break;
                    case R.id.your_jobs:
                        replaceFragment(new YourJobFragment(), getString(R.string.your_jobs), true);
//                        startActivity(new Intent(mContext, YourJobsActivity.class));
                        break;
                    case R.id.coupon:
                        startActivity(new Intent(mContext, CouponsScreen.class));
                        break;
                    case R.id.favorites:
                        replaceFragment(new FavoritesFragment(), getString(R.string.favorites), false);
//                        startActivity(new Intent(mContext, FavoritesScreen.class));
                        break;
                    case R.id.privateMessages:
                        replaceFragment(new InboxFragment(), getString(R.string.message), true);
//                        startActivity(new Intent(mContext, PrivateMessagesActivity.class));
                        break;
                    /*case R.id.payment:
                        startActivity(new Intent(mContext, PaymentActivity.class));
                        break;*/
                    case R.id.setting:
                        replaceFragment(new AccountSettingsFragment(), getString(R.string.account_setting), false);
//                        startActivity(new Intent(mContext, ChangePasswordActivity.class));
                        break;
                    case R.id.help:
                        replaceFragment(new HelpFragment(), getString(R.string.help), false);
//                        startActivity(new Intent(mContext, HelpScren.class));
                        break;
                    case R.id.invite:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_content) + " https://play.google.com/store/apps/details?id=" + getPackageName());
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Register on RentIN with "/*+sharedPreferences.getString("referral_code","")*/ + " and earn Rs. 5. Download on https://play.google.com/store/apps/details?id=" + getPackageName());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    /*case R.id.logout:
                        logoutFromApp();
                        break;*/
                }

                return true;
            }
        });

//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 1, 2) {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, 1, 2) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                actionBarDrawerToggle.syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we don't want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                actionBarDrawerToggle.syncState();
            }
        };
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_toc_black_24dp);

        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (drawer.isDrawerOpen(navigationView)) {
                    drawer.closeDrawer(navigationView);
                } else {
                    drawer.openDrawer(navigationView);
                }*/
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
    }

    public void replaceFragment(Fragment fragment, String title, boolean isRequiredToHideLogo) {

        getSupportFragmentManager().beginTransaction().replace(R.id.frmContainer, fragment).commit();

        titleChange(title, isRequiredToHideLogo);
    }

    public void titleChange(String title, boolean isRequiredToHideLogo) {
        if (isRequiredToHideLogo) {
            imgTitleLogo.setVisibility(View.GONE);
        } else {
            imgTitleLogo.setVisibility(View.VISIBLE);
        }
        txtTitleWithLogo.setText(title);

        if (title.equalsIgnoreCase(getString(R.string.home)))
            imgCurrentLocation.setVisibility(View.VISIBLE);
        else
            imgCurrentLocation.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                searchedLocation = place.getAddress();
                latLngSearch = place.getLatLng();

                autoSearchByLocation.setText(searchedLocation);

                imgSearchLocation.setVisibility(View.GONE);
                imgClearLocation.setVisibility(View.VISIBLE);

//                apiCallForSearch(searchedService, latLngSearch, searchedLocation, false);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngSearch, default_zoom_level));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == REQ_CODE_FILTER && resultCode == Activity.RESULT_OK) {
            String res = data.getStringExtra("data");

            JsonToPojoUtils utils = new JsonToPojoUtils();
            FilterPojo pojoItem = (FilterPojo) utils.getPojo(res, FilterPojo.class);

            MyLogs.w("TAG", "Number of filter result found " + pojoItem.getFilterPojoItem().size());

            if (pojoItem.getFilterPojoItem().size() > 0) {
                isFromFilter = true;
                filterPojoItems.clear();
                filterPojoItems.addAll(pojoItem.getFilterPojoItem());
                manageFilterData();
            }
        }*/
    }

    private void logoutFromApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.alert);
        builder.setMessage(R.string.msg_logout);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apiCallForLogoutFromApp();
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void apiCallForLogoutFromApp() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("device_auth_token");
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        new ParseJSON(mContext, BaseUrl.logoutFromApp, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                prefsUtil.LogOut();

                try {
                    NotificationHelper.getInstance(getApplicationContext()).removeNotification();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(mContext, UserType.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void enableMaps() {

        /*mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 3000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 5000); // 1 second, in milliseconds

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = supportMapFragment.getView();
        supportMapFragment.getMapAsync(this);*/

        /*List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(prefsUtil.getLat()), Double.parseDouble(prefsUtil.getLng()), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            strAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        } catch (Exception e) {
            e.printStackTrace();
        }*/

//        new apiCallGetServicesForAutoComplete().execute();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onMapReady(final GoogleMap gMap) {

        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(true);
        googleMap.setOnCameraMoveListener(onCameraMoveListener);
        googleMap.setOnMarkerClickListener(this);

        displayLocation();

        /**
         * Opening bottom professional detail */
        /*this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });*/
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

        if (googleMap != null) {
//            googleMap.setMyLocationEnabled(true);
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loca, 0);

            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            locationButton.setVisibility(View.GONE);
            /*RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 30);*/

            // gmap.getUiSettings().setZoomControlsEnabled(true);
            // gmap.moveCamera(CameraUpdateFactory.newLatLng(loca));

            /*
            gmap.setMaxZoomPreference(18);
            gmap.setMinZoomPreference(8);
            */

            /*Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);*/

            locationCurrent = locationManager.getLastKnownLocation(bestProvider);
            locationManager.requestLocationUpdates(bestProvider, 5000, 0, myUpdateListener);

            addPinOfCustomer(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        String markerTagId = (String) marker.getTag();

        if (isFromFilter) {
            manageWithFilter(markerTagId);
        } else {
            manageWithoutFilter(markerTagId);
        }

        return true;
    }

    private void manageWithoutFilter(String markerTagId) {
        if (professionaListPojoItems != null && markerTagId != null) {

            ProfessionaListPojoItem pojoItem = null;

            for (int i = 0; i < professionaListPojoItems.size(); i++) {

                if (markerTagId.equals(professionaListPojoItems.get(i).getUserId())) {
                    pojoItem = professionaListPojoItems.get(i);
                    break;
                }
            }

            if (pojoItem != null) {

//                sendRequestForDrawLine(pojoItem.getLat(), pojoItem.getLng());

                linlayBottomPopup.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .asBitmap()
                        .load(pojoItem.getUserProfilePicture())
                        .into(imgProfileBottomPopup);

                final String strId = pojoItem.getUserId();
                final String strName = pojoItem.getFirstName() + " " + pojoItem.getLastName();
                final String strMobile = pojoItem.getMobileNumber();
                final String strEmail = pojoItem.getCompanyEmail();
                final String strLocation = pojoItem.getLocation();
                final String strServices = pojoItem.getService();

                tv_popup_name.setText(strName);
                tv_popup_number.setText(strMobile);
                tv_usercode.setText(pojoItem.getUserCode());
                if (pojoItem.getRating() == 0) {
                    rb_rating.setRating(5f);
                } else {
                    rb_rating.setRating(pojoItem.getRating());
                }

                linlayRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (count <= 2) {
                            count++;
                        }

                        MyLogs.e("TAG", "COUNT ++" + count);

                        if (count == 0) {
                            tv_popup_name.setText(strName);
                            tv_popup_number.setText(strMobile);
                            rb_rating.setVisibility(View.VISIBLE);

                            iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                            iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        } else if (count == 1) {
                            tv_popup_name.setText(strEmail);
                            tv_popup_number.setText(strLocation);
                            rb_rating.setVisibility(View.GONE);

                            iv_indtwo.setBackgroundResource(R.drawable.blue_bg_pro);
                            iv_indOne.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        } else if (count == 2) {
                            tv_popup_name.setText(strServices);
                            tv_popup_number.setText("");
                            rb_rating.setVisibility(View.GONE);

                            iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indOne.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.blue_bg_pro);

                        }
                    }
                });

                linlayLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (count > 0) {
                            count--;
                        }

                        MyLogs.e("TAG", "COUNT --" + count);

                        if (count == 0) {
                            tv_popup_name.setText(strName);
                            tv_popup_number.setText(strMobile);
                            iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                            iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);
                            rb_rating.setVisibility(View.VISIBLE);

                        } else if (count == 1) {

                            tv_popup_name.setText(strEmail);
                            tv_popup_number.setText(strLocation);
                            iv_indtwo.setBackgroundResource(R.drawable.blue_bg_pro);
                            iv_indOne.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);
                            rb_rating.setVisibility(View.GONE);

                        } else if (count == 2) {
                            tv_popup_name.setText(strServices);
                            iv_indOne.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.blue_bg_pro);
                            rb_rating.setVisibility(View.GONE);

                        }
                    }
                });

                bt_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CustomerHomeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 45);
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strMobile));
                        startActivity(intent);

//                                sendRequest();

//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(prefsUtil.getLat()), Double.parseDouble(prefsUtil.getLng())), 11));

                    }
                });

                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count = 0;
                        rb_rating.setVisibility(View.VISIBLE);

                        iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                        iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                        iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        linlayBottomPopup.setVisibility(View.GONE);
                    }
                });

                btnBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count = 0;
                        rb_rating.setVisibility(View.VISIBLE);

                        iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                        iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                        iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        linlayBottomPopup.setVisibility(View.GONE);

                        /*Intent intent = new Intent(mContext, CreateJobActivity.class);
                        intent.putExtra("professionalId", strId);
                        intent.putExtra("professionalName", strName);
                        intent.putExtra("service", strServices);
                        startActivity(intent);*/
                    }
                });

                iv_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        apiCallForCreateChatRoom(strId, strName, strMobile);
                    }
                });

                boolean isFav = false;
                try {
                    isFav = pojoItem.isMyFavorite();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final boolean finalIsFav = isFav;
                tv_popup_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!strId.equalsIgnoreCase(prefsUtil.GetUserID())) {
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra("anotherUserId", strId);
                            intent.putExtra("isFavorite", finalIsFav);
                            startActivity(intent);
                        }
                    }
                });

            } else {
                Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        } else {
//                    Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void manageWithFilter(String markerTagId) {
        if (filterPojoItems != null && markerTagId != null) {

            FilterPojoItem pojoItem = null;

            for (int i = 0; i < filterPojoItems.size(); i++) {

                if (markerTagId.equals(filterPojoItems.get(i).getUserId())) {
                    pojoItem = filterPojoItems.get(i);
                    break;
                }
            }

            if (pojoItem != null) {

//                sendRequestForDrawLine(pojoItem.getLat(), pojoItem.getLng());

                linlayBottomPopup.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .asBitmap()
                        .load(pojoItem.getProfileImage())
                        .into(imgProfileBottomPopup);

                final String strId = pojoItem.getUserId();
                final String strName = pojoItem.getFirstName() + " " + pojoItem.getLastName();
                final String strMobile = pojoItem.getMobileNumber();
                final String strEmail = pojoItem.getCompanyEmail();
                final String strLocation = pojoItem.getLocation();
                final String strServices = pojoItem.getService();

                tv_popup_name.setText(strName);
                tv_popup_number.setText(strMobile);
                tv_usercode.setText(pojoItem.getUserCode());
                if (pojoItem.getRating() == 0) {
                    rb_rating.setRating(5f);
                } else {
                    rb_rating.setRating(pojoItem.getRating());
                }

                linlayRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (count <= 2) {
                            count++;
                        }

                        if (count == 0) {
                            tv_popup_name.setText(strName);
                            tv_popup_number.setText(strMobile);
                            rb_rating.setVisibility(View.VISIBLE);

                            iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                            iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        } else if (count == 1) {
                            tv_popup_name.setText(strEmail);
                            tv_popup_number.setText(strLocation);
                            rb_rating.setVisibility(View.GONE);

                            iv_indtwo.setBackgroundResource(R.drawable.blue_bg_pro);
                            iv_indOne.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        } else if (count == 2) {
                            tv_popup_name.setText(strServices);
                            tv_popup_number.setText("");
                            rb_rating.setVisibility(View.GONE);

                            iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indOne.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.blue_bg_pro);

                        }
                    }
                });

                linlayLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (count > 0) {
                            count--;
                        }

                        if (count == 0) {
                            tv_popup_name.setText(strName);
                            tv_popup_number.setText(strMobile);
                            iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                            iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);
                            rb_rating.setVisibility(View.VISIBLE);

                        } else if (count == 1) {

                            tv_popup_name.setText(strEmail);
                            tv_popup_number.setText(strLocation);
                            iv_indtwo.setBackgroundResource(R.drawable.blue_bg_pro);
                            iv_indOne.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);
                            rb_rating.setVisibility(View.GONE);

                        } else if (count == 2) {
                            tv_popup_name.setText(strServices);
                            iv_indOne.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                            iv_indthree.setBackgroundResource(R.drawable.blue_bg_pro);
                            rb_rating.setVisibility(View.GONE);

                        }
                    }
                });

                bt_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CustomerHomeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 45);
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strMobile));
                        startActivity(intent);

//                                sendRequest();
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(prefsUtil.getLat()), Double.parseDouble(prefsUtil.getLng())), 11));
                    }
                });

                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count = 0;
                        rb_rating.setVisibility(View.VISIBLE);

                        iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                        iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                        iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        linlayBottomPopup.setVisibility(View.GONE);
                    }
                });

                btnBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count = 0;
                        rb_rating.setVisibility(View.VISIBLE);

                        iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                        iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                        iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        linlayBottomPopup.setVisibility(View.GONE);

                        /*Intent intent = new Intent(mContext, CreateJobActivity.class);
                        intent.putExtra("professionalId", strId);
                        intent.putExtra("professionalName", strName);
                        intent.putExtra("service", strServices);
                        startActivity(intent);*/
                    }
                });

                iv_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        apiCallForCreateChatRoom(strId, strName, strMobile);
                    }
                });

                /*boolean isFav = false;
                try {
                    isFav = pojoItem.isMyFavorite();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final boolean finalIsFav = isFav;
                tv_popup_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!strId.equalsIgnoreCase(prefsUtil.GetUserID())) {
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra("anotherUserId", strId);
                            intent.putExtra("isFavorite", finalIsFav);
                            startActivity(intent);
                        }
                    }
                });*/

            } else {
                Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        } else {
//                    Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission(String which) {

        /** Check isCallPhone permission granted or not */
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            isPhoneCallPermissionAllow = true;
        } else {
            isPhoneCallPermissionAllow = false;
        }

        // Here, thisActivity is the current activity
        if (which.equals("location")) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CustomerHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                enableMaps();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMaps();
            } else {
                checkPermission("location");
            }

        } /*else if (requestCode == 45) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    public void defaultSettingsRequest() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(10 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            layoutGpsDisable.setVisibility(View.GONE);
                        }
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),

                            status.startResolutionForResult(CustomerHomeActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            if (prefsUtil.getUserGender().equalsIgnoreCase("male")) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(R.drawable.male)
                        .into(new SimpleTarget<Bitmap>(markerWidth, markerHeight) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                googleMap.addMarker(new MarkerOptions()
                                        .title(prefsUtil.getUserName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                        .position(new LatLng(currentLatitude, currentLongitude)));
                            }
                        });
            } else {
                Glide.with(mContext)
                        .asBitmap()
                        .load(R.drawable.female)
                        .into(new SimpleTarget<Bitmap>(markerWidth, markerHeight) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                googleMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                        .title(prefsUtil.getUserName())
                                        .position(new LatLng(currentLatitude, currentLongitude)));
                            }
                        });
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    public void startTimer() {
        MyLogs.e("ZZZ", "from start timer calling");
        inProgress = false;
        handler.post(runnable);
    }

    public void stopTimer() {
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequestForDrawLine(String strLat, String strLng) {

        String origin;

        if (locationCurrent != null) {
            origin = locationCurrent.getLatitude() + "," + locationCurrent.getLongitude();
        } else {
            origin = prefsUtil.getLat() + "," + prefsUtil.getLng();
        }

        if (strLat.equals("") && strLng.equals("")) {
            Toast.makeText(CustomerHomeActivity.this, R.string.msg_destination_address, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(CustomerHomeActivity.this, this, origin, strLat + "," + strLng).execute();
//            new DirectionFinder(CustomerHomeActivity.this, this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(CustomerHomeActivity.this, getString(R.string.please_wait),
                getString(R.string.find_direction), true);
        progressDialog.setCancelable(true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route routes : route) {
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
           /* duration_txt.setVisibility(View.VISIBLE);
            duration_txt.setText(route.duration.text + " ( " + route.distance.text + " ) ");
            duration_txt.startAnimation(AnimationUtils.loadAnimation(Summit_mode.this, R.anim.slide_frm_buttom));
*/
           /* originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map))
                    .title(route.startAddress)
                    .position(route.startLocation)));*/
           /* BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, heightt, false);
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(route.endAddress)
                    .position(route.endLocation)));
*/
            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(ContextCompat.getColor(mContext, R.color.c_btn_blue))
                    .width(10)
                    .zIndex(101);


            for (int i = 0; i < routes.points.size(); i++)
                polylineOptions.add(routes.points.get(i));

            polylinePaths.add(googleMap.addPolyline(polylineOptions));

            // durationEta = routes.duration.text;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View v) {
        if (v == autoSearchByLocation) {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } else if (v == txtButtonTurnOn) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 777);
        }
    }

    public void apiCallForSearch(String strServiceName, LatLng latLng, String strAddress, final boolean isFromAutoTimer) {

        latlngCenter = googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("search");
        value.add(strServiceName);

        param.add("lat");
        value.add(String.valueOf(latlngCenter.latitude));

        param.add("lng");
        value.add(String.valueOf(latlngCenter.longitude));

        param.add("location");
        value.add(strAddress);

        param.add("radius");
        value.add(String.valueOf(getMapVisibleRadius()));

        new ParseJSON(mContext, BaseUrl.serachByServiceOrLocation, param, value, ProfessionaListPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    if (!isFromAutoTimer)
                        inProgress = false;


                    if (isFromAutoTimer) {
                        MyLogs.e("ZZZ", "post delay after getting response");
                        handler.postDelayed(runnable, (1000 * UPDATE_INTERVAL));
                    }

                    List<ProfessionaListPojoItem> pojoItemList = ((ProfessionaListPojo) obj).getProfessionaListPojoItem();

                    googleMap.clear();

                    addPinOfCustomer(false);

                    professionaListPojoItems.clear();

                    professionaListPojoItems.addAll(pojoItemList);

                    MyLogs.e("TAG", "Number of search result found : " + professionaListPojoItems.size());

                    for (int i = 0; i < professionaListPojoItems.size(); i++) {

                        final String professionalId = professionaListPojoItems.get(i).getUserId();
                        final String userCode = professionaListPojoItems.get(i).getUserCode();
                        final String serviceName = professionaListPojoItems.get(i).getService();
                        final String serviceKeyword = professionaListPojoItems.get(i).getKeywords();
                        final String lat = professionaListPojoItems.get(i).getLat();
                        final String lng = professionaListPojoItems.get(i).getLng();
                        final String professionalLat = professionaListPojoItems.get(i).getProfessionalLatitude();
                        final String professionalLng = professionaListPojoItems.get(i).getProfessionalLongitude();
                        String serviceIcon = professionaListPojoItems.get(i).getServiceIcon();

                        try {
                            if (!professionalId.equals(prefsUtil.GetUserID())) {
                                if (!professionalLat.equals("") && !professionalLng.equals("")) {

                                    Glide.with(mContext).asBitmap()
                                            .load(serviceIcon)
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
                                                    canvas1.drawText(userCode, 15, 130, color);

                                                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                                            .title(serviceName)
                                                            .snippet(serviceKeyword)
                                                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                                            .position(new LatLng(Double.parseDouble(professionalLat), Double.parseDouble(professionalLng))));
//                                                            .anchor(0.5f, 1));
                                                    marker.setTag(professionalId);
                                                }
                                            });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    inProgress = false;
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, false);
    }

    private int getMapVisibleRadius() {
        VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();

        float[] distanceWidth = new float[1];
        float[] distanceHeight = new float[1];

        LatLng farRight = visibleRegion.farRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        Location.distanceBetween(
                (farLeft.latitude + nearLeft.latitude) / 2,
                farLeft.longitude,
                (farRight.latitude + nearRight.latitude) / 2,
                farRight.longitude,
                distanceWidth
        );

        Location.distanceBetween(
                farRight.latitude,
                (farRight.longitude + farLeft.longitude) / 2,
                nearRight.latitude,
                (nearRight.longitude + nearLeft.longitude) / 2,
                distanceHeight
        );

        return (int) Math.sqrt(Math.pow(distanceWidth[0], 2) + Math.pow(distanceHeight[0], 2)) / 2;
    }

    public class apiCallGetServicesForAutoComplete extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
            progressDialog.setMessage(getString(R.string.please_wait));
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

                MyLogs.e("URL", BaseUrl.URL + "services/servicesKeyword");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "services/servicesKeyword")
                        .post(body)
                        .build();

                okhttp3.Response response = client.newCall(request).execute();
                Response1 = response.body().string();
                return Response1;

            } catch (IOException e) {

            } catch (Exception e) {
                e.printStackTrace();
            }

            return Response1;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            searchListItme.clear();

            if (s == null) {
                Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    MyLogs.e("signupreturn", String.valueOf(jsonObject));

                    String status = jsonObject.getString("status");
//                    String message = jsonObject.getString("message");

                    if (status.equals("Yes")) {
                        dataa = jsonObject.getJSONArray("data");
                        String item_title[] = new String[dataa.length()];

                        for (int i = 0; i < dataa.length(); i++) {
                            //String service = data.getString("service");
                            //  String role = data.getString("role");
                            item_title[i] = dataa.getString(i).trim();
                            searchListItme.add(dataa.getString(i));
                        }

                        if (searchListItme.size() > 0) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void apiCallForCreateChatRoom(final String strId, final String strName, final String strMobile) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("sender_id");
        value.add(prefsUtil.GetUserID());

        param.add("reciever_id");
        value.add(strId);

        new ParseJSON(mContext, BaseUrl.createChatRoom, param, value, ChatRoomPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ChatRoomPojo pojo = (ChatRoomPojo) obj;

//                    ChatRoomPojoReceiver pojoReceiver = pojo.getChatRoomPojoItem().getChatRoomPojoReceiver();

                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("isFromPopup", true);
                    intent.putExtra("receiverId", strId);
                    intent.putExtra("chatRoomId", "" + pojo.getChatRoomPojoItem().getChatRoomId());
                    intent.putExtra("userName", strName);
                    intent.putExtra("phone", strMobile);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void manageFilterData() {

        googleMap.clear();

        addPinOfCustomer(false);

        for (int i = 0; i < filterPojoItems.size(); i++) {
            try {

                final String userId = filterPojoItems.get(i).getUserId();
                final String service = filterPojoItems.get(i).getService();
                final String userCode = filterPojoItems.get(i).getUserCode();
                final String lat = filterPojoItems.get(i).getLat();
                final String lng = filterPojoItems.get(i).getLng();

                Glide.with(mContext)
                        .asBitmap()
                        .load(filterPojoItems.get(i).getServiceIcon())
                        .into(new SimpleTarget<Bitmap>(160, 160) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                                Bitmap bmp = Bitmap.createBitmap(160, 160, conf);
                                Canvas canvas1 = new Canvas(bmp);

                                Paint color = new Paint();
                                color.setTextSize(28);
                                color.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                color.setColor(Color.BLACK);

                                canvas1.drawBitmap(resource, 0, 0, color);
                                canvas1.drawText(userCode, 15, 130, color);

                                try {
                                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                            .title(service)
                                            .snippet("")
                                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                            .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
                                    marker.setTag(userId);
                                } catch (NumberFormatException ee) {
                                    ee.printStackTrace();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addPinOfCustomer(final boolean isInitially) {

        /*if (locationCurrent != null) {
            int imgResource;
            if (prefsUtil.getUserGender().equals("male")) {
                imgResource = R.drawable.male;
            } else {
                imgResource = R.drawable.female;
            }

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

                            if (customerMarker != null) {
                                customerMarker.remove();
                            }

                            customerMarker = googleMap.addMarker(new MarkerOptions()
                                    .title(prefsUtil.getUserName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                    .position(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()))
                                    .anchor(0.5f, 1));

                            if (isInitially)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()), default_zoom_level));

                        }
                    });
        } else {
            Log.w("TAG", "Customer current location not found");
        }*/
    }

    @Override
    protected void onDestroy() {
        if (receiver != null)
            unregisterReceiver(receiver);

        handler.removeCallbacks(runnable);

        handlerForMap.removeCallbacks(runnableForMap);

        locationManager.removeUpdates(myUpdateListener);

        super.onDestroy();
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

    LocationListener myUpdateListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {

            if (locationCurrent == null) {
                locationCurrent = loc;
                addPinOfCustomer(true);
            } else {
                locationCurrent = loc;
                addPinOfCustomer(false);
            }

            prefsUtil.setLat(String.valueOf(locationCurrent.getLatitude()));
            prefsUtil.setLng(String.valueOf(locationCurrent.getLongitude()));
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

}
