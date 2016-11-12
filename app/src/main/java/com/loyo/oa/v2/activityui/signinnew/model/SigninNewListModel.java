package com.loyo.oa.v2.activityui.signinnew.model;

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
    public Creator creator;
    public long createdAt;      //创建时间
    public long updatedAt;      //更新时间
    public String attachmentUUId; //uuid
    public String memo;           //内容
    public String atNameAndDepts; //内容
    public float offsetDistance;  //偏差距离
    public ArrayList<AudioModel> audioInfo = new ArrayList<>();

    public class Creator {
        public String id;
        public String name;
        public String avatar;
    }

    public class AudioModel {
        public String url;
        public int length;
    }
}
