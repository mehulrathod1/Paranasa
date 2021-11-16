package com.ni.parnasa.AsyncTaskUtils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;

import com.ni.parnasa.R;

public class CustomProgressDialog extends Dialog {

//    private Context mContext;

    public CustomProgressDialog(@NonNull Context context) {
        super(context);
//        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.layout_custom_prograss_dialog);
        ProgressBar progressBar = findViewById(R.id.progress);

//        progressBar.getIndeterminateDrawable().setColorFilter(Color.CYAN, android.graphics.PorterDuff.Mode.MULTIPLY);
//        progressBar.getProgressDrawable().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN);
        //Glide.with(mContext).load(R.raw.loading1).into(ivLoader);
        setCancelable(false);
//        d.show();
    }
}
