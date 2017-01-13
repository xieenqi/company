package com.loyo.oa.v2.activityui.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.common.click.NoDoubleClickListener;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SwitchView;
import com.loyo.oa.v2.activityui.customer.SelfVisibleCustomerPickerActivity;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.other.CommonAdapter;
import com.loyo.oa.v2.activityui.other.ViewHolder;
import com.loyo.oa.v2.activityui.project.ProjectSearchActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.customview.RepeatTaskView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.task.api.TaskService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hk.ids.gws.android.sclick.SClick;

@EActivity(R.layout.activity_tasks_edit) //本Activity的布局文件
public class TasksEditActivity extends BaseActivity {

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    ViewGroup layout_del;
    @ViewById
    ViewGroup layout_responsiblePerson;
    @ViewById
    ViewGroup layout_deadline;
    @ViewById
    ViewGroup layout_remind;
    @ViewById
    ViewGroup layout_project;
    @ViewById
    ViewGroup layout_retask;
    @ViewById
    ViewGroup task_ll_deadline;
    @ViewById
    ViewGroup linear_task_approve;

    @ViewById
    View view_task_approve;
    @ViewById
    View layout_retask_view;
    @ViewById
    ImageView img_title_right_toUsers;
    @ViewById
    TextView tv_responsiblePerson;
    @ViewById
    TextView tv_toUsers;
    @ViewById
    TextView tv_deadline;
    @ViewById
    TextView tv_remind;
    @ViewById
    TextView tv_mycustomer;
    @ViewById
    TextView tv_Project;
    @ViewById
    TextView tv_retask;
    @ViewById
    SwitchView switch_approve;
    @ViewById
    EditText edt_content;
    @ViewById
    EditText edt_title;
    @ViewById
    GridView gridView_photo;
    @Extra
    Task mTask;
    @Extra("type")
    Boolean isCreator;

    private AlertDialog dialog_Product;
    private String uuid = StringUtil.getUUID();
    private ArrayList<OrganizationalMember> userss;
    private ArrayList<OrganizationalMember> depts;
    private Members member;
    private OrganizationalMember newUser;
    private StringBuffer joinName = new StringBuffer();
    private StringBuffer joinUserId = new StringBuffer();
    private boolean isState;
    private boolean isKind;


    @AfterViews
        //类似onCreate方法执行入口
    void initUI() {

        super.setTitle("编辑任务");
        Global.SetTouchView(img_title_left,
                img_title_right,
                layout_responsiblePerson,
                layout_deadline,
                layout_del,
                layout_remind, layout_project);

        userss = new ArrayList<>();
        depts = new ArrayList<>();
        member = new Members();

        UpdateUI();
    }

    void UpdateUI() {

        if (null != mTask.getResponsiblePerson()) {
            tv_responsiblePerson.setText(mTask.getResponsiblePerson().getName());
        }

        /*判断是否为创建人，否则负责人只能修改参与人*/
        if (!isCreator) {
            edt_title.setEnabled(false);
            edt_content.setEnabled(false);
            gridView_photo.setEnabled(false);
            layout_deadline.setEnabled(false);
            layout_remind.setEnabled(false);
            layout_responsiblePerson.setEnabled(false);
            layout_project.setEnabled(false);
            switch_approve.setEnabled(false);
        }

        savePostData();
        tv_toUsers.setText(joinName.toString());
        if (mTask.getPlanEndAt() != 0) {
//            tv_deadline.setText(MainApp.getMainApp().df10.format(new Date(mTask.getPlanEndAt() * 1000)));
            tv_deadline.setText(DateTool.getDateTimeFriendly(mTask.getPlanEndAt()));
        }
        tv_remind.setText(Task.GetRemindText(mTask.getRemindTime()));

        switch_approve.setState(mTask.getResponsiblePerson().isCurrentUser() ? false : mTask.isReviewFlag());
        linear_task_approve.setVisibility(mTask.getResponsiblePerson().isCurrentUser() ? View.GONE : View.VISIBLE);
        edt_content.setText(mTask.getContent());
        edt_title.setText(mTask.getTitle());
        tv_mycustomer.setText(mTask.getCustomerName());
        if (mTask.getProject() != null) {
            tv_Project.setText(mTask.getProject().title);
        }

        if (null != mTask && null != mTask.getCornBody() && 0 != mTask.getCornBody().getType()) {
            isKind = true;
            task_ll_deadline.setVisibility(View.GONE);
            linear_task_approve.setVisibility(View.GONE);
            view_task_approve.setVisibility(View.GONE);

        } else {
            isKind = false;
            layout_retask.setVisibility(View.GONE);
            layout_retask_view.setVisibility(View.GONE);
        }
        setCornBodyinfo();
    }

    /**
     * 重复任务数据拆解
     */
    public void setCornBodyinfo() {

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
                tv_retask.setText(caseName + " " + hourMins);
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
                tv_retask.setText(caseName + weekName + " " + hourMins);
                //每月
            } else if (mTask.getCornBody().getType() == 3) {
                dayName = mTask.getCornBody().getDay() + "号";
                tv_retask.setText(caseName + " " + dayName + " " + hourMins);
            }
        }
    }

    /*POST数据保存，防止不作任何编辑操作，没有POST数据*/
    void savePostData() {

        for (int i = 0; i < mTask.getMembers().getAllData().size(); i++) {

            joinName.append(mTask.getMembers().getAllData().get(i).getName() + ",");
            joinUserId.append(mTask.getMembers().getAllData().get(i).getId() + ",");

            OrganizationalMember newUser = new OrganizationalMember();
            newUser.setName(mTask.members.getAllData().get(i).getName());
            newUser.setId(mTask.getMembers().getAllData().get(i).getId());
            userss.add(newUser);

        }

        member.users = userss;
        newUser = mTask.getResponsiblePerson();

    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_responsiblePerson, R.id.layout_deadline, R.id.tv_toUsers, R.id.layout_del, R.id.layout_project, R.id.layout_mycustomer, R.id.layout_retask})
    void onClick(final View v) {
        v.setOnClickListener(new NoDoubleClickListener(5000) {
            @Override
            public void onNoDoubleClick(View v) {
                switch (v.getId()) {
                    case R.id.img_title_left:
                        app.finishActivity(TasksEditActivity.this, MainApp.ENTER_TYPE_LEFT, 0, null);
                        break;
                    case R.id.img_title_right:
                        String title = edt_title.getText().toString().trim();

                        if (TextUtils.isEmpty(title)) {
                            Toast(getString(R.string.app_title) + getString(R.string.app_no_null));
                            break;
                        }

                        String content = edt_content.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                            break;
                        }

                        if (mTask.getPlanEndAt() <= 0 && tv_retask.getText().toString().trim().isEmpty()) {
                            Toast("截止日期或重复任务必选一个功能！");
                            break;
                        }

                        if (tv_deadline.getText().toString().equals("不截止") && tv_retask.getText().toString().equals("不重复")) {
                            Toast("截止日期或重复任务必选一个功能！");
                            break;
                        }


                        if (tv_deadline.getText().toString().equals("不截止") && tv_retask.getText().toString().trim().isEmpty()) {
                            Toast("截止日期或重复任务必选一个功能！");
                            break;
                        }

                        if (tv_retask.getText().toString().equals("不重复") && mTask.getPlanEndAt() <= 0) {
                            Toast("截止日期或重复任务必选一个功能！");
                            break;
                        }


                        if (mTask.getResponsiblePerson() == null || TextUtils.isEmpty(mTask.getResponsiblePerson().getId())) {
                            Toast("负责人" + getString(R.string.app_no_null));
                            break;
                        }
                        requestCommitTask(title, content);
                        break;


            /*编辑负责人*/
                    case R.id.layout_responsiblePerson: {
                        StaffMemberCollection collection = Compat.convertNewUserToStaffCollection(newUser);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                        if (collection != null) {
                            bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
                        }
                        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_RESPONSIBLE_USER_REQUEST);
                        Intent intent = new Intent();
                        intent.setClass(TasksEditActivity.this, ContactPickerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    break;

            /*编辑参与人*/
                    case R.id.tv_toUsers: {
                        StaffMemberCollection collection = Compat.convertMembersToStaffCollection(member);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
                        if (collection != null) {
                            bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
                        }
                        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_INVOLVE_USER_REQUEST);
                        Intent intent = new Intent();
                        intent.setClass(TasksEditActivity.this, ContactPickerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    break;


                    //重复任务
                    case R.id.layout_retask:
                        setRepeatTask();
                        break;

            /*截至时间*/
                    case R.id.layout_deadline:
                        setDeadLine();
                        break;


                    case R.id.layout_del:
                        userss.clear();
                        depts.clear();
                        tv_toUsers.setText("");
                        layout_del.setVisibility(View.GONE);
                        img_title_right_toUsers.setVisibility(View.VISIBLE);
                        break;

            /*关联项目*/
                    case R.id.layout_project:
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("from", TASKS_ADD);
                        bundle2.putInt(ExtraAndResult.EXTRA_STATUS, 1);
                        app.startActivityForResult(TasksEditActivity.this, ProjectSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_SELECT_PROJECT, bundle2);
                        break;

            /*关联客户*/
                    case R.id.layout_mycustomer:
                        app.startActivityForResult(TasksEditActivity.this, SelfVisibleCustomerPickerActivity.class,
                                MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, null);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    void requestCommitTask(String title, String content) {
        showCommitLoading();
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("responsiblePerson", newUser);
        map.put("members", member);
        map.put("attachmentUUId", uuid);
        map.put("customerId", mTask.getCustomerId());
        map.put("customerName", mTask.getCustomerName());

        if (switch_approve.getState() == 4) {
            isState = true;
        } else if (switch_approve.getState() == 1) {
            isState = false;
        }

        if (!TextUtils.isEmpty(mTask.getProjectId())) {
            map.put("projectId", mTask.getProjectId());
        }

        if (isKind) {
            map.put("cornBody", mTask.getCornBody());
        } else if (!isKind) {
            map.put("planendAt", mTask.getPlanEndAt());
            map.put("remindflag", mTask.getRemindTime() > 0);
            map.put("remindtime", mTask.getRemindTime());
            map.put("reviewFlag", isState);
        }
        TaskService.updateTask(mTask.getId(), map)
                .subscribe(new DefaultLoyoSubscriber<Task>(hud) {
                    @Override
                    public void onNext(final Task task) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d("------------------爸爸不不不不不不------------------");
                                task.setViewed(true);
                                Intent intent = new Intent();
                                intent.putExtra("data", task);
                                setResult(Activity.RESULT_OK, intent);
                                onBackPressed();
                            }
                        }, 2000);
                    }
                });
    }

    void setDeadLine() {
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {
                long time = DateTool.getStamp(year, month, day, hour, min, 0);
                tv_deadline.setText(DateTool.getDateTimeFriendly(time));
                mTask.setPlanEndAt(time);


                isKind = false;
                layout_retask.setVisibility(View.GONE);
                layout_retask_view.setVisibility(View.GONE);
                layout_remind.setEnabled(true);
                tv_remind.setTextColor(mContext.getResources().getColor(R.color.title_bg1));
            }

            @Override
            public void onCancel() {
                mTask.setPlanEndAt(0);
                isKind = true;
                tv_deadline.setText("不截止");
                tv_remind.setTextColor(mContext.getResources().getColor(R.color.text99));
                layout_remind.setEnabled(false);
                layout_retask.setVisibility(View.VISIBLE);
                layout_retask_view.setVisibility(View.VISIBLE);
            }
        }, false, "不截止");
    }

    /**
     * 重复任务数据初始化
     */
    void setRepeatParam(String[] str) {

        if (str[0].equals("每天")) {
            mTask.getCornBody().setType(1);
        } else if (str[0].equals("每周")) {
            mTask.getCornBody().setType(2);

            if (str[1].equals("周一")) {
                mTask.getCornBody().setWeekDay(2);
            } else if (str[1].equals("周二")) {
                mTask.getCornBody().setWeekDay(3);
            } else if (str[1].equals("周三")) {
                mTask.getCornBody().setWeekDay(4);
            } else if (str[1].equals("周四")) {
                mTask.getCornBody().setWeekDay(5);
            } else if (str[1].equals("周五")) {
                mTask.getCornBody().setWeekDay(6);
            } else if (str[1].equals("周六")) {
                mTask.getCornBody().setWeekDay(7);
            } else if (str[1].equals("周日")) {
                mTask.getCornBody().setWeekDay(1);
            }

        } else if (str[0].equals("每月")) {
            mTask.getCornBody().setType(3);
            mTask.getCornBody().setDay(Integer.parseInt(str[1].replaceAll("号", "")));
        }

        mTask.getCornBody().setHour(Integer.parseInt(str[2].replaceAll("时", "")));
        mTask.getCornBody().setMinute(Integer.parseInt(str[3].replaceAll("分", "")));
    }


    /**
     * 重复任务功能
     */
    void setRepeatTask() {
        final RepeatTaskView repeatTaskView = new RepeatTaskView(this);
        repeatTaskView.setCanceledOnTouchOutside(true);
        repeatTaskView.show();
        repeatTaskView.setConfirmOnClick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                isKind = true;
                String[] cityArr = repeatTaskView.getResult();
                setRepeatParam(cityArr);
                String hour = cityArr[2];
                String mins = cityArr[3];
                if (hour.equals("0时")) {
                    hour = "0" + hour;
                }

                if (mins.equals("0分")) {
                    mins = "0" + mins;
                }

                if (cityArr[1].equals("无")) {
                    tv_retask.setText(cityArr[0] + " " + hour.replaceAll("时", "") + ":" + mins.replaceAll("分", ""));
                } else {
                    tv_retask.setText(cityArr[0] + " " + cityArr[1] + " " + hour.replaceAll("时", "") + ":" + mins.replaceAll("分", ""));
                }
                task_ll_deadline.setVisibility(View.GONE);
                view_task_approve.setVisibility(View.GONE);
                linear_task_approve.setVisibility(View.GONE);
                repeatTaskView.dismiss();


            }
        });

        repeatTaskView.setCancelOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isKind = false;
                tv_retask.setText("不重复");
                task_ll_deadline.setVisibility(View.VISIBLE);
                view_task_approve.setVisibility(View.VISIBLE);
                linear_task_approve.setVisibility(View.VISIBLE);
                repeatTaskView.dismiss();
            }
        });
    }


    @Click(R.id.layout_remind)
    void remindonClick() {
        if (dialog_Product == null) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_products_select, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout);
            dialog_Product = builder.create();
        }
        dialog_Product.show();
        ListView listView_products = (ListView) dialog_Product.findViewById(R.id.listView);

        final RemindAdapter productsRadioListViewAdapter = new RemindAdapter(this, Task.RemindList, R.layout.item_listview_product_select);
        listView_products.setAdapter(productsRadioListViewAdapter);
        listView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

                mTask.setRemindTime(Task.RemindListSource.get(position));
                tv_remind.setText(Task.RemindList.get(position));
                dialog_Product.dismiss();
            }
        });
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {

        if (FinalVariables.PICK_RESPONSIBLE_USER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            newUser = Compat.convertStaffCollectionToNewUser(collection);
            if (newUser == null) {
                tv_responsiblePerson.setText("无负责人");
            } else {
                tv_responsiblePerson.setText(newUser.getName());
            }
        } else if (FinalVariables.PICK_INVOLVE_USER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            member = Compat.convertStaffCollectionToMembers(collection);
            if (null == member || (member.users.size() == 0 && member.depts.size() == 0)) {
                tv_toUsers.setText("无参与人");
            } else {
                joinName = new StringBuffer();
                joinUserId = new StringBuffer();
                if (null != member.depts) {
                    for (OrganizationalMember newUser : member.depts) {
                        joinName.append(newUser.getName() + ",");
                        joinUserId.append(newUser.getId() + ",");
                    }
                }
                if (null != member.users) {
                    for (OrganizationalMember newUser : member.users) {
                        joinName.append(newUser.getName() + ",");
                        joinUserId.append(newUser.getId() + ",");
                    }
                }
                if (!TextUtils.isEmpty(joinName)) {
                    joinName.deleteCharAt(joinName.length() - 1);
                }
                tv_toUsers.setText(joinName.toString());
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
                        /*关联客户回调*/
            case ExtraAndResult.REQUEST_CODE_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                if (null != customer) {
                    mTask.setCustomerId(customer.id);
                    mTask.setCustomerName(customer.name);
                    tv_mycustomer.setText(customer.name);
                } else {
                    mTask.setCustomerId("");
                    mTask.setCustomerName("");
                    tv_mycustomer.setText("无");
                }
                break;


            /*所属项目回调*/
            case FinalVariables.REQUEST_SELECT_PROJECT:
                Project project = (Project) data.getSerializableExtra("data");
                if (null != project) {
                    tv_Project.setText(project.title);
                    mTask.setProjectId(project.getId());
                } else {
                    mTask.setProjectId("");
                    tv_Project.setText("无");
                }
                break;

            //用户单选, 负责人
            case FinalVariables.REQUEST_ONLY:
                OrganizationalMember u = (OrganizationalMember) data.getSerializableExtra("data");
                newUser = u;
                tv_responsiblePerson.setText(newUser.getName());
                break;
            //用户选择, 参与人
            case FinalVariables.REQUEST_ALL_SELECT:
                member = (Members) data.getSerializableExtra("data");
                if (null == member) {
                    tv_toUsers.setText("无参与人");
                } else {
                    joinName = new StringBuffer();
                    joinUserId = new StringBuffer();
                    if (null != member.depts) {
                        for (OrganizationalMember newUser : member.depts) {
                            joinName.append(newUser.getName() + ",");
                            joinUserId.append(newUser.getId() + ",");
                        }
                    }
                    if (null != member.users) {
                        for (OrganizationalMember newUser : member.users) {
                            joinName.append(newUser.getName() + ",");
                            joinUserId.append(newUser.getId() + ",");
                        }
                    }
                    if (!TextUtils.isEmpty(joinName)) {
                        joinName.deleteCharAt(joinName.length() - 1);
                    }
                    tv_toUsers.setText(joinName.toString());
                }
                break;

            default:
                break;
        }
    }

    public class RemindAdapter extends CommonAdapter<String> {
        public RemindAdapter(final Context context, final List<String> datas, final int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(final ViewHolder holder, final String s) {
            holder.setText(R.id.tv, s);
        }
    }
}
