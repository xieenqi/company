package com.loyo.oa.v2.activityui.clue.bean;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.signinnew.model.AudioModel;
import com.loyo.oa.v2.activityui.signinnew.model.CommentModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yyy on 16/11/15.
 */

public class ClueFollowUpListModel implements Serializable{

    public String id;
    public String customerId;
    public String creatorName;
    public long createAt;
    public String content;
    public long remindAt;
    public String typeName;
    public String contactName;
    public String audioUrl;
    public int audioLength;
    public ArrayList<Attachment> attachments;    //文件
    public ArrayList<Attachment> imgAttachments; //图片
    public ArrayList<AudioModel>  audioInfo;     //语音录音
    public String atNameAndDepts;  //@的相关人
    public ArrayList<CommentModel> comments;  //评论内容
    public String avatar;
    public String addr;


}
