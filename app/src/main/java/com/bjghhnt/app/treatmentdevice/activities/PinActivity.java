package com.bjghhnt.app.treatmentdevice.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.views.PinView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PinActivity extends AppCompatActivity {

    public static final String INTENT_KEY_TO_RECEIVE = "text";

    public static final int ENTER_PIN = 0;

    public static final int ENTER_OLD_PIN = 1;

    public static final int ENTER_NEW_PIN = 2;

    public static final int ENTER_NEW_PIN_AGAIN = 3;

    public static final int ROUTINE_ENTER_PIN = 0;

    public static final int ROUTINE_CHANGE_PIN = 1;

    @BindView(R.id.pin_view)
    PinView mPinView;

    @BindView(R.id.text_pin_info)
    TextView mTextViewPinInfo;

    private int mRoutineCode;

    @SuppressWarnings("PointlessBooleanExpression")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //in fact, this is not pointless
        //TODO !BuildConfig.MIPS
//        if (!BuildConfig.MIPS) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            setTheme(R.style.AppTheme);
//        }
        setContentView(R.layout.activity_pin);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String startIntent = intent.getStringExtra("START_INTENT");

        mRoutineCode = (int) (getIntent().getExtras()).get(INTENT_KEY_TO_RECEIVE);
        mPinView.setRoutineCode((mRoutineCode == ROUTINE_CHANGE_PIN) ?
                ROUTINE_CHANGE_PIN : ROUTINE_ENTER_PIN);

        Button cancel = (Button) findViewById(R.id.btn_pin_cancel);

        cancel.setOnClickListener(v -> {
            if (startIntent.equals("RELOCK")){
                finish();
            }
            if (startIntent.equals("START_SETTING")){
                finish();
                SettingActivity mSettingActivityInstance = SettingActivity.mSettingActivityInstance;
                if (mSettingActivityInstance != null){
                    mSettingActivityInstance.finish();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        setFinishOnTouchOutside(false);
        if (mRoutineCode == ROUTINE_ENTER_PIN) {
            setText(PinActivity.ENTER_PIN);
        } else if (mRoutineCode == ROUTINE_CHANGE_PIN) {
            setText(PinActivity.ENTER_OLD_PIN);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "口令输入不可跳过", Toast.LENGTH_SHORT).show();
    }


    public void setText(int textCode) {
        switch (textCode) {
            case ENTER_PIN:
                mTextViewPinInfo.setText(getString(R.string.str_enter_pin));
                break;
            case ENTER_OLD_PIN:
                mTextViewPinInfo.setText(getString(R.string.str_enter_old_pin));
                break;
            case ENTER_NEW_PIN:
                mTextViewPinInfo.setText(getString(R.string.str_enter_new_pin));
                break;
            case ENTER_NEW_PIN_AGAIN:
                mTextViewPinInfo.setText(getString(R.string.str_enter_new_pin_again));
                break;
            default:
                mTextViewPinInfo.setText(getString(R.string.str_enter_pin));
        }
    }

    public void finishWithPinChange(@Nullable String newPin) {
        //TODO fix this
        if (newPin != null) {
            ((TreatmentApplication) (getApplication())).setPIN(newPin);
        }
        finish();
    }
}
