package com.ni.parnasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ni.parnasa.R;
import com.ni.parnasa.models.Service_data;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class Service_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private List<Service_data> data;

    private OnServiceSelectedListener listener;
//    public static ArrayList<String> m_list_image = new ArrayList<String>();

    public interface OnServiceSelectedListener {
        void onServiceSelected(String pos);
    }


    public Service_adapter(Context mContext, List<Service_data> data, OnServiceSelectedListener listener) {

        inflater = LayoutInflater.from(mContext);
        this.data = data;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.service_item, parent, false);
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MyHolder handler = (MyHolder) holder;
        final Service_data dataProvider = data.get(position);

        handler.mountain_name.setText(dataProvider.service_name);

        handler.mountain_hight.setText(dataProvider.keyword);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.no_imageavailable)
                .showImageOnFail(R.drawable.no_imageavailable)
                .showImageOnLoading(R.drawable.loader).build();
        imageLoader.displayImage(dataProvider.service_icon, handler.mountain_icon, options);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        LinearLayout List_click;
        ImageView mountain_icon;
        TextView mountain_name, mountain_hight;
//        TextView image_name, image_height;

        public MyHolder(View row) {
            super(row);

            List_click = (LinearLayout) row.findViewById(R.id.list_click);
            mountain_icon = (ImageView) row.findViewById(R.id.mountain_icon);
            mountain_name = (TextView) row.findViewById(R.id.mountain_name);
            mountain_hight = (TextView) row.findViewById(R.id.mountain_height);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onServiceSelected(data.get(getAdapterPosition()).getService_id());
                }
            });
        }
    }
}
