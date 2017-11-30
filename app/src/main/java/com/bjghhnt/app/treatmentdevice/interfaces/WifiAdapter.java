package com.bjghhnt.app.treatmentdevice.interfaces;

import android.content.res.TypedArray;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.activities.PrefsActivity;
import com.bjghhnt.app.treatmentdevice.viewholders.ScanResultHolder;

import java.util.Collections;
import java.util.List;

/**
 * This activity replaces the system settings
 * Created by Q on 01/03/2016.
 */
public class WifiAdapter extends RecyclerView.Adapter<ScanResultHolder> {

	private static final int WIFI_STRENGTH_SCALE = 5;

	private TypedArray mWifiIcons;

	private String mCurrentWifiSSID;

	private static WifiManager mWifiManager;

	private List<ScanResult> mScanResults;

	private static final String NO_SSID = "";

	// android sdk level
	private static final int SDK_LEVEL = Build.VERSION.SDK_INT;

	public WifiAdapter(WifiManager wifiManager) {
		super();
		mWifiManager = wifiManager;
	}

	public void setWifiIcons(TypedArray wifiIcons) {
		this.mWifiIcons = wifiIcons;
	}

	public void setScanResults(List<ScanResult> scanResult) {
		this.mScanResults = scanResult;
		// from android 4.2 the getSSID() method returns "\{SSID}\"",
		//and the first and last char need to be removed
		//before 4.2, it doesn't start or end with "\
		mCurrentWifiSSID = mWifiManager.getConnectionInfo().getSSID();
		if (SDK_LEVEL > Build.VERSION_CODES.JELLY_BEAN) {
			mCurrentWifiSSID = mCurrentWifiSSID.substring(1, mCurrentWifiSSID.length() - 1);
		} else {
			if (mCurrentWifiSSID == null) {
				// to be consistent with ssid got from higher sdk level in onBindViewHolder
				mCurrentWifiSSID = NO_SSID;
			}
		}
		if (!mCurrentWifiSSID.equals(NO_SSID) && mScanResults != null) {
			// put the current wifi connection on top
			for (int i = 0; i < mScanResults.size(); i++) {
				if (mScanResults.get(i).SSID.equals(mCurrentWifiSSID)) {
					Collections.swap(mScanResults, i, 0);
					break;
				}
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public ScanResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.wifi_item_view, parent, false);
		ScanResultHolder vh = new ScanResultHolder(view);
		vh.itemView.setOnClickListener(v -> {
			ScanResult scanResult = mScanResults.get(vh.getAdapterPosition());
			Toast.makeText(parent.getContext(),
					scanResult.SSID,
					Toast.LENGTH_SHORT).show();
			((PrefsActivity) v.getContext())
					.showWifiPrompt(scanResult.SSID, scanResult.capabilities);
		});
		return vh;
	}

	@Override
	public void onBindViewHolder(ScanResultHolder holder, int position) {
		ScanResult result = mScanResults.get(position);
		int level = WifiManager.calculateSignalLevel(result.level, WIFI_STRENGTH_SCALE);
		holder.mTextViewID.setText(result.SSID);
		holder.mImageViewIcon.setImageResource(mWifiIcons.getResourceId(level, 0));
		if (!mCurrentWifiSSID.equals(NO_SSID) && result.SSID.equals(mCurrentWifiSSID)) {
			holder.mTextViewConnectStat.setText(R.string.str_wifi_connected);
		} else if (result.SSID.equals(NO_SSID)) {
			//TODO what to do with a wifi with no SSID?
			holder.mTextViewConnectStat.setText(null);
		} else {
			holder.mTextViewConnectStat.setText(null);
		}
	}

	@Override
	public int getItemCount() {
		return (mScanResults == null) ? 0 : mScanResults.size();
	}
}
