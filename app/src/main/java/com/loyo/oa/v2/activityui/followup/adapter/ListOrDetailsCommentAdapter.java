package com.loyo.oa.v2.activityui.followup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signinnew.model.CommentModel;
import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;

import java.util.ArrayList;

/**
 * 【跟进拜访评论】详情列表评论 公用Adapter
 * Created by yyy on 16/11/11.
 */

public class ListOrDetailsCommentAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<CommentModel> commentsl;

    public  ListOrDetailsCommentAdapter(Context mContext,ArrayList<CommentModel> commentsl){
        this.mContext = mContext;
        this.commentsl = commentsl;
    }

    @Override
    public int getCount() {
        return commentsl.size();
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
        CommentModel commentModel = commentsl.get(position);
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment,null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv_calls = (TextView) convertView.findViewById(R.id.iv_calls);
            holder.layout_audio = (LinearLayout) convertView.findViewById(R.id.layout_audio);

            holder.tv_audio_length = (TextView) convertView.findViewById(R.id.tv_audio_length);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(commentModel.creatorName+": ");
        holder.tv_title.setText(commentModel.title);

        /** 如果有语音 */
        if(null != commentModel.audioInfo){
            holder.layout_audio.setVisibility(View.VISIBLE);
            holder.iv_calls.setText("00000000");
        }else{
            holder.layout_audio.setVisibility(View.GONE);
        }

        return convertView;
    }


    class ViewHolder{
        TextView tv_name;
        TextView iv_calls;
        TextView tv_title;
        TextView tv_audio_length;

        LinearLayout layout_audio;
    }
}
