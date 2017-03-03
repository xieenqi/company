package com.loyo.oa.v2.order.cell;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.order.model.WorkflowModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by EthanGong on 2017/2/23.
 */

public class WorkflowCell extends RecyclerView.ViewHolder {

    WorkflowModel model;

    @BindView(R.id.tv_title)       TextView titleText;
    @BindView(R.id.tv_description) TextView descriptionText;
    @BindView(R.id.check_box)      CheckBox checkBox;

    public static WorkflowCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_workflow, parent, false);
        return new WorkflowCell(itemView);
    }

    private WorkflowCell(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void loadData(WorkflowModel model) {
        this.model = model;
        titleText.setText(model.title);
        descriptionText.setText(model.getDescription());
        checkBox.setChecked(model.isChecked);
        descriptionText.setVisibility(model.isChecked ? View.VISIBLE : View.GONE);
    }
}
