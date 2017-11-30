package com.bjghhnt.app.treatmentdevice;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.squareup.otto.Bus;

/**
 * created for easy access to shared preferences
 * Created by Q on 20/01/2016.
 */
public class TreatmentApplication extends Application {

    private static final String TAG = "TreatmentApplication";
    private static final String KEY_PIN = "pin";
    private static SharedPreferences sSharedPreferences;
    private static final String INITIAL_PIN = "1234";
//    public static final String ROCKCHIP = "ROCKCHIP";
//    public static final String MIPS = "MIPS";

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static float dpi;
    private static final Bus BUS = new Bus();
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sSharedPreferences.contains(KEY_PIN)) {
            setPIN(INITIAL_PIN);
        }
        //TODO move the dummy serial port into this class, then the shared preferences don't need to be initialized.
        ActivityManager actManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        dpi = getResources().getDisplayMetrics().density;
        Log.d(TAG, "onCreate: sharedPreference initiated, the screen dpi is " + dpi);
        Log.d(TAG, "onCreate: the total memory of this device is " + totalMemory);
    }

    /**
     * (not pin is also stored in SharedPreferences)
     * SharedPreferences is designated for updating the meter View.
     * Each channel has 4 preferences, namely
     * session time, level, start time, and isOpen(boolean flag),
     * are initiated as (0,0,0,false);
     * In settings activity, when submitting the settings, isOpen flag is set to true;
     * and the other three are set to their values accordingly.
     * When isOpen == true and a frame suggesting the channel is closed is received,
     * all 4 preferences are reset to (0,0,0,false);
     * <p/>
     * When a channel is in idle mode, the continuous frames suggesting it is closed
     * are simply discarded (thanks to the isOpen flag).
     **/
    public void initSharedPreferencesOtherThanPIN() {
        for (int chnNo = 1; chnNo <= Channels.TOTAL_CHN_NUM; chnNo++) {
            resetPreferenceOfOneChn(chnNo);
        }
        sSharedPreferences.edit()
                .putBoolean(Channels.KEY_ALL_USE_KNOB, false).apply();
    }

    public void resetPreferenceOfOneChn(int chnNo) {
        sSharedPreferences.edit()
                .putInt(Channels.KEY_CHN_SESSION_TIME_PREFIX + chnNo,
                        Channels.CHN_MIN_VALUE_OF_EVERYTHING)
                .putInt(Channels.KEY_CHN_LEVEL_PREFIX + chnNo,
                        Channels.CHN_MIN_VALUE_OF_EVERYTHING)
                .putInt(Channels.KEY_CHN_START_TIME_PREFIX + chnNo,
                        Channels.CHN_MIN_VALUE_OF_EVERYTHING)
                .putBoolean(Channels.KEY_CHN_IS_OPEN_PREFIX + chnNo, false)
                .apply();
    }

    public String getPIN() {
        return sSharedPreferences.getString(KEY_PIN, INITIAL_PIN);
    }

    public void setPIN(String pin) {
        sSharedPreferences.edit().putString(KEY_PIN, pin).apply();
    }

    /**
     * check if all channels have finished their treatment
     **/
    public static boolean haveAllFinished() {
        boolean result = false;
        for (int chnNo = 1; chnNo <= Channels.TOTAL_CHN_NUM; chnNo++) {
            result |= sSharedPreferences.getBoolean(Channels.KEY_CHN_IS_OPEN_PREFIX + chnNo, false);
        }
        return !result;
    }

    /**
     * Get the starting time of the session
     **/
    public static int getCurrentTimeMillis() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static Bus getBusInstance() {
        return BUS;
    }
    /**
     * 获取全局上下文*/
    public static Context getContext() {
        return context;
    }
}
