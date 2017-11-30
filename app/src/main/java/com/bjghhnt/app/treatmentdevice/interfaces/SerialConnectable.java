package com.bjghhnt.app.treatmentdevice.interfaces;

import java.io.IOException;

/**
 * the interface to implement on Real and dummy serial port class
 * Created by Q on 28/01/2016.
 */
public interface SerialConnectable {

	@SuppressWarnings("SameParameterValue")
	String receiveData(String type) throws IOException;

	@SuppressWarnings("SameParameterValue")
	void sendData(String data, String type);

	void closeSerial();

	boolean isOpen();
}
