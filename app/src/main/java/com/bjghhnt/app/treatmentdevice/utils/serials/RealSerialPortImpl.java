package com.bjghhnt.app.treatmentdevice.utils.serials;

import android.util.Log;

import com.bjghhnt.app.treatmentdevice.interfaces.SerialConnectable;
import com.dwin.dwinapi.SerialPort;

import static android.R.attr.data;

/**
 * This class makes use of the SerialPort class provided by D w i n Tech Ltd.
 * Created by Q on 28/01/2016.
 */
public class RealSerialPortImpl implements SerialConnectable {

	private final SerialPort mSerialPort;

	public RealSerialPortImpl(String devNum, int speed, int dataBits, int stopBits, int parity) {
		this.mSerialPort = new SerialPort(devNum, speed, dataBits, stopBits, parity);
	}

	@Override
	public String receiveData(String type) {
        String data = mSerialPort.receiveData(type);
        System.out.println("-----yykk接收的数据realSerial--" + data);
		return data;

	}

	@Override
	public void sendData(String data, String type) {
		mSerialPort.sendData(data, type);
	}

	@Override
	public void closeSerial() {
		mSerialPort.closeSerial();

	}

	@Override
	public boolean isOpen() {
		return mSerialPort.isOpen;
	}
}
