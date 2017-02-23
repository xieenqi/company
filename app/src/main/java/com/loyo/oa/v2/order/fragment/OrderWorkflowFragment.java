package com.loyo.oa.v2.order.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.order.adapter.WorkflowListAdapter;
import com.loyo.oa.v2.order.model.WorkflowModel;
import com.loyo.oa.v2.tool.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by EthanGong on 2017/2/23.
 */

public class OrderWorkflowFragment extends BaseFragment {

    public interface ActionListener {
        void onSelectWorkflowBack();
        void onSelectWorkflowConfirm(WorkflowModel model);
    }

    private WeakReference<OrderWorkflowFragment.ActionListener> listenerRef;

    View view;
    WorkflowListAdapter adapter;

    private ArrayList<WorkflowModel> data;

    @BindView(R.id.ll_back)  ViewGroup backButton;
    @BindView(R.id.tv_title) TextView titleView;
    @BindView(R.id.recycle_view) RecyclerView recycleView;

    @OnClick(R.id.ll_back) void onBackPressed() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onSelectWorkflowBack();
        }
    }

    @OnClick(R.id.btn_confirm) void onConfirm() {

        if (adapter.selectedIndex < 0 ||adapter.selectedIndex >= data.size()) {
            sweetAlertDialogView.alertIcon("提示", "请选择一个审批流程");
            return;
        }

        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onSelectWorkflowConfirm(data.get(adapter.selectedIndex));
        }
    }

    public OrderWorkflowFragment(OrderWorkflowFragment.ActionListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_workflow, container, false);
            ButterKnife.bind(this, view);
            titleView.setText("选择审批流程");
            setup();
        }
        return view;
    }

    private void setup() {
        adapter = new WorkflowListAdapter(data);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleView.setAdapter(adapter);
    }

    public void setData(ArrayList<WorkflowModel> data) {
        this.data = data;
    }
}
