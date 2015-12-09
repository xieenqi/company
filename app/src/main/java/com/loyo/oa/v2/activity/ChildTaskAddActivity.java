package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ICheckPoint;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 描述 :【新建子任】页面务类
 * 作者 : ykb
 * 时间 : 15/7/20.
 */
@EActivity(R.layout.activity_childtask_add_layout)
public class ChildTaskAddActivity extends BaseActivity {

    @ViewById ViewGroup layout_child_add_responser;
    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;

    @ViewById TextView tv_child_add_responser_name;

    @ViewById EditText et_child_add_content;

    @ViewById Button btn_child_add_complete;
    @ViewById Button btn_child_add_cancel_complete;

    @Extra("childTask") TaskCheckPoint chidTask;
    @Extra("Task") Task mTask;

    TaskCheckPoint mChildTask = new TaskCheckPoint();

    NewUser newUser;

    @AfterViews
    void intUi() {
        setTouchView(-1);
        layout_child_add_responser.setOnTouchListener(Global.GetTouch());
        btn_child_add_complete.setOnTouchListener(Global.GetTouch());
        btn_child_add_cancel_complete.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());

        if (null != chidTask) {
            mChildTask = chidTask;
            /**
             * 主任务的发布者、负责人、子任务发布者 拥有对子任务的全部编辑权限
             */
            if (mTask.getCreator().isCurrentUser() || mTask.getResponsiblePerson().isCurrentUser() || mChildTask.getCreator().isCurrentUser()) {
                ((ImageView) img_title_right.findViewById(R.id.imgview_title_right)).setImageResource(R.drawable.img_more);
            } else {
                img_title_right.setVisibility(View.GONE);
                if (!mChildTask.getResponsiblePerson().isCurrentUser()) {
                    findViewById(R.id.layout_child_add_action).setVisibility(View.GONE);
                    layout_child_add_responser.setEnabled(false);
                    et_child_add_content.setEnabled(false);
                }
            }

            tv_child_add_responser_name.setText(chidTask.getResponsiblePerson().getRealname());
            et_child_add_content.setText(chidTask.getTitle());
            ((TextView) findViewById(R.id.tv_title_1)).setText("子");
            /**
             * 只要已完成，删除、编辑都不允许，只允许改变为未完成
             */
            if (mChildTask.isAchieved()) {
                btn_child_add_complete.setVisibility(View.GONE);
                //                btn_child_delete_task.setVisibility(View.GONE);
                layout_child_add_responser.setEnabled(false);
                et_child_add_content.setEnabled(false);
                img_title_right.setVisibility(View.GONE);
            }
            /**
             * 1. 未完成子任务不能显示取消完成按钮
             * 2.未完成任务下，是主任务负责人或发布人或子任务创建人，允许所有权限；只是子任务负责人的话，只允许提交完成和未完成
             */
            else {
                btn_child_add_cancel_complete.setVisibility(View.GONE);
                if (mTask.getCreator().isCurrentUser() || mTask.getResponsiblePerson().isCurrentUser() || mChildTask.getCreator().isCurrentUser()) {

                } else if (mChildTask.getResponsiblePerson().isCurrentUser()) {
                    //                    btn_child_delete_task.setVisibility(View.GONE);
                    et_child_add_content.setEnabled(false);
                    img_title_right.setVisibility(View.GONE);
                    layout_child_add_responser.setEnabled(false);
                }
            }

        } else {
            ((TextView) findViewById(R.id.tv_title_1)).setText("新建子任务");
            findViewById(R.id.layout_child_add_action).setVisibility(View.GONE);
        }

        if (null != mTask) {
            boolean isSameUser = (null != mTask.getJoinedUsers() && mTask.getJoinedUsers().size() == 1 && mTask.getJoinedUsers().get(0).equals(mTask.getResponsiblePerson()));
            if ((null == mTask.getJoinedUsers() || mTask.getJoinedUsers().isEmpty() || isSameUser)) {
                newUser = mTask.getResponsiblePerson();
                if (null != newUser) {
                    tv_child_add_responser_name.setText(newUser.getRealname());
                }
            }
        }
    }

    @Click({R.id.layout_child_add_responser, R.id.btn_child_add_complete, R.id.btn_child_add_cancel_complete, R.id.img_title_left, R.id.img_title_right})
    void onClick(View view) {
        switch (view.getId()) {
            //选择添加负责人
            case R.id.layout_child_add_responser:

                ArrayList<NewUser> users = new ArrayList<>();
                if (null != mTask) {
                    if (null != mTask.getJoinedUsers() && !mTask.getJoinedUsers().isEmpty()) {
                        users.addAll(mTask.getJoinedUsers());
                    }
                    if (null != mTask.getResponsiblePerson()) {
                        if (!users.contains(mTask.getResponsiblePerson())) {
                            users.add(mTask.getResponsiblePerson());
                        }
                    }
                }
                Bundle b1 = new Bundle();
                b1.putSerializable("users", users);
                app.startActivityForResult(this, ChildTaskResponserSelectActivity_.class, MainApp.ENTER_TYPE_RIGHT, 300, b1);
                break;
            case R.id.btn_child_add_complete:
            case R.id.btn_child_add_cancel_complete:
                if (view.getId() == R.id.btn_child_add_complete) {
                    mChildTask.setAchieved(true);
                } else {
                    mChildTask.setAchieved(false);
                }
                update(false);
                break;
            case R.id.img_title_left:
                MainApp.getMainApp().finishActivity(this, MainApp.ENTER_TYPE_TOP, RESULT_OK, null);
                break;
            case R.id.img_title_right:
                if (null == chidTask || !mChildTask.getResponsiblePerson().equals(chidTask.getResponsiblePerson())) {
                    create();
                } else {
                    showEditPopu();
                }
                break;
        }
    }

    /**
     * 显示编辑子任务的弹出框
     */
    private void showEditPopu() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View menuView = mLayoutInflater.inflate(R.layout.popu_child_task_edit_layout, null, false);
        menuView.getBackground().setAlpha(50);
        Button btn_child_delete_task = (Button) menuView.findViewById(R.id.btn_child_delete_task);
        Button btnCancel = (Button) menuView.findViewById(R.id.btn_cancel_edit);
        Button btnUpdate = (Button) menuView.findViewById(R.id.btn_child_add_update);
        btn_child_delete_task.setOnTouchListener(Global.GetTouch());
        btnCancel.setOnTouchListener(Global.GetTouch());
        btnUpdate.setOnTouchListener(Global.GetTouch());

        final PopupWindow popupWindow = new PopupWindow(menuView, -1, -1, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));// 响应键盘三个主键的必须步骤
        popupWindow.showAtLocation(findViewById(R.id.tv_title_1), Gravity.BOTTOM, 0, 0);

        menuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return false;
            }
        });

        PopuOnClickListener listener = new PopuOnClickListener(popupWindow);
        btn_child_delete_task.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
        btnUpdate.setOnClickListener(listener);
    }

    /**
     * 处理popuwindow里按钮的点击事件
     */
    private class PopuOnClickListener implements View.OnClickListener {
        private PopupWindow mWindow;

        PopuOnClickListener(PopupWindow window) {
            mWindow = window;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_child_delete_task:
                    delete();
                    break;
                case R.id.btn_child_add_update:
                    update(true);
                    break;

            }
            mWindow.dismiss();
        }
    }

    /**
     * 删除子任务
     */
    private void delete() {
        MainApp.getMainApp().getRestAdapter().create(ICheckPoint.class).deleteChildTask(mTask.getId(), mChildTask.getId(), new RCallback<TaskCheckPoint>() {
            @Override
            public void success(TaskCheckPoint taskCheckPoint, Response response) {
                Toast("删除子任务成功");
                Intent delIntent = new Intent();
                delIntent.putExtra("childTaskId", chidTask.getId());
                MainApp.getMainApp().finishActivity(ChildTaskAddActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, delIntent);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("删除子任务失败");
                super.failure(error);
            }
        });
    }

    /**
     * 创建子任务
     */
    private synchronized void create() {

        if (null == newUser) {
            Toast("请选择负责人");
            return;
        }
        if (TextUtils.isEmpty(et_child_add_content.getText().toString())) {
            Toast("请填写内容");
            return;
        }
//        showDialog();
        mChildTask.setTitle(et_child_add_content.getText().toString());
        mChildTask.setResponsiblePerson(newUser);

        HashMap<String, Object> datas = new HashMap<String, Object>();
        datas.put("content", mChildTask.getTitle());
        datas.put("responsiblePerson", mChildTask.getResponsiblePerson());

        MainApp.getMainApp().getRestAdapter().create(ICheckPoint.class).createChildTask(mTask.getId(), datas, new RCallback<TaskCheckPoint>() {
            @Override
            public void success(TaskCheckPoint taskCheckPoint, Response response) {
//                dismissDialog();
                Toast("创建子任务成功");
                mChildTask = taskCheckPoint;
                Intent intent = new Intent();
                intent.putExtra("childTask", taskCheckPoint);
                MainApp.getMainApp().finishActivity(ChildTaskAddActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, intent);
            }

            @Override
            public void failure(RetrofitError error) {
//                dismissDialog();
                Toast("创建子任务失败");
                super.failure(error);
            }
        });
    }

    /**
     * 更新子任务
     *
     * @param update 是否需要更新子任务其他内容
     */
    private void update(boolean update) {
        if (TextUtils.isEmpty(tv_child_add_responser_name.getText().toString())) {
            Toast("请选择负责人");
            return;
        }
//        showDialog();
        if (update) {
            mChildTask.setTitle(et_child_add_content.getText().toString());
            if (null != newUser) {
                mChildTask.setResponsiblePerson(newUser);
            }
        }

        HashMap<String, Object> datas2 = new HashMap<String, Object>();
        datas2.put("title", mChildTask.getTitle());
        datas2.put("achieved", mChildTask.isAchieved());
        datas2.put("responsiblePersonId", mChildTask.getResponsiblePerson().getId());
        app.getRestAdapter().create(ICheckPoint.class).updateChildTask(mChildTask.getTaskId(), mChildTask.getId(), datas2, new RCallback<TaskCheckPoint>() {
            @Override
            public void success(TaskCheckPoint taskCheckPoint, Response response) {
//                dismissDialog();
                Toast("更新子任务成功");
                Intent completeIntent = new Intent();
                completeIntent.putExtra("childTask", mChildTask);
                MainApp.getMainApp().finishActivity(ChildTaskAddActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, completeIntent);
            }

            @Override
            public void failure(RetrofitError error) {
//                dismissDialog();
                Toast("更新子任务失败");
                super.failure(error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 300:
                NewUser user = (NewUser) data.getSerializableExtra("user");
                if (user != null) {
                    tv_child_add_responser_name.setText(user.getRealname());
                    newUser = user;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}