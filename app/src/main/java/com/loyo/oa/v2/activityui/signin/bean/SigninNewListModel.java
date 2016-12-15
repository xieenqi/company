package com.loyo.oa.v2.activityui.signin.bean;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yyy on 16/11/12.
 */

public class SigninNewListModel implements Serializable {

    public String id;
    public String customerId;
    public String customerName;  //客户
    public String position;      //客户定位
    public String contactName;   //联系人
    public String ContactTpl;
    public String gpsInfo;
    public String address;       //客户地址
    public String distance;
    public Creator creator;
    public long createdAt;      //创建时间
    public long updatedAt;      //更新时间
    public String attachmentUUId; //uuid
    public String memo;           //内容
    public String atNameAndDepts; //内容
    public double offsetDistance;  //偏差距离
    public ArrayList<AudioModel>  audioInfo;       //语音录音
    public ArrayList<Attachment> attachments;      //文件
    public ArrayList<Attachment> imageAttachments; //图片
    public ArrayList<CommentModel> comments;  //评论内容


    public class Creator {
        public String id;
        public String name;
        public String avatar;
    }
}
