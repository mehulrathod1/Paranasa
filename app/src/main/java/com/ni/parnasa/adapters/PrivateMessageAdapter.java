package com.ni.parnasa.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ni.parnasa.R;
import com.ni.parnasa.activities.ChatActivity;
import com.ni.parnasa.pojos.ChatUserDetailPojoItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.Holder> {

    private Context mContext;
    private OnPrivateMsgListener listener;
    private List<ChatUserDetailPojoItem> list;

    public interface OnPrivateMsgListener {
        void onStarClick(boolean isFavorite, int position);
    }

    public PrivateMessageAdapter(Context mContext, OnPrivateMsgListener listener, List<ChatUserDetailPojoItem> list) {
        this.mContext = mContext;
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.adpt_private_message, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        ChatUserDetailPojoItem item = list.get(i);
        holder.txtUserName.setText(item.getFirstName() + " " + item.getLastName());
        holder.txtDate.setText(item.getLastMsg());

        Glide.with(mContext).asBitmap()
                .load(item.getProfile())
                .into(holder.imgProfile);

        if (item.isFavorites()) {
            holder.imgFavorite.setImageResource(R.drawable.ic_star_full);
        } else {
            holder.imgFavorite.setImageResource(R.drawable.ic_star_empty);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView imgProfile, imgFavorite;
        private TextView txtUserName, txtDate;

        public Holder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtDate = itemView.findViewById(R.id.txtDate);

            imgFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onStarClick(list.get(getAdapterPosition()).isFavorites(), getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatUserDetailPojoItem item = list.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("isFromPopup", false);
                    intent.putExtra("receiverId", item.getUserId());
                    intent.putExtra("chatRoomId", item.getChatId());
                    intent.putExtra("userName", item.getFirstName() + " " + item.getLastName());
                    intent.putExtra("phone", item.getMobile());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
