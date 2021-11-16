package com.ni.parnasa.activities.professional;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.CouponsScreen;
import com.ni.parnasa.activities.DispatchActivity;
import com.ni.parnasa.activities.EditProfileActivity;
import com.ni.parnasa.activities.ProfileActivity;
import com.ni.parnasa.activities.UserType;
import com.ni.parnasa.fragments.AccountSettingsFragment;
import com.ni.parnasa.fragments.FavoritesFragment;
import com.ni.parnasa.fragments.HelpFragment;
import com.ni.parnasa.fragments.InboxFragment;
import com.ni.parnasa.fragments.JobRateFragment;
import com.ni.parnasa.fragments.PaymentOptionFragment;
import com.ni.parnasa.fragments.ProfessionalHomeFragment;
import com.ni.parnasa.fragments.YourJobFragment;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.ProfessionaListPojo;
import com.ni.parnasa.pojos.ProfessionaListPojoItem;
import com.ni.parnasa.services.BackendUpdateLatLngService;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.JsonToPojoUtils;
import com.ni.parnasa.utils.KeyboardUtils;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.NotificationHelper;
import com.ni.parnasa.utils.PrefsUtil;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import io.sentry.Sentry;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ProfessionalHomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private Context mContext;
    public PrefsUtil prefsUtil;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;

    //    private Toolbar toolbar;
    private View mapView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RatingBar ratingMyReview;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader, contentView;

    public ImageView imgCurrentLocation;
    private ImageView imgClearService, imgTitleLogo, imgClearLocation, imgSearchLocation, search_btn;
    private ImageView imgNav, imgProfileBottomPopup, iv_close, iv_indOne, iv_indtwo, iv_indthree, iv_chat;
    private TextView txtTitleWithLogo, tv_username, txtEdit, tv_popup_name, tv_popup_number, tv_usercode, txtButtonTurnOn;
    private AutoCompleteTextView autoSearchByService, autoSearchByLocation;
    private CircleImageView nav_profile;

    public ProgressDialog progressDialog;
    private RatingBar rb_rating;
    private Button bt_upgrade, bt_call, btnBooking;
    public Button btn_occu, btn_on_way, btn_free;
    private RelativeLayout linlayBottomPopup, rel_search, layoutGpsDisable;
    private LinearLayout linlayRight, linlayLeft, linlayThreeBtn, linlayTitleLogo;

    private JSONArray dataa;
    private String st_username, Address, userImageUrl = "", bestProvider = "";
    private double currentLatitude = 0.0, currentLongitude = 0.0;
    private long INTERVAL = 30 * 1000;
    private long FAST_INTERVAL = 5 * 1000;
    private int count = 0;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> searchListItme = new ArrayList<>();
    private List<ProfessionaListPojoItem> professionaListPojoItems;
    //    private ArrayList<String> SearchListTitle = new ArrayList<String>();


    /*---------------------------------*/
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static int MY_PERMISSIONS_REQUEST_LOCATION = 5000;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final float END_SCALE = 0.7f;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 78;
    private int markerHeight = 160, markerWidth = 160;
    private float default_zoom_level = 13.0f;

    private String searchedService = "", searchedLocation = "";
    private boolean isGpsProviderEnable, inProgress = false;

    private LatLng latlngCenter = null;
    private Location locationCurrent = null;
    private LocationManager locationManager;
    private BroadcastReceiver receiver, receiverForNewJobPost;
    protected Marker professionalMarker = null;

    private Handler handlerForMap = new Handler();
    private Runnable runnableForMap = new Runnable() {
        @Override
        public void run() {
//            apiCallForSearch(searchedService, searchedLocation);
        }
    };

    GoogleMap.OnCameraMoveListener onCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            if (!inProgress) {
                inProgress = true;
//                handlerForMap.postDelayed(runnableForMap, 600);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        BaseUrl.isProfessionalHomeOpen = true;
//        handlerForMap.post(runnableForMap);

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 5000, 1, myUpdateListener);

        Glide.with(mContext).asBitmap()
                .load(prefsUtil.getUserPic())
                .into(nav_profile);

        if (prefsUtil.getRating().equals("") || prefsUtil.getRating().equals("0")) {
            ratingMyReview.setRating(5f);
        } else {
            ratingMyReview.setRating(Float.parseFloat(prefsUtil.getRating()));
        }
    }

    @Override
    protected void onPause() {
        BaseUrl.isProfessionalHomeOpen = false;

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
        setContentView(R.layout.activity_professional_hme);
        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
        mContext = ProfessionalHomeActivity.this;
        prefsUtil = new PrefsUtil(mContext);
        progressDialog = new ProgressDialog(mContext);

        Intent intent = new Intent(getApplicationContext(), BackendUpdateLatLngService.class);
        startService(intent);

        layoutGpsDisable = findViewById(R.id.layoutGpsDisable);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGpsProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        bestProvider = LocationManager.NETWORK_PROVIDER;



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
                        layoutGpsDisable.setVisibility(View.VISIBLE);
                    } else {
                        layoutGpsDisable.setVisibility(View.GONE);
                    }
                }
            }
        };

        receiverForNewJobPost = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showDialogForNewJobPosted(intent.getStringExtra("jobId"));
            }
        };

        registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        registerReceiver(receiverForNewJobPost, new IntentFilter("newJobPostedNotify"));
        /** completion of receiver */

        checkPermissionManual("location");

        st_username = prefsUtil.getUserName();
        userImageUrl = prefsUtil.getUserPic();

        defaultSettingsRequest();

        initViews();

        replaceFragment(new ProfessionalHomeFragment(), "");

//        List<Address> addresses;

        /*Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(prefsUtil.getLat()), Double.parseDouble(prefsUtil.getLng()), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            Address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                /*case R.id.rel_search:
                    try {
                        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                        Intent intent = intentBuilder.build(ProfessionalHomeActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    break;*/
                case R.id.btn_occu:
//                    apiCallForUpdateProfessionalStatus("occupied");
                    break;
                case R.id.btn_on_way:
//                    apiCallForUpdateProfessionalStatus("onTheWay");
                    break;
                case R.id.btn_free:
//                    apiCallForUpdateProfessionalStatus("free");
                    break;
            }
        }
    };

    private void initViews() {

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        contentView = findViewById(R.id.content);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        btn_occu = (Button) findViewById(R.id.btn_occu);
        btn_on_way = (Button) findViewById(R.id.btn_on_way);
        btn_free = (Button) findViewById(R.id.btn_free);
        imgTitleLogo = findViewById(R.id.imgTitleLogo);
        nav_profile = navHeader.findViewById(R.id.nav_profile);

        txtTitleWithLogo = findViewById(R.id.txtTitleWithLogo);
        tv_username = (TextView) navHeader.findViewById(R.id.nav_name);
        imgClearService = findViewById(R.id.imgClear);
        imgClearLocation = findViewById(R.id.imgClearLocation);
        imgSearchLocation = findViewById(R.id.imgSearchLocation);
        search_btn = findViewById(R.id.search_btn);
        linlayBottomPopup = (RelativeLayout) findViewById(R.id.service_details);
        autoSearchByLocation = findViewById(R.id.autoSearchByLocation);
        autoSearchByLocation.setOnClickListener(this);

        tv_popup_name = (TextView) findViewById(R.id.header_name);
        tv_popup_number = (TextView) findViewById(R.id.header_number);
        tv_usercode = (TextView) findViewById(R.id.u_code);
        rb_rating = (RatingBar) findViewById(R.id.myRatingBar);
        imgProfileBottomPopup = findViewById(R.id.imgBottomPopup);
        linlayRight = findViewById(R.id.linlayRight);
        linlayLeft = findViewById(R.id.linlayLeft);
        linlayThreeBtn = findViewById(R.id.linlayThreeBtn);
        linlayTitleLogo = findViewById(R.id.linlayTitleLogo);
        iv_close = (ImageView) findViewById(R.id.close);

        iv_indOne = (ImageView) findViewById(R.id.indicate_one);
        iv_indtwo = (ImageView) findViewById(R.id.indicate_two);
        iv_indthree = (ImageView) findViewById(R.id.indicate_three);

        bt_call = (Button) findViewById(R.id.call_);
        btnBooking = (Button) findViewById(R.id.btnBooking);
        iv_chat = (ImageView) findViewById(R.id.chat);
        imgNav = (ImageView) findViewById(R.id.imgNav);

        autoSearchByService = (AutoCompleteTextView) findViewById(R.id.search_home);
        txtButtonTurnOn = findViewById(R.id.txtButtonTurnOn);
        txtButtonTurnOn.setOnClickListener(this);

        adapter = new ArrayAdapter<>(ProfessionalHomeActivity.this, android.R.layout.simple_dropdown_item_1line, searchListItme);
        autoSearchByService.setThreshold(1);
        autoSearchByService.setAdapter(adapter);

        autoSearchByService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchedService = (String) parent.getItemAtPosition(position);

                if (linlayBottomPopup.getVisibility() == View.VISIBLE) {
                    linlayBottomPopup.setVisibility(View.GONE);
                }

                KeyboardUtils.hideSoftKeyboard(ProfessionalHomeActivity.this);

                if (locationCurrent != null) {
//                    apiCallForSearch(searchedService, searchedLocation);
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

//                apiCallForSearch(searchedService, searchedLocation);

//                new apiCallForgetAllProfessionalOnMap().execute();
            }
        });

        imgClearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSearchByLocation.setText("");
                imgClearLocation.setVisibility(View.GONE);
                imgSearchLocation.setVisibility(View.VISIBLE);
                searchedLocation = "";

                if (locationCurrent != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()), default_zoom_level));
                }
            }
        });

        tv_username.setText(st_username);
        txtEdit = navHeader.findViewById(R.id.txtEdit);

        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                startActivity(new Intent(mContext, EditProfileActivity.class));
            }
        });

        nav_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(mContext, ProfileActivity.class);
//                Intent intent = new Intent(mContext, ProfileNewActivity.class);
                intent.putExtra("fromNavigation", true);
                startActivity(intent);
            }
        });

        professionaListPojoItems = new ArrayList<>();

        setUpNavigationView();

        rel_search.setOnClickListener(clickListener);
//        btn_on_way.setOnClickListener(clickListener);
//        btn_occu.setOnClickListener(clickListener);
//        btn_free.setOnClickListener(clickListener);

        bt_upgrade = navHeader.findViewById(R.id.upgrade);

        bt_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(mContext, DispatchActivity.class);
                startActivity(intent1);
            }
        });

        imgCurrentLocation = (ImageView) findViewById(R.id.current_location);
        imgCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleMap != null) {
                    if (ActivityCompat.checkSelfPermission(ProfessionalHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProfessionalHomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

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
        });

       /* try {
            Glide.with(mContext).asBitmap()
                    .load(userImageUrl)
//                    .asBitmap().centerCrop().dontAnimate()
//                    .error(R.drawable.img_no_image_found)
                    .into(new BitmapImageViewTarget(nav_profile) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            nav_profile.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }*/

//        setupAdMob();
    }

    private void setUpNavigationView() {

        View navHeader = navigationView.getHeaderView(0);
        TextView txt_logout = navHeader.findViewById(R.id.txt_logout);

        txt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutFromApp();
            }
        });

        ratingMyReview = navHeader.findViewById(R.id.ratingMyReview);
        try {
            ratingMyReview.setRating(Float.parseFloat(prefsUtil.getRating()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        imgNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(navigationView)) {
                    drawer.closeDrawer(navigationView);
                } else {
                    drawer.openDrawer(navigationView);
                }
            }
        });

       /* toolbar.setTitle(R.string.filter);
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
                drawer.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.home:
                        replaceFragment(new ProfessionalHomeFragment(), "");
                        break;
                    case R.id.dispatch:
                        startActivity(new Intent(ProfessionalHomeActivity.this, DispatchActivity.class));
                        break;
                    case R.id.job_rate:
                        replaceFragment(new JobRateFragment(), getString(R.string.job_rate));
//                        startActivity(new Intent(ProfessionalHomeActivity.this, JobRateActivity.class));
                        break;
                    /*case R.id.filter:
                        startActivity(new Intent(ProfessionalHomeActivity.this, FilterScreen.class));
                        break;*/
                    case R.id.payment:
                        replaceFragment(new PaymentOptionFragment(), getString(R.string.payment));
//                        startActivity(new Intent(ProfessionalHomeActivity.this, PaymentActivity.class));
                        break;
                    case R.id.your_jobs:
                        replaceFragment(new YourJobFragment(), getString(R.string.your_jobs));
//                        startActivity(new Intent(ProfessionalHomeActivity.this, YourJobsActivity.class));
                        break;
                    /*case R.id.navManageService:
                        startActivity(new Intent(ProfessionalHomeActivity.this, ManageServicesActivity.class));
                        break;*/
                    case R.id.coupon:
                        startActivity(new Intent(ProfessionalHomeActivity.this, CouponsScreen.class));
                        break;
                    case R.id.favorites:
                        replaceFragment(new FavoritesFragment(), getString(R.string.favorites));
//                        startActivity(new Intent(ProfessionalHomeActivity.this, FavoritesScreen.class));
                        break;
                    case R.id.privateMessages:
                        replaceFragment(new InboxFragment(), getString(R.string.message));
//                        startActivity(new Intent(ProfessionalHomeActivity.this, PrivateMessagesActivity.class));
                        break;
                    case R.id.setting:
                        replaceFragment(new AccountSettingsFragment(), getString(R.string.account_setting));
//                        startActivity(new Intent(ProfessionalHomeActivity.this, AccountSettings.class));
                        break;
                    case R.id.help:
                        replaceFragment(new HelpFragment(), getString(R.string.help));
//                        startActivity(new Intent(ProfessionalHomeActivity.this, HelpScren.class));
                        break;
                    case R.id.invite:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_content) + " https://play.google.com/store/apps/details?id=" + getPackageName());
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Register on RentIN with "/*+sharedPreferences.getString("referral_code","")*/ + " and earn Rs. 5. Download on https://play.google.com/store/apps/details?id=" + getPackageName());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    /*case R.id.navLogout:
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
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                actionBarDrawerToggle.syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                actionBarDrawerToggle.syncState();
            }
        };

//        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_toc_black_24dp);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    drawer.closeDrawer(navigationView);
                } else {
                    drawer.openDrawer(navigationView);
                }
            }
        });

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
    }

    public void replaceFragment(Fragment fragment, String title) {

        getSupportFragmentManager().beginTransaction().replace(R.id.frmContainer, fragment).commit();

        if (title.equals(""))
            titleChangeToThreeButton();
        else
            titleChangeToLogo(title);

        if (title.equals(getString(R.string.your_jobs))) {
            imgTitleLogo.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                searchedLocation = place.getAddress();

                autoSearchByLocation.setText(searchedLocation);

                imgSearchLocation.setVisibility(View.GONE);
                imgClearLocation.setVisibility(View.VISIBLE);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), default_zoom_level));

//                apiCallForSearch(searchedService, searchedLocation);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
                    NotificationHelper helper = NotificationHelper.getInstance(getApplicationContext());
                    if (helper != null) {
                        helper.removeNotification();
                    }
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
                .setInterval(30 * 1000)        // 30 seconds, in milliseconds
                .setFastestInterval(10 * 1000); // 10 second, in milliseconds

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = supportMapFragment.getView();
        supportMapFragment.getMapAsync(this);*/

//        new apiCallGetServicesForAutoComplete().execute();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {

        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(true);
        googleMap.setOnCameraMoveListener(onCameraMoveListener);
        googleMap.setOnMarkerClickListener(this);

        displayLocation();

    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (googleMap != null) {
//            googleMap.setMyLocationEnabled(true);

            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            locationButton.setVisibility(View.GONE);

            /* RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

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

            addPinOfProfessional(true);
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        String markerTagId = (String) marker.getTag();

        if (professionaListPojoItems != null && markerTagId != null) {

            ProfessionaListPojoItem pojoItem = null;

            for (int i = 0; i < professionaListPojoItems.size(); i++) {

                if (markerTagId.equals(professionaListPojoItems.get(i).getUserId())) {
                    pojoItem = professionaListPojoItems.get(i);
                    break;
                }
            }

            if (pojoItem != null) {

                linlayBottomPopup.setVisibility(View.VISIBLE);

                iv_chat.setVisibility(View.GONE);
                bt_call.setVisibility(View.GONE);
                btnBooking.setVisibility(View.GONE);

                Glide.with(mContext)
                        .asBitmap()
                        .load(pojoItem.getUserProfilePicture())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                imgProfileBottomPopup.setImageDrawable(circularBitmapDrawable);
                            }
                        });

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

                /*bt_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CustomerHomeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 45);
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strMobile));
                        startActivity(intent);

//                                sendRequest();

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(prefsUtil.getLat()), Double.parseDouble(prefsUtil.getLng())), 11));

                    }
                });*/

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

                /*btnBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count = 0;
                        rb_rating.setVisibility(View.VISIBLE);

                        iv_indOne.setBackgroundResource(R.drawable.blue_bg_pro);
                        iv_indtwo.setBackgroundResource(R.drawable.gray_bg_pro);
                        iv_indthree.setBackgroundResource(R.drawable.gray_bg_pro);

                        linlayBottomPopup.setVisibility(View.GONE);

                        Intent intent = new Intent(mContext, CreateJobActivity.class);
                        intent.putExtra("professionalId", strId);
                        intent.putExtra("professionalName", strName);
                        intent.putExtra("service", st_service);
                        startActivity(intent);
                    }
                });*/

                /*iv_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        apiCallForCreateChatRoom(strId, strName, strMobile);
                    }
                });*/

                boolean isFav = false;
                try {
                    isFav = pojoItem.isMyFavorite();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*final boolean finalIsFav = isFav;
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
        return true;
    }

    private void checkPermissionManual(String which) {
        // Here, thisActivity is the current activity
        if (which.equals("location")) {

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfessionalHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
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
                checkPermissionManual("location");
            }
            return;
        }
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
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FAST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(ProfessionalHomeActivity.this, REQUEST_CHECK_SETTINGS);
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
                        .into(new SimpleTarget<Bitmap>(150, 120) {
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
                        .into(new SimpleTarget<Bitmap>(150, 120) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                googleMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                        .title(prefsUtil.getUserName())
                                        .position(new LatLng(currentLatitude, currentLongitude)));
                            }
                        });
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 10));
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


    public void apiCallForSearch(String strServiceName, String strAddress) {

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

                    inProgress = false;

                    List<ProfessionaListPojoItem> pojoItemList = ((ProfessionaListPojo) obj).getProfessionaListPojoItem();

                    googleMap.clear();

                    addPinOfProfessional(false);

                    professionaListPojoItems.clear();

                    professionaListPojoItems.addAll(pojoItemList);


                    MyLogs.w("TAG", "Number of search result found : " + professionaListPojoItems.size());

                    for (int i = 0; i < professionaListPojoItems.size(); i++) {

                        final String professionalId = professionaListPojoItems.get(i).getUserId();
                        final String userCode = professionaListPojoItems.get(i).getUserCode();
                        final String serviceName = professionaListPojoItems.get(i).getService();
                        final String serviceKeyword = professionaListPojoItems.get(i).getKeywords();
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

    public class apiCallForgetAllProfessionalOnMap extends AsyncTask<String, Void, String> {
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
                request_main.put("user_id", prefsUtil.GetUserID());

                MyLogs.e("URL", BaseUrl.URL + "user/serviceAllList");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());
                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "user/serviceAllList")
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
                Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);

                    String status = jsonObject.getString("status");
//                    String message = jsonObject.getString("message");

                    if (status.equals("Yes")) {

                        googleMap.clear();

                        /*-----------for current user marker--------------*/
                        if (locationCurrent != null) {
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
                                                        .position(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude())));
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
                                                        .position(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude())));
                                            }
                                        });
                            }
                        }
                        /*-------------------End of marker ----------------------*/

                        JsonToPojoUtils utils = new JsonToPojoUtils();
                        ProfessionaListPojo listPojo = (ProfessionaListPojo) utils.getPojo(s, ProfessionaListPojo.class);

                        professionaListPojoItems.clear();
                        professionaListPojoItems.addAll(listPojo.getProfessionaListPojoItem());
                        MyLogs.e("TAG", "Number of professional found : " + professionaListPojoItems.size());

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        String item_title[] = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject data = jsonArray.getJSONObject(i);
                            final String user_id = data.getString("user_id");
                            String first_name = data.getString("first_name");
                            String last_name = data.getString("last_name");
                            final String company_email = data.getString("company_email");
                            String location = data.getString("location");
                            String mobile_number = data.getString("mobile_number");
                            final String service = data.getString("service");
                            final String lat = data.getString("lat");
                            final String lng = data.getString("lng");
                            final String keywords = data.getString("keywords");
                            final String user_code = data.getString("user_code");
                            String rating = data.getString("rating");

                            final String service_icon = data.getString("service_icon");

                            /*map2.put(service, first_name + " " + last_name + "and" + mobile_number);
                            map3.put(service, location + "and" + company_email);
                            map4.put(service, service + "\n" + keywords);
                            map5.put(service, user_code + "and" + rating);
                            maplatlng.put(service, lat + "and" + lng);*/

                            item_title[i] = service;

                            try {

                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(service_icon)
                                        .into(new SimpleTarget<Bitmap>(markerWidth, markerHeight) {
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
                                                // modify canvas
                                                canvas1.drawBitmap(resource, 0, 0, color);
                                                canvas1.drawText(user_code, 15, 130, color);

                                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                                        .title(service)
                                                        .snippet(keywords)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                                        .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                                                        .anchor(0.5f, 1));
                                                marker.setTag(user_id);
                                            }
                                        });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void apiCallForUpdateProfessionalStatus(String statusType) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("professional_id");
        value.add(prefsUtil.GetUserID());

        if (statusType.equalsIgnoreCase("onTheWay")) {

            param.add("location");
            value.add("");

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
                    CommonPojo pojo = (CommonPojo) obj;
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPinOfProfessional(final boolean isInitially) {
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

                            if (professionalMarker != null) {
                                professionalMarker.remove();
                            }

                            professionalMarker = googleMap.addMarker(new MarkerOptions()
                                    .title(prefsUtil.getUserName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                    .position(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()))
                                    .anchor(0.5f, 1));

                            if (isInitially)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()), default_zoom_level));

                        }
                    });
        } else {
            Log.w("TAG", "Professional current location not found");
        }*/
    }

    @Override
    protected void onDestroy() {

        if (receiver != null)
            unregisterReceiver(receiver);

        if (receiverForNewJobPost != null)
            unregisterReceiver(receiverForNewJobPost);

//        handlerForMap.removeCallbacks(runnableForMap);

        locationManager.removeUpdates(myUpdateListener);

        Intent intent = new Intent(getApplicationContext(), BackendUpdateLatLngService.class);
        try {
            stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private void setupAdMob() {

        /*View adContainer = findViewById(R.id.adMobView);

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
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(new GoogleAdLoader().getAdRequest());

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
        });*/
    }

    android.location.LocationListener myUpdateListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {

            if (locationCurrent == null) {
                locationCurrent = loc;
                addPinOfProfessional(true);
            } else {
                locationCurrent = loc;
                addPinOfProfessional(false);
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

    public void titleChangeToLogo(String title) {
        linlayThreeBtn.setVisibility(View.GONE);
        imgCurrentLocation.setVisibility(View.GONE);
        linlayTitleLogo.setVisibility(View.VISIBLE);
        txtTitleWithLogo.setText(title);
    }

    public void titleChangeToThreeButton() {
        linlayTitleLogo.setVisibility(View.GONE);
        linlayThreeBtn.setVisibility(View.VISIBLE);
        imgCurrentLocation.setVisibility(View.VISIBLE);
    }

    public class UserMap extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
            /*progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();*/
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

                MyLogs.e("URL", BaseUrl.URL + "User/get_user");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "User/get_user")
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

            // progressDialog.dismiss();
            if (s == null) {

                Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    MyLogs.e("stepone", String.valueOf(jsonObject));

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("Yes")) {
                        JSONObject data = jsonObject.getJSONObject("data");


                        String user_id = data.getString("user_id");
                        String first_name = data.getString("first_name");
                        String last_name = data.getString("last_name");
                        final String company_email = data.getString("company_email");
                        //String location = data.getString("location");
                        String mobile_number = data.getString("mobile_number");
                        final String service = data.getString("service");
                        final String lat = data.getString("lat");
                        final String lng = data.getString("lng");
                        final String address = data.getString("address");
                        final String service_icon = data.getString("service_icon");

                        //+"and"+ service+"/n"+keywords
                        // String role = data.getString("role");
                        //gmap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).anchor(0.5f, 0.5f).title(service).icon(BitmapDescriptorFactory.fromResource(R.mipmap.guitar_lessons_copy)));

                        LatLng loca = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loca, 0);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loca));

                        Glide.with(ProfessionalHomeActivity.this)
                                .asBitmap()
                                .load(service_icon)
                                .into(new SimpleTarget<Bitmap>(150, 120) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        googleMap.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                                .snippet(address)
                                                .title(service)
                                                .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
                                    }
                                });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showDialogForNewJobPosted(final String jobId) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_for_new_job_post);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        TextView txtCancel = ((TextView) dialog.findViewById(R.id.txtCancel));
        TextView txtView = ((TextView) dialog.findViewById(R.id.txtView));

        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(mContext, MapForProfessionalActivity.class);
                intent.putExtra("jobId", jobId);
                startActivity(intent);
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

}
