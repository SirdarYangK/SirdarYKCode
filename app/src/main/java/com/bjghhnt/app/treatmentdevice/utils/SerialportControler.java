package com.bjghhnt.app.treatmentdevice.utils;

import android.util.Log;

import com.bjghhnt.app.treatmentdevice.interfaces.SerialConnectable;
import com.bjghhnt.app.treatmentdevice.utils.serials.ReadThread;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.Queue;

import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2017/3/2.
 * 串口控制器
 */

public class SerialportControler implements SerialConnectable {

    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private String result;
    private SerialportControler.SendThread mSendThread;
    //    private SerialportControler.ReadThread mReadThread;
    private boolean isOpen = false;
    private byte[] loopData = new byte[]{0x30};//用于保存要发送到串口的数据
    private int iDelay = 500;//避免长久占用cpu sleep 500ms
    //初始化参数
    private String mPort = "/dev/ttyS0";//默认外设设备号
    private int baudRate = 115200;//默认的波特率
    private String subResult;

    /**
     * 构造方法(无参)
     */
    private SerialportControler() {

    }

    /**
     * 构造方法(含参)
     *
     * @param port     外设设备号
     * @param baudRate 波特率
     */
    public SerialportControler(String port, int baudRate) {
        this.mPort = port;
        this.baudRate = baudRate;
        try {
            this.open(port, baudRate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取串口控制器实例
     */
    public static SerialportControler getInstance() {
        return SerialportControlerHolder.serialportControler;
    }

    /**
     * 串口控制器持有类
     */
    private static class SerialportControlerHolder {
        private static final SerialportControler serialportControler = new SerialportControler();
    }

    /**
     * 判断设置波特率是否成功
     *
     * @param iBaud 传入波特
     * @return 返回结果(真/假)
     */
    public boolean setBaudRate(int iBaud) {
        if (isOpen) {
            return false;
        } else {
            baudRate = iBaud;
            return true;
        }
    }

    /**
     * 设置波特率
     *
     * @param sBaud 输入波特率
     * @return 返回设置结果
     */
    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    /**
     * 获取接口
     *
     * @return
     */
    public String getPort() {
        return mPort;
    }

    /**
     * 判断设置接口是否成功
     *
     * @param port
     * @return
     */
    public boolean setPort(String port) {
        if (isOpen) {
            return false;
        } else {
            this.mPort = port;
            return true;
        }
    }

    /**
     * 判断串口是否打开
     *
     * @return
     */
    @Override
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 串口开关
     *
     * @return
     */
    public void setSerialOpen(Boolean b) {
        isOpen = b;
    }

    /**
     * 获取要发送到串口的数据
     *
     * @return
     */
    public byte[] getbLoopData() {
        return loopData;
    }

    /**
     * 设置要发送到串口的数据
     *
     * @param bLoopData
     */
    public void setbLoopData(byte[] bLoopData) {
        this.loopData = bLoopData;
    }

    /**
     * 打开串口，并开启发送线程
     *
     * @throws SecurityException
     * @throws IOException
     * @throws InvalidParameterException
     */
    public void open(String port, int baudRate) throws SecurityException, IOException, InvalidParameterException {
        Log.i("SerialportControler", "go------>  openSerialPort");
        mSerialPort = new SerialPort(new File(port), baudRate, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
//        Logger.d("result ------>  mInputStream：" + mInputStream + ";   mOutputStream: " + mOutputStream);
//        ReadThread mReadThread = new ReadThread();
//        mReadThread.start();
        isOpen = true;
    }

    /**
     * 关闭串口
     */
    //捷德原有
//    public void close() {
//        if (mSerialPort != null) {
//            mSerialPort.close();
//            mSerialPort = null;
//        }
//        isOpen = false;
//    }
    @Override
    public void closeSerial() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        isOpen = false;
    }

    /**
     * 发送数据到串口
     */
    public synchronized void sendSerialPortData(byte[] bOutArray) {
        try {
            mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //捷德原有
//    public synchronized void sendSerialPortData(String data, String type) {
//        Logger.d("SerialportControler发送的数据 --------" + data + ";;;" + data.length());
//        try {
//            mOutputStream.write(TransformUtils
//                    .hexStringToBytes(TransformUtils.encode(data)));


//            byte[] hices = type.equals("HEX") ? HexString2Bytes(data.length() % 2 == 1 ? data + "0" : data.replace(" ", "")) : HexString2Bytes(this.toHexString(data));
//            mOutputStream.write(hices);
//            mOutputStream.write(HexString2Bytes(data.replace(" ", "")));
//            Logger.d("发送数据处理过的--------" + hices.length);
//            wait(100);
//            new ReadThread().start();
//            Logger.d("SerialportControler开启线程了吗 --------");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public synchronized void sendData(String data, String type) {

        try {
//            mOutputStream.write(TransformUtils
//                    .hexStringToBytes(TransformUtils.encode(data)));
            byte[] hices = type.equals("HEX") ? HexString2Bytes(data.length() % 2 == 1 ? data + "0" : data.replace(" ", "")) : HexString2Bytes(this.toHexString(data));
            mOutputStream.write(hices);
            wait(300);
//            Thread.sleep(500);//连续两次写操作建议间隔至少200ms
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d("SerialportControler发送的数据 --------" + data + ";;;" + data.length());
    }

    StringBuffer stringBuffer = new StringBuffer();
    String str = "";

    @Override
    public String receiveData(String type) throws IOException {
        byte[] buffer = new byte[1024];
        if (mInputStream == null) return null;
        int size = mInputStream.read(buffer);
        //把字节转成String
        Logger.d("yykk-----接收的字节size：" + size);
        if (size > 0) {
            //将接收到字节转换为字符串
            //后改动的
            try {
                this.result = type.equals("HEX") ? bytesToHexString(buffer, size) : (new String(buffer, 0, size, "gb2312")).trim().toString();
//                stringBuffer.append(result);
//                str = stringBuffer.toString();
                Logger.d("yykk-----得到的数据SerialportControler：" + result);
                return this.result;

//                Logger.d("yykk-----接收的数据stringBuffer" + stringBuffer+";;;length："+stringBuffer.length()+";;;size:"+size);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
//            finally {
//                stringBuffer.delete(0, stringBuffer.length());
//                Log.i("ReadThread", "stringBuffer清空 ------- " + stringBuffer.length());
//            }

        } else {
            return null;
        }

    }


    //windi开始
    private static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < tmp.length / 2; ++i) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    private String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }
    //windi结束


    /**
     * 发送指令的线程
     */
    private class SendThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (this) {

                }
                try {
                    Thread.sleep(iDelay);
                    sendSerialPortData(getbLoopData());//先得到封装到SerialHelper的数据再执行发送到串口的操作
                } catch (Error e) {
                    Log.e("CrazyMo", "run: Erro " + e.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送指令到串口
     *
     * @param sOut
     */
    public synchronized void sendPortData(String sOut) {

    }


    /**
     * 读取串口发送数据的线程
     */
//    StringBuilder receive = new StringBuilder();
//    //diwin
//    private String result;
//
//    public class ReadThread extends Thread {
//        private SerialHandler mHandler;
//        public ReadThread() {
//        }
//        public ReadThread(SerialportControler serialportControler, SerialHandler mHandler) {
//            this.mHandler = mHandler;
//            Logger.d("open---->ReadThread ");
//        }
//
//        @Override
//        public void run() {
//
//            //没有中断标记,持续读取数据
//            while (!isInterrupted()) {
//                try {
//                    Logger.d("等待接收数据 ------- " + !isInterrupted());
//                    String result = receiveData("HEX");
//                    //handler接收String类型不能放StringBuilder---》ClassCastException
//                    if (result != null) {
//                        Message msg = new Message();
//                        msg.obj = result;
//                        mHandler.sendMessage(msg);
//                    }
//                    System.out.println("yykk-----接收的数据：：" + result);
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                    return;
//                }
//            }
//            try {
//                sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public static String bytesToHexString(byte[] src, int size) {
        String ret = "";
        if (src != null && size > 0) {
            for (int i = 0; i < size; ++i) {
                String hex = Integer.toHexString(src[i] & 255);
                if (hex.length() < 2) {
                    hex = "0" + hex;
                }

                hex = hex + " ";
                ret = ret + hex;
            }

            return ret.toUpperCase();
        } else {
            return null;
        }
    }

    /**
     * 解析从串口发回的数据
     */
    private class AnalyzeThread extends Thread {
        //创建队列集合
        private Queue<String> queueList = new LinkedList<String>();
        //接收串口返回的数据
        private StringBuilder comdatas = new StringBuilder();

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                String comData;
                while ((comData = queueList.poll()) != null) {
                    isHoldSucess(comData);
                }
                try {
                    Thread.sleep(10);//性能高的话，可以把此数值调小。
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
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
}
