package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.AttachmentActivity_;
import com.loyo.oa.v2.activityui.customer.bean.Contact;
import com.loyo.oa.v2.activityui.customer.bean.Member;
import com.loyo.oa.v2.activityui.customer.bean.MembersRoot;
import com.loyo.oa.v2.activityui.signin.SignInListActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :【客户详情】 界面
 * 作者 : ykb
 * 时间 : 15/9/24.
 */
@EActivity(R.layout.activity_customer_detail_info)
public class CustomerDetailInfoActivity extends BaseActivity {

    @ViewById ViewGroup img_title_left, img_title_right, layout_customer_info, layout_contact, layout_send_sms,
            layout_call, layout_wiretel_call, layout_sale_activity, layout_visit, layout_task, layout_attachment,
            ll_sale, ll_order;
    @ViewById TextView tv_title_1, tv_customer_name, tv_address, tv_tags, tv_contact_name,
            tv_contact_tel, customer_detail_wiretel, tv_sale_number, tv_visit_times, tv_task_count, tv_attachment_count,
            tv_follow_content, tv_follow_crecter_type, tv_contact_Number, tv_sale_count, tv_order_count;
    @ViewById ImageView img_public;
    /*之前由传过来的Customer获取客户ID，改为直接把客户ID传过来*/
    Customer mCustomer;
    @Extra("Id")
    String id;
    @Extra(ExtraAndResult.EXTRA_TYPE)
    public int customerType;//"1,我的客户", "2,团队客户", "3,公海客户"
    public boolean isLock;
    public boolean isMyUser;
    public boolean isPutOcen;
    public boolean isRoot = false;
    public Permission perDelete;
    public Permission perOcean;
    public Permission perGet;
    private MembersRoot memRoot;


    @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        tv_title_1.setText("客户详情");
        showLoading("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getCustomerById(id, new RCallback<Customer>() {
            @Override
            public void success(final Customer customer, final Response response) {
                HttpErrorCheck.checkResponse("客户详情-->", response);
                if (customer == null) {
                    Toast("获取数据失败");
                    return;
                }
                isLock = customer.lock;
                mCustomer = customer;
                getMembersRoot();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                finish();
            }
        });
    }

    /**
     * 获取参与人权限
     */
    void getMembersRoot() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                getMembersRoot(new RCallback<MembersRoot>() {
                    @Override
                    public void success(MembersRoot membersRoot, Response response) {
                        HttpErrorCheck.checkResponse("参与人权限", response);
                        memRoot = membersRoot;
                        initData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


    /**
     * 数据初始化
     */
    private void initData() {

        if (null == mCustomer) {
            return;
        }

        /*超级管理员,我的客户,Web权限控制判断*/
        if (MainApp.user.isSuperUser() && customerType == 3) {
            img_public.setVisibility(View.VISIBLE);
        } else {
            if (customerType == 3) {
                try {
                    perGet = (Permission) MainApp.rootMap.get("0404");
                    if (perGet.isEnable()) {
                        img_public.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast("客户挑入权限,code错误");
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
        isMyUser = (customerType != 3) ? true : false;

        if(mCustomer.lock){
            if(null != mCustomer.owner){
                if(mCustomer.owner.id.equals(MainApp.user.getId())){
                    img_title_right.setOnTouchListener(Global.GetTouch());
                }else{
                    img_title_right.setVisibility(View.INVISIBLE);
                }
            }
        }else{
            img_title_right.setVisibility(View.INVISIBLE);
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
        Contact contact = Utils.findDeault(mCustomer);
        if (null != contact) {
            tv_contact_name.setText(contact.getName());
            tv_contact_tel.setText(contact.getTel());
            customer_detail_wiretel.setText(contact.getWiretel());
        }
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
            tv_follow_content.setText(mCustomer.saleActivityInfo.content);
            tv_follow_crecter_type.setText(app.df3.format(new Date(mCustomer.saleActivityInfo.createAt * 1000)) + " " +
                    mCustomer.saleActivityInfo.creatorName + " #" + mCustomer.saleActivityInfo.typeName);
        } else {
            tv_follow_content.setVisibility(View.GONE);
            tv_follow_crecter_type.setVisibility(View.GONE);
        }
        tv_contact_Number.setText("(" + mCustomer.contacts.size() + ")");
    }

    /**
     * 判断是否是参与人
     */
    public boolean isMenber(final Customer mCustomer) {
        if (null != mCustomer) {
            for (Member element : mCustomer.members) {
                if (MainApp.user.id.equals(element.getUser().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 显示编辑客户弹出框
     */
    private void showEditPopu() {

        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View menuView = mLayoutInflater.inflate(R.layout.popu_child_task_edit_layout, null, false);

        Button btn_child_delete_task = (Button) menuView.findViewById(R.id.btn_child_delete_task);
        Button btnCancel = (Button) menuView.findViewById(R.id.btn_cancel_edit);
        Button btnUpdate = (Button) menuView.findViewById(R.id.btn_child_add_update);

        btnUpdate.setText("投入公海");
        btn_child_delete_task.setText("删除");

        /*超级管理员\web控制权限判断*/
        if (!MainApp.user.isSuperUser()) {
            try {
                perDelete = (Permission) MainApp.rootMap.get("0405");
                perOcean = (Permission) MainApp.rootMap.get("0403");
                if (!perDelete.isEnable()) {
                    btn_child_delete_task.setVisibility(View.GONE);
                }
                if (!perOcean.isEnable()) {
                    btnUpdate.setVisibility(View.GONE);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast("客户挑入/删除权限,code错误:0405,0403");
            }
        }

        btn_child_delete_task.setOnTouchListener(Global.GetTouch());
        btnCancel.setOnTouchListener(Global.GetTouch());
        btnUpdate.setOnTouchListener(Global.GetTouch());

        final PopupWindow popupWindow = new PopupWindow(menuView, -1, -1, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));// 响应键盘三个主键的必须步骤
        popupWindow.showAtLocation(findViewById(R.id.tv_title_1), Gravity.BOTTOM, 0, 0);

        menuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                popupWindow.dismiss();
                return false;
            }
        });

        PopuOnClickListener listener = new PopuOnClickListener(popupWindow);
        btn_child_delete_task.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
        btnUpdate.setOnClickListener(listener);

    }

    /**
     * 处理popuwindow里按钮的点击事件
     */
    private class PopuOnClickListener implements View.OnClickListener {
        private PopupWindow mWindow;

        PopuOnClickListener(final PopupWindow window) {
            mWindow = window;
        }

        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.btn_child_delete_task:
                    setPopView(true, "你确定要删除客户?");
                    break;
                case R.id.btn_child_add_update:
                    setPopView(false, "投入公海，相当于放弃此客户所有数据和管理权限，您确定要投入公海?");
                    break;
                default:
                    break;
            }
            mWindow.dismiss();
        }
    }

    /**
     * 提示弹出框
     */
    private void setPopView(final boolean isKind, final String message) {

        showGeneralDialog(true, true, message);
        //确定
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (isKind) {
                    delete();
                } else {
                    toPublic();
                }
            }
        });
        //取消
        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
            }
        });
    }


    /**
     * 删除客户
     */
    private void delete() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).delete(mCustomer.getId(), new RCallback<Customer>() {
            @Override
            public void success(final Customer newCustomer, final Response response) {
                app.finishActivity(CustomerDetailInfoActivity.this, MainApp.ENTER_TYPE_RIGHT, CustomerManagerActivity.CUSTOMER_COMM_RUSH, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                Toast("删除客户失败");
            }
        });
    }

    /**
     * 丢入公海
     */
    private void toPublic() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).toPublic(mCustomer.getId(), new RCallback<Customer>() {
            @Override
            public void success(final Customer newCustomer, final Response response) {
                isPutOcen = true;
                app.finishActivity(CustomerDetailInfoActivity.this, MainApp.ENTER_TYPE_RIGHT, CustomerManagerActivity.CUSTOMER_COMM_RUSH, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_customer_info, R.id.img_public,
            R.id.layout_contact, R.id.layout_send_sms, R.id.layout_call, R.id.layout_sale_activity,
            R.id.layout_visit, R.id.layout_task, R.id.layout_attachment, R.id.layout_wiretel_call, R.id.ll_sale, R.id.ll_order})
    void onClick(final View view) {
        Bundle bundle = new Bundle();
        Class<?> _class = null;
        int requestCode = -1;
        switch (view.getId()) {
            /*返回*/
            case R.id.img_title_left:
                if (isPutOcen) {
                    app.finishActivity(CustomerDetailInfoActivity.this, BaseMainListFragment.REQUEST_REVIEW, CustomerManagerActivity.CUSTOMER_COMM_RUSH, new Intent());
                } else {
                    onBackPressed();
                }
                break;
            case R.id.img_title_right:
                showEditPopu();
                break;
            case R.id.layout_customer_info:
                bundle.putBoolean("isRoot", isRoot);
                bundle.putSerializable("Customer", mCustomer);
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putBoolean(ExtraAndResult.EXTRA_TYPE, customerType == 3);
                bundle.putBoolean(ExtraAndResult.EXTRA_STATUS, isMenber(mCustomer));
                _class = CustomerInfoActivity_.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO;
                break;

            /*挑入*/
            case R.id.img_public:

                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).pickedIn(id, new RCallback<Customer>() {
                    @Override
                    public void success(final Customer newCustomer, final Response response) {
                        app.finishActivity(CustomerDetailInfoActivity.this, BaseMainListFragment.REQUEST_REVIEW, CustomerManagerActivity.CUSTOMER_COMM_RUSH, new Intent());
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
                    bundle.putBoolean(ExtraAndResult.EXTRA_STATUS, isMenber(mCustomer));
                    bundle.putSerializable(ExtraAndResult.EXTRA_ID, mCustomer.id);
                    _class = CustomerContactManageActivity_.class;
                    requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_CONTACTS;
                } catch (NullPointerException e) {
                    Toast("参数不全");
                }
                break;
            case R.id.layout_send_sms:
                if (null != mCustomer.contacts && mCustomer.contacts.size() > 0) {
                    Utils.sendSms(this, mCustomer.contacts.get(0).getTel());
                } else {
                    Toast("没有号码");
                }
                break;
            case R.id.layout_call:
                if (null != mCustomer.contacts && mCustomer.contacts.size() > 0) {
                    Utils.call(this, mCustomer.contacts.get(0).getTel());
                } else {
                    Toast("没有号码");
                }
                break;
            case R.id.layout_wiretel_call:
                if (null != mCustomer.contacts && mCustomer.contacts.size() > 0) {
                    Utils.call(this, mCustomer.contacts.get(0).getWiretel());
                } else {
                    Toast("没有号码");
                }
                break;
            /*跟进动态*/
            case R.id.layout_sale_activity:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putSerializable(Customer.class.getName(), mCustomer);
                _class = CustomerDynamicManageActivity.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_ACTIVITIS;
                break;
            /*拜访签到*/
            case R.id.layout_visit:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putSerializable("mCustomer", mCustomer);
                _class = SignInListActivity_.class;
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
                app.finishActivity(CustomerDetailInfoActivity.this, BaseMainListFragment.REQUEST_REVIEW, CustomerManagerActivity.CUSTOMER_COMM_RUSH, new Intent());
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
                        app.finishActivity(CustomerDetailInfoActivity.this, BaseMainListFragment.REQUEST_REVIEW, CustomerManagerActivity.CUSTOMER_COMM_RUSH, new Intent());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                break;
            case FinalVariables.REQUEST_PREVIEW_CUSTOMER_CONTACTS:
                break;
            case FinalVariables.REQUEST_PREVIEW_CUSTOMER_TASKS:
                break;
            default:
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
}
