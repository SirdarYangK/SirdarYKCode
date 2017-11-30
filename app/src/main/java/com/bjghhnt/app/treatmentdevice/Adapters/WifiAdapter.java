package com.bjghhnt.app.treatmentdevice.Adapters;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjghhnt.app.treatmentdevice.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Development_Android on 2016/12/8.
 */

public class WifiAdapter extends BaseAdapter {

    private List<ScanResult> mWifiScanList = new ArrayList<>();
    private Context mContext;
    private String mConnectionBSSID = "123";
    private String mConnectionSSID = "123";

    public WifiAdapter(Context context) {
        this.mContext = context;
    }

    public void setWifiScanList(List<ScanResult> wifiScanList) {
        mWifiScanList.clear();
        if (wifiScanList.size() != 0) {
            mWifiScanList.addAll(wifiScanList);
        }
        notifyDataSetChanged();
    }

    public List<ScanResult> getWifiList(){
        return mWifiScanList;
    }

    public void clearWifiScanList(){
        mWifiScanList.clear();
    }

    @Override
    public int getCount() {
        return mWifiScanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWifiScanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.wifi_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ScanResult scanResult = mWifiScanList.get(position);
        if (scanResult!=null) {
            viewHolder.tvWifiSsid.setText(scanResult.SSID);
            if (scanResult.capabilities.contains("WPA-PSK")) {
                viewHolder.tvWifiEncrypted.setText("通过WAP进行保护");
                viewHolder.ivWifiLock.setVisibility(View.VISIBLE);
            } else if (scanResult.capabilities.contains("WPA2-PSK")) {
                viewHolder.tvWifiEncrypted.setText("通过WAP2进行保护");
                viewHolder.ivWifiLock.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvWifiEncrypted.setText("未设置保护");
                viewHolder.ivWifiLock.setVisibility(View.GONE);
            }
            if (scanResult.capabilities.contains("WPA2-PSK") && scanResult.capabilities.contains("WPA-PSK")) {
                viewHolder.tvWifiEncrypted.setText("通过WAP/WAP2进行保护");
                viewHolder.ivWifiLock.setVisibility(View.VISIBLE);
            }
            int level = scanResult.level;

            if (level <= 0 && level >= -50) {
                viewHolder.ivWifiStrong.setBackgroundResource(R.drawable.wifi_4_bar_48px);
            } else if (level < -50 && level >= -70) {
                viewHolder.ivWifiStrong.setBackgroundResource(R.drawable.wifi_3_bar_48px);
            } else if (level < -70 && level >= -80) {
                viewHolder.ivWifiStrong.setBackgroundResource(R.drawable.wifi_2_bar_48px);
            } else if (level < -80 && level >= -100) {
                viewHolder.ivWifiStrong.setBackgroundResource(R.drawable.wifi_1_bar_48px);
            } else {
                viewHolder.ivWifiStrong.setBackgroundResource(R.drawable.wifi_0_bar_48px);
            }
            if (mConnectionSSID != null && mConnectionBSSID != null) {
                if (mConnectionBSSID.equals(scanResult.BSSID) && mConnectionSSID.equals(scanResult.SSID)) {
                    viewHolder.tvWifiStatue.setText("已连接");
                } else {
                    viewHolder.tvWifiStatue.setText("");
                }
            }
        }

        return convertView;
    }

    public void setConnectionWifi(String connectionBSSID, String connectionSSID) {
        mConnectionBSSID = connectionBSSID;
        mConnectionSSID = connectionSSID;
        notifyDataSetChanged();
    }


    static class ViewHolder {
        @BindView(R.id.tv_wifi_ssid)
        TextView tvWifiSsid;
        @BindView(R.id.tv_wifi_encrypted)
        TextView tvWifiEncrypted;
        @BindView(R.id.iv_wifi_Strong)
        ImageView ivWifiStrong;
        @BindView(R.id.tv_wifi_statue)
        TextView tvWifiStatue;
        @BindView(R.id.iv_wifi_lock)
        ImageView ivWifiLock;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
