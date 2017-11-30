package com.bjghhnt.app.treatmentdevice.utils;

/**
 * Created by Development_Android on 2017/1/11.
 */

public class JniUtils {

    static {
        System.loadLibrary("SupperPassword");//之前在build.gradle里面设置的so名字，必须一致
    }

    public static native long getSupperPassword(int times);
}
