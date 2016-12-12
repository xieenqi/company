package com.loyo.oa.audio.player;

import java.lang.ref.WeakReference;

/**
 * Created by EthanGong on 2016/12/10.
 */
public class AudioPlayer implements PlayCallback {
    private static AudioPlayer ourInstance = new AudioPlayer();

    public static AudioPlayer getInstance() {
        return ourInstance;
    }

    // View
    private WeakReference<AudioPlayerView> playerViewRef;

    private AudioPlayer() {
    }


    public void bindView(AudioPlayerView view) {
        if (view == null) {
            return;
        }

        if (playerViewRef != null && playerViewRef.get()!=null) {
            playerViewRef.get().removePlayCallbackHandler();
        }

        playerViewRef = new WeakReference<AudioPlayerView>(view);
        if (playerViewRef != null && playerViewRef.get()!=null) {
            playerViewRef.get().setPlayCallbackHandler(this);
        }
    }


    private AudioPlayerView getAttachView() {
        if (playerViewRef != null && playerViewRef.get()!=null) {
            return playerViewRef.get();
        }
        return null;
    }



    // 播放
    public void play(String pathOrUrl) {

        AudioPlayerView attachedView = getAttachView();


        // 停止上一个
        // ....

        if (attachedView != null) {
            attachedView.updateUI(AudioPlayUpdate.stopState());
        }

        // 开始下一个

        // ....

        if (attachedView != null) {
            attachedView.updateUI(AudioPlayUpdate.playState());
        }
    }

    // 暂定
    public void pause() {

    }

    // 继续播放
    public void resume() {

    }

    // 停止
    public void stop() {

    }

    /**
     *
     */

    @Override
    public void onResume(AudioPlayerView view) {

    }

    @Override
    public void onPause(AudioPlayerView view) {

    }

    @Override
    public void onStop(AudioPlayerView view) {

    }

    @Override
    public void onProgressSeek(AudioPlayerView view, float percent) {

    }
}
