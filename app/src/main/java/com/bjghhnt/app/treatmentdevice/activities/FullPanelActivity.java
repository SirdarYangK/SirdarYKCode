package com.bjghhnt.app.treatmentdevice.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.utils.AlertDialogManager;
import com.bjghhnt.app.treatmentdevice.utils.BatteryInfo;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.bjghhnt.app.treatmentdevice.utils.serials.ComplexHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialFrame;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.views.TreatingMeterView;
import com.necer.ndialog.NDialog;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 治疗仪界面
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FullPanelActivity extends MinorActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    @BindView(R.id.custom_view_chn_1)
    TreatingMeterView mChn1;
    @BindView(R.id.custom_view_chn_2)
    TreatingMeterView mChn2;
    @Nullable
    @BindView(R.id.custom_view_chn_3)
    TreatingMeterView mChn3;
    @Nullable
    @BindView(R.id.custom_view_chn_4)
    TreatingMeterView mChn4;
    @Nullable
    @BindView(R.id.custom_view_chn_5)
    TreatingMeterView mChn5;
    @Nullable
    @BindView(R.id.img_view_bat)
    ImageView mImgViewBat;
    @BindView(R.id.tv_time)
    TextView tvTimeFullPanel;
//    @BindView(R.id.bt_listen_cancal)
//    Button listen_cancal;
//    @BindView(R.id.bt_listen_confirm)
//    Button listen_confirm;

    private final TreatingMeterView[] mChnArray = new TreatingMeterView[Channels.TOTAL_CHN_NUM];
    private static SharedPreferences mSharedPreferences;
    private static final String TAG = "FullPanelActivity";
    //    private SerialConnectable mSerialPort;
//    private SerialportControler serialportControler;
    private final MultiMeterHandler mMultiHandler = new MultiMeterHandler(this);
    private boolean mUseKnob;
    public static int nonZeroTag = 0x123;
    public static int isZeroTag = 0;
    public static int chn_one = 0x11;
    public static int chn_two = 0x22;
    public static int chn_three = 0x33;
    public static int chn_four = 0x44;
    public static int chn_five = 0x55;
    //无法获取网络时间
    private static final String SET_NET_TIME_ERROR = "SET_NET_TIME_ERROR";
    private static final String UPDATE_TIME_UI = "UPDATE_TIME_UI";
    private static View channelView;
    private static MaterialDialog mAlertDialog;
    private Button listenCancal;
    private Button listenConfirm;
    private static Map<Integer, Integer> caChe;

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int current = intent.getExtras().getInt("level");//获得当前电量
            int total = intent.getExtras().getInt("scale");//获得总电量
            int percent = current * 100 / total;
            System.out.println("Battery--" + percent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (Channels.TOTAL_CHN_NUM) {
            // has two channels on smart phone version
            case 2:
                setContentView(R.layout.activity_full_panel_2);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            // has three channels on desktop-3 version
            case 3:
                setContentView(R.layout.activity_full_panel_3);
                break;
            // has five channels on desktop-5 version
            case 5:
                setContentView(R.layout.activity_full_panel_5);
                break;
            default:

                setContentView(R.layout.activity_full_panel_5);
        }
        ButterKnife.bind(this);
        mSharedPreferences = TreatmentApplication.getSharedPreferences();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mChnArray[0] = mChn1;
        mChnArray[1] = mChn2;
        if (mChn3 != null) {
            mChnArray[2] = mChn3;
        }
        if (mChn4 != null) {
            mChnArray[3] = mChn4;
        }
        if (mChn5 != null) {
            mChnArray[4] = mChn5;
        }

        //TODO implement the indicator
        if (BuildConfig.MIPS) {
            mImgViewBat.setImageResource(R.drawable.ic_battery_unknown_black_24dp);
        } else if (BuildConfig.ROCKCHIP) {
            mImgViewBat.setImageResource(R.drawable.ic_battery_unknown_black_24dp);
        }
        // or use app:srcCompat="@drawable/XXX" in the layout file; cannot use android:src
        EventBus.getDefault().register(this);//订阅
//        Observable.create(this)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
        mUseKnob = mSharedPreferences.getBoolean(Channels.KEY_ALL_USE_KNOB, false);
        Logger.d("旋钮状态" + mUseKnob);
        if (mUseKnob) {
            mSharedPreferences.edit().putBoolean(Channels.KEY_ALL_USE_KNOB, false).apply();
            TreatingMeterView.changeIcon();
        }
        initCacheData();
    }

    //初始化缓存数据
    private void initCacheData() {
        caChe = new HashMap<Integer, Integer>();
        for (int i = 1; i <= 5; i++) {
            caChe.put(i, 0);
        }
    }

    //更新时间
    @Override
    protected void upDateTime() {
        tvTimeFullPanel.setText(formatTime);
//        Logger.d("time_FullPanel:" + tvTimeFullPanel.getText());
    }

    @Override
    protected void onStart() {
        super.onStart();
        onStartSetSessionTimeForDisplayForAll();
        onStartSetLevelDisplayForAll();
    }

    @Override
    protected SerialHandler setHandler() {
        return mMultiHandler;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);//解除订阅
        if (caChe != null) {
            caChe.clear();
            caChe = null;
        }
    }

    private TreatingMeterView getChnObject(int chnNo) {
        if (chnNo < Channels.CHN_ONE || chnNo > Channels.CHN_FIVE) {
            return null;
        } else {
            return mChnArray[chnNo - 1];
        }
    }

    //切换旋钮模式和触摸模式
    //commit to shared preference
    //TODO 根据主板返回的信息在切换前判断旋钮是否复位
    @OnClick(R.id.btn_switch_full_panel)
    void switchInputMode() {
        boolean keyAllUseKnob = mSharedPreferences.getBoolean(Channels.KEY_ALL_USE_KNOB, false);
        if (!keyAllUseKnob) {//触屏-->旋钮
            Toast.makeText(this, "旋钮模式--" + SerialFrame.getIsPotentiometer()
                    + " ；强度：" + mMultiHandler.getPotentiometersValue(), Toast.LENGTH_SHORT).show();
            Logger.d("jjkk--Map集合FullPanelActivity---key:" + nonZeroTag + "---value:" +
                    caChe.get(nonZeroTag));
//                    NDialog dialog = AlertDialogManager.displayNDialog(this,
//                            "设备第" + nonZeroTag + ":" + caChe.get(nonZeroTag) + "路没有复位",
//                            "所有治疗通道复位后方可切换模式", "复位", "取消");
//                    dialog.setOnConfirmListener(new NDialog.OnConfirmListener() {
//                        @Override
//                        public void onClick(int which) {//which,0代表NegativeButton，1代表PositiveButton
//                            Logger.d("which:" + which);
//                            if (which == 1) {
//                                //所有通道复位
//                                resetProgress(nonZeroTag);
//                                Logger.d("AlertDialog " + nonZeroTag);
//                            }
//                        }
//                    }).create(NDialog.CONFIRM).show();
            resetPrefsAndViewAndSendFrame(Channels.SWITCHINPUTMODE_KNOB); //切换时发送报文 44
            mAlertDialog = AlertDialogManager.displayMaterialDialog
                    (this, R.layout.channel_listen_view_five, 600, 350);
            channelView = mAlertDialog.getCustomView();
            channelView.findViewById(R.id.bt_listen_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
            mHandlerText.sendEmptyMessage(0);

        } else { //旋钮-->触屏
            EventBus.getDefault().post(Channels.SWITCHINPUTMODE_TUCH);//发送事件
            Toast.makeText(this, "旋钮模式--触屏", Toast.LENGTH_SHORT).show();
            mSharedPreferences.edit().putBoolean(Channels.KEY_ALL_USE_KNOB, false).apply();
            //切换时发送报文 99
            resetPrefsAndViewAndSendFrame(Channels.SWITCHINPUTMODE_TUCH);

        }
        Logger.d("switchInputMode切换keyAllUseKnob:" + keyAllUseKnob);
        TreatingMeterView.changeIcon();
        try {
            Thread.sleep(500);//每次切换不可过快
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
//        Calendar instance = Calendar.getInstance();
    }

    public static Handler mHandlerText = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int what = msg.what;
            if (what == 0) {    //update
                TextView value_one = channelView.findViewById(R.id.tv_channel_value_one);
                TextView value_two = channelView.findViewById(R.id.tv_channel_value_two);
                TextView value_three = channelView.findViewById(R.id.tv_channel_value_three);
                TextView value_four = channelView.findViewById(R.id.tv_channel_value_four);
                TextView value_five = channelView.findViewById(R.id.tv_channel_value_five);
//                value_one.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", System
//                        .currentTimeMillis()).toString());
                value_one.setText(String.valueOf(caChe.get(1)));
                value_two.setText(String.valueOf(caChe.get(2)));
                value_three.setText(String.valueOf(caChe.get(3)));
                value_four.setText(String.valueOf(caChe.get(4)));
                value_five.setText(String.valueOf(caChe.get(5)));
//                resetPrefsAndViewAndSendFrame(Channels.SWITCHINPUTMODE_KNOB); //切换时发送报文 44
                Logger.d("handleMessage--nonZeroTag:" + nonZeroTag);
                if (nonZeroTag == 0) {
                    mSharedPreferences.edit().putBoolean(Channels.KEY_ALL_USE_KNOB, true).apply();

                } else {
                    mSharedPreferences.edit().putBoolean(Channels.KEY_ALL_USE_KNOB, false).apply();
                }
                TreatingMeterView.changeIcon();
                if (mAlertDialog.isShowing()) {
                    mHandlerText.sendEmptyMessageDelayed(0, 50);
                    if (nonZeroTag == 0) {
                        mHandlerText.sendEmptyMessageDelayed(111, 5000);
                    }
                }
            } else if (what == 1) {
                mHandlerText.sendEmptyMessageDelayed(111, 2000);
            } else {
                mAlertDialog.dismiss();
            }
        }
    };

    /**
     * EventBus事件处理
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC) //在ui线程执行
    public void onDataSynEvent(Integer event) {
        Logger.d("eventBus--" + event);
        for (int i = 1; i < 6; i++) {
            resetProgress(i);
        }
    }

    //切换时，重置mChnArray
    private void resetProgress(int channel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.d("resetProgress-->run;" + channel);
                ((TreatmentApplication) getApplication()).resetPreferenceOfOneChn(channel);
                mChnArray[channel - 1].setRemainingTimeAndProgress(Channels.CHN_MIN_VALUE_OF_EVERYTHING);
                SerialFrame.setChannelIndex(channel);
            }
        });
        resetMessage();
//        Toast.makeText(this,"关掉所有正在治疗的进度",Toast.LENGTH_SHORT);
    }

    //复位报文
    private void resetMessage() {
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
            Logger.d("复位发送报文：" + SerialFrame
                    .makeLevelFrameToSend(Channels.CHN_MIN_VALUE_OF_EVERYTHING));
        }
    }

    //电位器报文
    private void resetPrefsAndViewAndSendFrame(int mode) {
        switch (mode) {
            case Channels.SWITCHINPUTMODE_KNOB://44
                if (BuildConfig.MIPS) {
                    mSerialPort.sendData(SerialFrame.makeUseKnobFrame(), "HEX");
                } else if (BuildConfig.ROCKCHIP) {
                    serialportControler.sendData(SerialFrame.makeTimingFrameToSend(60), "HEX");
                    serialportControler.sendData(SerialFrame.makeUseKnobFrame(), "HEX");
                }
                Logger.d("switchInputMode旋钮--状态发报文内容---" + SerialFrame.makeUseKnobFrame());
                break;
            case Channels.SWITCHINPUTMODE_TUCH://99
                if (BuildConfig.MIPS) {
                    mSerialPort.sendData(SerialFrame.makeUseTouchFrame(), "HEX");
                } else if (BuildConfig.ROCKCHIP) {
                    serialportControler.sendData(SerialFrame.makeTimingFrameToSend(60), "HEX");
                    serialportControler.sendData(SerialFrame.makeUseTouchFrame(), "HEX");
                }
                Logger.d("switchInputMode触屏--发送报文-----" + SerialFrame.makeUseTouchFrame());
                break;
        }
//        System.out.println(SerialFrame
//                .makeTimingFrameToSend(Channels.CHN_MIN_VALUE_OF_EVERYTHING));
        /** TODO the dummy serial port is faulty, it keeps counting down for 2 minutes
         after this button is clicked
         **/
    }

    @OnClick(R.id.btn_setting_full_panel)
    void startSettingsActivity() {
        startActivity(new Intent(this, MultiSetActivity.class));
    }

    @OnClick(R.id.btn_setting_go_back)
    void backToHome() {
        super.finish();
    }

    /**
     * This method is useful when preferences are changed from settings Activity
     * See also onStartSetSessionTimeForDisplayForAll()
     * and onStartSetLevelDisplayForAll()
     **/
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        TreatingMeterView chn;
        // It is possible that the preferences "bgsync_launch_next_online" and
        // "app restrictions" also changes. Hence, first ensure that the last char in
        // the key string is a number rather than a letter.
        //TODO what are "bgsync_launch_next_online" and "App Restrictions"? Should they be deleted?
        if (!key.matches("^.+?\\d$")) {
            Log.d(TAG, "onSharedPreferenceChanged: wired key encountered -- " + key);
            return;
        }
        chn = getChnObject(Integer.parseInt(key.substring(key.length() - 1)));
        if (chn == null) {
            return;
        }
        if (key.contains(Channels.KEY_CHN_SESSION_TIME_PREFIX)) {
            chn.setSessionTime(sharedPreferences.getInt(key, 0));
        } else if (key.contains(Channels.KEY_CHN_LEVEL_PREFIX)) {
            chn.setLevel(sharedPreferences.getInt(key, 0));
        }
    }

    /**
     * 控制治疗模式各通道，触摸or电位器
     */
    public void setControlSwitch(int channelValue, int channelNo) {
        if (caChe == null) return;
        caChe.put(channelNo, channelValue);
        Logger.d("setControlSwitch====" + channelNo + ": " + channelValue + " caChe:" + caChe);
        int receiveValue = (int) caChe.get(channelNo);
        if (caChe.size() < 5) return;
        if (caChe.get(channelNo) != 0) {
            nonZeroTag = channelNo;
            Logger.d("jjkk--Map集合setControlSwitch---key:" + " nonZeroTag:" + nonZeroTag);
        } else if (caChe.get(1) == 0 && caChe.get(2) == 0 &&
                caChe.get(3) == 0 && caChe.get(4) == 0 && caChe.get(5) == 0) {
            nonZeroTag = 0;
            Logger.d("jjkk--nonZeroTag:" + nonZeroTag);

//            for (Map.Entry<Integer, Integer> entry : caChe.entrySet()) {
//                Logger.d("Map----key= " + entry.getKey() + " and value= " + entry.getValue());
//                switch (entry.getKey()) {
//                    case 1:
//                        chn_one = caChe.get(1);
//                        break;
//                    case 2:
//                        chn_two = caChe.get(2);
//                        break;
//                    case 3:
//                        chn_three = caChe.get(3);
//                        break;
//                    case 4:
//                        chn_four = caChe.get(4);
//                        break;
//                    case 5:
//                        chn_five = caChe.get(5);
//                        break;
//                }
//            }


//            set.add(channelNo);
//            int size = set.size();
//            for (int str : set) {
//                int v = (int) caChe.get(str);
//                switch (str) {
//                    case 1:
//                        chn_one = (int) caChe.get(str);
//                        break;
//                    case 2:
//                        chn_two = (int) caChe.get(str);
//                        break;
//                    case 3:
//                        chn_three = (int) caChe.get(str);
//                        break;
//                    case 4:
//                        chn_four = (int) caChe.get(str);
//                        break;
//                    case 5:
//                        chn_five = (int) caChe.get(str);
//                        break;
//                }

//                if (caChe.size() == 1 && caChe.get(str) == 0) {
//                    nonZeroTag = 0;
//                }
//            }
        }
        Logger.d("jjkk--Map集合setControlSwitch---key:" + channelNo + "  value:" +
                caChe.get(channelNo) + "  nonZeroTag:" + nonZeroTag + "   caChe.size:" + caChe.size());
    }

    /**
     * use a weak reference in a static handler class to prevent memory leak
     **/
    private static class MultiMeterHandler extends ComplexHandler {
        private static int mPotentiometerValueTag = 0;
        private static int mPotentiometerChannelNoTag = 1;
        private static boolean mIsPotentiometer = false;

        public MultiMeterHandler(FullPanelActivity activity) {
            super(activity);
            Logger.d("Multi--MultiMeterHandler");
        }

        @Override
        protected boolean isChannelDisplayed(int channelNo) {
            Logger.d("Multi--isChannelDisplayed");
            return true;
        }

        @Override
        public void onReceiveSetProgressDisplay(int frameValue, int channelNo) {
            Logger.d("Multi--onReceiveSetProgressDisplay  ：" + channelNo + "," + frameValue);
            TreatingMeterView chn = ((FullPanelActivity) mActivity).getChnObject(channelNo);
            if (chn == null) {
                return;
            }
            chn.setRemainingTimeAndProgress(frameValue);

        }

        @Override
        public void onReceiveSetLevelDisplay(int frameValue, int channelNo) {
            Logger.d("Multi--onReceiveSetLevelDisplay");
            TreatingMeterView chn = ((FullPanelActivity) mActivity).getChnObject(channelNo);
            if (chn == null) {
                return;
            }
            chn.setLevel(frameValue);
        }

        @Override
        protected void changeLayout() {
            Logger.d("Multi--changeLayout");
            //TODO can do something here (method name may be changed)
        }

        @Override
        protected void getPotentiometerValue(int frameValue, int channelNo) {
            Logger.d("Multi--getPotentiometerValue---" + channelNo + ":" + frameValue);
            ((FullPanelActivity) mActivity).setControlSwitch(frameValue, channelNo);
        }

        protected int getPotentiometersValue() {
            Logger.d("Multi--mode--电位器mPotentiometerValueTag---" + mPotentiometerValueTag);
            return mPotentiometerValueTag;
        }

        protected int getPotentiometerChannelNo() {
            Logger.d("Multi--mode--电位器mPotentiometerChannelNoTag---" + mPotentiometerChannelNoTag);
            return mPotentiometerChannelNoTag;
        }

        @Override
        protected void getPotentiometerMode(Boolean b) {
            mIsPotentiometer = b;
        }

        protected Boolean getPotentiometersMode() {
            return mIsPotentiometer;
        }

        @Override
        protected void onReceiveBatteryFrame(int[] info) {
            //TODO info[0] is mode CODE_POWER_UNPLUGGED or CODE_POWER_PLUGGED, use a typed array for drawables
            int value = info[1];
            Logger.d("jjkk--FullPanelActivity电量---" + value);
            if (value < BatteryInfo.REMAIN_20) {
                ((FullPanelActivity) mActivity).mImgViewBat
                        .setImageResource(R.drawable.ic_battery_alert_black_24dp);
            } else if (value < BatteryInfo.REMAIN_30) {
                ((FullPanelActivity) mActivity).mImgViewBat
                        .setImageResource(R.drawable.ic_battery_20_black_24dp);
            } else if (value < BatteryInfo.REMAIN_50) {
                ((FullPanelActivity) mActivity).mImgViewBat
                        .setImageResource(R.drawable.ic_battery_30_black_24dp);
            } else if (value < BatteryInfo.REMAIN_60) {
                ((FullPanelActivity) mActivity).mImgViewBat
                        .setImageResource(R.drawable.ic_battery_50_black_24dp);
            } else if (value < BatteryInfo.REMAIN_80) {
                ((FullPanelActivity) mActivity).mImgViewBat
                        .setImageResource(R.drawable.ic_battery_60_black_24dp);
            } else if (value < BatteryInfo.REMAIN_90) {
                ((FullPanelActivity) mActivity).mImgViewBat
                        .setImageResource(R.drawable.ic_battery_80_black_24dp);
            } else if (value < BatteryInfo.REMAIN_FULL) {
                ((FullPanelActivity) mActivity).mImgViewBat
                        .setImageResource(R.drawable.ic_battery_90_black_24dp);
            } else {
                ((FullPanelActivity) mActivity).mImgViewBat
                        .setImageResource(R.drawable.ic_battery_full_black_24dp);
            }
        }

    }

    /**
     * if this activity is destroyed, each custom View's mSessionTime need to be set according to
     * haredPreference, then the progress can display properly
     */
    private void onStartSetSessionTimeForDisplayForAll() {
        int chnNo = 1;
        int sessionTime;
        for (TreatingMeterView chn : mChnArray) {
            sessionTime = TreatmentApplication.getSharedPreferences()
                    .getInt(Channels.KEY_CHN_SESSION_TIME_PREFIX + chnNo, 0);
            chn.setSessionTime(sessionTime);

            /** Without this if clause, when a channel is done when you are in MeterSetActivity and
             * get back to this activity,
             * the display on the corresponding meter would be faulty.**/
            if (sessionTime == 0) {
                chn.setRemainingTimeAndProgress(sessionTime);
            }
            chnNo++;
        }
    }

    /**
     * if this activity is destroyed, each custom View's mMagnitude need to be set according to
     * sharedPreference, then the view can display properly
     */
    private void onStartSetLevelDisplayForAll() {
        int chnNo = 1;
        for (TreatingMeterView chn : mChnArray) {
            chn.setLevel(mSharedPreferences.getInt(Channels.KEY_CHN_LEVEL_PREFIX + chnNo, 0));
            if (chn.getMagnitude() != 0) {
                chn.setRemainingTime(TreatingMeterView.REMAINING_TIME_UNAVAILABLE);
            }
            chnNo++;
        }
    }
}
