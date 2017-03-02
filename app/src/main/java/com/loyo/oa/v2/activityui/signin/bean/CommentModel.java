package com.loyo.oa.v2.activityui.signin.bean;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.common.Global;

/**
 * 【评论】
 * Created by yyy on 16/11/14.
 */


public class CommentModel {

    public String id;
    public String bizzId;
    public String title;
    public String creatorId;
    public String creatorName;
    public int commentType;
    public int bizzType;
    public AudioModel audioInfo;
    public long createdAt;

    public void setRecordUI(ViewGroup layout_audio, TextView tv_calls, TextView tv_audio_length) {
        if (null != audioInfo) {
            layout_audio.setVisibility(View.VISIBLE);
            long audioLength = audioInfo.length;
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
            tv_audio_length.setText(audioLength + "\"");
            tv_calls.setOnTouchListener(Global.GetTouch());
        } else {
            layout_audio.setVisibility(View.GONE);
        }
    }

}
