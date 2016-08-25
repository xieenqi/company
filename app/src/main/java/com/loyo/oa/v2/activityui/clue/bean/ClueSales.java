package com.loyo.oa.v2.activityui.clue.bean;

import com.loyo.oa.v2.activityui.customer.bean.CustomerRegional;

import java.io.Serializable;

/**
 * 线索详情 详情
 * Created by xeq on 16/8/23.
 */
public class ClueSales implements Serializable {
    public String id;
    public String name;
    public String companyId;
    public String companyName;
    public String cellphone;
    public String tel;

    public CustomerRegional region;
    public String address;
    public String source;
    public String remark;
    public int status;
    public String creatorId;
    public String creatorName;
    public String responsorId;
    public String responsorName;
    public long createAt;
    public long updateAt;
    public long lastActAt;
    public int saleActivityCount;

    public String getRegion() {
        if (region == null) {
            return "";
        }
        return region.salesleadDisplayText();
    }

    public String getStatus() {
        if (status == 0) {
            return "";
        }
        switch (status) {
            case 1:
                return "未处理";
            case 2:
                return "已联系";
            case 3:
                return "关闭";
        }
        return "";
    }

}
