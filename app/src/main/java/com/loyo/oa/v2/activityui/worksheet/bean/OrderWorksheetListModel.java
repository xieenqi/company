package com.loyo.oa.v2.activityui.worksheet.bean;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yyy on 16/10/22.
 */

public class OrderWorksheetListModel implements Serializable {

    public String uuid;
    public String title;
    public String templateId;
    public String templateName;
    public String content;
    public int    size;
    public List<String> mSelectPath;

    /**
     * 实现克隆方法，复制对象
     *
     * @return
     */
    @Override
    public OrderWorksheetListModel clone() {
        OrderWorksheetListModel worksheet = new OrderWorksheetListModel();
        worksheet.templateName  = null == templateName ? null : new String(templateName);
        worksheet.templateId    = null == templateId ? null : new String(templateId);
        worksheet.title         = null == title ? null : new String(title);
        worksheet.content       = null == content ? null : new String(content);
        worksheet.uuid          = null == uuid ? null : new String(uuid);
        return worksheet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OrderWorksheetListModel) {
            OrderWorksheetListModel temp = (OrderWorksheetListModel) obj;
            return (
                    TextUtils.equals(templateName, temp.templateName)
                    && TextUtils.equals(templateId, temp.templateId)
                    && TextUtils.equals(title, temp.title)
                    && TextUtils.equals(content, temp.content)
                    && TextUtils.equals(uuid, temp.uuid)
            );

        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return super.toString()+"|templateName:" + templateName + ",templateId:" + templateId + ",title:" + title + ",content:" + content + ",uuid:" + uuid;
    }

    public boolean isEmpty() {
        return (
                        TextUtils.isEmpty(templateName) &&
                        TextUtils.isEmpty(templateId) &&
                        TextUtils.isEmpty(title) &&
                        TextUtils.isEmpty(content)
        );
    }

    /**
     * 验证数据，直接返回错误类型，如果没错，就返回null
     * @return
     */
    public String verify(){
        if(TextUtils.isEmpty(templateId)){
            return "工单类型未选择";
        }else if(TextUtils.isEmpty(title)){
            return "工单标题未填写";
        }
        return null;
    }
}
