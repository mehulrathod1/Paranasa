package com.ni.parnasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ni.parnasa.R;
import com.ni.parnasa.tmpPojos.RatingPojoItems;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReviewRattingAdapter extends RecyclerView.Adapter<ReviewRattingAdapter.Holder> {

    private LayoutInflater inflater;
    private Context mContext;
    private List<RatingPojoItems> list;

    public ReviewRattingAdapter(Context mContext, List<RatingPojoItems> data) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.list = data;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adpt_review_rating, parent, false);
        return new Holder(view);

    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {

        RatingPojoItems pojoItems = list.get(position);
        holder.txtDate.setText(pojoItems.getCreatedAt());
        holder.txtUserName.setText(pojoItems.getFirstName() + " " + pojoItems.getLastName());
        holder.txtReview.setText(pojoItems.getComments());

        Glide.with(mContext).load(pojoItems.getLogoImage()).into(holder.imgUser);

        try {
            holder.ratingMyReview.setRating(Float.valueOf(pojoItems.getRating()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class Holder extends RecyclerView.ViewHolder {

        private CircleImageView imgUser;
        private TextView txtDate, txtUserName, txtReview;
        private RatingBar ratingMyReview;

        public Holder(View row) {
            super(row);
            imgUser = row.findViewById(R.id.imgUser);
            txtDate = row.findViewById(R.id.txtDate);
            txtUserName = row.findViewById(R.id.txtUserName);
            ratingMyReview = row.findViewById(R.id.ratingMyReview);
            txtReview = row.findViewById(R.id.txtReview);

        }
    }
}
