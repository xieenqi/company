package com.loyo.oa.v2.customermanagement.fragment;

import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.tool.BaseFragment;

import java.lang.ref.WeakReference;

/**
 * Created by EthanGong on 2017/2/10.
 */

public class CustomerChildFragment extends BaseFragment {

    public interface OnTotalCountChangeListener {
        void onTotalCountChange(CustomerChildFragment fragment, int index);
        void onDropRemindRefresh(CustomerChildFragment fragment);
    }
    public int index;
    WeakReference<OnTotalCountChangeListener> listenerRef;

    int totalCount;

    String title = "";
    Customer customer;

    public CustomerChildFragment(Customer customer, int index, OnTotalCountChangeListener listener, String title) {
        setCustomer(customer);
        this.index = index;
        setListener(listener);
        this.title = title;
    }

    public String getTitle() {
        return String.format("%s %d", title, totalCount);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void reloadWithCustomer(Customer customer) {

    }

    public void setListener(OnTotalCountChangeListener listener) {
        if (listener != null) {
            listenerRef = new WeakReference<>(listener);
        }
    }

    void notifyTotalCountChange() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onTotalCountChange(this, index);
        }
    }

    void notifyDropRemindRefresh() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onDropRemindRefresh(this);
        }
    }
}
