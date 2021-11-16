package com.ni.parnasa.activities.customer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.fileupload.FileUploadService;
import com.ni.parnasa.fileupload.FileUtils;
import com.ni.parnasa.fileupload.ServiceGenerator;
import com.ni.parnasa.pojos.AllServicesPojo;
import com.ni.parnasa.pojos.UploadImagePojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FingerPrintPrefUtil;
import com.ni.parnasa.utils.GeoCoderHelper;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.ni.parnasa.utils.SaveDataUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.loader.content.CursorLoader;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    private PrefsUtil prefsUtil;
    private Activity mactivity;

    //    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    //    private DatabaseReference mFirebaseDatabase;
//    private FirebaseFirestore db;
//    private StorageReference storageReference;
    private Uri filePath, camuri;

    private String st_email, st_first_name, st_last_name, st_gender, st_name, st_phnumber, st_password;
    private String country_code, what, st_usercode, ToadyDate, Gender = "", st_Role;
    private String userpicImageName = "", userpicImageUrl = "", fcmToken = "";

    private int REQUEST_CAMERA = 200;
    private int REQUEST_CAMERA_CODE = 11;
    private int REQUEST_CAMERA_CODE_SECOND = 18;
    private int SELECT_FILE = 201;
    private int SELECT_FILE_CODE = 154;
    private int STORAGE_PERMISSION_CODE = 23;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    private Context mContext;
    //    private EditText ;
    private TextView txtCompanyName;
    private ProgressBar progressBar;
    private ImageView img_back, img_profile;
    private EditText edt_email, edt_pass, edt_first_name, edt_last_name, edt_phone_no, edtAddressLocation; // edtServiceKeyword;
    private RadioGroup myRadioGroup;
    private RadioButton male, female;
    private Button btn_submit;
    private CountryCodePicker spin_code;
    private CheckBox check_term;
    private TextView txt_worker, txt_company;
    private LinearLayout len_term;
    private ProgressDialog progressDialog;

    private final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private ArrayAdapter<String> serviceAdapter;
    private ArrayList<String> serviceList;
    private GoogleApiClient mGoogleApiClient;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 54;
    private String CITY = "", COUNTRY = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mContext = SignUpActivity.this;
        mactivity = SignUpActivity.this;

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                fcmToken = task.getResult().getToken();
            }
        });

        what = getIntent().getStringExtra("what");
        MyLogs.w("TAG", "onCreate WHAT :" + what);

        progressDialog = new ProgressDialog(mContext);
//        firebaseAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();

        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ToadyDate = df.format(c);
        prefsUtil = new PrefsUtil(mactivity);

        init();

        if (!prefsUtil.GetUserType().equals("customer")) {
//            apiCallForGetServicesList();
        }

        edtAddressLocation.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == edtAddressLocation) {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } else if (v == btn_submit) {
            if (prefsUtil.GetUserType().equalsIgnoreCase("Customer")) {
                st_Role = "Customer";
            } else if (prefsUtil.GetUserType().equalsIgnoreCase("Sole Professional")) {
                if (prefsUtil.GetprofessionaType().equalsIgnoreCase("company")) {
                    st_Role = "Sole Professional";
                } else if (prefsUtil.GetprofessionaType().equalsIgnoreCase("worker")) {
                    st_Role = "Agency";
                }
            }
            validationChecker();
        } else if (v == img_back) {
            finish();
        }
    }

    private void init() {

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_worker = (TextView) findViewById(R.id.txt_worker);
        txt_company = (TextView) findViewById(R.id.txt_company);
        len_term = (LinearLayout) findViewById(R.id.len_term);

        if (what.equals("worker")) {
            txt_worker.setVisibility(View.GONE);
            len_term.setVisibility(View.VISIBLE);
        } else if (what.equals("company")) {
            txt_company.setVisibility(View.VISIBLE);
            txt_worker.setVisibility(View.VISIBLE);
        }
        spin_code = (CountryCodePicker) findViewById(R.id.spin_code);
        check_term = (CheckBox) findViewById(R.id.check_term);
        myRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_profile = (ImageView) findViewById(R.id.im_profile);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_pass = (EditText) findViewById(R.id.edt_pass);
        edt_last_name = (EditText) findViewById(R.id.edt_last_name);
        edt_first_name = (EditText) findViewById(R.id.edt_first_name);
        edt_phone_no = (EditText) findViewById(R.id.edt_phone_no);
        edtAddressLocation = (EditText) findViewById(R.id.edtAddressLocation);
        RadioGroup rg = (RadioGroup) findViewById(R.id.myRadioGroup);

//        edtCompanyName = findViewById(R.id.edtCompanyName);
//        autoTxtAddres = findViewById(R.id.autoTxtAddres);
        txtCompanyName = findViewById(R.id.txtCompanyName);
//        txtServiceName = findViewById(R.id.txtServiceName);
//        txtServiceKeyword = findViewById(R.id.txtServiceKeyword);
//        autoTxtService = findViewById(R.id.autoTxtService);
//        edtServiceKeyword = findViewById(R.id.edtServiceKeyword);

        if (!prefsUtil.GetUserType().equals("customer")) {
//            txtCompanyName.setVisibility(View.GONE);
//            edtCompanyName.setVisibility(View.GONE);

            /*autoTxtService.setThreshold(2);
            serviceList = new ArrayList<>();
            serviceAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, serviceList);
            autoTxtService.setAdapter(serviceAdapter);*/

        } else {
//            txtServiceName.setVisibility(View.GONE);
//            autoTxtService.setVisibility(View.GONE);
            /*txtServiceKeyword.setVisibility(View.GONE);
            edtServiceKeyword.setVisibility(View.GONE);*/
        }


        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.female:
                        Gender = "female";
                        // do operations specific to this selection
                        break;
                    case R.id.male:
                        Gender = "male";
                        // do operations specific to this selection
                        break;
                }
            }
        });


        spin_code.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country_code = spin_code.getSelectedCountryCode();
            }
        });

        spin_code.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {
                //your code
                TextView title =(TextView)  dialog.findViewById(R.id.textView_title);
                title.setText("Select Country or Region");
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
                //your code
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {
                //your code
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (lastLocation != null) {
            new GeoCoderHelper(SignUpActivity.this, lastLocation.getLatitude(), lastLocation.getLongitude(), new GeoCoderHelper.onGetAddress() {
                public void onSuccess(String address, String city, String country) {
//                    strLocation = address;
//                    strCity = city;
//                    strCountry = country;
//                    edtLocation.setText(strLocation);
                    edtAddressLocation.setText(address);
                }

                @Override
                public void onFail() {
                    Toast.makeText(mContext, "can't fetch location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(mactivity, "Unable to fetch address from current location", Toast.LENGTH_SHORT).show();
        }
    }

    private void apiCallForGetServicesList() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        /*param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());*/

        new ParseJSON(mContext, BaseUrl.urlAllServices, param, value, AllServicesPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    AllServicesPojo pojo = (AllServicesPojo) obj;
                    serviceList.addAll(pojo.getData());
                    serviceAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mactivity, obj.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void showFileChooser() {

        final CharSequence[] items = {getString(R.string.from_camera), getString(R.string.camera_roll), getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.from_camera))) {

                    checkManualPermission(true);

                } else if (items[item].equals(getString(R.string.camera_roll))) {

                    checkManualPermission(false);

                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void checkManualPermission(boolean isCamera) {
        if (isCamera) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_CODE_SECOND);
                } else {
                    openCamera();
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_FILE_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), SELECT_FILE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_CODE) {

            //If permission is granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkManualPermission(true);
                //Displaying a toast
//                Toast.makeText(mContext, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(mContext, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CAMERA_CODE_SECOND) {

            //If permission is granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkManualPermission(true);
                //Displaying a toast
//                Toast.makeText(mContext, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(mContext, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == SELECT_FILE_CODE) {

            //If permission is granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkManualPermission(false);
                //Displaying a toast
//                Toast.makeText(mContext, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(mContext, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openCamera() {
        String fileName = "new-photo-name.jpg";
        //create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
        camuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, camuri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                edtAddressLocation.setText(place.getAddress());

                Log.e("TAG", "Place: " + place.getName() + ", " + place.getAddress() + " " + place.getLatLng().latitude);

                new GeoCoderHelper(SignUpActivity.this, place.getLatLng().latitude, place.getLatLng().longitude, new GeoCoderHelper.onGetAddress() {
                    @Override
                    public void onSuccess(String address, String city, String country) {
                        CITY = city;
                        COUNTRY = country;
                    }

                    @Override
                    public void onFail() {
                        MyLogs.e("TAG", "Failed to get address from location");
                    }
                });

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Uri selectedImageUri = camuri;
                filePath = selectedImageUri;
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(mContext, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;

                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

                // uploadFile(camuri);
                // Log.v("pathshiuli", String.valueOf(imgeUrl));
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
                circularBitmapDrawable.setCircular(true);
                img_profile.setImageDrawable(circularBitmapDrawable);

//                img_profile.setImageBitmap(bitmap);
                // upload_txt.setVisibility(View.GONE);
                // uploadImage();
                //  ShareDialog(bitmap);
                apiCallForUploadImage(camuri);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                filePath = selectedImageUri;
                //picUri=selectedImageUri;
                MyLogs.e("onActivityResult", "Uri : " + String.valueOf(selectedImageUri));
                // uploadFile(selectedImageUri);
                // uploadFile(selectedImageUri);

                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(mContext, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;

                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                // select_image.setImageBitmap(bitmap);
                //uploadImage();

                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
                circularBitmapDrawable.setCircular(true);
                img_profile.setImageDrawable(circularBitmapDrawable);

//                img_profile.setImageBitmap(bitmap);
                //upload_txt.setVisibility(View.GONE);
                //uploadFile(selectedImageUri);
                // ShareDialog(bitmap);
                apiCallForUploadImage(selectedImageUri);
            }
        }
    }

    private void apiCallForUploadImage(Uri fileUri) {
        // create upload service client
        final ProgressDialog loading = ProgressDialog.show(mContext, getString(R.string.upload_photo), getString(R.string.please_wait), false, false);

        MyLogs.e("TAG", "URL : " + BaseUrl.URL + "Upload_file/profileImage");

        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(mContext, fileUri);

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

                    MyLogs.e("TAG", "postJSONRequest response.body : " + pojo.toString());

                    if (pojo.getStatus().equalsIgnoreCase("yes")) {
                        userpicImageName = pojo.getUploadImagePojoItem().getFileName();
                        userpicImageUrl = pojo.getUploadImagePojoItem().getFileUrl();
                        MyLogs.e("TAG", "Uploaded image name : " + userpicImageName);
                        MyLogs.e("TAG", "Uploaded image url : " + userpicImageUrl);
                    } else {
                        Toast.makeText(SignUpActivity.this, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } else {
                    MyLogs.e("Upload", "Response: error");
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UploadImagePojo> call, Throwable t) {
                //  Log.e("Upload error:", t.getMessage());
                //Toast.makeText(getApplicationContext(),"Server time out",Toast.LENGTH_SHORT).show();
                //select_image.setImageResource(R.drawable.camera1);

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //final AlertDialog alert = builder.create();

                builder.setTitle(R.string.app_name);
//                builder.setIcon(R.drawable.pickme_serviceapp_logo1);
                builder.setMessage(R.string.service_time_out);
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                final AlertDialog alert = builder.create();
                alert.show();
                loading.dismiss();
            }
        });
    }

    private void validationChecker() {
        st_email = edt_email.getText().toString().trim();
        st_first_name = edt_first_name.getText().toString().trim();
        st_last_name = edt_last_name.getText().toString().trim();
        st_phnumber = edt_phone_no.getText().toString().trim();
        st_password = edt_pass.getText().toString().trim();
        int random = (int) (Math.random() * 9000) + 1000;

        if (TextUtils.isEmpty(st_email)) {
            Toast.makeText(mContext, R.string.msg_email_validation, Toast.LENGTH_LONG).show();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(st_email).matches()) {
            Toast.makeText(mContext, R.string.msg_email_invalid, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(Gender)) {
            Toast.makeText(mContext, R.string.msg_gender_validation, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(st_first_name)) {
            Toast.makeText(mContext, R.string.msg_fname_validation, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(st_last_name)) {
            Toast.makeText(mContext, R.string.msg_lname_validation, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(st_phnumber)) {
            Toast.makeText(mContext, R.string.msg_phone_validation, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(st_password)) {
            Toast.makeText(mContext, R.string.msg_password_validation, Toast.LENGTH_LONG).show();
            return;
        } else if (st_password.length() < 8) {
            Toast.makeText(mContext, R.string.msg_password_minimum_validation, Toast.LENGTH_LONG).show();
            return;
        } else if (edtAddressLocation.getText().toString().trim().equals("")) {
            Toast.makeText(mactivity, R.string.msg_address_validation, Toast.LENGTH_SHORT).show();
        }
        /*else if (autoTxtService.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_service_name, Toast.LENGTH_LONG).show();
        } else if (autoTxtAddres.getText().toString().trim().equals("")) {
            Toast.makeText(mContext, R.string.msg_address, Toast.LENGTH_LONG).show();
        }
        */
        else {
            String Fname = String.valueOf(st_first_name.charAt(0));
            String Lname = String.valueOf(st_last_name.charAt(0));
            st_usercode = Fname.toUpperCase() + Lname.toUpperCase() + random + spin_code.getSelectedCountryNameCode().toUpperCase();

            new apiCallForSignUp().execute();
        }
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

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(mactivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(mactivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    public class apiCallForSignUp extends AsyncTask<String, Void, String> {

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
                request_main.put("first_name", st_first_name);
                request_main.put("last_name", st_last_name);
                request_main.put("company_email", st_email);
                request_main.put("full_mobile_number", spin_code.getSelectedCountryCodeWithPlus() + st_phnumber);
                request_main.put("gender", Gender);
                request_main.put("address", edtAddressLocation.getText().toString().trim());
                request_main.put("location", edtAddressLocation.getText().toString().trim());
                request_main.put("city", CITY);
                request_main.put("country", COUNTRY);
                request_main.put("lat", prefsUtil.getLat());
                request_main.put("lng", prefsUtil.getLng());
                request_main.put("role", st_Role);
                request_main.put("password", st_password);
                request_main.put("user_code", st_usercode);
                request_main.put("image", userpicImageName);
                request_main.put("device_type", BaseUrl.deviceType);
                request_main.put("communication_token", fcmToken);

                MyLogs.e("URL", BaseUrl.URL + "User/signup");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());
                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "User/signup")
                        .post(body)
                        .build();

                okhttp3.Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Response1 = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return Response1;

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
                    MyLogs.e("TAG", "Response " + jsonObject);

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("Yes")) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        String id = data.getString("id");
//                        String role = data.getString("role");

                        prefsUtil.Set_UserID(id);
                        try {
                            prefsUtil.setDeviceAuthToken(data.getString("device_auth_token"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /** For new way of finger print auth */
                        FingerPrintPrefUtil.with(mContext).write("email", st_email);
                        FingerPrintPrefUtil.with(mContext).write("pwd", st_password);
                        /*************/

                        if (st_Role.equalsIgnoreCase("Customer")) {

                            /** For new way finger print auth
                             * default isCompletedSignup values false but after completion of signup process it is true
                             * and we can check isCompletedSignup flag in login activity if it is true then display emailId prefix in edittext otherwise not
                             * */
                            FingerPrintPrefUtil.with(mContext).write(BaseUrl.isCompletedSignup, true);

                            String interal = data.getJSONObject("site_settings").getString("map_refresh_rate");
                            if (!interal.equals(""))
                                prefsUtil.setUpdateInterval(Integer.parseInt(interal));

                            SaveDataUtility utility = new SaveDataUtility(mContext);
                            utility.saveData(data.getString("id"),
                                    st_first_name,
                                    st_last_name,
                                    st_gender,
                                    userpicImageUrl,
                                    st_Role,
                                    false,
                                    true);

                            Intent intent = new Intent(mContext, CustomerHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.app_name);
                            builder.setMessage(message);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mContext, VerificationActivity.class);
                                    intent.putExtra("what", what);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            builder.create().show();
                        }
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        //final AlertDialog alert = builder.create();
                        builder.setTitle(R.string.app_name);
                        //builder.setIcon(R.drawable.);
                        builder.setMessage(message);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
