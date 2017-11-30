package com.bjghhnt.app.treatmentdevice.activities.fragment.propaganda;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.bean.MediaBean;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Development_Android on 2016/12/14.
 */

public class PropagandaVideoFragment extends Fragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    public final int UPDATE_UI_SEEKBAR = 100;
    @BindView(R.id.vv_propaganda_video)
    VideoView mVvPropagandaVideo;
    @BindView(R.id.ib_video_pause)
    ImageButton mIbVideoPause;
    @BindView(R.id.sb_video_progress)
    AppCompatSeekBar mSbVideoProgress;
    @BindView(R.id.ib_video_fullscreen)
    ImageButton mIbVideoFullscreen;
    @BindView(R.id.ll_video_controller)
    LinearLayout mLlVideoController;
    @BindView(R.id.fl_video_click)
    FrameLayout mFlVideoClick;
    @BindView(R.id.rl_VideoView)
    RelativeLayout mRlVideoView;
    private List<MediaBean> mVideoList;
    private Timer mTimer;
    public PropagandaVideoFragmentHandler mHandler;
    private int HIDE_CONTRPLLER = 101;
    private boolean fullscreen = false;
    private ListView mActivityListView;
    private ViewGroup mActivityViewGroup;
    private View mViewLine;
    private int mVideoViewHeight;

    @OnClick({R.id.ib_video_pause, R.id.ib_video_fullscreen})
    public void onIbClick(View view) {
        switch (view.getId()) {
            case R.id.ib_video_pause:
                if (mVvPropagandaVideo.isPlaying()) {
                    mVvPropagandaVideo.pause();
                    mIbVideoPause.setBackgroundResource(R.drawable.propaganda_video_pause);
                } else {
                    mVvPropagandaVideo.start();
                    mIbVideoPause.setBackgroundResource(R.drawable.propaganda_video_play);
                }
                break;
            case R.id.ib_video_fullscreen:
                setFullscreen();
                break;
        }
    }

    private synchronized void setFullscreen() {
        if (!fullscreen) {//设置RelativeLayout的全屏模式
            mActivityListView.setVisibility(View.GONE);
            mActivityViewGroup.setVisibility(View.GONE);
            mViewLine.setVisibility(View.GONE);
            mVideoViewHeight = mRlVideoView.getLayoutParams().height;
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRlVideoView.setLayoutParams(layoutParams);
            mIbVideoFullscreen.setBackgroundResource(R.drawable.propaganda_video_windows);
            fullscreen = true;//改变全屏/窗口的标记
        } else {//设置RelativeLayout的窗口模式
            mActivityListView.setVisibility(View.VISIBLE);
            mActivityViewGroup.setVisibility(View.VISIBLE);
            mViewLine.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mVideoViewHeight);
            mRlVideoView.setLayoutParams(layoutParams);
            mRlVideoView.setGravity(Gravity.CENTER);
            mIbVideoFullscreen.setBackgroundResource(R.drawable.propaganda_video_fullscreen);
            fullscreen = false;//改变全屏/窗口的标记

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp.isPlaying()) {
            mIbVideoPause.setBackgroundResource(R.drawable.propaganda_video_pause);
        } else {
            mIbVideoPause.setBackgroundResource(R.drawable.propaganda_music_play);
        }
        mVvPropagandaVideo.seekTo(0);
    }


    private static class PropagandaVideoFragmentHandler extends Handler {

        WeakReference<PropagandaVideoFragment> mFragmentReference;

        PropagandaVideoFragmentHandler(PropagandaVideoFragment propagandaAudioFragment) {
            mFragmentReference = new WeakReference<>(propagandaAudioFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PropagandaVideoFragment propagandaAudioFragment = mFragmentReference.get();
            if (msg.what == propagandaAudioFragment.UPDATE_UI_SEEKBAR) {
                if (propagandaAudioFragment.mLlVideoController.getVisibility() == View.VISIBLE) {
                    propagandaAudioFragment.mSbVideoProgress.setMax(msg.arg1);
                    propagandaAudioFragment.mSbVideoProgress.setProgress(msg.arg2);
                }
                if (propagandaAudioFragment.mVvPropagandaVideo.isPlaying()) {
                    propagandaAudioFragment.mIbVideoPause.setBackgroundResource(R.drawable.propaganda_video_pause);
                } else {
                    propagandaAudioFragment.mIbVideoPause.setBackgroundResource(R.drawable.propaganda_video_play);
                }
            }
            if (msg.what == propagandaAudioFragment.HIDE_CONTRPLLER) {
                propagandaAudioFragment.mLlVideoController.setVisibility(View.GONE);
            }
        }

    }


    public PropagandaVideoFragment() {


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_propaganda_video, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mVvPropagandaVideo.setMediaController(new MediaController(getContext()));
        mHandler = new PropagandaVideoFragmentHandler(this);
        mFlVideoClick.setOnClickListener(this);
        mSbVideoProgress.setOnSeekBarChangeListener(this);
        mHandler.sendEmptyMessageAtTime(HIDE_CONTRPLLER, 5000);
        mVvPropagandaVideo.setOnCompletionListener(this);


        initView();


    }

    private void initView() {
        mActivityListView = (ListView) getActivity().findViewById(R.id.lv_propaganda_list);
        mActivityViewGroup = (ViewGroup) getActivity().findViewById(R.id.rg_propaganda);
        mViewLine = getActivity().findViewById(R.id.view_line);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setVideoList(List<MediaBean> mVideoList) {
        this.mVideoList = mVideoList;
        if (mVideoList.isEmpty()){
            return;
        }
        playVideo(0);
        mVvPropagandaVideo.pause();
    }

    public void playVideo(int position) {
        if (mVideoList.isEmpty() || position>mVideoList.size()){
            return;
        }
        if (mVideoList != null) {
            MediaBean mediaBean = mVideoList.get(position);
            String videoUrl = mediaBean.mMediaData;
            mVvPropagandaVideo.setVideoPath(videoUrl);
            mVvPropagandaVideo.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mVvPropagandaVideo.isPlaying()) {
            mVvPropagandaVideo.pause();
        }
        mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPDATE_UI_SEEKBAR;
                message.arg1 = mVvPropagandaVideo.getDuration();
                message.arg2 = mVvPropagandaVideo.getCurrentPosition();
                mHandler.sendMessage(message);
            }
        }, 100, 1000);
    }

    @Override
    public void onClick(View v) {
        if (mLlVideoController.getVisibility() == View.GONE) {
            mLlVideoController.setVisibility(View.VISIBLE);
        } else {
            mLlVideoController.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mVvPropagandaVideo.seekTo(progress);
            mVvPropagandaVideo.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
