package com.bjghhnt.app.treatmentdevice.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.interfaces.SerialConnectable;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.bjghhnt.app.treatmentdevice.utils.SerialportControler;
import com.bjghhnt.app.treatmentdevice.utils.dummies.DummySerialPortImpl;
import com.bjghhnt.app.treatmentdevice.utils.serials.RealSerialPortImpl;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialFrame;

import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@SuppressWarnings({"WeakerAccess", "unused"})
public class FramingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.edit_text_setting_value)
    EditText mEditTextValue;
    @BindView(R.id.switch_serial_port)
    Switch mSwitchSerialPort;
    @BindView(R.id.text_view_preview)
    TextView mTextViewPreview;
    @BindView(R.id.text_raw_frame)
    TextView mTextViewRawFrame;
    @BindView(R.id.text_pass_or_not)
    TextView mTextViewPassOrNot;
    @BindView(R.id.text_parameters)
    TextView mTextViewParameters;
    private static final int MODE_LEVEL = 1;
    private static final int MODE_TIME = 2;
    private int mSettingMode;
    private String mHexStringToSend;
    private static final String TAG = "FramingActivity";
    private SerialConnectable mSerialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SerialPort nSerialPort;
    private SerialportControler serialportControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_framing);
//        initSerial();


    }

    /**
     * 初始化串口信息
     */
    private void initSerial() {
//        serialportControler = new SerialportControler("/dev/ttyS3", 9600);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ButterKnife.bind(this);
        mSwitchSerialPort.setOnCheckedChangeListener(this);

    }

    @OnClick(R.id.btn_framing)
    void framing() {
        int value = Integer.parseInt(mEditTextValue.getText().toString());
        if (mSettingMode == MODE_LEVEL) {
            mHexStringToSend = SerialFrame.makeLevelFrameToSend(value);
        } else if (mSettingMode == MODE_TIME) {
            mHexStringToSend = SerialFrame.makeTimingFrameToSend(value);
        }
        Log.d(TAG, "framing: whole frame is: " + mHexStringToSend);
        mTextViewPreview.setText(mHexStringToSend);

    }

    @OnClick(R.id.btn_send)
    void sendFrame() {
        if (BuildConfig.MIPS) {
            if (mSerialPort == null || !mSerialPort.isOpen()) {
                return;
            }
            mSerialPort.sendData(mHexStringToSend, "HEX");
        }
        if (BuildConfig.ROCKCHIP) {
            if (serialportControler == null || !serialportControler.isOpen()) {
                return;
            }
            serialportControler.sendData(mHexStringToSend, "HEX");
        }

    }

    public void onModeRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_btn_time:
                mSettingMode = MODE_TIME;
                break;
            case R.id.radio_btn_level:
                mSettingMode = MODE_LEVEL;
                break;
            default:
                break;
        }
    }

    public void onChannelRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_btn_port1:
                SerialFrame.setChannelIndex(Channels.CHN_ONE);
                break;
            case R.id.radio_btn_port2:
                SerialFrame.setChannelIndex(Channels.CHN_TWO);
                break;
            case R.id.radio_btn_port3:
                SerialFrame.setChannelIndex(Channels.CHN_THREE);
                break;
            case R.id.radio_btn_port4:
                SerialFrame.setChannelIndex(Channels.CHN_FOUR);
                break;
            case R.id.radio_btn_port5:
                SerialFrame.setChannelIndex(Channels.CHN_FIVE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_serial_port:
                if (isChecked) {
                    // open the serial port
                    if (BuildConfig.MIPS) {
                        mSerialPort = new RealSerialPortImpl("S0", 115200, 8, 1, 'n');
                    } else if (BuildConfig.ROCKCHIP) {
                        serialportControler = new SerialportControler("/dev/ttyS0", 115200);

                    } else {
                        mSerialPort = new DummySerialPortImpl("S0", 115200, 8, 1, 'n');
                    }
//                    new ReceiveThread().start();
//                    new ReadThread();
                } else {
                    if (BuildConfig.ROCKCHIP) {
                        // TODO ROCKCHIP
                        serialportControler.closeSerial();
                    } else if (BuildConfig.MIPS) {
                        mSerialPort.closeSerial();
                    }
                }
                break;
            default:
                break;

        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mTextViewRawFrame.setText((String) msg.obj);
            String hexStringReceived = ((String) msg.obj).replaceAll("\\s", "");
            if (SerialFrame.isReceivedFrameValid(hexStringReceived)) {
                mTextViewPassOrNot.setText("有效");
                mTextViewParameters.setText(SerialFrame.interpretReceivedFrame(hexStringReceived));
            } else {
                mTextViewPassOrNot.setText("无效");
                mTextViewParameters.setText("N/A");
            }

        }
    };

    /**
     * the thread for receiving serial data
     */
//    private class ReceiveThread extends Thread {
//
//        public void run() {
//
//            while (mSerialPort.isOpen()) {
//                String data = mSerialPort.receiveData("HEX");
//                if (data != null) {
//                    Message msg = new Message();
//                    msg.obj = data;
//                    mHandler.sendMessage(msg);
//                }
//                try {
//                    //TODO was Thread.sleep(100);
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    /**
     * 读取线程、获取外设返回的数据
     */
//    StringBuffer stringBuffer = new StringBuffer();
//
//    class ReadThread extends Thread {
//
//
//        //字符数组、接收数据
//        byte[] buffer = new byte[2048];
//
//        @Override
//        public void run() {
//            super.run();
//            //死循环、保持接收状态
//            while (true) {
//                try {
//                    Log.i("tagtest", "等待接收串口3数据");
//
//                    int size2 = inputStream.read(buffer);
//                    if (size2 > 0) {
//                        String result2 = new String(buffer, 0, size2).trim().toString();
//                        Log.i("tagtest", "接收到数据3长度：" + size2);
//                        Log.i("tagtest", "接收到数据3：" + result2);
//                        //读到的数据建议设立缓存，不能保证一次完整发送一包，有时会2次发一个
//                        stringBuffer.append(result2);
//                        //读到的数据显示到页面
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                while (serialportControler.isOpen()) {
//
//                                }
//                                stringBuffer.delete(0, stringBuffer.length());
//                            }
//                        });
//                    } else {
//                        Log.i("tagtest", "未读到串口3返回的数据");
//                    }
//                    Thread.sleep(400);
//
//                } catch (Exception e) {
//                    Log.i("tagtest", e.toString());
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
}