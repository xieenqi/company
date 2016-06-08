package com.loyo.oa.v2.activity.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshExpandableListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 客户详情->的任务计划->  【任务管理】
 */
@EActivity(R.layout.activity_customer_task_list)
public class TaskListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    @ViewById ViewGroup layout_back;
    @ViewById ViewGroup layout_add;
    @ViewById TextView tv_title;

    @ViewById(R.id.listView_tasks) PullToRefreshExpandableListView lv;
    @Extra Customer mCustomer;
    @Extra boolean isMyUser;

    private PaginationX<Task> taskPaginationX = new PaginationX<>(20);
    private ArrayList<Task> tasks = new ArrayList<>();
    protected ArrayList<PagingGroupData_<Task>> pagingGroupDatas = new ArrayList<>();
    private CommonExpandableListAdapter adapter;
    private boolean isTopAdd;
    private boolean isChanged;

    @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("任务管理");
        layout_back.setOnTouchListener(Global.GetTouch());
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }

        layout_add.setOnTouchListener(Global.GetTouch());
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
        Bundle b = new Bundle();
        b.putString(ExtraAndResult.EXTRA_ID, mCustomer.id);
        b.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
        app.startActivityForResult(this, TasksAddActivity_.class, MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_CREATE_TASK, b);
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
                public boolean onGroupClick(final ExpandableListView parent,final View v,final int groupPosition,final long id) {
                    return true;
                }
            });

            lv.getRefreshableView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(final ExpandableListView parent,final View v,final int groupPosition,final int childPosition,final long id) {
                    Task task = (Task) adapter.getChild(groupPosition, childPosition);
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
        map.put("endAt", DateTool.getEndAt_ofDay(app.df5));
        map.put("customerId", mCustomer.getId());
        map.put("pageIndex", taskPaginationX.getPageIndex());
        map.put("pageSize", isTopAdd ? tasks.size() >= 20 ? tasks.size() : 20 : 20);

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).getList(map).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<PaginationX<Task>>() {
            @Override
            public void onCompleted() {
                lv.onRefreshComplete();
            }

            @Override
            public void onError(final Throwable e) {
                lv.onRefreshComplete();
            }

            @Override
            public void onNext(final PaginationX<Task> paginationX) {
                lv.onRefreshComplete();
                taskPaginationX = paginationX;
                lv.onRefreshComplete();
                if (isTopAdd) {
                    tasks.clear();
                }
                tasks.addAll(paginationX.getRecords());
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
    private void openTaskDetial(final Task task) {
        Intent intent = new Intent(this, TasksInfoActivity_.class);
        intent.putExtra(ExtraAndResult.EXTRA_ID, task.getId());
        //intent.putExtra("mCustomer", mCustomer);
        startActivityForResult(intent, BaseCommonMainListFragment.REQUEST_REVIEW);
    }

    @Override
    public void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
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
        onPullDownToRefresh(lv);
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
