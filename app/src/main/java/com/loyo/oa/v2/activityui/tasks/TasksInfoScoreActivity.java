package com.loyo.oa.v2.activityui.tasks;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SwitchView;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.task.api.TaskService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

/**
 * 【任务审核】页面
 */
@EActivity(R.layout.activity_tasks_info_score)
public class TasksInfoScoreActivity extends BaseActivity {

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    EditText edt_content, et_score;
    //    @ViewById
//    RatingBar ratingBar_Task;
    @ViewById
    Button btn_task_agree;

    @Extra("mTask")
    Task mTask;

    public String comment;
    public String sorce;
    public int status = 1;

    public SwitchView task_info_switch;
    public LinearLayout tasks_info_sorceview;

    @AfterViews
    void init() {
        super.setTitle("任务审核");
        getTempTask();

//        ratingBar_Task.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//                score = v;
//            }
//        });
        et_score.setFilters(new InputFilter[]{Utils.decimalDigits(2)});
        et_score.addTextChangedListener(OrderCommon.getTextWatcher());
    }

    void getTempTask() {

        tasks_info_sorceview = (LinearLayout) findViewById(R.id.tasks_info_sorceview);
        task_info_switch = (SwitchView) findViewById(R.id.task_info_switch);
        task_info_switch.setState(true);
        task_info_switch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                task_info_switch.setState(true);
                status = 1;
                edt_content.setHint("请输入审核内容(选填)");
                tasks_info_sorceview.setVisibility(View.VISIBLE);
            }

            @Override
            public void toggleToOff() {
                task_info_switch.setState(false);
                status = 0;
                edt_content.setHint("请输入审核内容(必填)");
                tasks_info_sorceview.setVisibility(View.GONE);
            }
        });
    }

    @Click({R.id.img_title_left, R.id.btn_task_agree})
    void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*提交*/
            case R.id.btn_task_agree:
                comment = edt_content.getText().toString().trim();
                sorce = et_score.getText().toString();

                if (status == 0) {
                    if (comment.isEmpty()) {
                        Toast("点评内容不能为空!");
                        return;
                    }
                    verfyTask(sorce, status, comment);
                } else {
//                    if (TextUtils.isEmpty(sorce)) {
//                        Toast("请评分!");
//                        return;
//                    }
                    verfyTask(sorce, status, comment);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 审核任务post
     *
     * @param sorce status comment
     */
    private void verfyTask(final String sorce, final int status, final String comment) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("newScore", TextUtils.isEmpty(sorce) ? "-1" : sorce);
        map.put("comment", TextUtils.isEmpty(comment) ? "审核通过" : comment);
        map.put("status", status);

        LogUtil.dll("发送数据:" + MainApp.gson.toJson(map));
        TaskService.verifyTask(mTask.getId(), map)
                .subscribe(new DefaultLoyoSubscriber<Task>() {
                    @Override
                    public void onNext(Task task) {
                        isSave = false;
                        Intent intent = new Intent();
                        intent.putExtra("review", task);
                        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    }
                });
    }

    //isSave=true时保存临时Task,=false时删除Task临时Task
    boolean isSave = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
//
//        if (isSave) {
////            mTask.setScore(Integer.parseInt(et_score.getText().toString()));
//            String content = edt_content.getText().toString().trim();
//            if (!StringUtil.isEmpty(content)) {
//                mTask.setTaskComment(content);
//            }
//            mTask.setResponsiblePerson(null);
//            mTask.setAttachments(null);
//            mTask.setReviewComments(null);
//            mTask.setCreator(null);
////          mTask.setJoinedUsers(null);
//
//            DBManager.Instance().putTaskScore(MainApp.gson.toJson(mTask), mTask.getId());
//        } else {
//            DBManager.Instance().deleteTaskScore(mTask.getId());
//        }
    }

}
