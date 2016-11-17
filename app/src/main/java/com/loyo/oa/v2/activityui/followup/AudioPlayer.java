package com.loyo.oa.v2.activityui.followup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDynamicManageActivity;
import com.loyo.oa.v2.activityui.signinnew.model.AudioModel;
import com.loyo.oa.v2.tool.Player;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yyy on 16/11/17.
 */

public class AudioPlayer extends LinearLayout{

    private View mView;
    private Player player;
    private SeekBar musicProgress;
    private Context mContext;
    private TextView lastView;
    private TextView nowsView;

    public interface AnimControlCb{
        void startAnim();
        void stopAnim();
    }


    public AudioPlayer(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    private void initView(){
        mView = LayoutInflater.from(mContext).inflate(R.layout.cusview_audio_player,null);
        musicProgress = (SeekBar) mView.findViewById(R.id.music_progress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player = new Player(musicProgress);
    }


    /**
     * 线程池播放Player
     */
    public void threadPool(final String url) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                player.playUrl(url);
            }
        });
    }

    /**
     * 初始化Player
     */
    public void audioStart() {
        if (player != null) {
            player.play();
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

    /**
     * 拖动条监听
     */
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
