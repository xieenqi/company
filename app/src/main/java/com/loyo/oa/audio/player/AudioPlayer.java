package com.loyo.oa.audio.player;

import android.media.MediaPlayer;
import android.widget.SeekBar;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Player;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by EthanGong on 2016/12/10.
 */
public class AudioPlayer implements PlayCallback {

    private Player player;

    // View
    private WeakReference<AudioPlayerView> playerViewRef;
    private AudioPlayerView attachedView;
    private SeekBar mSeekbar;

    private static AudioPlayer ourInstance = new AudioPlayer();

    private AudioPlayer() {
    }

    public static AudioPlayer getInstance() {
        return ourInstance;
    }

    public void bindView(AudioPlayerView view) {
        if (view == null) {
            return;
        }

        if (playerViewRef != null && playerViewRef.get() != null) {
            //playerViewRef.get().removePlayCallbackHandler();
        }

        playerViewRef = new WeakReference<AudioPlayerView>(view);
        if (playerViewRef != null && playerViewRef.get() != null) {
            playerViewRef.get().setPlayCallbackHandler(this);
        }

        attachedView = getAttachView();
    }


    private AudioPlayerView getAttachView() {
        if (playerViewRef != null && playerViewRef.get() != null) {
            return playerViewRef.get();
        }
        return null;
    }

    /**
     * Player监听
     */
    class PlayerComplte implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mp.getDuration() != 0) {
                attachedView.updateUI(AudioPlayUpdate.stopState());
            }
        }
    }

    /**
     * Player播放失败监听
     */
    class PlayerError implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            attachedView.updateUI(AudioPlayUpdate.errorState());
            return false;
        }
    }

    /**
     * 拖动条监听
     */
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            this.progress = progress;
            if(null != player)
                attachedView.updateUI(AudioPlayUpdate.progressState(DateTool.int2time((progress)*1000)));

            /*if (null != player)
                this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
            attachedView.updateUI(AudioPlayUpdate.progressState(DateTool.int2time(this.progress)));*/
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            LogUtil.dee("progress:" + progress);
            player.mediaPlayer.seekTo(progress*1000);
            attachedView.updateUI(AudioPlayUpdate.progressState(DateTool.int2time(this.progress*1000)));
        }
    }

    /**
     * 获取音频时长
     */
    public int getAudioSize(MediaPlayer mediaPlayer) {
            int timelong = (int) Math.ceil((mediaPlayer.getDuration() / 1000));
            return timelong;
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean onPlaying() {
        return player.mediaPlayer.isPlaying();
    }

    /**
     * 初始化
     */
    @Override
    public void onInit(SeekBar musicProgress) {
        if (player != null) {
            player.stop();
        }

        this.mSeekbar = musicProgress;
        if (musicProgress != null) {
            musicProgress.setOnSeekBarChangeListener(null);
        }

        player = new Player(musicProgress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player.mediaPlayer.setOnCompletionListener(new PlayerComplte());
    }

    /**
     * 第一次播放
     */
    @Override
    public void onStart(AudioPlayerView view, final AudioModel audioModel) {
        if (null == player) {
            player = new Player(mSeekbar);
            player.mediaPlayer.setOnCompletionListener(new PlayerComplte());
            player.mediaPlayer.setOnErrorListener(new PlayerError());
        }

        mSeekbar.setProgress(0);
        if (attachedView != null) {
            attachedView.updateUI(AudioPlayUpdate.startState());
        }

        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    player.playUrl(audioModel.url);
                    mSeekbar.setMax(getAudioSize(player.mediaPlayer)+1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 继续播放
     */
    @Override
    public void onResume(AudioPlayerView view) {
        if (player != null) {
            player.play();
            mSeekbar.setMax(getAudioSize(player.mediaPlayer)+1);
        }
    }

    /**
     * 暂停
     */
    @Override
    public void onPause(AudioPlayerView view) {
        if (player != null) {
            player.pause();
        }
        if (attachedView != null) {
            attachedView.updateUI(AudioPlayUpdate.pauseState());
        }
    }

    /**
     * 停止销毁
     */
    @Override
    public void onStop(AudioPlayerView view) {
        if (player != null) {
            player.stop();
            player = null;
        }
    }

    @Override
    public void onProgressSeek(AudioPlayerView view, float percent) {

    }
}
