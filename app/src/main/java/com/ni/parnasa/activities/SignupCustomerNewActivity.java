package com.ni.parnasa.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.models.SignupNewPojo;
import com.ni.parnasa.models.SignupNewPojoItem;
import com.ni.parnasa.pojos.UserLoginPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FingerPrintPrefUtil;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.ni.parnasa.utils.SaveDataUtility;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class SignupCustomerNewActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private ImageView imgBack, imgProfile;
    private TextView lblUsername;
    private CountryCodePicker sprCode;
    private Spinner sprGender;
    private EditText edtPhoneNo, edtLocation;
    private Button btnSaveAndNext;
    private LinearLayout linlayPhone, linlayGender;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 78;
    private int REQ_CODE_FINGER = 79;
    private int DAY, MONTH, YEAR;
    private boolean isFromSocial;
    private String userId = "", userRole = "", strUserName = "", gender = "male", profilePic = "", strFname = "", strLname = "", strEmail = "";
    private String socialType = "", strLocation = "", strCity = "", strCountry = "";
    private String authToken = "", fcmToken = "";

    private SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calTest;
    private LatLng latLng = null;
    private UserLoginPojoItem loginPojo = null;
    private SignupNewPojoItem signupNewPojoItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_customer_new);

        mContext = SignupCustomerNewActivity.this;
        prefsUtil = new PrefsUtil(mContext);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                fcmToken = task.getResult().getToken();
            }
        });

        isFromSocial = getIntent().getBooleanExtra("isFromSocial", false);

        if (isFromSocial) {
            userRole = getIntent().getStringExtra("userRole");
            strFname = getIntent().getStringExtra("fName");
            strLname = getIntent().getStringExtra("lName");
            strEmail = getIntent().getStringExtra("email");
            profilePic = getIntent().getStringExtra("profilePic");
            socialType = getIntent().getStringExtra("socialType");
            authToken = getIntent().getStringExtra("authToken");
            strUserName = strFname + " " + strLname;

        } else {
            userId = getIntent().getStringExtra("userId");
            strFname = getIntent().getStringExtra("fName");
            strLname = getIntent().getStringExtra("lName");
            userRole = getIntent().getStringExtra("userRole");
            profilePic = getIntent().getStringExtra("profilePic");

            strUserName = strFname + " " + strLname;
        }

        MyLogs.w("TAG", "ROLE " + userRole);

        initViews();

        try {
            Glide.with(mContext)
                    .asBitmap()
                    .load(profilePic)
                    .apply(new RequestOptions().error((gender.equalsIgnoreCase("female") ? R.drawable.img_default_female : R.drawable.img_default_male)))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imgProfile.setImageDrawable(circularBitmapDrawable);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            imgProfile.setImageDrawable(getDrawable((gender.equalsIgnoreCase("female") ? R.drawable.img_default_female : R.drawable.img_default_male)));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        imgBack = findViewById(R.id.imgBack);
        imgProfile = findViewById(R.id.imgProfile);
        lblUsername = findViewById(R.id.lblUsername);
        sprCode = findViewById(R.id.sprCode);
        edtPhoneNo = findViewById(R.id.edtPhoneNo);
        edtLocation = findViewById(R.id.edtLocation);
//        edtAddress = findViewById(R.id.edtAddress);
        sprGender = findViewById(R.id.sprGender);
//        edtDOB = findViewById(R.id.edtDOB);
        btnSaveAndNext = findViewById(R.id.btnSaveAndNext);
        linlayPhone = findViewById(R.id.linlayPhone);
        linlayGender = findViewById(R.id.linlayGender);

        lblUsername.setText(getString(R.string.welcome) + " " + strUserName);

        imgBack.setOnClickListener(this);
        btnSaveAndNext.setOnClickListener(this);
        edtLocation.setOnClickListener(this);

        calTest = Calendar.getInstance();
        DAY = calTest.get(Calendar.DAY_OF_MONTH);
        MONTH = calTest.get(Calendar.MONTH);
        YEAR = calTest.get(Calendar.YEAR) - 18;

        if (!isFromSocial) {
            linlayPhone.setVisibility(View.GONE);
            linlayGender.setVisibility(View.GONE);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {

            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            new GeoCoderHelper(SignupCustomerNewActivity.this, latLng.latitude, latLng.longitude, new GeoCoderHelper.onGetAddress() {
                public void onSuccess(String address, String city, String country) {
                    strLocation = address;
                    strCity = city;
                    strCountry = country;
                    edtLocation.setText(strLocation);
//                    edtAddress.setText(strLocation);
                }

                @Override
                public void onFail() {
                    Toast.makeText(mContext, "can't fetch location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSaveAndNext) {

            String strMobile = edtPhoneNo.getText().toString().trim();
            strLocation = edtLocation.getText().toString().trim();
//            String strAddress = edtAddress.getText().toString().trim();
//            String strDob = edtDOB.getText().toString().trim();

            if (isValidAllField(strMobile, strLocation, "", "")) {
                if (isFromSocial) {
                    if (!socialType.equals("")) {
                        apiCallForSocialRegistration(sprCode.getSelectedCountryCodeWithPlus(), strMobile, strLocation, "", "");
                    } else {
                        Toast.makeText(mContext, R.string.no_social_type, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    apiCallForSignupStepTwo(strMobile, strLocation, "", "");
                }
            }

        } else if (v == edtLocation) {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } /*else if (v == edtDOB) {
            openDatePicker();
        } */ else if (v == imgBack) {
            onBackPressed();
        }
    }

    /*private void gotoSocialSignup() {
        if (socialType.equalsIgnoreCase("g")) {
            apiCallForSocialRegistration();
        } else if (socialType.equalsIgnoreCase("f")) {
            //apiCallForFacebookSignup();
        } else {
            Toast.makeText(mContext, R.string.no_social_type, Toast.LENGTH_SHORT).show();
        }
    }*/


    private void apiCallForSocialRegistration(String strCountryCode, String strMobile, String strLocation, String strAddress, String strBdate) {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("communication_token");
        value.add(fcmToken);

        param.add("device_type");
        value.add(BaseUrl.deviceType);

        param.add("email");
        value.add(strEmail);

        param.add("first_name");
        value.add(strFname);

        param.add("last_name");
        value.add(strLname);

        param.add("image");
        value.add(profilePic);

        param.add("gender");
        value.add((sprGender.getSelectedItemPosition() == 1 ? "male" : "female"));

        param.add("full_mobile_number");
        value.add(strCountryCode + strMobile);

        param.add("address");
        value.add(strLocation); // Client cimmited

        param.add("location");
        value.add(strLocation);

        param.add("city");
        value.add(strCity);

        param.add("country");
        value.add(strCountry);

        param.add("lat");
        value.add((latLng != null ? String.valueOf(latLng.latitude) : "0.0"));

        param.add("lng");
        value.add((latLng != null ? String.valueOf(latLng.longitude) : "0.0"));

        param.add("role");
        value.add(userRole);

        if (socialType.equalsIgnoreCase("f")) {
            param.add("fb_auth_token");
            value.add((authToken != null ? authToken : ""));
        } else {
            param.add("gp_auth_token");
            value.add((authToken != null ? authToken : ""));
        }

        param.add("user_code");
        value.add(generateUniqueCode());

        new ParseJSON(mContext, (socialType.equalsIgnoreCase("f") ? BaseUrl.socialLoginFacebook : BaseUrl.socialLoginGoogle), param, value, SignupNewPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    signupNewPojoItem = ((SignupNewPojo) obj).getSignupNewPojoItem();
                    prefsUtil.setDeviceAuthToken(signupNewPojoItem.getDeviceAuthToken());

                    gotoDashboard(false);

//                    openAlertForFingerprintAuth();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String generateUniqueCode() {
        int random = (int) (Math.random() * 9000) + 1000;
        String Fname = String.valueOf(strFname.charAt(0));
        String Lname = String.valueOf(strLname.charAt(0));
        return Fname.toUpperCase() + Lname.toUpperCase() + random + sprCode.getSelectedCountryNameCode().toUpperCase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                edtLocation.setText(place.getAddress());
                latLng = place.getLatLng();

                Log.e("TAG", "Place: " + place.getName() + ", " + place.getAddress() + " " + place.getLatLng().latitude);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == REQ_CODE_FINGER && resultCode == Activity.RESULT_OK) {
//            gotoDashboard(true);
        }
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calTest.set(year, month, dayOfMonth);
                YEAR = year;
                MONTH = month;
                DAY = dayOfMonth;
//                edtDOB.setText(formater.format(calTest.getTime()));
            }
        }, YEAR, MONTH, DAY);

        Calendar calTmp = Calendar.getInstance();
        calTmp.set(Calendar.YEAR, calTmp.get(Calendar.YEAR) - 18);
        datePickerDialog.getDatePicker().setMaxDate(calTmp.getTimeInMillis() + 1000);
        datePickerDialog.show();
    }

    public boolean isValidAllField(String strMobile, String strLocation, String strAddress, String strDob) {

        if (!isFromSocial) {
            if (strLocation.equals("")) {
                Toast.makeText(mContext, R.string.msg_location_validation, Toast.LENGTH_SHORT).show();
                return false;
            }/* else
            if (strAddress.equals("")) {
                Toast.makeText(mContext, R.string.msg_address_validation, Toast.LENGTH_SHORT).show();
                return false;
            }*/ /*else if (strDob.equals("")) {
                Toast.makeText(mContext, R.string.msg_dob_validation, Toast.LENGTH_SHORT).show();
                return false;
            } */ else {
                return true;
            }
        } else {
            if (strMobile.equals("")) {
                Toast.makeText(mContext, R.string.msg_phone_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else if (strLocation.equals("")) {
                Toast.makeText(mContext, R.string.msg_address, Toast.LENGTH_SHORT).show();
                return false;
            } else if (sprGender.getSelectedItemPosition() == 0) {
                Toast.makeText(mContext, R.string.msg_gender_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
    }

    private void apiCallForSignupStepTwo(String strMobile, String strLocation, String strAddress, String strDob) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(userId);

        param.add("user_role");
        value.add(userRole);

        param.add("address");
        value.add(strLocation); // Client cimmited

        param.add("location");
        value.add(strLocation);

        param.add("city");
        value.add(strCity);

        param.add("country");
        value.add(strCountry);

        param.add("lat");
        value.add((latLng != null ? String.valueOf(latLng.latitude) : "0.0"));

        param.add("lng");
        value.add((latLng != null ? String.valueOf(latLng.longitude) : "0.0"));

        new ParseJSON(mContext, BaseUrl.signupStepTwo, param, value, SignupNewPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    signupNewPojoItem = ((SignupNewPojo) obj).getSignupNewPojoItem();

                    /** For new way finger print auth
                     * default isCompletedSignup values false but after completion of signup process it is true
                     * and we can check isCompletedSignup flag in login activity if it is true then display emailId prefix in edittext otherwise not
                     * */
                    FingerPrintPrefUtil.with(mContext).write(BaseUrl.isCompletedSignup, true);
                    /**************************/
                    gotoDashboard(false);

//                    openAlertForFingerprintAuth();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openAlertForFingerprintAuth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(R.string.fingerprint_title);
        builder.setMessage(R.string.fingerprint_msg);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(mContext, FingerprintAuthActivity.class);
                startActivityForResult(intent, REQ_CODE_FINGER);
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

//                gotoDashboard(false);
            }
        });

        builder.create().show();
    }

    /**
     * This method call after finger print process completed whether it is required or not
     */
    private void gotoDashboard(boolean isRequiredFingerAuth) {

        if (signupNewPojoItem != null) {
            SaveDataUtility utility = new SaveDataUtility(mContext);
            utility.saveData(signupNewPojoItem.getUserId(),
                    signupNewPojoItem.getFirstName(),
                    signupNewPojoItem.getLastName(),
                    signupNewPojoItem.getGender(),
                    signupNewPojoItem.getLogoImage(),
                    signupNewPojoItem.getRole(),
                    isRequiredFingerAuth,
                    true);

//            utility.saveDeviceAuthToken(signupNewPojoItem.getDeviceAuthToken());

            if (userRole.equalsIgnoreCase("Customer")) {
                Intent intent = new Intent(mContext, CustomerHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } /*else if (userRole.equalsIgnoreCase("")) {
                Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }*/
        }
    }
}
