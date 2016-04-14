package com.loyo.oa.v2.tool.customview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.customer.CustomerContactManageActivity;
import com.loyo.oa.v2.activity.customer.CustomerContractAddActivity;
import com.loyo.oa.v2.activity.customer.CustomerInfoActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.tool.Utils;

/**
 * com.loyo.oa.v2.tool.customview
 * 描述 :客户联系人 非动态字段 信息详情条目
 * 作者 : ykb
 * 时间 : 15/9/24.
 */
public class ContactViewGroup extends LinearLayout {

    public interface OnContactProcessCallback {
        void onDel(Contact contact);
        void onSetDefault(Contact contact);
    }

    private Context context;
    private Contact mContact;
    private MainApp app = MainApp.getMainApp();
    private Customer mCustomer;
    private OnContactProcessCallback contactProcessCallback;

    private ContactViewGroup(Context c) {
        super(c);
        context = c;
    }

    public ContactViewGroup(Context _context, Customer customer, Contact contact, OnContactProcessCallback callback) {
        this(_context);
        setBackgroundColor(getResources().getColor(R.color.white));
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        setOrientation(LinearLayout.VERTICAL);
        contactProcessCallback = callback;
        mCustomer = customer;
        mContact = contact;
    }

    /**
     * 绑定视图
     *
     * @param id     视图id
     * @param parent 视图父容器
     */
    public void bindView(final int id, final ViewGroup parent, boolean isMyUser, boolean isMber) {
        setId(id);
        LayoutInflater inflater = LayoutInflater.from(context);

        if (getId() > 1) {
            View view = new View(context);
            view.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
            view.setLayoutParams(new ViewGroup.LayoutParams(-1, app.spTopx(10)));
            addView(view);
        }

        //加载删除条目
        if (getId() > 0) {
            inflater.inflate(R.layout.item_customer_contact_edit, this, true);
            final TextView title = (TextView) findViewById(R.id.tv_title);
            final ImageView del = (ImageView) findViewById(R.id.img_del);
            final ImageView default_ = (ImageView) findViewById(R.id.img_default);
            final ImageView edit = (ImageView) findViewById(R.id.img_edit);

            /*判断是否有操作权限*/
            if (!isMyUser) {
                edit.setVisibility(View.GONE);
                del.setVisibility(View.GONE);
                default_.setVisibility(View.GONE);
            }

            ViewGroup call = (ViewGroup) findViewById(R.id.layout_call);
            ViewGroup callwire = (ViewGroup) findViewById(R.id.layout_call_wiretel);
            ViewGroup sendsms = (ViewGroup) findViewById(R.id.layout_send_sms);

            call.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.call(context, mContact.getTel());
                }
            });
            callwire.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.call(context, mContact.getWiretel());
                }
            });

            sendsms.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.sendSms(context, mContact.getTel());
                }
            });

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

            //编辑联系人
            edit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putSerializable("customer", mCustomer);
                    b.putSerializable("contract", mContact);
                    app.startActivityForResult((CustomerContactManageActivity) context, CustomerContractAddActivity.class, MainApp.ENTER_TYPE_RIGHT, CustomerInfoActivity.REQUEST_CUSTOMER_UPDATE_CONTRACT, b);
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

        addView(new ContactInfoExtraData(context, mContact.getExtDatas(), false, R.color.diseditable, 14));
        //加载子条目
        parent.addView(this);
    }
}
