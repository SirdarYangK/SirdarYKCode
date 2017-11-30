package com.bjghhnt.app.treatmentdevice.utils.serials;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

/**
 * The parent class of SimpleHandler and ComplexHandler
 * use a weak reference in a static handler class to prevent memory leak
 * Created by Q on 26/02/2016.
 */
public abstract class SerialHandler extends Handler {

    private static final String TAG = "SerialHandler";

    protected final WeakReference<? extends AppCompatActivity> mActivityWeakReference;

    protected final AppCompatActivity mActivity;

    String mHexStringReceived;

    SerialHandler(AppCompatActivity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
        mActivity = mActivityWeakReference.get();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        mHexStringReceived = ((String) msg.obj).replaceAll("\\s", ""); // replace all spaces
        Logger.d("jjkk-SerialHandlerHexStringReceived--" + mHexStringReceived);
        int channelNo = SerialFrame.getFrameChn(mHexStringReceived);
        int frameValue = SerialFrame.getFrameValue(mHexStringReceived);
        Toast.makeText(mActivity, "SerialHandler---" + mHexStringReceived, Toast.LENGTH_SHORT).show();
//        if (mHexStringReceived.length() < 18 || !SerialFrame.isReceivedFrameValid(mHexStringReceived)) {
        if (!SerialFrame.isReceivedFrameValid(mHexStringReceived)) {
            Toast.makeText(mActivity, "无效 " + mHexStringReceived + "," + mHexStringReceived.length(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (SerialFrame.getFrameMode(mHexStringReceived).equals(SerialFrame.MODE_RECEIVE_LEVEL)) {
            if (channelNo > 0 && channelNo <= BuildConfig.CHANNELS_IN_TOTALL && frameValue >= 0 && frameValue <= 30) {
                getPotentiometerValue(frameValue, channelNo);
                Logger.d("jjkk-CHandlerPotentValue: No.-Value:" + channelNo + " : " + frameValue);
            }
        }
        if (SerialFrame.isBatteryInfo(mHexStringReceived)) {
            onReceiveBatteryFrame(SerialFrame.getBatteryInfo(mHexStringReceived));
            Logger.d("Handler get电量---" + mHexStringReceived + "--" + SerialFrame.getBatteryInfo(mHexStringReceived)[1]);
        } else {
            //Toast.makeText(activity, SerialFrame.interpretReceivedFrame(mHexStringReceived), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "handleMessage:--- " + SerialFrame.interpretReceivedFrame(mHexStringReceived));
            // if this channel is already closed (according to sharedPrefs), return
            if (!isChannelOpen(channelNo)) {
                return;
            }
            handleOpenChannel(channelNo,frameValue);
        }
    }

    /**
     * when a session is finished, reset the preference of a channel
     * the preference here is the same instance as mPreference
     * because it has an onPreferenceChange listener, the view will automatically change to idle mode
     * as shown in onSharedPreferenceChanged()
     **/
    void resetPreferenceOfOneChn(int channelNo) {
        if (channelNo == Channels.CHN_NULL) {
            return;
        }
        ((TreatmentApplication) (mActivity.getApplication()))
                .resetPreferenceOfOneChn(channelNo);
    }

    /**
     * check if the channel is working before receiving the current frame
     **/
    private static boolean isChannelOpen(int channelNo) {
        return (channelNo != Channels.CHN_NULL) &&
                TreatmentApplication.getSharedPreferences().getBoolean(Channels.KEY_CHN_IS_OPEN_PREFIX + channelNo, false);
    }

    /**
     * do something about the open-concerned channel
     **/
    protected abstract void handleOpenChannel(int channelNo,int frameValue);

    /**
     * change the display of battery icon according to sharedPreference.
     *
     * @param value the info received
     **/
    protected abstract void onReceiveBatteryFrame(int[] value);

    protected abstract void getPotentiometerMode(Boolean b);

    /**
     * when mode is Potentiometer
     *
     * @return Potentiometer Corresponding values
     */
    protected abstract void getPotentiometerValue(int value, int channelNo);

}
