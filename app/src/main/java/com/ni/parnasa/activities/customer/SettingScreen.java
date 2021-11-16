package com.ni.parnasa.activities.customer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;



import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.MultiLanguageUtils;

public class SettingScreen extends AppCompatActivity {

    ProgressBar progressBar;
    TextView txt_logout;
    ImageView img_back,img_profile;
    EditText edt_email,edt_pass,edt_first_name,edt_last_name,edt_phone_no;
    RadioGroup myRadioGroup;
    RadioButton male,female;
    Button btn_edit,btn_save;
    Spinner spin_code,spin_country,spin_date,spin_month,spin_year,spin_language;
    CheckBox check_term;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_screen);

        init();
    }

    private void init() {

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        txt_logout = (TextView)findViewById(R.id.txt_logout);
        spin_code = (Spinner) findViewById(R.id.spin_code);
        spin_country = (Spinner) findViewById(R.id.spin_country);
        spin_date = (Spinner) findViewById(R.id.spin_date);
        spin_month = (Spinner) findViewById(R.id.spin_month);
        spin_language = (Spinner) findViewById(R.id.spin_language);
        spin_year = (Spinner) findViewById(R.id.spin_year);
        check_term = (CheckBox) findViewById(R.id.check_term);
        myRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_profile = (ImageView) findViewById(R.id.im_profile);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_save = (Button) findViewById(R.id.btn_save);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_pass = (EditText) findViewById(R.id.edt_pass);
        edt_last_name = (EditText) findViewById(R.id.edt_last_name);
        edt_first_name = (EditText) findViewById(R.id.edt_first_name);
        edt_phone_no = (EditText) findViewById(R.id.edt_phone_no);
    }
}
