package com.ni.parnasa.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.fragments.BasicInfoFragment;
import com.ni.parnasa.fragments.RatingAndReviewFragment;
import com.ni.parnasa.models.ProfilePojo;
import com.ni.parnasa.models.ProfilePojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ProfileNewActivity extends AppCompatActivity {

    private Context mContext;
    public PrefsUtil prefsUtil;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView img_back;


    public String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        mContext = ProfileNewActivity.this;
        prefsUtil = new PrefsUtil(mContext);

        userId = getIntent().getStringExtra("userId");

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        img_back = findViewById(R.id.img_back);


        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        apiCallForGetProfile();

    }

    private void apiCallForGetProfile() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(userId);

        new ParseJSON(mContext, BaseUrl.getUserById, param, value, ProfilePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ProfilePojoItem pojoItem = ((ProfilePojo) obj).getProfilePojoItem();

                    List<Fragment> fragmentList = new ArrayList<>();
                    fragmentList.add(new BasicInfoFragment(pojoItem.getRole(), pojoItem.getFirstName(), pojoItem.getLastName(), pojoItem.getUser_code(),
                            pojoItem.getCompanyEmail(), pojoItem.getCompanyName(), pojoItem.getGender(), pojoItem.getService(), pojoItem.getLocation(),
                            pojoItem.getLogoImage(), pojoItem.getRating(), pojoItem.getMobileNumber()));

                    fragmentList.add(new RatingAndReviewFragment(pojoItem.getFirstName(), pojoItem.getLastName(), pojoItem.getUser_code(), pojoItem.getLogoImage(),pojoItem.getRating()));

                    viewPager.setAdapter(new ProfilePagerAdapter(getSupportFragmentManager(), fragmentList));
                    tabLayout.setupWithViewPager(viewPager);
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ProfilePagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public ProfilePagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.basic_info);
            } else {
                return getString(R.string.rating_and_review);
            }
        }
    }
}
