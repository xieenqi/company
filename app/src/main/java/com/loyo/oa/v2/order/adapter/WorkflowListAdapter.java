package com.loyo.oa.v2.order.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.order.cell.WorkflowCell;
import com.loyo.oa.v2.order.model.WorkflowModel;

import java.util.ArrayList;

/**
 * Created by EthanGong on 2017/2/23.
 */

public class WorkflowListAdapter extends RecyclerView.Adapter<WorkflowCell> {

    private ArrayList<WorkflowModel> data = new ArrayList<>();
    public int selectedIndex = -1;

    public WorkflowListAdapter(ArrayList<WorkflowModel> data) {
        this.data.addAll(data);
    }

    @Override
    public WorkflowCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return WorkflowCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(WorkflowCell holder, final int position) {
        WorkflowModel model = data.get(position);
        holder.loadData(model);
        if (model.isChecked) {
            selectedIndex = position;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int preIndex = selectedIndex;
                selectedIndex = position;
                WorkflowModel prev;
                if (preIndex >= 0 && preIndex < data.size()) {
                    prev = data.get(preIndex);
                    prev.isChecked = false;
                    WorkflowListAdapter.this.notifyItemChanged(preIndex);
                }
                WorkflowModel cur = data.get(selectedIndex);
                cur.isChecked = true;
                WorkflowListAdapter.this.notifyItemChanged(selectedIndex);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
