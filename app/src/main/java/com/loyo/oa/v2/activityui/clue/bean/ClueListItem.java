package com.loyo.oa.v2.activityui.clue.bean;

import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.beans.BaseBeans;

import java.io.Serializable;

/**
 * 【线索列表Item】
 * Created by yyy on 16/8/22.
 */
public class ClueListItem extends BaseBean implements Serializable{

    public String id;
    public String name;
    public String companyName;
    public String responsorName;
    public long   lastActAt;

}
