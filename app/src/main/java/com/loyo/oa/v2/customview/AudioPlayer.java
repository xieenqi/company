package com.loyo.oa.v2.customview;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Player;

/**
 * Created by loyo_dev1 on 16/9/18.
 */
public class AudioPlayer implements  MediaPlayer.OnCompletionListener,View.OnClickListener{

    private ImageView layout_audio_pauseorplay,layout_audio_close;
    private TextView tv_audio_starttime, tv_audio_endtime;
    private ViewGroup layout_audioplayer, layout_audio_contral;
    private ViewGroup layout_add;
    private View mView;

    private boolean isStart = false;
    private boolean isMyUser;
    private boolean isOnPlay;

    private Player player;
    private SeekBar musicProgress;

    private String endTime;
    private int ss = 0;

    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 0x01:/*打开关闭播放器*/
                    LogUtil.dee("0x01");
                    if (layout_audioplayer.getVisibility() == View.VISIBLE) {
                        layout_audioplayer.setVisibility(View.GONE);
                        LogUtil.dee("1");
                        if (isMyUser)
                            LogUtil.dee("2");
                        layout_add.setVisibility(View.VISIBLE);
                    } else if (layout_audioplayer.getVisibility() == View.GONE) {
                        LogUtil.dee("3");
                        layout_audioplayer.setVisibility(View.VISIBLE);
                        layout_add.setVisibility(View.GONE);
                        tv_audio_endtime.setText(endTime);
                    }
                    break;

                case 0x02:/*播放暂停*/
                    if (isOnPlay) {
                        audioStart();
                    } else {
                        audioPause();
                    }
                    break;

                case 0x03:/*计时*/
                    if (ss == 0 || ss == -1) {
                        tv_audio_starttime.setText("00:00");
                    } else {
                        tv_audio_starttime.setText(DateTool.timet(ss + "", "mm:ss"));
                    }
                    LogUtil.dee("录音时间:" + ss);
                    break;

                case 0x04:/*播放停止*/
                    ss = 0;
                    musicProgress.setProgress(0);
                    layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
                    isOnPlay = true;
                    break;
            }
        }
    };

    public AudioPlayer(Context context,ViewGroup layout_add,boolean isMyUser){
        mView = LayoutInflater.from(context).inflate(R.layout.cusview_audio_player,null);
        this.isMyUser = isMyUser;
        this.layout_add = layout_add;
        initUI();

        player = new Player(musicProgress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player.mediaPlayer.setOnCompletionListener(this);
    }

    public void initUI(){
        layout_audio_pauseorplay = (ImageView)mView.findViewById(R.id.layout_audio_pauseorplay);
        musicProgress = (SeekBar)mView.findViewById(R.id.music_progress);
        layout_audioplayer = (ViewGroup)mView.findViewById(R.id.layout_audioplayer);
        layout_audio_contral = (ViewGroup)mView.findViewById(R.id.layout_audio_contral);
        tv_audio_starttime = (TextView) mView.findViewById(R.id.tv_audio_starttime);
        tv_audio_endtime = (TextView) mView.findViewById(R.id.tv_audio_endtime);
        layout_audio_close = (ImageView) mView.findViewById(R.id.layout_audio_close);

        layout_audio_close.setOnClickListener(this);
        layout_audio_pauseorplay.setOnClickListener(this);
        layout_audio_close.setOnTouchListener(Global.GetTouch());
    }

    public void startPlayer(long audioLength, final String url){
        if (layout_audioplayer.getVisibility() != View.VISIBLE) {
            mHandler.sendEmptyMessage(0x01);
        }
        if (null == player) {
            player = new Player(musicProgress);
        }
        audioStart();

        ss = 0;
        endTime = DateTool.timet(audioLength + "", "mm:ss");
        //endTimerInt = Integer.parseInt(audioLength + "");
        tv_audio_endtime.setText(endTime);

        new Thread(new Runnable() {
            @Override
            public void run() {
                player.playUrl(url);
            }
        }).start();

    }

    public  void audioStart(){
        isOnPlay = false;
        layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
        if (player != null) {
            player.play();
        }
    }

    public void audioPause(){
        isOnPlay = true;
        layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
        if (player != null) {
            player.pause();
        }
    }

   public void stopPlayer() {
        if (player != null) {
            player.stop();
            player = null;
        }
    }

    /**
     * Player启动与停止回调
     * */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(!isStart){
            isStart = true;
        }else if(isStart){
            isStart = false;
            mHandler.sendEmptyMessage(0x04);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            /*播放暂停*/
            case R.id.layout_audio_pauseorplay:
                mHandler.sendEmptyMessage(0x02);
                break;

            /*关闭播放器*/
            case R.id.layout_audio_close:
                stopPlayer();
                mHandler.sendEmptyMessage(0x01);
                break;
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;
        int endProgress = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();

            if (progress > endProgress) {
                ss++;
            } else if (progress < endProgress) {
                ss--;
            }
            endProgress = progress;
            mHandler.sendEmptyMessage(0x03);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            player.mediaPlayer.seekTo(progress);
        }
    }
}
