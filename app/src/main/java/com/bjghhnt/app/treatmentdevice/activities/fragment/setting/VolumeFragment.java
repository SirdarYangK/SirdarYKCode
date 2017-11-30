package com.bjghhnt.app.treatmentdevice.activities.fragment.setting;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.bjghhnt.app.treatmentdevice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Development_Android on 2016/12/19.
 */

public class VolumeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.sb_media_volume_progress)
    SeekBar mSbMediaVolumeProgress;
    @BindView(R.id.cb_touch)
    CheckBox mCbTouch;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_volume_set, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int mediaCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSbMediaVolumeProgress.setProgress(mediaCurrent * 10);
        mSbMediaVolumeProgress.setOnSeekBarChangeListener(this);


        int notify = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, 0);
        mCbTouch.setChecked(notify == 1);
        mCbTouch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Settings.System.putInt(getContext().getContentResolver(),
                        Settings.System.SOUND_EFFECTS_ENABLED,1);
                mAudioManager.loadSoundEffects();
            }else {
                Settings.System.putInt(getContext().getContentResolver(),
                        Settings.System.SOUND_EFFECTS_ENABLED,0);
                mAudioManager.unloadSoundEffects();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int vol = progress / 10;
        if (fromUser) {
            if (seekBar == mSbMediaVolumeProgress) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, vol, 0);
//                int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
//                mSbMediaVolumeProgress.setProgress(currentVolume * 10);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
