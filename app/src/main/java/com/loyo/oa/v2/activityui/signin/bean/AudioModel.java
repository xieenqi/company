package com.loyo.oa.v2.activityui.signin.bean;


import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.tool.LogUtil;

import static com.loyo.oa.v2.R.id.layout_audio;
import static com.loyo.oa.v2.R.id.tv_audio_length;

/**
 * Created by yyy on 16/11/14.
 */

public class AudioModel {
    public String url;
    public long length;

    public void setRecordUI(ViewGroup layout_audio, TextView tv_calls, TextView tv_audio_length) {
        /*判断是否有录音*/
        if (!TextUtils.isEmpty(url)) {
            long audioLength = length;
            LogUtil.dee("length:" + audioLength);
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
            layout_audio.setVisibility(View.VISIBLE);
            tv_audio_length.setText(length + "\"");
        } else {
            layout_audio.setVisibility(View.GONE);
        }
    }
}