package com.loyo.oa.v2.activityui.followup.model;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.common.Global;

import java.io.Serializable;
import java.util.ArrayList;

import static com.loyo.oa.v2.R.id.layout_clue;
import static com.loyo.oa.v2.R.id.tv_clue;

/**
 * Created by yyy on 16/11/15.
 */

public class FollowUpListModel implements Serializable {

    public String id;
    public String customerId;
    public String customerName;
    public String sealsleadId;
    public String salesleadCompanyName;
    public String uuid;
    public String creatorName;
    public Creator creator;
    public String audioUrl;
    public String avatar;
    public int audioLength;
    public long createAt;
    public long remindAt;
    public String content;
    public ArrayList<Attachment> attachments;    //文件
    public ArrayList<Attachment> imgAttachments; //图片
    public ArrayList<AudioModel> audioInfo;     //语音录音
    public ArrayList<CommentModel> comments;  //评论内容
    public Location location;                 //位置信息

    public String typeName;
    public String contactName;
    public String contactPhone;
    public String atNameAndDepts;   //@的相关人
    public String contactRole;//联系人角色


    public class Creator {
        public String name;
        public String avatar;
    }

    public class Location {
        public String addr;
        public String[] loc;
    }

    public void setCuleName(TextView tv_clue, ViewGroup layout_clue) {
        /** 线索 */
        if (!isCustomer()) {
            SpannableStringBuilder tt = new SpannableStringBuilder();
            tt.append("线索: ");
            tt.append(salesleadCompanyName);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#2c9dfc"));
            tt.setSpan(span, 3, tt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            layout_clue.setVisibility(View.VISIBLE);
            tv_clue.setText(tt);
            tv_clue.setOnTouchListener(Global.GetTouch());
        } else {
            layout_clue.setVisibility(View.GONE);
        }
    }

    public void setCusomerName(TextView tv_customer, ViewGroup layout_customer) {
        /** 客户 */
        if (isCustomer()) {
            SpannableStringBuilder tt = new SpannableStringBuilder();
            tt.append("客户: ");
            tt.append(customerName);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#2c9dfc"));
            tt.setSpan(span, 3, tt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            layout_customer.setVisibility(View.VISIBLE);
            tv_customer.setText(tt);
            tv_customer.setOnTouchListener(Global.GetTouch());
        } else {
            layout_customer.setVisibility(View.GONE);
        }
    }

    /*salesleadCompanyName customerName 根据此两字段判断是 客户 还是 线索*/
    public boolean isCustomer() {
        if (null != customerName && !TextUtils.isEmpty(customerName)) {
            return true;
        }
        return false;
    }

    public void setContactRole(TextView tv_contact_tag, ViewGroup ll_contact_tag) {
        if (isCustomer()) {
            ll_contact_tag.setVisibility(View.VISIBLE);
            tv_contact_tag.setText("联系人角色: " + (TextUtils.isEmpty(contactRole) ? "无" : contactRole));
        } else {
            ll_contact_tag.setVisibility(View.GONE);
        }
    }

    /*下次跟进时间的设置  默认是红色 没有提醒过  提醒过就是灰色*/
    public void setFullowUpTime(TextView tv_last_time, ImageView iv_lasttime, ViewGroup layout_lasttime) {
        if (remindAt != 0) {
            layout_lasttime.setVisibility(View.VISIBLE);
            tv_last_time.setText("下次跟进: " + DateTool.getDateTimeFriendly(remindAt));
            if (DateTool.isMoreCurrentTime(remindAt)) {
                tv_last_time.setTextColor(Color.parseColor("#999999"));
                iv_lasttime.setImageResource(R.drawable.icon_remaind_li);
            } else {
                tv_last_time.setTextColor(Color.parseColor("#f5625a"));
                iv_lasttime.setImageResource(R.drawable.icon_remaind_li2);
            }
        } else {
            layout_lasttime.setVisibility(View.GONE);
        }
    }
}
