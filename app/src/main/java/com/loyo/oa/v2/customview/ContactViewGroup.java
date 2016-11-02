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

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerContactManageActivity;
import com.loyo.oa.v2.activityui.customer.CustomerContractAddActivity;
import com.loyo.oa.v2.activityui.customer.CustomerInfoActivity;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
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

        void onCallBack(String callNum, String id, String name, int callType);

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
     */
    public void paymentSet(final String phone, final int callType) {

        boolean checkTag = false;
        if (callType == 0) {
            checkTag = RegularCheck.isYunPhone(phone);
        } else {
            checkTag = RegularCheck.isYunTell(phone);
        }

        final CallPhonePopView callPhonePopView = new CallPhonePopView(context, mContact.getName(), checkTag);
        callPhonePopView.show();
        callPhonePopView.setCanceledOnTouchOutside(true);
        /*商务电话*/
        callPhonePopView.businessPhone(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contactProcessCallback.onCallBack(phone.replaceAll(" +", ""), mContact.getId(), mContact.getName().trim().toString(), callType);
                callPhonePopView.dismiss();
            }
        });
        /*普通电话*/
        callPhonePopView.commonlyPhone(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callType == 0) {
                    if (RegularCheck.isMobilePhone(phone)) {
                        Utils.call(context, phone);
                    } else {
                        contactProcessCallback.onPhoneError();
                    }
                } else {
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
     */
    public void isMobile(String phone, int callType) {
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
            }, "提示", context.getString(R.string.app_homeqq_message));
        } else {
            paymentSet(phone, callType);
        }
    }

    /**
     * 绑定打座机
     * */
    private void setWriteOnClick(LinearLayout callLayout, final String telNum){
        /*拨打座机*/
        callLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mContact.getWiretel() || mContact.getWiretel().isEmpty()) {
                    contactProcessCallback.onPhoneError();
                    return;
                }
                    /*暂时注销座机号验证
                    if(!RegularCheck.isPhoneNumberValid(mContact.getWiretel())){
                        contactProcessCallback.onPhoneError();
                        return;
                    }*/
                isMobile(telNum, 1);
            }
        });
    }

    /**
     * 绑定打电话,发短信监听
     */
    private void setTelOnClick(LinearLayout callLayout, LinearLayout smgLayout, final String telNum) {

        /*拨打手机*/
        callLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == telNum || TextUtils.isEmpty(telNum)) {
                    contactProcessCallback.onPhoneError();
                    return;
                }
                    /*if(!RegularCheck.isMobilePhone(mContact.getTel().replaceAll(" +",""))){
                        contactProcessCallback.onPhoneError();
                        return;
                    }*/

                isMobile(telNum, 0);
            }
        });

        /*发送短信*/
        smgLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.sendSms(context, telNum);
            }
        });
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

            /*手机ui*/
            LinearLayout layout_phone1 = (LinearLayout) findViewById(R.id.layout_phone1);
            LinearLayout layout_phone2 = (LinearLayout) findViewById(R.id.layout_phone2);
            LinearLayout layout_phone3 = (LinearLayout) findViewById(R.id.layout_phone3);

            TextView tv_phone_val1 = (TextView) findViewById(R.id.tv_phone_val1);
            TextView tv_phone_val2 = (TextView) findViewById(R.id.tv_phone_val2);
            TextView tv_phone_val3 = (TextView) findViewById(R.id.tv_phone_val3);

            TextView tv_phone_name1 = (TextView) findViewById(R.id.tv_phone_name1);
            TextView tv_phone_name2 = (TextView) findViewById(R.id.tv_phone_name2);
            TextView tv_phone_name3 = (TextView) findViewById(R.id.tv_phone_name3);

            LinearLayout layout_send_sms1 = (LinearLayout) findViewById(R.id.layout_send_sms1);
            LinearLayout layout_send_sms2 = (LinearLayout) findViewById(R.id.layout_send_sms2);
            LinearLayout layout_send_sms3 = (LinearLayout) findViewById(R.id.layout_send_sms3);

            LinearLayout layout_phone_call1 = (LinearLayout) findViewById(R.id.layout_phone_call1);
            LinearLayout layout_phone_call2 = (LinearLayout) findViewById(R.id.layout_phone_call2);
            LinearLayout layout_phone_call3 = (LinearLayout) findViewById(R.id.layout_phone_call3);

            /*座机ui*/
            LinearLayout layout_wiretel1 = (LinearLayout) findViewById(R.id.layout_wiretel1);
            LinearLayout layout_wiretel2 = (LinearLayout) findViewById(R.id.layout_wiretel2);
            LinearLayout layout_wiretel3 = (LinearLayout) findViewById(R.id.layout_wiretel3);

            TextView tv_wiletel_name1 = (TextView) findViewById(R.id.tv_wiletel_name1);
            TextView tv_wiletel_name2 = (TextView) findViewById(R.id.tv_wiletel_name2);
            TextView tv_wiletel_name3 = (TextView) findViewById(R.id.tv_wiletel_name3);

            TextView tv_wiletel_val1 = (TextView) findViewById(R.id.tv_wiletel_val1);
            TextView tv_wiletel_val2 = (TextView) findViewById(R.id.tv_wiletel_val2);
            TextView tv_wiletel_val3 = (TextView) findViewById(R.id.tv_wiletel_val3);

            LinearLayout layout_call_wiretel1 = (LinearLayout) findViewById(R.id.layout_call_wiretel1);
            LinearLayout layout_call_wiretel2 = (LinearLayout) findViewById(R.id.layout_call_wiretel2);
            LinearLayout layout_call_wiretel3 = (LinearLayout) findViewById(R.id.layout_call_wiretel3);


            TextView tv_name = (TextView) findViewById(R.id.tv_name);
            TextView tv_qq = (TextView) findViewById(R.id.tv_qq);
            TextView tv_birthday = (TextView) findViewById(R.id.tv_birthday);
            TextView tv_wx = (TextView) findViewById(R.id.tv_wx);
            TextView tv_email = (TextView) findViewById(R.id.tv_email);
            TextView tv_memo = (TextView) findViewById(R.id.tv_memo);
            TextView tv_depart = (TextView) findViewById(R.id.tv_depart);


            tv_name.setText(mContact.getName());
            tv_phone_val1.setText(mContact.getTel());       //兼容老数据
            tv_wiletel_val1.setText(mContact.getWiretel()); //兼容老数据
            tv_qq.setText(mContact.getQq());
            tv_wx.setText(mContact.getWx());
            tv_email.setText(mContact.getEmail());
            tv_memo.setText(mContact.getMemo());
            tv_birthday.setText(mContact.getBirthStr());
            tv_depart.setText(mContact.deptName);

            /*绑定手机号数据*/
            if(null != mContact.telGroup){
                switch (mContact.telGroup.size()) {

                    case 1:
                        tv_phone_val1.setText(mContact.telGroup.get(0));
                        setTelOnClick(layout_phone_call1, layout_send_sms1, mContact.telGroup.get(0));
                        break;

                    case 2:
                        layout_phone2.setVisibility(View.VISIBLE);
                        tv_phone_name1.setText("手机1");
                        tv_phone_name2.setText("手机2");
                        tv_phone_val1.setText(mContact.telGroup.get(0));
                        tv_phone_val2.setText(mContact.telGroup.get(1));
                        setTelOnClick(layout_phone_call1, layout_send_sms1, mContact.telGroup.get(0));
                        setTelOnClick(layout_phone_call2, layout_send_sms2, mContact.telGroup.get(1));
                        break;

                    case 3:
                        layout_phone2.setVisibility(View.VISIBLE);
                        layout_phone3.setVisibility(View.VISIBLE);
                        tv_phone_name1.setText("手机1");
                        tv_phone_name2.setText("手机2");
                        tv_phone_name3.setText("手机3");
                        tv_phone_val1.setText(mContact.telGroup.get(0));
                        tv_phone_val2.setText(mContact.telGroup.get(1));
                        tv_phone_val3.setText(mContact.telGroup.get(2));
                        setTelOnClick(layout_phone_call1, layout_send_sms1, mContact.telGroup.get(0));
                        setTelOnClick(layout_phone_call2, layout_send_sms2, mContact.telGroup.get(1));
                        setTelOnClick(layout_phone_call3, layout_send_sms3, mContact.telGroup.get(2));
                        break;

                }
            }


             /*绑定座机号数据*/
            if(null != mContact.wiretelGroup){
                switch (mContact.wiretelGroup.size()) {

                    case 1:
                        tv_wiletel_val1.setText(mContact.wiretelGroup.get(0));
                        setWriteOnClick(layout_call_wiretel1, mContact.wiretelGroup.get(0));
                        break;

                    case 2:
                        layout_wiretel2.setVisibility(View.VISIBLE);
                        tv_wiletel_name1.setText("座机1");
                        tv_wiletel_name2.setText("座机2");
                        tv_wiletel_val1.setText(mContact.wiretelGroup.get(0));
                        tv_wiletel_val2.setText(mContact.wiretelGroup.get(1));

                        setWriteOnClick(layout_call_wiretel1, mContact.wiretelGroup.get(0));
                        setWriteOnClick(layout_call_wiretel2, mContact.wiretelGroup.get(1));
                        break;

                    case 3:
                        layout_wiretel2.setVisibility(View.VISIBLE);
                        layout_wiretel3.setVisibility(View.VISIBLE);
                        tv_wiletel_name1.setText("座机1");
                        tv_wiletel_name2.setText("座机2");
                        tv_wiletel_name3.setText("座机3");
                        tv_wiletel_val1.setText(mContact.wiretelGroup.get(0));
                        tv_wiletel_val2.setText(mContact.wiretelGroup.get(1));
                        tv_wiletel_val3.setText(mContact.wiretelGroup.get(2));

                        setWriteOnClick(layout_call_wiretel1, mContact.wiretelGroup.get(0));
                        setWriteOnClick(layout_call_wiretel2, mContact.wiretelGroup.get(1));
                        setWriteOnClick(layout_call_wiretel3, mContact.wiretelGroup.get(2));
                        break;

                }
            }


            if (mContact.isDefault()) {
                default_.setImageResource(R.drawable.icon_contact_default_selected);
                del.setVisibility(INVISIBLE);
            }

            /*编辑联系人*/
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
        addView(new ContactListExtra(context, mContact.getExtDatas(), leftExtrases, false, 14));

        //加载子条目
        parent.addView(this);
    }
}
