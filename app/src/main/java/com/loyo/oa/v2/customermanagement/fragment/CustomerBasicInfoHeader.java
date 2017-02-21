package com.loyo.oa.v2.customermanagement.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by EthanGong on 2017/2/20.
 */

public class CustomerBasicInfoHeader extends Fragment {

    public interface CustomerBasicInfoHeaderListener {
        void onShowInfo();
        void onEditState();
        void onEditTag();
        void onDropDeadline();
    }

    WeakReference<CustomerBasicInfoHeaderListener> listenerRef;
    View view;

    Customer customer;
    boolean canEdit;

    @BindView(R.id.customer_basic_info) ViewGroup basicInfoView;
    @BindView(R.id.customer_state) ViewGroup customerStateView;
    @BindView(R.id.customer_tag) ViewGroup customerTagView;

    @BindView(R.id.tv_customer_name) TextView customerNameText;
    @BindView(R.id.tv_customer_state) TextView customerStateText;
    @BindView(R.id.tv_customer_tag) TextView customerTagText;
    @BindView(R.id.tv_drop_reason) TextView dropReasonText;

    @BindView(R.id.ll_warn) ViewGroup warnView;

    @BindView(R.id.state_editable) ImageView stateEditableVew;
    @BindView(R.id.tag_editable) ImageView tagEditableVew;
    @BindView(R.id.tv_recycleRemind) TextView recycleRemindText;

    @OnClick(R.id.customer_basic_info)
    void showInfo() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onShowInfo();
        }
    }

    @OnClick(R.id.customer_state)
    void editState() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onEditState();
        }
    }

    @OnClick(R.id.customer_tag)
    void editTag() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onEditTag();
        }
    }

    @OnClick(R.id.ll_warn)
    void onDropDeadline() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onDropDeadline();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(
                    R.layout.fragment_customer_basic_info_header, container, false);

            ButterKnife.bind(this, view);
            setup();
        }
        return view;
    }

    void setup() {
        basicInfoView.setOnTouchListener(Global.GetTouch());
        customerStateView.setOnTouchListener(Global.GetTouch());
        customerTagView.setOnTouchListener(Global.GetTouch());
        warnView.setOnTouchListener(Global.GetTouch());
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        canEdit = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                        customer.state, CustomerAction.EDIT);
    }

    public void setListenerRef(CustomerBasicInfoHeaderListener listener) {
        if (listener != null) {
            this.listenerRef = new WeakReference<>(listener);
        }
    }

    public void loadCustomer() {
        this.customerNameText.setText(customer.name);
        this.customerStateText.setText("状态：" + customer.statusName);
        this.customerTagText.setText("标签：" + customer.displayTagString());
        if (customer.state == Customer.DumpedCustomer) {
            dropReasonText.setVisibility(View.VISIBLE);
            String recycleReason = customer.recycleReason;
            if (TextUtils.isEmpty(recycleReason)) {
                recycleReason = "无";
            }
            dropReasonText.setText("丢公海原因：" + recycleReason);
        } else {
            dropReasonText.setVisibility(View.GONE);
        }
        if (customer.hasDropRemind()) {
            warnView.setVisibility(View.VISIBLE);
            recycleRemindText.setText(customer.getFormattedDropRemind());
        } else {
            warnView.setVisibility(View.GONE);

        }
        if (canEdit) {
            stateEditableVew.setVisibility(View.VISIBLE);
            tagEditableVew.setVisibility(View.VISIBLE);
            customerStateView.setClickable(true);
            customerTagView.setClickable(true);
        } else {
            stateEditableVew.setVisibility(View.GONE);
            tagEditableVew.setVisibility(View.GONE);
            customerStateView.setClickable(false);
            customerTagView.setClickable(false);
        }
    }
}
