package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;

import retrofit.client.Response;

//仅显示信息或提交，修改操作在TaskEditActivity
@EActivity(R.layout.activity_tasks_info)
public class TasksInfoActivity extends BaseActivity {

    static final int REQUEST_SCORE = 200;
    static final int REQUEST_EDIT = 300;
    static final int REQUEST_EDIT_DELETE = 500;
    static final int REQUEST_CREATE_SUB = 600;

    final int MSG_ATTACHMENT = 700;
    final int MSG_DISCUSSION = 800;
//    final int MSG_CHILD_TASK = 800;

    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;
    @ViewById ViewGroup layout_responsiblePerson;
    @ViewById ViewGroup layout_score;
    @ViewById ViewGroup layout_child_add_action;
    @ViewById ViewGroup layout_child_Add_area;

    @ViewById TextView tv_task_title;
    @ViewById TextView tv_sub_title;
    @ViewById TextView tv_content;
    @ViewById TextView tv_remind;
    @ViewById TextView tv_task_audit;
    @ViewById TextView  tv_task_project;
    @ViewById TextView tv_toUsers;
    @ViewById TextView tv_responsiblePerson;
    @ViewById TextView tv_reviewer;
    @ViewById TextView tv_comment;
    @ViewById TextView tv_discussion_count;
    @ViewById TextView tv_attachment_count;
    @ViewById TextView tv_children_info;

    @ViewById Button btn_complete;
    @ViewById RatingBar ratingBar_Task;

    @Extra("task") Task mTask;

    //信鸽透传过来的id
    @Extra("id") String mId;

//    User responseUser;      //负责人
    PaginationX<Discussion> mPageDiscussion;

    @AfterViews
    void init() {
        initUI();
        getTask();
    }

    void initUI() {
        super.setTitle("任务详情");

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        btn_complete.setOnTouchListener(Global.GetTouch());
//        layout_children_task.setOnTouchListener(Global.GetTouch());
        layout_child_add_action.setOnTouchListener(Global.GetTouch());
    }

    String getId() {
        return (mTask != null) ? mTask.getId() : mId;
    }

    void updateUI() {
        if (mTask != null) {
            updateUI_task_base();
            updateUI_task_responsiblePerson();
            updateUI_task_sub_task();
        }
    }

    void updateUI_task_responsiblePerson() {
        //进行中,分派人登陆可修改负责人和参与人
        if ((IsCreator() || IsResponsiblePerson()) && mTask.getStatus() == Task.STATUS_PROCESSING) {
            img_title_right.setVisibility(View.VISIBLE);
        } else {
            img_title_right.setVisibility(View.GONE);
        }

        if (mTask.getResponsiblePerson() != null) {
            tv_responsiblePerson.setText("负责人:" + mTask.getResponsiblePerson().getRealname());
        }

        ArrayList<NewUser> users = mTask.getJoinedUsers();
        if (users != null && users.size() > 0) {
            String userNames = NewUser.GetNewUserNames(users);

            if (!TextUtils.isEmpty(userNames)) {
                tv_toUsers.setText("参与人:" + userNames);
            } else {
                tv_toUsers.setText("");
            }
        }
        if(null!=mTask.getProject()){
            tv_task_project.setText("所属项目："+mTask.getProject().getTitle());
        }
    }

    void updateUI_task_base() {
        if (mTask.getCreator() != null) {
            if (mTask.getStatus() == Task.STATUS_PROCESSING && IsResponsiblePerson()) {
                //负责人提交
                btn_complete.setText("提交完成");
            } else if (mTask.getStatus() == Task.STATUS_REVIEWING && IsCreator()) {
                btn_complete.setText("审 核");
            } else if (mTask.getStatus() == Task.STATUS_FINISHED) {
                btn_complete.setVisibility(View.GONE);

                //显示分数
                layout_score.setVisibility(mTask.isReviewFlag() ? View.VISIBLE : View.GONE);
                ratingBar_Task.setProgress(mTask.getScore() / 20);
                tv_reviewer.setText(mTask.getCreator().getRealname());

                if (!ListUtil.IsEmpty(mTask.getReviewComments())) {
                    tv_comment.setText(mTask.getReviewComments().get(mTask.getReviewComments().size() - 1).getContent());
                }
            } else {
                btn_complete.setVisibility(View.GONE);
            }
        }

        tv_task_title.setText(mTask.getTitle());
        tv_content.setText(mTask.getContent());
        tv_task_audit.setText("是否审核:" + (mTask.isReviewFlag() ? "是" : "否"));

        if (mTask.getCreator() != null && mTask.getCreatedAt() > 0) {
            tv_sub_title.setText(String.format("%s %s 修改", mTask.getCreator().getRealname(),
                    app.df2.format(new Date(mTask.getCreatedAt()))));
        }

        if (mTask.getActualEndAt() > 0) {
            String s = app.df3.format(new Date(mTask.getActualEndAt())) + " 截止";

            if (mTask.getRemindTime() > 0) {
                s += "," + Task.GetRemindText(mTask.getRemindTime());
            }
            tv_remind.setText(s);
        }
    }

    void updateUI_task_sub_task() {
        if (ListUtil.IsEmpty(mTask.getchecklists())) {
            return;
        }

        layout_child_Add_area.removeAllViews();

        int finished = 0;

        for (TaskCheckPoint subTask : mTask.getchecklists()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_task_layout, null, false);

            TextView viewName = (TextView) view.findViewById(R.id.item_tv_child_principal);
            if (subTask.getResponsiblePerson() != null) {
                viewName.setText(subTask.getResponsiblePerson().getName());
            }

            TextView viewContent = (TextView) view.findViewById(R.id.item_tv_child_task_content);
            if (!TextUtils.isEmpty(subTask.getcontent())) {
                viewContent.setText(subTask.getcontent());
            }

            final CheckBox cb = (CheckBox)view.findViewById(R.id.cb);

            ViewGroup layout_cb = (ViewGroup)view.findViewById(R.id.layout_cb);
            if (layout_cb != null && cb != null){
                layout_cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cb.toggle();
                    }
                });
            }

            if (subTask.isAchieved()) {
                finished++;
            }

            layout_child_Add_area.addView(view);
        }

        tv_children_info.setText(String.format("(%d/%d)", finished, mTask.getchecklists().size()));
    }

    @Background
    void getTask() {
        app.getRestAdapter().create(ITask.class).getTask(getId(), new RCallback<Task>() {
            @Override
            public void success(Task task, Response response) {
                if (task != null) {
                    mTask = task;
                    updateUI();
                    getDiscussion();
                    showAttachment();
                }
            }
        });
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.btn_complete})
    void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                if (null == mTask) {
                    Toast("任务为空！");
                    return;
                }
                Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);

                if (IsCreator() && mTask.getStatus() == Task.STATUS_PROCESSING) {
                    intent.putExtra("edit", true);
                    intent.putExtra("delete", true);
                } else if (IsResponsiblePerson() && mTask.getStatus() == Task.STATUS_PROCESSING) {
                    intent.putExtra("edit", true);
                }

                startActivityForResult(intent, REQUEST_EDIT_DELETE);

                break;

            case R.id.btn_complete:
                //信鸽透传时可能task为空 ykb 07-16
                if (null != mTask && mTask.getStatus() == Task.STATUS_PROCESSING && IsResponsiblePerson()) {
                    RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class)
                    .commitTask(null!=mTask?mTask.getId():mId, new RCallback<Task>() {
                        @Override
                        public void success(Task task, Response response) {
                            if (task != null) {
                                task.setAck(true);
                                Intent intent = new Intent();
                                intent.putExtra("review", task);
                                app.finishActivity(TasksInfoActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                            }
                        }
                    });
                } else if (mTask.getStatus() == Task.STATUS_REVIEWING && mTask.getCreator().isCurrentUser()) {

                    mTask.setAck(true);
                    //跳转到评分
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("mTask", mTask);
                    app.startActivityForResult(this, TasksInfoScoreActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_SCORE, bundle2);
                }

                break;
        }
    }

    @Click(R.id.tv_upload)
    void openUpload() {
        Intent intent = new Intent(this, SelectPicPopupWindow.class);
        intent.putExtra("localpic", true);
        startActivityForResult(intent, SelectPicPopupWindow.GET_IMG);
    }

    @Click(R.id.layout_child_add_action)
    void openNewSubTask() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Task", mTask);
        app.startActivityForResult(this, ChildTaskAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CREATE_SUB, bundle);
    }

    @Background
    void getDiscussion() {
        ITask t = app.getRestAdapter().create(ITask.class);
        t.getDiscussions(String.valueOf(getId()), new RCallback<PaginationX<Discussion>>() {
            @Override
            public void success(PaginationX<Discussion> discussionPaginationX, Response response) {

                if (PaginationX.isEmpty(discussionPaginationX)) {
                    return;
                }

                mPageDiscussion = discussionPaginationX;
                tv_discussion_count.setText(String.valueOf(discussionPaginationX.getTotalRecords()));
            }
        });
    }

    void showAttachment() {
        if (ListUtil.IsEmpty(mTask.getAttachments())) {
            return;
        }

        tv_attachment_count.setText("("+(mTask.getAttachments() == null ? 0 : mTask.getAttachments().size())+")");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
//            case MSG_CHILD_TASK:
//                if (null != data && data.hasExtra("childTasks")) {
//                    ArrayList<TaskCheckPoint> points = (ArrayList<TaskCheckPoint>) data.getSerializableExtra("childTasks");
//                    mTask.setchecklists(points);
//                    checkChildStatus();
//                }
//                break;
//            case DepartmentUserActivity.request_Code:
//
//                User user = (User) data.getSerializableExtra(User.class.getName());
//                if (user != null) {
//                    responseUser = user;
//                    updateUI_task_responsiblePerson();
//                } else {
//                    String cc_user_id = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
//                    String cc_user_name = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
//                    if (cc_user_name != null) {
//                        tv_toUsers.setText(cc_user_name);
//                    }
//                }
//
//                break;
            case REQUEST_SCORE:
//                mTask = (Task) data.getSerializableExtra("review");
//                updateUI();
                getTask();
            case REQUEST_EDIT:
                //TODO:奇怪这里取不到数据
                //                mTask = (Task) data.getSerializableExtra("task_return");
                //                joinedUserName = data.getStringExtra("joinedUserName");
                //                updateUI();

                getTask();
                getDiscussion();
                break;
            case REQUEST_EDIT_DELETE:
                if (data.getBooleanExtra("edit", false)) {
                    //跳转到编辑界面
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mTask", mTask);

                    app.startActivityForResult(this, TasksEditActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_EDIT, bundle);

                } else if (data.getBooleanExtra("delete", false)) {

                    app.getRestAdapter().create(ITask.class).deleteTask(mTask.getId(), new RCallback<Task>() {
                        @Override
                        public void success(Task o, Response response) {
                            Intent intent = new Intent();
                            intent.putExtra("delete", mTask);
                            app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
                        }
                    });
                }
                break;

            case MSG_ATTACHMENT:
                if (data == null || data.getExtras() == null) {
                    return;
                }
                ArrayList<Attachment> attachments = (ArrayList<Attachment>) data.getSerializableExtra("data");
                mTask.setAttachments(attachments);
                showAttachment();

                break;

            case MSG_DISCUSSION:
                if (data == null || data.getExtras() == null) {
                    return;
                }

                mPageDiscussion = (PaginationX<Discussion>) data.getSerializableExtra("data");
                showDiscussion();

                break;
        }
    }

    @OnActivityResult(REQUEST_CREATE_SUB)
    void onNewSubTaskActivityResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data.hasExtra("data")) {
            return;
        }

        TaskCheckPoint subTask = (TaskCheckPoint) data.getSerializableExtra("childTask");
        if (subTask != null) {
            mTask.getchecklists().add(subTask);
            updateUI_task_sub_task();
        }
    }

    void showDiscussion() {
        if (!ListUtil.IsEmpty(mPageDiscussion.getRecords())) {
            int count = mPageDiscussion.getRecords().size();
            tv_discussion_count.setText("(" + count + ")");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (mTask != null) {
            mTask.setAck(true);
            intent.putExtra("data", mTask);
        }
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    @Click(R.id.layout_attachment)
    void clickAttachment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", mTask.getAttachments());
        bundle.putSerializable("uuid", mTask.getAttachmentUUId());

        ArrayList<NewUser> users = new ArrayList<>();
        if (mTask.getJoinedUsers() != null) {
            users.addAll(mTask.getJoinedUsers());
        }

        if (mTask.getCreator() != null && !users.contains(mTask.getCreator())) {
            users.add(mTask.getCreator());
        }

        if (mTask.getResponsiblePerson() != null && !users.contains(mTask.getResponsiblePerson())) {
            users.add(mTask.getResponsiblePerson());
        }

        bundle.putSerializable("users", users);

        app.startActivityForResult(this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);
    }

    @Click(R.id.layout_discussion)
    void clickDiscussion() {
        Bundle bundle = new Bundle();
        bundle.putString("attachmentUUId", mTask.getAttachmentUUId());
        app.startActivityForResult(this, DiscussionActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_DISCUSSION, bundle);
    }

    boolean IsCreator() {
        return null!=mTask.getCreator()?mTask.getCreator().isCurrentUser():false;
    }

    boolean IsResponsiblePerson() {
        return null!=mTask.getResponsiblePerson()?mTask.getResponsiblePerson().isCurrentUser():false;
    }
}
