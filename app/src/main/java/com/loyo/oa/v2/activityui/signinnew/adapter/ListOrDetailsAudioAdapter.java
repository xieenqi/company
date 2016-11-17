package com.loyo.oa.v2.activityui.signinnew.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.followup.AudioPlayer;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.signinnew.model.AudioModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 【语音录音】跟进拜访 列表详情 公用Adapter
 * Created by yyy on 16/11/14.
 */

public class ListOrDetailsAudioAdapter extends BaseAdapter implements AudioPlayer.AnimControlCb {

    private TextView lastView,nowsView;
    private Context mContext;
    private ArrayList<AudioModel> audioInfo;
    private AudioPlayCallBack audioPlayCallBack;

    public ListOrDetailsAudioAdapter(Context mContext,ArrayList<AudioModel> audioInfo,AudioPlayCallBack audioPlayCallBack){
        this.mContext = mContext;
        this.audioInfo = audioInfo;
        this.audioPlayCallBack = audioPlayCallBack;
    }

    @Override
    public int getCount() {
        return audioInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return audioInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final AudioModel audioModel = audioInfo.get(position);
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sigfollw_audio,null);
            holder.tv_calls = (TextView) convertView.findViewById(R.id.iv_calls);
            holder.tv_audio_length = (TextView) convertView.findViewById(R.id.tv_audio_length);
            holder.layout_audio = (LinearLayout) convertView.findViewById(R.id.layout_audio);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        /*判断是否有录音*/
        if (null != audioModel.url && !TextUtils.isEmpty(audioModel.url)) {
            long audioLength = audioModel.length;
            if (audioLength > 0 && audioLength <= 60) {
                holder.tv_calls.setText("000");
            } else if (audioLength > 60 && audioLength <= 300) {
                holder.tv_calls.setText("00000");
            } else if (audioLength > 300 && audioLength <= 600) {
                holder.tv_calls.setText("0000000");
            } else if (audioLength > 600 && audioLength <= 1200) {
                holder.tv_calls.setText("000000000");
            } else if (audioLength > 1200 && audioLength <= 1800) {
                holder.tv_calls.setText("00000000000");
            } else if (audioLength > 1800 && audioLength <= 3600) {
                holder.tv_calls.setText("00000000000000");
            } else if (audioLength > 3600) {
                holder.tv_calls.setText("0000000000000000");
            } else {
                holder.tv_calls.setText("");
            }
            holder.layout_audio.setVisibility(View.VISIBLE);
            holder.tv_audio_length.setText(DateTool.stringForTime((int) audioModel.length * 1000));
        } else {
            holder.layout_audio.setVisibility(View.GONE);
        }

        /*点击播放录音*/
        holder.tv_calls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayCallBack.playVoice(audioModel);
            }
        });

        return convertView;
    }

    @Override
    public void startAnim() {

    }

    @Override
    public void stopAnim() {

    }

    class ViewHolder {
        TextView tv_calls;        //录音播放
        TextView tv_audio_length; //录音长度
        LinearLayout layout_audio;
    }
}
