package com.loyo.oa.v2.activityui.customer.bean;

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

}
