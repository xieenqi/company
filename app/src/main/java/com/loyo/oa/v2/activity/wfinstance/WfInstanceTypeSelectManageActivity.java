package com.loyo.oa.v2.activity.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.WfInstanceTypeSelectListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BizForm;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【审批类型选择】 adapter
 */
public class WfInstanceTypeSelectManageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    ViewGroup img_title_left;

    public PullToRefreshListView listView_bizform;
    public WfInstanceTypeSelectListViewAdapter wfInstanceTypeSelectListViewAdapter;

    ArrayList<BizForm> lstData_BizForm = new ArrayList<>();
    PaginationX pagination = new PaginationX(20);
    BizForm bizForm;
    private boolean isTopAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfinstance_type_select);

        initUI();
        onPullDownToRefresh(null);
    }


    private void initUI() {
        super.setTitle("选择类型");
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());

        listView_bizform = (PullToRefreshListView) findViewById(R.id.listView_bizform);
        listView_bizform.setMode(PullToRefreshBase.Mode.DISABLED);

        if (lstData_BizForm != null) {
            wfInstanceTypeSelectListViewAdapter = new WfInstanceTypeSelectListViewAdapter(this, lstData_BizForm, false);
            listView_bizform.setAdapter(wfInstanceTypeSelectListViewAdapter);
            listView_bizform.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    bizForm = lstData_BizForm.get((int) id);
                    //bizForm = wfInstanceTypeSelectListViewAdapter.getData().get(position-1);
                    if (bizForm != null) {
RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForm(bizForm.getId(), new RCallback<BizForm>() {
                            @Override
                            public void success(BizForm bizForm, Response response) {
                                LogUtil.d(MainApp.gson.toJson(bizForm));//获得查询的数据
                                if (bizForm != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra(BizForm.class.getName(), bizForm);
                                    app.finishActivity(WfInstanceTypeSelectManageActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast("获取审批类型详情失败");
                                super.failure(error);
                            }
                        });

                    } else {
                        Toast("类型不存在");
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
        }
    }

    private void getData_BizForm() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", pagination.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData_BizForm.size() >= 20 ? lstData_BizForm.size() : 20 : 20);

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
            @Override
            public void success(PaginationX<BizForm> bizFormPaginationX, Response response) {
                listView_bizform.onRefreshComplete();

                if (null != bizFormPaginationX) {
                    pagination = bizFormPaginationX;
                    if (isTopAdd) {
                        lstData_BizForm.clear();
                    }
                    lstData_BizForm.addAll(pagination.getRecords());
                    wfInstanceTypeSelectListViewAdapter.notifyDataSetChanged();
                }
                LogUtil.d(MainApp.gson.toJson(bizFormPaginationX));
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("获取审批类型失败");
                listView_bizform.onRefreshComplete();
                super.failure(error);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pagination.setPageIndex(1);
        isTopAdd = true;
        getData_BizForm();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pagination.setPageIndex(pagination.getPageIndex() + 1);
        isTopAdd = false;
        getData_BizForm();
    }
}
