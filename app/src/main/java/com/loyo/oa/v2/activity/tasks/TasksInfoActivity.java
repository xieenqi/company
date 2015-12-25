package com.loyo.oa.v2.activity.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.AttachmentActivity_;
import com.loyo.oa.v2.activity.ChildTaskAddActivity_;
import com.loyo.oa.v2.activity.DiscussionActivity_;
import com.loyo.oa.v2.activity.SelectEditDeleteActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Reviewer;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【任务详情】
 * 仅显示信息或提交，修改操作在TaskEditActivity
 */
@EActivity(R.layout.activity_tasks_info)
public class TasksInfoActivity extends BaseActivity {

    public static final int REQUEST_SCORE = 200;
    public static final int REQUEST_EDIT = 300;
    public static final int REQUEST_EDIT_DELETE = 500;
    public static final int REQUEST_CREATE_SUB = 600;
    public static final int REQUEST_EDIT_TASK = 740;

    public static final int MSG_ATTACHMENT = 700;
    public static final int MSG_DISCUSSION = 800;

    String vTitle;
    String vContent;
    String realName;
    String isTest;
    String beProjects;

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    ViewGroup layout_responsiblePerson;
    @ViewById
    ViewGroup layout_score;
    @ViewById
    ViewGroup layout_child_add_action;
    @ViewById
    ViewGroup layout_child_Add_area;

    @ViewById
    TextView tv_task_title;
    @ViewById
    TextView tv_sub_title;
    @ViewById
    TextView tv_content;
    @ViewById
    TextView tv_remind;//截至 时间提醒
    @ViewById
    TextView tv_task_audit;
    @ViewById
    TextView tv_task_project;
    @ViewById
    TextView tv_task_aboutuser;
    @ViewById
    TextView tv_toUsers;
    @ViewById
    TextView tv_responsiblePerson;
    @ViewById
    TextView tv_reviewer;
    @ViewById
    TextView tv_comment;
    @ViewById
    TextView tv_discussion_count;
    @ViewById
    TextView tv_attachment_count;
    @ViewById
    TextView tv_children_info;
    @ViewById
    Button btn_complete;
    @ViewById
    RatingBar ratingBar_Task;
    @Extra("task")
    Task mTask;
    @Extra("Id")
    String mId;

    public String taskId;  //任务ID
    public String userId;
    public int statusSize;
    public boolean isJoin;
    public boolean isCreator;

    public PaginationX<Discussion> mPageDiscussion;
    public static TasksInfoActivity instance = null;
    public ArrayList<NewUser> allUsers;


    public android.os.Handler mHandler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                tv_children_info.setText(String.format("(%d/%d)", statusSize, mTask.getchecklists().size()));
            }
        }
    };

    @AfterViews
    void init() {
        instance = this;
        getTask();
        initUI();
    }

    void initUI() {
        super.setTitle("任务详情");

        /*是否为参与人判断*/
        userId = DBManager.Instance().getUser().getId();
        isJoin = !userId.equals(mTask.getCreator().getId()) && !userId.equals(mTask.getResponsiblePerson().getId()) ? true : false;
        isCreator = userId.equals(mTask.getCreator().getId()) ? true : false;

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        btn_complete.setOnTouchListener(Global.GetTouch());
        layout_child_add_action.setOnTouchListener(Global.GetTouch());
        allUsers = new ArrayList<>();
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

    /**
     * 任务属性设置
     */
    void updateUI_task_responsiblePerson() {
        //进行中,分派人登陆可修改负责人和参与人
        if ((IsCreator() || IsResponsiblePerson()) && mTask.getStatus() == Task.STATUS_PROCESSING) {
            img_title_right.setVisibility(View.VISIBLE);
        } else {
            img_title_right.setVisibility(View.GONE);
        }

        if (mTask.getResponsiblePerson() != null) {
            realName = mTask.getResponsiblePerson().getName();
            tv_responsiblePerson.setText("负责人:" + realName);
            allUsers.add(mTask.getResponsiblePerson());
        }

        if (mTask.members != null) {
            if (mTask.members.getAllData().size() > 0) {
                StringBuffer userNames = new StringBuffer();
                for (NewUser element : mTask.members.getAllData()) {
                    userNames.append(element.getName() + ",");
                }
                tv_toUsers.setText("参与人:" + userNames.toString());
                allUsers.addAll(mTask.members.getAllData());
            } else {
                tv_toUsers.setText("没有参与人");
            }
        }
        if (null != mTask.getProject()) {
            beProjects = mTask.getProject().title;
            tv_task_project.setText("所属项目：" + beProjects);
        }
        if(null != mTask.getCustomerName()){
            tv_task_aboutuser.setText("关联客户:"+mTask.getCustomerName());
        }else{
            tv_task_aboutuser.setVisibility(View.GONE);
        }
    }

    /**
     * 底部按钮内容控制
     */
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
        isTest = mTask.isRemindFlag() ? "是" : "否";
        tv_task_audit.setText("是否审核:" + isTest);

        vTitle = mTask.getTitle();
        vContent = mTask.getContent();

        if (mTask.getCreator() != null && mTask.getCreatedAt() > 0) {
            tv_sub_title.setText(String.format("%s %s 修改", mTask.getCreator().getRealname(),
                    app.df2.format(new Date(mTask.getCreatedAt()))));
        }

        /*截至时间*/
        if (mTask.getPlanEndAt() > 0) {
            String s = DateTool.timet(mTask.getPlanEndAt()+"") + " 截止";
            if (mTask.getRemindTime() > 0) {
                s += "," + Task.GetRemindText(mTask.getRemindTime());
            }
            tv_remind.setText(s);
        }
    }

    /**
     * 子任务View,内容设置
     * TaskCheckPoint为Bean
     * mTask.getchecklists()是子任务数据集
     */
    void updateUI_task_sub_task() {
        statusSize = 0;
        if (ListUtil.IsEmpty(mTask.getchecklists())) {
            return;
        }

        layout_child_Add_area.removeAllViews();

        //子任务列表内容，遍历
        for (final TaskCheckPoint subTask : mTask.getchecklists()) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_task_layout, null, false);

            //子任务标题
            TextView viewName = (TextView) view.findViewById(R.id.item_tv_child_principal);
            if (subTask.getResponsiblePerson() != null) {
                viewName.setText(subTask.getResponsiblePerson().getName());
            }

            //子任务内容
            TextView viewContent = (TextView) view.findViewById(R.id.item_tv_child_task_content);
            if (!TextUtils.isEmpty(subTask.getcontent())) {
                viewContent.setText(subTask.getcontent());
            }

            /*Checkbox勾选,赋值*/
            CheckBox childCheckbox = (CheckBox) view.findViewById(R.id.cb);
            boolean isStatus = subTask.getStatus().equals("1") ? true : false;

            /*子任务个数设置*/
            if (isStatus) {
                statusSize++;
            }

            childCheckbox.setChecked(isStatus);
            childCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                    if (isCheck) {
                        statusSize++;
                        mHandler.sendEmptyMessage(0x01);
                        requestTaskupdates(taskId, subTask.getId(), 1);//任务ID，子任务ID，勾选状态
                    } else {
                        statusSize--;
                        mHandler.sendEmptyMessage(0x01);
                        requestTaskupdates(taskId, subTask.getId(), 0);
                    }
                }
            });

            //到编辑子任务
            view.setTag(subTask);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //组装 负责人 于 参与人
                    ArrayList<Reviewer> reponserData = new ArrayList<Reviewer>();
                    //reponserData.addAll(mTask.getMembers().getUsers());
                    reponserData.addAll(mTask.responsiblePersons);
                    ArrayList<NewUser> reponserDataUser = new ArrayList<NewUser>();
                    for (Reviewer element : reponserData) {
                        reponserDataUser.add(element.getUser());
                    }

                    Intent intent = new Intent(TasksInfoActivity.this, TaskChildEdit.class);
                    intent.putExtra("TaskEdit", (TaskCheckPoint) v.getTag());
                    intent.putExtra("TaskId", mTask.getId());
                    intent.putExtra("reponserData", reponserDataUser);
                    TasksInfoActivity.this.startActivityForResult(intent, REQUEST_EDIT_TASK);
                    TasksInfoActivity.this.overridePendingTransition(R.anim.enter_lefttoright, R.anim.exit_lefttoright);
                }
            });
            layout_child_Add_area.addView(view);
        }
        //子任务完成度(3/5)设置
        tv_children_info.setText(String.format("(%d/%d)", statusSize, mTask.getchecklists().size()));
    }

    /**
     * 更新子任务状态（完成／未完成)
     */
    void requestTaskupdates(String id, String cid, int sts) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("status", sts);

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).updatesTask(id, cid, map, new RCallback<Task>() {
            @Override
            public void success(Task task, Response response) {
                Toast("更新成功");
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取任务信息【子任务等】
     */
    @Background
    void getTask() {
        app.getRestAdapter().create(ITask.class).getTask(getId(), new RCallback<Task>() {
            @Override
            public void success(Task task, Response response) {
                try {
                    LogUtil.dll("任务详情返回JSON：" + Utils.convertStreamToString(response.getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mTask = task;
                updateUI();
                showAttachment();
                taskId = task.getId(); //任务ID获取

            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 标题左右监听
     */
    @Click({R.id.img_title_left, R.id.img_title_right, R.id.btn_complete})
    void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_title_left:
                finish();
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
                    intent.putExtra("extra", "复制任务");
                } else if (IsResponsiblePerson() && mTask.getStatus() == Task.STATUS_PROCESSING) {
                    intent.putExtra("edit", true);
                }

                startActivityForResult(intent, REQUEST_EDIT_DELETE);

                break;

            /**提交完成*/
            case R.id.btn_complete:
                if (statusSize == mTask.getchecklists().size() || mTask.getchecklists().size() == 0) {
                    commitFinish();
                } else {
                    Toast("子任务尚未完成，不能提交！");
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

    /**
     * 任务提交完成
     */
    void commitFinish() {
        //信鸽透传时可能task为空 ykb 07-16
        if (null != mTask && mTask.getStatus() == Task.STATUS_PROCESSING && IsResponsiblePerson()) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class)
                    .commitTask(null != mTask ? mTask.getId() : mId, new RCallback<Task>() {
                        @Override
                        public void success(Task task, Response response) {
                            if (task != null) {
                                task.setAck(true);
                                Intent intent = new Intent();
                                intent.putExtra("review", task);
                                app.finishActivity(TasksInfoActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            super.failure(error);
                            HttpErrorCheck.checkError(error);
                        }
                    });
        } else if (mTask.getStatus() == Task.STATUS_REVIEWING && mTask.getCreator().isCurrentUser()) {

            mTask.setAck(true);
            //跳转到评分
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("mTask", mTask);
            app.startActivityForResult(this, TasksInfoScoreActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_SCORE, bundle2);
        }
    }


    /**
     * 新建子任务
     */
    @Click(R.id.layout_child_add_action)
    void openNewSubTask() {
        if (isJoin) {
            Toast("参与人不能创建子任务!");
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Task", mTask);
            bundle.putSerializable("allUsers", allUsers);
            app.startActivityForResult(this, ChildTaskAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CREATE_SUB, bundle);
        }
    }

    /**
     * 获取讨论内容，服务端已启用，暂注释
     */
/*    @Background
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
    }*/

    void showAttachment() {
        if (ListUtil.IsEmpty(mTask.getAttachments())) {
            return;
        }
        tv_attachment_count.setText("(" + (mTask.getAttachments() == null ? 0 : mTask.getAttachments().size()) + ")");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case REQUEST_EDIT_TASK://编辑 子任务 返回
                layout_child_Add_area.removeAllViews();
                getTask();
                break;
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
                break;

            case REQUEST_EDIT_DELETE:
                /*编辑回调*/
                if (data.getBooleanExtra("edit", false)) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mTask", mTask);
                    bundle.putBoolean("type", isCreator);
                    app.startActivityForResult(this, TasksEditActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_EDIT, bundle);

                }

                /*删除回调*/
                else if (data.getBooleanExtra("delete", false)) {

                    app.getRestAdapter().create(ITask.class).deleteTask(mTask.getId(), new RCallback<Task>() {
                        @Override
                        public void success(Task o, Response response) {
                            Intent intent = new Intent();
                            intent.putExtra("delete", mTask);
                            app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
                        }
                    });

                }
                /*复制回调*/
                else if (data.getBooleanExtra("extra", false)) {
                    Intent intent = new Intent(TasksInfoActivity.this, TasksAddActivity_.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("data",mTask);
                    intent.putExtras(mBundle);
                    startActivity(intent);
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

    /**
     * 附件上传
     */
    @Click(R.id.layout_attachment)
    void clickAttachment() {

        Bundle bundle = new Bundle();
        bundle.putSerializable("data", mTask.getAttachments());
        bundle.putSerializable("uuid", mTask.getAttachmentUUId());
        bundle.putBoolean("isMyUser", false);
        bundle.putInt("fromPage", Common.TASK_PAGE);

     /*   ArrayList<NewUser> users = new ArrayList<>();
        if (mTask.getMembers() != null) {
            users.addAll(mTask.getMembers().getAllData());
        }
        if (mTask.getCreator() != null && !users.contains(mTask.getCreator())) {
            users.add(mTask.getCreator());
        }
        if (mTask.getResponsiblePerson() != null && !users.contains(mTask.getResponsiblePerson())) {
            users.add(mTask.getResponsiblePerson());
        }
        bundle.putSerializable("users", users);*/

        app.startActivityForResult(this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);
    }

    @Click(R.id.layout_discussion)
    void clickDiscussion() {
        Bundle bundle = new Bundle();
        bundle.putString("attachmentUUId", mTask.getAttachmentUUId());
        app.startActivityForResult(this, DiscussionActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_DISCUSSION, bundle);
    }

    boolean IsCreator() {
        return null != mTask.getCreator() ? mTask.getCreator().isCurrentUser() : false;
    }

    boolean IsResponsiblePerson() {

        return null != mTask.getResponsiblePerson() ? mTask.getResponsiblePerson().isCurrentUser() : false;
    }
}
