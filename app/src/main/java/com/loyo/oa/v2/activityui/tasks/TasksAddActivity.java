package com.loyo.oa.v2.activityui.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.commonview.SwitchView;
import com.loyo.oa.v2.activityui.customer.CustomerSearchActivity;
import com.loyo.oa.v2.activityui.other.CommonAdapter;
import com.loyo.oa.v2.activityui.other.ViewHolder;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.project.ProjectSearchActivity;
import com.loyo.oa.v2.activityui.tasks.bean.CornBody;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.PostBizExtData;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.customview.RepeatTaskView;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * 【创建任务】 页面
 */
@EActivity(R.layout.activity_tasks_add)
public class TasksAddActivity extends BaseActivity {

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
    View layout_retask_view;
    @ViewById
    View view_task_approve;
    @ViewById
    ImageView img_title_right_toUsers;
    @ViewById
    TextView wordcount;
    @ViewById
    TextView tv_retask;
    @ViewById
    TextView tv_responsiblePerson;
    @ViewById
    TextView tv_toUsers;
    @ViewById
    TextView tv_deadline;
    @ViewById
    TextView tv_remind;
    @ViewById
    TextView tv_Project;
    @ViewById
    TextView tv_mycustomer;
    @ViewById
    SwitchView switch_approve;
    @ViewById
    EditText edt_content;
    @ViewById
    EditText edt_title;
    @ViewById
    CusGridView gridView_photo;
    @Extra
    String projectId;
    @Extra
    String projectTitle;
    @ViewById
    ViewGroup layout_mycustomer;
    @Extra("data")
    Task mTask;
    @Extra(ExtraAndResult.EXTRA_ID)
    String customerId;
    @Extra(ExtraAndResult.EXTRA_NAME)
    String customerName;

    private AlertDialog dialog_Product;
    private ImageGridViewAdapter imageGridViewAdapter;
    private NewUser newUser;
    private CornBody cornBody;
    private StringBuffer strBuf;
    private Members members;
    private PostBizExtData bizExtData;
    private ArrayList<NewUser> userss;
    private ArrayList<NewUser> depts;
    private long mDeadline;
    private int remindTime;
    private int mRemind = 0;
    private int bizType = 2;
    private int uploadSize;
    private int uploadNum;
    private boolean isCopy;
    private boolean isState = true;
    private boolean isKind;//true:重复 //截止

    private String title;
    private String content;
    private StringBuffer joinName;
    private StringBuffer joinUserId;
    private String uuid = StringUtil.getUUID();
    private ArrayList<ImageInfo> pickPhots = new ArrayList<>();
    private List<String> mSelectPath;
    private ArrayList<ImageInfo> pickPhotsResult;

    @AfterViews
    void initUI() {
        super.setTitle("创建任务");
        Global.SetTouchView(img_title_left,
                img_title_right,
                layout_responsiblePerson,
                layout_deadline,
                layout_del,
                layout_remind, layout_project);

        userss = new ArrayList<>();
        depts = new ArrayList<>();
        members = new Members();
        cornBody = new CornBody();
        strBuf = new StringBuffer();
        switch_approve.setState(true);
        edt_content.addTextChangedListener(new CountTextWatcher(wordcount));
        init_gridView_photo();
//        setTouchView(-1);
        getTempTask();

        projectAddTask();
        if (!TextUtils.isEmpty(customerId)) {
            tv_mycustomer.setText(customerName);
            layout_mycustomer.setEnabled(false);
        }
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
            members = Compat.convertStaffCollectionToMembers(collection);
            if (null == members || (members.users.size() == 0 && members.depts.size() == 0)) {
                tv_toUsers.setText("无参与人");
            } else {
                joinName = new StringBuffer();
                joinUserId = new StringBuffer();
                if (null != members.depts) {
                    for (NewUser newUser : members.depts) {
                        joinName.append(newUser.getName() + ",");
                        joinUserId.append(newUser.getId() + ",");
                    }
                }
                if (null != members.users) {
                    for (NewUser newUser : members.users) {
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

    /**
     * 项目 过来 创建任务
     */
    public void projectAddTask() {
        if (!TextUtils.isEmpty(projectId)) {
            layout_project.setEnabled(false);
            tv_Project.setText(projectTitle);
        }
    }

    /**
     * 复制任务，接受到的数据绑定
     */
    void getBundle() {

        for (int i = 0; i < mTask.getMembers().getAllData().size(); i++) {
            strBuf.append(mTask.getMembers().getAllData().get(i).getName() + ",");
        }

        edt_title.setText(mTask.getTitle());
        edt_content.setText(mTask.getContent());
        tv_responsiblePerson.setText(mTask.getResponsiblePerson().getName());
        tv_toUsers.setText(strBuf.toString());
        switch_approve.setState(true);
        isCopy = mTask != null ? true : false;
        members.users = mTask.getMembers().users; //参与人
        newUser = mTask.getResponsiblePerson();   //负责人

    }

    void getTempTask() {
        if (mTask == null) {
            return;
        }

        if (!TextUtils.isEmpty(mTask.getResponsiblePersonId()) && !StringUtil.isEmpty(mTask.getResponsiblePersonName())) {
            User u = new User();
            u.id = mTask.getResponsiblePersonId();
            u.realname = mTask.getResponsiblePersonName();
            setResponsiblePersion(u);
        }

        if (!TextUtils.isEmpty(projectTitle)) {
            tv_Project.setText(projectTitle);
        }
        getBundle();

    }

    /**
     * 图片列表绑定
     */
    void init_gridView_photo() {
        imageGridViewAdapter = new ImageGridViewAdapter(this, true, true, 0, pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
    }

    /**
     * 新建任务 POST提交
     */

    void requestCommitTask() {
        if (pickPhots.size() == 0) {
            showLoading("正在提交");
            //showDialogLoading(false);
        }
        bizExtData = new PostBizExtData();
        bizExtData.setAttachmentCount(pickPhots.size());
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("responsiblePerson", newUser);
        map.put("members", members);
        map.put("bizExtData", bizExtData); //上传图片的数量
        map.put("attachmentUUId", uuid);
        map.put("customerId", customerId);
        map.put("customerName", customerName);

        if (switch_approve.getState() == 1) {
            isState = false;
        } else if (switch_approve.getState() == 4) {
            isState = true;
        }

        if (!TextUtils.isEmpty(projectId)) {
            map.put("projectId", projectId);
        }

        if (isKind) {
            map.put("cornBody", cornBody);
        } else if (!isKind) {
            if (mRemind > 0) {
                map.put("remindflag", mRemind > 0);
                map.put("remindtime", remindTime);
            }
            map.put("reviewFlag", isState);
            map.put("planendAt", mDeadline);
        }


        LogUtil.d("任务创建 发送的数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).create(map, new RCallback<Task>() {
            @Override
            public void success(final Task task, final Response response) {
                //HttpErrorCheck.checkCommitSus(response);
                HttpErrorCheck.checkResponse(response);
                //不需要保存
                cancelLoading();
                isSave = false;
                Intent intent = new Intent();
                intent.putExtra("data", task);
                setResult(0x09, intent);
                onBackPressed();
                if (isCopy)
                    TasksInfoActivity.instance.finish();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                //HttpErrorCheck.checkCommitEro(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_responsiblePerson,
            R.id.layout_deadline, R.id.tv_toUsers, R.id.layout_del, R.id.layout_project, R.id.layout_mycustomer, R.id.layout_retask})
    void onClick(final View v) {
        switch (v.getId()) {

            case R.id.img_title_left:
                onBackPressed();
                break;

            //提交任务
            case R.id.img_title_right:
                title = edt_title.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    Toast(getString(R.string.app_title) + getString(R.string.app_no_null));
                    break;
                }

                content = edt_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    break;
                }

                if (mDeadline <= 0 && tv_retask.getText().toString().trim().isEmpty()) {
                    Toast("截止日期或重复任务必选一个功能！");
                    break;
                }

                if (TextUtils.isEmpty(tv_deadline.getText().toString()) && tv_retask.getText().toString().equals("不重复")) {
                    Toast("截止日期或重复任务必选一个功能！");
                    break;
                }

                if (TextUtils.isEmpty(tv_deadline.getText().toString()) && tv_retask.getText().toString().trim().isEmpty()) {
                    Toast("截止日期或重复任务必选一个功能！");
                    break;
                }

                if (TextUtils.isEmpty(tv_retask.getText().toString()) && mDeadline <= 0) {
                    Toast("截止日期或重复任务必选一个功能！");
                    break;
                }


                if (newUser == null || TextUtils.isEmpty(newUser.getId())) {
                    Toast("负责人" + getString(R.string.app_no_null));
                    break;
                }
                //没有附件
                if (pickPhots.size() == 0) {
                    requestCommitTask();
                    //有附件
                } else {
                    img_title_right.setEnabled(false);
                    newUploadAttachement();
                }
                break;

            //重复任务
            case R.id.layout_retask:
                setRepeatTask();
                break;

            //截至时间
            case R.id.layout_deadline:
                setDeadLine();
                break;

            //负责人选项
            case R.id.layout_responsiblePerson: {
                StaffMemberCollection collection = Compat.convertNewUserToStaffCollection(newUser);
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                if (collection != null) {
                    bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
                }
                bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_RESPONSIBLE_USER_REQUEST);
                Intent intent = new Intent();
                intent.setClass(this, ContactPickerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            break;

            //参与人选项
            case R.id.tv_toUsers: {
                StaffMemberCollection collection = Compat.convertMembersToStaffCollection(members);
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
            break;

            case R.id.layout_del:
                userss.clear();
                depts.clear();
                tv_toUsers.setText("");
                layout_del.setVisibility(View.GONE);
                img_title_right_toUsers.setVisibility(View.VISIBLE);
                break;

            /*所属项目*/
            case R.id.layout_project:
                Bundle bundle2 = new Bundle();
                bundle2.putInt("from", TASKS_ADD);
                bundle2.putInt(ExtraAndResult.EXTRA_STATUS, 1);
                app.startActivityForResult(this, ProjectSearchActivity.class, MainApp.ENTER_TYPE_RIGHT,
                        FinalVariables.REQUEST_SELECT_PROJECT, bundle2);
                break;

            /*关联客户*/
            case R.id.layout_mycustomer:
                Bundle bundle3 = new Bundle();
                bundle3.putInt("from", TASKS_ADD_CUSTOMER);
                bundle3.putInt(ExtraAndResult.EXTRA_TYPE, 1);
                app.startActivityForResult(this, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_SELECT_CUSTOMER, bundle3);
                break;

            default:
                break;
        }
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
                mRemind = Task.RemindListSource.get(position);
                remindTime = Task.RemindListSource.get(position);
                tv_remind.setText(Task.RemindList.get(position));
                dialog_Product.dismiss();
            }
        });
    }

    /**
     * 截至日期选择框
     */
    void setDeadLine() {
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {
//                String str = year + "." + String.format("%02d", (month + 1)) + "." +
//                        String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
//                tv_deadline.setText(str);
//                mDeadline = Long.parseLong(DateTool.getDataOne(str, "yyyy.MM.dd HH:mm"));

                mDeadline= com.loyo.oa.common.utils.DateTool.getStamp(year, month, day,hour,min,0);
                tv_deadline.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(mDeadline));


                isKind = false;
                layout_retask.setVisibility(View.GONE);
                layout_retask_view.setVisibility(View.GONE);
                layout_remind.setEnabled(true);
                tv_remind.setTextColor(mContext.getResources().getColor(R.color.title_bg1));
            }

            @Override
            public void onCancel() {
                isKind = true;
                tv_deadline.setText("");
                tv_remind.setTextColor(mContext.getResources().getColor(R.color.activity_bg));
                layout_remind.setEnabled(false);
                layout_retask.setVisibility(View.VISIBLE);
                layout_retask_view.setVisibility(View.VISIBLE);
            }

        }, false, "取消");
    }

    /**
     * 重复任务数据初始化
     */
    void setRepeatParam(String[] str) {

        if (str[0].equals("每天")) {
            cornBody.setType(1);
        } else if (str[0].equals("每周")) {
            cornBody.setType(2);

            if (str[1].equals("周一")) {
                cornBody.setWeekDay(2);
            } else if (str[1].equals("周二")) {
                cornBody.setWeekDay(3);
            } else if (str[1].equals("周三")) {
                cornBody.setWeekDay(4);
            } else if (str[1].equals("周四")) {
                cornBody.setWeekDay(5);
            } else if (str[1].equals("周五")) {
                cornBody.setWeekDay(6);
            } else if (str[1].equals("周六")) {
                cornBody.setWeekDay(7);
            } else if (str[1].equals("周日")) {
                cornBody.setWeekDay(1);
            }

        } else if (str[0].equals("每月")) {
            cornBody.setType(3);
            cornBody.setDay(Integer.parseInt(str[1].replaceAll("号", "")));
        }

        cornBody.setHour(Integer.parseInt(str[2].replaceAll("时", "")));
        cornBody.setMinute(Integer.parseInt(str[3].replaceAll("分", "")));
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

    /**
     * 负责人值设置,这里User为老版本bean,NewUser为新版本bean,做了一个数据转移
     * RealName字段，改为Name
     */
    void setResponsiblePersion(final User user) {
        newUser = user.toShortUser();
        tv_responsiblePerson.setText(newUser.getName());
    }

    /**
     * 批量上传附件
     */
    private void newUploadAttachement() {
        showLoading("正在提交");
        try {
            uploadSize = 0;
            uploadNum = pickPhots.size();
            for (ImageInfo item : pickPhots) {
                Uri uri = Uri.parse(item.path);
                File newFile = Global.scal(this, uri);
                if (newFile != null && newFile.length() > 0) {
                    if (newFile.exists()) {
                        TypedFile typedFile = new TypedFile("image/*", newFile);
                        LogUtil.dee("typeFile:" + typedFile);
                        TypedString typedUuid = new TypedString(uuid);
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).newUpload(typedUuid, bizType, typedFile,
                                new RCallback<Attachment>() {
                                    @Override
                                    public void success(final Attachment attachments, final Response response) {
                                        uploadSize++;
                                        if (uploadSize == uploadNum) {
                                            requestCommitTask();
                                        }
                                    }

                                    @Override
                                    public void failure(final RetrofitError error) {
                                        super.failure(error);
                                        HttpErrorCheck.checkError(error);
                                        img_title_right.setEnabled(true);
                                    }
                                });
                    }
                }
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
            Toast("图片过大");
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            /*关联客户回调*/
            case FinalVariables.REQUEST_SELECT_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                if (null != customer) {
                    customerId = customer.id;
                    customerName = customer.name;
                    tv_mycustomer.setText(customer.name);
                } else {
                    customerName = "";
                    customerId = "";
                    tv_mycustomer.setText("无");
                }
                break;

            /*所属项目回调*/
            case FinalVariables.REQUEST_SELECT_PROJECT:
                Project _project = (Project) data.getSerializableExtra("data");
                if (null != _project) {
                    projectId = _project.id;
                    tv_Project.setText(_project.title);
                } else {
                    projectId = "";
                    tv_Project.setText("无");
                }
                break;

            /*相册选择 回调*/
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    pickPhotsResult = new ArrayList<>();
                    mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        pickPhotsResult.add(new ImageInfo("file://" + path));
                    }
                    pickPhots.addAll(pickPhotsResult);
                    init_gridView_photo();
                }
                break;

            /*附件删除回调*/
            case PhotoPreview.REQUEST_CODE:
                int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                if (index >= 0) {
                    pickPhots.remove(index);
                    init_gridView_photo();
                }
                break;

            /*用户单选, 负责人*/
            case FinalVariables.REQUEST_ONLY:
                NewUser u = (NewUser) data.getSerializableExtra("data");
                newUser = u;
                tv_responsiblePerson.setText(newUser.getName());
                break;

            /*用户选择, 参与人*/
            case FinalVariables.REQUEST_ALL_SELECT:
                members = (Members) data.getSerializableExtra("data");
                if (null == members) {
                    tv_toUsers.setText("无参与人");
                } else {
                    joinName = new StringBuffer();
                    joinUserId = new StringBuffer();
                    if (null != members.depts) {
                        for (NewUser newUser : members.depts) {
                            joinName.append(newUser.getName() + ",");
                            joinUserId.append(newUser.getId() + ",");
                        }
                    }
                    if (null != members.users) {
                        for (NewUser newUser : members.users) {
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

    /**
     * 没明白，这里销毁后，为什么要保存数据
     */
    //isSave=true时保存临时Task,=false时删除Task临时Task
    boolean isSave = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.Instance().deleteTask();
        if (isSave) {
            if (switch_approve.getState() == 1) {
                isState = false;
            } else if (switch_approve.getState() == 4) {
                isState = true;
            }

            mTask = new Task();
            mTask.setTitle(edt_title.getText().toString().trim());
            mTask.setContent(edt_content.getText().toString().trim());
            mTask.setReviewFlag(isState);

            if (newUser != null) {
                //直接保存responsiblePerson会出错
                mTask.setResponsiblePersonId(newUser.getId());
                mTask.setResponsiblePersonName(newUser.getRealname());
            }

            if (mDeadline > 0) {
                mTask.setPlanEndAt(mDeadline);
            }

            mTask.setAttachments(null);
            DBManager.Instance().putTask(MainApp.gson.toJson(mTask));
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