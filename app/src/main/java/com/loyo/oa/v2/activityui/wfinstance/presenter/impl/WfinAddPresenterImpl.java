package com.loyo.oa.v2.activityui.wfinstance.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizFormFields;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfInstanceAdd;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinAddPresenter;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinAddView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PostBizExtData;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.WfinAddViewGroup;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * 【新建审批】Presenter
 * Created by yyy on 16/10/18.
 */

public class WfinAddPresenterImpl implements WfinAddPresenter{

    private int uploadSize;
    private int uploadNum;

    private Activity mActivity;
    private Context mContext;
    private WfinAddView crolView;

    private BizForm mBizForm;
    private ArrayList postValue = new ArrayList<>();
    private WfInstanceAdd wfInstanceAdd = new WfInstanceAdd();
    private ArrayList<Boolean> isRequiredList = new ArrayList<>();
    private ArrayList<String> startTimeArr = new ArrayList<>();
    private ArrayList<String> endTimeArr = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<HashMap<String, Object>>();
    private PostBizExtData bizExtData;
    private List<WfinAddViewGroup> WfinObj = new ArrayList<WfinAddViewGroup>();


    public WfinAddPresenterImpl(Activity mActivity,Context mContext,WfinAddView crolView,BizForm mBizForm){
        this.mContext = mContext;
        this.crolView = crolView;
        this.mBizForm = mBizForm;
        this.mActivity = mActivity;
    }

    /**
     * 新建审批验证
     * */
    @Override
    public void addWfinVeri(String deptId,ArrayList<ImageInfo> pickPhots) {
        if (submitData.isEmpty()) {
            crolView.showMsg("请输入审批内容");
            return;
        } else if (TextUtils.isEmpty(deptId)) {
            crolView.showMsg("请输选择部门");
            return;
        }
        /**审批内容，装进Post数据的list中*/
        postValue.clear();
        ArrayList<HashMap<String, Object>> workflowValues = new ArrayList<>();
        wfInstanceAdd.getWorkflowValuesAdd().wfInstanceValuesDatas.clear();
        for (int k = 0; k < submitData.size(); k++) {
            HashMap<String, Object> jsonMapValues = new HashMap<>();
            HashMap<String, Object> map_Values = submitData.get(k);
            try {
                for (BizFormFields field : mBizForm.getFields()) {
                    for (String key : map_Values.keySet()) {
                        if (!TextUtils.equals(field.getId(), key)) {
                            continue;
                        }
                        postValue.add(map_Values.get(key));
                        jsonMapValues.put(key, map_Values.get(key));
                    }
                }
                workflowValues.add(jsonMapValues);
            } catch (NullPointerException e) {
                e.printStackTrace();
                crolView.showMsg("操作失败!");
                return;
            }
        }

        for (int i = 0; i < postValue.size(); i++) {
            if (TextUtils.isEmpty(postValue.get(i).toString()) && isRequiredList.get(i) == true) {
                crolView.showMsg("请填写\"必填项\"");
                return;
            }
        }

        /**
         * 获取请假/出差系统字段的 开始结束时间
         * */

        String startTimeDate = "";
        String endTimeDate = "";

        long startTimelong;
        long endTimelong;

        for (int i = 0; i < startTimeArr.size(); i++) {
            for (HashMap<String, Object> map : workflowValues) {
                Set set = map.entrySet();
                Iterator it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    if (startTimeArr.get(i).equals(me.getKey())) {
                        startTimeDate = (String) map.get(startTimeArr.get(i));
                    }

                    if (endTimeArr.get(i).equals(me.getKey())) {
                        endTimeDate = (String) map.get(endTimeArr.get(i));
                    }
                }
//                startTimelong = Long.valueOf(DateTool.getDataOne(startTimeDate, DateTool.DATE_FORMATE_AT_MINUTES));
//                endTimelong = Long.valueOf(DateTool.getDataOne(endTimeDate, DateTool.DATE_FORMATE_AT_MINUTES));

                startTimelong= com.loyo.oa.common.utils.DateTool.getMinuteStamp(startTimeDate);
                endTimelong= com.loyo.oa.common.utils.DateTool.getMinuteStamp(endTimeDate);


                if (startTimelong > endTimelong) {
                    crolView.showMsg("开始时间不能大于结束时间");
                    return;
                }
            }
        }
        LogUtil.dee("5");
        if (pickPhots.size() == 0) {
            crolView.showProgress("");
        }
        crolView.requestAddWfinVeriSuccess(workflowValues);
    }

    /**
     * 新建审批请求
     * */
    @Override
    public void requestAddWfin(String title,String deptId,ArrayList<HashMap<String, Object>> workflowValues,String mTemplateId,String projectId,String uuid,String memo,ArrayList<ImageInfo> pickPhots) {
        bizExtData = new PostBizExtData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizformId", mBizForm.getId());              //表单Id
        map.put("title", title);                             //自定义标题
        map.put("deptId", deptId);                           //部门 id
        map.put("workflowValues", workflowValues);           //流程 内容
        map.put("wftemplateId", mTemplateId);                //流程模板Id
        map.put("projectId", projectId);                     //项目Id
        map.put("bizCode", mBizForm.getBizCode());           //流程类型
        if (uuid != null && pickPhots.size() > 0) {
            bizExtData.setAttachmentCount(pickPhots.size());
            map.put("attachmentUUId", uuid);
            map.put("bizExtData", bizExtData);
        }
        map.put("memo", memo); //备注
        LogUtil.d("创建审批传参：" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).addWfInstance(map, new RCallback<WfInstance>() {
            @Override
            public void success(final WfInstance wfInstance, final Response response) {
                HttpErrorCheck.checkResponse("新建审批cg", response);
                if (wfInstance != null) {
                    crolView.requestAddWfinSuccessEmbl(wfInstance);
                } else {
                    crolView.showMsg("服务器错误");
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 设置时间格式规范
     * 审批开始 结束时间规范判断:
     * 审批开始时间不能小于结束时间，
     * 从审批内容里获取到 开始时间 结束时间 的id
     * 再根据这个id去获取 开始结束 时间的值
     */
    @Override
    public void setStartendTime() {
        for (int i = 0; i < mBizForm.getFields().size(); i++) {
            if (mBizForm.getFields().get(i).getName().equals("开始时间") && mBizForm.getFields().get(i).isSystem()) {
                startTimeArr.add(mBizForm.getFields().get(i).getId());
            }
            if (mBizForm.getFields().get(i).getName().equals("结束时间") && mBizForm.getFields().get(i).isSystem()) {
                endTimeArr.add(mBizForm.getFields().get(i).getId());
            }
        }
        crolView.setStartendTimeEmbl();
    }

    @Override
    public void addTypeData(LinearLayout wfinstance_data_container) {
        if (mBizForm == null) {
            crolView.showMsg("请选择类型");
            return;
        }
        if (null == mBizForm.getFields()) {
            return;
        }

        HashMap<String, Object> newValues = new HashMap<String, Object>();
        for (BizFormFields field : mBizForm.getFields()) {
            newValues.put(field.getId(), "");
        }
        submitData.add(newValues);
        WfinAddViewGroup viewGroup = new WfinAddViewGroup(mContext, mBizForm.getFields(), submitData);
        viewGroup.bindView(submitData.size() > 0 ? submitData.size() - 1 : submitData.size(), wfinstance_data_container);
        WfinObj.add(viewGroup);//新增一个内容 就存起来
        for (int i = 0; i < mBizForm.getFields().size(); i++) {
            isRequiredList.add(mBizForm.getFields().get(i).isRequired());
        }
    }

    /**
     * 上传附件
     * */
    @Override
    public void newUploadAttachement(String uuid, int bizType, final ArrayList<ImageInfo> pickPhots) {
        crolView.showProgress("");
        try {
            uploadSize = 0;
            uploadNum = pickPhots.size();
            for (ImageInfo item : pickPhots) {
                Uri uri = Uri.parse(item.path);
                File newFile = Global.scal(mActivity, uri);
                if (newFile != null && newFile.length() > 0) {
                    if (newFile.exists()) {
                        TypedFile typedFile = new TypedFile("image/*", newFile);
                        TypedString typedUuid = new TypedString(uuid);
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).newUpload(typedUuid, bizType, typedFile,
                                new RCallback<Attachment>() {
                                    @Override
                                    public void success(final Attachment attachments, final Response response) {
                                        uploadSize++;
                                        if (uploadSize == uploadNum) {
                                            crolView.uploadSuccessEmbl(pickPhots);
                                        }
                                    }

                                    @Override
                                    public void failure(final RetrofitError error) {
                                        super.failure(error);
                                        HttpErrorCheck.checkError(error);
                                    }
                                });
                    }
                }
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }
}
