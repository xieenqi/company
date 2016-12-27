package com.loyo.oa.audio.player;

import android.widget.SeekBar;

import com.loyo.oa.v2.activityui.signin.bean.AudioModel;

/**
 * Created by EthanGong on 2016/12/10.
 */

public interface PlayCallback {

    boolean onPlaying();
    void onInit(SeekBar musicProgress);
    void onStart(AudioPlayerView view,AudioModel audioModel);
    void onResume(AudioPlayerView view);
    void onPause(AudioPlayerView view);
    void onStop(AudioPlayerView view);
    void onProgressSeek(AudioPlayerView view, float percent);
}
