package com.ni.parnasa.activities.customer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.FingerprintAuthActivity;
import com.ni.parnasa.activities.ForgetPasswordActivity;
import com.ni.parnasa.activities.ProfessionalSignupStepFour;
import com.ni.parnasa.activities.SignupCustomerNewActivity;
import com.ni.parnasa.activities.SignupProfessionalNewActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.activities.professional.owncompany.ProfileDetailsThree;
import com.ni.parnasa.models.SocialResponsePojo;
import com.ni.parnasa.models.SocialResponsePojoItem;
import com.ni.parnasa.models.UserByEmailPojo;
import com.ni.parnasa.models.UserByEmailPojoItem;
import com.ni.parnasa.pojos.UserLoginPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FingerPrintPrefUtil;
import com.ni.parnasa.utils.KeyboardUtils;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private Context mContext;
    private CallbackManager callbackmanager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    //        private FirebaseFirestore db;
    //    private FirebaseFirestore mFirestore;
    private PrefsUtil prefsUtil;

    private LinearLayout linlayGoogle, linlayFacebook;
    private ProgressBar progressBar;
    private ImageView img_back;
    private EditText edt_email, edt_pass;
    private TextView forgot_pass;
    private ProgressDialog progressDialog;
    private Button btn_login, btn_signup;

    private static final int RC_SIGN_IN = 9001;
    private double currentLatitude = 0.0, currentLongitude = 0.0;
    private String what, UserId, st_email, st_pass = "", userRole;
    private JSONObject jsonObjectGloble = null;
    private UserLoginPojoItem loginPojo = null;

    public static int MY_PERMISSIONS_REQUEST_LOCATION = 5000;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private int REQ_CODE_FINGER = 84;
    private static final String TAG = "LocationActivity";

    /* For finger print auth */
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManagerCompat.CryptoObject cryptoObject;
    //    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManagerCompat fingerprintManager;
    private KeyguardManager keyguardManager;
    private String fcmToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mContext = LoginActivity.this;

        //checkpremission("location");

        what = getIntent().getStringExtra("what");
        if (what.equalsIgnoreCase("customer")) {
            userRole = "Customer";
        } else if (what.equalsIgnoreCase("worker")) {
            userRole = "Employee";
//            userRole = "Agency";
        } else if (what.equalsIgnoreCase("company")) {
            userRole = "Sole Professional";
        } else {
            userRole = "Customer";
        }

        MyLogs.w("TAG", "ROLE - " + userRole + " WHAT - " + what);

        setupGoogleApiClient();

        defaultSettingsRequest();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        prefsUtil = new PrefsUtil(LoginActivity.this);
        init();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                fcmToken = task.getResult().getToken();
            }
        });

        if (FingerPrintPrefUtil.with(mContext).readBoolean(BaseUrl.isCompletedSignup)) {
            st_email = FingerPrintPrefUtil.with(mContext).readString("email");
            edt_email.setText(st_email);

            processToFingerPrintAuthentication();
        }
    }

    private void init() {

        linlayGoogle = findViewById(R.id.linlayGoogle);
        linlayFacebook = findViewById(R.id.linlayFacebook);
        forgot_pass = findViewById(R.id.forgot_pass);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        img_back = (ImageView) findViewById(R.id.img_back);
        btn_login = (Button) findViewById(R.id.btn_login);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_pass = (EditText) findViewById(R.id.edt_pass);
        ///// edt_pass = (EditText) findViewById(R.id.edt_pass);
        btn_signup = (Button) findViewById(R.id.btn_signup);

        btn_login.setOnClickListener(this);
        linlayFacebook.setOnClickListener(this);
        linlayGoogle.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        forgot_pass.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == btn_login) {
            st_email = edt_email.getText().toString().trim();
            st_pass = edt_pass.getText().toString().trim();
            if (st_email.equals("")) {
                Toast.makeText(LoginActivity.this, getString(R.string.msg_email_validation), Toast.LENGTH_LONG).show();
                return;
            } else if (st_pass.equals("")) {
                Toast.makeText(LoginActivity.this, getString(R.string.msg_password_validation), Toast.LENGTH_LONG).show();
                return;
            } else {
                KeyboardUtils.hideSoftKeyboard(LoginActivity.this);

                new apiCallForLogin(false).execute();
            }
        } else if (view == btn_signup) {
            if (userRole.equalsIgnoreCase("Employee")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://pickmeapp.co/dev_v2/register"));
                startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, SignUpActivity.class);
                intent.putExtra("what", what);
                startActivity(intent);
            }
        } else if (view == forgot_pass) {
            startActivity(new Intent(mContext, ForgetPasswordActivity.class));
        } else if (view == linlayGoogle) {
            if (!userRole.equalsIgnoreCase("Employee")) {
                googleLogin();
            } else {
                Toast.makeText(mContext, R.string.unable_signup, Toast.LENGTH_SHORT).show();
            }
        } else if (view == linlayFacebook) {
            if (!userRole.equalsIgnoreCase("Employee")) {
                facebookLogin();
            } else {
                Toast.makeText(mContext, R.string.unable_signup, Toast.LENGTH_SHORT).show();
            }
        } else if (view == img_back) {
            finish();
        }
    }

    private void facebookLogin() {

        callbackmanager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile, email"));
        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request1 = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            Log.e("FB Response ERROR : ", "GraphResponse Result" + response.toString());
                                        } else {
                                            Log.e("FB Response SUCCESS", "" + json.toString());
                                            try {

                                                String str_firstname = json.getString("first_name");
                                                String str_lastname = json.getString("last_name");
                                                String str_email = json.getString("email");
                                                String str_username = json.getString("name");
                                                URL profile_pic = new URL("https://graph.facebook.com/" + json.getString("id") + "/picture?width=250&height=250");

                                                /*AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                                accessToken.getToken();*/

                                                Log.e("FB_firstname : ", str_firstname);
                                                Log.e("FB_lastname : ", str_lastname);
                                                Log.e("FB_email : ", str_email);
                                                Log.e("FB_profile : ", profile_pic.toString());
                                                Log.e("FB_id : ", json.getString("id"));

                                                apiCallForGetUserByEmail(str_firstname, str_lastname, str_username, str_email, "f", profile_pic.toString(), json.getString("id"));

                                            } catch (Exception e) {
                                                Toast.makeText(mContext, getString(R.string.msg_facebook_login_error), Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                        );
                        Bundle parameter = new Bundle();
//                        parameter.putString("fields", "id,name,email,first_name,last_name");
                        parameter.putString("fields", "id,name,first_name,last_name,email,gender, birthday");
                        request1.setParameters(parameter);
                        request1.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("Cancel", "On cancel facebook login");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(mContext, getString(R.string.msg_facebook_login_failed), Toast.LENGTH_LONG).show();
                        Log.e("ERROR", error.toString());
                        if (error instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                }
        );
    }

    private void googleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQ_CODE_FINGER && resultCode == Activity.RESULT_OK) {
//                gotoDashboard(true);
            } else if (requestCode == RC_SIGN_IN) {

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } else {
                callbackmanager.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logoutGoogle() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
//                    mGoogleApiClient.disconnect();
                }
            });
        } else {
            Toast.makeText(mContext, getString(R.string.msg_googleapi_not_connect), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct != null && acct.getEmail() != null) {

                String fname = acct.getGivenName();
                String lname = acct.getFamilyName();
                String username = acct.getDisplayName();
                String email = acct.getEmail();
                String photo = String.valueOf(acct.getPhotoUrl());

                Log.e("G_Id", acct.getId() + "");
                Log.e("G_fname", fname + "");
                Log.e("G_lname", lname + "");
                Log.e("G_username", username + "");
                Log.e("G_email", email + "");
                Log.e("G_photo", photo + "");

                apiCallForGetUserByEmail(fname, lname, username, email, "g", photo, acct.getId());

            } else {
                Toast.makeText(mContext, getString(R.string.msg_google_login_failed), Toast.LENGTH_LONG).show();
            }
        } else {
            MyLogs.e("TAG", "Google Login failed:");
            Toast.makeText(mContext, getString(R.string.msg_google_login_failed), Toast.LENGTH_LONG).show();

        }
    }


    private void apiCallForGetUserByEmail(final String fname, final String lname, final String username, final String email, final String socialType, final String image, final String authToken) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("email");
        value.add(email);

        param.add("user_role");
        value.add(userRole);

        /*param.add("device_type");
        value.add(BaseUrl.deviceType);

        param.add("communication_token");
        value.add(fcmToken);*/

        new ParseJSON(mContext, BaseUrl.getUserByEmail, param, value, UserByEmailPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {

                /** Logout social login */
                if (socialType.equalsIgnoreCase("f")) {
                    LoginManager.getInstance().logOut();
                } else {
                    logoutGoogle();
                }

                UserByEmailPojoItem pojo = null;

                if (status) {
                    pojo = ((UserByEmailPojo) obj).getUserByEmailPojoItem();

                    apiCallForSocialRegistration(
                            pojo.getFirstName(), pojo.getLastName(), pojo.getEmail(), pojo.getGender(),
                            pojo.getMobileNumber(), pojo.getLogoImage(), pojo.getAddress(), pojo.getCity(), pojo.getCountry(),
                            pojo.getLocation(), socialType, pojo.getBirthdate(), pojo.getUserCode(), authToken,
                            pojo.getLat(), pojo.getLng(), pojo.getService(), pojo.getKeyword());
                } else {
                    if (userRole.equalsIgnoreCase("Customer")) {

                        gotoCutSignUpStepSecond(true, userRole, fname, lname, email, image, socialType, authToken);
                    } else if (userRole.equalsIgnoreCase("Sole Professional")) {

                        gotoProSignUpStepOne(true, userRole, fname, lname, email, image, socialType, authToken);
                    }
                }
            }
        });
    }

    private void gotoCutSignUpStepSecond(boolean isFromSocial, String userRole, String fname, String lname, String email, String image, String socialType, String authToken) {
        Intent intent = new Intent(mContext, SignupCustomerNewActivity.class);
        intent.putExtra("isFromSocial", isFromSocial);
        intent.putExtra("userRole", userRole);
        intent.putExtra("fName", fname);
        intent.putExtra("lName", lname);
        intent.putExtra("email", email);
        intent.putExtra("profilePic", image);
        intent.putExtra("socialType", socialType);
        intent.putExtra("authToken", authToken);
        startActivity(intent);
    }

    private void gotoProSignUpStepOne(boolean isFromSocial, String userRole, String fname, String lname, String email, String image, String socialType, String authToken) {
        Intent intent = new Intent(mContext, SignupProfessionalNewActivity.class);
        intent.putExtra("isFromSocial", isFromSocial);
        intent.putExtra("userRole", userRole);
        intent.putExtra("fName", fname);
        intent.putExtra("lName", lname);
        intent.putExtra("email", email);
        intent.putExtra("profilePic", image);
        intent.putExtra("socialType", socialType);
        intent.putExtra("authToken", authToken);
        startActivity(intent);
    }

    private void apiCallForSocialRegistration(
            String strFname, String strLname, String strEmail, String strGender,
            String strMobile, String imagePath, String strAddress, String strCity, String strCountry,
            String strLocation, final String socialType, String strBdate, String userCode, final String authToken,
            String lat, String lng, String service, String keyword) {

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

        param.add("birthdate");
        value.add(strBdate);

        param.add("image");
        value.add(imagePath);

        param.add("gender");
        value.add(strGender);

        param.add("full_mobile_number");
        value.add(strMobile);

        param.add("address");
        value.add(strAddress);

        param.add("location");
        value.add(strLocation);

        param.add("city");
        value.add(strCity);

        param.add("country");
        value.add(strCountry);

        param.add("lat");
        value.add(String.valueOf(lat));

        param.add("lng");
        value.add(String.valueOf(lng));

        param.add("role");
        value.add(userRole);

        if (socialType.equalsIgnoreCase("f")) {
            param.add("fb_auth_token");
            value.add(authToken);
        } else {
            param.add("gp_auth_token");
            value.add(authToken);
        }

        param.add("user_code");
        value.add(userCode);

        if (userRole.equalsIgnoreCase("Sole Professional")) {
            param.add("services_name");
            value.add(service);
            param.add("skills");
            value.add(keyword);
        }
        new ParseJSON(mContext, (socialType.equalsIgnoreCase("f") ? BaseUrl.socialLoginFacebook : BaseUrl.socialLoginGoogle), param, value, SocialResponsePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    SocialResponsePojoItem pojoItem = ((SocialResponsePojo) obj).getSocialResponsePojoItem();
                    prefsUtil.setDeviceAuthToken(pojoItem.getDeviceAuthToken());

                    /** Set map refresh rate */
                    String interal = pojoItem.getSiteSettingPojoItem().getMapRefreshRate();
                    if (!interal.equals(""))
                        prefsUtil.setUpdateInterval(Integer.parseInt(interal));

                    if (userRole.equalsIgnoreCase("Customer")) {

                        if (pojoItem.getGender().equals("") || pojoItem.getMobileNumber().equals("") || pojoItem.getAddress().equals("")) {

                            gotoCutSignUpStepSecond(true, userRole, pojoItem.getFirstName(), pojoItem.getLastName(), pojoItem.getCompanyEmail(), pojoItem.getLogoImage(), socialType, authToken);

                        } else {
                            prefsUtil.Set_UserID(pojoItem.getUserId());
                            prefsUtil.setFname(pojoItem.getFirstName());
                            prefsUtil.setUserName(pojoItem.getFirstName() + " " + pojoItem.getLastName());
                            prefsUtil.setUserGender(pojoItem.getGender());
                            prefsUtil.setUserPic(pojoItem.getLogoImage());
                            prefsUtil.setRole(pojoItem.getRole());
                            prefsUtil.SetIsloogedIn(true);
                            try {
                                prefsUtil.setRating(String.valueOf(pojoItem.getAverageRating()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(mContext, CustomerHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                    } else if (userRole.equalsIgnoreCase("Sole Professional")) {
                        if (pojoItem.getGender().equals("") || pojoItem.getMobileNumber().equals("") || pojoItem.getService().equals("") || pojoItem.getAddress().equals("")) {

                            gotoProSignUpStepOne(true, userRole, pojoItem.getFirstName(), pojoItem.getLastName(), pojoItem.getCompanyEmail(), pojoItem.getLogoImage(), socialType, authToken);

                        } else if (pojoItem.getIsRegistrationPurchased().equalsIgnoreCase("no")) {

                            Intent intent = new Intent(mContext, ProfileDetailsThree.class);
                            intent.putExtra("userId", pojoItem.getUserId());
                            intent.putExtra("fName", pojoItem.getFirstName());
                            intent.putExtra("lName", pojoItem.getLastName());
                            intent.putExtra("gender", pojoItem.getGender());
                            intent.putExtra("profilePic", pojoItem.getLogoImage());
                            intent.putExtra("userRole", pojoItem.getRole());
                            intent.putExtra("isFromSocial", true);
                            startActivity(intent);

                        } else if (pojoItem.getMemberShipStartDate().equals("0000-00-00")) {

                            Intent intent = new Intent(mContext, ProfessionalSignupStepFour.class);
                            intent.putExtra("userId", pojoItem.getUserId());
                            intent.putExtra("fName", pojoItem.getFirstName());
                            intent.putExtra("lName", pojoItem.getLastName());
                            intent.putExtra("gender", pojoItem.getGender());
                            intent.putExtra("profilePic", pojoItem.getLogoImage());
                            intent.putExtra("userRole", pojoItem.getRole());
                            intent.putExtra("isFromSocial", true);
                            startActivity(intent);

                        } else {
                            prefsUtil.Set_UserID(pojoItem.getUserId());
                            prefsUtil.setFname(pojoItem.getFirstName());
                            prefsUtil.setUserName(pojoItem.getFirstName() + " " + pojoItem.getLastName());
                            prefsUtil.setUserGender(pojoItem.getGender());
                            prefsUtil.setUserPic(pojoItem.getLogoImage());
                            prefsUtil.setRole(pojoItem.getRole());
                            prefsUtil.SetIsloogedIn(true);
                            try {
                                prefsUtil.setRating(String.valueOf(pojoItem.getAverageRating()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            /** goto professional home by social login */
                            Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
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

    private void gotoDashboard(boolean isReqFinger) {
        /** Save user data in preference and move to dashboard */

        MyLogs.e("TAG", "isReqFinger" + isReqFinger);

        if (loginPojo != null) {

            MyLogs.e("TAG", "ROLE " + userRole);

            String first_name = loginPojo.getFirstName();
            String last_name = loginPojo.getLastName();
            String logo_image = loginPojo.getLogoImage();
            String gender = loginPojo.getGender();

            prefsUtil.Set_UserID(loginPojo.getUserId());
            prefsUtil.setFname(first_name);
            prefsUtil.setUserName(first_name + " " + last_name);
            prefsUtil.setUserGender(gender);
            prefsUtil.setUserPic(logo_image);
            prefsUtil.setRole(loginPojo.getRole());
            try {
                prefsUtil.setRating(String.valueOf(loginPojo.getRating()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            prefsUtil.SetIsloogedIn(true);

//            prefsUtil.setUserLoginDetail(loginPojo.toString());
            /*if (isReqFinger)
                prefsUtil.setRequiredFingerPrint(true);
            else {
                prefsUtil.setRequiredFingerPrint(false);
            }*/

            /*if (userRole.equals("Customer")) {
                Intent intent = new Intent(mContext, CustomerHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (userRole.equals("Employee")) {
                Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (userRole.equals("Sole Professional")) {
                Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }*/
        } else {
            MyLogs.e("TAG", "loginPojo null");
        }
    }

    /*private void userLogin_customer() {
        final String email = edt_email.getText().toString().trim();
        String password = edt_pass.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        progressDialog.dismiss();


                        //if the task is successfull
                        if (task.isSuccessful()) {

                            UserId = firebaseAuth.getCurrentUser().getUid();
                            Log.v("UserId", UserId);

                            db.collection("users_customer").document(UserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.v("TAGsdutta", String.valueOf(document.getData()));
                                            String Firstname = document.getString("first_name");
                                            // String lon = document.getDouble("lon");
                                            String last_name = document.getString("last_name");
                                            String phone = document.getString("phone");
                                            String email = document.getString("email");
                                            String gender = document.getString("gender");
                                            String reg_date = document.getString("reg_date");
                                            String profile_image = document.getString("profile_image");
                                            String user_id = document.getString("user_id");
                                            String verification_code = document.getString("verification_code");
                                            String user_type = document.getString("user_type");


                                            Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("name", Firstname + " " + last_name);
                                            intent.putExtra("user_pic", profile_image);
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();


                                        } else {
                                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                                            //final AlertDialog alert = builder.create();
                                            builder.setTitle("PickMe App");
                                            // builder.setIcon(R.drawable.logo_icon);
                                            builder.setMessage("Incorrect email or password");
                                            builder.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {


                                                        }
                                                    });

                                            final android.app.AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });


                       *//*     //start the profile activity
                            db.collection("users_customer")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.i("LOGGER","First "+document.getString("email"));
                                                    if (email.equals(document.getString("email"))) {
                                                        LogInStatus="login";
                                                        String user_type = document.getString("user_type");
                                                        Log.d("Returnsdutta", document.getId() + " => " + document.getData());


                                                        if (prefsUtil.GetUserType().equals(user_type)) {
                                                            String Firstname = document.getString("first_name");
                                                            String lon = document.getString("lon");
                                                            String last_name = document.getString("last_name");
                                                            String phone = document.getString("phone");
                                                            String email = document.getString("email");
                                                            String gender = document.getString("gender");
                                                            String reg_date = document.getString("reg_date");

                                                            String user_id = document.getString("user_id");
                                                            String verification_code = document.getString("verification_code");

                                                            Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                                                            intent.putExtra("name", Firstname + " " + last_name);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                       *//**//* else {
                                                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                                                                //final AlertDialog alert = builder.create();
                                                            builder.setTitle("PickMe App");
                                                            // builder.setIcon(R.drawable.logo_icon);
                                                             builder.setMessage("Incorrect email or password");
                                                             builder.setPositiveButton("OK",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {




                                                            }
                                                        });

                                                final android.app.AlertDialog alert = builder.create();
                                                alert.show();

                                                        }
*//**//*
                                                    }
                                                if (LogInStatus.equals(""))
                                                {
                                                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                                                    //final AlertDialog alert = builder.create();
                                                    builder.setTitle("PickMe App");
                                                    // builder.setIcon(R.drawable.logo_icon);
                                                    builder.setMessage("Incorrect email or password");
                                                    builder.setPositiveButton("OK",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {


                                                                }
                                                            });

                                                    final android.app.AlertDialog alert = builder.create();
                                                    alert.show();
                                                }


                                            } else {


                                                Log.v("Return", "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
*//*
                            // finish();

                        } else {

                          final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                            //final AlertDialog alert = builder.create();
                            builder.setTitle("PickMe App");
                            // builder.setIcon(R.drawable.logo_icon);
                            builder.setMessage("Incorrect email or password");
                            builder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });

                            final android.app.AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }

                });
    }*/

    /*private void userLogin_professinal() {
        final String email = edt_email.getText().toString().trim();
        String password = edt_pass.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        //if the task is successfull
                        if (task.isSuccessful()) {

                            UserId = firebaseAuth.getCurrentUser().getUid();
                            Log.v("UserId", UserId);

                            db.collection("users_professional").document(UserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        Log.v("TAGsdutta_prof", String.valueOf(document.getData()));
                                        if (document.exists()) {

                                            String Firstname = document.getString("first_name");
                                            String lon = document.getString("lon");
                                            String last_name = document.getString("last_name");
                                            String phone = document.getString("phone");
                                            String email = document.getString("email");
                                            String gender = document.getString("gender");
                                            String reg_date = document.getString("reg_date");
                                            String user_type = document.getString("user_type");
                                            String user_id = document.getString("user_id");
                                            String verification_code = document.getString("verification_code");
                                            String profile_image = document.getString("profile_image");

                                            Intent intent = new Intent(LoginActivity.this, ProfessionalHomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("name", Firstname + " " + last_name);
                                            intent.putExtra("user_pic", profile_image);

                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                                            //final AlertDialog alert = builder.create();
                                            builder.setTitle("PickMe App");
                                            // builder.setIcon(R.drawable.logo_icon);
                                            builder.setMessage("Incorrect email or password");
                                            builder.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {


                                                        }
                                                    });

                                            final android.app.AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });



*//*
                            //start the profile activity
                            db.collection("users_professional")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.i("LOGGER","First "+document.getString("email"));
                                                    if (email.equals(document.getString("email"))) {
                                                        LogInStatus = "login";
                                                        Log.d("Returnsdutta", document.getId() + " => " + document.getData());
                                                        String Firstname = document.getString("first_name");
                                                        String lon = document.getString("lon");
                                                        String last_name = document.getString("last_name");
                                                        String phone = document.getString("phone");
                                                        String email = document.getString("email");
                                                        String gender = document.getString("gender");
                                                        String reg_date = document.getString("reg_date");
                                                        String user_type = document.getString("user_type");
                                                        String user_id = document.getString("user_id");
                                                        String verification_code = document.getString("verification_code");

                                                        Log.v("firstname", Firstname);

                                                       // if (prefsUtil.GetprofessionaType().equals(user_type)) {
                                                            Intent intent = new Intent(LoginActivity.this, ProfessionalHomeActivity.class);
                                                            intent.putExtra("name", Firstname + " " + last_name);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                       *//**//* } else {
                                                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                                                            //final AlertDialog alert = builder.create();
                                                            builder.setTitle("PickMe App");
                                                            // builder.setIcon(R.drawable.logo_icon);
                                                            builder.setMessage("Incorrect email or password");
                                                            builder.setPositiveButton("OK",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {


                                                                        }
                                                                    });

                                                            final android.app.AlertDialog alert = builder.create();
                                                            alert.show();

                                                        }*//**//*
                                                    }



                                                }
                                                if (LogInStatus.equals(""))
                                                {
                                                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                                                    //final AlertDialog alert = builder.create();
                                                    builder.setTitle("PickMe App");
                                                    // builder.setIcon(R.drawable.logo_icon);
                                                    builder.setMessage("Incorrect email or password");
                                                    builder.setPositiveButton("OK",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {


                                                                }
                                                            });

                                                    final android.app.AlertDialog alert = builder.create();
                                                    alert.show();
                                                }
                                            } else {


                                                Log.w("Return", "Error getting documents.", task.getException());
                                            }
                                        }
                                    });*//*


                        } else {
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                            //final AlertDialog alert = builder.create();
                            builder.setTitle("PickMe App");
                            // builder.setIcon(R.drawable.logo_icon);
                            builder.setMessage("Incorrect email or password");
                            builder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });

                            final android.app.AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }

                });
    }*/

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    public void defaultSettingsRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
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
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);
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

    private void setupGoogleApiClient() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        /*mGoogleApiClient = new GoogleApiClient.Builder(GoogleLoginActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this, this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 3000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 5000); // 1 second, in milliseconds
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        if (location != null) {

            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            /*prefsUtil.setlattitute(currentLatitude);
            prefsUtil.setLongitute(currentLongitude);*/

            prefsUtil.setLat(String.valueOf(currentLatitude));
            prefsUtil.setLng(String.valueOf(currentLongitude));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.e("TAG", "Firing onLocationChanged");
        mCurrentLocation = location;
        currentLatitude = mCurrentLocation.getLatitude();
        currentLongitude = mCurrentLocation.getLongitude();

/*        prefsUtil.setlattitute(currentLatitude);
        prefsUtil.setLongitute(currentLongitude);*/

        prefsUtil.setLat(String.valueOf(currentLatitude));
        prefsUtil.setLng(String.valueOf(currentLongitude));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    void checkpremission(String which) {
        // Here, thisActivity is the current activity
        if (which.equals("location")) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                } else {
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            } else {
                //handler.sendEmptyMessage(100);
            }
        }
    }


    public class apiCallForLogin extends AsyncTask<String, Void, String> {

        private boolean isFingerAuth = false;

        public apiCallForLogin(boolean isFingerAuth) {
            this.isFingerAuth = isFingerAuth;
        }

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
            progressDialog.setMessage("Please wait...");
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
                if (isFingerAuth) {
                    request_main.put("email", FingerPrintPrefUtil.with(mContext).readString("email"));
                    request_main.put("password", FingerPrintPrefUtil.with(mContext).readString("pwd"));
                } else {
                    request_main.put("email", st_email);
                    request_main.put("password", st_pass);
                }
                request_main.put("device_type", "android");
                request_main.put("user_role", userRole);
                request_main.put("communication_token", fcmToken);

                MyLogs.e("URL", BaseUrl.urlLogin);
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());
                Request request = new Request.Builder()
                        .url(BaseUrl.urlLogin)
                        .post(body)
                        .build();

                okhttp3.Response response = client.newCall(request).execute();
                Response1 = response.body().string();
                return Response1;

            } catch (IOException e) {
                e.printStackTrace();
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
                Toast.makeText(getApplicationContext(), getString(R.string.msg_network_error), Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    MyLogs.e("Response", String.valueOf(jsonObject));

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("Yes")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONObject userDetail = data.getJSONObject("user_detail");
                        jsonObjectGloble = userDetail;

                        String interal = userDetail.getJSONObject("site_settings").getString("map_refresh_rate");

                        if (!interal.equals(""))
                            prefsUtil.setUpdateInterval(Integer.parseInt(interal));

                        final String user_id = userDetail.getString("user_id");
                        userRole = userDetail.getString("role");

                        JSONObject verifyObject = data.getJSONObject("verification_data");
                        String verifyStatus = verifyObject.getString("status");

                        if (userRole.equalsIgnoreCase("Customer")) {

                            /*if (verifyStatus.equalsIgnoreCase("inactive")) {
                                prefsUtil.Set_UserID(user_id);
                                prefsUtil.setDeviceAuthToken(userDetail.getString("device_auth_token"));

                                Intent intent = new Intent(mContext, VerificationActivity.class);
                                intent.putExtra("what", what);
                                startActivity(intent);
                                finish();

                            } else {*/
                            if (userDetail.getString("mobile_number").equals("") ||
                                    userDetail.getString("gender").equals("") ||
                                    userDetail.getString("address").equals("")) {

                                prefsUtil.setDeviceAuthToken(userDetail.getString("device_auth_token"));

                                Intent intent = new Intent(mContext, SignupCustomerNewActivity.class);
                                intent.putExtra("userId", userDetail.getString("user_id"));
                                intent.putExtra("fName", userDetail.getString("first_name"));
                                intent.putExtra("lName", userDetail.getString("last_name"));
                                intent.putExtra("userRole", userDetail.getString("role"));
                                intent.putExtra("profilePic", userDetail.getString("logo_image"));
                                startActivity(intent);

                            } else {

                                /** for finger print auth new way implementation */
                                if (!isFingerAuth) {
                                    FingerPrintPrefUtil.with(mContext).write("email", edt_email.getText().toString().trim());
                                    FingerPrintPrefUtil.with(mContext).write("pwd", edt_pass.getText().toString().trim());
                                    FingerPrintPrefUtil.with(mContext).write(BaseUrl.isCompletedSignup, true);
                                }
                                moveToDashboard();
                            }
//                            }

                        } else if (userRole.equalsIgnoreCase("Sole Professional")) {

                            if (verifyStatus.equalsIgnoreCase("inactive")) {
                                prefsUtil.Set_UserID(user_id);
                                prefsUtil.setDeviceAuthToken(userDetail.getString("device_auth_token"));

                                Intent intent = new Intent(mContext, VerificationActivity.class);
                                intent.putExtra("what", what);
                                startActivity(intent);
                                finish();

                            } else {
                                if (jsonObjectGloble.getString("service").equals("")
                                        /*jsonObjectGloble.getString("address").equals("") ||
                                        jsonObjectGloble.getString("gender").equals("") ||
                                        jsonObjectGloble.getString("mobile_number").equals("")*/) {

                                    prefsUtil.setDeviceAuthToken(jsonObjectGloble.getString("device_auth_token"));

                                    Intent intent = new Intent(mContext, SignupProfessionalNewActivity.class);
                                    intent.putExtra("userId", userDetail.getString("user_id"));
                                    intent.putExtra("userRole", "Sole Professional");
                                    intent.putExtra("userName", userDetail.getString("first_name") + " " + userDetail.getString("last_name"));
                                    intent.putExtra("gender", userDetail.getString("gender"));
                                    intent.putExtra("profilePic", userDetail.getString("logo_image"));
                                    intent.putExtra("mobile", userDetail.getString("mobile_number"));
                                    startActivity(intent);

                                } else if (jsonObjectGloble.getString("isRegistrationPurchased").equalsIgnoreCase("no")) {

                                    prefsUtil.setDeviceAuthToken(jsonObjectGloble.getString("device_auth_token"));

                                    Intent intent = new Intent(mContext, ProfileDetailsThree.class);
                                    intent.putExtra("userId", jsonObjectGloble.getString("user_id"));
                                    intent.putExtra("fName", jsonObjectGloble.getString("first_name"));
                                    intent.putExtra("lName", jsonObjectGloble.getString("last_name"));
                                    intent.putExtra("gender", jsonObjectGloble.getString("gender"));
                                    intent.putExtra("profilePic", jsonObjectGloble.getString("logo_image"));
                                    intent.putExtra("userRole", jsonObjectGloble.getString("role"));
                                    startActivity(intent);

                                } else if (jsonObjectGloble.getString("membership_start_date").equals("0000-00-00")) {

                                    prefsUtil.setDeviceAuthToken(jsonObjectGloble.getString("device_auth_token"));

                                    Intent intent = new Intent(mContext, ProfessionalSignupStepFour.class);
                                    intent.putExtra("userId", jsonObjectGloble.getString("user_id"));
                                    intent.putExtra("fName", jsonObjectGloble.getString("first_name"));
                                    intent.putExtra("lName", jsonObjectGloble.getString("last_name"));
                                    intent.putExtra("gender", jsonObjectGloble.getString("gender"));
                                    intent.putExtra("profilePic", jsonObjectGloble.getString("logo_image"));
                                    intent.putExtra("userRole", jsonObjectGloble.getString("role"));
                                    startActivity(intent);

                                } else {
                                    if (!isFingerAuth) {
                                        FingerPrintPrefUtil.with(mContext).write("email", edt_email.getText().toString().trim());
                                        FingerPrintPrefUtil.with(mContext).write("pwd", edt_pass.getText().toString().trim());
                                        FingerPrintPrefUtil.with(mContext).write(BaseUrl.isCompletedSignup, true);
                                    }
                                    moveToDashboard();
                                }
                            }

                        } else if (userRole.equalsIgnoreCase("Employee")) {
                            if (!isFingerAuth) {
                                FingerPrintPrefUtil.with(mContext).write("email", edt_email.getText().toString().trim());
                                FingerPrintPrefUtil.with(mContext).write("pwd", edt_pass.getText().toString().trim());
                                FingerPrintPrefUtil.with(mContext).write(BaseUrl.isCompletedSignup, true);
                            }
                            moveToDashboard();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.app_name);
                        builder.setMessage(message);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveToDashboard() {

        if (jsonObjectGloble != null) {
            try {
                String user_id = jsonObjectGloble.getString("user_id");
                String first_name = jsonObjectGloble.getString("first_name");
                String last_name = jsonObjectGloble.getString("last_name");
                String logo_image = jsonObjectGloble.getString("logo_image");
                String gender = jsonObjectGloble.getString("gender");

                prefsUtil.Set_UserID(user_id);
                prefsUtil.setUserName(first_name + " " + last_name);
                prefsUtil.setUserGender(gender);
                prefsUtil.setUserPic(logo_image);
                prefsUtil.setRole(jsonObjectGloble.getString("role"));
                prefsUtil.setDeviceAuthToken(jsonObjectGloble.getString("device_auth_token"));
                prefsUtil.SetIsloogedIn(true);
                prefsUtil.setRating(jsonObjectGloble.getString("average_rating"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (userRole.equals("Customer")) {
                Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (userRole.equals("Employee")) {
                Intent intent = new Intent(LoginActivity.this, ProfessionalHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (userRole.equals("Sole Professional")) {
                Intent intent = new Intent(LoginActivity.this, ProfessionalHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void processToFingerPrintAuthentication() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//            fingerprintManager = (FingerprintManagerCompat) getSystemService(Context.FINGERPRINT_SERVICE);

            fingerprintManager = FingerprintManagerCompat.from(mContext);


            /*Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_fengerprint_auth);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);

            TextView txtMsgDialog = dialog.findViewById(R.id.txtMsgDialog);*/

            //Check whether the device has a fingerprint sensor//
            /*if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                Toast.makeText(mContext, getString(R.string.msg_finger_print_not_support), Toast.LENGTH_LONG).show();
            }*/


            if (!fingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//

                Toast.makeText(mContext, getString(R.string.msg_finger_print_not_support), Toast.LENGTH_LONG).show();
            } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {   //Check whether the user has granted your app the USE_FINGERPRINT permission//

                Toast.makeText(mContext, getString(R.string.msg_enable_finger_permission), Toast.LENGTH_LONG).show();
            } else if (!fingerprintManager.hasEnrolledFingerprints()) { //Check that the user has registered at least one fingerprint//

                Toast.makeText(mContext, getString(R.string.msg_no_finger_config), Toast.LENGTH_LONG).show();
            } else if (!keyguardManager.isKeyguardSecure()) {  //Check that the lock screen is secured//

                Toast.makeText(mContext, getString(R.string.msg_lock_security), Toast.LENGTH_LONG).show();
            } else {
                try {
                    generateKey();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (initCipher()) {
                    //If the cipher is initialized successfully, then create a CryptoObject instance//
                    cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);
//                    cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                    // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                    MyFingerprintHandler helper = new MyFingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }
//            dialog.show();
        } else {
            Toast.makeText(mContext, getString(R.string.msg_finger_print_not_support), Toast.LENGTH_LONG).show();
        }
    }

    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    @TargetApi(Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public class MyFingerprintHandler extends FingerprintManagerCompat.AuthenticationCallback {
// You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
        // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

        private CancellationSignal cancellationSignal;
        private Context context;

        public MyFingerprintHandler(Context mContext) {
            context = mContext;
        }

        //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

        /*public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

            cancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }*/

        public void startAuth(FingerprintManagerCompat manager, FingerprintManagerCompat.CryptoObject cryptoObject) {

            cancellationSignal = new CancellationSignal();

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject, 0, cancellationSignal, this, null);
        }

        @Override
        //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            //I’m going to display the results of fingerprint authentication as a series of toasts.
            //Here, I’m creating the message that’ll be displayed if an error occurs//
//            Toast.makeText(context, getString(R.string.finger_auth_error) + errString, Toast.LENGTH_LONG).show();
            MyLogs.e("TAG", "onAuthenticationError : " + errString);
        }

        @Override
        //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//
        public void onAuthenticationFailed() {
            Toast.makeText(context, getString(R.string.finger_auth_failed), Toast.LENGTH_LONG).show();
            MyLogs.e("TAG", "onAuthenticationFailed");
        }

        @Override
        //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
        //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(context, getString(R.string.finger_auth_help) + helpString, Toast.LENGTH_LONG).show();
            MyLogs.e("TAG", "onAuthenticationHelp : " + helpString);
        }

        @Override
        //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            MyLogs.e("TAG", "onAuthenticationSucceeded : "); // + result.getCryptoObject().getCipher().getAlgorithm());

            new apiCallForLogin(true).execute();
        }
    }

}

