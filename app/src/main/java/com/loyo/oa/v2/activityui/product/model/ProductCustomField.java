package com.loyo.oa.v2.activityui.product.model;

import java.util.ArrayList;

/**
 * Created by yyy on 16/12/30.
 */

public class ProductCustomField {

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
