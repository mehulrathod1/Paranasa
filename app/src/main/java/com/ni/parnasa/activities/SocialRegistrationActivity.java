package com.ni.parnasa.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.pojos.UserLoginPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/** This activity is unused in the app. */

public class SocialRegistrationActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Context mContext;

    private PrefsUtil prefsUtil;
    //    private PlaceAutocompleteAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private ImageView imgBack;
    private Button btnSignup;
    private EditText edt_phone_no, edtBDate;
    private AutoCompleteTextView autoTxtAddress;
    private RadioGroup rdbGrb;
    private CountryCodePicker spin_code;

    private String strFname, strLname, strEmail, strProfilePic, role, authToken;
    private boolean isFacebook;
    private double lat, lng;
    private int REQ_CODE_FINGER = 87;
    private int tmp_year, tmp_month, tmp_day;

    private UserLoginPojoItem loginPojo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_register);

//        mContext = SocialRegistrationActivity.this;
        prefsUtil = new PrefsUtil(mContext);
        try {
            isFacebook = getIntent().getBooleanExtra("isFacebook", false);
            strFname = getIntent().getStringExtra("fname");
            strLname = getIntent().getStringExtra("lname");
            strEmail = getIntent().getStringExtra("email");
            strProfilePic = getIntent().getStringExtra("image");
            role = getIntent().getStringExtra("role");
            authToken = getIntent().getStringExtra("authToken");
            lat = getIntent().getDoubleExtra("lat", 0.0);
            lng = getIntent().getDoubleExtra("lng", 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnSignup = findViewById(R.id.btnSignup);
        imgBack = findViewById(R.id.imgBack);
        spin_code = findViewById(R.id.spin_code);
        rdbGrb = findViewById(R.id.myRadioGroup);
        edt_phone_no = findViewById(R.id.edt_phone_no);
        edtBDate = findViewById(R.id.edtBDate);
        autoTxtAddress = findViewById(R.id.autoTxtAddress);

        Calendar calendar = Calendar.getInstance();
        tmp_year = calendar.get(Calendar.YEAR) - 18;
        tmp_month = calendar.get(Calendar.MONTH);
        tmp_day = calendar.get(Calendar.DAY_OF_MONTH);

        imgBack.setOnClickListener(this);
        btnSignup.setOnClickListener(this);

       /* mAdapter = new PlaceAutocompleteAdapter(mContext, android.R.layout.simple_list_item_1, BOUNDS_GREATER_SYDNEY, null);
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Places.GEO_DATA_API)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .build();
        } catch (IllegalStateException is) {
            is.printStackTrace();
        }
        autoTxtAddress.setAdapter(mAdapter);
        autoTxtAddress.setOnItemClickListener(mAutocompleteClickListener);*/

        edtBDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerForBirthdate();
            }
        });

        /*rdbGrb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (((RadioButton) group.getChildAt(0)).isChecked()) {
                    strGender = "male";
                } else if (((RadioButton) group.getChildAt(0)).isChecked()) {
                    strGender = "female";
                }
            }
        });*/

        /*spin_code.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country_code = spin_code.getSelectedCountryCode();
            }
        });*/

    }


    private void openDatePickerForBirthdate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, YY");

                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);

                edtBDate.setText(simpleDateFormat.format(c.getTime()));
                tmp_year = year;
                tmp_month = month;
                tmp_day = dayOfMonth;
            }
        }, tmp_year, tmp_month, tmp_day);

        datePickerDialog.setTitle("Select Date");
        datePickerDialog.setCancelable(false);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 18);
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis() + 1000);
//        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis() + 1000);
        datePickerDialog.show();
    }

    /*AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(i);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer places) {
                    if (!places.getStatus().isSuccess()) {
                        MyLogs.e("HOMEFRAGMENT", "Place query did not complete. Error: " + places.getStatus().toString());
                        return;
                    } else {
                        final Place myPlace = places.get(0);
                        LatLng queriedLocation = myPlace.getLatLng();

                        lat = queriedLocation.latitude;
                        lng = queriedLocation.longitude;

                        MyLogs.e("TAG", "Place API : " + queriedLocation.latitude + " | " + queriedLocation.longitude);
                        places.release();

                    }
                }
            });
            MyLogs.e("HOMEFRAGMENT", "Fetching details for ID: " + item.placeId);
        }
    };*/

    @Override
    public void onClick(View v) {
        if (imgBack == v) {
            onBackPressed();
        } else if (btnSignup == v) {
            String strMobile = edt_phone_no.getText().toString().trim();
            String strBdate = edtBDate.getText().toString().trim();
            String strAddress = autoTxtAddress.getText().toString().trim();
            String strCountryCode = spin_code.getSelectedCountryCodeWithPlus();

            if (strMobile.equals("")) {
                Toast.makeText(mContext, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            } else if (strMobile.length() < 10) {
                Toast.makeText(mContext, "Invalid mobile number", Toast.LENGTH_SHORT).show();
            } else if (strAddress.equals("")) {
                Toast.makeText(mContext, "Please enter address", Toast.LENGTH_SHORT).show();
            } else if (strBdate.equals("")) {
                Toast.makeText(mContext, "Please select birthdate", Toast.LENGTH_SHORT).show();
            } else {
                apiCallForSocialRegistration(strCountryCode, strMobile, strAddress, strBdate);
            }
        }
    }

    private void apiCallForSocialRegistration(String strCountryCode, String strMobile, String strAddress, String strBdate) {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("email");
        value.add(strEmail);

        param.add("first_name");
        value.add(strFname);

        param.add("last_name");
        value.add(strLname);

        param.add("gender");
        value.add((rdbGrb.getCheckedRadioButtonId() == R.id.male ? "male" : "female"));

        param.add("image");
        value.add(strProfilePic);

        param.add("full_mobile_number");
        value.add(strCountryCode + strMobile);

        param.add("location");
        value.add(strAddress);

        param.add("lat");
        value.add(String.valueOf(lat));

        param.add("lng");
        value.add(String.valueOf(lng));

        param.add("role");
        value.add(role);

        if (isFacebook) {
            param.add("fb_auth_token");
            value.add((authToken != null ? authToken : ""));
        } else {
            param.add("gp_auth_token");
            value.add((authToken != null ? authToken : ""));
        }

        param.add("user_code");
        value.add(generateUniqueCode());

        param.add("birthdate");
        value.add(strBdate);

        /*new ParseJSON(mContext, (isFacebook ? BaseUrl.socialLoginFacebook : BaseUrl.socialLoginGoogle), param, value, UserLoginPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    loginPojo = ((UserLoginPojo) obj).getUserLoginPojoItem();

//                    openAlertForFingerprintAuth();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void openAlertForFingerprintAuth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(R.string.fingerprint_title);
        builder.setMessage(R.string.fingerprint_msg);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, FingerprintAuthActivity.class);
                startActivityForResult(intent, REQ_CODE_FINGER);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                gotoDashboard(false);
            }
        });

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FINGER && resultCode == Activity.RESULT_OK) {
            gotoDashboard(true);
        }
    }

    private void gotoDashboard(boolean isReqFinger) {
        /** Save user data in preference and move to dashboard */

        if (loginPojo != null) {

            String user_id = loginPojo.getUserId();
            String first_name = loginPojo.getFirstName();
            String last_name = loginPojo.getLastName();
            String logo_image = loginPojo.getLogoImage();
            String gender = loginPojo.getGender();

            prefsUtil.Set_UserID(user_id);
            prefsUtil.setUserGender(gender);
            prefsUtil.setUserName(first_name + " " + last_name);
            prefsUtil.setUserPic(logo_image);
            prefsUtil.setRole(loginPojo.getRole());

            prefsUtil.setUserLoginDetail(loginPojo.toString());

            /*if (isReqFinger)
                prefsUtil.setRequiredFingerPrint(true);
            else {
                prefsUtil.setRequiredFingerPrint(false);
            }*/

            prefsUtil.SetIsloogedIn(true);

            if (role.equals("Customer")) {
                Intent intent = new Intent(mContext, CustomerHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (role.equals("Agency")) {
                Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (role.equals("Sole Professional")) {
                Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private String generateUniqueCode() {
        int random = (int) (Math.random() * 9000) + 1000;
        String Fname = String.valueOf(strFname.charAt(0));
        String Lname = String.valueOf(strLname.charAt(0));
        return Fname.toUpperCase() + Lname.toUpperCase() + random + spin_code.getSelectedCountryNameCode().toUpperCase();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyLogs.e("TAG", "OnConnectionFailed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        mAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
