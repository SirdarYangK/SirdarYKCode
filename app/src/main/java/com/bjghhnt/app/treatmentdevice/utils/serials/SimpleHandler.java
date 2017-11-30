package com.bjghhnt.app.treatmentdevice.utils.serials;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.TreatmentApplication;

/**
 * SimpleHandler and ComplexHandler are siblings.
 * Created by Q on 26/02/2016.
 */
public class SimpleHandler extends SerialHandler {

	public SimpleHandler(AppCompatActivity activity) {
		super(activity);
	}

	@Override
	protected void handleOpenChannel(int channelNo,int frameValue) {

//		int frameValue = SerialFrame.getFrameValue(mHexStringReceived);
		if (SerialFrame.getFrameMode(mHexStringReceived).equals(SerialFrame.MODE_RECEIVE_TIME) &&
				frameValue == 0) {
			resetPreferenceOfOneChn(channelNo);
		}
		if (TreatmentApplication.haveAllFinished()) {
			new AlertDialog.Builder(mActivity)
					.setTitle("全部终止")
					.setMessage("全部治疗已经终止,是否需要关机？")
					.setPositiveButton("关机", (dialog, which) -> {
						Toast.makeText(mActivity, "向单片机发送关机报文", Toast.LENGTH_SHORT).show();
						//TODO send a frame to cut the power supply
					})
					.setNegativeButton("取消", (dialog, which) -> {
						//TODO do nothing
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setCancelable(false)
					.show();
		}
	}

	@Override
	protected void onReceiveBatteryFrame(int[] value) {
		//TODO this should be populated
	}

	@Override
	protected void getPotentiometerMode(Boolean b) {

	}

	@Override
	protected void getPotentiometerValue(int value, int channelNo) {

	}

}
