package com.loyo.oa.v2.activityui.sale.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 销售机会动态字段
 * Created by xeq on 16/5/18.
 */
public class SaleFild implements Serializable {
    public String type;
    public String label;
    public boolean required;
    public boolean enabled;
    public boolean sortable;
    public boolean isList;
    public ArrayList<String> defVal;

//    {
//        "name": "changeType",
//            "fieldName": "change_type",
//            "type": "list",
//            "label": "机会类型",
//            "required": false,
//            "enabled": true,
//            "sortable": false,
//            "isList": true,
//            "defVal": [
//        "老客户",
//                "新客户"
//        ],
//        "val": ""
//    },
}
