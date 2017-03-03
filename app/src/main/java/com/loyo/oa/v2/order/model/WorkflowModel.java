package com.loyo.oa.v2.order.model;

import android.text.TextUtils;

/**
 * Created by EthanGong on 2017/2/23.
 */

public class WorkflowModel {
    public String id;
    public String title;
    public boolean enable;
    public String content;

    public boolean isChecked;

    public String getDescription() {
        if (TextUtils.isEmpty(content)) {
            return "流程说明：无";
        }
        else {
            return "流程说明：" + content;
        }
    }
}
