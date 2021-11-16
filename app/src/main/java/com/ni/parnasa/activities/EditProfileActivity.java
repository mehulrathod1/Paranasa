package com.ni.parnasa.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.adapters.ChipsAdapter;
import com.ni.parnasa.adapters.ChipsAdapterForSignup;
import com.ni.parnasa.fileupload.FileUploadService;
import com.ni.parnasa.fileupload.FileUtils;
import com.ni.parnasa.fileupload.ServiceGenerator;
import com.ni.parnasa.models.ProfilePojo;
import com.ni.parnasa.models.ProfilePojoItem;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.UploadImagePojo;
import com.ni.parnasa.tmpPojos.GetAllServicePojo;
import com.ni.parnasa.tmpPojos.GetAllServicePojoItem;
import com.ni.parnasa.utils.ApplicationController;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.ImgUtils;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
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
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private ImageView imgBack, imgProfile, imgUpload, imgRemove;
    private EditText edtFirstName, edtLastName, edtEmail, edtPhoneNo, edtDOB, edtLocation, edtServiceKeyword, edtCompanyName;
    private AutoCompleteTextView autoTxtService;
    private CountryCodePicker sprCode;
    private Spinner sprGender;
    private Button btnSave;
    private RecyclerView rvServices, rvKeyword;
    private TextView txtAddService, txtAddKeyword;
    private LinearLayout linlayProfessional, linlayCompany;

    private ArrayAdapter<GetAllServicePojoItem> serviceAdapter;
    private ArrayList<GetAllServicePojoItem> serviceList;
    private ArrayList<GetAllServicePojoItem> chipListService;
    private ArrayList<String> listKeyword;
    private ChipsAdapterForSignup chipsAdapterForSignup;
    private ChipsAdapter adapterKeywordChip;

    private int REQ_CODE_PERMISSION = 14, REQ_CODE_GALLERY = 15;
    private boolean isFromProfile;
    private int DAY, MONTH, YEAR;
    private String userProfileImageName = "", userProfileUrl = "", ROLE = "";
    private String CITY = "", COUNTRY = "";

    private final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng = null;
    private Calendar calTest;

    private double lat = 0.0, lng = 0.0;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 75;
    private int MAX_IMAGE_SIZE = 2; // 2 MB image size valid

    private Date dateGloble = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mContext = EditProfileActivity.this;

        init();

        Intent intent = getIntent();
        isFromProfile = intent.getBooleanExtra("isFromProfile", false);

        if (isFromProfile) {

            manageFromIntentDate();

            String uName = intent.getStringExtra("userName");
            String[] struname = uName.split(" ");
            edtFirstName.setText(struname[0]);
            edtLastName.setText(struname[1]);
            edtEmail.setText(intent.getStringExtra("email"));
            edtPhoneNo.setText(intent.getStringExtra("phoneNo"));
            edtLocation.setText(intent.getStringExtra("location"));
            CITY = intent.getStringExtra("city");
            COUNTRY = intent.getStringExtra("country");
            edtCompanyName.setText(intent.getStringExtra("company"));

            String bDate = intent.getStringExtra("bdate");
            if (!bDate.equalsIgnoreCase("n/a") && !bDate.equals("0000-00-00"))
                edtDOB.setText(bDate);

            if (intent.getStringExtra("gender").equalsIgnoreCase("male")) {
                sprGender.setSelection(1);
            } else if (intent.getStringExtra("gender").equalsIgnoreCase("female")) {
                sprGender.setSelection(2);
            } else {
                sprGender.setSelection(0);
            }

            lat = intent.getDoubleExtra("lat", 0.0);
            lng = intent.getDoubleExtra("lng", 0.0);

            if (!intent.getStringExtra("address").equals("")) {
                LatLng latLng1 = getLocationFromAddress(intent.getStringExtra("address"));
                if (latLng1 != null) {
                    lat = latLng1.latitude;
                    lng = latLng1.longitude;
                }
            }

            if (!ROLE.equalsIgnoreCase("Customer")) {

                try {
                    String slpitStringService = getIntent().getStringExtra("service");
                    if (!slpitStringService.equals("")) {
                        String service[] = slpitStringService.split(",");
                        for (int i = 0; i < service.length; i++) {
                            GetAllServicePojoItem item = new GetAllServicePojoItem();
                            item.setServiceName(service[i]);
                            chipListService.add(item);
                        }
                        chipsAdapterForSignup.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    String slpitStringKeyword = getIntent().getStringExtra("keyword");

                    if (!slpitStringKeyword.equals("")) {
                        String keyword[] = slpitStringKeyword.split(",");
                        for (int i = 0; i < keyword.length; i++) {
                            listKeyword.add(keyword[i]);
                        }
                        adapterKeywordChip.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            apiCallForGetProfile();
        }
    }

    private void manageFromIntentDate() {
        try {
            String tmpUrl = getIntent().getStringExtra("imagePath");

//            MyLogs.w("ZZZ", "manage " + tmpUrl);

            String imgeNAme = findNameOfImageFromPath(tmpUrl);

            if (!imgeNAme.equalsIgnoreCase("female-icon.jpeg") && !imgeNAme.equalsIgnoreCase("male-icon.jpeg")) {
                imgRemove.setVisibility(View.VISIBLE);
                userProfileUrl = tmpUrl;
                userProfileImageName = imgeNAme;
            } else {
                imgRemove.setVisibility(View.GONE);
            }

            Glide.with(mContext).asBitmap()
                    .load(tmpUrl)
                    .into(imgProfile);

            dateGloble = ((ApplicationController) getApplication()).date;

            MyLogs.e("ZZZZZz", "DATE " + dateGloble);

            if (dateGloble != null) {

                Calendar tmpCal = Calendar.getInstance();
                tmpCal.setTime(dateGloble);
                YEAR = tmpCal.get(Calendar.YEAR);
                MONTH = tmpCal.get(Calendar.MONTH);
                DAY = tmpCal.get(Calendar.DAY_OF_MONTH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String findNameOfImageFromPath(String imagePath) {
        return imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
    }

    private void init() {


        Places.initialize(mContext, getString(R.string.google_maps_key));

        prefsUtil = new PrefsUtil(mContext);
        ROLE = prefsUtil.getRole();

        imgBack = findViewById(R.id.imgBack);
        imgProfile = findViewById(R.id.imgProfile);
        imgUpload = findViewById(R.id.imgUpload);
        imgRemove = findViewById(R.id.imgRemove);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        sprCode = findViewById(R.id.sprCode);
        edtPhoneNo = findViewById(R.id.edtPhoneNo);
        edtLocation = findViewById(R.id.edtLocation);
        edtCompanyName = findViewById(R.id.edtCompanyName);
//        edtAddress = findViewById(R.id.edtAddress);
        edtDOB = findViewById(R.id.edtDOB);
        sprGender = findViewById(R.id.sprGender);
        btnSave = findViewById(R.id.btnSave);

        autoTxtService = findViewById(R.id.autoTxtService);
        edtServiceKeyword = findViewById(R.id.edtServiceKeyword);
        txtAddService = findViewById(R.id.txtAddService);
        txtAddKeyword = findViewById(R.id.txtAddKeyword);
        rvServices = findViewById(R.id.rvServices);
        rvKeyword = findViewById(R.id.rvKeyword);
        linlayProfessional = findViewById(R.id.linlayProfessional);
        linlayCompany = findViewById(R.id.linlayCompany);

        calTest = Calendar.getInstance();
        DAY = calTest.get(Calendar.DAY_OF_MONTH);
        MONTH = calTest.get(Calendar.MONTH);
        YEAR = calTest.get(Calendar.YEAR) - 18;

        edtDOB.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgUpload.setOnClickListener(this);
        imgRemove.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        edtLocation.setOnClickListener(this);
        txtAddService.setOnClickListener(this);
        txtAddKeyword.setOnClickListener(this);

        if (!ROLE.equalsIgnoreCase("Customer")) {

            linlayProfessional.setVisibility(View.VISIBLE);
            linlayCompany.setVisibility(View.VISIBLE);

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
                }
            });

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

            apiCallForGetServicesList();

        } else {
            linlayProfessional.setVisibility(View.GONE);
            linlayCompany.setVisibility(View.GONE);
        }

        setupAdMob();
    }


    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == imgUpload) {
            checkManualPermission();
        } else if (v == imgRemove) {
            userProfileUrl = "";
            userProfileImageName = "";
            if (sprGender.getSelectedItemPosition() == 1) {
                imgProfile.setImageResource(R.drawable.img_default_male);
            } else if (sprGender.getSelectedItemPosition() == 2) {
                imgProfile.setImageResource(R.drawable.img_default_female);
            }

        } else if (v == edtDOB) {
            openDatePicker();
        } else if (v == edtLocation) {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } else if (v == btnSave) {
            if (isValidField()) {
                apiCallForUpdateProfile(true);
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
        } else if (v == txtAddKeyword) {
            String keyword = edtServiceKeyword.getText().toString().trim();
            if (!keyword.equals("")) {
                listKeyword.add(keyword);
                adapterKeywordChip.notifyDataSetChanged();
                edtServiceKeyword.setText("");
            } else {
                Toast.makeText(mContext, R.string.msg_keyword_validation, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calTest.set(year, month, dayOfMonth);

                YEAR = year;
                MONTH = month;
                DAY = dayOfMonth;
                edtDOB.setText(formater.format(calTest.getTime()));
            }
        }, YEAR, MONTH, DAY);

        Calendar calTmp = Calendar.getInstance();
        calTmp.set(Calendar.YEAR, calTmp.get(Calendar.YEAR) - 18);
        datePickerDialog.getDatePicker().setMaxDate(calTmp.getTimeInMillis() + 1000);
        datePickerDialog.show();
    }

    public boolean isValidField() {
        if (edtFirstName.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_fname_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtLastName.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_lname_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtPhoneNo.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_phone_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtLocation.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_location_validation, Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (edtAddress.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_address_validation, Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtDOB.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_dob_validation, Toast.LENGTH_SHORT).show();
            return false;
        } */else if (sprGender.getSelectedItemPosition() == 0) {
            Toast.makeText(mContext, R.string.select_gender, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!ROLE.equalsIgnoreCase("Customer")) {
            if (edtCompanyName.getText().toString().trim().equals("")) {
                Toast.makeText(mContext, R.string.msg_company_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else if (chipListService.size() == 0) {
                Toast.makeText(mContext, R.string.msg_profession_validation, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void checkManualPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQ_CODE_GALLERY);
        }
    }

    private boolean isImageSizeValid(String path) {

        File file = new File(path);

        long mOriginSizeFile = file.length();
        long fileSizeInKB = mOriginSizeFile / 1024;


        if (fileSizeInKB > 1024) {
            long fileSizeInMB = fileSizeInKB / 1024;
            long tmp = fileSizeInKB % 1024;
            Log.e("TAG", "IMAGE SIZE " + String.format("%s %s", fileSizeInMB, "MB"));
            if (fileSizeInMB > MAX_IMAGE_SIZE) {
                return false;
            }
            return true;
        } else {
            Log.e("TAG", "IMAGE SIZE " + String.format("%s %s", fileSizeInKB, "KB"));
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GALLERY && data != null) {

            MyLogs.e("TAG", "Uri :: " + data.getData());
            String realPath = FileUtils.getPath(mContext, data.getData());
            MyLogs.e("TAG", realPath);

            if (isImageSizeValid(realPath)) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                ParcelFileDescriptor parcelFileDescriptor;
                try {
                    parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();
                    int height = image.getHeight(), width = image.getWidth();
                    if (height > 1280 && width > 960) {
                        Bitmap imgbitmap = BitmapFactory.decodeFile(realPath, options);
                        ((ApplicationController) getApplication()).img = imgbitmap;
                        Intent i = new Intent(mContext, CropActivity.class);
                        startActivityForResult(i, 555);
                    } else {
                        ((ApplicationController) getApplication()).img = image;
                        Intent i = new Intent(mContext, CropActivity.class);
                        startActivityForResult(i, 555);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, R.string.invalid_image_size, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == 555 & resultCode == RESULT_OK) {
            Bitmap bmp = ((ApplicationController) getApplication()).cropped;
            imgProfile.setImageBitmap(ImgUtils.createCircleBitmap(bmp));
            String selectedImagePath = savePicture(bmp, "profile_pic");
            if (!selectedImagePath.equals("")) {
                apiCallForUploadImage(selectedImagePath);
            }

        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                edtLocation.setText(place.getAddress());
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;

                Log.e("TAG", "Place: " + place.getName() + ", " + place.getAddress());

                new GeoCoderHelper(EditProfileActivity.this, lat, lng, new GeoCoderHelper.onGetAddress() {
                    @Override
                    public void onSuccess(String address, String city, String country) {
                        CITY = city;
                        COUNTRY = country;
                    }

                    @Override
                    public void onFail() {
                        MyLogs.e("TAG", "Failed to find address from location");
                    }
                });

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public String savePicture(Bitmap bitmap, String imageName) {
        FileOutputStream out = null;
        String selectedImagePath = "";

        try {
            if (createDir(BaseUrl.dirPath)) {
                out = new FileOutputStream(BaseUrl.dirPath + File.separator + imageName + ".jpg");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                selectedImagePath = BaseUrl.dirPath + File.separator + imageName + ".jpg";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return selectedImagePath;
    }

    private Boolean createDir(String path) {
        File folder = new File(path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkManualPermission();
            } else {
                Toast.makeText(mContext, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
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

    private void apiCallForUploadImage(String realPath) {
        final ProgressDialog loading = ProgressDialog.show(mContext, "Uploading", "Please wait...", false, false);

        MyLogs.e("TAG", "URL : " + BaseUrl.URL + "Upload_file/profileImage");
        MyLogs.e("TAG", "Image PATH  : " + realPath);

        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        // use the FileUtils to get the actual file by uri
//        File file = FileUtils.getFile(mContext, fileUri);
        File file = new File(realPath);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
        final String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<UploadImagePojo> call = service.upload(description, body);
        call.enqueue(new Callback<UploadImagePojo>() {
            @Override
            public void onResponse(Call<UploadImagePojo> call, retrofit2.Response<UploadImagePojo> response) {
                if (response.isSuccessful()) {

                    UploadImagePojo pojo = response.body();

                    MyLogs.e("TAG", "Image upload response : " + pojo.toString());

                    if (pojo.getStatus().equalsIgnoreCase("yes")) {
                        userProfileImageName = pojo.getUploadImagePojoItem().getFileName();
                        userProfileUrl = pojo.getUploadImagePojoItem().getFileUrl();

                        Glide.with(mContext).asBitmap()
                                .load(userProfileUrl)
                                .into(new BitmapImageViewTarget(imgProfile) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        imgProfile.setImageDrawable(circularBitmapDrawable);
                                    }
                                });

                        apiCallForUpdateProfile(false);

                    } else {
                        Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } else {
                    MyLogs.e("Upload", "Response: error");
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UploadImagePojo> call, Throwable t) {
                loading.dismiss();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setTitle(R.string.app_name);
                builder.setMessage(R.string.service_time_out);
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                final android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void apiCallForGetProfile() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        new ParseJSON(mContext, BaseUrl.getUserById, param, value, ProfilePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ProfilePojoItem pojoItem = ((ProfilePojo) obj).getProfilePojoItem();

                    edtFirstName.setText(pojoItem.getFirstName());
                    edtLastName.setText(pojoItem.getLastName());
                    edtEmail.setText(pojoItem.getCompanyEmail());
                    edtPhoneNo.setText(pojoItem.getMobileNumber());
                    edtLocation.setText(pojoItem.getLocation());
                    if (!prefsUtil.getRole().equalsIgnoreCase("customer")) {
                        edtCompanyName.setText(pojoItem.getCompanyName());
                    }

                    CITY = pojoItem.getCity();
                    COUNTRY = pojoItem.getCountry();

                    lat = (!pojoItem.getLat().equals("") ? Double.parseDouble(pojoItem.getLat()) : 0.0);
                    lng = (!pojoItem.getLng().equals("") ? Double.parseDouble(pojoItem.getLng()) : 0.0);

                    if (pojoItem.getGender().equalsIgnoreCase("male")) {
                        sprGender.setSelection(1);
                    } else if (pojoItem.getGender().equalsIgnoreCase("female")) {
                        sprGender.setSelection(2);
                    } else {
                        sprGender.setSelection(0);
                    }

                    if (!ROLE.equalsIgnoreCase("Customer")) {
                        try {
                            if (!pojoItem.getService().equals("")) {
                                String service[] = pojoItem.getService().split(",");
                                for (int i = 0; i < service.length; i++) {
                                    GetAllServicePojoItem item = new GetAllServicePojoItem();
                                    item.setServiceName(service[i]);
                                    chipListService.add(item);
                                }
                                chipsAdapterForSignup.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            if (!pojoItem.getKeyword().equals("")) {
                                String keyword[] = pojoItem.getKeyword().split(",");
                                for (int i = 0; i < keyword.length; i++) {
                                    listKeyword.add(keyword[i]);
                                }
                                adapterKeywordChip.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        String tmpUrl = pojoItem.getLogoImage();
                        String imgName = findNameOfImageFromPath(tmpUrl);

                        if (!imgName.equalsIgnoreCase("female-icon.jpeg") && !imgName.equalsIgnoreCase("male-icon.jpeg")) {
                            imgRemove.setVisibility(View.VISIBLE);
                            userProfileUrl = tmpUrl;
                            userProfileImageName = imgName;
                        } else {
                            imgRemove.setVisibility(View.GONE);
                        }

                        Glide.with(mContext).asBitmap()
                                .load(tmpUrl)
                                .into(new BitmapImageViewTarget(imgProfile) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        imgProfile.setImageDrawable(circularBitmapDrawable);
                                    }
                                });

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        String bDate = pojoItem.getBirthdate();
                        if (!bDate.equals("") && !bDate.equals("0000-00-00")) {
                            edtDOB.setText(pojoItem.getBirthdate());
                            dateGloble = simpleDateFormat.parse(pojoItem.getBirthdate());
                        }

                        if (dateGloble != null) {
                            Calendar tmpCal = Calendar.getInstance();
                            tmpCal.setTime(dateGloble);
                            YEAR = tmpCal.get(Calendar.YEAR);
                            MONTH = tmpCal.get(Calendar.MONTH);
                            DAY = tmpCal.get(Calendar.DAY_OF_MONTH);
                        }

                        if (!pojoItem.getAddress().equalsIgnoreCase("")) {

                            LatLng latLng1 = getLocationFromAddress(pojoItem.getAddress());

                            if (latLng1 != null) {
                                lat = latLng1.latitude;
                                lng = latLng1.longitude;
                            } /*else {
                                Toast.makeText(mContext, "lalLongnull", Toast.LENGTH_SHORT).show();
                            }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apiCallForUpdateProfile(final boolean isFromSaveButton) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("first_name");
        value.add(edtFirstName.getText().toString().trim());

        param.add("last_name");
        value.add(edtLastName.getText().toString().trim());

        param.add("mobile_number");
        value.add(edtPhoneNo.getText().toString().trim());

        param.add("gender");
        value.add((sprGender.getSelectedItemPosition() == 1 ? "male" : "female"));

        param.add("birthdate");
        value.add(edtDOB.getText().toString().trim());

        param.add("profile_image");
        value.add(userProfileImageName);

        param.add("location");
        value.add(edtLocation.getText().toString().trim());

        param.add("address");
        value.add(edtLocation.getText().toString().trim()); // This is client commented
//        value.add(edtAddress.getText().toString().trim());

        if (!ROLE.equalsIgnoreCase("Customer")) {
            param.add("service");
            value.add(getServicesNameCommaSaperated());

            param.add("keywords");
            value.add(getSkillString());

            param.add("company_name");
            value.add(edtCompanyName.getText().toString().trim());
        }

        param.add("city");
        value.add(CITY);

        param.add("country");
        value.add(COUNTRY);

        param.add("lat");
        value.add(String.valueOf(lat));
//        value.add((latLng != null ? String.valueOf(latLng.latitude) : "0.0"));

        param.add("lng");
        value.add(String.valueOf(lng));
//        value.add((latLng != null ? String.valueOf(latLng.longitude) : "0.0"));

        /*MyLogs.e("TAG", "ZZZZ " + userProfileUrl);
        MyLogs.e("TAG", "PARAM " + param);
        MyLogs.e("TAG", "VALUE " + value);*/

        new ParseJSON(mContext, BaseUrl.editProfile, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();

                    if (userProfileImageName.equals("")) {
                        if (sprGender.getSelectedItemPosition() == 1) {
                            imgProfile.setImageResource(R.drawable.img_default_male);
                            userProfileUrl = BaseUrl.defaultMale;
                        } else if (sprGender.getSelectedItemPosition() == 2) {
                            imgProfile.setImageResource(R.drawable.img_default_female);
                            userProfileUrl = BaseUrl.defaultFemale;
                        }
                    }

                    prefsUtil.setUserPic(userProfileUrl);

                    if (isFromSaveButton)
                        onBackPressed();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(mContext);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyLogs.e("TAG", "Connection failed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        mAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
//        mAdapter.setGoogleApiClient(null);
    }

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }
}
