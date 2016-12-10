package com.loyo.oa.audio.player;

/**
 * Created by EthanGong on 2016/12/10.
 */

public interface PlayCallback {
    void onResume(AudioPlayerView view);
    void onPause(AudioPlayerView view);
    void onStop(AudioPlayerView view);
    void onProgressSeek(AudioPlayerView view, float percent);
}
