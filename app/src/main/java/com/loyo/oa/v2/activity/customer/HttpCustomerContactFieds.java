package com.loyo.oa.v2.activity.customer;

import java.io.Serializable;

/**
 * Created by xeq on 16/3/24.
 */
public class HttpCustomerContactFieds implements Serializable {
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
}
