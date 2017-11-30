package com.bjghhnt.app.treatmentdevice.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.interfaces.WifiAdapter;
import com.bjghhnt.app.treatmentdevice.receivers.WifiScanReceiver;
import com.bjghhnt.app.treatmentdevice.utils.SerialportControler;
import com.bjghhnt.app.treatmentdevice.utils.serials.ReadThread;
import com.bjghhnt.app.treatmentdevice.utils.serials.RealSerialPortImpl;
import com.bjghhnt.app.treatmentdevice.utils.serials.ReceiveThread;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SimpleHandler;
import com.bjghhnt.app.treatmentdevice.views.WifiPasswordView;
import com.dwin.dwinapi.SerialPort;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 偏好设置debug模式
 */
//TODO need to fixed the way the list appears
@SuppressWarnings({"WeakerAccess", "unused"})
public class PrefsActivity extends MinorActivity {
    private static final String TAG = "PrefsActivity";
    @BindView(R.id.image_view_idle_preferences)
    ImageView mImageViewIdle;
    @BindView(R.id.tgl_btn_prefs_wifi)
    Switch mToggleButtonWifi;
    @BindView(R.id.linear_layout_wifi_state)
    LinearLayout mLinearLayoutWifiState;
    @BindView(R.id.recycler_view_available_items)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar_enabling_wifi)
    ProgressBar mProgressBar;
    private String mWifiCapabilities;
    public WifiManager mWifiManager;
    private WifiAdapter mWifiAdapter;
    private TypedArray mTypedArrayWifi;
    private IntentFilter mIntentFilter;
    private AlertDialog.Builder mDialogBuilder;
    private final BroadcastReceiver mWifiScanReceiver = new WifiScanReceiver(this);
    private WifiPasswordView mWifiDialogView;
//    private SerialPort mSerialPort;
//    private SerialportControler serialportControler;
    private final SimpleHandler mSimHandler = new SimpleHandler(this);
    @Override
    protected void upDateTime() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        ButterKnife.bind(this);
        mWifiDialogView = new WifiPasswordView(this);
        //context.getSystemService, use application instead of activity to prevent leaking
        mWifiManager = (WifiManager) this.getApplication().getSystemService(Context.WIFI_SERVICE);
        mWifiAdapter = new WifiAdapter(mWifiManager);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mWifiAdapter);
        mIntentFilter = new IntentFilter();
        // check if wifi is enabled or not
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // get available wifi
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        if (BuildConfig.MIPS) {
//            mSerialPort = new SerialPort("S0", 115200, 8, 1, 'n');
//        } else if (BuildConfig.ROCKCHIP) {
//            serialportControler = new SerialportControler("/dev/ttyS0", 115200);
//
//        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mWifiScanReceiver, mIntentFilter);
        // a typed array for referring different wifi icons
        mTypedArrayWifi = getResources().obtainTypedArray(R.array.wifi_icons);
        mWifiAdapter.setWifiIcons(mTypedArrayWifi);
        mWifiManager.startScan();
    }

    @Override
    protected SerialHandler setHandler() {
        return mSimHandler;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mWifiScanReceiver);
        // must recycle it, otherwise it leaks memory
        mTypedArrayWifi.recycle();
    }

    @OnClick(R.id.tgl_btn_prefs_wifi)
    void changeWifiState() {
        if (mToggleButtonWifi.isChecked()) {
            mWifiManager.setWifiEnabled(true);//Turn on Wifi
        } else {
            mWifiManager.setWifiEnabled(false);//Turn off Wifi
        }
        //TODO this maybe useful
        //mWifiManager.startScan();
    }

    @OnClick(R.id.btn_prefs_wifi)
    void showWifiState() {
        //	mToggleButtonWifi.setChecked(mWifiManager.isWifiEnabled());
        mImageViewIdle.setVisibility(View.GONE);
        mLinearLayoutWifiState.setVisibility(View.VISIBLE);
        if (mDialogBuilder == null) {
            //prepare the dialog builder
            mDialogBuilder = new AlertDialog.Builder(this);
            mDialogBuilder.setMessage("请输入密码");
            mDialogBuilder.setIcon(android.R.drawable.ic_lock_lock);

            mDialogBuilder.setPositiveButton("连接",
                    (dialog, which) -> {
                        //TODO connect to wifi programmatically
                        if (mWifiDialogView.getRememberCheckBox().isChecked()) {
                            //TODO save the wifi id and password somewhere
                        }
                    });

            mDialogBuilder.setNegativeButton("取消",
                    (dialog, which) -> dialog.cancel());
        }
    }


    @OnClick(R.id.btn_prefs_back)
    void goBack() {
        super.finish();
    }

    @OnClick(R.id.btn_start_debug)
    void debug() {
        startActivity(new Intent(this, DebugActivity.class));
    }

    @OnClick(R.id.btn_change_pin)
    void changePIN() {
        startActivity(new Intent(this, PinActivity.class)
//                .putExtra(PinActivity.INTENT_KEY_TO_RECEIVE,
//                        PinActivity.ROUTINE_CHANGE_PIN));
                .putExtra("START_INTENT", "START_SETTING"));
    }

    /**
     * display the enter wifi password prompt
     **/
    public void showWifiPrompt(String id, String capabilities) {
        mDialogBuilder.setTitle("连接到" + id);
        /**TODO  why otherwise you get "java.lang.IllegalStateException:
         The specified child already has a parent. You must call removeView() on the
         child's parent first." ?*/
        if (mWifiDialogView.getParent() != null) {
            mWifiDialogView = new WifiPasswordView(this);
        }
        // set the view as well as the padding, weirdly the padding defined in layout file doesn't work.
        mDialogBuilder.setView(mWifiDialogView,
                (int) (19 * TreatmentApplication.dpi), (int) (5 * TreatmentApplication.dpi),
                (int) (14 * TreatmentApplication.dpi), (int) (5 * TreatmentApplication.dpi));
        mDialogBuilder.show();
        mWifiCapabilities = capabilities;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public WifiAdapter getWifiAdapter() {
        return mWifiAdapter;
    }

    public Switch getToggleButtonWifi() {
        return mToggleButtonWifi;
    }

    @OnClick(R.id.btn_import_serial)
    public void onClick() {
        EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("序列号")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", (dialog, which) -> {
                    if (BuildConfig.MIPS) {
                        mSerialPort.sendData(et.getText().toString(), "HEX");
                    } else if (BuildConfig.ROCKCHIP) {
                        serialportControler.sendData(et.getText().toString(), "HEX");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
