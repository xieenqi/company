package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.WfInstanceValuesInfoAdapter;
import com.loyo.oa.v2.adapter.WorkflowNodesListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.BizFormFields;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WfNodes;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.ListView_inScrollView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 审批详情
 */

@EActivity(R.layout.activity_wfinstance_info)
public class WfinstanceInfoActivity extends BaseActivity {

    @ViewById
    ListView listView_wfinstance;
    @ViewById
    ListView_inScrollView listView_workflowNodes;
    @ViewById
    TextView tv_lastowrk, tv_attachment_count, tv_wfnodes_title;
    @ViewById
    TextView tv_memo;
    @ViewById
    TextView tv_time_creator;
    @ViewById
    TextView tv_title_role;
    @ViewById
    TextView tv_title_creator;
    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    ViewGroup layout_nopass;
    @ViewById
    ViewGroup layout_pass;
    @ViewById
    ViewGroup layout_AttachFile;
    @ViewById
    ViewGroup layout_lastwork;
    @ViewById
    ViewGroup layout_memo;
    @ViewById
    ViewGroup layout_bottom, layout_wfinstance_content;
    @ViewById
    ImageView img_wfinstance_status;

    public final int MSG_DELETE_WFINSTANCE = 100;
    public final int MSG_ATTACHMENT = 200;

    public String userId;
    public WorkflowNodesListViewAdapter workflowNodesListViewAdapter;
    public WfInstanceValuesInfoAdapter wfInstanceValuesListViewAdapter;
    public ArrayList<HashMap<String, Object>> wfInstanceValuesDatas = new ArrayList<>();
    public ArrayList<WfNodes> lstData_WfNodes = new ArrayList<>();
    public ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

    @Extra("data")
    WfInstance wfInstance;


    @AfterViews
    void init() {
        super.setTitle("审批详情");
        initUI();
        requestData_wfinstance();
    }

    void initData_WorkflowValues() {
        if (wfInstance == null || wfInstance.getWorkflowValues() == null) {
            return;
        }
        wfInstanceValuesDatas.clear();
        for (int i = 0; i < wfInstance.getWorkflowValues().size(); i++) {
            wfInstanceValuesDatas.add(wfInstance.getWorkflowValues().get(i));
        }
    }

    @Override
    public void onBackPressed() {
        if (wfInstance != null && wfInstance.getWorkflowValues() != null && wfInstance.getWorkflowValues() != null) {
            wfInstance.setAck(true);
            wfInstance.getWorkflowValues().clear();
            Intent intent = new Intent();
            intent.putExtra("review", wfInstance);
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        } else {
            super.onBackPressed();
        }
    }

    void initUI() {
        userId = DBManager.Instance().getUser().id;
        getWindow().getDecorView().setOnTouchListener(new ViewUtil.OnTouchListener_softInput_hide());
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());

        layout_nopass.setOnTouchListener(touch);
        layout_pass.setOnTouchListener(touch);
        layout_AttachFile.setOnTouchListener(touch);
        img_title_left.setOnTouchListener(touch);
        img_title_right.setOnTouchListener(touch);
        img_title_right.setVisibility(View.GONE);
    }

    void initUI_listView_wfinstance() {
        ArrayList<BizFormFields> fields = new ArrayList<>();
        if (wfInstance != null && wfInstance.getBizform() != null && wfInstance.getBizform().getFields() != null) {
            fields = wfInstance.getBizform().getFields();
        }

        if (null != wfInstanceValuesDatas) {
            for (int j = 0; j < wfInstanceValuesDatas.size(); j++) {
                HashMap<String, Object> jsonObject = wfInstanceValuesDatas.get(j);
                for (int i = 0; i < fields.size(); i++) {
                    BizFormFields field = fields.get(i);
                    View view_value = LayoutInflater.from(this).inflate(R.layout.item_listview_wfinstancevalues_data, null, false);
                    EditText tv_value = (EditText) view_value.findViewById(R.id.et_value);
                    tv_value.setEnabled(false);
                    tv_value.setText(jsonObject.get(field.getId()) + "");

                    TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
                    tv_key.setText(field.getName());
                    layout_wfinstance_content.addView(view_value);
                }
            }
        }

        //        wfInstanceValuesListViewAdapter = new WfInstanceValuesInfoAdapter(this, wfInstanceValuesDatas, fields);
        //        listView_wfinstance.setAdapter(wfInstanceValuesListViewAdapter);
        //
        //        Global.setListViewHeightBasedOnChildren(listView_wfinstance);

        //显示删除
        if (wfInstance.getStatus() == WfInstance.STATUS_NEW && wfInstance.getCreator() != null && wfInstance.getCreator().isCurrentUser()) {
            img_title_right.setVisibility(View.VISIBLE);
        }
    }

    void initUI_listView_workflowNodes() {
        workflowNodesListViewAdapter = new WorkflowNodesListViewAdapter(wfInstance.getStatus(), lstData_WfNodes, LayoutInflater.from(this));
        listView_workflowNodes.setAdapter(workflowNodesListViewAdapter);
        Global.setListViewHeightBasedOnChildren(listView_workflowNodes);
    }

    void updateUI() {
        if (wfInstance == null) {
            return;
        }
        tv_time_creator.setText(wfInstance.getCreator().name + " " + app.df3.format(new Date(wfInstance.getCreatedAt() * 1000)) + " 提交");
        if (wfInstance.getCreator() != null) {
            tv_title_creator.setText(wfInstance.getTitle());

            if (null != wfInstance.getCreator().shortPosition) {
                tv_title_role.setText(wfInstance.getCreator().shortPosition.getName());
            }
        }

        if (!StringUtil.isEmpty(wfInstance.getMemo())) {
            layout_memo.setVisibility(View.VISIBLE);
            tv_memo.setText(wfInstance.getMemo());
        } else {
            layout_memo.setVisibility(View.GONE);
        }

        switch (wfInstance.getStatus()) {

            case 1:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status1);
                break;
            case 2:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status2);
                break;
            case 3:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status3);
                break;
            case 4:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status4);
                break;
            case 5:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status5);
                break;

        }
        initUI_listView_wfinstance();
        initUI_listView_workflowNodes();
        updateUI_layout_bottom();
    }

    private String getWfNodesTitle() {

        StringBuilder builder = new StringBuilder();
        if (null != wfInstance.getWorkflowNodes()) {
            int actives = 0;
            for (int i = wfInstance.getWorkflowNodes().size() - 1; i >= 0; i--) {
                WfNodes node = wfInstance.getWorkflowNodes().get(i);
                if (node.isActive()) {
                    actives++;
                }
            }
            builder.append("(" + actives + "/" + wfInstance.getWorkflowNodes().size() + ")");
        } else {
            builder.append("(0/0)");
        }

        return builder.toString();
    }

    /**
     * 底部同意／驳回 菜单设置
     */

    void updateUI_layout_bottom() {

        if (wfInstance == null) {
            return;
        }
        tv_wfnodes_title.setText(getWfNodesTitle());

        if (wfInstance.getStatus() == WfInstance.STATUS_ABORT || wfInstance.getStatus() == WfInstance.STATUS_FINISHED) {
            return;
        }

        ArrayList<WfNodes> nodes = wfInstance.getWorkflowNodes();
        if (nodes == null) {
            return;
        }

        for(int i = 0;i<nodes.size();i++){
            LogUtil.dll("id:"+nodes.get(i).getExecutorUser().getId());
            LogUtil.dll("name:"+nodes.get(i).getExecutorUser().getRealname());
        }



/*        WfNodes node = null;
        for (int i = nodes.size() - 1; i >= 0; i--) {
            //激活的节点,并且没有审批的就是当前节点
            if (nodes.get(i).isActive()) {
                node = nodes.get(i);
                break;
            }
        }*/


        WfNodes node = null;
        for (int i = 0;i<nodes.size(); i++) {
            if (nodes.get(i + 1).getExecutorUser().getId().equals(userId)) {
                node = nodes.get(i + 1);
                break;
            }
        }

        if (node != null) {

            LogUtil.dll("当前节点ID：" + node.getExecutorUser().getId());
            LogUtil.dll("本地登录ID：" + userId);

            if (node.isActive()) {
                if (node.isNeedApprove()) {
                    layout_nopass.setOnTouchListener(touch);
                    layout_pass.setOnTouchListener(touch);
                    layout_bottom.setVisibility(View.VISIBLE);
                    layout_lastwork.setVisibility(View.GONE);
                } else {
                    layout_nopass.setOnClickListener(null);
                    layout_nopass.setOnTouchListener(null);
                    layout_pass.setOnClickListener(null);
                    layout_pass.setOnTouchListener(null);
                    layout_bottom.setVisibility(View.GONE);
                    layout_lastwork.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    /**
     * 获取审批详情
     */
    void requestData_wfinstance() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfInstance(wfInstance.getId(), new RCallback<WfInstance>() {
            @Override
            public void success(WfInstance wfInstance_current, Response response) {
                wfInstance = wfInstance_current;
                if (wfInstance_current.getWorkflowNodes() != null) {
                    lstData_WfNodes.clear();
                    lstData_WfNodes.addAll(wfInstance_current.getWorkflowNodes());
                }
                initData_WorkflowValues();
                updateUI();


                try {
                    LogUtil.dll("返回的数据：" + Utils.convertStreamToString(response.getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LogUtil.dll("返回的解析数据：" + MainApp.gson.toJson(wfInstance_current));

            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                finish();
            }
        });
    }

    /**
     * 提交审批请求
     */
    void setData_wfinstance_approve(int type, String comment) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", comment);
        map.put("type", type);

        LogUtil.dll("手机端发送数据：" + MainApp.gson.toJson(map));
        LogUtil.dll("ID1：" + wfInstance.getId());

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).doWfInstance(wfInstance.getId(), map, new RCallback<WfInstance>() {
            @Override
            public void success(WfInstance wfInstance_current, Response response) {
                Toast("审批" + getString(R.string.app_succeed));
                //如果不clear,会提示java.io.NotSerializableException
                if (null != wfInstance_current.getWorkflowValues()) {
                    wfInstance_current.getWorkflowValues().clear();
                }
                wfInstance_current.setAck(true);
                Intent intent = getIntent();
                intent.putExtra("review", wfInstance_current);
                app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                LogUtil.dll("url:" + error.getUrl());
                try {
                    LogUtil.dll("error result:" + Utils.convertStreamToString(error.getResponse().getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_nopass, R.id.layout_pass, R.id.layout_lastwork, R.id.layout_AttachFile})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);
                intent.putExtra("delete", true);
                startActivityForResult(intent, MSG_DELETE_WFINSTANCE);
                break;
            case R.id.layout_nopass:
                showApproveDialog(2);
                LogUtil.dll("点击不同意");
                break;
            case R.id.layout_pass:
                showApproveDialog(1);
                LogUtil.dll("点击同意");
                break;
            case R.id.layout_lastwork:
                showApproveDialog(1);
                break;
            case R.id.layout_AttachFile:
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", wfInstance.getAttachments());
                bundle.putSerializable("uuid", wfInstance.getAttachmentUUId());
                app.startActivityForResult(this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);
                break;
        }
    }

    /**
     * @param type 显示审批对话框
     */
    private void showApproveDialog(final int type) {


        View container = LayoutInflater.from(mContext).inflate(R.layout.dialog_wfinstance_approve, null, false);
        container.getBackground().setAlpha(70);
        TextView tv_confirm = (TextView) container.findViewById(R.id.tv_confirm);
        TextView tv_cancel = (TextView) container.findViewById(R.id.tv_cancel);
        final EditText et_comment = (EditText) container.findViewById(R.id.et_comment);
        tv_confirm.setOnTouchListener(Global.GetTouch());
        tv_cancel.setOnTouchListener(Global.GetTouch());

        if (type == 1) {
            et_comment.setHint("请输入评语");
        } else if (type == 2) {
            et_comment.setHint("请输入驳回原因");
        }

        final PopupWindow popupWindow = new PopupWindow(container, -1, -1, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources()));// 响应键盘三个主键的必须步骤
        popupWindow.showAtLocation(findViewById(R.id.tv_title_1), Gravity.BOTTOM, 0, 0);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = et_comment.getText().toString().trim();
                if (TextUtils.isEmpty(comment) && type == 2) {
                    Toast("请输入驳回原因");
                    return;
                }
                popupWindow.dismiss();
                setData_wfinstance_approve(type, comment);
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case MSG_DELETE_WFINSTANCE:
                RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).deleteWfinstance(wfInstance.getId(), new RCallback<WfInstance>() {
                    @Override
                    public void success(WfInstance wfInstance, Response response) {
                        if (null != wfInstance.getWorkflowValues()) {
                            wfInstance.getWorkflowValues().clear();
                        }
                        Intent intent = new Intent();
                        intent.putExtra("delete", wfInstance);
                        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast("删除失败");
                        super.failure(error);
                    }
                });
                break;
            case MSG_ATTACHMENT:
                if (data == null || data.getExtras() == null) {
                    return;
                }
                ArrayList<Attachment> attachments = (ArrayList<Attachment>) data.getSerializableExtra("data");
                wfInstance.setAttachments(attachments);
                if (null != attachments) {
                    tv_attachment_count.setText("附件 " + "(" + attachments.size() + ")");
                }
                break;
        }
    }
}
