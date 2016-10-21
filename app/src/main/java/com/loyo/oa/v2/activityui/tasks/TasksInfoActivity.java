package com.loyo.oa.v2.activityui.tasks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.AttachmentActivity_;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.discuss.DiscussDetialActivity;
import com.loyo.oa.v2.activityui.discuss.bean.Discussion;
import com.loyo.oa.v2.activityui.other.SelectEditDeleteActivity;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.work.bean.Reviewer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    public String vTitle;
    public String vContent;
    public String realName;
    public String isTest;
    public String beProjects;

    private String taskId;  //任务ID
    //    private String userId;
    private String uuid = StringUtil.getUUID();

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    ViewGroup layout_responsiblePerson;
    @ViewById
    ViewGroup layout_child_add_action;
    @ViewById
    ViewGroup layout_child_Add_area;

    View v_split;
    @ViewById
    RelativeLayout layout_attachment;

    @ViewById
    TextView tv_repeatTask;
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
    TextView tv_comment;
    @ViewById
    TextView tv_discussion_count;
    @ViewById
    TextView tv_attachment_count;
    @ViewById
    TextView tv_children_info;
    @ViewById
    Button btn_complete;
    @Extra(ExtraAndResult.EXTRA_ID)//推送的id   ="56935898526f152260000016"
            String mTaskId;
    @Extra(ExtraAndResult.EXTRA_TYPE)
    String keyType;
    @Extra(ExtraAndResult.IS_UPDATE)
    boolean isUpdate;//是否需要刷新列表

    private boolean isOver = false;
    private int statusSize;
    private Members member;
    private Task mTask;
    public PaginationX<Discussion> mPageDiscussion;
    public static TasksInfoActivity instance = null;
    public ArrayList<TextView> taskChildView = new ArrayList<>();
    public ArrayList<NewUser> childTastUsers = new ArrayList<>();
    public ArrayList<NewUser> requestDepts = new ArrayList<>();
    public ArrayList<User> aboutDepts = new ArrayList<>();
    public ArrayList<User> childTaskUsers2 = new ArrayList<>();

//    public ArrayList<Department> deptSource = Common.getLstDepartment();
    public LinearLayout layout_test_Add_area;
    public LinearLayout layout_task_testfather;
    public LinearLayout item_tasks_sorece;
    public ImageView iv_task_status;
    public RatingBar ratingBar_Task;
    public TextView tv_reviewtime;
    public TextView tv_task_content;
    public TextView tv_task_status;
    public TextView tv_reviewer;
    public TextView viewName;
    public TextView viewContent;
    private StringBuffer joinUserId;
    private StringBuffer joinName;

    public android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(final Message msg) {
            if (msg.what == 0x01) {
                tv_children_info.setText(String.format("(%d/%d)", statusSize, mTask.getchecklists().size()));
            } else if (msg.what == 0x02) {
                viewContent.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewContent.setTextColor(getResources().getColor(R.color.text99));
            } else if (msg.what == 0x03) {
                viewContent.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
                viewContent.setTextColor(getResources().getColor(R.color.text33));
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
//        userId = DBManager.Instance().getUser().getId();
        member = new Members();
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());
        layout_test_Add_area = (LinearLayout) findViewById(R.id.layout_test_Add_area);
        layout_task_testfather = (LinearLayout) findViewById(R.id.layout_task_testfather);
        iv_task_status = (ImageView) findViewById(R.id.iv_task_status);
        v_split = findViewById(R.id.v_splite);

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        btn_complete.setOnTouchListener(Global.GetTouch());
        layout_child_add_action.setOnTouchListener(Global.GetTouch());
    }

    void updateUI() {
        if (mTask != null) {
            updateUI_task_base();
            updateUI_task_responsiblePerson();
            updateUI_task_sub_task();
            updateUIInTask();
        }
    }

    /**
     * 当当前用户不在项目中, 不显示附件和子任务
     */
    private void updateUIInTask() {
        NewUser user = mTask.getResponsiblePerson(); // 获取负责人
        NewUser createUser = mTask.getCreator(); //获取创建人
        List<NewUser> users = mTask.getMembers().getAllData(); // 获取参与人列表

        users.add(user);
        users.add(createUser);

        boolean isInTask = false; //判断当前用户是否在任务中
        for (int i = 0; i < users.size(); i++) {
            if (null != users.get(i) && (MainApp.user.id.equals(users.get(i).getId())
                    || isMenberShortDept(users.get(i).getId(), users.get(i).getXpath()))) {
                isInTask = true;
                break;
            }
        }

        if (isInTask) {
            layout_child_add_action.setVisibility(View.VISIBLE);
            layout_attachment.setVisibility(View.VISIBLE);
            v_split.setVisibility(View.VISIBLE);
            //img_title_right.setVisibility(View.VISIBLE);
        } else {
            layout_child_add_action.setVisibility(View.GONE);
            layout_attachment.setVisibility(View.GONE);
            v_split.setVisibility(View.GONE);
            img_title_right.setVisibility(View.GONE);
            layout_child_Add_area.setVisibility(View.GONE);
            layout_task_testfather.setVisibility(View.GONE);
            btn_complete.setVisibility(View.GONE);
        }

        if (!IsCreator() && !IsResponsiblePerson()) {
            img_title_right.setVisibility(View.GONE);
        }
    }

    /**
     * 多部门的人员有权限
     *
     * @param id
     * @return
     */
    private boolean isMenberShortDept(String id, String xpath) {
        for (UserInfo element : MainApp.user.depts) {
            if (element.getShortDept().getId().equals(id)
                    || (null != xpath && element.getShortDept().getXpath().contains(xpath))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 任务属性设置
     */
    void updateUI_task_responsiblePerson() {
        childTastUsers.clear();
        //参与人
        if (!IsResponsiblePerson() && !IsCreator()) {
            img_title_right.setVisibility(View.GONE);
        }
        //其他情况
        switch (mTask.getStatus()) {
            case Task.STATUS_REVIEWING:
                if (IsResponsiblePerson()) {
                    img_title_right.setVisibility(View.GONE);
                }
                break;

            case Task.STATUS_FINISHED:
                if (IsResponsiblePerson()) {
                    img_title_right.setVisibility(View.GONE);
                }
                break;
        }


        if (mTask.getResponsiblePerson() != null) {
            realName = mTask.getResponsiblePerson().getName();
            tv_responsiblePerson.setText("负责人: " + realName);
            childTastUsers.add(mTask.getResponsiblePerson());
            childTastUsers.add(mTask.getCreator());
        }

        if (mTask.members != null) {
            if (mTask.members.getAllData().size() > 0) {
                StringBuffer userNames = new StringBuffer();
                for (NewUser element : mTask.members.getAllData()) {
                    userNames.append(element.getName() + " ");
                }
                tv_toUsers.setText("参与人: " + userNames.toString());
                childTastUsers.addAll(mTask.members.users);

                // 获取部门（包括子部门）的用户
                List<DBUser> deptsUsers = new ArrayList<DBUser>();
                if (null != mTask.members.depts) {
                    for (NewUser dept : mTask.members.depts) {
                        deptsUsers.addAll(OrganizationManager.shareManager().entireUsersOfDepartment(dept.getId()));
                    }
                }

                for (DBUser user : deptsUsers) {
                    childTastUsers.add(user.toShortUser());
                }
//
//                LogUtil.d("参与人:" + MainApp.gson.toJson(mTask.members));
//                LogUtil.d("子任务负责人:" + MainApp.gson.toJson(childTastUsers));
//
//                getAboutUser();

            } else {
                tv_toUsers.setText("没有参与人");
            }
        }
        if (null != mTask.getProject()) {
            beProjects = mTask.getProject().title;
            tv_task_project.setText("所属项目: " + beProjects);
        } else {
            tv_task_project.setText("所属项目：无");
        }
        if (null != mTask.getCustomerName()) {
            tv_task_aboutuser.setText("关联客户: " + mTask.getCustomerName());
        } else {
            tv_task_aboutuser.setText("关联客户: 无");
        }

        /**重复任务赋值*/
        if (null != mTask.getCornBody() && mTask.getCornBody().getType() != 0) {
            String caseName = "";
            String hourMins = "";
            String weekName = "";
            String dayName = "";

            String hour = "";
            String mins = "";
            switch (mTask.getCornBody().getType()) {
                case 1:
                    caseName = "每天";
                    break;

                case 2:
                    caseName = "每周";
                    break;

                case 3:
                    caseName = "每月";
                    break;
            }
            hour = mTask.getCornBody().getHour() + "";
            mins = mTask.getCornBody().getMinute() + "";

            /*如果小时分钟为单数，则前面拼上0*/
            if (hour.length() == 1) {
                hour = "0" + hour;
            }

            if (mins.length() == 1) {
                mins = "0" + mins;
            }
            hourMins = hour + ":" + mins;

            //每天
            if (mTask.getCornBody().getType() == 1) {
                tv_repeatTask.setText("重复: " + caseName + " " + hourMins + "重复");
                //每周
            } else if (mTask.getCornBody().getType() == 2) {
                switch (mTask.getCornBody().getWeekDay()) {
                    case 1:
                        weekName = "日";
                        break;

                    case 2:
                        weekName = "一";
                        break;

                    case 3:
                        weekName = "二";
                        break;

                    case 4:
                        weekName = "三";
                        break;

                    case 5:
                        weekName = "四";
                        break;

                    case 6:
                        weekName = "五";
                        break;

                    case 7:
                        weekName = "六";
                        break;

                    default:
                        break;
                }
                tv_repeatTask.setText("重复: " + caseName + weekName + " " + hourMins + "重复");
                //每月
            } else if (mTask.getCornBody().getType() == 3) {
                dayName = mTask.getCornBody().getDay() + "号";
                tv_repeatTask.setText("重复: " + caseName + " " + dayName + " " + hourMins + "重复");
            }
            tv_task_audit.setVisibility(View.GONE);
        } else {
            tv_repeatTask.setVisibility(View.GONE);
        }


        switch (mTask.getStatus()) {
            case 1:
                iv_task_status.setImageResource(R.drawable.icon_project_processing);
                break;

            case 2:
                iv_task_status.setImageResource(R.drawable.img_task_wite);
                break;

            case 3:
                iv_task_status.setImageResource(R.drawable.img_task_status_finish);
                break;

            default:
                break;
        }

        /**
         * 审核列表View
         * */

        if (ListUtil.IsEmpty(mTask.getReviewers())) {
            return;
        }

        layout_task_testfather.setVisibility(View.VISIBLE);
        layout_test_Add_area.removeAllViews();

        for (Reviewer reviewer : mTask.getReviewers()) {

            View mView = LayoutInflater.from(mContext).inflate(R.layout.item_tasks_testview, null);

            tv_reviewtime = (TextView) mView.findViewById(R.id.tv_reviewtime);
            tv_task_content = (TextView) mView.findViewById(R.id.tv_task_content);
            tv_task_status = (TextView) mView.findViewById(R.id.tv_task_status);
            tv_reviewer = (TextView) mView.findViewById(R.id.tv_reviewer);
            ratingBar_Task = (RatingBar) mView.findViewById(R.id.ratingBar_Task);
            item_tasks_sorece = (LinearLayout) mView.findViewById(R.id.item_tasks_sorece);

            if (!TextUtils.isEmpty(reviewer.user.getName())) {
                tv_reviewer.setText(reviewer.user.getName());
            }

            if (!TextUtils.isEmpty(reviewer.reviewedAt + "")) {
                tv_reviewtime.setText(MainApp.getMainApp().df10.format(new Date(reviewer.reviewedAt * 1000L)));
            }

            if (!TextUtils.isEmpty(reviewer.comment)) {
                tv_task_content.setText(reviewer.comment);
            }

            if ("0".equals(reviewer.status)) {
                item_tasks_sorece.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(reviewer.score + "")) {
                int rat = (reviewer.score / 20);
                ratingBar_Task.setRating((float) (rat / 1.0));
            }

            if ("1".equals(reviewer.status)) {
                tv_task_status.setText("通过");
                tv_task_status.setTextColor(getResources().getColor(R.color.green51));
                tv_task_status.setVisibility(View.GONE);
            } else {
                tv_task_status.setText("不通过");
                tv_task_status.setTextColor(getResources().getColor(R.color.red1));
            }
            layout_test_Add_area.addView(mView);
        }
    }

    /**
     * 底部按钮内容控制
     */
    void updateUI_task_base() {
        if (mTask.getCreator() != null) {
            if (mTask.getStatus() == Task.STATUS_PROCESSING && IsResponsiblePerson()) {
                //负责人提交
                btn_complete.setVisibility(View.VISIBLE);
                btn_complete.setText("提交完成");
            } else if (mTask.getStatus() == Task.STATUS_REVIEWING
                    && IsCreator()
                    && !mTask.getCreator().getId().equals(mTask.getResponsiblePerson().getId())) {
                btn_complete.setVisibility(View.VISIBLE);
                btn_complete.setText("审 核");
            } else {
                btn_complete.setVisibility(View.GONE);
            }
            if (!ListUtil.IsEmpty(mTask.getReviewComments())) {
                tv_comment.setText(mTask.getReviewComments().get(mTask.getReviewComments().size() - 1).getContent());
            }
        }

        tv_task_title.setText(mTask.getTitle());
        tv_content.setText(mTask.getContent());
        isTest = mTask.isReviewFlag() ? "是" : "否";
        tv_task_audit.setText("是否审核:" + isTest);

        vTitle = mTask.getTitle();
        vContent = mTask.getContent();

        if (mTask.getCreator() != null && mTask.getCreatedAt() > 0) {
            tv_sub_title.setText(String.format("%s %s 提交", mTask.getCreator().getRealname(),
                    app.df2.format(new Date(mTask.getCreatedAt()))));
        }

        tv_discussion_count.setText("(" + mTask.getBizExtData().getDiscussCount() + ")");
        tv_attachment_count.setText("(" + mTask.getBizExtData().getAttachmentCount() + ")");

        /*截至时间*/
        if (mTask.getPlanEndAt() > 0) {
            String s = MainApp.getMainApp().df10.format(new Date(mTask.getPlanEndAt() * 1000)) + " 截止";
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
        taskChildView.clear();

        //子任务列表内容，遍历
        for (int i = 0; i < mTask.getchecklists().size(); i++) {

            final TaskCheckPoint subTask = mTask.getchecklists().get(i);
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_task_layout, null, false);
            RelativeLayout childView = (RelativeLayout) view.findViewById(R.id.item_childtask_info);

            //子任务标题
            viewName = (TextView) view.findViewById(R.id.item_tv_child_principal);
            if (subTask.getResponsiblePerson() != null) {
                viewName.setText(subTask.getResponsiblePerson().getName());
            }

            //子任务内容
            viewContent = (TextView) view.findViewById(R.id.item_tv_child_task_content);
            if (!TextUtils.isEmpty(subTask.getcontent())) {
                viewContent.setText(subTask.getcontent());
            }
            taskChildView.add(viewContent);

            /*Checkbox勾选,赋值*/
            CheckBox childCheckbox = (CheckBox) view.findViewById(R.id.cb);
            boolean isStatus = "1".equals(subTask.getStatus()) ? true : false;

            /*子任务个数设置*/
            if (isStatus) {
                statusSize++;
            }

            /*初始化完成/未完成状态*/
            if (isStatus) {
                viewContent.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewContent.setTextColor(getResources().getColor(R.color.text99));
            }

            childCheckbox.setChecked(isStatus);

            if (mTask.getStatus() != Task.STATUS_PROCESSING) {
                childCheckbox.setEnabled(false);
                view.setEnabled(false);
                layout_child_add_action.setVisibility(View.GONE);
            } else {
                final int finalI = i;
                childCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(final CompoundButton compoundButton, final boolean isCheck) {
                        if (IsCreator() || IsResponsiblePerson() || MainApp.user.getId().equals(subTask.getResponsiblePerson().getId())) {
                            if (isCheck) {
                                viewContent = taskChildView.get(finalI);
                                statusSize++;
                                mHandler.sendEmptyMessage(0x01);
                                mHandler.sendEmptyMessage(0x02);
                                requestTaskupdates(taskId, subTask.getId(), 1);//任务ID，子任务ID，勾选状态
                            } else {
                                viewContent = taskChildView.get(finalI);
                                statusSize--;
                                mHandler.sendEmptyMessage(0x01);
                                mHandler.sendEmptyMessage(0x03);
                                requestTaskupdates(taskId, subTask.getId(), 0);
                            }
                        } else {
                            Toast("你没有操作权限");
                        }
                    }
                });
                //没有权限就不显示
                if (!(IsCreator() || IsResponsiblePerson() || MainApp.user.getId().equals(subTask.getResponsiblePerson().getId()))) {
                    childCheckbox.setVisibility(View.INVISIBLE);
                }
                /**
                 * 子任务编辑跳转
                 * */
                childView.setTag(subTask);
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        //组装 负责人 于 参与人
                        ArrayList<Reviewer> reponserData = new ArrayList<Reviewer>();
                        reponserData.addAll(mTask.responsiblePersons);
                        ArrayList<NewUser> reponserDataUser = new ArrayList<NewUser>();
                        for (Reviewer element : reponserData) {
                            reponserDataUser.add(element.user);
                        }

                        Intent intent = new Intent(TasksInfoActivity.this, ChildTaskEdit.class);
                        intent.putExtra("TaskEdit", (TaskCheckPoint) v.getTag());
                        intent.putExtra("TaskId", mTask.getId());
                        intent.putExtra("allUsers", childTastUsers);
                        if (IsCreator() || IsResponsiblePerson()) {
                            intent.putExtra("isReponser", true);
                        } else {
                            intent.putExtra("isReponser", false);
                        }
                        TasksInfoActivity.this.startActivityForResult(intent, REQUEST_EDIT_TASK);
                        TasksInfoActivity.this.overridePendingTransition(R.anim.enter_lefttoright, R.anim.exit_lefttoright);

                    }
                });
            }
            layout_child_Add_area.addView(view);
        }
        //子任务完成度(3/5)设置
        tv_children_info.setText(String.format("(%d/%d)", statusSize, mTask.getchecklists().size()));
    }

    /**
     * 更新子任务状态（完成／未完成)
     */
    void requestTaskupdates(final String id, final String cid, final int sts) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("status", sts);

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).updatesTask(id, cid, map, new RCallback<Task>() {
            @Override
            public void success(final Task task, final Response response) {
                HttpErrorCheck.checkResponse("更新子任务", response);
                Toast("更新成功");
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取任务信息【子任务等】
     */
    void getTask() {
        showLoading("");
        if (TextUtils.isEmpty(mTaskId)) {
            Toast("参数不完整");
            finish();
            return;
        }

        app.getRestAdapter().create(ITask.class).getTask(mTaskId, keyType, new RCallback<Task>() {
            @Override
            public void success(final Task task, final Response response) {
                HttpErrorCheck.checkResponse("任务详情返回", response);
                mTask = task;
                updateUI();
                showAttachment();
                taskId = task.getId(); //任务ID获取
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                finish();
            }
        });
    }

    /**
     * 标题左右监听
     */
    @Click({R.id.img_title_left, R.id.img_title_right, R.id.btn_complete, R.id.layout_cb})
    void onClick(final View v) {

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

                /*创建人*/
                if (IsCreator()) {
                    if (mTask.getStatus() == Task.STATUS_PROCESSING) {//创建者 任务进行中
                        intent.putExtra("edit", true);
                        intent.putExtra("delete", true);
                        intent.putExtra("extra", "复制任务");
                    } else if (mTask.getStatus() == Task.STATUS_REVIEWING) {
                        intent.putExtra("extra", "复制任务");
                    } else if (mTask.getStatus() == Task.STATUS_FINISHED) {
                        intent.putExtra("extra", "复制任务");
                    }
                    /*负责人*/
                } else if (IsResponsiblePerson()) {
                    switch (mTask.getStatus()) {
                        case Task.STATUS_PROCESSING:
                            intent.putExtra("edit", true);
                            intent.putExtra("editText", "修改参与人");
                            break;

                        case Task.STATUS_REVIEWING:
                            intent.putExtra("extra", "复制任务");
                            break;

                        case Task.STATUS_FINISHED:
                            intent.putExtra("extra", "复制任务");
                            break;
                    }
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

            case R.id.layout_cb:


                break;


            default:
                break;
        }
    }

    @Click(R.id.tv_upload)
    void openUpload() {
        Intent intent = new Intent(this, SelectPicPopupWindow.class);
        intent.putExtra("localpic", true);
        startActivityForResult(intent, MainApp.GET_IMG);
    }

    /**
     * 任务提交完成
     */
    void commitFinish() {
        //信鸽透传时可能task为空 ykb 07-16
        if (null != mTask && mTask.getStatus() == Task.STATUS_PROCESSING && IsResponsiblePerson()) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class)
                    .commitTask(null != mTask ? mTask.getId() : mTaskId, new RCallback<Task>() {
                        @Override
                        public void success(final Task task, final Response response) {
                            if (task != null) {
                                task.setViewed(true);
                                Intent intent = new Intent();
                                intent.putExtra("review", task);
                                app.finishActivity(TasksInfoActivity.this, MainApp.ENTER_TYPE_LEFT, 0x09, intent);
                            }
                        }

                        @Override
                        public void failure(final RetrofitError error) {
                            super.failure(error);
                            HttpErrorCheck.checkError(error);
                        }
                    });
        } else if (mTask.getStatus() == Task.STATUS_REVIEWING && mTask.getCreator().isCurrentUser()) {

            mTask.setViewed(true);
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
        if (mTask.getStatus() != Task.STATUS_PROCESSING) {
            Toast("当前状态不能添加子任务");
        } else {
            if (IsResponsiblePerson() || IsCreator()) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Task", mTask);
                bundle.putSerializable("allUsers", childTastUsers);
                app.startActivityForResult(this, ChildTaskAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CREATE_SUB, bundle);
            } else {
                Toast("你没有创建子任务权限!");
            }
        }
    }


    void showAttachment() {
        if (null == mTask.getAttachments() || mTask.getAttachments().size() == 0) {
            return;
        }
        tv_attachment_count.setText("(" + (mTask.getAttachments() == null ? 0 : mTask.getAttachments().size()) + ")");
    }

    @OnActivityResult(REQUEST_CREATE_SUB)
    void onNewSubTaskActivityResult(final int resultCode, final Intent data) {
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
            mTask.setViewed(true);
            intent.putExtra("data", mTask);
        }
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isUpdate ? 0x09 : RESULT_OK, intent);
    }

    /**
     * 修改参与人
     */
    void editJoiner() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", mTask.getTitle());
        map.put("content", mTask.getContent());
        map.put("responsiblePerson", mTask.getResponsiblePerson());
        map.put("members", member);
        map.put("planendAt", mTask.getPlanEndAt());
        map.put("remindflag", mTask.getRemindTime() > 0);
        map.put("remindtime", mTask.getRemindTime());
        map.put("reviewFlag", mTask.isReviewFlag());
        map.put("attachmentUUId", uuid);

        if (!TextUtils.isEmpty(mTask.getProjectId())) {
            map.put("projectId", mTask.getProjectId());
        }
        LogUtil.d("修改参与人传递数据：" + app.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).updateJioner(mTask.getId(), map, new RCallback<Task>() {
            @Override
            public void success(final Task task, final Response response) {
                Toast("修改参与人成功");
                getTask();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }


    /**
     * 附件上传
     */
    @Click(R.id.layout_attachment)
    void clickAttachment() {
        if (null == mTask) {
            return;
        }

        /*任务Status: 1-未完成 2-待点评 3-完成*/
        if (mTask.getStatus() == 2 || mTask.getStatus() == 3) {
            isOver = true;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("data", mTask.getAttachments());
        bundle.putSerializable("uuid", mTask.getAttachmentUUId());
        bundle.putBoolean("isOver", isOver);
        bundle.putInt("bizType", 2);

        ArrayList<NewUser> users = new ArrayList<>();
        if (mTask.getMembers() != null) {
            users.addAll(mTask.getMembers().getAllData());
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

    /*讨论*/
    @Click(R.id.layout_discussion)
    void clickDiscussion() {
        if (null == mTask) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("attachmentUUId", mTask.getAttachmentUUId());
        bundle.putBoolean("isMyUser", IsCreator() || IsResponsiblePerson() ? true : false);
        bundle.putInt("status", mTask.getStatus());
        bundle.putInt("bizType", 2);
        DiscussDetialActivity.startThisActivity(this, 2, mTask.getAttachmentUUId(), mTask.getStatus(), MSG_DISCUSSION);
        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    /**
     * 是否是任务的创建者
     *
     * @return
     */
    public boolean IsCreator() {
        return null != mTask.getCreator() ? mTask.getCreator().isCurrentUser() : false;
    }

    /**
     * 是否是任务负责人
     *
     * @return
     */
    public boolean IsResponsiblePerson() {
        return null != mTask.getResponsiblePerson() ? mTask.getResponsiblePerson().isCurrentUser() : false;
    }


//    /**
//     * 参与人当中的部门，拆分成员工
//     */
//    void getAboutUser() {
//
//        requestDepts.addAll(mTask.members.depts);
//
//        for (Department department : deptSource) {
//            for (NewUser newUser : requestDepts) {
//                try {
//                    if (department.getId().equals(newUser.getId())) {
//                        aboutDepts.addAll(department.getUsers());
//                    }
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        for (User user : aboutDepts) {
//            childTastUsers.add(user.toShortUser());
//        }
//    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {

        if (FinalVariables.PICK_INVOLVE_USER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            member = Compat.convertStaffCollectionToMembers(collection);
            if (null == member || (member.users.size()==0 && member.depts.size()==0)) {
                tv_toUsers.setText("无参与人");
            } else {
                joinName = new StringBuffer();
                joinUserId = new StringBuffer();
                if (null != member.depts) {
                    for (NewUser newUser : member.depts) {
                        joinName.append(newUser.getName() + ",");
                        joinUserId.append(newUser.getId() + ",");
                    }
                }
                if (null != member.users) {
                    for (NewUser newUser : member.users) {
                        joinName.append(newUser.getName() + ",");
                        joinUserId.append(newUser.getId() + ",");
                    }
                }
                if (!TextUtils.isEmpty(joinName)) {
                    joinName.deleteCharAt(joinName.length() - 1);
                }
                editJoiner();
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            /*选择完参与人后，回调*/
            case SelectDetUserActivity2.REQUEST_ALL_SELECT:
                member = (Members) data.getSerializableExtra("data");
                joinName = new StringBuffer();
                joinUserId = new StringBuffer();
                if (member.users.size() == 0 && member.depts.size() == 0) {
                    Toast("未选择相关人员");
                    joinUserId.reverse();
                } else {
                    if (null != member.depts) {
                        for (NewUser newUser : member.depts) {
                            joinName.append(newUser.getName() + ",");
                            joinUserId.append(newUser.getId() + ",");
                        }
                    }
                    if (null != member.users) {
                        for (NewUser newUser : member.users) {
                            joinName.append(newUser.getName() + ",");
                            joinUserId.append(newUser.getId() + ",");
                        }
                    }
                    if (!TextUtils.isEmpty(joinName)) {
                        joinName.deleteCharAt(joinName.length() - 1);
                    }
                    editJoiner();
                }
                break;

            //编辑 子任务 返回
            case REQUEST_EDIT_TASK:
                layout_child_Add_area.removeAllViews();
                getTask();
                break;

            case REQUEST_SCORE:
                getTask();
                break;

            case REQUEST_EDIT:
                getTask();
                break;

            case REQUEST_EDIT_DELETE:

                /*编辑回调 创建人可编辑 负责人只能修改参与人*/
                if (data.getBooleanExtra("edit", false)) {
                    if (IsCreator()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("mTask", mTask);
                        bundle.putBoolean("type", IsCreator());
                        app.startActivityForResult(this, TasksEditActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_EDIT, bundle);
                    } else {
//                        SelectDetUserActivity2.startThisForAllSelect(this,
//                                joinUserId == null ? null : joinUserId.toString(), true);
                        {
                            StaffMemberCollection collection = Compat.convertMembersToStaffCollection(member);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
                            if (collection != null) {
                                bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
                            }
                            bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_INVOLVE_USER_REQUEST);
                            Intent intent = new Intent();
                            intent.setClass(this, ContactPickerActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                    isUpdate = true;
                 /*删除回调*/
                } else if (data.getBooleanExtra("delete", false)) {
                    app.getRestAdapter().create(ITask.class).deleteTask(mTask.getId(), new RCallback<Task>() {
                        @Override
                        public void success(final Task o, final Response response) {
                            Intent intent = new Intent();
                            intent.putExtra("delete", mTask);
                            app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, 0x09, intent);
                        }
                    });
                 /*复制回调*/
                } else if (data.getBooleanExtra("extra", false)) {
                    Intent intent = new Intent(TasksInfoActivity.this, TasksAddActivity_.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("data", mTask);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                 /*修改参与人回调*/
                } else if (data.getBooleanExtra("editjoiner", false)) {
//                    SelectDetUserActivity2.startThisForAllSelect(this,
//                            joinUserId == null ? null : joinUserId.toString(), true);
                    {
                        StaffMemberCollection collection = Compat.convertMembersToStaffCollection(member);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
                        if (collection != null) {
                            bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
                        }
                        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_INVOLVE_USER_REQUEST);
                        Intent intent = new Intent();
                        intent.setClass(this, ContactPickerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    isUpdate = true;
                }
                break;

            case MSG_ATTACHMENT:
                if (data == null || data.getExtras() == null) {
                    return;
                }
                ArrayList<Attachment> attachments = (ArrayList<Attachment>) data.getSerializableExtra("data");
                try {
                    mTask.setAttachments(attachments);
                    showAttachment();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;

            case MSG_DISCUSSION:
                if (data == null || data.getExtras() == null) {
                    return;
                }

                mPageDiscussion = (PaginationX<Discussion>) data.getSerializableExtra("data");
                showDiscussion();

                break;

            default:
                break;
        }
    }

}
