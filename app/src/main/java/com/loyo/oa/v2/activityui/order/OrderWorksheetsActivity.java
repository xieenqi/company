package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorksheetListAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.common.adapter.BaseGroupsDataAdapter;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.worksheet.api.WorksheetService;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 【工单搜索】
 */

public class OrderWorksheetsActivity extends BaseLoadingActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshExpandableListView expandableListView;
    private ViewGroup layout_add;
    private OrderDetail detail;
    private int page = 1;
    private int status;
    private boolean isPullDown = true;
    private Bundle mBundle;
    protected GroupsData groupsData;
    private BaseGroupsDataAdapter adapter;
    boolean isMyUser;
    boolean canAddWorksheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupsData = new GroupsData();
        mBundle = getIntent().getExtras();
        detail = (OrderDetail) mBundle.getSerializable(ExtraAndResult.EXTRA_OBJ);
        canAddWorksheet = (boolean) mBundle.getBoolean(ExtraAndResult.EXTRA_BOOLEAN);
        status = mBundle.getInt(ExtraAndResult.EXTRA_ID);
        if (detail == null || detail.id == null) {
            Toast("参数错误");
            finish();
        }

        initView();
        getPageData();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.layout_order_worksheet);
    }

    @Override
    public void getPageData() {
        getData();
    }

    @Subscribe
    public void onWorksheetCreated(Worksheet data) {
        isPullDown = true;
        page = 1;
        getData();
    }

    /**
     * 初始化
     */
    void initView() {
        setTitle("工单");

        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        isMyUser = detail.directorId != null && detail.directorId.equals(MainApp.getMainApp().user.id);
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }
        if (!canAddWorksheet) {
            layout_add.setVisibility(View.GONE);
        }

        layout_add.setOnClickListener(this);

        expandableListView = (PullToRefreshExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setMode(PullToRefreshBase.Mode.BOTH);
        expandableListView.setOnRefreshListener(this);

        ExpandableListView innerListView = expandableListView.getRefreshableView();
        if (status == 1) {
            adapter = new WorksheetListAdapter(this, groupsData, true, true);
        } else {
            adapter = new WorksheetListAdapter(this, groupsData, false, true);
        }

        innerListView.setAdapter(adapter);
        innerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        innerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (status == 1) {
                    sweetAlertDialogView.alertIcon(null, "订单未通过审核,无法查看工单详情!");
                } else {
                    Intent mIntent = new Intent(getApplicationContext(), WorksheetDetailActivity.class);
                    String wsId = null;
                    Worksheet ws = (Worksheet) groupsData.get(groupPosition, childPosition);
                    wsId = ws.id;
                    if (wsId == null) {
                        wsId = "";
                    }
                    mIntent.putExtra(ExtraAndResult.EXTRA_ID, wsId);
                    startActivity(mIntent);
                }
                return true;
            }
        });
    }

    protected void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("type", 1);
        WorksheetService.getWorksheetListByOrder(detail.id, map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Worksheet>>() {

                    @Override
                    public void onError(Throwable e) {
                        @LoyoErrorChecker.CheckType
                        int type = groupsData.size() > 0?
                                LoyoErrorChecker.TOAST:LoyoErrorChecker.LOADING_LAYOUT;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                        expandableListView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(PaginationX<Worksheet> x) {
                        expandableListView.onRefreshComplete();
                        if (isPullDown) {
                            groupsData.clear();
                        }
                        if (isPullDown && PaginationX.isEmpty(x) && groupsData.size()==0) {
                            ll_loading.setStatus(LoadingLayout.Empty);
                        }
                        else  if (PaginationX.isEmpty(x)){
                            Toast("没有更多数据了");
                            ll_loading.setStatus(LoadingLayout.Success);
                        }
                        else  {
                            ll_loading.setStatus(LoadingLayout.Success);
                        }

                        loadData(x != null?x.records:new ArrayList<Worksheet>());
                    }
                });
    }

    private void loadData(List<Worksheet> list) {
        Iterator<Worksheet> iterator = list.iterator();
        while (iterator.hasNext()) {
            groupsData.addItem(iterator.next());
        }
        groupsData.sort();
        adapter.notifyDataSetChanged();
        expand();
    }

    protected void expand() {
        for (int i = 0; i < groupsData.size(); i++) {
            expandableListView.getRefreshableView().expandGroup(i, false);//true 自动滑到底部
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }

    protected void changeAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*新建线索*/
            case R.id.layout_add:
                Bundle bundle = new Bundle();
                bundle.putSerializable(ExtraAndResult.EXTRA_OBJ, WorksheetOrder.converFromDetail(detail));
                app.startActivityForResult(this, WorksheetAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE, bundle);
                break;

            default:
                break;

        }
    }
}
