package com.bjghhnt.app.treatmentdevice.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.bean.MediaBean;
import com.bjghhnt.app.treatmentdevice.utils.Channels;

import java.io.IOException;
import java.util.List;

/**
 * Created by Development_Android on 2016/12/14.
 */

public class PropagandaMusicService extends Service implements MediaPlayer.OnCompletionListener {

    public static MediaPlayer mp;
    public List<MediaBean> mMusicList;
    public int mPosition = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        addBallView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TreatmentApplication.getBusInstance().post(this);
        return super.onStartCommand(intent, flags, startId);
    }




    public void playOrPauseMusic() {
        if (mMusicList == null){
            Toast.makeText(this, "未找到该文件", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.pause();
            } else {
                mp.start();
            }
        }
    }

    public boolean isPlaying() {
        if (mp != null) {
            if (mp.isPlaying()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setMusicListAndInitMediaPlayer(List<MediaBean> musicList) {
        if (musicList.isEmpty()) {
            return;
        }
        if (mp != null) {
            if (mp.isPlaying()) {
                return;
            }
        }
        this.mMusicList = musicList;
        try {
            if (mp != null) {
                mp.reset();
                mp.release();
            }
            mp = new MediaPlayer();
            mp.setDataSource(mMusicList.get(mPosition).mMediaData);
            mp.setLooping(false);
            mp.setOnCompletionListener(this);
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAudioCurrentDuration() {
        return mp == null ? 0 : mp.getCurrentPosition();
    }

    public int getAudioTotalDuration() {
        return mp == null ? 0 : mp.getDuration();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
        }
    }

    public void startSelectePlayMusic(int position) {
        this.mPosition = position;
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        if (mPosition > mMusicList.size()){
            Toast.makeText(this, "未找到该文件", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (mp != null) {
                mp.reset();
                mp.release();
            }
            mp = new MediaPlayer();
            mp.setDataSource(mMusicList.get(mPosition).mMediaData);
            mp.setLooping(false);
            mp.prepare();
            mp.setOnCompletionListener(this);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preAudio() {
        if (mMusicList == null){
            Toast.makeText(this, "未找到该文件", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mPosition == 0) {
            mPosition = mMusicList.size() - 1;
        } else {
            mPosition--;
        }
        initMediaPlayer();
    }

    public void nextAudio() {
        if (mMusicList == null){
            Toast.makeText(this, "未找到该文件", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mPosition == (mMusicList.size() - 1)) {
            mPosition = 0;
        } else {
            mPosition++;
        }
    initMediaPlayer();
}

    public void seekTo(int msec) {
        if (mp != null) {
            mp.seekTo(msec);
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        nextAudio();
        TreatmentApplication.getBusInstance().post(Channels.PLAY_MUSIC_SUCCESS);
    }
}
