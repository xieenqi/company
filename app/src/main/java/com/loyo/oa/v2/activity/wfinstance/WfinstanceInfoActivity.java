package com.loyo.oa.v2.activity.wfinstance;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.SelectEditDeleteActivity;
import com.loyo.oa.v2.activity.attachment.AttachmentActivity_;
import com.loyo.oa.v2.adapter.WorkflowNodesListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.BizFormFields;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WfNodes;
import com.loyo.oa.v2.common.ExtraAndResult;
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
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.ListView_inScrollView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【审批详情】
 */

@EActivity(R.layout.activity_wfinstance_info)
public class WfinstanceInfoActivity extends BaseActivity {

    @ViewById
    ListView_inScrollView listView_workflowNodes;
    @ViewById
    TextView tv_lastowrk, tv_attachment_count, tv_wfnodes_title;
    @ViewById
    TextView tv_memo;
    @ViewById
    TextView tv_projectName;
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
    LinearLayout ll_project;
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

    public boolean isOver = false;
    public String userId;
    public WorkflowNodesListViewAdapter workflowNodesListViewAdapter;
    public ArrayList<HashMap<String, Object>> wfInstanceValuesDatas = new ArrayList<>();
    public ArrayList<WfNodes> lstData_WfNodes = new ArrayList<>();
    public ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();
    public WfInstance wfInstance;

    @Extra(ExtraAndResult.EXTRA_ID)
    String wfInstanceId;

    @AfterViews
    void init() {
        super.setTitle("审批详情");
        initUI();
        getWfinstanceData();
    }

    void initData_WorkflowValues() {
        if (null == wfInstance || null == wfInstance.workflowValues) {
            return;
        }
        wfInstanceValuesDatas.clear();
        for (int i = 0; i < wfInstance.workflowValues.size(); i++) {
            wfInstanceValuesDatas.add(wfInstance.workflowValues.get(i));
        }
    }

    @Override
    public void onBackPressed() {
        if (null != wfInstance && null != wfInstance.workflowValues && null != wfInstance.workflowValues) {
            wfInstance.setViewed(true);
            wfInstance.workflowValues.clear();
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


    /**
     * 审批内容数据设置
     */
    void initUI_listView_wfinstance() {

        ArrayList<BizFormFields> fields = new ArrayList<>();
        if (wfInstance != null && wfInstance.bizForm != null && wfInstance.bizForm.getFields() != null) {
            fields = wfInstance.bizForm.getFields();
        }

        if (null != wfInstanceValuesDatas) {
            for (int j = 0; j < wfInstanceValuesDatas.size(); j++) {
                HashMap<String, Object> jsonObject = wfInstanceValuesDatas.get(j);
                for (int i = 0; i < fields.size(); i++) {
                    if (!fields.get(i).isEnable()) {
                        continue;
                    }
                    BizFormFields field = fields.get(i);
                    View view_value = LayoutInflater.from(this).inflate(R.layout.item_listview_wfinstancevalues_data, null, false);
                    EditText tv_value = (EditText) view_value.findViewById(R.id.et_value);
                    tv_value.setEnabled(false);
                    tv_value.setText(wfinstanceInfoValue(jsonObject.get(field.getId())));
                    TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
                    tv_key.setText(field.getName() + ": ");
                    layout_wfinstance_content.addView(view_value);
                }
            }
        }

        //显示删除
        if (wfInstance.status == WfInstance.STATUS_NEW && wfInstance.creator != null
                && wfInstance.creator.isCurrentUser() && !("300".equals(wfInstance.bizForm.bizCode + ""))) {
            img_title_right.setVisibility(View.VISIBLE);
        }

    }

    private String wfinstanceInfoValue(Object obj) {
        if (null == obj) {
            return "没有内容";
        }

        if (obj.getClass().toString().contains("Double")) {
            BigDecimal bigDecimal = new BigDecimal(obj + "");
            return bigDecimal.doubleValue() + "";
        } else {
            return obj + "";
        }
    }

    void initUI_listView_workflowNodes() {
        workflowNodesListViewAdapter = new WorkflowNodesListViewAdapter(wfInstance.status, lstData_WfNodes, LayoutInflater.from(this), wfInstance.serverTime);
        listView_workflowNodes.setAdapter(workflowNodesListViewAdapter);
        Global.setListViewHeightBasedOnChildren(listView_workflowNodes);
    }

    void updateUI() {
        if (wfInstance == null) {
            return;
        }
        try {
            tv_time_creator.setText(wfInstance.creator.name + " " + app.df3.format(new Date(wfInstance.createdAt * 1000)) + " 提交");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (wfInstance.creator != null) {
            tv_title_creator.setText(wfInstance.title);

            if (null != wfInstance.creator.shortPosition) {
                tv_title_role.setText(wfInstance.creator.shortPosition.getName());
            }
        }

        if (!StringUtil.isEmpty(wfInstance.memo)) {
            layout_memo.setVisibility(View.VISIBLE);
            tv_memo.setText(wfInstance.memo);
        } else {
            if (null != wfInstance.demand && 300 == wfInstance.bizForm.bizCode && !TextUtils.isEmpty(wfInstance.demand.memo)) {
                layout_memo.setVisibility(View.VISIBLE);
                tv_memo.setText(wfInstance.demand.memo);
            } else {
                layout_memo.setVisibility(View.GONE);
            }
        }
        tv_attachment_count.setText("附件 (" + wfInstance.bizExtData.getAttachmentCount() + ")");
        tv_projectName.setText(null == wfInstance.ProjectInfo ? "无" : wfInstance.ProjectInfo.title);
        if (300 == wfInstance.bizForm.bizCode) {//赢单审批隐藏项目 和 附件
            layout_AttachFile.setVisibility(View.GONE);
            ll_project.setVisibility(View.GONE);
        }
        switch (wfInstance.status) {

            case WfInstance.STATUS_NEW:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status1);
                break;
            case WfInstance.STATUS_PROCESSING:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status2);
                break;
            case WfInstance.STATUS_ABORT:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status3);
                break;
            case WfInstance.STATUS_APPROVED:
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status4);
                break;
            case WfInstance.STATUS_FINISHED:
                //img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status5);
                img_wfinstance_status.setImageResource(R.drawable.img_wfinstance_status4); //状态4，5都归类为 已通过
                break;
            default:
                break;
        }
        initUI_listView_wfinstance();
        initUI_listView_workflowNodes();
        updateUI_layout_bottom();
    }

    private String getWfNodesTitle() {

        StringBuilder builder = new StringBuilder();
        if (null != wfInstance.workflowNodes) {
            int actives = 0;
            for (int i = wfInstance.workflowNodes.size() - 1; i >= 0; i--) {
                WfNodes node = wfInstance.workflowNodes.get(i);
                if (node.isActive()) {
                    actives++;
                }
            }
            builder.append("(" + actives + "/" + wfInstance.workflowNodes.size() + ")");
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

        if (wfInstance.status == WfInstance.STATUS_ABORT || wfInstance.status == WfInstance.STATUS_FINISHED) {
            return;
        }

        ArrayList<WfNodes> nodes = wfInstance.workflowNodes;
        if (nodes == null) {
            return;
        }

        WfNodes node = null;
        for (int i = 0; i < nodes.size(); i++) {
            if (null != nodes.get(i).getExecutorUser() && nodes.get(i).getExecutorUser().getId().equals(userId)) {
                node = nodes.get(i);
                break;
            }
        }

        if (node != null) {
            if (node.getActive() == 2) {
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
    void getWfinstanceData() {
        if (TextUtils.isEmpty(wfInstanceId)) {
            Toast("参数不完整！");
            finish();
            return;
        }
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfInstance(wfInstanceId, new RCallback<WfInstance>() {
            @Override
            public void success(final WfInstance wfInstance_current, final Response response) {
                HttpErrorCheck.checkResponse("审批详情返回的数据：", response);
                wfInstance = wfInstance_current;
                if (null != wfInstance_current && null != wfInstance_current.workflowNodes) {
                    lstData_WfNodes.clear();
                    lstData_WfNodes.addAll(wfInstance_current.workflowNodes);
                }
                /**
                 * 赢单审批
                 */
                if (300 == wfInstance_current.bizForm.bizCode) {
                    wfData(wfInstance_current);
                } else {
                    initData_WorkflowValues();
                }
                updateUI();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                finish();
            }
        });
    }

    /**
     * 赢单审批 信息
     *
     * @param wfData
     */
    private void wfData(final WfInstance wfData) {
        if (null == wfData.demand) {
            return;
        }
        List<String> wfList = new ArrayList<>();
        String custerName = wfData.demand.customerName;
        wfList.add("客户名称：" + (TextUtils.isEmpty(custerName) ? "" : custerName));
        wfList.add("产品名称：" + wfData.demand.productName);
        wfList.add("预估：数量 " + wfData.demand.estimatedNum + "   单价：" + wfData.demand.estimatedPrice + " " + wfData.demand.unit);
        wfList.add("预估：数量 " + wfData.demand.actualNum + "   单价：" + wfData.demand.actualPrice + " " + wfData.demand.unit);
        for (String text : wfList) {
            View view_value = LayoutInflater.from(this).inflate(R.layout.item_wf_data, null, false);
            TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
            tv_key.setText(text);
            layout_wfinstance_content.addView(view_value);
        }
    }

    /**
     * 提交审批请求
     */
    void setData_wfinstance_approve(final int type, final String comment) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", comment);
        map.put("type", type);
        LogUtil.dll("请求内容:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).doWfInstance(wfInstance.getId(), map, new RCallback<WfInstance>() {
            @Override
            public void success(final WfInstance wfInstance_current, final Response response) {
                HttpErrorCheck.checkResponse("审批成功：", response);
                if (null != wfInstance_current) {
                    Toast("审批" + getString(R.string.app_succeed));
                    //如果不clear,会提示java.io.NotSerializableException
                    if (null != wfInstance_current.workflowValues) {
                        wfInstance_current.workflowValues.clear();
                    }
                    wfInstance_current.setViewed(true);
                    Intent intent = getIntent();
                    intent.putExtra("review", wfInstance_current);
                    app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                } else {
                    Toast("服务器错误");
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_nopass, R.id.layout_pass, R.id.layout_lastwork, R.id.layout_AttachFile})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);
                intent.putExtra("delete", true);
                startActivityForResult(intent, MSG_DELETE_WFINSTANCE);
                break;
            /*同意*/
            case R.id.layout_nopass:
                showApproveDialog(2);
                break;
            /*驳回*/
            case R.id.layout_pass:
                showApproveDialog(1);
                break;
            case R.id.layout_lastwork:
                showApproveDialog(1);
                break;
            /*附件上传*/
            case R.id.layout_AttachFile:

                if (wfInstance.status == 2 || wfInstance.status == 4 || wfInstance.status == 5) {
                    isOver = true;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", wfInstance.attachments);
                bundle.putSerializable("uuid", wfInstance.attachmentUUId);
                bundle.putBoolean("isOver", isOver);
                bundle.putInt("bizType", 12);
                app.startActivityForResult(this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);
                break;
            default:
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
            public void onClick(final View view) {
                popupWindow.dismiss();
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String comment = et_comment.getText().toString().trim();
                if (comment.isEmpty() && type == 2) {
                    Toast("请输入驳回原因");
                    return;
                } else if (comment.isEmpty() && type == 1) {
                    comment = "同意";
                    return;
                }
                popupWindow.dismiss();
                setData_wfinstance_approve(type, comment);
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                popupWindow.dismiss();
            }
        });
    }


    /**
     * 判断是否是创建人
     */
    public boolean isCreater() {
        return wfInstance.creator.getId().equals(MainApp.user.getId()) ? true : false;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case MSG_DELETE_WFINSTANCE:
                RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).deleteWfinstance(wfInstance.getId(), new RCallback<WfInstance>() {
                    @Override
                    public void success(final WfInstance wfInstance, final Response response) {
                        if (null != wfInstance.workflowValues) {
                            wfInstance.workflowValues.clear();
                        }
                        Intent intent = new Intent();
                        intent.putExtra("delete", wfInstance);
                        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
                    }

                    @Override
                    public void failure(final RetrofitError error) {
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
                wfInstance.attachments = attachments;
                if (null != attachments) {
                    tv_attachment_count.setText("附件 " + "(" + attachments.size() + ")");
                }
                break;

            default:
                break;
        }
    }
}
