package com.bjghhnt.app.treatmentdevice.utils.serials;

import com.bjghhnt.app.treatmentdevice.interfaces.SerialConnectable;

import java.io.IOException;

import android_serialport_api.SerialPort;

/**
 * Created by SirdarYangK on 2017/9/30.
 * Function description：
 */

public class RealSerialPortImpl2 implements SerialConnectable {
    public RealSerialPortImpl2() {
    }

    @Override
    public String receiveData(String type) throws IOException {
        return null;
    }

    @Override
    public void sendData(String data, String type) {

    }

    @Override
    public void closeSerial() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }
}
