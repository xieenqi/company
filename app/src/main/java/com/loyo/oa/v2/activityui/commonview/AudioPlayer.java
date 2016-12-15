package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yyy on 16/11/17.
 */

public class AudioPlayer extends LinearLayout implements View.OnClickListener{

    private View mView;
    private Player player;
    private SeekBar musicProgress;
    private Context mContext;
    private TextView nowsView;
    private TextView tv_audio_starttime;
    private TextView tv_audio_endtime;
    private ImageView layout_audio_pauseorplay;
    private LinearLayout layout_audio_close;
    private RelativeLayout layout_audioplayer;

    private String playTime;
    private boolean isOnPlay;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            switch (msg.what){

                case 0x03:
                    tv_audio_starttime.setText(playTime);
                    break;

                 /*播放暂停*/
                case 0x02:
                    if (isOnPlay) {
                        audioStart(nowsView);
                    } else {
                        audioPause(nowsView);
                    }
                    break;

                /*播放停止*/
                case 0x04:
                    isOnPlay = true;
                    playTime = "00:00:00";
                    musicProgress.setProgress(0);
                    layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
                    break;

            }
        }
    };

    public AudioPlayer(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    private void initView(){
        mView = LayoutInflater.from(mContext).inflate(R.layout.sgcopy_audio_player,null);
        musicProgress = (SeekBar) mView.findViewById(R.id.music_progress);
        tv_audio_starttime = (TextView) mView.findViewById(R.id.tv_audio_starttime);
        tv_audio_endtime = (TextView) mView.findViewById(R.id.tv_audio_endtime);
        layout_audio_pauseorplay = (ImageView) mView.findViewById(R.id.layout_audio_pauseorplay);
        layout_audio_close = (LinearLayout) mView.findViewById(R.id.layout_audio_close);
        layout_audioplayer = (RelativeLayout) mView.findViewById(R.id.layout_audioplayer);
        layout_audio_pauseorplay.setOnClickListener(this);
        layout_audio_close.setOnClickListener(this);
        this.addView(mView);
    }

    /**
     * 初始化Player
     * */
    public void initPlayer(){

        // Add by Ethan on 2016/12/08 临时处理
        // TODO: 2016/12/8

        if (player != null) {
            player.stop();
        }

        if (musicProgress != null) {
            musicProgress.setOnSeekBarChangeListener(null);
        }

        player = new Player(musicProgress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player.mediaPlayer.setOnCompletionListener(new PlayerComplte());
    }


    /**
     * 线程池播放Player
     */
    public void threadPool(final AudioModel audioModel,TextView nowsView) {
        if(null == player){
            player = new Player(musicProgress);
            player.mediaPlayer.setOnCompletionListener(new PlayerComplte());
            player.mediaPlayer.setOnErrorListener(new PlayerError());
        }

        playTime = "00:00:00";
        musicProgress.setProgress(0);

        this.nowsView = nowsView;
        tv_audio_endtime.setText(DateTool.stringForTime((int) audioModel.length * 1000));
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    player.playUrl(audioModel.url);
                }catch (NullPointerException e){
                    LogUtil.dee("崩溃");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化Player
     */
    public void audioStart(TextView textView) {
        isOnPlay = false;
        MainApp.getMainApp().startAnim(textView);
        layout_audioplayer.setVisibility(View.VISIBLE);
        layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
        if (player != null) {
            player.play();
        }
    }

    /**
     * 是否正在播放
     * */
    public boolean isPlaying(){
        return player.mediaPlayer.isPlaying();
    }

    /**
     * 暂停Player
     * */
    public void audioPause(TextView textView){
        isOnPlay = true;
        MainApp.getMainApp().stopAnim(textView);
        if (player != null) {
            mHandler.sendEmptyMessage(0x04);
            player.pause();
        }
    }

    /**
     * 停止Player
     * */
    public void stopPlayer(){
        if (player != null) {
            player.stop();
        }
    }

    /**
     * 销毁Player
     */
    public void killPlayer() {
        if (player != null) {
            player.stop();
            player = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            /*播放,暂停*/
            case R.id.layout_audio_pauseorplay:
                mHandler.sendEmptyMessage(0x02);
                break;

            /*关闭播放条*/
            case R.id.layout_audio_close:
                audioPause(nowsView);
                layout_audioplayer.setVisibility(View.GONE);
                break;

        }
    }

    /**
     * Player监听
     * */
    class PlayerComplte implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mp.getDuration() != 0) {
                mHandler.sendEmptyMessage(0x04);
                MainApp.getMainApp().stopAnim(nowsView);
            }
        }
    }

    /**
     * Player播放失败监听
     * */
    class PlayerError implements MediaPlayer.OnErrorListener{
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            Toast.makeText(mContext,"录音文件损坏!",Toast.LENGTH_SHORT).show();
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
            if(null != player)
            this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
            playTime = DateTool.stringForTime(this.progress);
            mHandler.sendEmptyMessage(0x03);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            playTime = DateTool.stringForTime(progress);
            player.mediaPlayer.seekTo(progress);
            mHandler.sendEmptyMessage(0x03);
        }
    }
}
