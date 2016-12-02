package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.AttachmentActivity_;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.customer.event.EditCustomerEvent;
import com.loyo.oa.v2.activityui.customer.event.EditCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerListRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.MembersRoot;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.customer.presenter.impl.CustomerDetailinfoPresenterimpl;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerDetailinfoView;
import com.loyo.oa.v2.activityui.followup.DynamicAddActivity;
import com.loyo.oa.v2.activityui.signin.SignInActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :【客户详情】 界面
 * 作者 : ykb
 * 时间 : 15/9/24.
 */
@EActivity(R.layout.activity_customer_detail_info)
public class CustomerDetailInfoActivity extends BaseActivity implements CustomerDetailinfoView {

    @ViewById
    ViewGroup img_title_left, img_title_right, layout_customer_info, layout_contact, layout_send_sms,
            layout_call, layout_wiretel_call, layout_sale_activity, layout_visit, layout_task, layout_attachment,
            ll_sale, ll_order;
    @ViewById
    TextView tv_title_1, tv_customer_name, tv_address, tv_tags, tv_contact_name,
            tv_contact_tel, customer_detail_wiretel, tv_sale_number, tv_visit_times, tv_task_count, tv_attachment_count,
            tv_follow_content, tv_follow_crecter_type, tv_contact_Number, tv_sale_count, tv_order_count;
    @ViewById
    ImageView img_public;
    /*之前由传过来的Customer获取客户ID，改为直接把客户ID传过来*/
    Customer mCustomer;
    @Extra("Id")
    String id;
    @Extra(ExtraAndResult.EXTRA_TYPE)
    public int customerType;//"1,我负责的", "2,我参与的", "3,团队客户","4.公海客户" 5.游客
    public boolean isLock;
    public boolean isMyUser;
    public boolean isPutOcen;
    public boolean isEdit;
    public boolean isRoot = false;
    private boolean isMem = false;
    private boolean isTourist; //true:游客状态
    private MembersRoot memRoot;
    private Contact mContact;
    private RelativeLayout layout_wirete, layout_phone;
    private LinearLayout layout_gj, layout_sign;
    private LinearLayout layout_menu;
    private ImageView iv_select_tag;
    private CustomerDetailinfoPresenterimpl mPresenter;
    private ArrayList<NewTag> mTagItems = new ArrayList<>();


    @AfterViews
    void initViews() {
//        setTouchView(NO_SCROLL);
        tv_title_1.setText("客户详情");
        showLoading("", false);

        isTourist = getIntent().getBooleanExtra(ExtraAndResult.EXTRA_OBJ,false);

        layout_wirete = (RelativeLayout) findViewById(R.id.layout_wirete);
        layout_phone = (RelativeLayout) findViewById(R.id.layout_phone);
        layout_gj = (LinearLayout) findViewById(R.id.layout_gj);
        layout_sign = (LinearLayout) findViewById(R.id.layout_sign);
        layout_menu = (LinearLayout) findViewById(R.id.layout_menu);
        iv_select_tag = (ImageView) findViewById(R.id.iv_select_tag);

        iv_select_tag.setOnTouchListener(Global.GetTouch());
        layout_sign.setOnTouchListener(Global.GetTouch());
        layout_gj.setOnTouchListener(Global.GetTouch());

        mPresenter = new CustomerDetailinfoPresenterimpl(mContext, this);

        if (PermissionManager.getInstance().hasPermission(BusinessOperation.VISIT_TIMELINE)) {
            layout_gj.setVisibility(View.VISIBLE);
        }
        else {
            layout_gj.setVisibility(View.GONE);
        }

        if (PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_VISIT)) {
            layout_sign.setVisibility(View.VISIBLE);
        }
        else {
            layout_sign.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getData(id);
    }


    /**
     * 数据初始化
     */
    private void initData() {

        if (null == mCustomer) {
            finish();
            return;
        }

        if (! PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.PREVIEW)) {
            sweetAlertDialogView.alertMessageClick(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dismissSweetAlert();
                    finish();
                }
            }, "提示", "你无此功能权限");
            return;
        }

        boolean canDelete = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.DELETE);
        boolean canDump = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.DUMP);
        boolean canPickin = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.PICK_IN);
        img_title_right.setVisibility((!canDelete && !canDump)?View.GONE : View.VISIBLE);
        img_public.setEnabled(canPickin);
        img_public.setVisibility(canPickin?View.VISIBLE : View.GONE);

        /*超级管理员,我的客户,Web权限控制判断*/
        if (null != MainApp.user && MainApp.user.isSuperUser() && customerType == 4) {
            img_public.setVisibility(View.VISIBLE);
            layout_menu.setVisibility(View.GONE);
        } else {
            if (customerType == 4) {
                /* 客户挑入权限 */
                if (PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_PICKING)) {
                    if(!isTourist){
                        img_public.setVisibility(View.VISIBLE);
                    }
                    layout_menu.setVisibility(View.GONE);
                }
            }
        }

        if (memRoot.getValue().equals("0")) {
            isRoot = false;
        } else {
            isRoot = true;
        }

        /*判断是否有操作权限，来操作改客户信息
        * 本地userid与服务器回传ownerId比较，相等则是自己的客户，islock＝true为自己客户，false在公海中
        * 这里不是我的客户，也会返回到我的客户列表里面,接口应该出现问题
        * */
        isMyUser = (customerType != 4) ? true : false;

        if (mCustomer.lock) {
            if (null != mCustomer.owner) {
                if (mCustomer.owner.id.equals(MainApp.user.getId())) {
                    img_title_right.setOnTouchListener(Global.GetTouch());
                } else {
                    img_title_right.setVisibility(View.INVISIBLE);
                    isMem = true;
                }
            }
        } else {
            img_title_right.setVisibility(View.INVISIBLE);
            isMem = true;
        }

        if (customerType == 3 /*团队客户火力全开 相当于自己的客户*/ ) {
            img_title_right.setVisibility(View.VISIBLE);
        }else if(customerType == 5 /*游客*/){
            img_title_right.setVisibility(View.INVISIBLE);
            layout_menu.setVisibility(View.GONE);
            mCustomer.lock = false;
            isMem = true;
            isRoot = false;
            isMyUser = false;
        }


        img_title_left.setOnTouchListener(Global.GetTouch());
        layout_customer_info.setOnTouchListener(Global.GetTouch());
        img_public.setOnTouchListener(Global.GetTouch());
        layout_contact.setOnTouchListener(Global.GetTouch());
        layout_send_sms.setOnTouchListener(Global.GetTouch());
        layout_call.setOnTouchListener(Global.GetTouch());
        tv_sale_number.setOnTouchListener(Global.GetTouch());
        layout_sale_activity.setOnTouchListener(Global.GetTouch());
        layout_visit.setOnTouchListener(Global.GetTouch());
        layout_task.setOnTouchListener(Global.GetTouch());
        layout_attachment.setOnTouchListener(Global.GetTouch());


        tv_customer_name.setText(mCustomer.name);
        if (null != mCustomer.loc) {
            tv_address.setText("地址：" + mCustomer.loc.addr);
        }
        tv_tags.setText("标签：" + Utils.getTagItems(mCustomer));
        mContact = Utils.findDeault(mCustomer);
        if (null != mContact) {
            mPresenter.setDefaultContact(mContact.getId(), mCustomer.id);

            if (null == mContact.getTel() || TextUtils.isEmpty(mContact.getTel())) {
                layout_phone.setVisibility(View.GONE);
            } else {
                tv_contact_tel.setText(mContact.getTel());
            }

            if (null == mContact.getWiretel() || TextUtils.isEmpty(mContact.getWiretel())) {
                layout_wirete.setVisibility(View.GONE);
            } else {
                customer_detail_wiretel.setText(mContact.getWiretel());
            }

            tv_contact_name.setText(mContact.getName());

        } else {
            layout_phone.setVisibility(View.GONE);
            layout_wirete.setVisibility(View.GONE);
        }
        mTagItems.clear();
        mTagItems.addAll(mCustomer.tags);

        tv_visit_times.setText("(" + mCustomer.counter.getVisit() + ")");
        tv_sale_count.setText("(" + mCustomer.counter.getDemand() + ")");
        tv_order_count.setText("(" + mCustomer.counter.order + ")");
        tv_task_count.setText("(" + mCustomer.counter.getTask() + ")");
        tv_attachment_count.setText("(" + mCustomer.counter.getFile() + ")");

        //正式启用销售机会 弃用购买意向
        ll_sale.setVisibility(View.VISIBLE);
        ll_sale.setOnTouchListener(Global.GetTouch());

        //突出显示跟进动态
        tv_sale_number.setText("(" + mCustomer.saleActivityNum + ")");
        if (null != mCustomer.saleActivityInfo) {
            tv_follow_content.setVisibility(View.VISIBLE);
            tv_follow_crecter_type.setVisibility(View.VISIBLE);
            tv_follow_content.setText(mCustomer.saleActivityInfo.content.contains("<p>") ?
                    CommonHtmlUtils.Instance().checkContent(mCustomer.saleActivityInfo.content) : mCustomer.saleActivityInfo.content);
            tv_follow_crecter_type.setText(app.df3.format(new Date(mCustomer.saleActivityInfo.createAt * 1000)) + " " +
                    mCustomer.saleActivityInfo.creatorName + " #" + mCustomer.saleActivityInfo.typeName);
        } else {
            tv_follow_content.setVisibility(View.GONE);
            tv_follow_crecter_type.setVisibility(View.GONE);
        }
        tv_contact_Number.setText("(" + mCustomer.contacts.size() + ")");

        //正式启用销售机会 弃用购买意向
        ll_sale.setVisibility(View.VISIBLE);
        ll_sale.setOnTouchListener(Global.GetTouch());
    }

    /**
     * 提示弹出框
     */
    private void setPopView(final boolean isKind, final String message) {

        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
                if (isKind) {
                    mPresenter.delete(mCustomer.getId());
                } else {
                    mPresenter.toPublic(mCustomer.getId());
                }
            }
        }, "提示", message);
    }


    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_customer_info, R.id.img_public,
            R.id.layout_contact, R.id.layout_send_sms, R.id.layout_call, R.id.layout_sale_activity,
            R.id.layout_visit, R.id.layout_task, R.id.layout_attachment, R.id.layout_wiretel_call,
            R.id.ll_sale, R.id.ll_order, R.id.layout_gj, R.id.layout_sign, R.id.iv_select_tag})
    void onClick(final View view) {
        Bundle bundle = new Bundle();
        Intent mIntent;
        Class<?> _class = null;
        int requestCode = -1;
        switch (view.getId()) {

            /*选择标签*/
            case R.id.iv_select_tag:
                mIntent = new Intent(CustomerDetailInfoActivity.this, CustomerLabelCopyActivity.class);
                mIntent.putExtra("isMem", isMem);
                mIntent.putExtra("fromPage", 0);
                if (null != mTagItems) {
                    mIntent.putExtra("tagitems", Utils.convertTagItems(mTagItems));
                    mIntent.putExtra("customerId", mCustomer.getId());
                }
                startActivity(mIntent);
                break;

            /*拜访*/
            case R.id.layout_sign:
                mIntent = new Intent(CustomerDetailInfoActivity.this, SignInActivity.class);
                mIntent.putExtra("data", mCustomer);
                startActivity(mIntent);
                break;

            /*快捷跟进*/
            case R.id.layout_gj:
                mIntent = new Intent(CustomerDetailInfoActivity.this, DynamicAddActivity.class);
                mIntent.putExtra(Customer.class.getName(), mCustomer);
                mIntent.putExtra("isDetail", true);
                mIntent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
                startActivity(mIntent);
                break;

            /*返回*/
            case R.id.img_title_left:
                if (isPutOcen) {
                    AppBus.getInstance().post(new MyCustomerListRushEvent());
                    finish();
                } else {
                    onBackPressed();
                }
                break;
            /*菜单*/
            case R.id.img_title_right:
                onActionsheet();
                break;

            /*客户信息*/
            case R.id.layout_customer_info:
                bundle.putBoolean("isRoot", isRoot);
                bundle.putSerializable("Customer", mCustomer);
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putBoolean(ExtraAndResult.EXTRA_TYPE, customerType == 4);
                bundle.putBoolean(ExtraAndResult.EXTRA_STATUS, mPresenter.isMenber(mCustomer));
                _class = CustomerInfoActivity_.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO;
                break;

            /*挑入*/
            case R.id.img_public:

                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).pickedIn(id, new RCallback<Customer>() {
                    @Override
                    public void success(final Customer newCustomer, final Response response) {
                        AppBus.getInstance().post(new MyCustomerListRushEvent());
                        finish();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        finish();
                    }
                });

                break;
            /*联系人*/
            case R.id.layout_contact:
                try {
                    bundle.putBoolean("isLock", mCustomer.lock);
                    bundle.putBoolean("isMyUser", isMyUser);
                    bundle.putBoolean("isRoot", isRoot);
                    bundle.putBoolean(ExtraAndResult.EXTRA_STATUS, mPresenter.isMenber(mCustomer));
                    bundle.putSerializable(ExtraAndResult.EXTRA_ID, mCustomer.id);
                    _class = CustomerContactManageActivity_.class;
                    requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_CONTACTS;
                } catch (NullPointerException e) {
                    Toast("参数不全");
                }
                break;
            /*发送短信*/
            case R.id.layout_send_sms:
                if (null != mCustomer.contacts && mCustomer.contacts.size() > 0) {
                    Utils.sendSms(this, mCustomer.contacts.get(0).getTel());
                } else {
                    Toast("没有号码");
                }
                break;
            /*拨打手机*/
            case R.id.layout_call:
                if (null != mCustomer.contacts && mCustomer.contacts.size() > 0) {
                    mPresenter.isMobile(CustomerDetailInfoActivity.this, mContact.getTel(), 0, mContact.getName());
                } else {
                    Toast("没有号码");
                }
                break;
            /*拨打座机*/
            case R.id.layout_wiretel_call:
                if (null != mCustomer.contacts && mCustomer.contacts.size() > 0) {
                    mPresenter.isMobile(CustomerDetailInfoActivity.this, mContact.getWiretel(), 1, mContact.getName());
                } else {
                    Toast("没有号码");
                }
                break;
            /*跟进动态列表*/
            case R.id.layout_sale_activity:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putSerializable("mCustomer", mCustomer);
                _class = CustomerFollowUpListActivity.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_ACTIVITIS;
                break;
            /*拜访签到*/
            case R.id.layout_visit:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putSerializable("mCustomer", mCustomer);
                _class = CustomerSigninListActivity.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_LEGWORKS;
                break;
            /*任务计划*/
            case R.id.layout_task:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putSerializable("mCustomer", mCustomer);
                _class = TaskListActivity_.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_TASKS;
                break;
            /*文件*/
            case R.id.layout_attachment:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putInt("fromPage", Common.CUSTOMER_PAGE);
                bundle.putSerializable("uuid", mCustomer.uuid);
                bundle.putInt("bizType", 6);
                _class = AttachmentActivity_.class;
                requestCode = FinalVariables.REQUEST_DEAL_ATTACHMENT;
                break;
            /*销售机会*/
            case R.id.ll_sale:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putString(ExtraAndResult.EXTRA_ID, mCustomer.getId());
                bundle.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
                _class = SaleManageActivity.class;
                requestCode = ExtraAndResult.REQUEST_CODE;
                break;
            /*订单管理*/
            case R.id.ll_order:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putString(ExtraAndResult.EXTRA_ID, mCustomer.getId());
                bundle.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
                _class = CustomerOrderList.class;
                requestCode = ExtraAndResult.REQUEST_CODE;
                break;
        }
        if (null != _class && requestCode != -1) {
            goToChild(bundle, _class, requestCode);
        }
    }

    private void onActionsheet() {
        boolean canDelete = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.DELETE);
        boolean canDump = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.DUMP);

        if (!canDelete && !canDump) {
            return;
        }

        ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
        if (canDelete)
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    setPopViewEmbl(true, "你确定要删除客户?");
                }
            });
        if (canDump)
            dialog.addSheetItem("投入公海", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    setPopViewEmbl(false, "投入公海，相当于放弃此客户所有数据和管理权限，您确定要投入公海?");
                }
            });
        dialog.show();
    }

    /**
     * 编辑行为确认
     */
    @Subscribe
    public void onEditCustomerEvent(EditCustomerEvent event) {
        isEdit = true;
    }

    /**
     * 查看子内容
     *
     * @param b
     * @param _class
     * @param requestCode
     */

    private void goToChild(final Bundle b, final Class<?> _class, final int requestCode) {
        app.startActivityForResult(this, _class, MainApp.ENTER_TYPE_RIGHT, requestCode, b);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isPutOcen) {
                AppBus.getInstance().post(new MyCustomerListRushEvent());
                finish();
            } else if (isEdit) {
                AppBus.getInstance().post(new EditCustomerRushEvent());
                AppBus.getInstance().post(new MyCustomerListRushEvent());
                finish();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO:
                /*如果修改了负责人，不是自己，则finish该页面*/
                try {
                    Bundle bundle = data.getExtras();
                    boolean isCreator = bundle.getBoolean("isCreator");
                    if (!isCreator) {
                        AppBus.getInstance().post(new MyCustomerListRushEvent());
                        finish();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                break;
        }

        switch (resultCode) {
            case CustomerManagerActivity.CUSTOMER_COMM_RUSH:
                isPutOcen = true;
                break;
            //新建跟进 回调
            case FinalVariables.REQUEST_CREATE_TASK:
                break;
        }
    }

    /**
     * 投入公海成功处理
     */
    @Override
    public void toPublicEmbl() {
        isPutOcen = true;
        AppBus.getInstance().post(new MyCustomerListRushEvent());
        finish();
    }

    /**
     * 删除操作成处理
     */
    @Override
    public void deleteEmbl() {
        AppBus.getInstance().post(new MyCustomerListRushEvent());
        finish();
    }

    /**
     * 获取参与人权限处理
     */
    @Override
    public void getMembersRootEmbl(MembersRoot membersRoot) {
        memRoot = membersRoot;
        PermissionManager.getInstance().loadCRMConfig(membersRoot);
        initData();
    }

    /**
     * 获取详情数据成功处理
     */
    @Override
    public void getDataSuccessEmbl(Customer customer) {
        isLock = customer.lock;
        mCustomer = customer;
        mPresenter.getMembersRoot();
    }

    /**
     * 获取详情数据失败处理
     */
    @Override
    public void getDataErrorEmle() {
        finish();
    }

    /**
     * 弹出通用提示框
     */
    @Override
    public void setPopViewEmbl(boolean mespray, String message) {
        setPopView(mespray, message);
    }

    @Override
    public void showProgress(String message) {
        showLoading(message);
    }

    @Override
    public void hideProgress() {
        cancelLoading();
    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }
}
