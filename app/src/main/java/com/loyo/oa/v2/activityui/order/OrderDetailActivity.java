package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.activityui.order.common.ViewOrderDetailsExtra;
import com.loyo.oa.v2.activityui.sale.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.GeneralPopView;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.squareup.otto.Subscribe;

import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【订单详情】页面
 * Created by xeq on 16/8/2.
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout img_title_left, ll_extra, ll_product, ll_record, ll_enclosure, ll_plan, ll_wflayout;
    private RelativeLayout img_title_right;
    private TextView tv_title_1, tv_title, tv_status, tv_customer, tv_money, tv_product, tv_plan, tv_plan_value,
            tv_record, tv_record_value, tv_enclosure, tv_responsible_name, tv_creator_name,
            tv_creator_time, tv_wfname, tv_order_number, tv_memo;


    /**
     * 新增工单 2016-09-01
     */
    private LinearLayout ll_worksheet; /* 工单cell */
    private TextView tv_worksheet;     /* 工单数  */

    private OrderDetail mData;
    private String orderId;
    private Bundle mBundle;
    private boolean isDelete, isEdit, isStop, isAdd;
    private int attachmentSize = 0;

    /**
     * 来自订单新建
     */
    public final static int ORDER_ADD = 0x10;

    /**
     * 来自订单编辑
     */
    public final static int ORDER_EDIT = 0x11;

    /**
     * 机会 生成订单
     */
    public final static int ORDER_CREATE = 0x12;


    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ExtraAndResult.MSG_WHAT_VISIBLE:
                    if (attachmentSize != 0) {
                        tv_enclosure.setText("附件(" + attachmentSize + ")");
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getIntentData();
        initView();
    }

    @Subscribe
    public void
    onWorksheetCreated(Worksheet data) {
        // 刷新数目
        getData();
    }

    private void getIntentData() {
        orderId = getIntent().getStringExtra(ExtraAndResult.EXTRA_ID);
        if (TextUtils.isEmpty(orderId)) {
            onBackPressed();
            Toast("参数不全");
        }
    }

    private void initView() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText("订单详情");
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_plan = (TextView) findViewById(R.id.tv_plan);
        tv_plan_value = (TextView) findViewById(R.id.tv_plan_value);
        tv_record = (TextView) findViewById(R.id.tv_record);
        tv_record_value = (TextView) findViewById(R.id.tv_record_value);
        tv_enclosure = (TextView) findViewById(R.id.tv_enclosure);
        tv_responsible_name = (TextView) findViewById(R.id.tv_responsible_name);
        tv_creator_name = (TextView) findViewById(R.id.tv_creator_name);
        tv_creator_time = (TextView) findViewById(R.id.tv_creator_time);
        tv_wfname = (TextView) findViewById(R.id.tv_wfname);
        tv_order_number = (TextView) findViewById(R.id.tv_order_number);
        tv_memo = (TextView) findViewById(R.id.tv_memo);
        ll_extra = (LinearLayout) findViewById(R.id.ll_extra);
        tv_customer.setOnClickListener(this);
        tv_wfname.setOnClickListener(this);
        ll_product = (LinearLayout) findViewById(R.id.ll_product);
        ll_product.setOnClickListener(this);
        ll_record = (LinearLayout) findViewById(R.id.ll_record);
        ll_record.setOnClickListener(this);
        ll_enclosure = (LinearLayout) findViewById(R.id.ll_enclosure);
        ll_enclosure.setOnClickListener(this);
        ll_plan = (LinearLayout) findViewById(R.id.ll_plan);
        ll_plan.setOnClickListener(this);
        ll_wflayout = (LinearLayout) findViewById(R.id.ll_wflayout);
        ll_worksheet = (LinearLayout) findViewById(R.id.ll_worksheet);
        ll_worksheet.setOnClickListener(this);
        tv_worksheet = (TextView) findViewById(R.id.tv_worksheet);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                functionBuuton();
                break;
            case R.id.tv_customer://跳转到相关客户
                Intent intent = new Intent();
                intent.putExtra("Id", mData.customerId);
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_MY);
                intent.setClass(OrderDetailActivity.this, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            case R.id.tv_wfname://跳转到相关审批
                Intent intentWf = new Intent();
                intentWf.putExtra(ExtraAndResult.EXTRA_ID, mData.wfId);
                intentWf.setClass(OrderDetailActivity.this, WfinstanceInfoActivity_.class);
                startActivityForResult(intentWf, ExtraAndResult.REQUEST_CODE);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            case R.id.ll_product://购买产品
                Bundle product = new Bundle();
                product.putInt("data", ActionCode.ORDER_DETAIL);
                product.putSerializable(ExtraAndResult.EXTRA_DATA, mData.proInfo);
                app.startActivityForResult(OrderDetailActivity.this, IntentionProductActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                break;
            case R.id.ll_record://回款记录  mData.backMoney + "(" + mData.ratePayment + "%)");
                Bundle mBundle = new Bundle();
                mBundle.putInt("fromPage", OrderEstimateListActivity.ORDER_DETAILS);
                mBundle.putString("price", tv_money.getText().toString());
                mBundle.putString("orderId", orderId);
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, isAdd);
                mBundle.putInt("已回款", mData.backMoney);
                mBundle.putDouble("回款率", mData.ratePayment);
                mBundle.putInt("订单待审核", mData.status);//不显示回款记录状态
                app.startActivityForResult(this, OrderEstimateListActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, mBundle);
                break;
            case R.id.ll_enclosure://附件
                mBundle = new Bundle();
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, false);
                mBundle.putBoolean("isOver", true);
                mBundle.putString("uuid", mData.attachmentUUId);
                app.startActivityForResult(this, OrderAttachmentActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_HIDEDIALOG, mBundle);
                break;
            case R.id.ll_plan://回款计划
                mBundle = new Bundle();
                mBundle.putInt("status", mData.status);
                mBundle.putString("orderId", mData.id);
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, isAdd);
                app.startActivityForResult(this, OrderPlanListActivity.class, MainApp.ENTER_TYPE_RIGHT, 102, mBundle);
                break;
            case R.id.ll_worksheet://工单

                boolean canAddWorksheet = false;
                if (mData.status == 3 || mData.status == 4) {
                    canAddWorksheet = true;
                }

                mBundle = new Bundle();
                mBundle.putSerializable(ExtraAndResult.EXTRA_OBJ, mData);
                mBundle.putBoolean(ExtraAndResult.EXTRA_BOOLEAN,canAddWorksheet);
                app.startActivityForResult(this, OrderWorksheetsActivity.class, MainApp.ENTER_TYPE_RIGHT, 102, mBundle);
                break;
        }
    }

    private void getData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .getSaleDetails(orderId, new Callback<OrderDetail>() {
                    @Override
                    public void success(OrderDetail orderdetail, Response response) {
                        HttpErrorCheck.checkResponse("订单详情", response);
                        if (null == orderdetail) {
                            Toast("没有获取到数据");
                            onBackPressed();
                            return;
                        }
                        mData = orderdetail;
                        bindData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        onBackPressed();
                    }
                });
    }

    private void bindData() {
        switch (mData.status) {
            case 1://待审核
                img_title_right.setVisibility(View.GONE);
                isDelete = false;
                isEdit = false;
                isAdd = false;
                isStop = false;
                break;
            case 2://未通过
                isDelete = true;
                isEdit = true;
                isAdd = false;
                isStop = false;
                break;
            case 3://进行中
                isDelete = false;
                isEdit = false;
                isAdd = true;
                isStop = true;
                break;
            case 4://已完成
                isDelete = false;
                isEdit = false;
                isAdd = false;
                isStop = true;
                break;
            case 5://意外终止
                isDelete = false;
                isEdit = false;
                isAdd = false;
                isStop = false;
                img_title_right.setVisibility(View.GONE);
                break;

        }
        if (!MainApp.user.id.equals(mData.directorId)) {//如果不是负责人有编辑 添加的权限
            img_title_right.setVisibility(View.GONE);
            isDelete = false;
            isEdit = false;
            isAdd = false;
            isStop = false;
        }
        tv_title.setText(mData.title);
        tv_customer.setText(mData.customerName);
        tv_money.setText(Utils.setValueDouble(mData.dealMoney));
        tv_product.setText(mData.proName);
        tv_responsible_name.setText(mData.directorName);
        tv_creator_name.setText(mData.creatorName);
        tv_plan.setText("回款计划（" + mData.planNum + "）");
        tv_record.setText("回款记录（" + mData.recordNum + "）");
        tv_record_value.setText("¥" + mData.backMoney + "(" + mData.ratePayment + "%)");
        tv_worksheet.setText("工单" + "(" + mData.worksheetNum + ")");
        tv_enclosure.setText("附件（" + mData.attachmentCount + "）");
        tv_creator_time.setText(app.df3.format(new Date(Long.valueOf(mData.createdAt + "") * 1000)));
        tv_plan_value.setText(mData.planMoney + "");
        OrderCommon.getOrderDetailsStatus(tv_status, mData.status);
        if (!TextUtils.isEmpty(mData.wfName)) {//是否关联审批
            ll_wflayout.setVisibility(View.VISIBLE);
            tv_wfname.setText(mData.wfName);
        }
        tv_order_number.setText(mData.orderNum);
        tv_memo.setText("备注：" + mData.remark);
        if (ll_extra.getChildCount() != 0) {
            ll_extra.removeAllViews();
        }
        if (null != mData.extensionDatas) {
            for (ContactLeftExtras ele : mData.extensionDatas) {
                ll_extra.addView(new ViewOrderDetailsExtra(mContext, ele));
            }
        }
    }

    /**
     * 右上角菜单
     */
    private void functionBuuton() {
        ActionSheetDialog dialog = new ActionSheetDialog(OrderDetailActivity.this).builder();
        if (isEdit)
            dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    mBundle = new Bundle();
                    mBundle.putInt("fromPage", ORDER_EDIT);
                    mBundle.putSerializable("data", mData);
                    app.startActivityForResult(OrderDetailActivity.this, OrderAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
                }
            });
        if (isStop)
            dialog.addSheetItem("意外终止", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    final GeneralPopView wran = showGeneralDialog(true, false, "此订单无法再创建回款计划、回款记录，而且添加的回款记录也无法纳入业绩统计。" +
                            "意外终止后不可恢复，你确定要终止吗？");
                    wran.setNoCancelOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            terminationOrder();
                            wran.dismisDialog();
                        }
                    });
                }
            });
        if (isDelete)
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    final GeneralPopView wran = showGeneralDialog(true, false, "删除不可恢复\n确定删除？");
                    wran.setNoCancelOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteOrder();
                            wran.dismisDialog();
                        }
                    });

                }
            });
        dialog.show();
    }

    private void deleteOrder() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class).
                deleteOrder(mData.id, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("删除订单：", response);
                        app.finishActivity(OrderDetailActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    private void terminationOrder() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class).
                terminationOrder(mData.id, new Callback<Object>() {
                    @Override
                    public void success(Object estimatePlanAdd, Response response) {
                        HttpErrorCheck.checkResponse("意外终止订单：", response);
                        Toast("订单终止成功");
                        getData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
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
            //编辑回调
            case ExtraAndResult.REQUEST_CODE_STAGE:
                getData();
                break;

            //附件回调
            case ExtraAndResult.MSG_WHAT_HIDEDIALOG:
                if (null == data) {
                    return;
                }
                attachmentSize = data.getIntExtra("size", 0);
                mHandler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_VISIBLE);
                break;

        }
    }
}
