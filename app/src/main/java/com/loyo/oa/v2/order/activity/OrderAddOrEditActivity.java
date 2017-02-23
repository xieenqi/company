package com.loyo.oa.v2.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.loyo.oa.common.utils.LoyoUIThread;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.bean.OrderAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
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

    public static final int ORDER_ADD = 0;    /* 订单新建 */
    public static final int ORDER_IMPORT = 1; /* 机会转订单 */
    public static final int ORDER_COPY = 2;   /* 订单复制 */
    public static final int ORDER_EDIT = 3;   /* 订单编辑 */

    /*
    * * 操作类型 ORDER_ADD ORDER_IMPORT ORDER_COPY ORDER_EDIT
    */
    public static final String KEY_ACTION_TYPE = "OrderAddOrEditActivity.KEY_ACTION_TYPE";

    public static final String KEY_ORDER = "OrderAddOrEditActivity.KEY_ORDER"; /* 订单实体 */
    public static final String KEY_ORDER_ID = "OrderAddOrEditActivity.KEY_ORDER_ID"; /* 订单id */
    public final static String KEY_CAPITAL_RETURNING_PLAN_EDIT = "OrderAddOrEditActivity.KEY_CAPITAL_RETURNING_PLAN_EDIT";
    public final static String KEY_CAPITAL_RETURNING_RECORD_EDIT = "OrderAddOrEditActivity.KEY_CAPITAL_RETURNING_RECORD_EDIT";


    private HashMap<String, Object> commitMap;
    private String workflowId;
    private int actionType;
    private String orderId;
    private OrderDetail orderDetail;
    private boolean capitalReturningPlanEdit = true;
    private boolean capitalReturningRecordEdit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add_or_edit);
        getIntentData();
        FragmentManager fm = getSupportFragmentManager();
        OrderFieldsFragment fragment = new OrderFieldsFragment(this);
        fragment.actionType = actionType;
        fragment.orderDetail = orderDetail;
        fragment.orderId = orderId;
        fragment.capitalReturningRecordEdit = capitalReturningRecordEdit;
        fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    private void getIntentData() {
        Intent mIntent = getIntent();
        if (null != mIntent) {
            orderDetail = (OrderDetail) mIntent.getSerializableExtra(KEY_ORDER);
            orderId = (String) mIntent.getSerializableExtra(KEY_ORDER_ID);
            actionType = mIntent.getIntExtra(KEY_ACTION_TYPE, ORDER_ADD);
            capitalReturningPlanEdit = mIntent.getBooleanExtra(KEY_CAPITAL_RETURNING_PLAN_EDIT, true);
            capitalReturningRecordEdit = mIntent.getBooleanExtra(KEY_CAPITAL_RETURNING_RECORD_EDIT, true);
        }
    }

    /**
     * OrderFieldsFragment.ActionListener
     */

    @Override
    public void onCommit(final HashMap<String, Object> orderData) {
        this.commitMap = orderData;
        showLoading2("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 400 /* 400 订单  500：回款 */);
        OrderService.getWorkflows(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<WorkflowModel>>(hud) {
                    @Override
                    public void onNext(ArrayList<WorkflowModel> workflowModels) {
                        Log.v("o", workflowModels.toString());
                        processWorkflows(workflowModels);
                    }
                });
    }

    private void processWorkflows(ArrayList<WorkflowModel> workflowModels) {
        if (workflowModels == null || workflowModels.size() <= 1) {
            /* 避免loading动画被前一个请求关闭 */
            LoyoUIThread.runAfterDelay(new Runnable() {
                @Override
                public void run() {
                    if (actionType == ORDER_EDIT) {
                        editOrderData(commitMap);
                    }
                    else {
                        addOrderData(commitMap);
                    }
                }
            }, 100);
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
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        commitMap.put("wfTplId", model.id);
        if (actionType == ORDER_EDIT) {
            editOrderData(commitMap);
        }
        else {
            addOrderData(commitMap);
        }
    }

    /**
     * 编辑订单
     */
    public void editOrderData(HashMap<String, Object> map) {
        showCommitLoading();
        OrderService.editOrder(orderId, map)
                .subscribe(new DefaultLoyoSubscriber<OrderAdd>(hud) {
                    @Override
                    public void onNext(OrderAdd add) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                app.finishActivity(OrderAddOrEditActivity.this,
                                        MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                            }
                        }, 2000);
                    }
                });

    }

    /**
     * 新建订单
     */
    public void addOrderData(HashMap<String, Object> map) {
        showCommitLoading();
        OrderService.addOrder(map)
                .subscribe(new DefaultLoyoSubscriber<OrderAdd>(hud) {
                    @Override
                    public void onNext(OrderAdd add) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                app.finishActivity(OrderAddOrEditActivity.this,
                                        MainApp.ENTER_TYPE_LEFT,
                                        ExtraAndResult.REQUEST_CODE,
                                        new Intent());
                            }
                        }, 2000);
                    }
                });
    }
}
