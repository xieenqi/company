package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.activityui.order.common.ViewOrderDetailsExtra;
import com.loyo.oa.v2.activityui.product.IntentionProductActivity;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.order.permission.OrderAction;
import com.loyo.oa.v2.order.permission.OrderPermission;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【订单详情】页面
 */
public class OrderDetailActivity extends BaseLoadingActivity implements View.OnClickListener {

    private final static String ORDER_RESPONSDER_REQUEST = "com.loyo.OrderDetailActivity.ORDER_RESPONSDER_REQUEST";
    private LinearLayout img_title_left, ll_extra, ll_product,
                         ll_record, ll_enclosure, ll_plan,
                         ll_wflayout, ll_responsible, ll_wf_terminate;
    private ImageView    img_responsible;
    private RelativeLayout img_title_right;
    private TextView tv_title_1, tv_title, tv_status, tv_customer, tv_money, tv_product, tv_plan, tv_plan_value,
            tv_record, tv_record_value, tv_enclosure, tv_responsible_name, tv_creator_name,
            tv_creator_time, tv_wfname, tv_order_number, tv_memo,
            tv_start_time, tv_end_time, tv_wf_terminate_name;


    /**
     * 新增工单 2016-09-01
     */
    private LinearLayout ll_worksheet; /* 工单cell */
    private TextView tv_worksheet;     /* 工单数  */

    private OrderDetail mData;
    private String orderId;
    private Bundle mBundle;
    private boolean isDelete, isEdit, isStop, isCopy, isAdd;
    private boolean capitalReturningPlanCRUD;
    private boolean capitalReturningRecordCRUD;
    private boolean responsibleChange;
    private boolean worksheetAdd;
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

    /**
     * 机会 生成订单
     */
    public final static int ORDER_COPY = 0x13;

    public final static int RET_CAPITAL_RETURNING = 0x15;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ExtraAndResult.MSG_WHAT_VISIBLE:
                    if (attachmentSize != 0) {
                        tv_enclosure.setText("附件（"+ attachmentSize + "）");
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntentData();
        initView();
        getPageData();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_order_detail_new);
    }

    @Override
    public void getPageData() {
        ll_loading.setStatus(LoadingLayout.Success);
        getData();
    }

    @Subscribe
    public void onWorksheetCreated(Worksheet data) {
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

        ll_responsible = (LinearLayout) findViewById(R.id.ll_responsible);
        img_responsible = (ImageView)findViewById(R.id.img_responsible_arrow);

        tv_start_time = (TextView)findViewById(R.id.tv_start_time);
        tv_end_time = (TextView)findViewById(R.id.tv_end_time);

        tv_wf_terminate_name = (TextView)findViewById(R.id.tv_wf_terminate_name);
        ll_wf_terminate = (LinearLayout)findViewById(R.id.ll_wf_terminate);
        ll_wf_terminate.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                actionMenu();
                break;
            case R.id.tv_customer://跳转到相关客户
                if (!PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_MANAGEMENT)) {
                    sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("Id", mData.customerId);
                intent.setClass(OrderDetailActivity.this, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            case R.id.tv_wfname://跳转到相关审批
                if (!PermissionManager.getInstance().hasPermission(BusinessOperation.APPROVAL_PROCESS)) {
                    sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                    return;
                }
                Intent intentWf = new Intent();
                intentWf.putExtra(ExtraAndResult.EXTRA_ID, mData.wfId);
                intentWf.setClass(OrderDetailActivity.this, WfinstanceInfoActivity_.class);
                startActivityForResult(intentWf, ExtraAndResult.REQUEST_CODE);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            case R.id.ll_wf_terminate://跳转到相关审批
                if (!PermissionManager.getInstance().hasPermission(BusinessOperation.APPROVAL_PROCESS)) {
                    sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                    return;
                }
                Intent intentWf2 = new Intent();
                intentWf2.putExtra(ExtraAndResult.EXTRA_ID, mData.unExpectedWfId);
                intentWf2.setClass(OrderDetailActivity.this, WfinstanceInfoActivity_.class);
                startActivityForResult(intentWf2, ExtraAndResult.REQUEST_CODE);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            case R.id.ll_product://购买产品
                Bundle product = new Bundle();
                product.putBoolean(IntentionProductActivity.KEY_CAN_EDIT, false);
                product.putBoolean(IntentionProductActivity.KEY_CAN_DELETE, false);
                product.putSerializable(ExtraAndResult.EXTRA_DATA, mData.proInfo);
                app.startActivityForResult(OrderDetailActivity.this, IntentionProductActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                break;
            case R.id.ll_record://回款记录  mData.backMoney + "（"+ mData.ratePayment + "%)");
                Bundle mBundle = new Bundle();
                if (mData.paymentRecords != null) {
                    mBundle.putSerializable("data", mData.paymentRecords);
                }
                else {
                    mBundle.putSerializable(OrderEstimateListActivity.KEY_GET_DATA, true);
                }
                mBundle.putString("price", tv_money.getText().toString());
                mBundle.putString("orderId", mData.id);
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, capitalReturningRecordCRUD);
                mBundle.putInt("已回款", mData.backMoney);
                mBundle.putDouble("回款率", mData.ratePayment);
                mBundle.putInt("订单待审核", mData.status);//不显示回款记录状态
                mBundle.putBoolean(OrderEstimateListActivity.KEY_COMMIT_CHANGE, true);
                app.startActivityForResult(this, OrderEstimateListActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, mBundle);
                break;
            case R.id.ll_enclosure://附件
                mBundle = new Bundle();
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, false);
                if(2 == mData.status){
                    //订单没有通过,可以编辑附件
                    mBundle.putBoolean("isOver", false);
                    mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, true);
                }else{
                    mBundle.putBoolean("isOver", true);
                    mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, false);
                }
                mBundle.putString("uuid", mData.attachmentUUId);
                app.startActivityForResult(this, OrderAttachmentActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_HIDEDIALOG, mBundle);
                break;
            case R.id.ll_plan://回款计划
                mBundle = new Bundle();
                mBundle.putInt("status", mData.status);
                mBundle.putString("orderId", mData.id);
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, capitalReturningPlanCRUD);
                app.startActivityForResult(this, OrderPlanListActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, 102, mBundle);
                break;
            case R.id.ll_worksheet://工单
                if (!PermissionManager.getInstance().hasPermission(BusinessOperation.WORKSHEET_MANAGEMENT)) {
                    sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                    return;
                }
                mBundle = new Bundle();
                mBundle.putSerializable(ExtraAndResult.EXTRA_OBJ, mData);
                mBundle.putBoolean(ExtraAndResult.EXTRA_BOOLEAN, worksheetAdd);
                mBundle.putInt(ExtraAndResult.EXTRA_ID, mData.status);
                app.startActivityForResult(this, OrderWorksheetsActivity.class, MainApp.ENTER_TYPE_RIGHT, 102, mBundle);
                break;
            case R.id.ll_responsible:
            {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, ORDER_RESPONSDER_REQUEST);
                Intent intent2 = new Intent();
                intent2.setClass(this, ContactPickerActivity.class);
                intent2.putExtras(bundle);
                startActivity(intent2);
            }
            break;
        }
    }

    private void getData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        OrderService.getSaleDetails(orderId, new HashMap<String, Object>())
                .subscribe(new DefaultLoyoSubscriber<OrderDetail>(ll_loading) {
                    @Override
                    public void onNext(OrderDetail detail) {
                        ll_loading.setStatus(LoadingLayout.Success);
                        if (null == detail) {
                            ll_loading.setStatus(LoadingLayout.No_Network);
                            ll_loading.setNoNetworkText("没有获取到数据");
                            return;
                        }
                        mData = detail;
                        bindData();
                    }
                });
    }

    private void bindData() {
        isDelete = OrderPermission.getInstance().hasOrderAuthority(mData.relationState, mData.status, OrderAction.ORDER_DELETE);
        isEdit   = OrderPermission.getInstance().hasOrderAuthority(mData.relationState, mData.status, OrderAction.ORDER_EDIT);
        isCopy   = OrderPermission.getInstance().hasOrderAuthority(mData.relationState, mData.status, OrderAction.ORDER_COPY);
        isStop   = OrderPermission.getInstance().hasOrderAuthority(mData.relationState, mData.status, OrderAction.ORDER_TERMINATE);
        capitalReturningPlanCRUD
                 = OrderPermission.getInstance().hasOrderAuthority(mData.relationState, mData.status, OrderAction.ORDER_CAPITAL_RETURN_PLAN_CRUD);
        capitalReturningRecordCRUD
                 = OrderPermission.getInstance().hasOrderAuthority(mData.relationState, mData.status, OrderAction.ORDER_CAPITAL_RETURN_RECORD_CRUD);

        responsibleChange
                 = OrderPermission.getInstance().hasOrderAuthority(mData.relationState, mData.status, OrderAction.ORDER_RESPONSIBLE_PERSON_CHANGE);
        worksheetAdd
                 = OrderPermission.getInstance().hasOrderAuthority(mData.relationState, mData.status, OrderAction.ORDER_WORKSHEET_ADD);

        isStop   = isStop && ! mData.hasTerminate();

        img_title_right.setVisibility(
                (isDelete || isEdit || isCopy || isStop) ? View.VISIBLE:View.GONE
        );

        if (responsibleChange) {
            ll_responsible.setOnClickListener(this);
            tv_responsible_name.setTextColor(getResources().getColor(R.color.text33));
            img_responsible.setVisibility(View.VISIBLE);
        }
        else {
            ll_responsible.setOnClickListener(null);
            tv_responsible_name.setTextColor(getResources().getColor(R.color.text99));
            img_responsible.setVisibility(View.GONE);
        }

        tv_title.setText(mData.title);
        tv_customer.setText(mData.customerName);
        tv_money.setText(Utils.setValueDouble(mData.dealMoney));
        tv_product.setText(mData.proName);
        tv_responsible_name.setText(mData.directorName);
        tv_creator_name.setText(mData.creatorName);
        tv_plan.setText("回款计划（"+ mData.planNum + "）");
        tv_record.setText("回款记录（"+ mData.recordNum + "）");
        tv_record_value.setText("¥" + mData.backMoney + "（"+ mData.ratePayment + "%)");
        tv_worksheet.setText("工单" + "（"+ mData.worksheetNum + "）");
        if(attachmentSize==0)tv_enclosure.setText("附件（"+ mData.attachmentCount + "）");//避免上传附件回来,把原来的数值抹掉了
//        tv_creator_time.setText(app.df3.format(new Date(Long.valueOf(mData.createdAt + "") * 1000)));
        tv_creator_time.setText(DateTool.getDateTimeFriendly(mData.createdAt));
        tv_start_time.setText(DateTool.getDateTimeFriendly(mData.startAt));
        tv_end_time.setText(DateTool.getDateTimeFriendly(mData.endAt));
        tv_plan_value.setText(mData.planMoney + "");
        OrderCommon.getOrderDetailsStatus(tv_status, mData.status);
        if (!TextUtils.isEmpty(mData.wfName)) {//是否关联审批
            ll_wflayout.setVisibility(View.VISIBLE);
            tv_wfname.setText(mData.wfName);
        }
        if (!TextUtils.isEmpty(mData.unExpectedWfId)) {//是否关联审批
            ll_wf_terminate.setVisibility(View.VISIBLE);
            tv_wf_terminate_name.setText(mData.getTerminateWfDisplayTitle());
        }
        else {
            ll_wf_terminate.setVisibility(View.GONE);
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
    private void actionMenu() {
        ActionSheetDialog dialog = new ActionSheetDialog(OrderDetailActivity.this).builder();
        if (isEdit)
            dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    mBundle = new Bundle();
                    mBundle.putInt("fromPage", ORDER_EDIT);
                    mBundle.putSerializable("data", mData);
                    mBundle.putSerializable("orderId", mData.id);
                    mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, capitalReturningRecordCRUD);
                    app.startActivityForResult(OrderDetailActivity.this, OrderAddActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
                }
            });

        if (isCopy)
            dialog.addSheetItem("复制订单", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {


                    sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dismissSweetAlert();
                        }
                    }, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dismissSweetAlert();
                            mBundle = new Bundle();
                            mBundle.putInt("fromPage", ORDER_COPY);
                            mBundle.putSerializable("orderId", mData.id);
                            app.startActivityForResult(OrderDetailActivity.this, OrderAddActivity.class,
                                    MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
                        }
                    }, "提示", "复制订单将自动获取最新的产品信息，如产品名称、单价、单位等信息，你确定要复制吗？");

                }
            });

        if (isStop)
            dialog.addSheetItem("意外终止", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    terminationOrder();
                }
            });
        if (isDelete)
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {

                    sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dismissSweetAlert();
                        }
                    }, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dismissSweetAlert();
                            deleteOrder();
                        }
                    }, "提示", "订单删除后不可恢复！删除后将清除订单中的所有回款记录、计划（工单除外）。你确定要删除吗？");
                }
            });
        dialog.show();
    }

    private void deleteOrder() {
        OrderService.deleteOrder(mData.id)
                .subscribe(new DefaultLoyoSubscriber<Object>() {
                    @Override
                    public void onNext(Object o) {
                        app.finishActivity(OrderDetailActivity.this,
                                MainApp.ENTER_TYPE_LEFT,
                                ExtraAndResult.REQUEST_CODE,
                                new Intent());

                    }
                });
    }

    private void terminationOrder() {

        if (mData.wfSwitch) {
            Bundle mBundle = new Bundle();
            mBundle.putString(TerminateOrderCommitActivity.KEY_ORDER_ID, mData.id);
            app.startActivityForResult(OrderDetailActivity.this, TerminateOrderCommitActivity.class,
                    MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
        }
        else {
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dismissSweetAlert();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dismissSweetAlert();
                    OrderService.terminationOrder(mData.id)
                            .subscribe(new DefaultLoyoSubscriber<Object>() {
                                @Override
                                public void onNext(Object o) {
                                    Toast("订单终止成功");
                                    getData();
                                }
                            });
                }
            }, "提示", "意外终止后，此订单无法再创建回款计划、回款记录，而且添加的回款记录也无法纳入业绩统计。" +
                    "意外终止后不可恢复，你确定要终止吗？");
        }


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
            case ExtraAndResult.REQUEST_CODE_SOURCE: // 回款记录
            {
                if (null == data) {
                    return;
                }
                boolean hasChangedData = data.getBooleanExtra(OrderEstimateListActivity.RET_HAS_CHANGED_DATA, false);
                if (hasChangedData) {
                    ArrayList<EstimateAdd> capitalReturningList = (ArrayList<EstimateAdd>)data.getSerializableExtra("data");
                    if (capitalReturningList != null) {
                        mData.paymentRecords = capitalReturningList;
                    }
                }
            }
            break;

        }
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {
        if (ORDER_RESPONSDER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            OrganizationalMember user = Compat.convertStaffCollectionToNewUser(collection);
            if (user == null) {
                return;
            } else {
                showCommitLoading();
                HashMap<String, Object> map = new HashMap<>();
                HashMap<String, Object> owner = new HashMap<>();
                owner.put("id", user.getId());
                map.put("ids", mData.id);
                map.put("owner", owner);
                OrderService.updateOwner(map)
                        .subscribe(new DefaultLoyoSubscriber<Object>(hud) {
                            @Override
                            public void onNext(Object o) {
                                getData();
                            }
                        });
            }
        }
    }
}
