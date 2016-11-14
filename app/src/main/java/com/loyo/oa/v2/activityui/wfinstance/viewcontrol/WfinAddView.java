package com.loyo.oa.v2.activityui.wfinstance.viewcontrol;

import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.tool.ImageInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yyy on 16/10/18.
 */

public interface WfinAddView extends BaseView{

    /*设置时间格式处理*/
    void setStartendTimeEmbl();

    /*新建审批验证通过*/
    void requestAddWfinVeriSuccess(ArrayList<HashMap<String, Object>> workflowValues);

    /*新建审批成功处理*/
    void requestAddWfinSuccessEmbl(WfInstance wfInstance);

    /*上传附件成功处理*/
    void uploadSuccessEmbl(ArrayList<ImageInfo> pickPhots);


}
