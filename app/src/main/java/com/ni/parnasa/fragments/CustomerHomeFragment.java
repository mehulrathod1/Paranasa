package com.ni.parnasa.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ni.parnasa.activities.ChatActivity;
import com.ni.parnasa.activities.Modules.DirectionFinder;
import com.ni.parnasa.activities.Modules.DirectionFinderListener;
import com.ni.parnasa.activities.Modules.Route;
import com.ni.parnasa.activities.customer.CreateJobActivity;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.models.country.CountryResponse;
import com.ni.parnasa.models.currencies.CurrencySymbolResponse;
import com.ni.parnasa.pojos.ChatRoomPojo;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.CustoemreHomeJobPojo;
import com.ni.parnasa.pojos.ProfessionaListPojo;
import com.ni.parnasa.pojos.ProfessionaListPojoItem;
import com.ni.parnasa.tmpPojos.FilterPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.CommonUtils;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.KeyboardUtils;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class CustomerHomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, DirectionFinderListener {

    private PrefsUtil prefsUtil;
    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;
    private Location locationCurrent = null;
    private LatLng latLngSearch = null;
    private LocationManager locationManager;
    private Marker customerMarker = null;

    private View mapView, contentView;
    private AutoCompleteTextView autoSearchByService, autoSearchByLocation;
    private RelativeLayout linlayBottomPopup;
    private LinearLayout linlayRight, linlayLeft;
    private ImageView iv_close, imgProfileBottomPopup, search_btn, imgSearchLocation, imgClearService, imgClearLocation, iv_indOne,
            iv_indtwo, iv_indthree, iv_chat, iv_forward, imgFavUnfavProvider, imgCurrentLocation;
    private TextView tv_popup_name, tv_popup_number, tv_usercode, txtEta, txtTimer;
    private RatingBar rb_rating;
    private Button bt_call, btnBooking;
    private ProgressDialog progressDialog;

    private List<FilterPojoItem> filterPojoItems;
    private List<ProfessionaListPojoItem> professionaListPojoItems;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> searchListItme = new ArrayList<>();

    private String searchedService = "", searchedLocation = "", bestProvider = "";

    private float default_zoom_level = 13.0f;
    private int UPDATE_INTERVAL;
    private boolean inProgress = false;
    private int markerWidth = 160, markerHeight = 160, count = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (locationCurrent != null) {
                apiCallForSearch(searchedService, null, searchedLocation, true); // with search text
            } else {
                handler.postDelayed(runnable, (1000 * UPDATE_INTERVAL));
                requestForLocationUpdate();
            }
        }
    };

    private Handler handlerForMap = new Handler();
    private Runnable runnableForMap = new Runnable() {
        @Override
        public void run() {
            apiCallForSearch(searchedService, null, searchedLocation, false);
        }
    };
    private static Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_customer_home, container, false);

        mActivity = getActivity();
        initViews(view);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        mapView = supportMapFragment.getView();
        supportMapFragment.getMapAsync(this);

        new apiCallGetServicesForAutoComplete().execute();

        apiCallforGetOngoingJobDetail();

        return view;
    }

    private void initViews(View view) {
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        prefsUtil = ((CustomerHomeActivity) getActivity()).prefsUtil;
        progressDialog = ((CustomerHomeActivity) getActivity()).progressDialog;

        UPDATE_INTERVAL = prefsUtil.getUpdateInterval();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        bestProvider = LocationManager.NETWORK_PROVIDER;

        contentView = view.findViewById(R.id.content);
        autoSearchByLocation = view.findViewById(R.id.autoSearchByLocation);
        linlayBottomPopup = view.findViewById(R.id.service_details);

        iv_close = view.findViewById(R.id.close);
        tv_popup_name = view.findViewById(R.id.header_name);
        tv_popup_number = view.findViewById(R.id.header_number);
        tv_usercode = view.findViewById(R.id.u_code);
        rb_rating = view.findViewById(R.id.myRatingBar);
        imgProfileBottomPopup = view.findViewById(R.id.imgBottomPopup);
        linlayRight = view.findViewById(R.id.linlayRight);
        linlayLeft = view.findViewById(R.id.linlayLeft);
        search_btn = view.findViewById(R.id.search_btn);
        imgSearchLocation = view.findViewById(R.id.imgSearchLocation);
        imgClearService = view.findViewById(R.id.imgClear);
        imgClearLocation = view.findViewById(R.id.imgClearLocation);

        iv_indOne = view.findViewById(R.id.indicate_one);
        iv_indtwo = view.findViewById(R.id.indicate_two);
        iv_indthree = view.findViewById(R.id.indicate_three);
        bt_call = view.findViewById(R.id.call_);
        btnBooking = view.findViewById(R.id.btnBooking);
        iv_chat = view.findViewById(R.id.chat);
        iv_forward = view.findViewById(R.id.forward_arrow);
        imgFavUnfavProvider = view.findViewById(R.id.imgFavUnfavProvider);
        autoSearchByService = view.findViewById(R.id.search_home);
        txtEta = view.findViewById(R.id.txtEta);
        txtTimer = view.findViewById(R.id.txtTimer);

        filterPojoItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, searchListItme);
        autoSearchByService.setThreshold(1);
        autoSearchByService.setAdapter(adapter);
        autoSearchByService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchedService = (String) parent.getItemAtPosition(position);

                if (linlayBottomPopup.getVisibility() == View.VISIBLE) {
                    linlayBottomPopup.setVisibility(View.GONE);
                }

                KeyboardUtils.hideSoftKeyboard(getActivity());

                if (locationCurrent != null) {
                    apiCallForSearch(searchedService, new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()), searchedLocation, false);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.location_not_found, Toast.LENGTH_SHORT).show();
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

        autoSearchByLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getActivity());
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        imgClearService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoSearchByService.setText("");
                search_btn.setVisibility(View.VISIBLE);
                imgClearService.setVisibility(View.GONE);
                searchedService = "";

                apiCallForSearch(searchedService, null, searchedLocation, false);
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

                if (locationCurrent != null && googleMap != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()), default_zoom_level));
                }
            }
        });

        professionaListPojoItems = new ArrayList<>();

        imgCurrentLocation = ((CustomerHomeActivity) getActivity()).imgCurrentLocation;

        imgCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleMap != null) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationCurrent = locationManager.getLastKnownLocation(bestProvider);

                    if (locationCurrent != null) {

                        LatLng latLng = new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude());

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, default_zoom_level));

//                        getAddressFromLatLong();

                    } else {
                        requestForLocationUpdate();
                        Toast.makeText(getActivity(), R.string.location_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        setupAdMob(view);
    }

    GoogleMap.OnCameraMoveListener onCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            if (!inProgress) {
                inProgress = true;
                handlerForMap.postDelayed(runnableForMap, 600);
            }
        }
    };

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

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        locationButton.setVisibility(View.GONE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationCurrent = locationManager.getLastKnownLocation(bestProvider);

        requestForLocationUpdate();

//        getAddressFromLatLong();

        addPinOfCustomer(true);

        startTimer();
    }

    public void getAddressFromLatLong() {
        if (locationCurrent != null) {
            new GeoCoderHelper(getActivity(), locationCurrent.getLatitude(), locationCurrent.getLongitude(), new GeoCoderHelper.onGetAddress() {
                public void onSuccess(String address, String city, String country) {
                    Log.e("address", "onSuccess: "+address);
                    searchedLocation = address;
                    autoSearchByLocation.setText(searchedLocation);

                    String countryCurrencyAbbreviation = "USD";
                    String countryCurrencySymbol = "$";
                    String jsonCountry = CommonUtils.loadJSONFromAsset(getActivity(), "countries.json");
                    String jsonCurrency = CommonUtils.loadJSONFromAsset(getActivity(), "countryCurrency.json");

                    Gson gsonCountry = new Gson();
                    CountryResponse countryResponse = gsonCountry.fromJson(jsonCountry, CountryResponse.class);

                    Gson gsonCountryCurrency = new Gson();
                    CurrencySymbolResponse currencySymbolResponse = gsonCountryCurrency.fromJson(jsonCurrency, CurrencySymbolResponse.class);

                    List<CountryResponse.CountryItem> countryItems = countryResponse.getCountries().getCountry();
                    Log.e("countryResponse", "onSuccess: "+countryResponse.getCountries().getCountry() );

                    for (CountryResponse.CountryItem countryItem : countryItems) {
                        if (countryItem.getCountryName().equalsIgnoreCase(country)) {
                            countryCurrencyAbbreviation = countryItem.getCurrencyCode();
                            PrefUtils.with(getActivity()).write(PrefConstant.COUNTRY_CURRENCY_CODE, countryItem.getCurrencyCode());
                            break;
                        }
                    }

                    String currencySymbol = "";
                    for (CurrencySymbolResponse.CurrenciesItem currency : currencySymbolResponse.getCurrencies()) {

                        Log.e("TAG", "onSuccess: "+currency.getAbbreviation() );
                        if (Build.VERSION.SDK_INT >= 24) {
                            currencySymbol = String.valueOf(Html.fromHtml(currency.getSymbol(), Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            currencySymbol = String.valueOf(Html.fromHtml(currency.getSymbol()));
                        }

                        Log.e("TAG", "onSuccess: "+currencySymbol );
                        if (countryCurrencyAbbreviation.equalsIgnoreCase(currency.getAbbreviation())) {
                            countryCurrencySymbol = currencySymbol;
                            PrefUtils.with(getActivity()).write(PrefConstant.COUNTRY_CURRENCY_SYMBOL, countryCurrencySymbol);
                            break;
                        }
                    }
                    PrefUtils.with(getActivity()).write(PrefConstant.CURRENT_PRICE,"1");
                    Log.e("TAG", "onSuccess: country::  "+country+" countryCurrencySymbol:: "+countryCurrencySymbol+ " countryCurrencyAbbreviation:: "+countryCurrencyAbbreviation );

                    //                    strCity = city;
//                    strCountry = country;
//                    edtLocation.setText(strLocation);
                }

                @Override
                public void onFail() {
                    Toast.makeText(getActivity(), "can't fetch location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        String markerTagId = (String) marker.getTag();

        manageWithoutFilter(markerTagId);

        /*if (isFromFilter) {
            manageWithFilter(markerTagId);
        } else {
            manageWithoutFilter(markerTagId);
        }*/

        return true;
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private void addPinOfCustomer(final boolean isInitially) {

        if (locationCurrent != null) {
            int imgResource;
            if (prefsUtil.getUserGender().equals("male")) {
                imgResource = R.drawable.male;
            } else {
                imgResource = R.drawable.female;
            }

            Glide.with(Objects.requireNonNull(getActivity())).asBitmap()
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
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude()),
                                        default_zoom_level));

                        }
                    });
        } else {
            Log.w("TAG", "Customer current location not found");
        }
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

                /**
                 *  Draw polyline from current location to selected professional */
                sendRequestForDrawLine(pojoItem.getProfessionalLatitude(), pojoItem.getProfessionalLongitude());

                linlayBottomPopup.setVisibility(View.VISIBLE);

                Glide.with(getActivity())
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

                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQ_CODE_CALL);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strMobile));
                            startActivity(intent);
                        }
//                      sendRequest();
//                      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(prefsUtil.getLat()), Double.parseDouble(prefsUtil.getLng())), 11));
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

                        gotoCreateJob(strId, strName, strServices);

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
                    Log.e("ZZZ", "isFav " + isFav);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final boolean finalIsFav = isFav;

                if (finalIsFav) {
                    imgFavUnfavProvider.setImageResource(R.drawable.ic_heart_fill);
                } else {
                    imgFavUnfavProvider.setImageResource(R.drawable.ic_heart_outline);
                }

                imgFavUnfavProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        apiCallForFavUnfav(finalIsFav, strId);
                    }
                });

                /*tv_popup_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!strId.equalsIgnoreCase(prefsUtil.GetUserID())) {
                            Intent intent = new Intent(getActivity(), ProfileActivity.class);
                            intent.putExtra("anotherUserId", strId);
                            intent.putExtra("isFavorite", finalIsFav);
                            startActivity(intent);
                        }
                    }
                });*/

            } else {
                Toast.makeText(getActivity(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        } else {
//                    Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoCreateJob(String strId, String strName, String strServices) {
        Intent intent = new Intent(getActivity(), CreateJobActivity.class);
        intent.putExtra("professionalId", strId);
        intent.putExtra("professionalName", strName);
        intent.putExtra("service", strServices);
        startActivity(intent);
    }

    private void apiCallForFavUnfav(final boolean isFavorited, String strId) {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        if (isFavorited) {
            param.add("unfavorite_user_id");
            value.add(strId);
        } else {
            param.add("favorite_user_id");
            value.add(strId);
        }

        new ParseJSON(getActivity(), (isFavorited ? BaseUrl.unfavoriteUser : BaseUrl.favoriteUser), param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;

                    if (isFavorited) {
                        imgFavUnfavProvider.setImageResource(R.drawable.ic_heart_outline);
                    } else {
                        imgFavUnfavProvider.setImageResource(R.drawable.ic_heart_fill);
                    }
                    Toast.makeText(getActivity(), pojo.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        new ParseJSON(getActivity(), BaseUrl.createChatRoom, param, value, ChatRoomPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ChatRoomPojo pojo = (ChatRoomPojo) obj;

//                    ChatRoomPojoReceiver pojoReceiver = pojo.getChatRoomPojoItem().getChatRoomPojoReceiver();

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("isFromPopup", true);
                    intent.putExtra("receiverId", strId);
                    intent.putExtra("chatRoomId", "" + pojo.getChatRoomPojoItem().getChatRoomId());
                    intent.putExtra("userName", strName);
                    intent.putExtra("phone", strMobile);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 111;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                searchedLocation = place.getAddress();
                latLngSearch = place.getLatLng();

                autoSearchByLocation.setText(searchedLocation);

                imgSearchLocation.setVisibility(View.GONE);
                imgClearLocation.setVisibility(View.VISIBLE);

                apiCallForSearch(searchedService, latLngSearch, searchedLocation, false);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngSearch, default_zoom_level));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            }
        }
    }

    private void setupAdMob(View view) {
        View adContainer = view.findViewById(R.id.adMobView);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        param.addRule(RelativeLayout.CENTER_HORIZONTAL);

        RelativeLayout rel = (RelativeLayout) adContainer;

        AdView mAdView = new AdView(getActivity());

        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.admob_home_ad_id));
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

    private LocationListener myLocationUpdateListener = new LocationListener() {
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

    public void requestForLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 5000, 0, myLocationUpdateListener);
    }

    @Override
    public void onDestroy() {
//        handler.removeCallbacks(runnable);
        handlerForMap.removeCallbacks(runnableForMap);

        locationManager.removeUpdates(myLocationUpdateListener);

        stopTimer();

        try {
            handlerForOnGoingJob.removeCallbacks(runnableForOnGoingJob);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public void startTimer() {
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

    public void apiCallForSearch(String strServiceName, LatLng latLng, String strAddress, final boolean isFromAutoTimer) {

        LatLng latlngCenter = googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

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

        new ParseJSON(mActivity, BaseUrl.serachByServiceOrLocation, param, value, ProfessionaListPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    if (!isFromAutoTimer)
                        inProgress = false;


                    if (isFromAutoTimer) {
                        MyLogs.e("ZZZ", "After getting response post delay of " + UPDATE_INTERVAL + " sec");
                        handler.postDelayed(runnable, (1000 * UPDATE_INTERVAL));
                    }

                    List<ProfessionaListPojoItem> pojoItemList = ((ProfessionaListPojo) obj).getProfessionaListPojoItem();

                    googleMap.clear();

                    /** for polyline */

                    if (polylinePaths.size() > 0) {

                        for (int i = 0; i < polylinePaths.size(); i++) {

                            List<LatLng> points = polylinePaths.get(i).getPoints();
                            polylinePaths.get(i).setPoints(points);

                        }
                        //addPoly(null, polylinePaths.get(0).getPoints(), false);
                    }

                    addPinOfCustomer(false);

                    professionaListPojoItems.clear();

                    professionaListPojoItems.addAll(pojoItemList);

                    MyLogs.e("TAG", "Number of search result found : " + professionaListPojoItems.size());

                    for (int i = 0; i < professionaListPojoItems.size(); i++) {

                        final String professionalId = professionaListPojoItems.get(i).getUserId();
                        final String userCode = professionaListPojoItems.get(i).getUserCode();
                        final String serviceName = professionaListPojoItems.get(i).getService();
                        final String serviceKeyword = professionaListPojoItems.get(i).getKeywords();
//                        final String lat = professionaListPojoItems.get(i).getLat();
//                        final String lng = professionaListPojoItems.get(i).getLng();
                        final String professionalLat = professionaListPojoItems.get(i).getProfessionalLatitude();
                        final String professionalLng = professionaListPojoItems.get(i).getProfessionalLongitude();
                        String serviceIcon = professionaListPojoItems.get(i).getServiceIcon();

                        try {
                            if (!professionalId.equals(prefsUtil.GetUserID())) {
                                if (!professionalLat.equals("") && !professionalLng.equals("")) {

                                    Glide.with(getActivity()).asBitmap()
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
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
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

    private void sendRequestForDrawLine(String strLat, String strLng) {

        String origin;

        if (locationCurrent != null) {
            origin = locationCurrent.getLatitude() + "," + locationCurrent.getLongitude();
        } else {
            origin = prefsUtil.getLat() + "," + prefsUtil.getLng();
        }

        if (strLat.equals("") && strLng.equals("")) {
            Toast.makeText(getActivity(), R.string.msg_destination_address, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(getActivity(), this, origin, strLat + "," + strLng).execute();
//            new DirectionFinder(CustomerHomeActivity.this, this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //    private List<Marker> originMarkers = new ArrayList<>();
//    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    @Override
    public void onDirectionFinderStart() {
        //progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.find_direction), true);
        //progressDialog.setCancelable(true);

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {
//        progressDialog.dismiss();

//        polylinePaths.clear();

        addPoly(route, null, true);
    }

    public void addPoly(List<Route> route, List<LatLng> latLngList, boolean isNewPolyline) {

        /*if (polylinePaths.size() > 0) {
            polylinePaths.get(0).remove();
        }*/

        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(getActivity(), R.color.c_btn_blue))
                .width(10)
                .zIndex(101);

        if (isNewPolyline) {
            String durationEta = "";
            for (Route routes : route) {
                for (int i = 0; i < routes.points.size(); i++) {
                    polylineOptions.add(routes.points.get(i));
                }

                polylinePaths.add(googleMap.addPolyline(polylineOptions));
                durationEta = routes.duration.text;
            }

            txtEta.setText(getString(R.string.eta) + ": " + durationEta);
        } else {


            /*for (int i = 0; i < latLngList.size(); i++)
                polylineOptions.add(latLngList.get(i));

            googleMap.addPolyline(polylineOptions);*/

        }
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
                        JSONArray dataa = jsonObject.getJSONArray("data");
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

    private int REQ_CODE_CALL = 47;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_CALL) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void apiCallforGetOngoingJobDetail() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("language");
        value.add(prefsUtil.getLanguage());

        new ParseJSON(getActivity(), BaseUrl.getCustomerOngoingJob, param, value, CustoemreHomeJobPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CustoemreHomeJobPojo pojo = (CustoemreHomeJobPojo) obj;

                    if (pojo.getCustoemreHomeJobPojoItem().size() == 1) {
                        String strServerDateTime = pojo.getCustoemreHomeJobPojoItem().get(0).getServerDatetime();
                        String strAssignDateTime = pojo.getCustoemreHomeJobPojoItem().get(0).getAssignDate();

                        MyLogs.w("ZZZZ", "Server DateTime " + strServerDateTime);
                        MyLogs.w("ZZZZ", "Assign DateTime " + strAssignDateTime);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date serverDateTime = format.parse(strServerDateTime);
                            Date assignDateTime = format.parse(strAssignDateTime);
                            txtTimer.setVisibility(View.VISIBLE);
                            startTimerForOnGoingJob(assignDateTime, serverDateTime, true);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, false);
    }

    private long elapsedTime = 0;

    private void startTimerForOnGoingJob(Date assignDateTime, Date serverDateTime, boolean wantToStartTimer) {

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
                handlerForOnGoingJob.post(runnableForOnGoingJob);
            } else {
                String JOB_COMPLETE_TIME = elapsedHours + ":" + elapsedMinutes;
                MyLogs.w("TAG", "TOTAL JOB HOURS " + JOB_COMPLETE_TIME);
            }
        } else {
            Toast.makeText(getActivity(), "Invalid date", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handlerForOnGoingJob = new Handler();
    Runnable runnableForOnGoingJob = new Runnable() {
        public void run() {
            updateTimer();
        }
    };

    private void updateTimer() {

        elapsedTime = elapsedTime + 1000;
        long second = (elapsedTime / 1000) % 60;
        long minute = (elapsedTime / (1000 * 60)) % 60;
        long hour = (elapsedTime / (1000 * 60 * 60)) % 60;

        String formating = (hour <= 9 ? "0" + hour : String.valueOf(hour)) + ":" + (minute <= 9 ? "0" + minute : String.valueOf(minute)) + ":" + (second <= 9 ? "0" + second : String.valueOf(second));

        txtTimer.setText(formating);

        handlerForOnGoingJob.postDelayed(runnableForOnGoingJob, 1000);
    }
}
