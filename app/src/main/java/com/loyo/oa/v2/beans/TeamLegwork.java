package com.loyo.oa.v2.beans;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :团队拜访
 * 作者 : ykb
 * 时间 : 15/10/29.
 */
public class TeamLegwork extends BaseBeans {
    private int CustNum;
    private int VistNum;
    private ArrayList<TeamLegworkDetail>  Detail=new ArrayList<>();

    public ArrayList<TeamLegworkDetail>  getDetail() {
        return Detail;
    }

    public void setDetail(ArrayList<TeamLegworkDetail>  detail) {
        Detail = detail;
    }

    public int getCustNum() {
        return CustNum;
    }

    public void setCustNum(int custNum) {
        CustNum = custNum;
    }

    public int getVisitNum() {
        return VistNum;
    }

    public void setVisitNum(int visitNum) {
        VistNum = visitNum;
    }

    @Override
    String getOrderStr() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }
}
