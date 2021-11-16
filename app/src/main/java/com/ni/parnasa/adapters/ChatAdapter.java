package com.ni.parnasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ni.parnasa.R;
import com.ni.parnasa.pojos.ChatHistoryMessagesPojoItem;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ChatHistoryMessagesPojoItem> messagesPojoItemList;
    private String receiverId;
    private int VIEW_IMAGE = 1, VIEW_MSG = 2, VIEW_MIDDLE_DATE = 3;
    private Calendar calendarCurrent;
    private boolean isStartingFlag;

    private OnImageClickedListener listener;

    public interface OnImageClickedListener {
        void onImageClicked(int pos,String image);
    }

    public ChatAdapter(Context mContext, List<ChatHistoryMessagesPojoItem> messagesPojoItemList, String receiverId, OnImageClickedListener listener) {
        this.mContext = mContext;
        this.messagesPojoItemList = messagesPojoItemList;
        this.receiverId = receiverId;
        this.listener = listener;
        calendarCurrent = Calendar.getInstance();
        isStartingFlag = true;
    }

    public void setIsStartingFlag(boolean isStartingFlag) {
        this.isStartingFlag = isStartingFlag;
    }

    @Override
    public int getItemViewType(int position) {
        if (!messagesPojoItemList.get(position).getDateHeader().equals("")) {
            return VIEW_MIDDLE_DATE;
        } else if (!messagesPojoItemList.get(position).getImage().equals("")) {
            return VIEW_IMAGE;
        } else {
            return VIEW_MSG;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == VIEW_IMAGE) {
            return new HolderForImage(LayoutInflater.from(mContext).inflate(R.layout.adpt_chat_img, viewGroup, false));
        } else if (i == VIEW_MIDDLE_DATE) {
            return new HolderForMiddleDate(LayoutInflater.from(mContext).inflate(R.layout.adpt_chat_middle_data, viewGroup, false));
        } else {
            return new HolderForMessage(LayoutInflater.from(mContext).inflate(R.layout.adpt_chat_msg, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        ChatHistoryMessagesPojoItem item = messagesPojoItemList.get(position);

        if (viewHolder instanceof HolderForImage) {

            HolderForImage holder = (HolderForImage) viewHolder;

            if (!item.getUserId().equals(receiverId)) {
                holder.linlaySender.setVisibility(View.VISIBLE);
                holder.linlayReceiver.setVisibility(View.GONE);
                holder.txtSenderDate.setText(item.getCreatedAt());
                Glide.with(mContext).asBitmap()
                        .load(item.getImage())
                        .into(holder.imgSender);
            } else {
                holder.linlayReceiver.setVisibility(View.VISIBLE);
                holder.linlaySender.setVisibility(View.GONE);
                holder.txtReceiverDate.setText(item.getCreatedAt());
                Glide.with(mContext).asBitmap()
                        .load(item.getImage())
                        .into(holder.imgReceiver);
            }
        } else if (viewHolder instanceof HolderForMiddleDate) {

            HolderForMiddleDate holder = (HolderForMiddleDate) viewHolder;

            holder.txtMiddleDate.setText(item.getDateHeader());

        } else {

            HolderForMessage holder = (HolderForMessage) viewHolder;

            if (!item.getUserId().equals(receiverId)) {
                holder.linlaySender.setVisibility(View.VISIBLE);
                holder.linlayReceiver.setVisibility(View.GONE);
                holder.txtSenderMsg.setText(item.getContent());
                holder.txtSenderDate.setText(item.getCreatedAt());
            } else {
                holder.linlayReceiver.setVisibility(View.VISIBLE);
                holder.linlaySender.setVisibility(View.GONE);
                holder.txtReceiverMsg.setText(item.getContent());
                holder.txtReceiverDate.setText(item.getCreatedAt());
            }
        }
    }


    public class HolderForImage extends RecyclerView.ViewHolder {

        //        private CardView cardSender, cardReceiver;
        private LinearLayout linlaySender, linlayReceiver;
        private TextView txtSenderDate, txtReceiverDate;
        private ImageView imgSender, imgReceiver;

        public HolderForImage(@NonNull View itemView) {
            super(itemView);

            imgSender = itemView.findViewById(R.id.imgSender);
            linlaySender = itemView.findViewById(R.id.linlaySender);
            imgReceiver = itemView.findViewById(R.id.imgReceiver);
            linlayReceiver = itemView.findViewById(R.id.linlayReceiver);
            txtSenderDate = itemView.findViewById(R.id.txtSenderDate);
            txtReceiverDate = itemView.findViewById(R.id.txtReceiverDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onImageClicked(getAdapterPosition(),messagesPojoItemList.get(getAdapterPosition()).getImage());
                }
            });
        }
    }

    public class HolderForMessage extends RecyclerView.ViewHolder {

        //        private CardView cardReceiver;
        private LinearLayout linlaySender, linlayReceiver;
        private TextView txtSenderMsg, txtSenderDate, txtReceiverMsg, txtReceiverDate;

        public HolderForMessage(@NonNull View itemView) {
            super(itemView);

            linlaySender = itemView.findViewById(R.id.linlaySender);
            linlayReceiver = itemView.findViewById(R.id.linlayReceiver);
            txtSenderMsg = itemView.findViewById(R.id.txtSenderMsg);
            txtSenderDate = itemView.findViewById(R.id.txtSenderDate);
            txtReceiverMsg = itemView.findViewById(R.id.txtReceiverMsg);
            txtReceiverDate = itemView.findViewById(R.id.txtReceiverDate);
//            txtMiddleDate = itemView.findViewById(R.id.txtMiddleDate);

        }
    }

    public class HolderForMiddleDate extends RecyclerView.ViewHolder {

        private TextView txtMiddleDate;

        public HolderForMiddleDate(@NonNull View itemView) {
            super(itemView);
            txtMiddleDate = itemView.findViewById(R.id.txtMiddleDate);
        }
    }

    @Override
    public int getItemCount() {
        return messagesPojoItemList.size();
    }
}
