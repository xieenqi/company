package com.loyo.oa.v2.beans;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :团队拜访
 * 作者 : ykb
 * 时间 : 15/10/29.
 */
public class TeamLegwork extends BaseBeans {
    public int CustNum;
    public int VistNum;
    public ArrayList<TeamLegworkDetail> Detail;


    @Override
    String getOrderStr() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }
}
