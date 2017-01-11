package com.loyo.oa.v2.activityui.customer.model;

import android.support.annotation.IntDef;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.beans.SaleActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
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


    public enum RecycleType {
        NONE(0) {
            @Override
            String getText() {
                return "--";
            }
        },
        MANUAL(1) {
            @Override
            String getText() {
                return "手动";
            }
        },
        AUTOMATIC(2) {
            @Override
            String getText() {
                return "自动";
            }
        };

        private int type;

        RecycleType(int value) {
            type = value;
        }

        public int getmValue() {
            return type;
        }

        abstract String getText();

        public static class RecycleTypeSerializer implements JsonSerializer<RecycleType>, JsonDeserializer<RecycleType> {

            @Override
            public JsonElement serialize(RecycleType src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.type);
            }

            @Override
            public RecycleType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

                return RecycleType.NONE;
            }
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
    public OrganizationalMember creator;
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

    public long activityRecycleAt;//跟进行为丢公海时间
    public long orderRecycleAt;   //订单丢公海时间
    public boolean activityRemind; //跟进行为丢公海提醒时间
    public boolean orderRemind;   //订单丢公海提醒时间

    public long recycledAt;//丢公海的时间
    public RecycleType recycleType;//丢公海类型 0.无 1.手动 2.自动
    public String recycleReason;//丢公海原因

    @Override
    public String getOrderStr() {
        return null;
    }

    public String getId() {
        return null == id ? "" : id;
    }

    /**
     * Added by Ethan 2016-11-30
     * <p>
     * "state": 1,  // 1表示普通客户，2表示公海客户，3表示回收站客户
     * "relationState": 1, // 1表示责任人，2表示参与人，3表示关联业务人员
     * "isStartUsing": true 客户管理菜单是否启用 1:是 0否
     */

    public int state;
    public int relationState;
    public boolean isStartUsing;


    public final static int NormalCustomer = 1;              /*1表示普通客户*/
    public final static int DumpedCustomer = 2;              /*2表示公海客户*/
    public final static int RecycledCustomer = 3;            /*3表示回收站客户*/

    public final static int RelationResponsible = 1;         /*1表示责任人*/
    public final static int RelationParticipated = 2;        /*2表示参与人*/
    public final static int RelationInvolved = 3;            /*3表示业务相关*/

    @IntDef({NormalCustomer, DumpedCustomer, RecycledCustomer})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CustomerState {
    }

    @IntDef({RelationResponsible, RelationParticipated, RelationInvolved})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RelationState {
    }
}
