package com.loyo.oa.audio.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by EthanGong on 2016/12/10.
 */

public class AudioPlayerView extends LinearLayout {
    public AudioPlayerView(Context context) {
        super(context);
    }

    public AudioPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private PlayCallback callbackHandler;

    public void setPlayCallbackHandler(PlayCallback handler){
        callbackHandler = handler;
    }

    public void removePlayCallbackHandler() {
        callbackHandler = null;
    }

    public void updateUI(AudioPlayUpdate updateState) {
        // 播放
        // 暂停UI
        // ...
        // 更新
    }

    // 播放按钮
    private void onResume() {
        if (callbackHandler != null) {
            callbackHandler.onResume(this);
        }
    }

    // 暂停按钮
    private void onPause() {

    }

    // 停止按钮
    private void onStop() {

    }

    // 进度条按钮
    private void onProgressSeek() {

    }
}
