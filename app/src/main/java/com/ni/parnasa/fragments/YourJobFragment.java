package com.ni.parnasa.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.adapters.CustomPagerAdapter;
import com.ni.parnasa.utils.FacebookAdLoader;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class
YourJobFragment extends Fragment {

    private PrefsUtil prefsUtil;
//    private BroadcastReceiver receiver;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CustomPagerAdapter adapter;

    private List<Fragment> fragmentList;
    private List<String> titleList;
    private String ROLE = "";

    private JobListFragment fragmentOne, fragmentTwo, fragmentThree, fragmentFour, fragmentFive;

    private int tmpTabPos = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();

        View view = inflater.inflate(R.layout.fragment_your_job, container, false);

        if (getActivity() instanceof CustomerHomeActivity) {
            prefsUtil = ((CustomerHomeActivity) getActivity()).prefsUtil;
        } else {
            prefsUtil = ((ProfessionalHomeActivity) getActivity()).prefsUtil;
        }

        ROLE = prefsUtil.getRole();

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        fragmentList = new ArrayList<>();
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

        adapter = new CustomPagerAdapter(getChildFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentOne.refreshList();
            }
        }, 300);


        /*receiver = new BroadcastReceiver() {
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
        registerReceiver(receiver, new IntentFilter("onTabRefresh"));*/

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
        });


        setupAdMob(view);

        return view;
    }

    public boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    public boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    private FacebookAdLoader facebookAdLoader;

    private void setupAdMob(View view) {

        /** Load Google Ad */
        LinearLayout linlayConteiner = view.findViewById(R.id.adMobView);

        new GoogleAdLoader(getActivity(), getString(R.string.admob_job_list_ad_id),linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });

        /** Load facebook Ad */
        facebookAdLoader = new FacebookAdLoader(getActivity(), linlayConteiner, new FacebookAdLoader.CustomAdListenerFacebook() {
            @Override
            public void onError(Ad aad, AdError adError) {
                Log.e("facebookAd", "onError CODE :" + adError.getErrorCode() + " Message : " + adError.getErrorMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        if (facebookAdLoader != null) {
            facebookAdLoader.destroyAdView();
        }
        super.onDestroy();
    }
}
