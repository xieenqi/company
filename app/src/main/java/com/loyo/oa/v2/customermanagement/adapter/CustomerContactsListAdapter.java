package com.loyo.oa.v2.customermanagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.customermanagement.cell.ContactCardCell;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by EthanGong on 2017/2/13.
 */

public class CustomerContactsListAdapter extends RecyclerView.Adapter<ContactCardCell> {

    private ArrayList<Contact> data = new ArrayList<>();
    private WeakReference<ContactCardCell.OnContactCellActionListener> listenerRef;

    public CustomerContactsListAdapter(ContactCardCell.OnContactCellActionListener listener) {
        if (listener != null) {
            this.listenerRef = new WeakReference<>(listener);
        }

    }

    public void loadData(ArrayList<Contact> data) {
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    public void addData(ArrayList<Contact> data) {
        this.data.addAll(data);
    }

    @Override
    public ContactCardCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return ContactCardCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(ContactCardCell holder, int position) {
        holder.loadContact(data.get(position), position);
        holder.listenerRef = listenerRef;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
