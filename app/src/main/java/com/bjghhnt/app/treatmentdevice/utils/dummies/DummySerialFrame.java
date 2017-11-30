package com.bjghhnt.app.treatmentdevice.utils.dummies;

import com.bjghhnt.app.treatmentdevice.utils.serials.SerialFrame;

import static com.bjghhnt.app.treatmentdevice.utils.serials.CRC16ansi.getCRC;

/**
 * this class is used to forge timing frame
 * Created by Q on 27/01/2016.
 */
class DummySerialFrame {

	//private static final String TAG = "DummySerialFrame";

	public static String forgeTimeReportFrame(int chnNo, int remainingTime) {
		String result;
		String stringForCRC = SerialFrame.RECEIVE_TIME[chnNo - 1] + String.format("%02X", (0xFF & remainingTime));
		String crc = getCRC(stringForCRC);
		//Log.d(TAG, "crc: " + crc);

		result = SerialFrame.FRAME_HEAD + SerialFrame.DATA_LENGTH_NON_SEND
				+ stringForCRC + crc;
		//Log.d(TAG, "whole frame is: " + result);
		return result;
	}
}
