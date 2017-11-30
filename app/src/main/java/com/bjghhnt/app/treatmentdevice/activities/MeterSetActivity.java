package com.bjghhnt.app.treatmentdevice.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.bjghhnt.app.treatmentdevice.utils.serials.ComplexHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialFrame;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.views.TreatingMeterView;
import com.orhanobut.logger.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import android_serialport_api.SerialPort;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * 每路对应的治疗设置界面
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MeterSetActivity extends MinorActivity implements
        NumberPicker.OnValueChangeListener, SharedPreferences.OnSharedPreferenceChangeListener,
        NumberPickerView.OnValueChangeListener{
    @BindView(R.id.btn_meter_set_plus)
    Button btnMeterSetPlus;
    @BindView(R.id.btn_meter_final_confirm)
    Button btnMeterFinalConfirm;
    @BindView(R.id.btn_add)
    Button mBtnAdd;
    @BindView(R.id.btn_minus)
    Button mBtnMinus;
    @BindView(R.id.linear_layout_settings)
    LinearLayout mLinearLayout;
    @BindView(R.id.custom_view_in_settings)
    TreatingMeterView mChnView;
    @BindView(R.id.numPicker_meter_hr)
    NumberPickerView mNumberPickerHr;
    @BindView(R.id.numPicker_meter_min)
    NumberPickerView mNumberPickerMin;
    @BindView(R.id.btn_meter_settings_reset)
    Button mButtonReset;
    @BindView(R.id.btn_meter_settings_confirm_time)
    ToggleButton mToggleButtonConfirmTime;
    @BindView(R.id.linear_layout_level_setter)
    View mViewLevelSetter;
    @BindView(R.id.text_meter_set_level)
    TextView mTextViewLevel;
    @BindView(R.id.tv_time)
    TextView tvTimeMeterSet;
    private static final String TAG = "MeterSetActivity";
    private final ComplexHandler mComplexHandler = new SingleMeterHandler(this);
//    private SerialConnectable mSerialPort;
    private static boolean mUseKnob;
    private int mChannelSelected;
    private int mLevelToSet;
    private int mSessionTime = -1;
    private static SharedPreferences mSharedPreferences;
//    private SerialportControler serialportControler;
    //test
    private OutputStream outputStream;
    private InputStream inputStream;
    private SerialPort mSerialPort1;
    private AlertDialog alertDialog;

    //更新时间
    @Override
    protected void upDateTime() {
        tvTimeMeterSet.setText(formatTime);
//        Logger.d("time_FullPanel:" + tvTimeMeterSet.getText());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_set);
        ButterKnife.bind(this);
        mSharedPreferences = TreatmentApplication.getSharedPreferences();
        mUseKnob = mSharedPreferences.getBoolean(Channels.KEY_ALL_USE_KNOB, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if (mLinearLayout.getVisibility() == View.GONE) {
            mLevelToSet = mSharedPreferences.getInt("STRONG_STATUE", 0);
            System.out.println(mLevelToSet);
        }
    }

    @Override
    protected SerialHandler setHandler() {
        return mComplexHandler;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister the listener, otherwise OnSharedPreferenceChanged() may be invoked,
        // while a finished instance of MeterSetActivity is not GCed.
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        mChannelSelected = (int) extras.get(Channels.INTENT_KEY_CHANNEL_NUMBER);
        mChnView.setChanelNo(mChannelSelected);
        mNumberPickerHr.setDisplayedValues(new String[]{"0", "1", "2", "3"});
        mNumberPickerHr.setMinValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerHr.setMaxValue(Channels.CHN_MAX_HRS);
//		mNumberPickerHr.setValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerHr.setWrapSelectorWheel(true);
        mNumberPickerHr.setOnValueChangedListener(this);
        // disable manual input to the number picker
//		mNumberPickerHr.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        String[] minDisplayedValues = new String[60];
        for (int i = 0; i < minDisplayedValues.length; i++) {
            minDisplayedValues[i] = "" + i;
        }
        mNumberPickerMin.setDisplayedValues(minDisplayedValues);
        mNumberPickerMin.setMinValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerMin.setMaxValue(Channels.CHN_MAX_MIN);
        mNumberPickerMin.setWrapSelectorWheel(true);
        mNumberPickerMin.setOnValueChangedListener(this);

        // set the visibilities of the settings linear layout
        // if the channel is closed, the linear layout is invisible
        setLayoutVisibility(!mSharedPreferences.getBoolean(
                Channels.KEY_CHN_IS_OPEN_PREFIX + mChannelSelected, false), mUseKnob);

        mChnView.setSessionTime(mSharedPreferences
                .getInt(Channels.KEY_CHN_SESSION_TIME_PREFIX + mChannelSelected, 0));
        mChnView.setLevel(mSharedPreferences
                .getInt(Channels.KEY_CHN_LEVEL_PREFIX + mChannelSelected, 0));
        if (mChnView.getMagnitude() != 0) {
            mChnView.setRemainingTime(TreatingMeterView.REMAINING_TIME_UNAVAILABLE);
        }
    }

    private void setLayoutVisibility(boolean isSettingWidgetsVisible, boolean useKnob) {
        if (isSettingWidgetsVisible) {
            mButtonReset.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
            mBtnAdd.setVisibility(View.GONE);
            mBtnMinus.setVisibility(View.GONE);
        } else {
            mLinearLayout.setVisibility(View.GONE);
            mButtonReset.setVisibility(View.VISIBLE);
            if (!mUseKnob) {
                mBtnAdd.setVisibility(View.VISIBLE);
                mBtnMinus.setVisibility(View.VISIBLE);
            }
        }
        if (useKnob) {
            mViewLevelSetter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.numPicker_meter_hr:
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


    /**
     * only do something about the display when the prefs associated with
     * the channel selected is changed
     **/
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // should unregister onSharedPreferenceChangedListener in onStop(),
        // otherwise this method might be accidentally called
        if (!key.substring(key.length() - 1).equals(String.valueOf(mChannelSelected))) {
            return;
        }
        if (key.contains(Channels.KEY_CHN_LEVEL_PREFIX)) {
            mChnView.setLevel(sharedPreferences.getInt(key, 0));
        }
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.numPicker_meter_hr:
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

    @OnClick({R.id.btn_add, R.id.btn_minus})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                if (mLevelToSet == Channels.CHN_MAX_LEVEL) return;
                mLevelToSet++;
                updateLevelForPrefsAndViewAndSendFrame(mLevelToSet);
                break;
            case R.id.btn_minus:
                if (mLevelToSet == Channels.CHN_MIN_VALUE_OF_EVERYTHING) return;
                mLevelToSet--;
                updateLevelForPrefsAndViewAndSendFrame(mLevelToSet);
                break;
        }
    }

    private static class SingleMeterHandler extends ComplexHandler {

        public SingleMeterHandler(MeterSetActivity activity) {
            super(activity);
        }

        @Override
        protected boolean isChannelDisplayed(int channelNo) {
            return channelNo == ((MeterSetActivity) mActivity).mChannelSelected;
        }

        @Override
        protected void onReceiveSetProgressDisplay(int frameValue, int channelNo) {
            ((MeterSetActivity) mActivity).mChnView.setRemainingTimeAndProgress(frameValue);
        }

        @Override
        protected void onReceiveSetLevelDisplay(int frameValue, int channelNo) {
            ((MeterSetActivity) mActivity).mChnView.setLevel(frameValue);
            mSharedPreferences.edit()
                    .putInt(Channels.KEY_CHN_LEVEL_PREFIX + channelNo, frameValue).apply();
            Log.d(TAG, "onReceiveSetLevelDisplay: ");
        }

        @Override
        protected void changeLayout() {
            ((MeterSetActivity) mActivity).setLayoutVisibility(true, mUseKnob);
        }

        @Override
        protected void getPotentiometerValue(int value, int channelNo) {

        }

        @Override
        protected void onReceiveBatteryFrame(int[] value) {
            //TODO this should be populated
        }

        @Override
        protected void getPotentiometerMode(Boolean b) {

        }
    }

    @OnClick(R.id.btn_meter_settings_back)
    void LeaveSettings() {
        super.finish();
    }

    //重置
    @OnClick(R.id.btn_meter_settings_reset)
    void resetTreatment() {
//        new AlertDialog.Builder(this)
        // do nothing
        alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("您要终止此次疗程吗？")
                .setMessage(" ")
                .setNegativeButton(" 取消  ", (dialog, which) -> {
                    // do nothing
                })
                .setPositiveButton(" 终止  ", (dialog, which) -> {
                    resetPrefsAndViewAndSendFrame();
                    setLayoutVisibility(true, mUseKnob);
                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIcon(R.drawable.ic_dialog_warn)
                .setCancelable(false)
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0XEE336633);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(50);
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(0XEE336633);
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(50);

        //设置alertDialog宽高
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = 700;
        params.height = 300;
        alertDialog.getWindow().setAttributes(params);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSharedPreferences.edit().putInt("STRONG_STATUE", mLevelToSet).apply();
        if (alertDialog != null) {
            alertDialog.cancel();
        }
    }

    //重置操作
    private void resetPrefsAndViewAndSendFrame() {
        /** reset the corresponding preferences and reset the view **/
        ((TreatmentApplication) getApplication())
                .resetPreferenceOfOneChn(mChannelSelected);
        /** setRemainingTimeAndProgress needs to be done here
         * don't need setSessionTime here because it is done by the OnPreferencesChangedListener**/
        mChnView.setRemainingTimeAndProgress(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        /** send stopping command via serialPort **/
        SerialFrame.setChannelIndex(mChannelSelected);
        if (BuildConfig.MIPS) {
            mSerialPort.sendData(SerialFrame
                    .makeTimingFrameToSend(Channels.CHN_MIN_VALUE_OF_EVERYTHING), "HEX");
            mSerialPort.sendData(SerialFrame
                    .makeLevelFrameToSend(Channels.CHN_MIN_VALUE_OF_EVERYTHING), "HEX");
        } else if (BuildConfig.ROCKCHIP) {
            serialportControler.sendData(SerialFrame
                    .makeTimingFrameToSend(Channels.CHN_MIN_VALUE_OF_EVERYTHING), "HEX");
            serialportControler.sendData(SerialFrame
                    .makeLevelFrameToSend(Channels.CHN_MIN_VALUE_OF_EVERYTHING), "HEX");

        }
        System.out.println(SerialFrame
                .makeTimingFrameToSend(Channels.CHN_MIN_VALUE_OF_EVERYTHING));
        /** TODO the dummy serial port is faulty, it keeps counting down for 2 minutes
         after this button is clicked
         **/
    }

    /**
     * 向串口发送数据
     *
     * @param level
     */
    private void updateLevelForPrefsAndViewAndSendFrame(int level) {
        /** reset the corresponding preferences and reset the view **/
        mSharedPreferences.edit()
                .putInt(Channels.KEY_CHN_LEVEL_PREFIX + mChannelSelected, level)
                .apply();
        /** update the level to display in the meter **/
        mChnView.setLevel(level);
        /** send stopping command via serialPort **/
        SerialFrame.setChannelIndex(mChannelSelected);
        if (BuildConfig.MIPS) {
            mSerialPort.sendData(SerialFrame.makeLevelFrameToSend(level), "HEX");
        } else if (BuildConfig.ROCKCHIP) {
            serialportControler.sendData(SerialFrame.makeLevelFrameToSend(level), "HEX");
            Logger.d(level + "-----" + SerialFrame.makeLevelFrameToSend(level));
        }
//        Toast.makeText(this, "发送到串口前的数据::"+SerialFrame.makeLevelFrameToSend(level), Toast.LENGTH_SHORT).show();
        System.out.println(level + "：yykk-----发送的数据：：" + SerialFrame.makeLevelFrameToSend(level));
    }

    private void responseToToggleBtn(Boolean isChecked) {
        mToggleButtonConfirmTime.setText((isChecked) ? "停止" : "开始");
        mNumberPickerMin.setEnabled(!isChecked);
        mNumberPickerHr.setEnabled(!isChecked);
        //TODO there is a redundant View.INVISIBLE or GONE somewhere added today
        mViewLevelSetter.setVisibility((isChecked && !mUseKnob) ? View.VISIBLE : View.INVISIBLE);
    }

    //开始or停止
    @OnClick(R.id.btn_meter_settings_confirm_time)
    void confirmTime() {
        if (mToggleButtonConfirmTime.isChecked()) {
            int sessionTime = mNumberPickerHr.getValue() * 60 + mNumberPickerMin.getValue();
            // if sessionTime == 0, un-check the toggle button and return
            // pretend nothing happened
            Logger.d("sessionTime:"+sessionTime);
            if (sessionTime == Channels.CHN_MIN_VALUE_OF_EVERYTHING) {
                mToggleButtonConfirmTime.setChecked(false);
                return;
            }
            /** update the corresponding preferences (sessionTime, startTime, isOpen) and the view.
             * the dummy serial port has implemented OnSharedPreferencesChangedListener **/
            mSharedPreferences.edit()
                    .putInt(Channels.KEY_CHN_SESSION_TIME_PREFIX + mChannelSelected, sessionTime)
                    .putInt(Channels.KEY_CHN_START_TIME_PREFIX + mChannelSelected,
                            TreatmentApplication.getCurrentTimeMillis())
                    .putBoolean(Channels.KEY_CHN_IS_OPEN_PREFIX + mChannelSelected, true)
                    .apply();
            //TODO is this correct?
            mChnView.setSessionTime(sessionTime);
            mChnView.setRemainingTimeAndProgress(sessionTime);

            /** send set session time command via serialPort **/
            SerialFrame.setChannelIndex(mChannelSelected);

            if (BuildConfig.MIPS) {
                mSerialPort.sendData(SerialFrame.makeTimingFrameToSend(sessionTime), "HEX");
            } else if (BuildConfig.ROCKCHIP) {
                serialportControler.sendData(SerialFrame.makeTimingFrameToSend(sessionTime), "HEX");
            }
            responseToToggleBtn(true);
            if (mUseKnob) {
                Logger.d("旋钮状态发报文？" + mUseKnob);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                if (BuildConfig.MIPS) {
                    mSerialPort.sendData(SerialFrame.makeUseKnobFrame(), "HEX");
                } else if (BuildConfig.ROCKCHIP) {
                    serialportControler.sendData(SerialFrame.makeUseKnobFrame(), "HEX");
                    Logger.d("旋钮状态发报文内容---" + SerialFrame.makeUseKnobFrame());
                }
            }
        } else {
            Logger.d("旋钮状态发报文" + mUseKnob);
            resetPrefsAndViewAndSendFrame();
            responseToToggleBtn(false);
            /**reset the display and record of level as well**/
            mLevelToSet = Channels.CHN_MIN_VALUE_OF_EVERYTHING;
            mTextViewLevel.setText(String.format(Locale.US, "%1$d", mLevelToSet));
        }
    }

    @OnClick(R.id.btn_meter_set_minus)
    void decreaseLevel() {
        if (mLevelToSet == Channels.CHN_MIN_VALUE_OF_EVERYTHING) {
            return;
        }
        mLevelToSet--;
        mTextViewLevel.setText(String.format(Locale.US, "%1$d", mLevelToSet));
        //TODO send setting level frame and change shared prefs and display
        updateLevelForPrefsAndViewAndSendFrame(mLevelToSet);
        isLevelZero();
    }

    @OnClick(R.id.btn_meter_set_plus)
    void increaseLevel() {
        if (mLevelToSet == Channels.CHN_MAX_LEVEL) {
            return;
        }
        mLevelToSet++;
        mTextViewLevel.setText(String.format(Locale.US, "%1$d", mLevelToSet));
        //TODO send setting level frame and change shared prefs and display
        updateLevelForPrefsAndViewAndSendFrame(mLevelToSet);
        isLevelZero();
    }

    //判断强度是否为零
    private void isLevelZero() {
        String text = (String) mTextViewLevel.getText();
        Logger.d("强度等级---" + text);
        if (text.equals("0")) {
            btnMeterFinalConfirm.setVisibility(View.GONE);
        } else {
            btnMeterFinalConfirm.setVisibility(View.VISIBLE);
        }
    }

    //确定
    @OnClick(R.id.btn_meter_final_confirm)
    void restoreView() {
        /**First hide the whole settings layout and only show reset button**/
        setLayoutVisibility(false, mUseKnob);
        //if magnitude == 0 close this channel and tell shared prefs
        if (mLevelToSet == 0) {
            resetPrefsAndViewAndSendFrame();
        }
        /**Then reset all the settings display**/
        // reset the record and text for the level to set
//        mLevelToSet = Channels.CHN_MIN_VALUE_OF_EVERYTHING;
        mTextViewLevel.setText(String.format(Locale.US, "%1$d", mLevelToSet));
        // reset the visibility and text shown on the toggle button
        responseToToggleBtn(false);
        // If the user reset the channel more than once in the same instance of this activity,
        // the toggle button should first shown as unchecked every time it appears
        mToggleButtonConfirmTime.setChecked(false);
        // the number picker should show 0'0" every time after the reset button is clicked.
        mNumberPickerHr.setValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
        mNumberPickerMin.setValue(Channels.CHN_MIN_VALUE_OF_EVERYTHING);

    }


}
