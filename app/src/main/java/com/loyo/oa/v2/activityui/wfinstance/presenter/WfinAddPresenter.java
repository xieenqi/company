package com.loyo.oa.v2.activityui.wfinstance.presenter;

import android.widget.LinearLayout;

import com.loyo.oa.v2.tool.ImageInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yyy on 16/10/18.
 */

public interface WfinAddPresenter {

    /*新建审批验证*/
    void addWfinVeri(String deptId);

    /*新建审批请求*/
    void requestAddWfin(String title,String deptId,ArrayList<HashMap<String, Object>> workflowValues,
                        String mTemplateId,String projectId,
                        String uuid,String memo,
                        int attachmentCount);

    /*设置审批开始结束时间规范*/
    void setStartendTime();

    /*新增审批内容*/
    void addTypeData(LinearLayout wfinstance_data_container);

    /*上传附件*/
    void newUploadAttachement(String uuid,int bizType,ArrayList<ImageInfo> pickPhots);

}
