package com.bjghhnt.app.treatmentdevice.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.interfaces.SerialConnectable;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.bjghhnt.app.treatmentdevice.utils.SerialportControler;
//import com.bjghhnt.app.treatmentdevice.utils.serials.RealSerialPortImpl;
import com.bjghhnt.app.treatmentdevice.utils.serials.RealSerialPortImpl;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialFrame;
import com.orhanobut.logger.Logger;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//TODO fix the following issues (as commented below) if it's necessary

/**
 * This activity is faulty when the 1937 micro-controller thinks the user is using knobs for
 * treatment level adjustment; This activity doesn't support knob input.
 **/

@SuppressWarnings({"WeakerAccess", "unused"})
public class MultiSetActivity extends AppCompatActivity
        implements NumberPicker.OnValueChangeListener {

    private static final String TAG = "MultiSetActivity";

    private int mChannelSelected;

    private int mCurrentLevel;

    private int mSessionTime;

    private SerialConnectable mSerialPort;

    @BindView(R.id.numPicker_hr)
    NumberPicker mNumberPickerHr;

    @BindView(R.id.numPicker_min)
    NumberPicker mNumberPickerMin;

    @BindView(R.id.text_setting_level)
    TextView mTextViewLevel;

    @BindView(R.id.radio_group_settings)
    RadioGroup mRadioGroupChannels;
    private SerialportControler serialportControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_set);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    public void onRadioButtonClicked(View view) {
        //TODO first stop the electrical current output
        // then reset the settings
        mCurrentLevel = Channels.CHN_MIN_VALUE_OF_EVERYTHING;
        mNumberPickerMin.setValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerHr.setValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mTextViewLevel.setText(String.format(Locale.US, "%1$d", mCurrentLevel));

        switch (view.getId()) {
            case R.id.radio_btn_chn1:
                mChannelSelected = Channels.CHN_ONE;
                break;
            case R.id.radio_btn_chn2:
                mChannelSelected = Channels.CHN_TWO;
                break;
            case R.id.radio_btn_chn3:
                mChannelSelected = Channels.CHN_THREE;
                break;
            case R.id.radio_btn_chn4:
                mChannelSelected = Channels.CHN_FOUR;
                break;
            case R.id.radio_btn_chn5:
                mChannelSelected = Channels.CHN_FIVE;
                break;
            default:
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.numPicker_hr:
                if (newVal == Channels.CHN_MAX_HRS) {
                    mNumberPickerMin.setMaxValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
                } else if (oldVal == Channels.CHN_MAX_HRS) {
                    mNumberPickerMin.setMaxValue(Channels.CHN_MAX_MIN);
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_setting_minus)
    void decreaseLevel() {
        // if channel isn't selected, level cannot be set
        if (mChannelSelected == Channels.CHN_NULL)
            return;
        if (mCurrentLevel != Channels.CHN_MIN_VALUE_OF_EVERYTHING) {
            mCurrentLevel--;
        }
        mTextViewLevel.setText(String.format(Locale.US, "%1$d", mCurrentLevel));
        //TODO change the electrical output accordingly (stop then start?)
    }

    @OnClick(R.id.btn_setting_plus)
    void increaseLevel() {
        // if channel isn't selected, level cannot be set
        if (mChannelSelected == Channels.CHN_NULL)
            return;
        if (mCurrentLevel != Channels.CHN_MAX_LEVEL) {
            mCurrentLevel++;
        }
        mTextViewLevel.setText(String.format(Locale.US, "%1$d", mCurrentLevel));
        //TODO change the electrical output accordingly (stop then start?)
    }

    @OnClick(R.id.btn_setting_confirm)
    void submitSettings() {
        String timeKey, levelKey, startTimeKey, isOpenKey;
        int startTimeInSecond = TreatmentApplication.getCurrentTimeMillis();
        mSessionTime = mNumberPickerHr.getValue() * 60 + mNumberPickerMin.getValue();
        String info = "设置完成: " + "第" + mChannelSelected + "路 "
                + "强度: " + mCurrentLevel + " 时长: " + mSessionTime;
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();

        // if btn clicked when chn not having been selected, do nothing
        if (mChannelSelected == Channels.CHN_NULL) {
            return;
        }
        timeKey = Channels.KEY_CHN_SESSION_TIME_PREFIX + mChannelSelected;
        levelKey = Channels.KEY_CHN_LEVEL_PREFIX + mChannelSelected;
        startTimeKey = Channels.KEY_CHN_START_TIME_PREFIX + mChannelSelected;
        isOpenKey = Channels.KEY_CHN_IS_OPEN_PREFIX + mChannelSelected;


        //update shared preferences
        TreatmentApplication.getSharedPreferences().edit()
                .putInt(levelKey, mCurrentLevel)
                .putInt(timeKey, mSessionTime)
                .putInt(startTimeKey, startTimeInSecond)
                .putBoolean(isOpenKey, true)
                .commit();
        SerialFrame.setChannelIndex(mChannelSelected);

        //TODO change the electrical output accordingly (stop then start?)
        //TODO change mSerialPort to the real one
        if (BuildConfig.MIPS) {
            mSerialPort = new RealSerialPortImpl("S0", 115200, 8, 1, 'n');
//            mSerialPort = new TestUtilYYKK("S0", 115200, 8, 1, 'n');
            //mSerialPort = new DummySerialPortImpl("S0", 115200, 8, 1, 'n');
            mSerialPort.sendData(SerialFrame.makeTimingFrameToSend(mSessionTime), "HEX");
            mSerialPort.sendData(SerialFrame.makeLevelFrameToSend(mCurrentLevel), "HEX");
            mSerialPort.closeSerial();

        } else if (BuildConfig.ROCKCHIP) {
            Logger.d("go--->MultiSetActivity");
            serialportControler = new SerialportControler("/dev/ttyS0", 115200);
            serialportControler.sendData(SerialFrame.makeTimingFrameToSend(mSessionTime), "HEX");
            serialportControler.sendData(SerialFrame.makeLevelFrameToSend(mCurrentLevel), "HEX");
            serialportControler.closeSerial();
        }
        // clean the current view
        cancelCurrentSettings();
    }

    @OnClick(R.id.btn_setting_cancel)
    void cancelCurrentSettings() {
        //TODO first stop the electrical current output
        mChannelSelected = Channels.CHN_NULL;
        mRadioGroupChannels.clearCheck();
        mCurrentLevel = Channels.CHN_MIN_VALUE_OF_EVERYTHING;
        mNumberPickerHr.setValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerMin.setValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerMin.setMaxValue(Channels.CHN_MAX_MIN);
        mTextViewLevel.setText(String.format(Locale.US, "%1$d", mCurrentLevel));
    }

    @OnClick(R.id.btn_setting_back)
    void LeaveSettings() {
        cancelCurrentSettings();
        // destroy this activity
        super.finish();
    }

    private void init() {
        mNumberPickerHr.setMinValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerHr.setMaxValue(Channels.CHN_MAX_HRS);
        mNumberPickerHr.setWrapSelectorWheel(true);
        mNumberPickerHr.setOnValueChangedListener(this);
        // disable manual input to the number picker
        mNumberPickerHr.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPickerMin.setMinValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerMin.setMaxValue(Channels.CHN_MAX_MIN);
        mNumberPickerMin.setWrapSelectorWheel(true);
        mNumberPickerMin.setOnValueChangedListener(this);
        mNumberPickerMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mChannelSelected = Channels.CHN_NULL;
        mCurrentLevel = Channels.CHN_MIN_VALUE_OF_EVERYTHING;
    }
}
