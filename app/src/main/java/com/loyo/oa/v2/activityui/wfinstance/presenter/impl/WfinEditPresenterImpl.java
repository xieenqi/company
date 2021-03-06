package com.loyo.oa.v2.activityui.wfinstance.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.wfinstance.api.WfinstanceService;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizFormFields;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfInstanceAdd;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinEditPresenter;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinEditView;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.customview.WfinAddViewGroup;
import com.loyo.oa.v2.customview.WfinEditViewGroup;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.loyo.oa.v2.common.Global.Toast;

/**
 * Created by yyy on 16/10/20.
 */

public class WfinEditPresenterImpl implements WfinEditPresenter{

    private int bizType = 12;
    private int uploadSize;
    private WfinEditView crolView;
    private Context mContext;
    private Activity mActivity;
    private BizForm mBizForm;
    private ArrayList<String> startTimeArr = new ArrayList<>();
    private ArrayList<String> endTimeArr = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<HashMap<String, Object>>();
    private ArrayList<Boolean> isRequiredList = new ArrayList<>();
    private ArrayList postValue = new ArrayList<>();
    private WfInstanceAdd wfInstanceAdd = new WfInstanceAdd();


    public WfinEditPresenterImpl(Context mContext,WfinEditView crolView,Activity mActivity){
        this.mContext = mContext;
        this.crolView = crolView;
        this.mActivity = mActivity;
    }

    /**
     * 获取附件
     * */
    @Override
    public void getAttachments(String uuid) {
        AttachmentService.getAttachments(uuid)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>() {
                    @Override
                    public void onNext(ArrayList<Attachment> attachments) {
                        crolView.getAttachmentsEmbl(attachments);
                    }
                });
    }

    /**
     * 设置开始结束时间规范
     * */
    @Override
    public void setStartendTime(ArrayList<HashMap<String, Object>> wfInstanceValuesDatas,BizForm mBizForm,LinearLayout layout_wfinstance_data, LinearLayout wfinstance_data_container) {
        this.mBizForm = mBizForm;
        for (int i = 0; i < mBizForm.getFields().size(); i++) {
            if (mBizForm.getFields().get(i).getName().equals("开始时间") && mBizForm.getFields().get(i).isSystem()) {
                startTimeArr.add(mBizForm.getFields().get(i).getId());
            }
            if (mBizForm.getFields().get(i).getName().equals("结束时间") && mBizForm.getFields().get(i).isSystem()) {
                endTimeArr.add(mBizForm.getFields().get(i).getId());
            }
        }
        layout_wfinstance_data.setVisibility(View.VISIBLE);
        submitData.clear();
        wfinstance_data_container.removeAllViews();
        //审批内容 有多少组就执行多少次动态view
        for (int i = 0; i < wfInstanceValuesDatas.size(); i++) {
            editTypeData(i,wfinstance_data_container,wfInstanceValuesDatas);
        }
    }

    /**
     * 编辑加审批内容 栏目
     */
    void editTypeData(int position,LinearLayout wfinstance_data_container,ArrayList<HashMap<String, Object>> wfInstanceValuesDatas) {
        if (mBizForm == null) {
            Toast("请选择类型");
            return;
        }
        if (null == mBizForm.getFields()) {
            LogUtil.dee("mBizForm return");
            return;
        }

        HashMap<String, Object> newValues = new HashMap<String, Object>();
        for (BizFormFields field : mBizForm.getFields()) {
            newValues.put(field.getId(), wfInstanceValuesDatas.get(position).get(field.getId()));
        }

        LogUtil.dee("newValues size:"+newValues.size());
        submitData.add(newValues);
        WfinEditViewGroup viewGroup = new WfinEditViewGroup(mContext, mBizForm.getFields(), submitData, wfInstanceValuesDatas, position);
        viewGroup.bindView(submitData.size() > 0 ? submitData.size() - 1 : submitData.size(), wfinstance_data_container);
        addIsRequired();
    }

    /**
     * 新建审批内容栏目
     * */
    @Override
    public void addTypeData(LinearLayout wfinstance_data_container) {
        if (mBizForm == null) {
            Toast("请选择类型");
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
        addIsRequired();
    }

    /**
     * 编辑请求内容验证
     * */
    @Override
    public void requestEditWfinVeri(String deptId) {

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
            if (null != postValue.get(i) && TextUtils.isEmpty(postValue.get(i).toString()) && isRequiredList.get(i) == true) {
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

                if (startTimelong > endTimelong ) {
                    crolView.showMsg("开始时间不能大于结束时间!");
                    return;
                }
            }
        }
        crolView.requestEditVeriEmbl(workflowValues);
    }

    /**
     * 编辑请求
     * */
    @Override
    public void requestEditWfin(String id,String title,String deptId,
                                ArrayList<HashMap<String, Object>> workflowValues,
                                String projectId,String memo,
                                String customerId, String customerName) {
        LoyoProgressHUD hud = crolView.showStatusProgress();
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);                               //自定义标题
        map.put("deptId", deptId);                             //部门 id
        map.put("workflowValues", workflowValues);             //流程 内容
        map.put("projectId", projectId);                       //项目Id
        map.put("memo",memo); //备注
        if (!TextUtils.isEmpty(customerId)) {
            map.put("customerId", customerId);
            if (!TextUtils.isEmpty(customerName)) {
                map.put("customerName", customerName);
            }
        }

        WfinstanceService.editWfInstance(id,map)
                .subscribe(new DefaultLoyoSubscriber<WfInstance>(hud, "编辑审批成功") {
            @Override
            public void onNext(final WfInstance wfInstance) {
                crolView.requestEditWfinEmbl(wfInstance);
            }
        });
    }


    /*内容是否必填，加入list*/
    void addIsRequired() {
        for (int i = 0; i < mBizForm.getFields().size(); i++) {
            isRequiredList.add(mBizForm.getFields().get(i).isRequired());
        }
    }
}
