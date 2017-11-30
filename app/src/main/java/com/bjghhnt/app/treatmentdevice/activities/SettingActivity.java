package com.bjghhnt.app.treatmentdevice.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.activities.fragment.setting.CasesFragment;
import com.bjghhnt.app.treatmentdevice.activities.fragment.setting.ScreenBrightnessFragment;
import com.bjghhnt.app.treatmentdevice.activities.fragment.setting.VolumeFragment;
import com.bjghhnt.app.treatmentdevice.activities.fragment.setting.WiFiFragment;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.bjghhnt.app.treatmentdevice.utils.JniUtils;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SimpleHandler;
import com.bjghhnt.app.treatmentdevice.views.NoScrollViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 偏好设置界面
 *
 */

public class SettingActivity extends MinorActivity {

    @BindView(R.id.setting_bar)
    Toolbar mSettingBar;
    @BindView(R.id.rb_wifi)
    RadioButton mRbWifi;
    @BindView(R.id.rb_screen)
    RadioButton mRbScreen;
    @BindView(R.id.rb_sound)
    RadioButton mRbSound;
    @BindView(R.id.rb_dev)
    RadioButton mRbDev;
    @BindView(R.id.rb_lock)
    RadioButton mRbLock;
    @BindView(R.id.rb_about)
    RadioButton mRbAbout;
    @BindView(R.id.rb_back)
    RadioButton mRbBack;
    @BindView(R.id.rg_set)
    RadioGroup mRgSet;
    @BindView(R.id.vp_set_details)
    NoScrollViewPager mVpSetDetails;
    @BindView(R.id.tv_time)
    TextView tvTimeSetting;
    private ArrayList<Fragment> mFragmentList;
    @SuppressLint("StaticFieldLeak")
    static SettingActivity mSettingActivityInstance;
    private int checkedRadioButtonId;
    private int editPasswordCount;
    private SharedPreferences sp;
    private String password;
    private final SimpleHandler mSimHandler = new SimpleHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSettingActivityInstance = this;
        ButterKnife.bind(this);
        setSupportActionBar(mSettingBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        sp = getSharedPreferences("PasswordSP", MODE_PRIVATE);
        editPasswordCount = sp.getInt("Edit_Password_count", 0);
        password = CalcPsw();
        Intent intent = new Intent(this, PinActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(PinActivity.INTENT_KEY_TO_RECEIVE,
                        PinActivity.ROUTINE_ENTER_PIN);
        intent.putExtra("START_INTENT","START_SETTING");
        startActivity(intent);
    }

    @Override
    protected SerialHandler setHandler() {

        return mSimHandler;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //更新时间
    @Override
    protected void upDateTime() {
        tvTimeSetting.setText(formatTime);
    }


    private void init() {

        initFragment();
        initRadio();

    }

    private void initRadio() {
        mRgSet.check(R.id.rb_wifi);
        checkedRadioButtonId = mRgSet.getCheckedRadioButtonId();
        mRgSet.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_wifi:
                    mVpSetDetails.setCurrentItem(0);
                    checkedRadioButtonId = mRgSet.getCheckedRadioButtonId();
                    break;
                case R.id.rb_screen:
                    mVpSetDetails.setCurrentItem(1);
                    checkedRadioButtonId = mRgSet.getCheckedRadioButtonId();
                    break;
                case R.id.rb_sound:
                    mVpSetDetails.setCurrentItem(2);
                    checkedRadioButtonId = mRgSet.getCheckedRadioButtonId();
                    break;
                case R.id.rb_lock:

                    Intent intent = new Intent(this, PinActivity.class)
                            .putExtra(PinActivity.INTENT_KEY_TO_RECEIVE,
                                    PinActivity.ROUTINE_CHANGE_PIN);
                    intent.putExtra("START_INTENT","RELOCK");
                    startActivity(intent);
                    mRgSet.check(checkedRadioButtonId);
                    break;
                case R.id.rb_dev:

                    showPasswordDialog();

                    mRgSet.check(checkedRadioButtonId);
                    break;
                case R.id.rb_about:
                    mVpSetDetails.setCurrentItem(3);
                    break;
                case R.id.rb_back:
                    finish();
                    break;
            }
        });
    }

    private void showPasswordDialog() {
        EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("管理员密码")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", (dialog, which) -> {
                    if (et.getText().toString().equals(password)){
                        sp.edit().putInt("Edit_Password_count", ++editPasswordCount).apply();
                        password = CalcPsw();
                        startActivity(new Intent(this,PrefsActivity.class));
                    }else {
                        Toast.makeText(this, "密码输入错误，请重新输入", Toast.LENGTH_SHORT).show();
                        et.setText("");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new WiFiFragment());
        mFragmentList.add(new ScreenBrightnessFragment());
        mFragmentList.add(new VolumeFragment());
        mFragmentList.add(new CasesFragment());
        mVpSetDetails.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String CalcPsw(){
        long supperPassword = JniUtils.getSupperPassword(editPasswordCount);
        String psw = Long.toString(supperPassword);
        if (psw.length() > 8){
            psw = psw.substring(psw.length() - 8, psw.length());
        } else if (psw.length() < 8){
            for (int j = 0; j < 9 - psw.length(); j++) {
                psw += editPasswordCount;
            }
        }
        System.out.println(psw);
        return psw;
    }

}
