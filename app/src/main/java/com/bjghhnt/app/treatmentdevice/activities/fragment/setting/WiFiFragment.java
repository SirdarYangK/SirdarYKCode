package com.bjghhnt.app.treatmentdevice.activities.fragment.setting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.Adapters.WifiAdapter;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.utils.WifiAutoConnectManager;
import com.bjghhnt.app.treatmentdevice.utils.WifiSearcher;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Development_Android on 2016/12/7.
 */

public class WiFiFragment extends Fragment implements WifiSearcher.SearchWifiListener,
        AdapterView.OnItemClickListener {

    @BindView(R.id.sb_wifi)
    SwitchButton mSbWifi;
    @BindView(R.id.ib_add_ssid)
    ImageButton mIbAddSsid;
    @BindView(R.id.ib_refresh_wifi)
    ImageButton mIbRefreshWifi;
    @BindView(R.id.lv_wifi)
    ListView mLvWifi;
    // 扫描结果列表
    private WifiAdapter mAdapter;
    private WifiManager mWifiManager;
    private WifiSearcher mWifiSearcher;
    private NetworkConnectChangedReceiver receiver;
    private ProgressDialog progressDialog;
    private boolean isWifiOpen = false;
    private WifiAutoConnectManager mWifiAutoConnectManager;
    private WifiAutoConnectManager.WifiCipherType mWifiCipherType =
            WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getActivity(), "打开无线网络失败", Toast.LENGTH_SHORT).show();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View wifiView = View.inflate(getContext(), R.layout.fragment_wifi_set, null);
        ButterKnife.bind(this, wifiView);
        return wifiView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mAdapter = new WifiAdapter(getContext());
        mLvWifi.setAdapter(mAdapter);
        mLvWifi.setOnItemClickListener(this);
        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        mWifiSearcher = new WifiSearcher(getContext(), this);
        mWifiAutoConnectManager = new WifiAutoConnectManager(mWifiManager);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkConnectChangedReceiver();
        getContext().registerReceiver(receiver, filter);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("正在开启无线网络...");
        progressDialog.setCanceledOnTouchOutside(false);
        showWifiState();
    }

    private void showWifiState() {
        switch (mWifiManager.getWifiState()) {
            case WifiManager.WIFI_STATE_ENABLED:
//                Toast.makeText(getActivity(), "Wifi已开启", Toast.LENGTH_SHORT).show();
                mSbWifi.setEnabled(true);
                mSbWifi.setChecked(true);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
//                Toast.makeText(getActivity(), "Wifi已关闭", Toast.LENGTH_SHORT).show();
                mSbWifi.setEnabled(true);
                mSbWifi.setChecked(false);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
//                Toast.makeText(getActivity(), "Wifi状态未知", Toast.LENGTH_SHORT).show();
                mSbWifi.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<ScanResult> wifiList = mAdapter.getWifiList();
        ScanResult scanResult = wifiList.get(position);
        String capabilities = scanResult.capabilities;
        //初始化对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = View.inflate(getContext(), R.layout.dialog_wifi_passwd, null);
        builder.setTitle("请输入密码").setIcon(android.R.drawable.ic_dialog_info).
                setView(dialogView);
        Dialog dialog = builder.create();
        TextView tvSSID = (TextView) dialogView.findViewById(R.id.tv_dialog_ssid);
        TextView tvEN = (TextView) dialogView.findViewById(R.id.tv_dialog_en);
        TextView tvStrong = (TextView) dialogView.findViewById(R.id.tv_dialog_strong);
        EditText etPasswd = (EditText) dialogView.findViewById(R.id.et_wifi_passwd);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        Button btnConnect = (Button) dialogView.findViewById(R.id.btn_Connect);
        CheckBox cbShowPasswd = (CheckBox) dialogView.findViewById(R.id.cb_show_password);
        tvSSID.setText("网络名称：" + scanResult.SSID);
        tvEN.setText("加密方式：" + scanResult.capabilities);
        tvStrong.setText("信号强度：" + scanResult.level + "dB");
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConnect.setOnClickListener(v -> {
            String wifiPasswd = etPasswd.getText().toString();
            mWifiAutoConnectManager.connect(scanResult.SSID,wifiPasswd,mWifiCipherType);
            dialog.dismiss();
        });
        cbShowPasswd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                etPasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else {
                etPasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        if (capabilities.contains("WPA")) {
            mWifiCipherType = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
        } else {
            if (capabilities.contains("WEP")){
                mWifiCipherType = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WEP;
            }else {
                mWifiCipherType = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS;
            }
        }
        if (getConnectionWifi().equals(scanResult.SSID)){
            btnConnect.setEnabled(false);
            btnConnect.setText("已连接");
            etPasswd.setVisibility(View.GONE);
            cbShowPasswd.setVisibility(View.GONE);
        }else {
            btnConnect.setEnabled(true);
            btnConnect.setText("连接");
            etPasswd.setVisibility(View.VISIBLE);
            cbShowPasswd.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }


    class MyTask extends TimerTask {

        @Override
        public void run() {
            progressDialog.dismiss();
            if (!isWifiOpen) {
                handler.sendEmptyMessage(1);
            }
        }

    }

    @OnClick({R.id.sb_wifi, R.id.ib_add_ssid, R.id.ib_refresh_wifi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sb_wifi:
                if (mSbWifi.isChecked()) {
                    progressDialog.show();
                    isWifiOpen = false;
                    Timer timer = new Timer();
                    timer.schedule(new MyTask(), 20000);
                    //打开wifi
                    if (!mWifiManager.isWifiEnabled()) {
                        mWifiManager.setWifiEnabled(true);
                    }
                } else {
                    //关闭wifi
                    if (mWifiManager.isWifiEnabled()) {
                        mWifiManager.setWifiEnabled(false);
                    }
                }
                break;
            case R.id.ib_add_ssid:
                break;
            case R.id.ib_refresh_wifi:
                if (mSbWifi.isChecked()) {
                    mWifiSearcher.search();
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
                    mIbRefreshWifi.setAnimation(animation);
                } else {
                    Toast.makeText(getActivity(), "请先开启无线网络", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSearchWifiFailed(WifiSearcher.ErrorType errorType) {
//        Toast.makeText(getContext(), errorType.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchWifiSuccess(List<ScanResult> results) {
        mAdapter.setWifiScanList(results);
        getConnectionWifi();
        mAdapter.notifyDataSetChanged();

    }

    private String getConnectionWifi() {
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
        String connectionBSSID = connectionInfo.getBSSID();
        String connectionSSID = connectionInfo.getSSID();
        mAdapter.setConnectionWifi(connectionBSSID, connectionSSID);
        return connectionSSID;
    }


    private class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        mSbWifi.setChecked(false);
                        mAdapter.clearWifiScanList();
                        mAdapter.notifyDataSetChanged();
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        isWifiOpen = true;
                        mSbWifi.setChecked(true);
                        progressDialog.dismiss();
                        mWifiSearcher.search();
                        break;
                    //
                }
            }
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;//当然，这边可以更精确的确定状态
                    if (isConnected) {
                        getConnectionWifi();
                    } else {

                    }
                }
            }
        }
    }
}
