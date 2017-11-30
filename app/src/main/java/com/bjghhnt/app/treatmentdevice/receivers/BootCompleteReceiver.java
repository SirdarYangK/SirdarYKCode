package com.bjghhnt.app.treatmentdevice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.views.TreatingMeterView;

/**
 * To fix the issue of "bland" display after crash, only init sharedPreferences at boot completion.
 * Created by Q on 07/07/2016.
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // init shared preference only at boot completion
        ((TreatmentApplication) context.getApplicationContext()).initSharedPreferencesOtherThanPIN();

        Toast.makeText(context, "调试消息 : 开机正常", Toast.LENGTH_LONG).show();
        //TODO only send the boot completed frame here
    }
}
