package com.ni.parnasa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.models.ProfilePojo;
import com.ni.parnasa.models.ProfilePojoItem;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.ApplicationController;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private ImageView img_back, imgEditProfile, imgProfile, imgFavUnfav;
    private RatingBar ratingProfile;
    private TextView txtUserName, txtEmail, txtPhone, txtGender, txtBdata, txtCompanyName, txtService, txtAddress, txtKeywords;
    private LinearLayout linlayService, linlayCompanyName;

    private boolean fromNavigation, isFavorite;
    private String unotherUserId, imagePath = "";
    private double lat = 0.0, lng = 0.0;
    private String location = "";
    private String services = "", keyWord = "";
    private String CITY = "", COUNTRY = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = ProfileActivity.this;

        init();

        /** Get Intent data */

        fromNavigation = getIntent().getBooleanExtra("fromNavigation", false);
        isFavorite = getIntent().getBooleanExtra("isFavorite", false);

//        MyLogs.e("TAG","isFavorite "+isFavorite);

        if (fromNavigation) {
//            imagePath = prefsUtil.getUserPic();
//            imgEditProfile.setVisibility(View.VISIBLE);
            imgFavUnfav.setVisibility(View.GONE);
        } else {
            unotherUserId = getIntent().getStringExtra("anotherUserId");
//            imagePath = getIntent().getStringExtra("profilePath");
//            imgEditProfile.setVisibility(View.GONE);
            imgFavUnfav.setVisibility(View.VISIBLE);
            /*if (isFavorite) {
                imgFavUnfav.setImageResource(R.drawable.ic_star_full);
            } else {
                imgFavUnfav.setImageResource(R.drawable.ic_star_empty);
            }*/
        }
        /** over intent data */
    }

    private void init() {

        prefsUtil = new PrefsUtil(mContext);

        img_back = findViewById(R.id.img_back);
        imgEditProfile = findViewById(R.id.imgEditProfile);
        ratingProfile = findViewById(R.id.ratingProfile);
        imgProfile = findViewById(R.id.imgProfile);
        imgFavUnfav = findViewById(R.id.imgFavUnfav);
        txtUserName = findViewById(R.id.txtUserName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtGender = findViewById(R.id.txtGender);
        txtBdata = findViewById(R.id.txtBdata);
        txtCompanyName = findViewById(R.id.txtCompanyName);
        txtService = findViewById(R.id.txtService);
        txtAddress = findViewById(R.id.txtAddress);
        txtKeywords = findViewById(R.id.txtKeywords);
        linlayService = findViewById(R.id.linlayService);
        linlayCompanyName = findViewById(R.id.linlayCompanyName);

        img_back.setOnClickListener(this);
        imgEditProfile.setOnClickListener(this);
        imgFavUnfav.setOnClickListener(this);

        setupAdMob();
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallForGetProfile();
    }

    @Override
    public void onClick(View v) {
        if (v == img_back) {
            onBackPressed();
        } else if (v == imgEditProfile) {
            Intent intent = new Intent(mContext, EditProfileActivity.class);
            intent.putExtra("isFromProfile", true);
            intent.putExtra("imagePath", imagePath);
            intent.putExtra("userName", txtUserName.getText().toString());
            intent.putExtra("email", txtEmail.getText().toString());
            intent.putExtra("company", txtCompanyName.getText().toString());
            intent.putExtra("phoneNo", txtPhone.getText().toString());
            intent.putExtra("address", txtAddress.getText().toString());
            intent.putExtra("location", location);
            intent.putExtra("city", CITY);
            intent.putExtra("country", COUNTRY);
            intent.putExtra("bdate", txtBdata.getText().toString());
            intent.putExtra("gender", txtGender.getText().toString());
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.putExtra("service", services);
            intent.putExtra("keyword", keyWord);

            startActivity(intent);
        } else if (v == imgFavUnfav) {
            apiCallForFavUnfav();
        }
    }

    private void apiCallForFavUnfav() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        if (isFavorite) {
            param.add("unfavorite_user_id");
            value.add(unotherUserId);
        } else {
            param.add("favorite_user_id");
            value.add(unotherUserId);
        }

        new ParseJSON(mContext, (isFavorite ? BaseUrl.unfavoriteUser : BaseUrl.favoriteUser), param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                    if (isFavorite) {
                        isFavorite = false;
                        imgFavUnfav.setImageResource(R.drawable.ic_star_empty);
                    } else {
                        isFavorite = true;
                        imgFavUnfav.setImageResource(R.drawable.ic_star_full);
                    }

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void apiCallForGetProfile() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        if (fromNavigation)
            value.add(prefsUtil.GetUserID());
        else
            value.add(unotherUserId);

        new ParseJSON(mContext, BaseUrl.getUserById, param, value, ProfilePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ProfilePojoItem pojoItem = ((ProfilePojo) obj).getProfilePojoItem();

                    txtUserName.setText(pojoItem.getFirstName() + " " + pojoItem.getLastName());
                    txtEmail.setText(pojoItem.getCompanyEmail());
                    txtPhone.setText(pojoItem.getMobileNumber());
                    txtGender.setText(pojoItem.getGender());
                    CITY = pojoItem.getCity();
                    COUNTRY = pojoItem.getCountry();


                    txtAddress.setText(pojoItem.getLocation());

                    if (pojoItem.getRating() == 0) {
                        ratingProfile.setRating(5f);
                    } else {
                        ratingProfile.setRating(pojoItem.getRating());
                    }

                    location = pojoItem.getLocation();

                    services = pojoItem.getService();
                    keyWord = pojoItem.getKeyword();

                    try {
                        lat = Double.parseDouble(pojoItem.getLat());
                        lng = Double.parseDouble(pojoItem.getLng());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (fromNavigation) {
                        imagePath = pojoItem.getLogoImage();
                        prefsUtil.setUserPic(imagePath);

                        /** process of date formatting */

                        String bDate = pojoItem.getBirthdate();

                        if (bDate.equals("") || bDate.equals("0000-00-00")) {
                            txtBdata.setText(R.string.not_applicable);
                        } else {
                            txtBdata.setText(bDate);
                            try {
                                String bdate = pojoItem.getBirthdate();
                                if (!bdate.equals("")) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = simpleDateFormat.parse(pojoItem.getBirthdate());
                                    ((ApplicationController) getApplication()).date = date;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ((ApplicationController) getApplication()).date = null;
                            }
                        }


                    } else {
                        String bDate = pojoItem.getBirthdate();

                        if (bDate.equals("") || bDate.equals("0000-00-00")) {
                            txtBdata.setText(R.string.not_applicable);
                        } else {
                            txtBdata.setText(bDate);
                        }

                        imagePath = pojoItem.getLogoImage();
                        isFavorite = pojoItem.isFavorite();
                        if (isFavorite) {
                            imgFavUnfav.setImageResource(R.drawable.ic_star_full);
                        } else {
                            imgFavUnfav.setImageResource(R.drawable.ic_star_empty);
                        }
                    }

                    Glide.with(mContext).asBitmap()
                            .load(imagePath)
                            .into(imgProfile);

                    prefsUtil.setRating(String.valueOf(pojoItem.getRating()));

                    if (pojoItem.getRole().equalsIgnoreCase("Customer")) {

                        linlayService.setVisibility(View.GONE);
                        linlayCompanyName.setVisibility(View.GONE);

                    } else {

                        linlayService.setVisibility(View.VISIBLE);
                        linlayCompanyName.setVisibility(View.VISIBLE);
                        if (!pojoItem.getCompanyName().equals(""))
                            txtCompanyName.setText(pojoItem.getCompanyName());
                        else {
                            txtCompanyName.setText(R.string.not_applicable);
                        }
                        txtService.setText(pojoItem.getService());
                        txtKeywords.setText(pojoItem.getKeyword());
                    }

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
