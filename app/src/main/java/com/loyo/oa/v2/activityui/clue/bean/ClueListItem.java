package com.loyo.oa.v2.activityui.clue.bean;

import java.io.Serializable;

/**
 * 【线索列表Item】
 * Created by yyy on 16/8/22.
 */
public class ClueListItem implements Serializable{

    public String id;
    public String name;
    public String company_id;
    public String company_name;
    public String cellphone;
    public String tel;
    public Region region;
    public String address;
    public String source;
    public String remark;
    public int    status;
    public String creator_id;
    public String creator_name;
    public String admin_id;
    public String admin_name;
    public long ctime;
    public long mtime;
    public long ftime;

    public class Region{

        public String province;
        public String city;
        public String county;

    }


}
