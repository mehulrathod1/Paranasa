package com.ni.parnasa.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.adapters.FilterAdapter;
import com.ni.parnasa.models.FilterModel;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FilterScreen extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    ImageView img_back;
    RecyclerView recycler_view;
    ProgressBar progressBar;
    CheckBox checkbox;
    RecyclerView.LayoutManager mLayoutManager;
    Button btn_save_conti;
    ArrayList<FilterModel> arrayList = new ArrayList<>();
    ProgressDialog progressDialog;
    PrefsUtil prefsUtil;
    String st_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_screen);

        mContext = FilterScreen.this;
        prefsUtil = new PrefsUtil(mContext);

        init();
    }

    private void init() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        img_back = (ImageView) findViewById(R.id.img_back);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        btn_save_conti = (Button) findViewById(R.id.btn_save_conti);

        progressDialog = new ProgressDialog(FilterScreen.this);

        img_back.setOnClickListener(this);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                FilterScreen.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        new apiCallForGetAllService().execute();

//        prepareList();
    }

    @Override
    public void onClick(View v) {
        if (v == img_back) {
            onBackPressed();
        }
    }

    private void prepareList() {
       /* for(int i = 0; i<99;i++){
            arrayList.add(new FilterModel("","",""));
        }
        FilterAdapter adapter = new FilterAdapter(getApplicationContext(),arrayList);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);*/

    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public class apiCallForGetAllService extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
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

                    MyLogs.e("service_return", String.valueOf(jsonObject));

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
                            arrayList.add(new FilterModel(service_id, service_icon, service_name));

                        }

                        //service_adapter = new FilterAdapter(FilterScreen.this, Service_data);
                        final FilterAdapter adapter = new FilterAdapter(getApplicationContext(), arrayList);
                        mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
                        recycler_view.setLayoutManager(mLayoutManager);
                        recycler_view.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                        recycler_view.setAdapter(adapter);

                        btn_save_conti.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                st_ids = FilterAdapter.array_List.toString();
                                st_ids = adapter.getCheckedServiceIds();
//                                st_ids = st_ids.replaceAll("\\[", "").replaceAll("\\]", "");

                                new apiCallForApplyFilter().execute();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class apiCallForApplyFilter extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // swipeRefreshLayout.setRefreshing(true);
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
                request_main.put("user_id", prefsUtil.GetUserID());
                request_main.put("services_id", st_ids);

                MyLogs.e("URL", BaseUrl.URL + "services/filter_data");
                MyLogs.e("PARAM", String.valueOf(request_main));

                RequestBody body = RequestBody.create(JSON, request_main.toString());

                Request request = new Request.Builder()
                        .url(BaseUrl.URL + "services/filter_data")
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
                    String status = jsonObject.getString("status");

                    MyLogs.e("service_return", String.valueOf(jsonObject));

                    if (status.equals("Yes")) {

//                        JSONArray data = jsonObject.getJSONArray("data");


                        /*Intent intent = new Intent();
                        intent.putExtra("data", jsonObject.toString());
                        setResult(Activity.RESULT_OK, intent);
                        finish();*/

                        /*for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            String lat = c.getString("lat");
                            String lng = c.getString("lng");
                            String service_icon = c.getString("service_icon");
                        }*/
                        /*Intent intent = new Intent(FilterScreen.this, ProfessionalHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
