package com.bjghhnt.app.treatmentdevice.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bjghhnt.app.treatmentdevice.Adapters.VideoInfoArrayAdapter;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.utils.models.VideoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings({"WeakerAccess", "unused"})
public class VideoActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, AdapterView.OnItemClickListener {

	private static final String TAG = "VideoActivity";

	@BindView(R.id.list_view_movies)
	ListView mListView;

	@BindView(R.id.video_view_player)
	VideoView mVideoView;

	@BindView(R.id.image_view_idle_player)
	ImageView mImageView;

	private static final int NONE_WAS_TOUCHED = -1;

	private int mLastPosition = 0;

	private int mLastTouchedItemPosition;

	private Bundle mBundle;

	private ProgressDialog mProgressDialog;

	private VideoInfoArrayAdapter mArrayAdapter;

	private List<VideoInfo> mItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		ButterKnife.bind(this);


		mLastTouchedItemPosition = NONE_WAS_TOUCHED;
		mItems = new ArrayList<>();
		//TODO some directories in external and internal sd cards
		getAllVideosOfDir(new File("/storage/sdcard1"), new File("/storage/sdcard0/Movies"));
		mArrayAdapter = new VideoInfoArrayAdapter(this, R.layout.list_item_video, mItems);

		mListView.setAdapter(mArrayAdapter);
		mListView.setOnItemClickListener(this);

		mBundle = new Bundle();
		mBundle.putInt("Position", mLastPosition);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("华汉针神宣传片");
		mProgressDialog.setMessage("载入中。。。");
		//set the progress bar not cancelable on users' touch
		mProgressDialog.setCancelable(true);
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消", (dialogInterface, i) -> {
            mProgressDialog.dismiss();
        });


		//set the media controller in the VideoView
		mVideoView.setMediaController(new MediaController(this));

		// Display an image instead of the player when it's idle.
		mVideoView.setVisibility(View.GONE);
		mImageView.setVisibility(View.VISIBLE);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mLastPosition = mBundle.getInt("Position");
		mVideoView.seekTo(mLastPosition);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mVideoView.pause();
		int currentPosition = mVideoView.getCurrentPosition();
		// go back 4 seconds
		currentPosition -= 4000;
		mVideoView.pause();
		mBundle.putInt("Position", (currentPosition > 0) ? currentPosition : 0);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.seekTo(mLastPosition);
		// dismiss the progress bar
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		if (mLastPosition == 0) {
			mp.start();
		} else {
			new AlertDialog.Builder(this)
					.setTitle("视频播放")
					.setMessage("是否从离开处继续？")
					.setPositiveButton("继续", (dialog, which) -> {
						mVideoView.start();
					})
					.setNegativeButton("重新开始", (dialog, which) -> {
						mVideoView.seekTo(0);
						mVideoView.start();
					})
					.setIcon(R.drawable.film_100px)
					.show();
		}
	}

	@OnClick(R.id.btn_go_back_from_video)
	void back() {
		super.finish();
	}


	private void getAllVideosOfDir(File... dirs) {
		for (File directory : dirs) {
			Log.d(TAG, "Directory: " + directory.getAbsolutePath() + "\n");

			final File[] files = directory.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file != null) {
						if (!file.isDirectory()) {  // it is a file...
							mItems.add(new VideoInfo(file));
						}
					}
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mVideoView.pause();
		String filePath = mItems.get(position).getAbsolutePath();
		/**If a list item is clicked when the media player is not idle,
		 * store the current position of the stopping video, then get the
		 * last position of the selected video.**/
		if (mLastTouchedItemPosition != NONE_WAS_TOUCHED) {
			mItems.get(mLastTouchedItemPosition)
					.setLastPosition(mVideoView.getCurrentPosition());
			mVideoView.stopPlayback();
			mLastPosition = mItems.get(position)
					.getLastPosition();
		}
		// go back a little bit (4 seconds)
		mLastPosition -= (mLastPosition > 4000) ? 4000 : 0;

		mLastTouchedItemPosition = position;
		mProgressDialog.show();
		try {
			//set the path of the video to be played
			mVideoView.setVideoPath(filePath);

		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		mVideoView.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.GONE);
		mVideoView.requestFocus();
		mVideoView.setOnPreparedListener(this);
	}
}
