package com.bjghhnt.app.treatmentdevice.utils.serials;

import com.bjghhnt.app.treatmentdevice.utils.SerialportControler;

/**
 * Created by SirdarYangK on 2017/9/19.
 * Function description：
 */

public class ReceiveThread2 extends Thread {

    private final SerialHandler mHandler;
    private final SerialportControler mSerialPort;

    public ReceiveThread2( SerialportControler mSerialPort,SerialHandler mHandler) {
        this.mHandler = mHandler;
        this.mSerialPort = mSerialPort;
    }
}
