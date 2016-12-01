package com.loyo.oa.v2.activityui.followup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.signinnew.model.CommentModel;
import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.tool.DateTool;

import java.util.ArrayList;

/**
 * 【跟进拜访评论】详情列表评论 公用Adapter
 * Created by yyy on 16/11/11.
 */

public class ListOrDetailsCommentAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CommentModel> commentsl;
    private AudioPlayCallBack audioPlayCallBack;

    public ListOrDetailsCommentAdapter(Context mContext, ArrayList<CommentModel> commentsl, AudioPlayCallBack audioPlayCallBack) {
        this.mContext = mContext;
        this.commentsl = commentsl;
        this.audioPlayCallBack = audioPlayCallBack;
    }

    @Override
    public int getCount() {
        return commentsl == null ? 0 : commentsl.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final CommentModel commentModel = commentsl.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.layout_audio = (LinearLayout) convertView.findViewById(R.id.layout_audio);
            holder.tv_audio_length = (TextView) convertView.findViewById(R.id.tv_audio_length);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(commentModel.creatorName + ": ");
        holder.tv_title.setText(commentModel.title);

        final TextView tv_calls = (TextView) convertView.findViewById(R.id.iv_calls);

        /** 如果有语音 */
        if (null != commentModel.audioInfo) {
            holder.layout_audio.setVisibility(View.VISIBLE);
            long audioLength = commentModel.audioInfo.length;
            if (audioLength > 0 && audioLength <= 10) {
                tv_calls.setText("000");
            } else if (audioLength > 10 && audioLength <= 20) {
                tv_calls.setText("00000");
            } else if (audioLength > 20 && audioLength <= 30) {
                tv_calls.setText("0000000");
            } else if (audioLength > 30 && audioLength <= 40) {
                tv_calls.setText("00000000");
            } else if (audioLength > 40 && audioLength <= 50) {
                tv_calls.setText("000000000");
            } else if (audioLength > 50 && audioLength <= 60) {
                tv_calls.setText("0000000000");
            } else {
                tv_calls.setText("");
            }
            holder.layout_audio.setVisibility(View.VISIBLE);
            holder.tv_audio_length.setText(audioLength+"\"");

        } else {
            holder.layout_audio.setVisibility(View.GONE);
        }

        /*点击播放录音*/
        tv_calls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayCallBack.playVoice(commentModel.audioInfo, tv_calls);
            }
        });

        return convertView;
    }


    class ViewHolder {
        TextView tv_name;
        TextView tv_title;
        TextView tv_audio_length;
        LinearLayout layout_audio;
    }
}
