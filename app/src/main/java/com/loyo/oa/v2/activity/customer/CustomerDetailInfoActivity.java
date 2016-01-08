package com.loyo.oa.v2.activity.customer;

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
import com.loyo.oa.v2.activity.DemandsManageActivity;
import com.loyo.oa.v2.activity.SaleActivitiesManageActivity;
import com.loyo.oa.v2.activity.SignInListActivity_;
import com.loyo.oa.v2.activity.attendance.AttachmentActivity_;
import com.loyo.oa.v2.activity.tasks.TaskListActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
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

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    TextView tv_title_1;
    @ViewById
    ImageView img_public;

    @ViewById
    ViewGroup layout_customer_info;
    @ViewById
    TextView tv_customer_name;
    @ViewById
    TextView tv_address;
    @ViewById
    TextView tv_tags;

    @ViewById
    ViewGroup layout_contact;
    @ViewById
    TextView tv_contact_name;
    @ViewById
    TextView tv_contact_tel;
    @ViewById
    ViewGroup layout_send_sms;
    @ViewById
    ViewGroup layout_call;

    @ViewById
    ViewGroup layout_sale_activity;
    @ViewById
    ViewGroup layout_visit;
    @ViewById
    ViewGroup layout_purchase;
    @ViewById
    ViewGroup layout_task;
    @ViewById
    ViewGroup layout_attachment;

    @ViewById
    TextView tv_sale_activity_date;
    @ViewById
    TextView tv_visit_times;
    @ViewById
    TextView tv_purchase_count;
    @ViewById
    TextView tv_task_count;
    @ViewById
    TextView tv_attachment_count;

    /*之前由传过来的Customer获取客户ID，改为直接把客户ID传过来*/
    Customer mCustomer;
    @Extra("Id")
    String id;

    String ownErId;
    boolean isLock;
    boolean isMyUser;
    boolean isPutOcen;

    @AfterViews
    void initViews() {

        setTouchView(NO_SCROLL);
        tv_title_1.setText("客户详情");
        getData();

    }

    /**
     * 获取数据
     */
    private void getData() {
        Utils.dialogShow(this, "请稍候");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getCustomerById(id, new RCallback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                HttpErrorCheck.checkResponse("客户详情-->", response);
                ownErId = customer.owner.id;
                isLock = customer.lock;
                mCustomer = customer;
                initData();
                Utils.dialogDismiss();

            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                Utils.dialogDismiss();
                finish();
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
        if (!mCustomer.lock) {
            img_public.setVisibility(View.VISIBLE);
        }

        /*判断是否有操作权限，来操作改客户信息
        * 本地userid与服务器回传ownerId比较，相等则是自己的客户，islock＝true为自己客户，false在公海中
        * 这里不是我的客户，也会返回到我的客户列表里面,接口应该出现问题
        * */

        isMyUser = MainApp.user.id.equals(ownErId) && isLock ? true : false;

        if (isMyUser) {
            img_title_right.setOnTouchListener(Global.GetTouch());
        } else {
            img_title_right.setVisibility(View.INVISIBLE);
        }

        img_title_left.setOnTouchListener(Global.GetTouch());
        layout_customer_info.setOnTouchListener(Global.GetTouch());
        img_public.setOnTouchListener(Global.GetTouch());
        layout_contact.setOnTouchListener(Global.GetTouch());
        layout_send_sms.setOnTouchListener(Global.GetTouch());
        layout_call.setOnTouchListener(Global.GetTouch());
        tv_sale_activity_date.setOnTouchListener(Global.GetTouch());
        layout_sale_activity.setOnTouchListener(Global.GetTouch());
        layout_visit.setOnTouchListener(Global.GetTouch());
        layout_purchase.setOnTouchListener(Global.GetTouch());
        layout_task.setOnTouchListener(Global.GetTouch());
        layout_attachment.setOnTouchListener(Global.GetTouch());

        tv_sale_activity_date.setText(app.df3.format(new Date(mCustomer.lastActAt * 1000)));
        tv_customer_name.setText(mCustomer.name);
        if (null != mCustomer.loc) {
            tv_address.setText(mCustomer.loc.addr);
        }
        tv_tags.setText(Utils.getTagItems(mCustomer));
        Contact contact = Utils.findDeault(mCustomer);
        if (null != contact) {
            tv_contact_name.setText(contact.getName());
            tv_contact_tel.setText(contact.getTel());
        }
        tv_visit_times.setText("(" + mCustomer.counter.getVisit() + ")");
        tv_purchase_count.setText("(" + mCustomer.counter.getDemand() + ")");
        tv_task_count.setText("(" + mCustomer.counter.getTask() + ")");
        tv_attachment_count.setText("(" + mCustomer.counter.getFile() + ")");

    }

    /**
     * 显示编辑客户弹出框
     */
    private void showEditPopu() {

        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View menuView = mLayoutInflater.inflate(R.layout.popu_child_task_edit_layout, null, false);
        menuView.getBackground().setAlpha(100);
        Button btn_child_delete_task = (Button) menuView.findViewById(R.id.btn_child_delete_task);
        Button btnCancel = (Button) menuView.findViewById(R.id.btn_cancel_edit);
        Button btnUpdate = (Button) menuView.findViewById(R.id.btn_child_add_update);

        btnUpdate.setText("投入公海");
        btn_child_delete_task.setText("删除");
        //btn_child_delete_task.setVisibility(View.GONE);

        btn_child_delete_task.setOnTouchListener(Global.GetTouch());
        btnCancel.setOnTouchListener(Global.GetTouch());
        btnUpdate.setOnTouchListener(Global.GetTouch());

        final PopupWindow popupWindow = new PopupWindow(menuView, -1, -1, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));// 响应键盘三个主键的必须步骤
        popupWindow.showAtLocation(findViewById(R.id.tv_title_1), Gravity.BOTTOM, 0, 0);

        menuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
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

        PopuOnClickListener(PopupWindow window) {
            mWindow = window;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_child_delete_task:
                    delete();
                    break;
                case R.id.btn_child_add_update:
                    ConfirmDialog("提示", "投入公海，相当于放弃此客户所有数据和管理权限，你确定要投入公海？", new ConfirmDialogInterface() {
                        @Override
                        public void Confirm() {
                            toPublic();
                        }
                    });
                    break;
            }
            mWindow.dismiss();
        }
    }

    /**
     * 删除客户
     */
    private void delete() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).delete(mCustomer.getId(), new RCallback<Customer>() {
            @Override
            public void success(Customer newCustomer, Response response) {
                app.finishActivity(CustomerDetailInfoActivity.this, BaseMainListFragment.REQUEST_REVIEW, RESULT_OK, new Intent());
            }

            @Override
            public void failure(RetrofitError error) {
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
            public void success(Customer newCustomer, Response response) {
                //getData();
                isPutOcen = true;
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_customer_info, R.id.img_public,
            R.id.layout_contact, R.id.layout_send_sms, R.id.layout_call, R.id.layout_sale_activity,
            R.id.layout_visit, R.id.layout_purchase, R.id.layout_task, R.id.layout_attachment})
    void onClick(View view) {
        Bundle bundle = new Bundle();
        Class<?> _class = null;
        int requestCode = -1;
        switch (view.getId()) {
            /*返回*/
            case R.id.img_title_left:
                Intent intent = new Intent();
                if (isPutOcen) {
                    app.finishActivity(this, BaseMainListFragment.REQUEST_REVIEW, RESULT_OK, intent);
                } else {
                    finish();
                }
                break;
            case R.id.img_title_right:
                showEditPopu();
                break;
            case R.id.layout_customer_info:
                bundle.putSerializable("Customer", mCustomer);
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putBoolean(ExtraAndResult.EXTRA_STATUS,mCustomer.lock);
                _class = CustomerInfoActivity_.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO;

                break;

            /*挑入*/
            case R.id.img_public:

                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).pickedIn(id, new RCallback<Customer>() {
                    @Override
                    public void success(Customer newCustomer, Response response) {
                        app.finishActivity(CustomerDetailInfoActivity.this, BaseMainListFragment.REQUEST_REVIEW, RESULT_OK, new Intent());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        finish();
                    }
                });


                break;
            /*联系人*/
            case R.id.layout_contact:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putSerializable("Customer", mCustomer);
                _class = CustomerContactManageActivity_.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_CONTACTS;
                break;
            case R.id.layout_send_sms:
                Utils.sendSms(this, mCustomer.contacts.get(0).getTel());
                break;
            case R.id.layout_call:
                Utils.call(this, mCustomer.contacts.get(0).getTel());
                break;
            /*跟进动态*/
            case R.id.layout_sale_activity:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putSerializable(Customer.class.getName(), mCustomer);
                _class = SaleActivitiesManageActivity.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_ACTIVITIS;
                break;
            /*拜访签到*/
            case R.id.layout_visit:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putSerializable("mCustomer", mCustomer);
                _class = SignInListActivity_.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_LEGWORKS;
                break;
            /*购买意向*/
            case R.id.layout_purchase:
                bundle.putBoolean("isMyUser", isMyUser);
                bundle.putString(ExtraAndResult.EXTRA_ID, mCustomer.getId());
                bundle.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
                _class = DemandsManageActivity.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_DEMANDS;
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
                bundle.putSerializable("goneBtn", 1);
                _class = AttachmentActivity_.class;
                requestCode = FinalVariables.REQUEST_DEAL_ATTACHMENT;
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

    private void goToChild(Bundle b, Class<?> _class, int requestCode) {
        app.startActivityForResult(this, _class, MainApp.ENTER_TYPE_RIGHT, requestCode, b);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            Intent intent = new Intent();
            if (isPutOcen) {
                app.finishActivity(this, BaseMainListFragment.REQUEST_REVIEW, RESULT_OK, intent);
            } else {
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || null == data) {
            return;
        }

        switch (requestCode) {
            case FinalVariables.REQUEST_PREVIEW_LEGWORKS:
            case FinalVariables.REQUEST_PREVIEW_DEMANDS:
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
            case FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO:
            case FinalVariables.REQUEST_PREVIEW_CUSTOMER_CONTACTS:
            case FinalVariables.REQUEST_PREVIEW_CUSTOMER_ACTIVITIS:
            case FinalVariables.REQUEST_PREVIEW_CUSTOMER_TASKS:
                getData();
                break;
        }
    }
}
