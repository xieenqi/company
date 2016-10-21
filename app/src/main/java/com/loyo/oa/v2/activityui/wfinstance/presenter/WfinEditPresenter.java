package com.loyo.oa.v2.activityui.wfinstance.presenter;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yyy on 16/10/20.
 */

public interface WfinEditPresenter {

    /*获取附件*/
    void getAttachments(String uui);

    /*上传附件*/
    void newUploadAttachement(String uuid,File file);

    /*设置开始结束时间规范*/
    void setStartendTime(ViewGroup layout_wfinstance_data,LinearLayout wfinstance_data_container);

    /*新增审批内容 栏目处理*/
    void addTypeData(LinearLayout wfinstance_data_container);

    /*编辑请求验证*/
    void requestEditWfinVeri(String deptId);

    /*编辑请求*/
    void requestEditWfin(String id, String title, String deptId, ArrayList<HashMap<String, Object>> workflowValues, String projectId, String memo);

}
