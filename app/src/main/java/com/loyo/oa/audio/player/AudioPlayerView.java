package com.loyo.oa.audio.player;

import android.content.Context;
import android.util.AttributeSet;
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
import com.loyo.oa.v2.tool.Player;

import static com.loyo.oa.v2.R.id.tv_audio_endtime;

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
    private LinearLayout layout_audio_close;
    private RelativeLayout layout_audioplayer;

    //private String playTime;
    private boolean isOnPlay;


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
        layout_audio_close = (LinearLayout) mView.findViewById(R.id.layout_audio_close);
        layout_audioplayer = (RelativeLayout) mView.findViewById(R.id.layout_audioplayer);
        layout_audio_pauseorplay.setOnClickListener(this);
        layout_audio_close.setOnClickListener(this);
        this.addView(mView);
        AudioPlayer.getInstance().bindView(this);
    }

    public void updateUI(AudioPlayUpdate updateState) {

        switch (updateState.type){

            case AudioPlayUpdate.START:

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
                //startTimeRest();
                musicProgress.setProgress(0);
                layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
                MainApp.getMainApp().stopAnim(nowsView);
                break;

            // 拖动条..开始时间
            case AudioPlayUpdate.PROGRESS:
                tv_audio_starttime.setText(updateState.progress);
                break;

            // 播放错误
            case AudioPlayUpdate.ERROR:
                Toast.makeText(mContext,"录音文件损坏!",Toast.LENGTH_SHORT).show();
                break;

        }
    }


    /**
     * 是否正在播放
     * */
    public boolean isPlaying(){
        return callbackHandler.onPlaying();
    }

    // 初始化
    public void onInit(){
        if (callbackHandler != null) {
            callbackHandler.onInit(musicProgress);
        }
    }

    // 第一次播放
    public void onStart(AudioModel audioModel,TextView nowsView){
        if (callbackHandler != null) {
            isOnPlay = false;
            this.nowsView = nowsView;
            //startTimeRest();
            tv_audio_endtime.setText(com.loyo.oa.common.utils.DateTool.int2time((int) audioModel.length * 1000));
            callbackHandler.onStart(this,audioModel);
        }
    }

    // 继续播放
    public void onResume(TextView textView) {
        if (callbackHandler != null) {
            isOnPlay = false;
            MainApp.getMainApp().startAnim(textView);
            layout_audioplayer.setVisibility(View.VISIBLE);
            layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
            callbackHandler.onResume(this);
        }
    }

    // 暂停按钮
    public void onPause(TextView textView) {
        if(callbackHandler != null){
            isOnPlay = true;
            //startTimeRest();
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

/*    public void startTimeRest(){
        playTime = "00:00:00";
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            /*播放,暂停*/
            case R.id.layout_audio_pauseorplay:
                if (isOnPlay) {
                    isOnPlay = false;
                    layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
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
