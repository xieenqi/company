package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.LoyoUIThread;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.adapter.TerminateProcessListAdapter;
import com.loyo.oa.v2.activityui.wfinstance.api.WfinstanceService;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfTemplate;
import com.loyo.oa.v2.activityui.wfinstance.common.WfinstanceBizformConfig;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EthanGong on 2017/1/10.
 */

public class TerminateOrderProcessListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    public final static String KEY_SELECTED_PROCESS = "com.loyo.TerminateOrderProcessListActivity.SELECTED_PROCESS";
    private LinearLayout ll_back;
    private PullToRefreshListView listView;
    private LoadingLayout ll_loading;
    private TerminateProcessListAdapter adapter;
    private WfTemplate preSelectedItem;
    private BizForm terminateBizForm;
    private ArrayList<WfTemplate> templates;

    private View.OnClickListener onClickListener = new View.OnClickListener(){

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ll_back) {
                onBackPressed();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminate_order_process_list);
        setupUI();
        retrieveData();
        getPageData();
    }

    private void setupUI() {
        setTitle(R.id.tv_title, "选择流程");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(onClickListener);

        listView = (PullToRefreshListView)findViewById(R.id.lv_list);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selectItemAtIndex(position-1);
                preSelectedItem = adapter.getSelectItem();
                adapter.notifyDataSetChanged();
                finishWithResult();
            }
        });
        ll_loading = (LoadingLayout) findViewById(R.id.ll_loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                getPageData();
            }
        });
        adapter = new TerminateProcessListAdapter(app);
        listView.setAdapter(adapter);
    }

    private void retrieveData() {
        preSelectedItem = (WfTemplate)getIntent().getSerializableExtra(KEY_SELECTED_PROCESS);
        if (preSelectedItem != null) {
            adapter.setSelectedItem(preSelectedItem);
        }
    }

    private void finishWithResult() {
        WfTemplate item = adapter.getSelectItem();
        if (item != null) {
            Intent intent = new Intent();
            intent.putExtra(KEY_SELECTED_PROCESS, item);
            setResult(RESULT_OK, intent);
        }
        else {
            setResult(RESULT_CANCELED);
        }

        finishWithPopAnimation();
    }

    private void getPageData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        getBizForm();
    }

    private void getBizForm() {
        ArrayList<BizForm> list = WfinstanceBizformConfig.getBizform(true);
        boolean needFetch = false;
        if (list == null || list.size() == 0) {
            needFetch = true;
        }
        BizForm targetForm = null;
        for (BizForm form : list) {
            if (form.bizCode == 401) {
                targetForm = form;
                break;
            }
        }
        if (targetForm == null) {
            needFetch = true;
        }

        if (needFetch) {
            getBizFormList();
        }
        else {
            terminateBizForm = targetForm;
            getData();
        }
    }

    /**
     * 获取审批类别列表
     */
    private void getBizFormList() {
        showLoading2("");
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 2000);
        WfinstanceService.getWfBizForms(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<BizForm>>(hud) {
                    @Override
                    public void onNext(PaginationX<BizForm> bizFormPaginationX) {
                        BizForm targetForm = null;
                        if (PaginationX.isEmpty(bizFormPaginationX)) {
                            for (BizForm form : bizFormPaginationX.records) {
                                if (form.bizCode == 401) {
                                    targetForm = form;
                                    break;
                                }
                            }
                        }
                        if (targetForm == null) {
                            LoyoToast.error(TerminateOrderProcessListActivity.this, "未配置意外终止审批流程");
                        }
                        else {
                            terminateBizForm = targetForm;
                            getData();
                        }
                    }
                });
    }

    private void getData() {

//        if (terminateBizForm!=null /*&& !terminateBizForm.isEnable() && null == terminateBizForm .getFields()*/) {
//            LoyoToast.info(TerminateOrderProcessListActivity.this, "该审批类型没有配置流程，请重新选择!");
//            ll_loading.setStatus(LoadingLayout.No_Network);
//            ll_loading.setNoNetworkText("该审批类型没有配置流程，请重新选择!");
//            return;
//        }

        WfinstanceService.getWfTemplate(terminateBizForm.getId())
                .subscribe(new DefaultLoyoSubscriber<ArrayList<WfTemplate>>(ll_loading) {

                    @Override
                    public void onNext(ArrayList<WfTemplate> bizFormFieldsPaginationX) {

                        templates = bizFormFieldsPaginationX;
                        if (templates == null || templates.size() == 0) {
                            ll_loading.setStatus(LoadingLayout.Empty);
                        }
                        else {
                            ll_loading.setStatus(LoadingLayout.Success);
                        }
                        adapter.setData(templates);
                    }
                });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        LoyoUIThread.runAfterDelay(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
            }
        }, 1000);

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
