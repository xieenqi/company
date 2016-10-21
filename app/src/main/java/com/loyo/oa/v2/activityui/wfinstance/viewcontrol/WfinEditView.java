package com.loyo.oa.v2.activityui.wfinstance.viewcontrol;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.BaseView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yyy on 16/10/20.
 */

public interface WfinEditView extends BaseView{

    /*获取附件成功处理*/
    void getAttachmentsEmbl(ArrayList<Attachment> attachments);

    /*编辑请求成功处理*/
    void requestEditWfinEmbl(WfInstance wfInstance);

    /*编辑请求验证通过处理*/
    void requestEditVeriEmbl(ArrayList<HashMap<String, Object>> workflowValues);
}
