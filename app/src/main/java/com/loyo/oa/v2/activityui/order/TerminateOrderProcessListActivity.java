package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.LoyoUIThread;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.adapter.TerminateProcessListAdapter;
import com.loyo.oa.v2.activityui.order.bean.ProcessItem;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * Created by EthanGong on 2017/1/10.
 */

public class TerminateOrderProcessListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    public final static String KEY_SELECTED_PROCESS = "com.loyo.TerminateOrderProcessListActivity.SELECTED_PROCESS";
    private LinearLayout ll_back;
    private PullToRefreshListView listView;
    private LoadingLayout ll_loading;
    private TerminateProcessListAdapter adapter;
    private ProcessItem preSelectedItem;

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
    }

    private void setupUI() {
        setTitle(R.id.tv_title, "选择流程");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(onClickListener);

        listView = (PullToRefreshListView)findViewById(R.id.lv_list);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
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
        preSelectedItem = (ProcessItem)getIntent().getSerializableExtra(KEY_SELECTED_PROCESS);
        if (preSelectedItem != null) {
            adapter.setSelectedItem(preSelectedItem);
        }
    }

    private void finishWithResult() {
        ProcessItem item = adapter.getSelectItem();
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
        getData();
    }

    private void getData() {
        ll_loading.setStatus(LoadingLayout.Success);
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
