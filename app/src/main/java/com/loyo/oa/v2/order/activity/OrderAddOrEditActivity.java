package com.loyo.oa.v2.order.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.order.fragment.OrderFieldsFragment;
import com.loyo.oa.v2.order.fragment.OrderWorkflowFragment;
import com.loyo.oa.v2.order.model.WorkflowModel;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EthanGong on 2017/2/22.
 */

public class OrderAddOrEditActivity extends BaseActivity
        implements OrderFieldsFragment.ActionListener, OrderWorkflowFragment.ActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add_or_edit);
        FragmentManager fm = getSupportFragmentManager();
        OrderFieldsFragment fragment = new OrderFieldsFragment(this);
        fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * OrderFieldsFragment.ActionListener
     */

    @Override
    public void onCommit(Object object) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 400 /* 400 订单  500：回款 */);
        OrderService.getWorkflows(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<WorkflowModel>>() {
                    @Override
                    public void onNext(ArrayList<WorkflowModel> workflowModels) {
                        Log.v("o", workflowModels.toString());
                        if (workflowModels == null || workflowModels.size() <= 1) {

                        }
                        else {
                            OrderWorkflowFragment fragment =
                                    new OrderWorkflowFragment(OrderAddOrEditActivity.this);
                            fragment.setData(workflowModels);
                            FragmentManager fm = getSupportFragmentManager();
                            fm.beginTransaction().setCustomAnimations(
                                    R.anim.enter_righttoleft, R.anim.exit_righttoleft,
                                    R.anim.enter_righttoleft, R.anim.exit_lefttoright)
                                    .add(R.id.fragment_container, fragment).addToBackStack("workflow")
                                    .commit();
                        }
                    }
                });
    }

    /**
     * OrderWorkflowFragment.ActionListener
     */

    @Override
    public void onSelectWorkflowBack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onSelectWorkflowConfirm(WorkflowModel model) {
        Log.v("workflow", model.toString());
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }
}
