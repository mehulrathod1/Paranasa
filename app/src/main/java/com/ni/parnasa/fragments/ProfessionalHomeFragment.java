package com.ni.parnasa.fragments;

import android.Manifest;
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
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.models.country.CountryResponse;
import com.ni.parnasa.models.currencies.CurrencySymbolResponse;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.ProfessionaListPojo;
import com.ni.parnasa.pojos.ProfessionaListPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.CommonUtils;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.KeyboardUtils;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefConstant;
import com.ni.parnasa.utils.PrefUtils;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfessionalHomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private PrefsUtil prefsUtil;
    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private Location locationCurrent = null;
    protected Marker professionalMarker = null;

    private String searchedService = "", searchedLocation = "", bestProvider = "";
    private int UPDATE_INTERVAL;
    private float default_zoom_level = 13.0f;
    private boolean inProgress = false;
    private int markerWidth = 160, markerHeight = 160, count = 0;

    private Handler handlerForMap = new Handler();
    private Runnable runnableForMap = new Runnable() {
        @Override
        public void run() {
            apiCallForSearch(searchedService, searchedLocation);
        }
    };

    GoogleMap.OnCameraMoveListener onCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            if (!inProgress) {
                inProgress = true;
                handlerForMap.postDelayed(runnableForMap, 600);
            }
        }
    };

    private View contentView, mapView;
    private ProgressDialog progressDialog;
    private ImageView imgCurrentLocation, imgClearService, imgClearLocation, imgSearchLocation, search_btn, imgProfileBottomPopup,
            iv_close, iv_indOne, iv_indtwo, iv_indthree, iv_chat, imgNav;

    private RelativeLayout rel_search, linlayBottomPopup;
    private LinearLayout linlayRight, linlayLeft;
    private Button btn_occu, btn_on_way, btn_free, bt_call, btnBooking;
    private AutoCompleteTextView autoSearchByLocation, autoSearchByService;
    private TextView tv_popup_name, tv_popup_number, tv_usercode, txtButtonTurnOn;
    private RatingBar rb_rating;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> searchListItme = new ArrayList<>();
    private List<ProfessionaListPojoItem> professionaListPojoItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_professional_home, container, false);

        initViews(view);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        mapView = supportMapFragment.getView();
        supportMapFragment.getMapAsync(this);

        new apiCallGetServicesForAutoComplete().execute();

        return view;
    }

    private void initViews(View view) {
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        prefsUtil = ((ProfessionalHomeActivity) getActivity()).prefsUtil;
        progressDialog = ((ProfessionalHomeActivity) getActivity()).progressDialog;

        UPDATE_INTERVAL = prefsUtil.getUpdateInterval();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        bestProvider = LocationManager.NETWORK_PROVIDER;

        contentView = view.findViewById(R.id.content);
        rel_search = view.findViewById(R.id.rel_search);
        btn_occu = ((ProfessionalHomeActivity) getActivity()).btn_occu;
        btn_on_way = ((ProfessionalHomeActivity) getActivity()).btn_on_way;
        btn_free = ((ProfessionalHomeActivity) getActivity()).btn_free;

        rel_search.setOnClickListener(clickListener);
        btn_on_way.setOnClickListener(clickListener);
        btn_occu.setOnClickListener(clickListener);
        btn_free.setOnClickListener(clickListener);

        imgClearService = view.findViewById(R.id.imgClear);
        imgClearLocation = view.findViewById(R.id.imgClearLocation);
        imgSearchLocation = view.findViewById(R.id.imgSearchLocation);
        search_btn = view.findViewById(R.id.search_btn);
        linlayBottomPopup = view.findViewById(R.id.service_details);

        autoSearchByService = view.findViewById(R.id.search_home);
        autoSearchByLocation = view.findViewById(R.id.autoSearchByLocation);
        autoSearchByLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getActivity());
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        tv_popup_name = view.findViewById(R.id.header_name);
        tv_popup_number = view.findViewById(R.id.header_number);
        tv_usercode = view.findViewById(R.id.u_code);
        rb_rating = view.findViewById(R.id.myRatingBar);
        imgProfileBottomPopup = view.findViewById(R.id.imgBottomPopup);
        linlayRight = view.findViewById(R.id.linlayRight);
        linlayLeft = view.findViewById(R.id.linlayLeft);
        iv_close = view.findViewById(R.id.close);
        iv_indOne = view.findViewById(R.id.indicate_one);
        iv_indtwo = view.findViewById(R.id.indicate_two);
        iv_indthree = view.findViewById(R.id.indicate_three);

        bt_call = view.findViewById(R.id.call_);
        btnBooking = view.findViewById(R.id.btnBooking);
        iv_chat = view.findViewById(R.id.chat);
        imgNav = view.findViewById(R.id.imgNav);


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
                    apiCallForSearch(searchedService, searchedLocation);
                } else {
                    Toast.makeText(getActivity(), R.string.location_not_found, Toast.LENGTH_SHORT).show();
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

                apiCallForSearch(searchedService, searchedLocation);
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

        professionaListPojoItems = new ArrayList<>();

        imgCurrentLocation = ((ProfessionalHomeActivity) getActivity()).imgCurrentLocation;
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
                    } else {
                        requestForLocationUpdate();
                        Toast.makeText(getActivity(), R.string.location_not_found, Toast.LENGTH_SHORT).show();
                    }
                }

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

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_occu:
                    apiCallForUpdateProfessionalStatus("occupied");
                    break;
                case R.id.btn_on_way:
                    apiCallForUpdateProfessionalStatus("onTheWay");
                    break;
                case R.id.btn_free:
                    apiCallForUpdateProfessionalStatus("free");
                    break;
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
//        googleMap.setOnCameraMoveListener(onCameraMoveListener);
        googleMap.setOnMarkerClickListener(this);

        displayLocation();
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        locationButton.setVisibility(View.GONE);

        locationCurrent = locationManager.getLastKnownLocation(bestProvider);

        requestForLocationUpdate();

//        getAddressFromLatLong();

        addPinOfProfessional(true);

//        handlerForMap.postDelayed(runnableForMap, 2000);
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

                Glide.with(getActivity())
                        .asBitmap()
                        .load(pojoItem.getUserProfilePicture())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
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
                Toast.makeText(getActivity(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        } else {
//          Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
        }
        return true;
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
            value.add(searchedLocation);

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

        new ParseJSON(getActivity(), strUrl, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    Toast.makeText(getActivity(), pojo.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPinOfProfessional(final boolean isInitially) {
        if (locationCurrent != null) {
            int imgResource;
            if (prefsUtil.getUserGender().equals("male")) {
                imgResource = R.drawable.male;
            } else {
                imgResource = R.drawable.female;
            }

            Glide.with(getActivity()).asBitmap()
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
        }
    }

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 123;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                searchedLocation = place.getAddress();
                autoSearchByLocation.setText(searchedLocation);
                imgSearchLocation.setVisibility(View.GONE);
                imgClearLocation.setVisibility(View.VISIBLE);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), default_zoom_level));

                apiCallForSearch(searchedService, searchedLocation);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void setupAdMob(View view) {
        /** Load Google Ad */
        LinearLayout linlayConteiner = view.findViewById(R.id.adMobView);

        new GoogleAdLoader(getActivity(), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }

    public void requestForLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 5000, 0, myLocationUpdateListener);
    }

    public void getAddressFromLatLong() {
        if (locationCurrent != null) {
            new GeoCoderHelper(getActivity(), locationCurrent.getLatitude(), locationCurrent.getLongitude(), new GeoCoderHelper.onGetAddress() {
                public void onSuccess(String address, String city, String country) {
                    searchedLocation = address;
                    String countryCurrencyAbbreviation = "USD";
                    String countryCurrencySymbol = "$";
                    String jsonCountry = CommonUtils.loadJSONFromAsset(getActivity(), "countries.json");
                    String jsonCurrency = CommonUtils.loadJSONFromAsset(getActivity(), "countryCurrency.json");

                    Gson gsonCountry = new Gson();
                    CountryResponse countryResponse = gsonCountry.fromJson(jsonCountry, CountryResponse.class);

                    Gson gsonCountryCurrency = new Gson();
                    CurrencySymbolResponse currencySymbolResponse = gsonCountryCurrency.fromJson(jsonCurrency, CurrencySymbolResponse.class);

                    List<CountryResponse.CountryItem> countryItems = countryResponse.getCountries().getCountry();

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

                    Log.e("TAG", "onSuccess: country::  "+country+" countryCurrencySymbol:: "+countryCurrencySymbol+ " countryCurrencyAbbreviation:: "+countryCurrencyAbbreviation );
                    PrefUtils.with(getActivity()).write(PrefConstant.CURRENT_PRICE,"1");
                }

                @Override
                public void onFail() {
                    Toast.makeText(getActivity(), "can't fetch location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private LocationListener myLocationUpdateListener = new LocationListener() {
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

    public void apiCallForSearch(String strServiceName, String strAddress) {

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

        new ParseJSON(getActivity(), BaseUrl.serachByServiceOrLocation, param, value, ProfessionaListPojo.class, new ParseJSON.OnResultListner() {
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

    @Override
    public void onDestroy() {
        handlerForMap.removeCallbacks(runnableForMap);

        locationManager.removeUpdates(myLocationUpdateListener);

        super.onDestroy();
    }



}
