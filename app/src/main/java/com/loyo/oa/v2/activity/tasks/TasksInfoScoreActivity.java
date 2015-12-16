package com.loyo.oa.v2.activity.tasks;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【任务审核】页面
 */
@EActivity(R.layout.activity_tasks_info_score)
public class TasksInfoScoreActivity extends BaseActivity {

    @ViewById
    ViewGroup img_title_left;

    @ViewById
    EditText edt_content;
    @ViewById
    RatingBar ratingBar_Task;

    @Extra("mTask")
    Task mTask;

    @ViewById
    Button btn_task_agree;
    @ViewById
    Button btn_task_notagree;

    @AfterViews
    void init() {
        super.setTitle("任务审核");
        setTouchView(R.id.layout_btn);
        getTempTask();
    }

    void getTempTask() {
        Task task = DBManager.Instance().getTaskScore(mTask.getId());
        if (task == null) return;

        ratingBar_Task.setProgress(Integer.valueOf(String.valueOf(task.getScore())).intValue() / 20);

        if (!StringUtil.isEmpty(task.getTaskComment())) {
            edt_content.setText(task.getTaskComment());
        }
    }

    @Click({R.id.img_title_left, R.id.btn_task_agree, R.id.btn_task_notagree})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.btn_task_agree:
                final int grade = ratingBar_Task.getProgress() * 20;
                final String comment1 = edt_content.getText().toString().trim();

                HashMap<String,Object> map=new HashMap<>();
                map.put("score", grade);
                map.put("comment", comment1);
                map.put("status", 1);

                verfyTask(map);
                break;
            case R.id.btn_task_notagree:
                final int grade1 = ratingBar_Task.getProgress() * 20;
                final String comment = edt_content.getText().toString().trim();
                if (comment.isEmpty()) {
                    Toast("请输入意见");
                    break;
                }
                HashMap<String,Object> map1=new HashMap<>();
                map1.put("score", grade1);
                map1.put("comment", comment);
                map1.put("status", 0);

                verfyTask(map1);
                break;
        }
    }

    /**
     * 审核任务
     * @param map
     */
    private void verfyTask(HashMap<String,Object> map){
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).verifyTask(mTask.getId(), map, new RCallback<Task>() {
            @Override
            public void success(Task task, Response response) {
                if (task != null) {
                    isSave = false;
                    Intent intent = new Intent();
                    intent.putExtra("review", task);
                    app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                if(error.getKind() == RetrofitError.Kind.NETWORK){
                    Toast("请检查您的网络连接");
                }else if(error.getKind() == RetrofitError.Kind.HTTP){
                    if(error.getResponse().getStatus() == 500){
                        Toast("网络异常500，请稍候再试");
                    }
                }
            }
        });
    }


    //isSave=true时保存临时Task,=false时删除Task临时Task
    boolean isSave = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isSave) {
            mTask.setScore(ratingBar_Task.getProgress() * 20);
            String content = edt_content.getText().toString().trim();

            if (!StringUtil.isEmpty(content)) {
                mTask.setTaskComment(content);
            }

            mTask.setResponsiblePerson(null);
            mTask.setAttachments(null);
            mTask.setReviewComments(null);
            mTask.setCreator(null);
//            mTask.setJoinedUsers(null);

            DBManager.Instance().putTaskScore(MainApp.gson.toJson(mTask), mTask.getId());
        } else {
            DBManager.Instance().deleteTaskScore(mTask.getId());
        }
    }

}
