package com.ni.parnasa.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.professional.AddServiceActivity;
import com.ni.parnasa.activities.professional.ManageServicesActivity;
import com.ni.parnasa.pojos.ManageServicePojo;
import com.ni.parnasa.utils.ApplicationController;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ManageServiceAdapter extends RecyclerView.Adapter<ManageServiceAdapter.Holder> {

    private Context mContext;
    private List<ManageServicePojo> list;
    private LinearLayoutManager linearLayoutManager;

    public ManageServiceAdapter(Context mContext, List<ManageServicePojo> list) {
        this.mContext = mContext;
        this.list = list;
        linearLayoutManager = new LinearLayoutManager(mContext);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.adpt_manage_service, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        ManageServicePojo item = list.get(i);
        holder.txtServiceName.setText(item.getName());
        holder.txtServicePrice.setText(item.getPrice());
        holder.rvKeyword.setHasFixedSize(true);

        holder.rvKeyword.setAdapter(new ChipsAdapter(mContext, item.getKeyword()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView txtServiceName, txtServicePrice;
        private ImageView imgEdit;
        private RecyclerView rvKeyword;

        public Holder(@NonNull View itemView) {
            super(itemView);

            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            txtServicePrice = itemView.findViewById(R.id.txtServicePrice);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            rvKeyword = itemView.findViewById(R.id.rvKeyword);

            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
            flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
            flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
            rvKeyword.setLayoutManager(flexboxLayoutManager);

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Intent intent = new Intent(mContext, AddServiceActivity.class);
                    intent.putExtra("isFromEdit", true);
                    intent.putExtra("serviceName", list.get(pos).getName());
                    intent.putExtra("servicePrice", list.get(pos).getPrice());
                    ((ApplicationController) ((ManageServicesActivity) mContext).getApplication()).keywordList.addAll(list.get(pos).getKeyword());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
