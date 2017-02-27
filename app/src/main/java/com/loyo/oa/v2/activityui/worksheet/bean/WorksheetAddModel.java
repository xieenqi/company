package com.loyo.oa.v2.activityui.worksheet.bean;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.tool.StringUtil;

import java.util.List;

/**
 * 新建工单的时候，数据实体
 * Created by jie on 17/2/24.
 */

public class WorksheetAddModel implements Cloneable {
    public String orderName;//订单名字
    public String orderId;//订单id
    public String typeName;//工单类型
    public String typeId;//工单类型id
    public String title;//工单标题
    public String content;//工单内容
    public String uuid;//附件

    /**
     * 实现克隆方法，复制对象
     * @return
     */
    @Override
    public WorksheetAddModel clone()  {
        WorksheetAddModel worksheetAddModel=new WorksheetAddModel();
        worksheetAddModel.orderName = null == orderName?null:new String (orderName);
        worksheetAddModel.orderId   = null == orderId?null:new String (orderId);
        worksheetAddModel.typeName  = null == typeName?null:new String (typeName);
        worksheetAddModel.typeId    = null == typeId?null:new String (typeId);
        worksheetAddModel.title     = null == title?null:new String (title);
        worksheetAddModel.content   = null == content?null:new String (content);
        worksheetAddModel.uuid      = null == uuid?null:new String (uuid);
        return worksheetAddModel;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof WorksheetAddModel){
            WorksheetAddModel temp= (WorksheetAddModel) obj;
            return (TextUtils.equals(orderName,temp.orderName)
            &&TextUtils.equals(orderId,temp.orderId)
            &&TextUtils.equals(typeName,temp.typeName)
            &&TextUtils.equals(typeId,temp.typeId)
            &&TextUtils.equals(title,temp.title)
            &&TextUtils.equals(content,temp.content)
            &&TextUtils.equals(uuid,temp.uuid)
            );

        }else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "orderName:"+orderName+",orderId:"+orderId+",typeName:"+typeName+",typeId:"+typeId+",title:"+title+",content:"+content+",uuid:"+uuid;
    }
}
