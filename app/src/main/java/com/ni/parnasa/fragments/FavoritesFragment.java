package com.ni.parnasa.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.ChatActivity;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.adapters.FavoriteAdapter;
import com.ni.parnasa.pojos.ChatRoomPojo;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.FavoritePojo;
import com.ni.parnasa.pojos.FavoritePojoItem;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesFragment extends Fragment implements FavoriteAdapter.OnFavoriteActionListener {

    private PrefsUtil prefsUtil;

    private RecyclerView recycler_view;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    private int REQ_CALL = 74;
    String strMobile = "";

    private List<FavoritePojoItem> pojoItem;
    private FavoriteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recycler_view = view.findViewById(R.id.recycler_view);

        progressDialog = new ProgressDialog(getActivity());
        if (getActivity() instanceof CustomerHomeActivity) {
            prefsUtil = ((CustomerHomeActivity) getActivity()).prefsUtil;
        } else {
            prefsUtil = ((ProfessionalHomeActivity) getActivity()).prefsUtil;
        }

        pojoItem = new ArrayList<>();
        adapter = new FavoriteAdapter(getActivity(), pojoItem, this);

        if (!prefsUtil.getRole().equalsIgnoreCase("Customer"))
            adapter.setCustomer(true);

        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);

        apiCallForGetFavUser();

        setupAdMob(view);

        return view;
    }

    private void apiCallForGetFavUser() {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        new ParseJSON(getActivity(), BaseUrl.favoriteUserList, param, value, FavoritePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    FavoritePojo pojo = (FavoritePojo) obj;
                    pojoItem.addAll(pojo.getFavoriteSubPojo().getFavoritePojoItem());
                    if (pojoItem.size() > 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
                    }
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

        new GoogleAdLoader(getActivity(), getString(R.string.admob_favorite_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
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

    @Override
    public void onFavoriteCall(int position) {
        strMobile = pojoItem.get(position).getMobileNumber();

        checkPhoneCallPermission();
    }

    @Override
    public void onFavoriteChat(int position) {
        FavoritePojoItem item = pojoItem.get(position);
//        startActivity(new Intent(mContext, ChatActivity.class));
        apiCallForCreateChatRoom(item.getUserId(), item.getFirstName() + " " + item.getLastName(), item.getMobileNumber());

    }

    @Override
    public void onUnfavorite(int position) {
        apiCallForFavUnfav(true, position);
    }

    private void checkPhoneCallPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQ_CALL);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strMobile));
            startActivity(intent);
        }
    }

    private void apiCallForFavUnfav(boolean isFavorite, final int position) {
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
            value.add(pojoItem.get(position).getUserId());
        } else {
            param.add("favorite_user_id");
            value.add(pojoItem.get(position).getUserId());
        }

        new ParseJSON(getActivity(), (isFavorite ? BaseUrl.unfavoriteUser : BaseUrl.favoriteUser), param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    pojoItem.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getActivity(), pojo.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void apiCallForCreateChatRoom(final String strId, final String strName, final String strMobile) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("sender_id");
        value.add(prefsUtil.GetUserID());

        param.add("reciever_id");
        value.add(strId);

        new ParseJSON(getActivity(), BaseUrl.createChatRoom, param, value, ChatRoomPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ChatRoomPojo pojo = (ChatRoomPojo) obj;

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("isFromPopup", false);
                    intent.putExtra("receiverId", strId);
                    intent.putExtra("chatRoomId", "" + pojo.getChatRoomPojoItem().getChatRoomId());
                    intent.putExtra("userName", strName);
                    intent.putExtra("phone", strMobile);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
