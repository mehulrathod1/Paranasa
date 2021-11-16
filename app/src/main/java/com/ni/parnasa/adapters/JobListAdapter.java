package com.ni.parnasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ni.parnasa.R;
import com.ni.parnasa.pojos.JobListPojoItem;
import com.ni.parnasa.utils.PrefsUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.Holder> {

    private Context mContext;
    private List<JobListPojoItem> list;
    //    private boolean isNeedToClick = true;
    private OnJobItemClickListener listener;
    private String ROLE = "";

    public interface OnJobItemClickListener {
        void onJobItemClick(int pos);
    }

    public JobListAdapter(Context mContext, List<JobListPojoItem> list, OnJobItemClickListener listener) {
        this.mContext = mContext;
        this.list = list;
        this.listener = listener;
        PrefsUtil prefsUtil = new PrefsUtil(mContext);
        ROLE = prefsUtil.getRole();
    }

   /* public JobListAdapter(Context mContext, List<JobListPojoItem> list, boolean isNeedToClick) {
        this.mContext = mContext;
        this.list = list;
        this.isNeedToClick = isNeedToClick;
    }*/

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.adpt_job, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        JobListPojoItem item = list.get(i);

        holder.txtServiceName.setText(item.getService());
        holder.txtDateTime.setText(item.getJobDate());
//        holder.txtStatus.setText(item.getJobStatus());

        String jobStatus = item.getJobStatus();

        if (ROLE.equalsIgnoreCase("Customer")) {
            holder.txtCustomerName.setText(item.getAssignTo());
            Glide.with(mContext).asBitmap()
                    .load(item.getProfessionalProfile())
                    .into(holder.imgProfile);

            if (jobStatus.equalsIgnoreCase("Completed")) {
                String invoiceStatus = item.getInvoiceStatus();
                if (invoiceStatus.equalsIgnoreCase("Paid")) {
                    holder.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                    holder.txtStatus.setBackgroundResource(R.drawable.drw_border_green);
                    holder.txtStatus.setText(invoiceStatus);
                } else {
                    holder.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorYellow));
                    holder.txtStatus.setBackgroundResource(R.drawable.drw_border_yellow);
                    holder.txtStatus.setText(R.string.payment_pending);
                }
            } else {
                holder.txtStatus.setText(item.getJobStatus());
            }
        } else {
            holder.txtCustomerName.setText(item.getCustomerName());
            holder.txtStatus.setText(item.getJobStatus());
            Glide.with(mContext).asBitmap()
                    .load(item.getCustomerProfile())
                    .into(holder.imgProfile);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView imgProfile;
        private TextView txtCustomerName, txtServiceName, txtDateTime, txtStatus;

        public Holder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onJobItemClick(getAdapterPosition());
                }
            });
        }
    }
}
