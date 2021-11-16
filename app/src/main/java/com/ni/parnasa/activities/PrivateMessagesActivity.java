package com.ni.parnasa.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.adapters.PrivateMessageAdapter;
import com.ni.parnasa.pojos.ChatUserDetailPojoItem;
import com.ni.parnasa.pojos.ChatUserPojo;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PrivateMessagesActivity extends AppCompatActivity implements View.OnClickListener, PrivateMessageAdapter.OnPrivateMsgListener {

    private Context mContext;
    private PrefsUtil prefsUtil;
    private ImageView imgBack;
    private RecyclerView rvSearchResult;
    private PrivateMessageAdapter adapter;
    private List<ChatUserDetailPojoItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_messages);

        mContext = PrivateMessagesActivity.this;

        init();
    }

    private void init() {

        prefsUtil = new PrefsUtil(mContext);

        imgBack = findViewById(R.id.imgBack);

        rvSearchResult = findViewById(R.id.rvSearchResult);

        rvSearchResult.setLayoutManager(new LinearLayoutManager(mContext));
        list = new ArrayList<>();
        adapter = new PrivateMessageAdapter(mContext, this, list);
        rvSearchResult.setAdapter(adapter);

        imgBack.setOnClickListener(this);

        setupAdMob();
    }

    @Override
    protected void onResume() {
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

        new ParseJSON(mContext, BaseUrl.myChatRoom, param, value, ChatUserPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ChatUserPojo chatUserPojo = (ChatUserPojo) obj;

                    list.clear();

                    list.addAll(chatUserPojo.getChatUserItemPojo().getChatUserDetailPojo());

                    if (list.size() > 0)
                        adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        }
    }

    @Override
    public void onStarClick(boolean isFavorited, int position) {
        apiCallForFavUnfav(isFavorited, position);
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

        new ParseJSON(mContext, (isFavorited ? BaseUrl.unfavoriteUser : BaseUrl.favoriteUser), param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
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

                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
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
