package com.bjghhnt.app.treatmentdevice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.bjghhnt.app.treatmentdevice.interfaces.WifiAdapter;
import com.bjghhnt.app.treatmentdevice.activities.PrefsActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * The wifi scan receiver for prefs activity.
 * Created by qichen on 4/20/16.
 */
public class WifiScanReceiver extends BroadcastReceiver {

    final WeakReference<PrefsActivity> mWeakReference;

    private final PrefsActivity mPrefsActivity;
    // mIsWifiOn fixes a weired bug - when the toggle button is off, the recycler view isn't empty.
    private boolean mIsWifiOn;

    public WifiScanReceiver(PrefsActivity activity) {
        mWeakReference = new WeakReference<>(activity);
        mPrefsActivity = mWeakReference.get();
    }

    @Override
    public void onReceive(Context c, Intent intent) {
        String action = intent.getAction();
        ProgressBar progressBar = mPrefsActivity.getProgressBar();
        WifiAdapter wifiAdapter = mPrefsActivity.getWifiAdapter();
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && mIsWifiOn) {
            List<ScanResult> mScanResults = mPrefsActivity.mWifiManager.getScanResults();
            // dismiss the progress bar
            progressBar.setVisibility(View.GONE);
            wifiAdapter.setScanResults(mScanResults);
        } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            Switch toggleButtonWifi = mPrefsActivity.getToggleButtonWifi();
            switch (intent.getIntExtra(
                    WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                case WifiManager.WIFI_STATE_ENABLED:
                    // when wifi is on, automatically check the toggle button, no matter what.
                    toggleButtonWifi.setChecked(true);
                    mIsWifiOn = true;
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    // show the progress bar
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    // when wifi is off, automatically un-check the toggle button, no matter what.
                    toggleButtonWifi.setChecked(false);
                    mIsWifiOn = false;
                    wifiAdapter.setScanResults(null);
                    // dismiss the progress bar
                    progressBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }
}