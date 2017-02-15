package com.loyo.oa.v2.customermanagement.cell;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Contact;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by EthanGong on 2017/2/13.
 */

public class ContactCardCell extends RecyclerView.ViewHolder {

    public interface OnContactCellActionListener {
        void onSMS(String phone);
        void onCallPhone(Contact contact, String phone);
        void onCallTel(Contact contact, String tel);
        void onSetDefaultContact(Contact contact, int contactIndex);
        void onContactSelect(Contact contact, int contactIndex);
    }

    private Contact contact;
    private ArrayList<ViewGroup> phoneContainers;
    private ArrayList<ViewGroup> telContainers;
    private ArrayList<TextView> phoneTexts;
    private ArrayList<TextView> telTexts;
    private int contactIndex;
    public WeakReference<OnContactCellActionListener> listenerRef;

    @BindView(R.id.tv_name) TextView nameText;
    @BindView(R.id.default_contact_view) ImageView actionView;
    @BindView(R.id.layout_phone1) ViewGroup phoneContainer1;
    @BindView(R.id.layout_phone2) ViewGroup phoneContainer2;
    @BindView(R.id.layout_phone3) ViewGroup phoneContainer3;
    @BindView(R.id.layout_wiretel1) ViewGroup telContainer1;
    @BindView(R.id.layout_wiretel2) ViewGroup telContainer2;
    @BindView(R.id.layout_wiretel3) ViewGroup telContainer3;

    @BindView(R.id.tv_phone_val1)   TextView phoneText1;
    @BindView(R.id.tv_phone_val2)   TextView phoneText2;
    @BindView(R.id.tv_phone_val3)   TextView phoneText3;
    @BindView(R.id.tv_wiletel_val1) TextView telText1;
    @BindView(R.id.tv_wiletel_val2) TextView telText2;
    @BindView(R.id.tv_wiletel_val3) TextView telText3;

    @OnClick(R.id.contact_card) void onSelect() {
        if (getListener() != null) {
            getListener().onContactSelect(contact, contactIndex);
        }
    }

    @OnClick(R.id.default_contact_view) void setDefaultContact() {
        if (getListener() != null) {
            getListener().onSetDefaultContact(contact, contactIndex);
        }
    }

    @OnClick(R.id.layout_send_sms1) void onSMS1() {
        if (getListener() != null && contact.telGroup.size() > 0) {
            getListener().onSMS(contact.telGroup.get(0));
        }
    }
    @OnClick(R.id.layout_send_sms2) void onSMS2() {
        if (getListener() != null && contact.telGroup.size() > 1) {
            getListener().onSMS(contact.telGroup.get(1));
        }
    }
    @OnClick(R.id.layout_send_sms3) void onSMS3() {
        if (getListener() != null && contact.telGroup.size() > 2) {
            getListener().onSMS(contact.telGroup.get(2));
        }
    }

    @OnClick(R.id.layout_phone_call1) void onPhoneCall1() {
        if (getListener() != null && contact.telGroup.size() > 0) {
            getListener().onCallPhone(contact, contact.telGroup.get(0));
        }
    }
    @OnClick(R.id.layout_phone_call2) void onPhoneCall2() {
        if (getListener() != null && contact.telGroup.size() > 1) {
            getListener().onCallPhone(contact, contact.telGroup.get(1));
        }
    }
    @OnClick(R.id.layout_phone_call3) void onPhoneCall3() {
        if (getListener() != null && contact.telGroup.size() > 2) {
            getListener().onCallPhone(contact, contact.telGroup.get(2));
        }
    }

    @OnClick(R.id.layout_call_wiretel1) void onTelCall1() {
        if (getListener() != null && contact.wiretelGroup.size() > 0) {
            getListener().onCallTel(contact, contact.wiretelGroup.get(0));
        }
    }
    @OnClick(R.id.layout_call_wiretel2) void onTelCall2() {
        if (getListener() != null && contact.wiretelGroup.size() > 1) {
            getListener().onCallTel(contact, contact.wiretelGroup.get(1));
        }
    }
    @OnClick(R.id.layout_call_wiretel3) void onTelCall3() {
        if (getListener() != null  && contact.wiretelGroup.size() > 2) {
            getListener().onCallTel(contact, contact.wiretelGroup.get(2));
        }
    }

    public OnContactCellActionListener getListener() {
        if (listenerRef != null) {
            return listenerRef.get();
        }
        return null;
    }

    public static ContactCardCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_customer_contact_card, parent, false);
        ContactCardCell cell = new ContactCardCell(itemView);
        return cell;
    }

    public void loadContact(Contact contact, int contactIndex) {
        contact.compatTransform();
        this.contact = contact;
        this.contactIndex = contactIndex;
        nameText.setText(contact.getName() +
                (TextUtils.isEmpty(contact.getContactRoleName())?"":"-"+contact.getContactRoleName()));
        actionView.setImageResource(contact.isDefault()?
                R.drawable.icon_default_sel:R.drawable.icon_default);
        for (int i = 0; i < 3; i++) {
            if (i < contact.telGroup.size()) {
                phoneContainers.get(i).setVisibility(View.VISIBLE);
                phoneTexts.get(i).setText(contact.telGroup.get(i));
            }
            else {
                phoneContainers.get(i).setVisibility(i == 0?View.VISIBLE:View.GONE);
            }

            if (i < contact.wiretelGroup.size()) {
                telContainers.get(i).setVisibility(View.VISIBLE);
                telTexts.get(i).setText(contact.wiretelGroup.get(i));
            }
            else {
                telContainers.get(i).setVisibility(i == 0?View.VISIBLE:View.GONE);
            }
        }

    }

    private ContactCardCell(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        phoneContainers = new ArrayList<>();
        phoneContainers.add(phoneContainer1);
        phoneContainers.add(phoneContainer2);
        phoneContainers.add(phoneContainer3);

        telContainers = new ArrayList<>();
        telContainers.add(telContainer1);
        telContainers.add(telContainer2);
        telContainers.add(telContainer3);

        phoneTexts = new ArrayList<>();
        phoneTexts.add(phoneText1);
        phoneTexts.add(phoneText2);
        phoneTexts.add(phoneText3);

        telTexts = new ArrayList<>();
        telTexts.add(telText1);
        telTexts.add(telText2);
        telTexts.add(telText3);
    }

}
