package com.bjghhnt.app.treatmentdevice.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.interfaces.SerialConnectable;
import com.bjghhnt.app.treatmentdevice.utils.SerialportControler;
import com.bjghhnt.app.treatmentdevice.utils.dummies.DummySerialPortImpl;
import com.bjghhnt.app.treatmentdevice.utils.serials.ReadThread;
import com.bjghhnt.app.treatmentdevice.utils.serials.RealSerialPortImpl;
import com.bjghhnt.app.treatmentdevice.utils.serials.ReceiveThread;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialFrame;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SimpleHandler;
import com.orhanobut.logger.Logger;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 基类
 */
public abstract class MinorActivity extends AppCompatActivity implements Observable.OnSubscribe<String>{
    protected SerialConnectable mSerialPort;
    protected SerialportControler serialportControler;
    private final SimpleHandler mHandler = new SimpleHandler(this);
    protected String formatTime;
//    protected View inflate;
    //无法获取网络时间
    private static final String SET_NET_TIME_ERROR = "SET_NET_TIME_ERROR";
    private static final String UPDATE_TIME_UI = "UPDATE_TIME_UI";
    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onNext(String s) {
            switch (s) {
                case SET_NET_TIME_ERROR:
                    Toast.makeText(MinorActivity.this, "网络获取时间失败，请检查网络", Toast.LENGTH_LONG).show();
                    Logger.d("time_minorAct:error");
                    break;
                case UPDATE_TIME_UI:
                    long sysTime = System.currentTimeMillis();
                    Date date = new Date(sysTime);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
//                    CharSequence sysTimeStr =  DateFormat.format("yyyy-MM-dd hh:mm:ss", sysTime);
                    formatTime = sdf.format(date);
                    upDateTime();
//                    Logger.d("time_minorAct:"+formatTime);
                    break;
            }
        }
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
    };

    @Override
    public void call(Subscriber<? super String> subscriber) {
        getNetTime(subscriber);
        upDateTimeUI(subscriber);
    }
    private void upDateTimeUI(Subscriber<? super String> observable) {
        do {
            try {
                Thread.sleep(1000);
                observable.onNext(UPDATE_TIME_UI);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }
    private void getNetTime(Subscriber<? super String> observable) {
        URL url = null;//取得资源对象
        try {
            url = new URL("http://time.windows.com/");
//                url = new URL("http://www.baidu.com/");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            boolean isSuc = SystemClock.setCurrentTimeMillis(ld);
        } catch (IOException e) {
            observable.onNext(SET_NET_TIME_ERROR);
        }
    }
   protected abstract void upDateTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        inflate = getLayoutInflater().inflate(R.layout.layout_titlebar,null);
        Logger.d("minor--->onCreate");
        if (BuildConfig.MIPS || BuildConfig.ROCKCHIP) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        Observable.create(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    protected void onStart() {
        Logger.d("MinorActivity ------->  onStart");
        super.onStart();
        // open the serial port
        if (BuildConfig.MIPS) {
            Logger.d("systemTpye   ------->    MIPS");
            mSerialPort = new RealSerialPortImpl("S0", 115200, 8, 1, 'n');
            //TODO will this anonymous ReceivedThread object be GCed after run() is done?
            new ReceiveThread(mSerialPort, setHandler()).start();
            //TODO fix this
            mSerialPort.sendData(SerialFrame.makeBootDoneFrame(), "HEX");
        } else if (BuildConfig.ROCKCHIP) {
            Logger.d("systemTpye   ------->    ROCKCHIP");
            serialportControler = new SerialportControler("/dev/ttyS0", 115200);
//            serialportControler = new SerialportControler("/dev/ttyAMA1", 115200);
            serialportControler.sendData(SerialFrame.makeBootDoneFrame(), "HEX");
            new ReadThread(serialportControler, setHandler()).start();
        } else {
            Logger.d("systemTpye   ------->    DummySerialPort");
            mSerialPort = new DummySerialPortImpl("S0", 115200, 8, 1, 'n');
//            new ReceiveThread(mSerialPort, setHandler()).start();
            new ReceiveThread(mSerialPort, setHandler()).start();
            //TODO fix this
            mSerialPort.sendData(SerialFrame.makeBootDoneFrame(), "HEX");
//            mSerialPort.sendData(sendSerialData(), "HEX");
        }
    }
    protected abstract SerialHandler setHandler();
//    protected abstract String sendSerialData();

    @Override
    protected void onStop() {
        super.onStop();
        if (BuildConfig.MIPS) {
            mSerialPort.closeSerial();
        } else if (BuildConfig.ROCKCHIP) {
            serialportControler.closeSerial();
            Logger.d("close---->jiede_Serial");
        }
    }
}
