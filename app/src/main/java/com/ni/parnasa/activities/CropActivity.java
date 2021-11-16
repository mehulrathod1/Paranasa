package com.ni.parnasa.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.ApplicationController;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.isseiaoki.simplecropview.CropImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CropActivity extends AppCompatActivity {

    CropImageView cropImageView;
    Bitmap image;

    private Toolbar toolbar;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        /*titleText = (TextView) findViewById(R.id.txtTitle);
        titleText.setText("Crop");*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = ((ApplicationController) getApplication()).img;
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        try {
            String cropRatio = getIntent().getStringExtra("cropRatio");
            if (cropRatio.equals("app")) {
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
//                cropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
            } else if (cropRatio.equals("web")) {
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);
            } else {
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
            }
        } catch (Exception e) {
            cropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);
            e.printStackTrace();
        }

        cropImageView.setImageBitmap(image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crop_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_done:
                Bitmap cropped_image = cropImageView.getCroppedBitmap();
                ((ApplicationController) getApplication()).cropped = cropped_image;
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
