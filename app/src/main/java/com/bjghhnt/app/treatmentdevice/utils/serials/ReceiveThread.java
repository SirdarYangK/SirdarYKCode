package com.bjghhnt.app.treatmentdevice.utils.serials;

import android.os.Message;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.interfaces.SerialConnectable;

import java.io.IOException;

/**
 * The instance of this class is used in MeterSetActivity and FullPanelActivity
 * Created by Q on 15/02/2016.
 */
public class ReceiveThread extends Thread {
    private final SerialConnectable mSerialPort;
    private final SerialHandler mHandler;
    private String data;

    public ReceiveThread(SerialConnectable serialPort, SerialHandler handler) {
        this.mSerialPort = serialPort;
        this.mHandler = handler;
    }

    public void run() {
        while (mSerialPort.isOpen()) {
            try {
                data = mSerialPort.receiveData("HEX");
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("-----yykk接收的数据thread--" + data);
            if (data != null) {
                Message msg = new Message();
                msg.obj = data;
                mHandler.sendMessage(msg);
            }

            try {
                // TODO was Thread.sleep(100);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
