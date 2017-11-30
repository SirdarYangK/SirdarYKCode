package com.bjghhnt.app.treatmentdevice.activities.fragment.propaganda;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.service.PropagandaMusicService;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.bean.MediaBean;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.squareup.otto.Subscribe;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Development_Android on 2016/12/13.
 */

public class PropagandaAudioFragment extends Fragment implements
        DiscreteSeekBar.OnProgressChangeListener {

    @BindView(R.id.tv_music_name)
    TextView mTvMusicName;
    @BindView(R.id.tv_Artist)
    TextView mTvArtist;
    @BindView(R.id.sb_vol)
    DiscreteSeekBar mSbVol;
    @BindView(R.id.sb_music_progress)
    DiscreteSeekBar mSbMusicProgress;
    @BindView(R.id.ib_music_pre)
    ImageButton mIbMusicPre;
    @BindView(R.id.ib_music_play)
    ImageButton mIbMusicPlay;
    @BindView(R.id.ib_music_next)
    ImageButton mIbMusicNext;
    @BindView(R.id.iv_music_album)
    ImageView mIvMusicAlbum;
    private PropagandaMusicService mPropagandaMusicService;

    private List<MediaBean> mAudioList;
    private Timer mTimer;
    private FragmentPropagandaAudioHandler mHandler;
    private int UPDATA_SEEKBAR_PROGRESS;
    private AudioManager mAudioManager;

    private static class FragmentPropagandaAudioHandler extends Handler {

        WeakReference<PropagandaAudioFragment> mFragmentReference;

        FragmentPropagandaAudioHandler(PropagandaAudioFragment propagandaAudioFragment) {
            mFragmentReference = new WeakReference<>(propagandaAudioFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PropagandaAudioFragment propagandaAudioFragment = mFragmentReference.get();
            if (msg.what == propagandaAudioFragment.UPDATA_SEEKBAR_PROGRESS) {
                propagandaAudioFragment.mSbMusicProgress.setMax(msg.arg2);
                propagandaAudioFragment.mSbMusicProgress.setProgress(msg.arg1);
            }
        }

    }

    public void setmAudioList(List<MediaBean> mAudioList) {
        if (mAudioList.isEmpty()){
            return;
        }
        this.mAudioList = mAudioList;
        MediaBean mediaBean = mAudioList.get(mPropagandaMusicService.mPosition);
        mTvMusicName.setText(mediaBean.mMediaTitle);
        mTvArtist.setText(mediaBean.mMediaArtist);
    }

    public PropagandaAudioFragment(PropagandaMusicService mPropagandaMusicService) {
        this.mPropagandaMusicService = mPropagandaMusicService;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPropagandaMusicService.isPlaying()) {
            mIbMusicPlay.setBackgroundResource(R.drawable.propaganda_music_stop);

        } else {
            mIbMusicPlay.setBackgroundResource(R.drawable.propaganda_music_play);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_propaganda_audio, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new FragmentPropagandaAudioHandler(this);
        playOrPauseMusic();
        mSbMusicProgress.setOnProgressChangeListener(this);
        TreatmentApplication.getBusInstance().register(this);

        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int mediaCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSbVol.setProgress(mediaCurrent * 10);
        mSbVol.setOnProgressChangeListener(this);
    }

    @Subscribe
    public void getMusicServiceSuccess(String musicSuccess) {
        if (musicSuccess.equals(Channels.PLAY_MUSIC_SUCCESS)) {
            updataUI(mPropagandaMusicService.mPosition);
        }
    }


    public void playOrPauseMusic() {

        if (mPropagandaMusicService.isPlaying()) {
            mIbMusicPlay.setBackgroundResource(R.drawable.propaganda_music_stop);
        } else {
            mIbMusicPlay.setBackgroundResource(R.drawable.propaganda_music_play);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPDATA_SEEKBAR_PROGRESS;
                message.arg2 = mPropagandaMusicService.getAudioTotalDuration();
                message.arg1 = mPropagandaMusicService.getAudioCurrentDuration();
                mHandler.sendMessage(message);
            }
        }, 1000, 1000);
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        if (fromUser) {
            if (seekBar == mSbMusicProgress) {
                mPropagandaMusicService.seekTo(value);
            }
            if (seekBar == mSbVol){
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value / 10, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, value / 10, 0);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
        System.out.println("onStartTrackingTouch");
    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        System.out.println("onStopTrackingTouch");
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimer.cancel();
    }

    public void playMusic(int position) {
        updataUI(position);
        mPropagandaMusicService.startSelectePlayMusic(position);
    }

    private void updataUI(int position) {
        if (mAudioList == null){
            Toast.makeText(getActivity(), "未找到该文件", Toast.LENGTH_SHORT).show();
            return;
        }
        MediaBean mediaBean = mAudioList.get(position);
        mTvMusicName.setText(mediaBean.mMediaTitle);
        mTvArtist.setText(mediaBean.mMediaArtist);
        mIvMusicAlbum.setBackground(new BitmapDrawable(getResources(), mediaBean.album));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        TreatmentApplication.getBusInstance().unregister(this);
    }

    @OnClick({R.id.ib_music_pre, R.id.ib_music_play, R.id.ib_music_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_music_pre:
                mPropagandaMusicService.preAudio();
                updataUI(mPropagandaMusicService.mPosition);
                break;
            case R.id.ib_music_play:
                mPropagandaMusicService.playOrPauseMusic();
                if (mPropagandaMusicService.isPlaying()) {
                    mIbMusicPlay.setBackgroundResource(R.drawable.propaganda_music_stop);

                } else {
                    mIbMusicPlay.setBackgroundResource(R.drawable.propaganda_music_play);
                }
                break;
            case R.id.ib_music_next:
                mPropagandaMusicService.nextAudio();
                updataUI(mPropagandaMusicService.mPosition);
                break;
        }
    }

}
