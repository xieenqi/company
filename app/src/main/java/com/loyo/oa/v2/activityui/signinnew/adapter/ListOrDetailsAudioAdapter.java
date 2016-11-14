package com.loyo.oa.v2.activityui.signinnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signinnew.model.AudioModel;

import java.util.ArrayList;

/**
 * 【语音录音】跟进拜访 列表详情 公用Adapter
 * Created by yyy on 16/11/14.
 */

public class ListOrDetailsAudioAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<AudioModel> audioInfo;

    public ListOrDetailsAudioAdapter(Context mContext,ArrayList<AudioModel> audioInfo){
        this.mContext = mContext;
        this.audioInfo = audioInfo;
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
        AudioModel audioModel = audioInfo.get(position);
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sigfollw_audio,null);
            holder.iv_calls = (TextView) convertView.findViewById(R.id.iv_calls);
            holder.tv_audio_length = (TextView) convertView.findViewById(R.id.tv_audio_length);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder {
        TextView iv_calls;    //录音播放
        TextView tv_audio_length; //录音长度
    }

}
