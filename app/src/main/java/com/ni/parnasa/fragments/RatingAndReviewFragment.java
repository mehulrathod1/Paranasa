package com.ni.parnasa.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.ProfileNewActivity;
import com.ni.parnasa.adapters.ReviewRattingAdapter;
import com.ni.parnasa.tmpPojos.RatingPojoItems;
import com.ni.parnasa.tmpPojos.ReviewRatingPojo;
import com.ni.parnasa.tmpPojos.ReviewRatingPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ValidFragment")
public class RatingAndReviewFragment extends Fragment {

    private String firstName, lastName, user_code, logoImage;
    private List<RatingPojoItems> list;
    private RecyclerView rvReviews;
    private ReviewRattingAdapter adapter;
    private int rating = 0;

    public RatingAndReviewFragment(String firstName, String lastName, String user_code, String logoImage, int rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.user_code = user_code;
        this.logoImage = logoImage;
        this.rating = rating;
    }

    private ImageView imgProfile;
    private TextView txtName, txtCode, txtAverageRating, txtFive, txtFour, txtThree, txtTwo, txtOne;
    private RatingBar ratingMyReview, ratingProfile;

    private final int recordPerPage = 15;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_rating_and_review, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
        txtName = view.findViewById(R.id.txtName);
        txtCode = view.findViewById(R.id.txtCode);
        txtFive = view.findViewById(R.id.txtFive);
        txtFour = view.findViewById(R.id.txtFour);
        txtThree = view.findViewById(R.id.txtThree);
        txtTwo = view.findViewById(R.id.txtTwo);
        txtOne = view.findViewById(R.id.txtOne);
        rvReviews = view.findViewById(R.id.rvReviews);

        txtAverageRating = view.findViewById(R.id.txtAverageRating);
        ratingMyReview = view.findViewById(R.id.ratingMyReview);
        ratingProfile = view.findViewById(R.id.ratingProfile);

        txtName.setText(firstName + " " + lastName);
        txtCode.setText(user_code);
        ratingMyReview.setRating(rating);

        Glide.with(getActivity()).asBitmap()
                .load(logoImage)
                .into(imgProfile);


        list = new ArrayList<>();
        rvReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ReviewRattingAdapter(getActivity(), list);
        rvReviews.setAdapter(adapter);

        apiCallForGetReviews();

        return view;
    }

    private void apiCallForGetReviews() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(((ProfileNewActivity) getActivity()).userId);

        param.add("page");
        value.add(String.valueOf(page));

        param.add("limit");
        value.add(String.valueOf(recordPerPage));

        param.add("language");
        value.add("EN");

        new ParseJSON(getActivity(), BaseUrl.getUserReviewRating, param, value, ReviewRatingPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    ReviewRatingPojoItem pojoItem = ((ReviewRatingPojo) obj).getReviewRatingPojoItem();

                    txtAverageRating.setText(pojoItem.getRatingsSummery().getAverageRatings());
                    try {
                        ratingProfile.setRating(Float.valueOf(pojoItem.getRatingsSummery().getAverageRatings()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    txtFive.setText(pojoItem.getRatingsSummery().getRatingsRatio().getJsonMember5Star() + " %");
                    txtFour.setText(pojoItem.getRatingsSummery().getRatingsRatio().getJsonMember4Star() + " %");
                    txtThree.setText(pojoItem.getRatingsSummery().getRatingsRatio().getJsonMember3Star() + " %");
                    txtTwo.setText(pojoItem.getRatingsSummery().getRatingsRatio().getJsonMember2Star() + " %");
                    txtOne.setText(pojoItem.getRatingsSummery().getRatingsRatio().getJsonMember1Star() + " %");

                    list.addAll(pojoItem.getReviewRatings());
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
