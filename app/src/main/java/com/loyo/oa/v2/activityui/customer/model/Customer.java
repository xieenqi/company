package com.loyo.oa.v2.activityui.customer.model;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

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

import static com.loyo.oa.v2.activityui.customer.model.Customer.RecycleType.AUTOMATIC;
import static com.loyo.oa.v2.activityui.customer.model.Customer.RecycleType.MANUAL;

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
            public String getText() {
                return "--";
            }
        },
        MANUAL(1) {
            @Override
            public String getText() {
                return "手动丢公海";
            }
        },
        AUTOMATIC(2) {
            @Override
            public String getText() {
                return "自动丢公海";
            }
        };

        private int type;

        RecycleType(int value) {
            type = value;
        }

        public int getmValue() {
            return type;
        }

        public abstract String getText();

        public static class RecycleTypeSerializer implements JsonSerializer<RecycleType>, JsonDeserializer<RecycleType> {

            @Override
            public JsonElement serialize(RecycleType src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.type);
            }

            @Override
            public RecycleType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                RecycleType[] list = RecycleType.values();
                for (int i = 0; i < list.length; i++) {
                    if (list[i].type == json.getAsInt()) {
                        return list[i];
                    }
                }
                return RecycleType.NONE;
            }
        }


    }

    public String id;
    public String name;
    public Locate loc;//客户详细地址，一般是人可以手动输入的地址
    public Locate position;//客户位置，一般是定位得到的
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
    public String summary;//客户介绍
    public String uuid;
    public Industry industry;
    public int winCount;

    public SaleActivity saleActivityInfo;
    public int saleActivityNum;
    public int wfNum;

    public long activityRecycleAt;//跟进行为丢公海时间
    public long orderRecycleAt;   //订单丢公海时间
    public boolean activityRemind; //跟进行为丢公海提醒飘红
    public boolean orderRemind;   //订单丢公海提醒飘红
    public String activityRecycleRemind; // 拜访丢公海提醒文字
    public String orderRecycleRemind; // 订单丢公海提醒文字

    public long saleactRecycleAt; // 跟进丢公海
    public long visitRecycleAt;   // 拜访丢公海
    public long voiceRecycleAt;   // 电话跟进丢公海
    public int conditionType;     // 条件类型： 1、全部（and）2、任意（or）

    public long recycledAt;//丢公海的时间
    public RecycleType recycleType;//丢公海类型 0.无 1.手动 2.自动
    public String recycleReason;//丢公海原因

    public String statusId;//客户状态id
    public String statusName;//客户状态
    public String webSite;//网址
    public String recycleName;//丢公海人

    @Override
    public String getOrderStr() {
        return null;
    }

    public String getId() {
        return null == id ? "" : id;
    }

    public String displayTagString() {
        StringBuffer buffer = new StringBuffer();
        boolean needSeperate = false;
        for (NewTag tag : tags) {
            if (needSeperate) {
                buffer.append("、");
            }
            buffer.append(tag.getItemName());
            needSeperate = true;
        }

        String result = buffer.toString();

        return TextUtils.isEmpty(result) ? "无" : result;
    }

    public SpannableStringBuilder getFormattedDropRemind() {
        if (!hasDropRemind()) {
            SpannableStringBuilder builder = new SpannableStringBuilder("");
            return builder;
        }

        StringBuilder sb = new StringBuilder();
        boolean hasActivityRemind = false;
        boolean hasOrderRemind = false;
        if (!TextUtils.isEmpty(activityRecycleRemind)) {
            sb.append(activityRecycleRemind);
            hasActivityRemind = true;
        }
        if (!TextUtils.isEmpty(orderRecycleRemind)) {
            if (hasActivityRemind) {
                sb.append(" 或 ");
            }
            sb.append(orderRecycleRemind);
            hasOrderRemind = true;
        }
        if (hasActivityRemind || hasOrderRemind) {
            sb.append("丢公海");
        }
        String compoundRemind = sb.toString();

        SpannableStringBuilder builder = new SpannableStringBuilder(compoundRemind);
        try {
            if (hasActivityRemind && activityRemind) {
                ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#f5625a"));
                builder.setSpan(redSpan, 0, activityRecycleRemind.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
        }
        try {
            if (hasOrderRemind && orderRemind) {
                ForegroundColorSpan redSpan2 = new ForegroundColorSpan(Color.parseColor("#f5625a"));
                builder.setSpan(redSpan2, compoundRemind.length() - orderRecycleRemind.length() - 3,
                        compoundRemind.length() - 3,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
        }
        return builder;
    }

    public boolean hasDropRemind() {
        return !TextUtils.isEmpty(activityRecycleRemind) || !TextUtils.isEmpty(orderRecycleRemind) ;
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

    public String getRecycleName() {
        if (recycleType == MANUAL) {
            return recycleName;
        }else if(recycleType == AUTOMATIC){
            return "系统";
        }
        return "--";
    }
}
