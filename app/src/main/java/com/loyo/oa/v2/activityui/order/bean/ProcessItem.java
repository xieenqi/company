package com.loyo.oa.v2.activityui.order.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by EthanGong on 2017/1/10.
 */

public class ProcessItem implements Serializable {
    public String id;
    public String name;


    public static ArrayList<ProcessItem> testList() {
        ArrayList<ProcessItem> result = new ArrayList<>();


        for (int i = 0; i < 10; i++) {
            ProcessItem item = new ProcessItem();
            item.id = "id:"+i;
            item.name = "类型" + (i+1) + "订单流程";
            result.add(item);
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessItem)) return false;

        ProcessItem item = (ProcessItem) o;

        if (id != null ? !id.equals(item.id) : item.id != null) return false;
        return name != null ? name.equals(item.name) : item.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
