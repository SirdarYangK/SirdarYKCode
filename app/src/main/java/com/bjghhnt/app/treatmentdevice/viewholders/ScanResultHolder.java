package com.bjghhnt.app.treatmentdevice.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjghhnt.app.treatmentdevice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The view holder of the wifi items.
 * Created by Q on 01/03/2016.
 */
@SuppressWarnings("unused")
public class ScanResultHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.text_view_wifi_id)
	public TextView mTextViewID;

	@BindView(R.id.image_view_wifi_icon)
	public ImageView mImageViewIcon;

	@BindView(R.id.text_view_connect_stat)
	public TextView mTextViewConnectStat;

	public ScanResultHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}
}
