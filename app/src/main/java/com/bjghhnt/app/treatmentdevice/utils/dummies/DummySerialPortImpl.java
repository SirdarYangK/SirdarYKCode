package com.bjghhnt.app.treatmentdevice.utils.dummies;

import android.content.SharedPreferences;
import android.util.Log;

import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.interfaces.SerialConnectable;
import com.bjghhnt.app.treatmentdevice.utils.Channels;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * this is the dummy serial port class for testing purposes
 * Created by Q on 27/01/2016.
 */
public class DummySerialPortImpl extends TimerTask implements SerialConnectable, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = "DummySerialPortImpl";

	private boolean isOpen;

	private boolean mForgeIt;

	private boolean mFirstReport;

	private String mData;

	private final Timer mTimer;

	private final int[] mRemainingTime;

	private final int[] mStartTime;

	private final int[] mSessionTime;

	private final ArrayList<Integer> mChannels;

	private final SharedPreferences mSharedPreferences;

	@SuppressWarnings({"SameParameterValue", "UnusedParameters"})
	public DummySerialPortImpl(String devNum, int speed, int dataBits, int stopBits, int parity) {
		this.isOpen = true;
		this.mFirstReport = true;
		mTimer = new Timer();
		// because the ReceiveThread in FullPanelActivity Thread.sleep(100) Thread.sleep(1)
		// you don't want it to miss an update and you don't want it to receive the same frame too many times
		mTimer.schedule(this, 200, 200);
		mStartTime = new int[Channels.TOTAL_CHN_NUM];
		mSessionTime = new int[Channels.TOTAL_CHN_NUM];
		mRemainingTime = new int[Channels.TOTAL_CHN_NUM];
		mChannels = new ArrayList<>(Channels.TOTAL_CHN_NUM);
		mSharedPreferences = TreatmentApplication.getSharedPreferences();
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		init();
	}

	@Override
	public void closeSerial() {
		this.isOpen = false;
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean isOpen() {
		return this.isOpen;
	}

	@Override
	public String receiveData(String type) {
		if (!mForgeIt)
			return null;
		//TODO this line "mForgeIt = false;" is for a cleaner logcat, comment it for a more realistic mock
		mForgeIt = false;
		return mData;
	}

	@Override
	public void sendData(String data, String type) {
		Log.d(TAG, "sendData: " + data);
	}

	@Override
	public void run() {
		if (isOpen) {
			int chnNo = mChannels.get(0);
			// mimic the the reporting frame immediately sent as the setting is submitted
			if (mFirstReport) {
				mForgeIt = true;
				mData = DummySerialFrame.forgeTimeReportFrame(chnNo, mRemainingTime[chnNo - 1]);
				mChannels.add(mChannels.remove(0));
				if (mChannels.get(0) == Channels.CHN_ONE) {
					mFirstReport = false;
				}
			} else {
				if (getTimeDiff(chnNo) > 59) { // if 59 seconds have passed
					mForgeIt = true;

					if (mRemainingTime[chnNo - 1] > 0) {
						mRemainingTime[chnNo - 1]--;
					}
					mData = DummySerialFrame.forgeTimeReportFrame(chnNo, mRemainingTime[chnNo - 1]);
				} else {
					mForgeIt = false;
				}
				mChannels.add(mChannels.remove(0));
			}
		} else {
			mTimer.cancel();
		}
	}

	private int getTimeDiff(int chnNo) {
		return (int) (System.currentTimeMillis() / 1000)
				- mStartTime[chnNo - 1]
				- 60 * (mSessionTime[chnNo - 1] - mRemainingTime[chnNo - 1]);
	}

	private void init() {
		for (int chnNo = 1; chnNo <= Channels.TOTAL_CHN_NUM; chnNo++) {
			mStartTime[chnNo - 1] = mSharedPreferences
					.getInt(Channels.KEY_CHN_START_TIME_PREFIX + chnNo, 0);

			mChannels.add(chnNo);

			mSessionTime[chnNo - 1] = mSharedPreferences
					.getInt(Channels.KEY_CHN_SESSION_TIME_PREFIX + chnNo, 0);

			mRemainingTime[chnNo - 1] = mSessionTime[chnNo - 1];
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.contains(Channels.KEY_CHN_SESSION_TIME_PREFIX)) {
			int chnThatChanged = Integer.parseInt(key.substring(key.length() - 1));

			// refresh the times
			mStartTime[chnThatChanged - 1] = sharedPreferences
					.getInt(Channels.KEY_CHN_START_TIME_PREFIX + chnThatChanged, 0);
			mSessionTime[chnThatChanged - 1] = sharedPreferences
					.getInt(Channels.KEY_CHN_SESSION_TIME_PREFIX + chnThatChanged, 0);
			mRemainingTime[chnThatChanged - 1] = mSessionTime[chnThatChanged - 1];
		}
	}
}
