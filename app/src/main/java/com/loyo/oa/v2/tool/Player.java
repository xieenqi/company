package com.loyo.oa.v2.tool;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener {

    public MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Timer mTimer = new Timer();
    TimerTask timerTask;
    private String curentUrl;


    public Player(final SeekBar seekBar) {
        super();
        this.seekBar = seekBar;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                        handler.sendEmptyMessage(0);
                    }
                }
                /** http://stackoverflow.com/questions/15730772/android-java-lang-illegalstateexception-mediaplayer-isplaying/15730932
                 *
                 * umeng crash
                 * ------
                 * java.lang.IllegalStateException
                 * at android.media.MediaPlayer.isPlaying(Native Method)
                 * at com.loyo.oa.v2.tool.Player$1.run(Player.java:39)
                 * at java.util.Timer$TimerImpl.run(Timer.java:284)
                 * ------
                 * fix by ethan 2016-11-18
                 * TODO:
                 */
                catch (IllegalStateException e) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnBufferingUpdateListener(Player.this);
                    mediaPlayer.setOnPreparedListener(Player.this);
                    if (curentUrl != null) {
                        playUrl(curentUrl);
                    }
                }
            }
        };
        mTimer.schedule(timerTask, 0, 1000);
    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (duration > 0) {
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int) pos);
            }
        }

        ;
    };

    public void play() {
        mediaPlayer.start();
    }


    public void playUrl(String url) {
        try {
            curentUrl = url;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void pause() {
        mediaPlayer.pause();
    }


    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        recycleTimer();
    }

    /**
     * 回收计时器
     */
    private void recycleTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.e("mediaPlayer", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }
}