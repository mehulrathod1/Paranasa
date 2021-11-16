package com.ni.parnasa.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ni.parnasa.R;
import com.ni.parnasa.utils.MultiLanguageUtils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

@SuppressLint("ValidFragment")
public class BasicInfoFragment extends Fragment {

    private String role, firstName, lastName, user_code, companyEmail, companyName, gender, service, location, logoImage, mobile;
    private int rating;

    private ImageView imgProfile;
    private TextView txtName, txtCode, txtEmail, txtPhone, txtCompanyName, txtGender, txtService, txtAddress;
    private RatingBar ratingMyReview;
    private RelativeLayout relService, relCompany;

    public BasicInfoFragment(String role, String firstName, String lastName, String user_code, String companyEmail,
                             String companyName, String gender, String service, String location, String logoImage,
                             int rating, String mobileNumber) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.user_code = user_code;
        this.companyEmail = companyEmail;
        this.companyName = companyName;
        this.gender = gender;
        this.service = service;
        this.location = location;
        this.logoImage = logoImage;
        this.rating = rating;
        this.mobile = mobileNumber;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
        txtName = view.findViewById(R.id.txtName);
        txtCode = view.findViewById(R.id.txtCode);
        ratingMyReview = view.findViewById(R.id.ratingMyReview);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtCompanyName = view.findViewById(R.id.txtCompanyName);
        txtGender = view.findViewById(R.id.txtGender);
        txtService = view.findViewById(R.id.txtService);
        txtAddress = view.findViewById(R.id.txtAddress);
        relService = view.findViewById(R.id.relService);
        relCompany = view.findViewById(R.id.relCompany);

        txtName.setText(firstName + " " + lastName);
        txtCode.setText(user_code);
        txtEmail.setText(companyEmail);
        txtPhone.setText(mobile);
        txtCompanyName.setText(companyName);
        txtGender.setText(gender);
        txtService.setText(service);
        txtAddress.setText(location);

        if (rating == 0) {
            ratingMyReview.setRating(5f);
        } else {
            ratingMyReview.setRating(rating);
        }

        Glide.with(getActivity()).asBitmap()
                .load(logoImage)
                .into(imgProfile);

        if (role.equalsIgnoreCase("Customer")) {
            relCompany.setVisibility(View.GONE);
            relService.setVisibility(View.GONE);
        } else if (role.equalsIgnoreCase("Sole Professional")) {
            relCompany.setVisibility(View.VISIBLE);
            relService.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
