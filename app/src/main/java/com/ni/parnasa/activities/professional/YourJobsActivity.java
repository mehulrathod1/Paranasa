package com.ni.parnasa.activities.professional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class YourJobsActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private PrefsUtil prefsUtil;
    private BroadcastReceiver receiver;

    private ImageView iv_back, imgAddJob;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CustomPagerAdapter adapter;

    private List<Fragment> fragmentList;
    private List<String> titleList;
    private String ROLE = "";

//    private JobListFragment fragmentOne, fragmentTwo, fragmentThree, fragmentFour, fragmentFive;

    private int tmpTabPos = -1;

    @Override
    protected void onResume() {
        super.onResume();
        /*if (ROLE.equalsIgnoreCase("Customer")) {
            if (tmpTabPos == 3) {
                fragmentFour.refreshList();
            }
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_jobs);

        mContext = YourJobsActivity.this;
        prefsUtil = new PrefsUtil(mContext);

        iv_back = findViewById(R.id.imgBack);
        imgAddJob = findViewById(R.id.imgAddJob);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        ROLE = prefsUtil.getRole();

        /*fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();

        if (ROLE.equalsIgnoreCase("Customer")) {
            fragmentOne = new JobListFragment("Pending", 0);
            titleList.add(getString(R.string.pending));
            fragmentList.add(fragmentOne);
        } else {
            fragmentOne = new JobListFragment("New", 0);
            titleList.add(getString(R.string.new_status));
            fragmentList.add(fragmentOne);
        }

        fragmentTwo = new JobListFragment("UpComing", 1);
        titleList.add(getString(R.string.upcomming));
        fragmentList.add(fragmentTwo);

        fragmentThree = new JobListFragment("OnGoing", 2);
        titleList.add(getString(R.string.ongoing));
        fragmentList.add(fragmentThree);

        fragmentFour = new JobListFragment("Completed", 3);
        titleList.add(getString(R.string.completed));
        fragmentList.add(fragmentFour);

        fragmentFive = new JobListFragment("Rejected", 4);
        titleList.add(getString(R.string.rejected));
        fragmentList.add(fragmentFive);

        adapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager);

        iv_back.setOnClickListener(this);
        imgAddJob.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentOne.refreshList();
            }
        }, 300);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int refreshTabNo = intent.getIntExtra("refreshTabNo", -1);

                if (refreshTabNo == 0) {
                    fragmentOne.refreshList();
                } else if (refreshTabNo == 1) {
                    fragmentTwo.refreshList();
                } else if (refreshTabNo == 2) {
                    fragmentThree.refreshList();
                }
            }
        };

        registerReceiver(receiver, new IntentFilter("onTabRefresh"));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    fragmentOne.refreshList();
                } else if (i == 1) {
                    fragmentTwo.refreshList();
                } else if (i == 2) {
                    fragmentThree.refreshList();
                } else if (i == 3) {
                    fragmentFour.refreshList();
                } else if (i == 4) {
                    fragmentFive.refreshList();
                }
                tmpTabPos = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });*/

//        setupAdMob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null)
            unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back) {
            onBackPressed();
        } else if (v == imgAddJob) {
            Toast.makeText(mContext, "Add", Toast.LENGTH_SHORT).show();
            /*Intent intent = new Intent(mContext, CreateJobActivity.class);
            if (viewPager.getCurrentItem() == 0)
                intent.putExtra("isFromJobList", true);
            startActivity(intent);*/
        }
    }

    public class CustomPagerAdapter extends FragmentPagerAdapter {

        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
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
            return titleList.get(position);
        }
    }

    private void setupAdMob() {

        /*View adContainer = findViewById(R.id.adMobView);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        param.addRule(RelativeLayout.CENTER_HORIZONTAL);

        RelativeLayout rel = (RelativeLayout) adContainer;

        AdView mAdView = new AdView(mContext);

        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.admob_unit_id));
        mAdView.setLayoutParams(param);
        rel.addView(mAdView);

//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(new GoogleAdLoader().getAdRequest());

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(mContext, "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });*/
    }
}
