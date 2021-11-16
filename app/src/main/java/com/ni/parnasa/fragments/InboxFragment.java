package com.ni.parnasa.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.adapters.PrivateMessageAdapter;
import com.ni.parnasa.pojos.ChatUserDetailPojoItem;
import com.ni.parnasa.pojos.ChatUserPojo;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.FacebookAdLoader;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InboxFragment extends Fragment implements PrivateMessageAdapter.OnPrivateMsgListener {

    private RecyclerView rvSearchResult;
    private PrivateMessageAdapter adapter;
    private List<ChatUserDetailPojoItem> list;
    private PrefsUtil prefsUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        rvSearchResult = view.findViewById(R.id.rvSearchResult);

        if (getActivity() instanceof CustomerHomeActivity) {
            prefsUtil = ((CustomerHomeActivity) getActivity()).prefsUtil;
        } else {
            prefsUtil = ((ProfessionalHomeActivity) getActivity()).prefsUtil;
        }

        rvSearchResult.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        adapter = new PrivateMessageAdapter(getActivity(), this, list);
        rvSearchResult.setAdapter(adapter);

        setupAdMob(view);

        return view;
    }

    @Override
    public void onStarClick(boolean isFavorited, int position) {
        apiCallForFavUnfav(isFavorited, position);
    }

    @Override
    public void onResume() {
        super.onResume();
        apiCallForGetList();
    }

    private void apiCallForGetList() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        new ParseJSON(getActivity(), BaseUrl.myChatRoom, param, value, ChatUserPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ChatUserPojo chatUserPojo = (ChatUserPojo) obj;

                    list.clear();

                    list.addAll(chatUserPojo.getChatUserItemPojo().getChatUserDetailPojo());

                    if (list.size() > 0)
                        adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void apiCallForFavUnfav(final boolean isFavorited, final int position) {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        if (isFavorited) {
            param.add("unfavorite_user_id");
            value.add(list.get(position).getUserId());
        } else {
            param.add("favorite_user_id");
            value.add(list.get(position).getUserId());
        }

        new ParseJSON(getActivity(), (isFavorited ? BaseUrl.unfavoriteUser : BaseUrl.favoriteUser), param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    if (isFavorited) {
                        list.get(position).setFavorites(false);
                    } else {
                        list.get(position).setFavorites(true);
                    }
                    adapter.notifyDataSetChanged();

                    Toast.makeText(getActivity(), pojo.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private FacebookAdLoader facebookAdLoader;

    private void setupAdMob(View view) {

        /** Load Google Ad */
        LinearLayout linlayConteiner = view.findViewById(R.id.adMobView);

        new GoogleAdLoader(getActivity(), getString(R.string.admob_inbox_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
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
