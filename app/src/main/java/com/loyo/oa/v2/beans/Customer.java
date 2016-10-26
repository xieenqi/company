package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Counter;
import com.loyo.oa.v2.activityui.customer.model.CustomerRegional;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;
import com.loyo.oa.v2.activityui.customer.model.Industry;
import com.loyo.oa.v2.activityui.customer.model.Locate;
import com.loyo.oa.v2.activityui.customer.model.Member;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.other.model.User;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :新版客户
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class Customer extends BaseBeans {
    /**
     * 我的客户
     **/
    public static final int CUSTOMER_TYPE_MINE = 1;
    /**
     * 团队客户
     **/
    public static final int CUSTOMER_TYPE_TEAM = CUSTOMER_TYPE_MINE + 1;
    /**
     * 公海客户
     **/
    public static final int CUSTOMER_TYPE_PUBLIC = CUSTOMER_TYPE_MINE + 2;
    /**
     * 我的附近客户
     **/
    public static final int CUSTOMER_TYPE_NEAR_MINE = CUSTOMER_TYPE_MINE + 3;
    /**
     * 公司已赢单客户
     **/
    public static final int CUSTOMER_TYPE_NEAR_COMPANY = CUSTOMER_TYPE_MINE + 4;
    /**
     * 团队附近客户
     **/
    public static final int CUSTOMER_TYPE_NEAR_TEAM = CUSTOMER_TYPE_MINE + 5;


    public enum CustomerType {
        MY(1),//我的客户
        TEAM(2),//团队客户
        PUBLIC(3),//公海客户

        NEAR_MY(4),//附近我的客户
        NEAR_COMPANY(5);//附近公司已赢单客户

        private int mValue;

        CustomerType(int value) {
            mValue = value;
        }

        public int getmValue() {
            return mValue;
        }
    }

    public String id;
    public String name;
    public Locate loc;
    public Locate position;
    public long createdAt;
    public ArrayList<ExtraData> extDatas = new ArrayList<>();
    public boolean lock;
    public long lastActAt;
    public String companyId;
    public User owner;
    public NewUser creator;
    public CustomerRegional regional;
    public Counter counter;
    public ArrayList<Member> members = new ArrayList<>();
    public ArrayList<NewTag> tags = new ArrayList<>();
    public ArrayList<Contact> contacts = new ArrayList<>();

    public String distance;
    public String lastAct;
    public String summary;
    public String uuid;
    public Industry industry;
    public int winCount;

    public SaleActivity saleActivityInfo;
    public int saleActivityNum;

    @Override
    public String getOrderStr() {
        return null;
    }

    public String getId() {
        return null == id ? "" : id;
    }
}
