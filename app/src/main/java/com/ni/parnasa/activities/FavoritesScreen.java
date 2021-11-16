package com.ni.parnasa.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.adapters.FavoriteAdapter;
import com.ni.parnasa.pojos.ChatRoomPojo;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.pojos.FavoritePojo;
import com.ni.parnasa.pojos.FavoritePojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FavoritesScreen extends AppCompatActivity implements FavoriteAdapter.OnFavoriteActionListener {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private ImageView img_back;
    private RecyclerView recycler_view;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    private int REQ_CALL = 74;

    private List<FavoritePojoItem> pojoItem;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_screen);

        mContext = FavoritesScreen.this;

        init();
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

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.img_back:
                    finish();
                    break;
            }
        }
    };

    private void init() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        img_back = (ImageView) findViewById(R.id.img_back);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);

        // prepareList();
        progressDialog = new ProgressDialog(mContext);
        prefsUtil = new PrefsUtil(mContext);

        pojoItem = new ArrayList<>();
        adapter = new FavoriteAdapter(mContext, pojoItem, this);
        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);

        img_back.setOnClickListener(clickListener);

//        new Service_lst().execute();

        apiCallForGetFavUser();

        setupAdMob();
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

        new ParseJSON(mContext, BaseUrl.favoriteUserList, param, value, FavoritePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    FavoritePojo pojo = (FavoritePojo) obj;
                    pojoItem.addAll(pojo.getFavoriteSubPojo().getFavoritePojoItem());
                    if (pojoItem.size() > 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String strMobile = "";

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
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FavoritesScreen.this, new String[]{Manifest.permission.CALL_PHONE}, REQ_CALL);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strMobile));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPhoneCallPermission();
        } else {
            Toast.makeText(mContext, R.string.permission_denied, Toast.LENGTH_SHORT).show();
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

        new ParseJSON(mContext, (isFavorite ? BaseUrl.unfavoriteUser : BaseUrl.favoriteUser), param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo pojo = (CommonPojo) obj;
                    pojoItem.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(mContext, pojo.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
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

        new ParseJSON(mContext, BaseUrl.createChatRoom, param, value, ChatRoomPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    ChatRoomPojo pojo = (ChatRoomPojo) obj;

//                    ChatRoomPojoReceiver pojoReceiver = pojo.getChatRoomPojoItem().getChatRoomPojoReceiver();

                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("isFromPopup", false);
                    intent.putExtra("receiverId", strId);
                    intent.putExtra("chatRoomId", "" + pojo.getChatRoomPojoItem().getChatRoomId());
                    intent.putExtra("userName", strName);
                    intent.putExtra("phone", strMobile);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class Service_lst extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage(getString(R.string.please_wait));
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

                MyLogs.e("URL", BaseUrl.URL + "services/get_all");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "services/get_all")
                        .post(body)
                        .build();

                okhttp3.Response response = client.newCall(request).execute();
                Response1 = response.body().string();
                return Response1;

            } catch (IOException e) {

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
                Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_LONG).show();
            } else {

                try {
                    JSONObject jsonObject = new JSONObject(s);

                    MyLogs.e("Response", String.valueOf(jsonObject));

                    String status = jsonObject.getString("status");

                    if (status.equals("Yes")) {

                        JSONArray data = jsonObject.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            String service_id = c.getString("service_id");
                            String service_name = c.getString("service_name");
                            String keyword = c.getString("keyword");

                            String service_icon = c.getString("service_icon");
                            String statuss = c.getString("status");
                            String created_at = c.getString("created_at");

                          /*  Service_data service_data = new Service_data();

                            service_data.service_name = service_name;
                            service_data.service_icon = service_icon;
                            service_data.service_id = service_id;
                            service_data.keyword = keyword;
                            Service_data.add(service_data);*/
                            //arrayList.add(new FavoriteModel("", "", "", ""));

                            // arrayList.add(new FilterModel(service_id,service_icon,service_name));

                        }

                        //service_adapter = new FilterAdapter(FilterScreen.this, Service_data);
                       /* FilterAdapter adapter = new FilterAdapter(getApplicationContext(),arrayList);
                        mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
                        recycler_view.setLayoutManager(mLayoutManager);
                        recycler_view.addItemDecoration(new FilterScreen.GridSpacingItemDecoration(3, dpToPx(10), true));
                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                        recycler_view.setAdapter(adapter);*/

                       /* FavoriteAdapter adapter = new FavoriteAdapter(getApplicationContext(), arrayList);
                        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, true));
                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                        recycler_view.setAdapter(adapter);*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
