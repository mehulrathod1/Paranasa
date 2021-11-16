package com.ni.parnasa.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.JobDetailActivity;
import com.ni.parnasa.adapters.JobListAdapter;
import com.ni.parnasa.pojos.JobListPojo;
import com.ni.parnasa.pojos.JobListPojoItem;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyDateUtil;
import com.ni.parnasa.utils.PrefsUtil;

@SuppressLint("ValidFragment")
public class JobListFragment extends Fragment implements JobListAdapter.OnJobItemClickListener {

    private RecyclerView rvJoblist;
    private TextView txtNoJobFound;
    private JobListAdapter adapter;
    private List<JobListPojoItem> list;

    private String TAG;
    private int TAB_NO = -1;

    private PrefsUtil prefsUtil;

    public JobListFragment() {
    }

    public JobListFragment(String tag, int TabNo) {
        TAG = tag;
        TAB_NO = TabNo;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        Log.w("ZZZ", "onResume " + TAG);
    }*/


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);

        prefsUtil = new PrefsUtil(getActivity());

        rvJoblist = view.findViewById(R.id.rvJoblist);
        txtNoJobFound = view.findViewById(R.id.txtNoJobFound);

        rvJoblist.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();

        adapter = new JobListAdapter(getActivity(), list, this);

        rvJoblist.setAdapter(adapter);

        return view;
    }

    public void refreshList() {
        try {
            apiCallForGetList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apiCallForGetList() throws NullPointerException {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("filter_by_job_status"); // filter_by_job_status=(Completed, Waiting, Rejected, NotAssigned, OnGoing), filter_by_customer_id=  enter Costomer id
        if (TAG.equalsIgnoreCase("Pending")) {
            value.add("Waiting");
        } else if (TAG.equalsIgnoreCase("New")) {
            value.add("Waiting");
        } else if (TAG.equalsIgnoreCase("UpComing")) {
            value.add("Accepted");
        } else if (TAG.equalsIgnoreCase("OnGoing")) {
            value.add("On Going");
        } else if (TAG.equalsIgnoreCase("Completed")) {
            value.add("Completed");
        } else if (TAG.equalsIgnoreCase("Rejected")) {
            value.add("Rejected");
        } else {
            value.add("");
        }

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        new ParseJSON(getActivity(), (prefsUtil.getRole().equalsIgnoreCase("Customer") ? BaseUrl.customerJobList : BaseUrl.soleJobList), param, value, JobListPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    JobListPojo pojo = (JobListPojo) obj;

                    int size = pojo.getJobListPojoItem().size();

                    list.clear();
                    adapter.notifyDataSetChanged();

                    if (size > 0) {
                        rvJoblist.setVisibility(View.VISIBLE);
                        txtNoJobFound.setVisibility(View.GONE);
                        for (int i = 0; i < size; i++) {
                            JobListPojoItem item = pojo.getJobListPojoItem().get(i);
                            try {
                                String[] tmp = MyDateUtil.getInstance().formateDateTime2(item.getJobDate());
                                item.setJobDate(tmp[0] + " " + tmp[1]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        rvJoblist.setVisibility(View.GONE);
                        txtNoJobFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    rvJoblist.setVisibility(View.GONE);
                    txtNoJobFound.setVisibility(View.VISIBLE);
//                    Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onJobItemClick(int pos) {
//        if (!TAG.equalsIgnoreCase("Pending")) {
        Intent intent = new Intent(getActivity(), JobDetailActivity.class);
        intent.putExtra("jobId", list.get(pos).getId());
        intent.putExtra("tabNo", TAB_NO);
        getActivity().startActivity(intent);
//        }
    }
}
