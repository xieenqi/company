package com.loyo.oa.v2.customermanagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.customermanagement.cell.AttachmentCell;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by EthanGong on 2017/2/13.
 */

public class CustomerAttachmentsListAdapter extends RecyclerView.Adapter<AttachmentCell> {


    private ArrayList<Attachment> data = new ArrayList<>();

    private WeakReference<AttachmentCell.OnAttachmentCellListener> listenerRef;

    public CustomerAttachmentsListAdapter(AttachmentCell.OnAttachmentCellListener listener) {
        if (listener != null) {
            this.listenerRef = new WeakReference<>(listener);
        }

    }

    public void loadData(ArrayList<Attachment> data) {
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    public void addData(ArrayList<Attachment> data) {
        this.data.addAll(data);
    }

    @Override
    public AttachmentCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return AttachmentCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(AttachmentCell holder, int position) {
        holder.loadAttachment(data.get(position), position);
        holder.listenerRef = listenerRef;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
