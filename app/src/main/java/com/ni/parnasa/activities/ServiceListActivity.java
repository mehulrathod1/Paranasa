package com.ni.parnasa.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.adapters.Service_adapter;
import com.ni.parnasa.models.Service_data;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
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
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ServiceListActivity extends AppCompatActivity implements Service_adapter.OnServiceSelectedListener {

    private Context mContext;
//    private FirebaseAuth firebaseAuth;
//    private FirebaseStorage storage;
//    private StorageReference storageRef;
//    private FirebaseFirestore db;

    private List<Service_data> serviceData;

    private RecyclerView rv_list;
    private RelativeLayout relNoDataFound;
    private Service_adapter service_adapter;
    private ProgressDialog progressDialog;
    private EditText edtSearch;
    private TextView txtMsg, txtAddService;
    private ImageView imgClear;

    private String UserId;
    private String icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = ServiceListActivity.this;

        initViews();

//        db = FirebaseFirestore.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//        storage = FirebaseStorage.getInstance();
//        storageRef = storage.getReference();
//        UserId = firebaseAuth.getCurrentUser().getUid();

        /*db.collection("services")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String service_id = document.getString("service_id");
                                String service_name = document.getString("service_name");
                                final String service_icon = document.getString("service_icon");
                                String keyword = document.getString("keyword");
                                Service_data service_data=new Service_data();
                                //StorageReference storageRef = storage.getReferenceFromUrl("gs://pickmeapp-195313.appspot.com").child("service_icons").child("Blue").child(service_icon);
                                //String icon= String.valueOf(storageRef);
                                System.out.println("sduttasdaf"+service_name+service_id+service_icon);

                                storageRef.child("service_icons/Blue/"+service_icon).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Got the download URL for 'users/me/profile.png'
                                        String image=uri.toString();
                                        icon=image;
                                        System.out.println("sdutta"+service_icon+image);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                                service_data.service_name=service_name;
                                service_data.service_icon=icon;
                                service_data.service_id=service_id;
                                service_data.keyword=keyword;
                                Service_data.add(service_data);



                            }
                            rv_list = (RecyclerView)findViewById(R.id.recycler_search);

                            service_adapter = new Service_adapter(mContext, Service_data);
                            rv_list.setAdapter(service_adapter);
                            rv_list.setLayoutManager(new LinearLayoutManager(mContext));

                        }
                    }
                });
*/

    }

    private void initViews() {
        rv_list = (RecyclerView) findViewById(R.id.recycler_search);
        relNoDataFound = findViewById(R.id.relNoDataFound);
        txtMsg = findViewById(R.id.txtMsg);
        txtAddService = findViewById(R.id.txtAddService);

        edtSearch = findViewById(R.id.edtSearch);
        imgClear = findViewById(R.id.imgClear);

        progressDialog = new ProgressDialog(mContext);

        serviceData = new ArrayList<>();
        service_adapter = new Service_adapter(mContext, serviceData, this);
        rv_list.setAdapter(service_adapter);
        rv_list.setLayoutManager(new LinearLayoutManager(mContext));

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        new apiCallForGetServiceList().execute();
    }

    @Override
    public void onServiceSelected(String serviceId) {
        for (int i = 0; i < serviceData.size(); i++) {
            if (serviceData.get(i).getService_id().equals(serviceId)) {
                Intent intent = new Intent();
                intent.putExtra("sId", serviceId);
                intent.putExtra("sName", serviceData.get(i).getService_name());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }


    public class apiCallForGetServiceList extends AsyncTask<String, Void, String> {

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
                e.printStackTrace();
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
                Toast.makeText(getApplicationContext(), "Network Error !", Toast.LENGTH_LONG).show();
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
                            String service_icon = c.getString("service_icon");
                            String keyword = c.getString("keyword");
//                            String statuss = c.getString("status");
//                            String created_at = c.getString("created_at");

                            Service_data service_data = new Service_data(service_id, service_name, service_icon, keyword);
                            serviceData.add(service_data);
                        }

                        service_adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(mContext, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
