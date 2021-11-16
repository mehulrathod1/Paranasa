package com.ni.parnasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ni.parnasa.R;
import com.ni.parnasa.pojos.FavoritePojoItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private Context mContext;
    private List<FavoritePojoItem> pojoItem;
    private OnFavoriteActionListener listener;
    private boolean isCustomer = false;

    public interface OnFavoriteActionListener {
        void onFavoriteCall(int position);

        void onFavoriteChat(int position);

        void onUnfavorite(int position);
    }

    public void setCustomer(boolean isCustomer) {
        this.isCustomer = isCustomer;
    }

    public FavoriteAdapter(Context context, List<FavoritePojoItem> pojoItem, OnFavoriteActionListener listener) {
        this.mContext = context;
        this.pojoItem = pojoItem;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favorite_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        FavoritePojoItem item = pojoItem.get(position);

        holder.txtProfName.setText(item.getFirstName() + " " + item.getLastName());
        holder.txtCompanyName.setText(item.getCompanyName());
        holder.txtLocation.setText(item.getLocation());

        Glide.with(mContext)
                .load(item.getProfileImage())
                .into(holder.imgProfile);


        if (isCustomer) {
            holder.linlayCompany.setVisibility(View.GONE);
        } else {
            holder.linlayCompany.setVisibility(View.VISIBLE);
        }

        try {
            holder.ratingBar.setRating(item.getTotal_average_rating());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return pojoItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProfile;
        private RatingBar ratingBar;
        private LinearLayout linlayPhone, linlayChat, linlayUnfav, linlayCompany;
        private TextView txtProfName, txtCompanyName, txtLocation;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            txtProfName = itemView.findViewById(R.id.txtProfName);
            txtCompanyName = itemView.findViewById(R.id.txtCompanyName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            linlayPhone = itemView.findViewById(R.id.linlayPhone);
            linlayChat = itemView.findViewById(R.id.linlayChat);
            linlayUnfav = itemView.findViewById(R.id.linlayUnfav);
            linlayCompany = itemView.findViewById(R.id.linlayCompany);

            linlayPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFavoriteCall(getAdapterPosition());
                }
            });

            linlayChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFavoriteChat(getAdapterPosition());
                }
            });

            linlayUnfav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUnfavorite(getAdapterPosition());
                }
            });
        }
    }
}
