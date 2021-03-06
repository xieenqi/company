package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateFormatSet;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.other.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.activityui.tasks.TasksAddActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.task.api.TaskService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 客户详情->的任务计划->  【任务管理】
 */
@EActivity(R.layout.activity_customer_task_list)
public class TaskListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {

    @ViewById
    ViewGroup layout_back;
    @ViewById
    ViewGroup layout_add;
    @ViewById
    LoadingLayout ll_loading;
    @ViewById
    TextView tv_title;
    @ViewById(R.id.listView_tasks)
    PullToRefreshExpandableListView lv;
    @Extra
    Customer mCustomer;
    @Extra
    boolean canAdd;

    private PaginationX<TaskRecord> taskPaginationX = new PaginationX<>(20);
    private ArrayList<TaskRecord> tasks = new ArrayList<>();
    protected ArrayList<PagingGroupData_<TaskRecord>> pagingGroupDatas = new ArrayList<>();
    private CommonExpandableListAdapter adapter;
    private boolean isTopAdd;
    private boolean isChanged;

    @AfterViews
    void initViews() {
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                isTopAdd = true;
                getData();
            }
        });
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("任务管理");
        layout_back.setOnTouchListener(Global.GetTouch());
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
        layout_add.setOnTouchListener(Global.GetTouch());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isTopAdd = true;
        getData();
    }

    @Override
    public void onBackPressed() {
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isChanged ? RESULT_OK : RESULT_CANCELED, new Intent());
    }

    @Click(R.id.layout_back)
    void back() {
        onBackPressed();
    }

    /**
     * 客户创建新的任务
     */
    @Click(R.id.layout_add)
    void createNewTask() {
        if (!PermissionManager.getInstance().hasPermission(BusinessOperation.TASK)) {
            sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
        } else {
            Bundle b = new Bundle();
            b.putString(ExtraAndResult.EXTRA_ID, mCustomer.id);
            b.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
            app.startActivityForResult(this, TasksAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_CREATE_TASK, b);
        }
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            adapter = new CommonExpandableListAdapter(this, pagingGroupDatas);
            lv.getRefreshableView().setAdapter(adapter);
            lv.setOnRefreshListener(this);
            lv.getRefreshableView().setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(final ExpandableListView parent, final View v, final int groupPosition, final long id) {
                    return true;
                }
            });

            lv.getRefreshableView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
                    if (!PermissionManager.getInstance().hasPermission(BusinessOperation.TASK)) {
                        sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                        return false;
                    }
                    TaskRecord task = (TaskRecord) adapter.getChild(groupPosition, childPosition);
                    openTaskDetial(task);
                    return false;
                }
            });
        } else {
            adapter.setData(pagingGroupDatas);
            adapter.notifyDataSetChanged();
        }
        expand();
    }

    /**
     * 展开listview
     */
    private void expand() {
        for (int i = 0; i < pagingGroupDatas.size(); i++) {
            lv.getRefreshableView().expandGroup(i, false);
        }
    }

    /**
     * 获取列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("startAt", "2014-01-01");
        map.put("endAt", DateFormatSet.dateSdf.format(com.loyo.oa.common.utils.DateTool.getCurrentDayEndMillis()));
        map.put("customerId", mCustomer.getId());
        map.put("pageIndex", taskPaginationX.getPageIndex());
        map.put("pageSize", isTopAdd ? tasks.size() >= 20 ? tasks.size() : 20 : 20);
        TaskService.getListData(map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<TaskRecord>>(ll_loading) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        lv.onRefreshComplete();
                    }

                    @Override
                    public void onNext(PaginationX<TaskRecord> x) {
                        taskPaginationX = x;
                        lv.onRefreshComplete();
                        if (isTopAdd) {
                            tasks.clear();
                        }
                        if (isTopAdd && x.getRecords().size() == 0) {
                            ll_loading.setStatus(LoadingLayout.Empty);
                        }
                        else {
                            ll_loading.setStatus(LoadingLayout.Success);
                        }
                        tasks.addAll(x.getRecords());
                        pagingGroupDatas = PagingGroupData_.convertGroupData(tasks);
                        bindData();
                    }
                });

    }

    /**
     * 查看任务
     *
     * @param task
     */
    private void openTaskDetial(final TaskRecord task) {
        Intent intent = new Intent(this, TasksInfoActivity_.class);
        intent.putExtra(ExtraAndResult.EXTRA_ID, task.getId());
        //intent.putExtra("mCustomer", mCustomer);
        startActivityForResult(intent, BaseCommonMainListFragment.REQUEST_REVIEW);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || null == data) {
            return;
        }
        switch (requestCode) {
            case BaseCommonMainListFragment.REQUEST_REVIEW:

                break;
            case FinalVariables.REQUEST_CREATE_TASK:
                isChanged = true;
                break;

            default:
                break;
        }
        //回到页面已经在请求数据不需要再手动请求数据了（默认获取第一页数据）
        isTopAdd = true;
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        taskPaginationX.setPageIndex(1);
        isTopAdd = true;
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        taskPaginationX.setPageIndex(taskPaginationX.getPageIndex() + 1);
        getData();
    }
}
