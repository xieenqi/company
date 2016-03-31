package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/3/24.
 */
public class ContactExtras implements Serializable {

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
