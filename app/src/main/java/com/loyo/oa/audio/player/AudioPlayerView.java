package com.loyo.oa.audio.player;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.LogUtil;

/**
 * Created by EthanGong on 2016/12/10.
 */

public class AudioPlayerView extends LinearLayout implements View.OnClickListener{

    private View mView;
    private SeekBar musicProgress;
    private Context mContext;
    private TextView nowsView;
    private TextView tv_audio_starttime;
    private TextView tv_audio_endtime;
    private ImageView layout_audio_pauseorplay;
    private ImageView image;
    private LinearLayout layout_audio_close;
    private RelativeLayout layout_audioplayer;
    private ProgressBar progress;

    private boolean isOnPlay;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0x01){
                progress.setVisibility(View.GONE);
            }else if(msg.what == 0x02){
                progress.setVisibility(View.VISIBLE);
            }
        }
    };


    public AudioPlayerView(Context context) {
        super(context);
        mContext = context;
        initView();
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

    private void initView(){
        mView = LayoutInflater.from(mContext).inflate(R.layout.sgcopy_audio_player,null);
        musicProgress = (SeekBar) mView.findViewById(R.id.music_progress);
        tv_audio_starttime = (TextView) mView.findViewById(R.id.tv_audio_starttime);
        tv_audio_endtime = (TextView) mView.findViewById(R.id.tv_audio_endtime);
        layout_audio_pauseorplay = (ImageView) mView.findViewById(R.id.layout_audio_pauseorplay);
        image = (ImageView) mView.findViewById(R.id.image);
        layout_audio_close = (LinearLayout) mView.findViewById(R.id.layout_audio_close);
        layout_audioplayer = (RelativeLayout) mView.findViewById(R.id.layout_audioplayer);
        progress = (ProgressBar) mView.findViewById(R.id.progress);
        layout_audio_pauseorplay.setOnClickListener(this);
        layout_audio_close.setOnClickListener(this);
        this.addView(mView);
        AudioPlayer.getInstance().bindView(this);
    }


    public void updateUI(AudioPlayUpdate updateState) {

        switch (updateState.type){

            // 播放
            case AudioPlayUpdate.START:
                MainApp.getMainApp().startAnim(nowsView);
                layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
                break;

            // 暂停
            case AudioPlayUpdate.PAUSE:
                isOnPlay = true;
                layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
                MainApp.getMainApp().stopAnim(nowsView);
                break;

            // 停止
            case AudioPlayUpdate.STOP:
                isOnPlay = true;
                musicProgress.setProgress(0);
                layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
                MainApp.getMainApp().stopAnim(nowsView);
                break;

            // 拖动条..开始时间
            case AudioPlayUpdate.PROGRESS:
                tv_audio_starttime.setText(updateState.msg);
                break;

            // 播放错误
            case AudioPlayUpdate.ERROR:
                Toast.makeText(mContext,"录音文件损坏!",Toast.LENGTH_SHORT).show();
                break;

            // 录音时长
            case AudioPlayUpdate.SIZE:
                if(Integer.parseInt(updateState.msg) != 0){
                    mHandler.sendEmptyMessage(0x01);
                }
                break;
        }
    }

    // 重置view
    public void restView(TextView textView){
        isOnPlay = false;
        mHandler.sendEmptyMessage(0x02);
        MainApp.getMainApp().startAnim(textView);
    }

    // 是否正在播放
    public boolean isPlaying(){
        return callbackHandler.onPlaying();
    }

    // 初始化
    public void onInit(){
        if (callbackHandler != null) {
            callbackHandler.onInit(musicProgress);
        }
    }

    // 首次播放
    public void onStart(AudioModel audioModel,TextView nowsView){
        if (callbackHandler != null) {
            LogUtil.dee("onStart");
            /*mHandler.sendEmptyMessage(0x02);
            isOnPlay = false;
            MainApp.getMainApp().startAnim(nowsView);*/
            restView(nowsView);
            this.nowsView = nowsView;
            layout_audioplayer.setVisibility(View.VISIBLE);
            layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
            tv_audio_endtime.setText(com.loyo.oa.common.utils.DateTool.int2time((int) audioModel.length * 1000));
            callbackHandler.onStart(this,audioModel);
        }
    }

    // 暂停后..继续播放
    public void onResume(TextView textView) {
        if (callbackHandler != null) {
            LogUtil.dee("onResume");
            //mHandler.sendEmptyMessage(0x02);
            //isOnPlay = false;
            //MainApp.getMainApp().startAnim(textView);
            restView(textView);
            //layout_audioplayer.setVisibility(View.VISIBLE);
            layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
            callbackHandler.onResume(this);
        }
    }

    // 暂停按钮
    public void onPause(TextView textView) {
        if(callbackHandler != null){
            isOnPlay = true;
            musicProgress.setProgress(0);
            layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
            MainApp.getMainApp().stopAnim(textView);
            callbackHandler.onPause(this);
        }
    }

    // 关闭播放器
    public void onStop() {
        layout_audioplayer.setVisibility(View.GONE);
        callbackHandler.onStop(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            /*播放,暂停*/
            case R.id.layout_audio_pauseorplay:
                if (isOnPlay) {
                    isOnPlay = false;
                    callbackHandler.onResume(this);
                } else {
                    callbackHandler.onPause(this);
                }
                break;

            /*关闭播放条*/
            case R.id.layout_audio_close:
                callbackHandler.onPause(this);
                layout_audioplayer.setVisibility(View.GONE);
                break;

        }
    }
}
