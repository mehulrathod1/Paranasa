package com.ni.parnasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ni.parnasa.R;
import com.ni.parnasa.tmpPojos.GetAllServicePojoItem;

public class ChipsAdapterForSignup extends RecyclerView.Adapter<ChipsAdapterForSignup.Holder> {

    private Context mContext;
    private List<GetAllServicePojoItem> list;
    //    private OnChipRemoveListener listener = null;
    private boolean isRequiredRemove = false;

   /* public interface OnChipRemoveListener {
        void onChipRemove(int position);
    }*/

   /* public ChipsAdapterForSignup(Context mContext, List<String> list, OnChipRemoveListener listener) {
        this.mContext = mContext;
        this.list = list;
        this.listener = listener;
    }*/

    /*public ChipsAdapterForSignup(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
    }*/

    public ChipsAdapterForSignup(Context mContext, List<GetAllServicePojoItem> list, boolean isRequiredRemove) {
        this.mContext = mContext;
        this.list = list;
        this.isRequiredRemove = isRequiredRemove;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.adpt_chips, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.txtChips.setText(list.get(i).getServiceName());
        if (isRequiredRemove) {
            holder.imgRemove.setVisibility(View.VISIBLE);
        } else {
            holder.imgRemove.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView txtChips;
        private ImageView imgRemove;

        public Holder(@NonNull View itemView) {
            super(itemView);

            txtChips = itemView.findViewById(R.id.txtChips);
            imgRemove = itemView.findViewById(R.id.imgRemove);

            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    list.remove(pos);
                    notifyItemRemoved(pos);

                    /*if (listener != null) {
                        listener.onChipRemove(pos);
                    } else {
                        list.remove(pos);
                        notifyItemRemoved(pos);
                    }*/
                }
            });
        }
    }
}
