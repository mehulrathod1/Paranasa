package com.ni.parnasa.activities.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.pojos.CategoriesPojo;
import com.ni.parnasa.pojos.CategoriesPojoItem;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.ProfessionalFilterPojo;
import com.ni.parnasa.pojos.ProfessionalFilterPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FingerPrintPrefUtil;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.KeyboardUtils;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

public class CreateJobActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Context mContext;
    private PrefsUtil prefsUtil;
    private Location locationCurrent;
    private LocationManager locationManager;

    private ImageView imgBack, imgProfile;
    private EditText edtTime, edtDate, edtJobDetails, edtJobNotes, edtProfessional, edtAddress, edtLocation;
    private AutoCompleteTextView autoCategory;
    //    private Spinner sprCategory, sprProfessional;
    private Button btnCreateJob;

    private ArrayAdapter<CategoriesPojoItem> adptCategory;
    private ArrayAdapter<ProfessionalFilterPojoItem> adptProfessional;
    private List<CategoriesPojoItem> listCategory;
    private List<ProfessionalFilterPojoItem> listProfessional;
    private Calendar calendarCurrent;
    private Calendar calTest;

    private final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private GoogleApiClient mGoogleApiClient;

    private int HH, MM, AMorPM;
    private boolean isFromJobList;
    private LatLng latLng;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 77;

    private String categoryId = "", professionalId = "", professionalName = "", service = "";
    private int calEventId = -1;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        mContext = CreateJobActivity.this;

        isFromJobList = getIntent().getBooleanExtra("isFromJobList", false);
        professionalId = getIntent().getStringExtra("professionalId");
        professionalName = getIntent().getStringExtra("professionalName");
        service = getIntent().getStringExtra("service");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationCurrent = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        init();

        getAddressFromLatLong();

        checkManualContactPermission();

        setupAdMob();
    }

    private void checkManualContactPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateJobActivity.this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 99);
        } else {
//            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 99) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkManualContactPermission();
            } else {
                onBackPressed();
            }
        }
    }

    private void init() {

        Places.initialize(mContext, getString(R.string.google_maps_key));

        prefsUtil = new PrefsUtil(mContext);

        /** Check whether any account selected for calendar event store or not */
        calEventId = FingerPrintPrefUtil.with(mContext).readCalendarEventId();
        if (calEventId == -1) {
            try {
                openDialogForChooseAccount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        calTest = Calendar.getInstance();
        calendarCurrent = Calendar.getInstance();

        HH = calendarCurrent.get(Calendar.HOUR_OF_DAY);
        MM = calendarCurrent.get(Calendar.MINUTE);
        AMorPM = calendarCurrent.get(Calendar.AM_PM);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        imgBack = findViewById(R.id.imgBack);
        imgProfile = findViewById(R.id.imgProfile);
//        sprCategory = findViewById(R.id.sprCategory);
//        sprProfessional = findViewById(R.id.sprProfessional);
//        autoLocation = findViewById(R.id.txtLocation);
        edtTime = findViewById(R.id.edtTime);
        edtDate = findViewById(R.id.edtDate);
        edtJobDetails = findViewById(R.id.edtJobDetails);
        edtJobNotes = findViewById(R.id.edtJobNotes);
        edtProfessional = findViewById(R.id.edtProfessional);
        btnCreateJob = findViewById(R.id.btnCreateJob);
        autoCategory = findViewById(R.id.autoCategory);
        edtAddress = findViewById(R.id.edtAddress);
        edtLocation = findViewById(R.id.edtLocation);

        autoCategory.setText(service);
        edtProfessional.setText(professionalName);

        /*edtDate.setText(simpleDateFormat.format(calendarCurrent.getTime()));
        edtTime.setText(formatOfTime(HH, MM, AMorPM));*/

        listCategory = new ArrayList<>();
        listProfessional = new ArrayList<>();

        ProfessionalFilterPojoItem pojoItem = new ProfessionalFilterPojoItem();
        pojoItem.setFirstName("Select");
        pojoItem.setLastName("Professional");
        listProfessional.add(pojoItem);

        /*adptCategory = new ArrayAdapter<>(mContext, R.layout.adpt_spinner, listCategory);
        autoCategory.setAdapter(adptCategory);*/
//        autoCategory.setThreshold(2);

        imgBack.setOnClickListener(this);
        edtLocation.setOnClickListener(this);
        edtTime.setOnClickListener(this);
        edtDate.setOnClickListener(this);
        btnCreateJob.setOnClickListener(this);

       /* autoCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoriesPojoItem pojoItem1 = (CategoriesPojoItem) parent.getItemAtPosition(position);
                categoryId = pojoItem1.getServiceId();
            }
        });*/

//        adptProfessional = new ArrayAdapter<>(mContext, R.layout.adpt_spinner, listProfessional);
//        sprProfessional.setAdapter(adptProfessional);


        /*sprCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    CategoriesPojoItem pojoItem = (CategoriesPojoItem) parent.getSelectedItem();

                    apiCallForGetProfessionalList(pojoItem.getServiceId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        /*sprProfessional.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    ProfessionalFilterPojoItem pojoItem = (ProfessionalFilterPojoItem) parent.getSelectedItem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

//        apiCallForGetCategory();
    }

    private void openDialogForChooseAccount() {
        final ContentResolver cr = getContentResolver();
        Cursor cursor;
        if (Build.VERSION.SDK_INT >= 8) {
            cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"), new String[]{"_id", "calendar_displayName", "isPrimary"}, null, null, null);
        } else {
            cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{"_id", "displayname"}, null, null, null);
        }

        if (cursor.moveToFirst()) {

            List<String> nameOfAccount = new ArrayList<>();
            List<Integer> idOfAccount = new ArrayList<>();

            for (int i = 0; i < cursor.getCount(); i++) {

                if (cursor.getString(2).equalsIgnoreCase("1")) {
                    idOfAccount.add(cursor.getInt(0));
                    nameOfAccount.add(cursor.getString(1));
                }
                cursor.moveToNext();
            }

            int availableAccountSize = idOfAccount.size();

            final int[] calIds = new int[availableAccountSize];
            final String[] calNames = new String[availableAccountSize];

            for (int i = 0; i < idOfAccount.size(); i++) {
                calIds[i] = idOfAccount.get(i);
                calNames[i] = nameOfAccount.get(i);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.cal_event_title);
            builder.setCancelable(false);
            builder.setSingleChoiceItems(calNames, -1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    calEventId = calIds[which];
//                    Toast.makeText(mContext, "ID " + calEventId, Toast.LENGTH_SHORT).show();
                    FingerPrintPrefUtil.with(mContext).writeCalendarEventId(calEventId);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == edtDate) {
            openDatePicker();
        } else if (v == edtTime) {
            openTimePicker();
        } else if (v == btnCreateJob) {
            if (isValidAllField()) {
//                Toast.makeText(mContext, "ApiCalling", Toast.LENGTH_SHORT).show();
                KeyboardUtils.hideSoftKeyboard(CreateJobActivity.this);
                apiCallForCreateJob();
            }
        } else if (v == edtLocation) {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                edtLocation.setText(place.getAddress());
                edtLocation.setError(null);
                latLng = place.getLatLng();
                edtAddress.setText(place.getAddress());
                Log.e("TAG", "Place: " + place.getName() + ", " + place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void apiCallForGetProfessionalList(String serviceId) {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("services_id");
        value.add(serviceId);

        new ParseJSON(mContext, BaseUrl.getFilterProfessional, param, value, ProfessionalFilterPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                listProfessional.clear();
                ProfessionalFilterPojoItem pojoItem = new ProfessionalFilterPojoItem();
                pojoItem.setFirstName("Select");
                pojoItem.setLastName("Professional");

                listProfessional.add(0, pojoItem);
                adptProfessional.notifyDataSetChanged();

                if (status) {
                    ProfessionalFilterPojo pojo = (ProfessionalFilterPojo) obj;

                    listProfessional.addAll(pojo.getProfessionalFilterPojoItem());
                    adptProfessional.notifyDataSetChanged();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apiCallForGetCategory() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        new ParseJSON(mContext, BaseUrl.getAllCategory, param, value, CategoriesPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CategoriesPojo pojo = (CategoriesPojo) obj;

                    /*CategoriesPojoItem pojoItem = new CategoriesPojoItem();
                    pojoItem.setServiceName("Select category");
                    listCategory.add(0, pojoItem);*/
                    listCategory.addAll(pojo.getCategoriesPojoItem());
                    adptCategory.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String formatOfTime(int H, int M, int PrePost) {

        String res = "";

        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        try {
            Date date = displayFormat.parse(H + ":" + M);
//            Date date = parseFormat.parse("10:30 PM");
            res = parseFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    private SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calTest.set(year, month, dayOfMonth);

                edtDate.setText(formater.format(calTest.getTime()));
            }
        }, calTest.get(Calendar.YEAR), calTest.get(Calendar.MONTH), calTest.get(Calendar.DAY_OF_MONTH));

//        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis() - 1);

        datePickerDialog.getDatePicker().setMinDate(calendarCurrent.getTimeInMillis() - 1000);

        Calendar tmpCal = Calendar.getInstance();
        tmpCal.add(Calendar.MONTH, 1);
        datePickerDialog.getDatePicker().setMaxDate(tmpCal.getTimeInMillis());
        datePickerDialog.show();
    }

    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                calTest.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calTest.set(Calendar.MINUTE, minute);
                HH = hourOfDay;
                MM = minute;

                if (hourOfDay < 12)
                    edtTime.setText(formatOfTime(hourOfDay, minute, 0));
                else
                    edtTime.setText(formatOfTime(hourOfDay, minute, 1));
            }
        }, HH, MM, true);

        timePickerDialog.show();
    }

    private void openLocationSelection() {
        /**
         * After enable billing option this can be work
         *
         * when you implement this please remove old places API dependency
         * |
         * | */

        /*try {
            PlacesClient placesClient = Places.createClient(mContext);

            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

            autocompleteFragment.setPlaceFields(placeFields);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    MyLogs.e("TAG", "onPlaceSelected : " + place.getName() + ", " + place.getId());
                }

                @Override
                public void onError(Status status) {
                    MyLogs.e("TAG", "onPlaceSelect an error occurred: " + status);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(mContext, "connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        mAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public boolean isValidAllField() {
        /*if (sprCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(mContext, R.string.msg_category_validation, Toast.LENGTH_SHORT).show();
            return false;
        } */
        if (autoCategory.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_category_validation, Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (sprProfessional.getSelectedItemPosition() == 0) {
            Toast.makeText(mContext, R.string.msg_professional_validation, Toast.LENGTH_SHORT).show();
            return false;
        } */ else if (edtDate.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_date_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtTime.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_time_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtLocation.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_location_validation, Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (edtAddress.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_address_validation, Toast.LENGTH_SHORT).show();
            return false;
        }*/ else if (edtJobDetails.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_job_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (latLng == null) {
            Toast.makeText(mContext, R.string.loc_not_found, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void apiCallForCreateJob() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("customer_id");
        value.add(prefsUtil.GetUserID());

        /*param.add("service_id");
        value.add(categoryId);*/

        param.add("services_name");
        value.add(autoCategory.getText().toString());

//        value.add(((CategoriesPojoItem) sprCategory.getSelectedItem()).getServiceId());

        param.add("professional_id");
        value.add(professionalId);
//        value.add(((ProfessionalFilterPojoItem) sprProfessional.getSelectedItem()).getUserId());

        param.add("job_starts_on");
        value.add(sf.format(calTest.getTime()));

        param.add("job_location");
        value.add(edtLocation.getText().toString().trim());

        param.add("job_address");
        value.add(edtLocation.getText().toString().trim());
//        value.add(edtAddress.getText().toString().trim());

        param.add("job_lat");
        value.add(String.valueOf(latLng.latitude));

        param.add("job_lng");
        value.add(String.valueOf(latLng.longitude));

        param.add("job_details");
        value.add(edtJobDetails.getText().toString().trim());

        param.add("job_remarks");
        value.add(edtJobNotes.getText().toString().trim());

        MyLogs.e("TasaAG", "VALUE " + value);


        /*MyLogs.e("TAG", "PARAM " + param);
        MyLogs.e("TAG", "VALUE " + value);
*/
        new ParseJSON(mContext, BaseUrl.createJob, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                MyLogs.e("tddoasr", String.valueOf(status));

                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;

                    if (isFromJobList) {
                        Intent intent = new Intent("onTabRefresh");
                        intent.putExtra("refreshTabNo", 0);
                        sendBroadcast(intent);
                    }
                    addEventIntoGoogleCalendar("PickMeAppJob", edtJobDetails.getText().toString().trim(), calTest.getTimeInMillis(), (calTest.getTimeInMillis() + (1000 * 60 * 60 * 1))); //event end after 1 hours
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                    MyLogs.e("toast",pojo.getMessage());
                    onBackPressed();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                    MyLogs.e("error",obj.toString());
                    MyLogs.e("error", String.valueOf(status));
                }
            }
        });
    }

    private void addEventIntoGoogleCalendar(String title, String desc, long startTime, long endTime) {

        try {
            MyLogs.w("ZZZ", "CalEventID:" + calEventId);
            if (calEventId != -1) {
                ContentValues cv = new ContentValues();
                cv.put("calendar_id", calEventId);
                cv.put("title", title);
                cv.put("description", desc);
                cv.put("dtstart", startTime);
                cv.put("dtend", endTime);
                cv.put("hasAlarm", 1);
                cv.put("eventTimezone", "UTC/GMT +5:30");

                Uri newEvent;
                final ContentResolver cr = getContentResolver();

                if (Build.VERSION.SDK_INT >= 8)
                    newEvent = cr.insert(Uri.parse("content://com.android.calendar/events"), cv);
                else
                    newEvent = cr.insert(Uri.parse("content://calendar/events"), cv);

                if (newEvent != null) {
                    long id = Long.parseLong(newEvent.getLastPathSegment());
                    ContentValues values = new ContentValues();
                    values.put("event_id", id);
                    values.put("method", 1);
                    values.put("minutes", 5); // notify before 5 min
                    if (Build.VERSION.SDK_INT >= 8)
                        cr.insert(Uri.parse("content://com.android.calendar/reminders"), values);
                    else
                        cr.insert(Uri.parse("content://calendar/reminders"), values);

                } else {
                    MyLogs.w("TAG", "Event not added");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_create_job_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }

    public void getAddressFromLatLong() {
        if (locationCurrent != null) {
            new GeoCoderHelper(CreateJobActivity.this, locationCurrent.getLatitude(), locationCurrent.getLongitude(), new GeoCoderHelper.onGetAddress() {
                public void onSuccess(String address, String city, String country) {
//                    searchedLocation = address;
                    edtLocation.setText(address);
                    latLng = new LatLng(locationCurrent.getLatitude(), locationCurrent.getLongitude());

//                    strCity = city;
//                    strCountry = country;
//                    edtLocation.setText(strLocation);
                }

                @Override
                public void onFail() {
                    Toast.makeText(mContext, R.string.cant_fetch_location, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
