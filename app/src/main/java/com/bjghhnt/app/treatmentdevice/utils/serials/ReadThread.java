package com.bjghhnt.app.treatmentdevice.utils.serials;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.utils.SerialportControler;
import com.orhanobut.logger.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by SirdarYangK on 2017/9/18.
 * Function description：捷德接收数据线程
 */

public class ReadThread extends Thread {

    private SerialHandler mHandler;
    private SerialportControler mSerialportControler;
    private String result = null;
    StringBuffer stringBuffer = new StringBuffer();
    private String manageStr;
    private int indexHand;
    private String subResult;
    //    private Message msg;

    public ReadThread(SerialportControler serialportControler, SerialHandler mHandler) {
        this.mHandler = mHandler;
        this.mSerialportControler = serialportControler;
        Log.i("ReadThread", "onStart-->开线程读取报文");
    }

    //创建队列集合
    private Queue<String> queueList = new LinkedList<String>();
    //接收串口返回的数据
    private StringBuilder comdatas = new StringBuilder();

    //    @Override
    public void run() {
        //没有中断标记,持续读取数据
        while (mSerialportControler.isOpen()) {
            try {
                result = mSerialportControler.receiveData("HEX");
//                try {
//                    Thread.sleep(50);//延时
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if (this.result != null) {
                    resultDataCheck(result);
                    isHoldSucess(result);
                    int indexHead = comdatas.indexOf("A5 5A");
                    Log.i("ReadThread", "yykk等待接收的数据ReadThread -- " + result + "--" + result.length());
                    Log.i("ReadThread", "yykk--indexHead ----- " + indexHead + "---" + comdatas.length());
                    if (this.comdatas != null && indexHead != -1 && comdatas.length() >= 27) {
                        subResult = comdatas.substring(indexHead, indexHead + 27);
                        Message msg = new Message();
                        msg.obj = subResult;
                        mHandler.sendMessage(msg);
                        Log.i("ReadThread", "yykk-----发送结果mHandler：" + subResult);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                comdatas.delete(0, comdatas.length());
                Log.i("ReadThread", "yykk--stringBuffer清空 ------- " + comdatas.length());
            }
        }
    }

    //对结果进行处理拼接
    private void resultDataCheck(String data) {
        if (data.indexOf("5A") == 0) {
            result = "A5 " + data;
        } else if (data.indexOf("06") == 0) {
            result = "A5 5A " + data;
        } else if (data.indexOf("82") == 0) {
            result = "A5 5A 06 " + data;
        } else if (data.indexOf("01") == 0) {
            result = "A5 5A 06 82 " + data;
        } else if (data.indexOf("02") == 0) {
            result = "A5 5A 06 82 " + data;
        } else if (data.indexOf("03") == 0) {
            result = "A5 5A 06 82 " + data;
        } else if (data.indexOf("04") == 0) {
            result = "A5 5A 06 82 " + data;
        } else {
            if (data.length() < 27) return;
            int index = data.indexOf("A5 5A");
            while (index != -1) {
//                 int startIndex = index + 27;
//                 data = data.substring(startIndex);
                result = data.substring(index, index + 27);
                data = data.substring(index + 27);
                // 继续查
                index = data.indexOf("A5 5A");
//                String substring = data.substring(index, data.length() + 1);
//                result = substring;
            }
        }
    }

    //将数据加入队列
    public synchronized void AddQueue(String recevie) {
        queueList.add(recevie);
    }

    //保留成功
    protected void isHoldSucess(String comData) {
        comdatas.append(comData);
    }
}
