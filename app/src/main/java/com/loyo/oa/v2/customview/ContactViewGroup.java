package com.loyo.oa.v2.customview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerContactManageActivity;
import com.loyo.oa.v2.activityui.customer.CustomerContractAddActivity;
import com.loyo.oa.v2.activityui.customer.CustomerInfoActivity;
import com.loyo.oa.v2.activityui.customer.bean.Contact;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.activityui.setting.EditUserMobileActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.RegularCheck;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * com.loyo.oa.v2.customview
 * 描述 :客户联系人 非动态字段 信息详情条目
 * 作者 : ykb
 * 时间 : 15/9/24.
 */
public class ContactViewGroup extends LinearLayout {

    public interface OnContactProcessCallback {
        void onDel(Contact contact);

        void onSetDefault(Contact contact);

        void onCallBack(String callNum, String id, String name,int callType);

        void onPhoneError();
    }

    private Context context;
    private Contact mContact;
    private MainApp app = MainApp.getMainApp();
    private Customer mCustomer;
    private ArrayList<ContactLeftExtras> leftExtrases;//左侧lable数据
    private OnContactProcessCallback contactProcessCallback;

    private ContactViewGroup(Context c) {
        super(c);
        context = c;
    }

    public ContactViewGroup(Context _context, Customer customer, ArrayList<ContactLeftExtras> leftExtrases, Contact contact, OnContactProcessCallback callback) {
        this(_context);
        setBackgroundColor(getResources().getColor(R.color.white));
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        setOrientation(LinearLayout.VERTICAL);
        contactProcessCallback = callback;
        mCustomer = customer;
        mContact = contact;
        this.leftExtrases = leftExtrases;
    }

    /**
     * 手机拨打弹出框
     * */
    public void paymentSet(final String phone,final int callType) {

        boolean checkTag = false;
        if(callType == 0){
            checkTag = RegularCheck.isYunPhone(phone);
        }else{
            checkTag = RegularCheck.isYunTell(phone);
        }

        final CallPhonePopView callPhonePopView = new CallPhonePopView(context,mContact.getName(),checkTag);
        callPhonePopView.show();
        callPhonePopView.setCanceledOnTouchOutside(true);
        /*商务电话*/
        callPhonePopView.businessPhone(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contactProcessCallback.onCallBack(phone.replaceAll(" +",""), mContact.getId(), mContact.getName().trim().toString(),callType);
                callPhonePopView.dismiss();
            }
        });
        /*普通电话*/
        callPhonePopView.commonlyPhone(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(callType == 0){
                    if(RegularCheck.isMobilePhone(phone)){
                        Utils.call(context, phone);
                    }else{
                        contactProcessCallback.onPhoneError();
                    }
                }else{
                    Utils.call(context, phone);
                }

                callPhonePopView.dismiss();
            }
        });
        callPhonePopView.cancelPhone(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhonePopView.dismiss();
            }
        });
    }

    /**
     * 判断本账号是否有电话
     * */
    public void isMobile(String phone,int callType){
        if (null == MainApp.user.mobile || TextUtils.isEmpty(MainApp.user.mobile)) {
            final SweetAlertDialogView sweetAlertDialogView = new SweetAlertDialogView(context);
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                    MainApp.getMainApp().startActivity((Activity) context, EditUserMobileActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                }
            },"提示",context.getString(R.string.app_homeqq_message));
        }else{
            paymentSet(phone,callType);
        }
    }


    /**
     * 绑定视图
     *
     * @param id     视图id
     * @param parent 视图父容器
     */
    public void bindView(final int id, final ViewGroup parent, boolean isMyUser, boolean isMber, boolean isRoot, boolean isLock) {
        setId(id);
        LayoutInflater inflater = LayoutInflater.from(context);

        if (getId() > 1) {
            View view = new View(context);
            view.setBackgroundColor(getResources().getColor(R.color.activity_bg));
            view.setLayoutParams(new ViewGroup.LayoutParams(-1, app.spTopx(15)));
            addView(view);
        }

        //加载删除条目
        if (getId() > 0) {
            inflater.inflate(R.layout.item_customer_contact_edit, this, true);
            final TextView title = (TextView) findViewById(R.id.tv_title);
            final ImageView del = (ImageView) findViewById(R.id.img_del);
            final ImageView default_ = (ImageView) findViewById(R.id.img_default);
            final ImageView edit = (ImageView) findViewById(R.id.img_edit);

            /*是否为公海客户*/
            if (!isLock) {
                edit.setVisibility(View.GONE);
                del.setVisibility(View.GONE);
                default_.setVisibility(View.GONE);
                /*判断是否有操作权限*/
            } else if (!isMyUser || isMber) {
                if (!isRoot) {
                    edit.setVisibility(View.GONE);
                    del.setVisibility(View.GONE);
                    default_.setVisibility(View.GONE);
                }
            }

            ViewGroup call = (ViewGroup) findViewById(R.id.layout_call);
            ViewGroup callwire = (ViewGroup) findViewById(R.id.layout_call_wiretel);
            ViewGroup sendsms = (ViewGroup) findViewById(R.id.layout_send_sms);

            TextView tv_name = (TextView) findViewById(R.id.tv_name);
            TextView tv_tel = (TextView) findViewById(R.id.tv_phone);
            TextView tv_wiletel = (TextView) findViewById(R.id.tv_wiletel);
            TextView tv_qq = (TextView) findViewById(R.id.tv_qq);
            TextView tv_birthday = (TextView) findViewById(R.id.tv_birthday);
            TextView tv_wx = (TextView) findViewById(R.id.tv_wx);
            TextView tv_email = (TextView) findViewById(R.id.tv_email);
            TextView tv_memo = (TextView) findViewById(R.id.tv_memo);
            TextView tv_depart = (TextView) findViewById(R.id.tv_depart);

            tv_name.setText(mContact.getName());
            tv_tel.setText(mContact.getTel());
            tv_wiletel.setText(mContact.getWiretel());
            tv_qq.setText(mContact.getQq());
            tv_wx.setText(mContact.getWx());
            tv_email.setText(mContact.getEmail());
            tv_memo.setText(mContact.getMemo());
            tv_birthday.setText(mContact.getBirthStr());

            tv_depart.setText(mContact.deptName);
            if (mContact.isDefault()) {
                default_.setImageResource(R.drawable.icon_contact_default_selected);
                del.setVisibility(INVISIBLE);
            }

            /*拨打手机*/
            call.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null == mContact.getTel() || mContact.getTel().isEmpty()){
                        contactProcessCallback.onPhoneError();
                        return;
                    }
                    /*if(!RegularCheck.isMobilePhone(mContact.getTel().replaceAll(" +",""))){
                        contactProcessCallback.onPhoneError();
                        return;
                    }*/
                    isMobile(mContact.getTel(),0);
                }
            });

            /*拨打座机*/
            callwire.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null == mContact.getWiretel() || mContact.getWiretel().isEmpty()){
                        contactProcessCallback.onPhoneError();
                        return;
                    }
                    /*暂时注销座机号验证
                    if(!RegularCheck.isPhoneNumberValid(mContact.getWiretel())){
                        contactProcessCallback.onPhoneError();
                        return;
                    }*/
                    isMobile(mContact.getWiretel(),1);
                }
            });

            sendsms.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.sendSms(context, mContact.getTel());
                }
            });

            //编辑联系人
            edit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putSerializable("customer", mCustomer);
                    b.putSerializable("contract", mContact);
                    app.startActivityForResult((CustomerContactManageActivity) context, CustomerContractAddActivity.class, MainApp.ENTER_TYPE_RIGHT,
                            CustomerInfoActivity.REQUEST_CUSTOMER_UPDATE_CONTRACT, b);
                }
            });

            /*设置默认联系人*/
            default_.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != contactProcessCallback && !mContact.isDefault()) {
                        contactProcessCallback.onSetDefault(mContact);
                    }
                }
            });

            title.setText("联系人" + getId());

            /*删除条目*/
            del.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != contactProcessCallback) {
                        contactProcessCallback.onDel(mContact);
                    }
                }
            });
        }


        //添加动态字段
        addView(new ContactListExtra(context, mContact.getExtDatas(), leftExtrases, false, R.color.text99, 14));

        //加载子条目
        parent.addView(this);
    }
}
