package com.ni.parnasa.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.activities.professional.owncompany.ProfileDetailsThree;
import com.ni.parnasa.adapters.ChipsAdapter;
import com.ni.parnasa.adapters.ChipsAdapterForSignup;
import com.ni.parnasa.models.SignupNewPojo;
import com.ni.parnasa.models.SignupNewPojoItem;
import com.ni.parnasa.models.SocialResponsePojo;
import com.ni.parnasa.models.SocialResponsePojoItem;
import com.ni.parnasa.tmpPojos.GetAllServicePojo;
import com.ni.parnasa.tmpPojos.GetAllServicePojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.ni.parnasa.utils.SaveDataUtility;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

public class SignupProfessionalNewActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private ImageView imgBack, imgProfile;
    private TextView lblUsername, txtAddKeyword, txtAddService;
    private CountryCodePicker sprCode;
    private Spinner sprGender;
    private Button btnSaveAndNext;
    private EditText edtPhoneNo, edtServiceKeyword, edtLocation, edtCompanyName;
    private AutoCompleteTextView autoTxtService;
    private RecyclerView rvServices, rvKeyword;
    private LinearLayout linlayPhone, linlayAddressLocation, linlayGender;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 78;
    private int REQ_CODE_FINGER = 79;
    private int REQ_CODE_SERVICE = 80;
    private int DAY, MONTH, YEAR;
    private boolean isFromSocial;
    //    private String userId = "", userRole = "", strUserName = "", gender = "", profilePic = "";
    private String userId = "", userRole = "", strUserName = "", gender = "male", profilePic = "", strFname = "", strLname = "", strEmail = "";//, mobile = "";
    private String authToken = "", fcmToken = "", socialType = "";
    private String strLocation = "", strCity = "", strCountry = "";

    private Calendar calTest;
    private LatLng latLng = null;
    private SignupNewPojoItem signupNewPojoItem = null;

    private ArrayAdapter<GetAllServicePojoItem> serviceAdapter;
    private ArrayList<GetAllServicePojoItem> serviceList;

    private ArrayList<String> listKeyword;
    private ChipsAdapter adapterKeywordChip;
    private ArrayList<GetAllServicePojoItem> chipListService;
    private ChipsAdapterForSignup chipsAdapterForSignup;
    private String skillStrin;
    private PrefsUtil prefsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup_professional_new);

        mContext = SignupProfessionalNewActivity.this;
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
            userRole = getIntent().getStringExtra("userRole");
            strUserName = getIntent().getStringExtra("userName");
            gender = getIntent().getStringExtra("gender");
            profilePic = getIntent().getStringExtra("profilePic");
//            mobile = getIntent().getStringExtra("mobile");
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
        btnSaveAndNext = findViewById(R.id.btnSaveAndNext);
        sprCode = findViewById(R.id.sprCode);
        edtPhoneNo = findViewById(R.id.edtPhoneNo);
        edtLocation = findViewById(R.id.edtLocation);
        edtCompanyName = findViewById(R.id.edtCompanyName);
//        edtAddress = findViewById(R.id.edtAddress);
        sprGender = findViewById(R.id.sprGender);
//        edtDOB = findViewById(R.id.edtDOB);
        autoTxtService = findViewById(R.id.autoTxtService);
        edtServiceKeyword = findViewById(R.id.edtServiceKeyword);
        txtAddKeyword = findViewById(R.id.txtAddKeyword);
        txtAddService = findViewById(R.id.txtAddService);
        rvServices = findViewById(R.id.rvServices);
        rvKeyword = findViewById(R.id.rvKeyword);
        linlayPhone = findViewById(R.id.linlayPhone);
        linlayAddressLocation = findViewById(R.id.linlayAddressLocation);
        linlayGender = findViewById(R.id.linlayGender);

        lblUsername.setText(getString(R.string.welcome) + " " + strUserName);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        rvServices.setLayoutManager(flexboxLayoutManager);

        chipListService = new ArrayList<>();
        chipsAdapterForSignup = new ChipsAdapterForSignup(mContext, chipListService, true);
        rvServices.setAdapter(chipsAdapterForSignup);

        FlexboxLayoutManager flexboxLayoutManager1 = new FlexboxLayoutManager(mContext);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        rvKeyword.setLayoutManager(flexboxLayoutManager1);

        listKeyword = new ArrayList<>();
        adapterKeywordChip = new ChipsAdapter(mContext, listKeyword, true);
        rvKeyword.setAdapter(adapterKeywordChip);

        imgBack.setOnClickListener(this);
        btnSaveAndNext.setOnClickListener(this);
//        edtDOB.setOnClickListener(this);
        edtLocation.setOnClickListener(this);
        txtAddKeyword.setOnClickListener(this);
        txtAddService.setOnClickListener(this);

        calTest = Calendar.getInstance();
        DAY = calTest.get(Calendar.DAY_OF_MONTH);
        MONTH = calTest.get(Calendar.MONTH);
        YEAR = calTest.get(Calendar.YEAR) - 18;

        serviceList = new ArrayList<>();
        serviceAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, serviceList);
        autoTxtService.setAdapter(serviceAdapter);
        autoTxtService.setThreshold(2);

        autoTxtService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GetAllServicePojoItem item = (GetAllServicePojoItem) parent.getItemAtPosition(position);

                chipListService.clear();
                chipListService.add(item);
                chipsAdapterForSignup.notifyDataSetChanged();

                /*if (chipListService.size() > 0) {
                    boolean isMatchAny = false;
                    for (int i = 0; i < chipListService.size(); i++) {
                        if (chipListService.get(i).getServiceName().equalsIgnoreCase(item.getServiceName())) {
                            isMatchAny = true;
                            break;
                        }
                    }

                    if (!isMatchAny) {
                        chipListService.add(item);
                        chipsAdapterForSignup.notifyDataSetChanged();
                    }
                } else {
                    chipListService.add(item);
                    chipsAdapterForSignup.notifyDataSetChanged();
                }*/

                autoTxtService.setText("");
                /*CategoriesPojoItem pojoItem1 = (CategoriesPojoItem) parent.getItemAtPosition(position);
                categoryId = pojoItem1.getServiceId();*/
            }
        });

        apiCallForGetServicesList();

        if (isFromSocial) {
            linlayPhone.setVisibility(View.VISIBLE);
            linlayAddressLocation.setVisibility(View.VISIBLE);
            linlayGender.setVisibility(View.VISIBLE);

            if (!prefsUtil.getLat().equalsIgnoreCase("0.0") && !prefsUtil.getLng().equalsIgnoreCase("0.0")) {
                new GeoCoderHelper(SignupProfessionalNewActivity.this, Double.parseDouble(prefsUtil.getLat()), Double.parseDouble(prefsUtil.getLng()), new GeoCoderHelper.onGetAddress() {
                    public void onSuccess(String address, String city, String country) {
                        strLocation = address;
                        strCity = city;
                        strCountry = country;
                        edtLocation.setText(strLocation);
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(mContext, "can't fetch location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            linlayPhone.setVisibility(View.GONE);
            linlayAddressLocation.setVisibility(View.GONE);
            linlayGender.setVisibility(View.GONE);
        }
    }

    private void apiCallForGetServicesList() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        new ParseJSON(mContext, BaseUrl.getAllCategory, param, value, GetAllServicePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    GetAllServicePojo pojo = (GetAllServicePojo) obj;
                    serviceList.addAll(pojo.getGetAllServicePojoItem());
                    serviceAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == btnSaveAndNext) {
            String strMobile = edtPhoneNo.getText().toString().trim();
            strLocation = edtLocation.getText().toString().trim();
//            String strAddress = edtAddress.getText().toString().trim();
//            String strDob = edtDOB.getText().toString().trim();

            if (isValidAllField(strMobile, strLocation)) {
                if (isFromSocial) {
                    if (!socialType.equals("")) {
                        apiCallForSocialRegistration(sprCode.getSelectedCountryCodeWithPlus(), strMobile);
                    } else {
                        Toast.makeText(mContext, R.string.no_social_type, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    apiCallForSignupStepTwo(strMobile, strLocation, "", "");
                }
            }
        }/* else if (v == edtDOB) {
            openDatePicker();
        }*/ else if (v == txtAddKeyword) {
            String keyword = edtServiceKeyword.getText().toString().trim();
            if (!keyword.equals("")) {
                listKeyword.add(keyword);
                adapterKeywordChip.notifyDataSetChanged();
                edtServiceKeyword.setText("");
            } else {
                Toast.makeText(mContext, R.string.msg_keyword_validation, Toast.LENGTH_SHORT).show();
            }
        } else if (v == txtAddService) {
            String service = autoTxtService.getText().toString().trim();
            if (!service.equals("")) {
                GetAllServicePojoItem item = new GetAllServicePojoItem();
                item.setServiceName(service);

                chipListService.clear();
                chipListService.add(item);
                chipsAdapterForSignup.notifyDataSetChanged();

                /*if (chipListService.size() > 0) {
                    boolean isMatchAny = false;
                    for (int i = 0; i < chipListService.size(); i++) {
                        if (chipListService.get(i).getServiceName().equalsIgnoreCase(item.getServiceName())) {
                            isMatchAny = true;
                            break;
                        }
                    }
                    if (!isMatchAny) {
                        chipListService.add(item);
                        chipsAdapterForSignup.notifyDataSetChanged();
                    }
                } else {
                    chipListService.add(item);
                    chipsAdapterForSignup.notifyDataSetChanged();
                }*/

                autoTxtService.setText("");
            } else {
                Toast.makeText(mContext, R.string.msg_service_name, Toast.LENGTH_SHORT).show();
            }
        } else if (v == edtLocation) {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } /*else if (v == edtServiceName) {
            Intent intent = new Intent(mContext, ServiceListActivity.class);
            startActivityForResult(intent, REQ_CODE_SERVICE);
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                edtLocation.setText(place.getAddress());

                latLng = place.getLatLng();

                MyLogs.e("TAG", "Place: " + place.getName() + ", " + place.getAddress() + " " + place.getLatLng().latitude);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == REQ_CODE_FINGER && resultCode == Activity.RESULT_OK) {
//            gotoDashboard(true);
        } else if (requestCode == REQ_CODE_SERVICE && resultCode == Activity.RESULT_OK) {

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

    public boolean isValidAllField(String strMobile, String strLocation) {

        if (!isFromSocial) {
            if (edtCompanyName.getText().toString().trim().equals("")) {
                Toast.makeText(mContext, R.string.msg_company_validation, Toast.LENGTH_SHORT).show();
                return false;
            } /*else if (strLocation.equals("")) {
                Toast.makeText(mContext, R.string.msg_address, Toast.LENGTH_SHORT).show();
                return false;
            } */ else if (chipListService.size() == 0) {
                Toast.makeText(mContext, R.string.msg_profession_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            if (edtCompanyName.getText().toString().trim().equals("")) {
                Toast.makeText(mContext, R.string.msg_company_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else if (strMobile.equals("")) {
                Toast.makeText(mContext, R.string.msg_phone_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else if (strLocation.equals("")) {
                Toast.makeText(mContext, R.string.msg_address, Toast.LENGTH_SHORT).show();
                return false;
            } else if (sprGender.getSelectedItemPosition() == 0) {
                Toast.makeText(mContext, R.string.msg_gender_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else if (chipListService.size() == 0) {
                Toast.makeText(mContext, R.string.msg_profession_validation, Toast.LENGTH_SHORT).show();
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

        param.add("company_name");
        value.add(edtCompanyName.getText().toString().trim());

       /* param.add("address");
        value.add(strLocation); // Client committed address and location both are the same.

        param.add("location");
        value.add(strLocation);

        param.add("city");
        value.add(strCity);

        param.add("country");
        value.add(strCountry);

        param.add("lat");
        value.add(prefsUtil.getLat());
//        value.add(String.valueOf(latLng.latitude));

        param.add("lng");
        value.add(prefsUtil.getLng());*/

        param.add("services_name");
        value.add(getServicesNameCommaSaperated());

        param.add("skills");
        value.add(getSkillString());

        /*MyLogs.w("PARAM", param.toString());
        MyLogs.w("VALUE", value.toString());*/

        new ParseJSON(mContext, BaseUrl.signupStepTwo, param, value, SignupNewPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    signupNewPojoItem = ((SignupNewPojo) obj).getSignupNewPojoItem();

                    Intent intent = new Intent(mContext, ProfileDetailsThree.class);
                    intent.putExtra("userId", signupNewPojoItem.getUserId());
                    intent.putExtra("fName", signupNewPojoItem.getFirstName());
                    intent.putExtra("lName", signupNewPojoItem.getLastName());
                    intent.putExtra("gender", signupNewPojoItem.getGender());
                    intent.putExtra("profilePic", signupNewPojoItem.getLogoImage());
                    intent.putExtra("userRole", signupNewPojoItem.getRole());
//                    intent.putExtra("deviceAuthToken", signupNewPojoItem.getDeviceAuthToken());
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apiCallForSocialRegistration(String strCountryCode, String strMobile) {
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

        param.add("company_name");
        value.add(edtCompanyName.getText().toString().trim());

        param.add("image");
        value.add(profilePic);

        param.add("gender");
        value.add((sprGender.getSelectedItemPosition() == 1 ? "male" : "female"));

        param.add("full_mobile_number");
        value.add(strCountryCode + strMobile);

        param.add("address");
        value.add(strLocation); // Client committed address and location both are same.

        param.add("location");
        value.add(strLocation); // Client committed address and location both are same.

        param.add("city");
        value.add(strCity);

        param.add("country");
        value.add(strCountry);

        param.add("lat");
        value.add(prefsUtil.getLat());

        param.add("lng");
        value.add(prefsUtil.getLng());

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

        param.add("services_name");
        value.add(getServicesNameCommaSaperated());

        param.add("skills");
        value.add(getSkillString());

        new ParseJSON(mContext, (socialType.equalsIgnoreCase("f") ? BaseUrl.socialLoginFacebook : BaseUrl.socialLoginGoogle), param, value, SocialResponsePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
//                    signupNewPojoItem = ((SignupNewPojo) obj).getSignupNewPojoItem();
                    SocialResponsePojoItem pojoItem = ((SocialResponsePojo) obj).getSocialResponsePojoItem();
                    prefsUtil.setDeviceAuthToken(pojoItem.getDeviceAuthToken());

                    /** Set map refresh rate */
                    String interal = pojoItem.getSiteSettingPojoItem().getMapRefreshRate();
                    if (!interal.equals(""))
                        prefsUtil.setUpdateInterval(Integer.parseInt(interal));

                    Intent intent = new Intent(mContext, ProfileDetailsThree.class);
                    intent.putExtra("userId", pojoItem.getUserId());
                    intent.putExtra("fName", pojoItem.getFirstName());
                    intent.putExtra("lName", pojoItem.getLastName());
                    intent.putExtra("gender", pojoItem.getGender());
                    intent.putExtra("profilePic", pojoItem.getLogoImage());
                    intent.putExtra("userRole", pojoItem.getRole());
                    intent.putExtra("isFromSocial", true);
                    startActivity(intent);
                    finish();

                    /*openAlertForFingerprintAuth();*/
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
            utility.saveDeviceAuthToken(signupNewPojoItem.getDeviceAuthToken());

            if (userRole.equalsIgnoreCase("Sole Professional")) {
                Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    public String getServiceIds() {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < chipListService.size(); i++) {
            builder.append(chipListService.get(i).getServiceId());
            if (i != (chipListService.size() - 1)) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    public String getServicesNameCommaSaperated() {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < chipListService.size(); i++) {
            builder.append(chipListService.get(i).getServiceName());
            if (i != (chipListService.size() - 1)) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    public String getSkillString() {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < listKeyword.size(); i++) {
            builder.append(listKeyword.get(i));
            if (i != (listKeyword.size() - 1)) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}