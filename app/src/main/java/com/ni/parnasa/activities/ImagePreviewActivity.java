package com.ni.parnasa.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.ni.parnasa.R;
import com.ni.parnasa.utils.MultiLanguageUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ImagePreviewActivity extends AppCompatActivity {

    private Context mContext;
    private ImageView imgZoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        mContext = ImagePreviewActivity.this;
        imgZoom = findViewById(R.id.imgPreview);
        imgZoom.setDrawingCacheEnabled(true);

        Glide.with(mContext).asBitmap()
                .load(getIntent().getStringExtra("imagePath"))
                .into(imgZoom);

        imgZoom.setOnTouchListener(new ImageMatrixTouchHandler(mContext));
    }
}
