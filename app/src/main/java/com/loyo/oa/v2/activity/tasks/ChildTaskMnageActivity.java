package com.loyo.oa.v2.activity.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.activity
 * 描述 :子页
 * 作者 : ykb
 * 时间 : 15/7/20.
 */
@EActivity(R.layout.activity_childtask_info_layout)
public class ChildTaskMnageActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    public static final int REQUEST_ADD_CHILD_TASK = 800;
    public static final int REQUEST_EDIT_CHILD_TASK = 900;
    private ChildTaskInfoAdapter mAdapter;

    @ViewById(R.id.lv_child_task) PullToRefreshListView lv_child_task;
    @ViewById(R.id.iv_child_task_add) ImageView iv_child_task_add;
    @ViewById ViewGroup img_title_left;

    @Extra("Task") Task mTask;
    /**
     * 子任务集
     */
    ArrayList<TaskCheckPoint> mChildTasks = new ArrayList<TaskCheckPoint>();

    @AfterViews
    void initUi() {
        setTouchView(-1);
        iv_child_task_add.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnTouchListener(Global.GetTouch());
        mChildTasks = mTask.getchecklists();
        mAdapter = new ChildTaskInfoAdapter();
        lv_child_task.setAdapter(mAdapter);
        lv_child_task.setOnRefreshListener(this);
        lv_child_task.setMode(PullToRefreshBase.Mode.DISABLED);
        //任务创建或负责人可以添加子任务
        boolean showAdd = mTask.getCreator().equals(MainApp.user) || mTask.getResponsiblePerson().equals(MainApp.user);
        if (!showAdd) {
            for (TaskCheckPoint point : mChildTasks) {
                if (null != point.getResponsiblePerson() && point.getResponsiblePerson().equals(MainApp.user)) {
                    showAdd = false;
                    break;
                }
            }
        }
        if (!showAdd) {
            iv_child_task_add.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh(final PullToRefreshBase refreshView) {

    }

    @Click({R.id.img_title_left, R.id.iv_child_task_add})
    void onClick(final View view) {
        switch (view.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.iv_child_task_add:
                Bundle b = new Bundle();
                b.putSerializable("Task", mTask);
                MainApp.getMainApp().startActivityForResult(this, ChildTaskAddActivity_.class, MainApp.ENTER_TYPE_BUTTOM, REQUEST_ADD_CHILD_TASK, b);
                break;
            default:
                break;
        }
    }

    private class ChildTaskInfoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mChildTasks == null ? 0 : mChildTasks.size();
        }

        @Override
        public TaskCheckPoint getItem(final int i) {
            return mChildTasks == null || mChildTasks.isEmpty() ? null : mChildTasks.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view,final ViewGroup viewGroup) {
            final TaskCheckPoint point = getItem(position);
            if (null == point) {
                return null;
            }

            if (null == view) {
                view = getLayoutInflater().inflate(R.layout.item_child_task_layout, viewGroup, false);
            }

            TextView principal = ViewHolder.get(view, R.id.item_tv_child_principal);
//            TextView status = ViewHolder.get(view, R.id.item_tv_child_task_status);
            TextView content = ViewHolder.get(view, R.id.item_tv_child_task_content);
            if (null != point.getResponsiblePerson()) {
                principal.setText(point.getResponsiblePerson().getRealname());
            }
//            status.setText(point.isAchieved() ? "已完成" : "未完成");
            content.setText(point.getTitle());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (null == point || null == point.getResponsiblePerson() || null == MainApp.user) {
                        return;
                    }
                    //                    if (mTask.getCreator().getId() == MainApp.user.getId() ||mTask.getResponsiblePerson().getId()==MainApp.user.getId()|| point.getResponsiblePerson().getId() == MainApp.user.getId()) {
                    Bundle b = new Bundle();
                    b.putSerializable("childTask", point);
                    b.putSerializable("Task", mTask);
                    MainApp.getMainApp().startActivityForResult(ChildTaskMnageActivity.this, ChildTaskAddActivity_.class, MainApp.ENTER_TYPE_BUTTOM, REQUEST_EDIT_CHILD_TASK, b);
                    //                    }
                }
            });

            return view;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("childTasks", mChildTasks);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_ADD_CHILD_TASK:
                if (data != null && data.hasExtra("childTask")) {
                    TaskCheckPoint point = (TaskCheckPoint) data.getSerializableExtra("childTask");
                    mChildTasks.add(point);
                    mAdapter.notifyDataSetChanged();
                }

                break;

            case REQUEST_EDIT_CHILD_TASK:
                if (null != data) {
                    if (data.hasExtra("childTask")) {
                        TaskCheckPoint tempPoint = (TaskCheckPoint) data.getSerializableExtra("childTask");
                        for (int i = 0; i < mChildTasks.size(); i++) {
                            TaskCheckPoint mpoint = mChildTasks.get(i);
                            if (mpoint.getId() == tempPoint.getId()) {
                                mChildTasks.set(i, tempPoint);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }

                    } else if (data.hasExtra("childTaskId")) {
                        String tempPointId = data.getStringExtra("childTaskId");
                        for (int i = 0; i < mChildTasks.size(); i++) {
                            TaskCheckPoint mpoint = mChildTasks.get(i);
                            if (mpoint.getId().equals(tempPointId)) {
                                mChildTasks.remove(i);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
