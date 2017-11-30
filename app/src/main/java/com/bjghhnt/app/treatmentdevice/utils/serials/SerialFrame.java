package com.bjghhnt.app.treatmentdevice.utils.serials;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.orhanobut.logger.Logger;

import java.util.Locale;

/**
 * for testing purposes
 * Created by Q on 07/06/2016.
 */
@SuppressWarnings("unused")
public class SerialFrame {


    public static final int HEX_STRING_LENGTH_SEND = 20;

    private static final int HEX_STRING_LENGTH_NON_SEND = 18;

    private static final int POS_CRC_H = 14;

    private static final int POS_DATA_SIZE_H = 12;

    private static final int POS_ADDR_CHN_H = 10;

    private static final int POS_ADDR_MODE_H = 8;

    private static final int POS_COMMAND_H = 6;

    private static final int POS_LENGTH_L = 5;

    /** addr - level 0* **/
    /**
     * addr - time 1*
     **/
    public static final String MODE_RECEIVE_LEVEL = "68201";//

    public static final String MODE_RECEIVE_TIME = "68202";

    public static final String MODE_RECEIVE_POTENTIOMETER = "68203";

    private static final String MODE_RECEIVE_REQUEST_LEVEL = "68301";

    private static final String MODE_RECEIVE_REQUEST_TIME = "68302";

    private static final String MODE_RECEIVE_BATTERY_INFO = "68211";

    public static final String MODE_SEND_LEVEL = "78301";

    public static final String MODE_SEND_TIME = "78302";

    public static final String FRAME_HEAD = "A55A";

    public static final String ADDR_BATTERY_INFO_UNPLUGGED = "1122";

    public static final String ADDR_BATTERY_INFO_PLUGGED = "1133";

    private static final String DATA_LENGTH_SEND = "07";

    public static final String DATA_LENGTH_NON_SEND = "06";

    /**
     * command + identifier
     **/
    public static final String[] RECEIVE_LEVEL = {"820101", "820102", "820103", "820104", "820105"};

    public static final String[] RECEIVE_TIME = {"820201", "820202", "820203", "820204", "820205"};

    /**
     * command + identifier
     **/
    public static final String[] RECEIVE_REQUEST_LEVEL = {"830101", "830102", "830103", "830104", "830105"};

    public static final String[] RECEIVE_REQUEST_TIME = {"830201", "830202", "830203", "830204", "830205"};

    /**
     * command + identifier + data length
     **/
    private static final String[] SEND_LEVEL = {"83010101", "83010201", "83010301", "83010401", "83010501"};

    private static final String[] SEND_TIME = {"83020101", "83020201", "83020301", "83020401", "83020501"};

    private static final String STRING_REPORTING_TIME = "remaining time: %3d minutes";

    public static final String STRING_SETTING_TIME = "setting time: %3d minutes";

    private static final String STRING_REQUESTING_TIME = "device requesting the remaining time of Channel %1d";

    private static final String STRING_REPORTING_LEVEL = "current level: %2d";

    public static final String STRING_SETTING_LEVEL = "setting level: %2d";

    private static final String STRING_REQUESTING_LEVEL = "device requesting the level of Channel %1d";

    private static final String TAG = "Frame helper class: ";

    private static final int CODE_POWER_UNPLUGGED = 1;

    private static final int CODE_POWER_PLUGGED = 2;

    private static int channelIndex;

    private static boolean potentiometerTag = false;
    private static int potentiometerValueTag = 0;

    public static void setChannelIndex(int channelNo) {
        if (channelNo < Channels.CHN_ONE || channelNo > Channels.CHN_FIVE) {
            //TODO throw an exception
        } else {
            channelIndex = channelNo - 1;
        }
    }

    public static String makeTimingFrameToSend(int value) {
        String stringForCRC = SEND_TIME[channelIndex] + String.format("%02X", value);
        String crc = CRC16ansi.getCRC(stringForCRC);
        Log.d(TAG, "crc: " + crc);
        return FRAME_HEAD + DATA_LENGTH_SEND + stringForCRC + crc;
    }

    public static String makeLevelFrameToSend(int value) {
        String stringForCRC = SEND_LEVEL[channelIndex] + String.format("%02X", value);
        System.out.println(stringForCRC);
        String crc = CRC16ansi.getCRC(stringForCRC);
        Log.d(TAG, "crc: " + crc);
        return FRAME_HEAD + DATA_LENGTH_SEND + stringForCRC + crc;
    }

    //get potentiometer mode
    public static boolean isPotentiometerMode(String hexStringReceived) {
//        boolean isPotentiometer = hexStringReceived.substring(POS_LENGTH_L, POS_ADDR_CHN_H)//5-10
//                .equals(MODE_RECEIVE_POTENTIOMETER);
        boolean isPotentiometer = getFrameMode(hexStringReceived).equals(MODE_RECEIVE_POTENTIOMETER);
        potentiometerTag = isPotentiometer;
        return isPotentiometer;// 68203
    }

    // return the code of the mode
    public static String getFrameMode(String hexStringReceived) {
        return hexStringReceived.substring(POS_LENGTH_L, POS_ADDR_CHN_H);//5-10
    }

    // return the value
    public static int getFrameValue(String hexStringReceived) {
        if (hexStringReceived.length() != HEX_STRING_LENGTH_NON_SEND) {
            // TODO throw an exception somewhere
            return 0xFF;
        }
        String substring = hexStringReceived.substring(POS_DATA_SIZE_H, POS_CRC_H);
        int result = Integer.parseInt(substring, 16);
        Logger.d("lose----Integer----" + result);
        potentiometerValueTag = result;
        return result;
    }

    // return the channel number of the Chn
    public static int getFrameChn(String hexStringReceived) {
        int chnNoReceived = Integer.parseInt(
                hexStringReceived.substring(POS_ADDR_CHN_H, POS_DATA_SIZE_H), 16);//10-12
        if (chnNoReceived < Channels.CHN_ONE || chnNoReceived > Channels.CHN_FIVE) {
            //TODO throw an exception somewhere
            return Channels.CHN_NULL;
        }
        return chnNoReceived;
    }

    // human readable information contained in the frame received
    public static String interpretReceivedFrame(@NonNull String hexStringReceived) {
        String mode;
        boolean isReportingValue = false;
//        boolean isPotentiometer = false;
        String modeReceived = hexStringReceived.substring(POS_LENGTH_L, POS_ADDR_CHN_H);//5-10
        switch (modeReceived) {
            case MODE_RECEIVE_LEVEL:
                mode = STRING_REPORTING_LEVEL;
                isReportingValue = true;
                break;
            case MODE_RECEIVE_TIME:
                mode = STRING_REPORTING_TIME;
                isReportingValue = true;
                break;
            case MODE_RECEIVE_REQUEST_LEVEL:
                mode = STRING_REQUESTING_LEVEL;
                break;
            case MODE_RECEIVE_REQUEST_TIME:
                mode = STRING_REQUESTING_TIME;
                break;
//            case MODE_RECEIVE_POTENTIOMETER://电位器
//                isPotentiometer = true;
//                mode = STRING_REQUESTING_TIME;
//                break;
            default:
                return null;
        }

        if (isReportingValue) {
            //TODO handel the channel; no exception where this method is called
            return String.format(Locale.US, "Channel %1d: ", getFrameChn(hexStringReceived)) + String.format(mode, getFrameValue(hexStringReceived));
        } else {
            return String.format(mode, getFrameChn(hexStringReceived));
        }
    }

    public static boolean isReceivedFrameValid(String hexStringReceived) {
        // return false if got wrong frame length
        String s = CRC16ansi.getCRC(hexStringReceived
                .substring(POS_COMMAND_H, POS_CRC_H));//6-14
        String s1 = hexStringReceived
                .substring(POS_CRC_H, HEX_STRING_LENGTH_NON_SEND);//14-18
        Logger.d("判断是否有效 CRC16ansi----" + s + "--hexStringReceived--" + s1 + "  hexStringReceived.length" + hexStringReceived.length());
        return hexStringReceived.length() == HEX_STRING_LENGTH_NON_SEND &&
                s.equals(s1);
    }

    public static String makeBootDoneFrame() {
        return "A55A0783668801013F2C";
    }

    public static String makeUseKnobFrame() {
        return "A55A078333440101EEDF";
    }

    public static String makeUseTouchFrame() {
        return "A55A0783339901017EE5";
    }

    //电量模式
    public static boolean isBatteryInfo(String hexStringReceived) {
        return hexStringReceived.substring(POS_LENGTH_L, POS_ADDR_CHN_H)//5-10
                .equals(MODE_RECEIVE_BATTERY_INFO);// 68211
    }

    public static int[] getBatteryInfo(String hexStringReceived) {
        int[] BatteryInfo = new int[2];
        BatteryInfo[0] = (hexStringReceived.substring(POS_ADDR_MODE_H, POS_DATA_SIZE_H)
                .equals(ADDR_BATTERY_INFO_UNPLUGGED)) ? CODE_POWER_UNPLUGGED : CODE_POWER_PLUGGED;
        BatteryInfo[1] = Integer.parseInt(hexStringReceived
                .substring(POS_DATA_SIZE_H, POS_CRC_H), 16);//截取12-14的字节得到当前电量
        return BatteryInfo;
    }

    public static boolean getIsPotentiometer() {
        return potentiometerTag;
    }

    public static int getPotentiometerValue() {
        return potentiometerValueTag;
    }

//    public static int getPotentiometer(String hexStringReceived) {
//
//
//    }


    /**
     * for testing purposes only
     * **/
//	public static void main(String[] args) {
//		String frame1 = "A55A068201010F3808";
//		System.out.println("--------frame interpretation test---------");
//		System.out.println(isReceivedFrameValid(frame1));
//		System.out.println(interpretReceivedFrame(frame1));
//		System.out.println(getFrameChn(frame1));
//		System.out.println(getFrameMode(frame1));
//
//		setChannelIndex(2);
//		System.out.println("-------frame making test---------------");
//		System.out.println(makeLevelFrameToSend(12));
//		System.out.println(makeTimingFrameToSend(120));
//
//	}

}
