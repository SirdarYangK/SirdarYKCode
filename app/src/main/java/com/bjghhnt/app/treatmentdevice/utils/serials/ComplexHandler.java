package com.bjghhnt.app.treatmentdevice.utils.serials;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.orhanobut.logger.Logger;

/**
 * The parent of inner static handler classes in MeterSetActivity and FullPanelActivity
 **/
public abstract class ComplexHandler extends SerialHandler {

    public ComplexHandler(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    protected void handleOpenChannel(int channelNo,int frameValue) {
//        int frameValue = SerialFrame.getFrameValue(mHexStringReceived);
        Logger.d("jjkk-channelNo:frameValue--" + channelNo + ":" + frameValue);
        Logger.d("jjkk-isChannelDisplayed--" + isChannelDisplayed(channelNo));
        Logger.d("jjkk-ComplexHandlerHexStringReceived--" + mHexStringReceived);
        Logger.d("jjkk-frameMode--" + SerialFrame.getFrameMode(mHexStringReceived));
        if (isChannelDisplayed(channelNo)) {
            if (SerialFrame.getFrameMode(mHexStringReceived).equals(SerialFrame.MODE_RECEIVE_LEVEL)) {
                onReceiveSetLevelDisplay(frameValue, channelNo);
                Logger.d("level-channelNo:frameValue--" + channelNo + ":" + frameValue);
            }
           else { // when reporting remaining time
                onReceiveSetProgressDisplay(frameValue, channelNo);
                // if time's up, reset the preference
                if (frameValue == 0) {
                    resetPreferenceOfOneChn(channelNo);
                    changeLayout();
                }
            }
        } else {
            // if a channel that is not displayed has finished working,
            // reset its preferences (sensible in MeterSetActivity)
            if (SerialFrame.getFrameMode(mHexStringReceived).equals(SerialFrame.MODE_RECEIVE_TIME) &&
                    frameValue == 0) {
                resetPreferenceOfOneChn(channelNo);
            }
        }
    }

    /**
     * only return true if the Channel (according to the channelNo the frame contains)
     * is displayed in the current activity
     **/
    protected abstract boolean isChannelDisplayed(int channelNo);

    /**
     * when receiving a timing frame, update the progress display on the meter view
     **/
    protected abstract void onReceiveSetProgressDisplay(int frameValue, int channelNo);

    /**
     * when receiving a level frame, update the level display on the meter view
     * according to the function on the single chip machine, this method may be redundant
     **/
    protected abstract void onReceiveSetLevelDisplay(int frameValue, int channelNo);

    /**
     * when a channel is done, change the layout of the current activity slightly
     **/
    protected abstract void changeLayout();


}
