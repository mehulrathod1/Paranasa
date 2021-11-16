package com.ni.parnasa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.ni.parnasa.R;
import com.ni.parnasa.models.FilterModel;
import com.ni.parnasa.models.Service_data;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {

    Context context;
    ArrayList<FilterModel> arrayList;
    List<Service_data> data = Collections.emptyList();
//    public static ArrayList<String> array_List = new ArrayList<>();

    public FilterAdapter(Context context, ArrayList<FilterModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MyViewHolder handler = holder;
        final FilterModel dataProvider = arrayList.get(position);

        if (arrayList.get(position).isChecked()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.no_imageavailable)
                .showImageOnFail(R.drawable.no_imageavailable)
                .showImageOnLoading(R.drawable.loader).build();
        imageLoader.displayImage(dataProvider.getImage(), handler.img, options);

//        array_List.clear();
        /*handler.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                array_List.add(dataProvider.getId());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private CheckBox checkbox;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    FilterModel filterModel = arrayList.get(pos);

                    if (filterModel.isChecked()) {
                        filterModel.setChecked(false);
                    } else {
                        filterModel.setChecked(true);
                    }

                    notifyItemChanged(pos);
                }
            });*/

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getAdapterPosition();

                    FilterModel filterModel = arrayList.get(pos);

                    if (isChecked) {
                        filterModel.setChecked(true);
                    } else {
                        filterModel.setChecked(false);
                    }
                }
            });
        }
    }

    public String getCheckedServiceIds() {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isChecked()) {
                builder.append(arrayList.get(i).getId());
                builder.append(",");
            }
        }

        String strFinal = builder.toString();

        return strFinal.substring(0, strFinal.length() - 1);
    }
}
