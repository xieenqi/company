package com.loyo.oa.v2.activity.tasks;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
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
    @ViewById
    Button btn_task_agree;

    @Extra("mTask")
    Task mTask;

    public String comment;
    public int sorce = 0;
    public int status = 1;

    public Switch task_info_switch;
    public LinearLayout tasks_info_sorceview;


    @AfterViews
    void init() {
        super.setTitle("任务审核");
        setTouchView(R.id.layout_btn);
        getTempTask();
    }

    void getTempTask() {

        tasks_info_sorceview = (LinearLayout) findViewById(R.id.tasks_info_sorceview);
        task_info_switch = (Switch) findViewById(R.id.task_info_switch);
        task_info_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    status = 1;
                    tasks_info_sorceview.setVisibility(View.VISIBLE);
                } else {
                    status = 0;
                    tasks_info_sorceview.setVisibility(View.GONE);
                }
            }
        });
    }

    @Click({R.id.img_title_left, R.id.btn_task_agree})
    void onClick(View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*提交*/
            case R.id.btn_task_agree:
                comment = edt_content.getText().toString().trim();
                sorce = ratingBar_Task.getProgress() * 20;
                if (comment.isEmpty()) {
                    Toast("请输入评语");
                } else {
                    verfyTask(sorce, status, comment);
                }
                break;
        }
    }

    /**
     * 审核任务post
     *
     * @param sorce status comment
     */
    private void verfyTask(int sorce, int status, String comment) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("score", sorce);
        map.put("comment", comment);
        map.put("status", status);

        LogUtil.dll("发送数据:"+MainApp.gson.toJson(map));
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
                HttpErrorCheck.checkError(error);
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
//          mTask.setJoinedUsers(null);

            DBManager.Instance().putTaskScore(MainApp.gson.toJson(mTask), mTask.getId());
        } else {
            DBManager.Instance().deleteTaskScore(mTask.getId());
        }
    }

}
