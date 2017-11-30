package com.bjghhnt.app.treatmentdevice.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.utils.models.VideoInfo;

import java.util.List;

/**
 * the array adapter to populate the playlist in video activity
 * Created by Q on 17/02/2016.
 */
public class VideoInfoArrayAdapter extends ArrayAdapter<VideoInfo> {

	@SuppressWarnings("SameParameterValue")
	public VideoInfoArrayAdapter(Context context, int resource, List<VideoInfo> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VideoInfoViewHolder vh;
		String videoName = getItem(position).getName();
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			vh = new VideoInfoViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_video, parent, false);
			vh.videoName = (TextView) convertView.findViewById(R.id.item_video_name);
			convertView.setTag(vh);
		} else {
			vh = (VideoInfoViewHolder) convertView.getTag();
		}

		// Populate the data into the template view using the data object
		vh.videoName.setText(videoName);
		return convertView;
	}

	//  "What the ViewHolder class does is cache the call to findViewById()"
	private static class VideoInfoViewHolder {

		TextView videoName;
	}
}
