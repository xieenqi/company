package com.loyo.oa.v2.activityui.followup.model;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.signinnew.model.AudioModel;
import com.loyo.oa.v2.activityui.signinnew.model.CommentModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yyy on 16/11/15.
 */

public class FollowUpListModel implements Serializable{

    public String id;
    public String customerId;
    public String customerName;
    public String uuid;
    public Creator creator;
    public String audioUrl;
    public int audioLength;
    public long createAt;
    public String content;
    public ArrayList<Attachment> attachments;    //文件
    public ArrayList<Attachment> imgAttachments; //图片
    public ArrayList<AudioModel>  audioInfo;     //语音录音
    public ArrayList<String> atUserIds;
    public ArrayList<CommentModel> comments;  //评论内容
    public Location location;                 //位置信息

    public String typeName;
    public String contactName;
    public String atNameAndDepts;   //@的相关人


    public class Creator{
        public String name;
        public String avatar;
    }

    public class Location{
        public String addr;
    }

}
