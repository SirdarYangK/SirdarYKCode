package com.bjghhnt.app.treatmentdevice.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.necer.ndialog.NDialog;

/**
 * Created by SirdarYangK on 2017/11/1.
 * Function description：
 */

public class AlertDialogManager {
    private static NDialog nAlertDialog;
    private static AlertDialog mAlertDialog;

    public static NDialog displayNDialog(@NonNull Activity Activity, String title, String msg, String confirm, String cancel) {
        if (nAlertDialog != null) {
            nAlertDialog
                    .setTitle(title)
                    .setMessage(msg)
                    .setTitleSize(43)
                    .setTitleColor(Color.parseColor("#EE336633"))
                    .setTitleCenter(true)
                    .setMessage(msg)
                    .setMessageSize(35)
                    .setMessageCenter(true)
                    .setMessageColor(Color.parseColor("#EE336633"))
                    .setPositiveButtonText(confirm)//确认
                    .setNegativeButtonText(cancel)//取消
                    .setNegativeTextColor(Color.parseColor("#000000"))
                    .setPositiveTextColor(Color.parseColor("#000000"))
                    .setButtonSize(50)
                    .setButtonCenter(true)
                    .setCancleable(false);
        } else {
            nAlertDialog = new NDialog(Activity)
                    .setTitle(title)
                    .setMessage(msg)
                    .setTitleSize(43)
                    .setTitleColor(Color.parseColor("#EE336633"))
                    .setTitleCenter(true)
                    .setMessage(msg)
                    .setMessageSize(35)
                    .setMessageCenter(true)
                    .setMessageColor(Color.parseColor("#EE336633"))
                    .setPositiveButtonText(confirm)
                    .setNegativeButtonText(cancel)
                    .setNegativeTextColor(Color.parseColor("#000000"))
                    .setPositiveTextColor(Color.parseColor("#000000"))
                    .setButtonSize(50)
                    .setButtonCenter(true)
                    .setCancleable(false);
        }
        return nAlertDialog;
    }

    public static AlertDialog displayADialog(@NonNull Context context, View view) {
        if (mAlertDialog != null) {
//            mAlertDialog.setTitle(title);
//            mAlertDialog.setMessage(msg);
            mAlertDialog.setView(view);
            mAlertDialog.setCancelable(false);
        } else {
            mAlertDialog = new AlertDialog.Builder(context)
//                    .setTitle(title)
//                    .setMessage(msg)
                    .setView(view)
                    .setCancelable(false)
//                    .setNegativeButton("取消", null)
//                    .setPositiveButton("确定", null)
                    .create();
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        return mAlertDialog;
    }

    public static MaterialDialog displayMaterialDialog(@NonNull Context context, int layout,int width,int height) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .customView(layout, false)
                .show();
        //设置alertDialog宽高
        WindowManager.LayoutParams params = materialDialog.getWindow().getAttributes();
        params.width = width;
        params.height = height;
        materialDialog.getWindow().setAttributes(params);
        return materialDialog;
    }

    public static void destory(@NonNull Activity Activity) {
        if (Activity != null) {
            Activity = null;
            return;
        }
        if (nAlertDialog != null) {
//            sAlertDialog.cancel();
            AlertDialog alertDialog = nAlertDialog.create(NDialog.CONFIRM);
        }

    }
}
