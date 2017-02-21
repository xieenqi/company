package com.loyo.oa.v2.customermanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

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
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.task.api.TaskService;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class TasksFragment extends CustomerChildFragment implements PullToRefreshBase.OnRefreshListener2 {

    View view;
    @BindView(R.id.layout_add) ViewGroup layout_add;
    @BindView(R.id.ll_loading) LoadingLayout ll_loading;
    PullToRefreshExpandableListView lv;
    Customer mCustomer;
    boolean canAdd;

    private PaginationX<TaskRecord> taskPaginationX = new PaginationX<>(20);
    private ArrayList<TaskRecord> tasks = new ArrayList<>();
    protected ArrayList<PagingGroupData_<TaskRecord>> pagingGroupDatas = new ArrayList<>();
    private CommonExpandableListAdapter adapter;
    private boolean isTopAdd;
    private boolean isChanged;

    public TasksFragment(Customer customer, int index, OnTotalCountChangeListener listener, String title) {
        super(customer, index, listener, title);
    }

    void initViews() {
        lv = (PullToRefreshExpandableListView)view.findViewWithTag("listView_tasks");
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                isTopAdd = true;
                getData();
            }
        });
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
        layout_add.setOnTouchListener(Global.GetTouch());
    }

    public void setCustomer(Customer customer) {
        mCustomer = customer;
        canAdd = mCustomer != null && mCustomer.state == Customer.NormalCustomer &&
                PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                        mCustomer.state, CustomerAction.TASK_ADD);
        this.totalCount = customer.counter.getTask();
    }

    public void reloadWithCustomer(Customer customer) {
        mCustomer = customer;
        canAdd = mCustomer != null && mCustomer.state == Customer.NormalCustomer &&
                PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                        mCustomer.state, CustomerAction.TASK_ADD);
        this.totalCount = customer.counter.getTask();
        if (view == null) {
            return;
        }
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_tasks, container, false);

            ButterKnife.bind(this, view);
            initViews();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isTopAdd = true;
        getData();
    }

    /**
     * 客户创建新的任务
     */
    @OnClick(R.id.layout_add)
    void createNewTask() {
        if (!PermissionManager.getInstance().hasPermission(BusinessOperation.TASK)) {
            sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
        } else {
            Bundle b = new Bundle();
            b.putString(ExtraAndResult.EXTRA_ID, mCustomer.id);
            b.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
            app.startActivityForResult(getActivity(), TasksAddActivity_.class,
                    MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_CREATE_TASK, b);
        }
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            adapter = new CommonExpandableListAdapter(getActivity(), pagingGroupDatas);
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
                        TasksFragment.this.totalCount = x.totalRecords;
                        notifyTotalCountChange();
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
        Intent intent = new Intent(getContext(), TasksInfoActivity_.class);
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
