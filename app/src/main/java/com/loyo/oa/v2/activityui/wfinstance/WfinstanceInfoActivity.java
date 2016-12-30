package com.loyo.oa.v2.activityui.wfinstance;

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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.AttachmentActivity_;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.order.OrderEstimateListActivity;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.activityui.other.SelectEditDeleteActivity;
import com.loyo.oa.v2.activityui.product.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.SaleDetailsActivity;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.wfinstance.api.WfinstanceService;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizFormFields;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfNodes;
import com.loyo.oa.v2.activityui.work.adapter.WorkflowNodesListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.ListView_inScrollView;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【审批详情】
 */

@EActivity(R.layout.activity_wfinstance_info_new)
public class WfinstanceInfoActivity extends BaseActivity {

    @ViewById
    ScrollView scrollView;
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
    TextView tv_status;
    @ViewById
    LinearLayout ll_sale;
    @ViewById
    TextView tv_sale;
    //订单审批
    @ViewById
    LinearLayout ll_order_layout, ll_order_content, ll_product;
    @ViewById
    TextView tv_product, tv_plan_value;
    //回款审批
    @ViewById
    LinearLayout ll_payment_layout, ll_payment_content;
    @ViewById
    LoadingLayout ll_loading;

    @Extra(ExtraAndResult.IS_UPDATE)
    boolean isUpdate;//是否需要刷新列表
    public final int MSG_DELETE_WFINSTANCE = 100;
    public final int MSG_ATTACHMENT = 200;

    public boolean isPass = false;
    public boolean isOver = false;
    public String userId, saleId;

    public Bundle mBundle;
    public WorkflowNodesListViewAdapter workflowNodesListViewAdapter;
    public ArrayList<HashMap<String, Object>> wfInstanceValuesDatas = new ArrayList<>();
    public ArrayList<WfNodes> lstData_WfNodes = new ArrayList<>();
    public ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();
    public WfInstance mWfInstance;
    private String AttachmentUUId;
    private int AttachmentCount;

    @Extra(ExtraAndResult.EXTRA_ID)
    String wfInstanceId;


    @AfterViews
    void init() {
        super.setTitle("审批详情");
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getWfinstanceData();
            }
        });
        initUI();
        getWfinstanceData();
    }

    void initUI() {
        try {
            userId = DBManager.Instance().getUser().id;
        } catch (Exception e) {
            e.printStackTrace();
            Toast("人员信息不全");
            finish();
        }

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
     * 获取审批详情
     */
    void getWfinstanceData() {
        if (TextUtils.isEmpty(wfInstanceId)) {
            Toast("参数不完整！");
            finish();
            return;
        }
//        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfInstance(wfInstanceId, new RCallback<WfInstance>() {
//            @Override
//            public void success(final WfInstance wfInstance_current, final Response response) {
//                HttpErrorCheck.checkResponse("审批详情返回的数据：", response);
//                mWfInstance = wfInstance_current;
//                Log.i("MSG_ATTACHMENT","date"+mWfInstance.attachments);
//                if (null != wfInstance_current && null != wfInstance_current.workflowNodes) {
//                    lstData_WfNodes.clear();
//                    lstData_WfNodes.addAll(wfInstance_current.workflowNodes);
//                }
//                /**
//                 * 赢单审批
//                 */
//                if (wfInstance_current==null || wfInstance_current.bizForm == null)
//                {
//                    return;
//                }
//
//                if (300 == wfInstance_current.bizForm.bizCode) {
//                    wfData(wfInstance_current);
//                } else if (400 == wfInstance_current.bizForm.bizCode) {//订单审批
//                    orderData();
//                } else if (500 == wfInstance_current.bizForm.bizCode) {//回款审批
//                    paymentData();
//                } else {
//                    initData_WorkflowValues();
//                }
//                updateUI();
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error,ll_loading);
////                finish();
//            }
//        });

        WfinstanceService.getWfInstance(wfInstanceId).subscribe(new DefaultLoyoSubscriber<WfInstance>(LoyoErrorChecker.LOADING_LAYOUT) {
            @Override
            public void onNext(WfInstance wfInstance_current) {
                mWfInstance = wfInstance_current;
                Log.i("MSG_ATTACHMENT","date"+mWfInstance.attachments);
                if (null != wfInstance_current && null != wfInstance_current.workflowNodes) {
                    lstData_WfNodes.clear();
                    lstData_WfNodes.addAll(wfInstance_current.workflowNodes);
                }
                /**
                 * 赢单审批
                 */
                if (wfInstance_current==null || wfInstance_current.bizForm == null)
                {
                    return;
                }

                if (300 == wfInstance_current.bizForm.bizCode) {
                    wfData(wfInstance_current);
                } else if (400 == wfInstance_current.bizForm.bizCode) {//订单审批
                    orderData();
                } else if (500 == wfInstance_current.bizForm.bizCode) {//回款审批
                    paymentData();
                } else {
                    initData_WorkflowValues();
                }
                updateUI();
            }
        });
    }

    /**
     * 赢单审批 信息
     *
     * @param wfData
     */
    private void wfData(WfInstance wfData) {
//        if (null == wfData.demand) {
//            return;
//        }
//        List<String> wfList = new ArrayList<>();
//        String custerName = wfData.demand.customerName;
//        wfList.add("客户名称：" + (TextUtils.isEmpty(custerName) ? "" : custerName));
//        wfList.add("产品名称：" + wfData.demand.productName);
//        wfList.add("预估：数量 " + wfData.demand.estimatedNum + "   单价：" + wfData.demand.estimatedPrice + " " + wfData.demand.unit);
//        wfList.add("预估：数量 " + wfData.demand.actualNum + "   单价：" + wfData.demand.actualPrice + " " + wfData.demand.unit);
//        for (String text : wfList) {
//            View view_value = LayoutInflater.from(this).inflate(R.layout.item_wf_data, null, false);
//            TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
//            tv_key.setText(text);
//            layout_wfinstance_content.addView(view_value);
//        }
        if (null == wfData.chance) {
            return;
        }
        SaleDetails chanceData = wfData.chance;
        saleId = chanceData.id;
        ll_sale.setVisibility(View.VISIBLE);
        layout_wfinstance_content.setVisibility(View.GONE);
        ll_sale.setOnTouchListener(Global.GetTouch());
        tv_sale.setText(chanceData.name);
        List<String> wfList = new ArrayList<>();
        wfList.add("意向产品：" + getProductName(chanceData.proInfos));
        wfList.add("销售总金额：" + getSaleAmount(chanceData.proInfos));
        wfList.add("总折扣：" + getProductDiscount(chanceData.proInfos) + "%");
        wfList.add("对应客户：" + chanceData.cusName);
        if (!TextUtils.isEmpty(chanceData.chanceType)) {
            wfList.add("机会类型：" + chanceData.chanceType);
        }
        if (!TextUtils.isEmpty(chanceData.chanceSource)) {
            wfList.add("机会来源：" + chanceData.chanceSource);
        }
        if (null != chanceData.extensionDatas) {
            wfList.addAll(getProductCustomData(chanceData.extensionDatas));
        }
        if (!TextUtils.isEmpty(chanceData.memo)) {
            wfList.add("备注：" + chanceData.memo);
        }
//        wfList.add("创建时间：" + app.df3.format(Long.valueOf(chanceData.createdAt + "") * 1000));
        wfList.add("创建时间：" + DateTool.getDateTimeFriendly(chanceData.createdAt));

        for (String text : wfList) {
            View view_value = LayoutInflater.from(this).inflate(R.layout.item_wf_data, null, false);
            TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
            tv_key.setText(text);
            layout_wfinstance_content.addView(view_value);
        }
        layout_wfinstance_content.setVisibility(View.VISIBLE);
    }

    /**
     * 订单审批相关内容
     */
    private void orderData() {
        if (null == mWfInstance || null == mWfInstance.order) {
            return;
        }
        layout_wfinstance_content.setVisibility(View.GONE);
        ll_order_layout.setVisibility(View.VISIBLE);
        List<String> orderList = new ArrayList<>();
        OrderDetail order = mWfInstance.order;
        orderList.add("对应订单：" + order.title);
        orderList.add("对应客户：" + order.customerName);
        orderList.add("成交金额：" + order.dealMoney);
        orderList.add("订单编号：" + order.orderNum);
        if (null != order.extensionDatas && order.extensionDatas.size() > 0) {
            for (ContactLeftExtras ele : order.extensionDatas) {
                orderList.add(ele.label + "：" + ele.val);
            }
        }
        orderList.add("备注：" + order.remark);
        for (String text : orderList) {
            View view_value = LayoutInflater.from(this).inflate(R.layout.item_wf_data, null, false);
            TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
            tv_key.setText(text);
            ll_order_content.addView(view_value);
        }
        tv_product.setText(order.proName);
        tv_plan_value.setText("¥" + order.backMoney + "（" + order.ratePayment + "%)");
        AttachmentUUId = order.attachmentUUId;
        AttachmentCount = order.attachmentCount;
    }

    /**
     * 回款审批数据
     */
    private void paymentData() {
        if (null == mWfInstance || null == mWfInstance.paymentRecord || !(mWfInstance.paymentRecord.size() > 0)) {
            return;
        }
        layout_wfinstance_content.setVisibility(View.GONE);
        ll_payment_layout.setVisibility(View.VISIBLE);
        EstimateAdd payment = mWfInstance.paymentRecord.get(0);
        List<String> paymentList = new ArrayList<>();
        paymentList.add("对应订单：" + payment.orderTitle);
        paymentList.add("对应客户：" + payment.customerName);
//        paymentList.add("回款时间：" + DateTool.timet(payment.receivedAt + "", "yyyy.MM.dd"));
        paymentList.add("回款时间：" + com.loyo.oa.common.utils.DateTool.getDateFriendly(payment.receivedAt));
        paymentList.add("回款金额：" + "￥" + payment.receivedMoney);
        paymentList.add("开票金额：" + "￥" + payment.billingMoney);
        paymentList.add("收款人：" + payment.payeeUser.name);
        paymentList.add("收款方式：" + OrderCommon.getPaymentMode(payment.payeeMethod));
        paymentList.add("备注：" + payment.remark);
        for (String text : paymentList) {
            View view_value = LayoutInflater.from(this).inflate(R.layout.item_wf_data, null, false);
            TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
            tv_key.setText(text);
            ll_payment_content.addView(view_value);
        }
        AttachmentUUId = payment.attachmentUUId;
        AttachmentCount = payment.attachmentCount;
    }

    /**
     * 获得 机会 意向产品 的名字
     *
     * @param data
     * @return
     */
    private String getProductName(ArrayList<SaleIntentionalProduct> data) {
        String product = "";
        if (null == data) {
            return product;
        }
        for (SaleIntentionalProduct ele : data) {
            product += ele.name + "、";
        }
        return product.substring(0, product.length() - 1);
    }

    /**
     * 获取销售总金额
     */
    private float getSaleAmount(ArrayList<SaleIntentionalProduct> data) {
        float salePrice = 0;
        if (null == data) {
            return salePrice;
        }
        for (SaleIntentionalProduct ele : data) {
            salePrice += ele.salePrice * ele.quantity;
        }
        return salePrice;
    }

    /**
     * 获取意向产品总折扣
     *
     * @param data
     * @return
     */
    private float getProductDiscount(ArrayList<SaleIntentionalProduct> data) {
        float salePrice = 0;
        float totalMoney = 0;
        if (null == data) {
            return 0;
        }
        for (SaleIntentionalProduct ele : data) {
            salePrice += ele.salePrice;
            totalMoney += ele.totalMoney;
        }
        return salePrice / totalMoney;
    }

    /**
     * 获取机会的动态字段
     */
    private List<String> getProductCustomData(ArrayList<ContactLeftExtras> data) {
        List<String> newData = new ArrayList<>();
        for (ContactLeftExtras ele : data) {
            newData.add(ele.label + "：" + ele.val);
        }
        return newData;
    }

    void initData_WorkflowValues() {
        if (null == mWfInstance || null == mWfInstance.workflowValues) {
            return;
        }
        wfInstanceValuesDatas.clear();
        for (int i = 0; i < mWfInstance.workflowValues.size(); i++) {
            wfInstanceValuesDatas.add(mWfInstance.workflowValues.get(i));
        }
    }


    void updateUI() {
        if (mWfInstance == null) {
            return;
        }
        try {
//            tv_time_creator.setText(mWfInstance.creator.name + " " + app.df3.format(new Date(mWfInstance.createdAt * 1000)) + " 提交");
            tv_time_creator.setText(mWfInstance.creator.name + " " +DateTool.getDateTimeFriendly(mWfInstance.createdAt) + " 提交");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (mWfInstance.creator != null) {
            tv_title_creator.setText(mWfInstance.title);

            if (null != mWfInstance.creator.shortPosition) {
                tv_title_role.setText(mWfInstance.creator.shortPosition.getName());
            }
        }

        if (!StringUtil.isEmpty(mWfInstance.memo)) {
            layout_memo.setVisibility(View.VISIBLE);
            tv_memo.setText(mWfInstance.memo);
        } else {
            if (null != mWfInstance.demand && 300 == mWfInstance.bizForm.bizCode && !TextUtils.isEmpty(mWfInstance.demand.memo)) {
                layout_memo.setVisibility(View.VISIBLE);
                tv_memo.setText(mWfInstance.demand.memo);
            } else {
                layout_memo.setVisibility(View.GONE);
            }
        }
        tv_attachment_count.setText("附件 (" + (AttachmentCount == 0 ? mWfInstance.bizExtData.getAttachmentCount() : AttachmentCount) + "）");
        tv_projectName.setText(null == mWfInstance.ProjectInfo || TextUtils.isEmpty(mWfInstance.ProjectInfo.title) ? "无" : mWfInstance.ProjectInfo.title);
        if (300 == mWfInstance.bizForm.bizCode || 400 == mWfInstance.bizForm.bizCode
                || 500 == mWfInstance.bizForm.bizCode) {//赢单审批隐藏项目 和 附件  订单审批  回款审批
            layout_AttachFile.setVisibility(300 == mWfInstance.bizForm.bizCode ? View.GONE : View.VISIBLE);
            ll_project.setVisibility(View.GONE);
        }
        switch (mWfInstance.status) {

            case WfInstance.STATUS_NEW:
                tv_status.setText("待审批");
                tv_status.setBackgroundResource(R.drawable.wfinstance_retange_blue);
                break;
            case WfInstance.STATUS_PROCESSING:
                tv_status.setText("审批中");
                tv_status.setBackgroundResource(R.drawable.wfinstance_retange_purple);
                break;
            case WfInstance.STATUS_ABORT:
                tv_status.setText("未通过");
                tv_status.setBackgroundResource(R.drawable.wfinstance_retange_red);
                break;
            case WfInstance.STATUS_APPROVED:
                tv_status.setText("已通过");
                tv_status.setBackgroundResource(R.drawable.wfinstance_retange_green);
                break;
            case WfInstance.STATUS_FINISHED:
                tv_status.setText("已通过");
                tv_status.setBackgroundResource(R.drawable.wfinstance_retange_green); //状态4，5都归类为 已通过
                break;
            default:
                break;
        }
        initUI_listView_wfinstance();
        initUI_listView_workflowNodes();
        updateUI_layout_bottom();
        ll_loading.setStatus(LoadingLayout.Success);
    }

    /**
     * 审批内容数据设置
     */
    void initUI_listView_wfinstance() {
        if (layout_wfinstance_content.getChildCount() > 0) {
            layout_wfinstance_content.removeAllViews();
        }
        ArrayList<BizFormFields> fields = new ArrayList<>();
        if (mWfInstance != null && mWfInstance.bizForm != null && mWfInstance.bizForm.getFields() != null) {
            fields = mWfInstance.bizForm.getFields();
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
                    //EditText tv_value = (EditText) view_value.findViewById(R.id.et_value);
                    //tv_value.setEnabled(false);
                    //tv_value.setText(wfinstanceInfoValue(jsonObject.get(field.getId())));
                    TextView tv_key = (TextView) view_value.findViewById(R.id.tv_key);
                    tv_key.setText(field.getName() + "：" + wfinstanceInfoValue(jsonObject.get(field.getId())));
                    layout_wfinstance_content.addView(view_value);
                    layout_wfinstance_content.setVisibility(View.VISIBLE);
                }
            }
        }

        if (mWfInstance.status == WfInstance.STATUS_NEW || mWfInstance.status == WfInstance.STATUS_ABORT) {
            isPass = true;
        }

        //显示菜单
        if (isPass && mWfInstance.creator != null && mWfInstance.creator.isCurrentUser()) {
            img_title_right.setVisibility(View.VISIBLE);
        }
//        else if (mWfInstance.status == WfInstance.STATUS_ABORT && "300".equals(mWfInstance.bizForm.bizCode + "")) {
//            img_title_right.setVisibility(View.VISIBLE);
//        }
        if ("400".equals(mWfInstance.bizForm.bizCode + "") || "500".equals(mWfInstance.bizForm.bizCode + "")
                || "300".equals(mWfInstance.bizForm.bizCode + "")) {
            img_title_right.setVisibility(View.GONE);//自动生成的审批不可以编辑删除
        }
    }

    /**
     * 审批流程展示初始化相关
     */
    void initUI_listView_workflowNodes() {
        workflowNodesListViewAdapter = new WorkflowNodesListViewAdapter(mWfInstance.status, lstData_WfNodes, LayoutInflater.from(this));
        listView_workflowNodes.setAdapter(workflowNodesListViewAdapter);
        Global.setListViewHeightBasedOnChildren(listView_workflowNodes);
    }

    /**
     * 底部同意／驳回 菜单设置
     */

    void updateUI_layout_bottom() {

        if (mWfInstance == null) {
            return;
        }
        tv_wfnodes_title.setText(getWfNodesTitle());

        if (mWfInstance.status == WfInstance.STATUS_ABORT || mWfInstance.status == WfInstance.STATUS_FINISHED) {
            return;
        }

        ArrayList<WfNodes> nodes = mWfInstance.workflowNodes;
        if (nodes == null) {
            return;
        }

        WfNodes node = null;
        for (int i = 0; i < nodes.size(); i++) {
            if (null != nodes.get(i) && null != nodes.get(i).getExecutorUser() && !(TextUtils.isEmpty(userId))
                    && userId.equals(nodes.get(i).getExecutorUser().getId())) {
                node = nodes.get(i);
                if (node.getActive() == 2) {
                    break;
                }
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

    private String wfinstanceInfoValue(Object obj) {
        if (null == obj) {
            return "没有内容";
        }

        if (obj.getClass().toString().contains("Double")) {
            BigDecimal bigDecimal = new BigDecimal(obj + "");
            return bigDecimal.doubleValue() + "";
        } else {
            //判断是时间,就转换成友好的格式现实
            Long stamp = DateTool.getMinuteStamp(obj + "");
            if(0!=stamp){
                return  DateTool.getDateTimeFriendly(stamp/1000);
            }
            return obj + "";
        }

    }

    private String getWfNodesTitle() {

        StringBuilder builder = new StringBuilder();
        if (null != mWfInstance.workflowNodes) {
            int actives = 0;
            for (int i = mWfInstance.workflowNodes.size() - 1; i >= 0; i--) {
                WfNodes node = mWfInstance.workflowNodes.get(i);
                if (node.isActive()) {
                    actives++;
                }
            }
            builder.append("（" + actives + "/" + mWfInstance.workflowNodes.size() + "）");
        } else {
            builder.append("(0/0)");
        }

        return builder.toString();
    }

    @Override
    public void onBackPressed() {
        if (null != mWfInstance && null != mWfInstance.workflowValues && null != mWfInstance.workflowValues) {
            mWfInstance.setViewed(true);
            mWfInstance.workflowValues.clear();
            Intent intent = new Intent();
            intent.putExtra("review", mWfInstance);
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isUpdate ? 0x09 : RESULT_OK, intent);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 提交审批请求
     */
    void setData_wfinstance_approve(final int type, final String comment) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", comment);
        map.put("type", type);

        WfinstanceService.doWfInstance(mWfInstance.getId(),map).subscribe(new DefaultLoyoSubscriber<WfInstance>() {
            @Override
            public void onNext(WfInstance wfInstance_current) {
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
        });
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_nopass, R.id.layout_pass, R.id.layout_lastwork,
            R.id.layout_AttachFile, R.id.ll_sale, R.id.ll_product, R.id.ll_plan})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);
                //赢单未通过
                if (mWfInstance.status == WfInstance.STATUS_ABORT && "300".equals(mWfInstance.bizForm.bizCode + "")) {
                    intent.putExtra("delete", true);
                } else {
                    if (mWfInstance.status == WfInstance.STATUS_NEW||mWfInstance.status == WfInstance.STATUS_ABORT) {
                        intent.putExtra("delete", true);
                    }
                    intent.putExtra("edit", true);
                }
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

//                if (null != mWfInstance && (mWfInstance.status == 2 || mWfInstance.status == 4 || mWfInstance.status == 5)
//                        || "300".equals(mWfInstance.bizForm.bizCode + "") || "400".equals(mWfInstance.bizForm.bizCode + "")
//                        || "500".equals(mWfInstance.bizForm.bizCode + "")) {
//                }
                isOver = true;// 所有审批附件在详情是不准 操作16.08.12
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", mWfInstance.attachments);
                if (TextUtils.isEmpty(AttachmentUUId)) {
                    bundle.putSerializable("uuid", mWfInstance.attachmentUUId);
                } else {
                    bundle.putSerializable("uuid", AttachmentUUId);
                }
                bundle.putBoolean("isOver", isOver);
                bundle.putInt("bizType", 12);
                app.startActivityForResult(this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);
                break;
            case R.id.ll_sale:
                Bundle sale = new Bundle();
                sale.putString("id", saleId);
                sale.putString("formPath", "审批");
                app.startActivityForResult(this, SaleDetailsActivity.class, MainApp.ENTER_TYPE_RIGHT, 3, sale);
                break;
            case R.id.ll_product://订单 查看产品
                if (null == mWfInstance.order && null == mWfInstance.order.proInfo && mWfInstance.order.proInfo.size() > 0) {
                    Toast("没有产品");
                    return;
                }
                Bundle product = new Bundle();
                product.putInt("data", ActionCode.ORDER_DETAIL);
                product.putSerializable(ExtraAndResult.EXTRA_DATA, mWfInstance.order.proInfo);
                app.startActivityForResult(this, IntentionProductActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                break;
            case R.id.ll_plan://订单 查看回款记录
                Bundle mBundle = new Bundle();
                mBundle.putInt("fromPage", OrderEstimateListActivity.ORDER_DETAILS);
                mBundle.putString("price", mWfInstance.order.dealMoney + "");
                mBundle.putString("orderId", mWfInstance.order.id);
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, false);
                app.startActivityForResult(this, OrderEstimateListActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, mBundle);
                break;
//            case R.id.ll_payment_order://回款审批到订单详情
//                Intent mIntent = new Intent();
////              mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
//                mIntent.putExtra(ExtraAndResult.EXTRA_ID, (String) v.getTag());
//                mIntent.setClass(this, OrderDetailActivity.class);
//                startActivityForResult(mIntent, 1);
//                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
//                break;
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
                }
//                else if (comment.isEmpty() && type == 1) {
//                    comment = "同意";
//                }
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
     * 审批删除
     */
    public void deleteWfin() {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).deleteWfinstance(mWfInstance.getId(), new RCallback<WfInstance>() {
//            @Override
//            public void success(final WfInstance wfInstance, final Response response) {
//                if (null != wfInstance.workflowValues) {
//                    wfInstance.workflowValues.clear();
//                }
//                Intent intent = new Intent();
//                intent.putExtra("delete", wfInstance);
//                AppBus.getInstance().post(new BizForm());
//                app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                Toast("删除失败");
//                super.failure(error);
//            }
//        });
        WfinstanceService.deleteWfinstance(mWfInstance.getId()).subscribe(new DefaultLoyoSubscriber<WfInstance>() {
            @Override
            public void onNext(WfInstance wfInstance) {
                if (null != wfInstance.workflowValues) {
                    wfInstance.workflowValues.clear();
                }
                Intent intent = new Intent();
                intent.putExtra("delete", wfInstance);
                AppBus.getInstance().post(new BizForm());
                app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
            }
        });
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //编辑后 回调刷新
        if (resultCode == WfInstanceManageActivity.WFIN_FINISH_RUSH) {
            getWfinstanceData();
        }

        if (null == data) {
            return;
        }

        switch (requestCode) {

            case MSG_DELETE_WFINSTANCE:
                //选择编辑回调
                if (data.getBooleanExtra("edit", false)) {
                    mBundle = new Bundle();
                    mBundle.putSerializable("data", mWfInstance);
                    app.startActivityForResult(WfinstanceInfoActivity.this, WfInEditActivity.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, mBundle);
                    isUpdate = true;
                }
                //选择删除回调
                else if (data.getBooleanExtra("delete", false)) {
                    deleteWfin();
                }
                break;

            //附件上传 刷新数量
            case MSG_ATTACHMENT:
                if (data == null || data.getExtras() == null) {
                    return;
                }
                int fileNum=data.getIntExtra("attachFileNum",0);
                //这里判断!0才设置,因为back按键返回,是没有的,也就不需要设置。
                if(0!=fileNum){
                    tv_attachment_count.setText("附件" + "（" + fileNum + "）");
                    //更新存储的附件数量,避免跳转页面,携带过去,参数是错误的
                    mWfInstance.bizExtData.setAttachmentCount(fileNum);
                }

                break;

            default:
                break;
        }
    }
}
