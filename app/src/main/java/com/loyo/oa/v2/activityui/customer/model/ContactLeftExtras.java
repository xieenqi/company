package com.loyo.oa.v2.activityui.customer.model;

import android.text.TextUtils;

import com.loyo.oa.common.utils.DateTool;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 客户联系人 左侧Lable数据  销售机会动态字段公用Ben
 *
 * Created by loyo_dev1 on 16/3/24.
 */
public class ContactLeftExtras implements Serializable {


    public String name;
    public String fieldName;
    public String type;
    public String label;
    public boolean required;
    public boolean enabled;
    public boolean sortable;
    public boolean isList;
    public boolean isSystem;
    public boolean isCustom;
    public boolean canEdit;
    public String val;
    public ArrayList<String> defVal = new ArrayList<>();

    public String getFormatValue() {
        try {
            if (("birth".equals(type) ||"long".equals(type)) && !TextUtils.isEmpty(val)) {
                return DateTool.getDateTimeFriendly(Long.valueOf(val));
            }
        }
        catch (Exception e) {
            return val;
        }
        return val;
    }

}
