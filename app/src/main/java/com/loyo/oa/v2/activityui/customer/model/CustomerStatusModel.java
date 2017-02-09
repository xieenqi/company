package com.loyo.oa.v2.activityui.customer.model;

import java.io.Serializable;
import java.util.List;

/**
 * 客户状态的实体
 * Created by jie on 17/2/9.
 */

public class CustomerStatusModel implements Serializable {
    public String id;
    public String name;
    public Boolean enable;
    public Boolean isSystem;
    public Boolean canEdit;
    public List<CustomerStatusItemModel> items;

    public class CustomerStatusItemModel implements Serializable {
        public String id;
        public String name;
        public Boolean isSystem;
        public Boolean canEdit;
        public Boolean canDelete;
        public Integer order;

        @Override
        public String toString() {
            return name;
        }
    }
}
