package com.loyo.oa.v2.activity.tasks;

import android.app.Activity;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.customer.CustomerSearchActivity;
import com.loyo.oa.v2.activity.DepartmentUserActivity;
import com.loyo.oa.v2.activity.ProjectSearchActivity;
import com.loyo.oa.v2.activity.commonview.SelectDetUserActivity;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonAdapter.CommonAdapter;
import com.loyo.oa.v2.tool.CommonAdapter.ViewHolder;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.DateTimePickDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

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
    TextView tv_Project;
    @ViewById
    TextView tv_mycustomer;
    @ViewById
    Switch switch_approve;
    @ViewById
    EditText edt_content;
    @ViewById
    EditText edt_title;
    @ViewById
    GridView gridView_photo;
    @Extra
    String projectId;
    @Extra
    String projectTitle;
    @Extra("data")
    Task mTask;
    @Extra(ExtraAndResult.EXTRA_ID)
    String customerId;
    @Extra(ExtraAndResult.EXTRA_NAME)
    String customerName;


    private AlertDialog dialog_Product;
    private SignInGridViewAdapter signInGridViewAdapter;
    private NewUser newUser;
    private ArrayList<Attachment> lstData_Attachment = new ArrayList<>();
    private StringBuffer strBuf;
    private Members member;
    private ArrayList<NewUser> userss;
    private ArrayList<NewUser> depts;
    private String remindTime;

    private String uuid = StringUtil.getUUID();
    private long mDeadline;
    private int mRemind = 0;
    private boolean isCopy;

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
        member = new Members();
        strBuf = new StringBuffer();

        init_gridView_photo();
        setTouchView(-1);
        getTempTask();

        projectAddTask();
        if (!TextUtils.isEmpty(customerId)) {
            tv_mycustomer.setText(customerName);
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
        //tv_Project.setText();
        switch_approve.setChecked(true);
        isCopy = mTask != null ? true : false;
        member.users = mTask.getMembers().users; //参与人
        newUser = mTask.getResponsiblePerson();  //负责人
    }

    void getTempTask() {
        if (mTask == null) {
            return;
        }

        switch_approve.setChecked(mTask.isReviewFlag());

        if (!TextUtils.isEmpty(mTask.getResponsiblePersonId()) && !StringUtil.isEmpty(mTask.getResponsiblePersonName())) {

            User u = new User();
            u.id = mTask.getResponsiblePersonId();
            u.realname = mTask.getResponsiblePersonName();
            setResponsiblePersion(u);

        }

        //截至日期设置,需求没要求默认时间，暂注释
        /*  if (mTask.getPlanEndAt() > 0) {
            mDeadline = mTask.getPlanEndAt();
            tv_deadline.setText(app.df3.format(new Date(mDeadline * 1000)));
        }*/

        if (!TextUtils.isEmpty(projectTitle)) {
            tv_Project.setText(projectTitle);
        }
        getBundle();
    }

    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true, true);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    /**
     * 新建任务 POST提交
     */

    void requestCommitTask(String title, String content) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("responsiblePerson", newUser);
        map.put("members", member);
        map.put("planendAt", mDeadline);
        map.put("remindflag", mRemind > 0);
        map.put("remindtime", remindTime);
        map.put("reviewFlag", switch_approve.isChecked());
        map.put("attachmentUUId", uuid);
        map.put("customerId", customerId);
        map.put("customerName", customerName);
        if (!TextUtils.isEmpty(projectId)) {
            map.put("projectId", projectId);
        }
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).create(map, new RCallback<Task>() {
            @Override
            public void success(Task task, Response response) {

                task.setAck(true);
                Toast(getString(R.string.app_add) + getString(R.string.app_succeed));
                //不需要保存
                isSave = false;
                Intent intent = new Intent();
                intent.putExtra("data", task);
                setResult(Activity.RESULT_OK, intent);
                finish();
                if (isCopy)
                    TasksInfoActivity.instance.finish();

            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    Toast("请检查您的网络连接");
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    if (error.getResponse().getStatus() == 500) {
                        Toast("网络异常500，请稍候再试");
                    }
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {

                }
            }
        });
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_responsiblePerson,
            R.id.layout_deadline, R.id.tv_toUsers, R.id.layout_del, R.id.layout_project, R.id.layout_mycustomer})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
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

                if (mDeadline <= 0) {
                    Toast("截止日期" + getString(R.string.app_no_null));
                    break;
                }

                if (newUser == null || TextUtils.isEmpty(newUser.getId())) {
                    Toast("负责人" + getString(R.string.app_no_null));
                    break;
                }

                requestCommitTask(title, content);

                break;


            //截至时间
            case R.id.layout_deadline:
                DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null);
                dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
                    @Override
                    public void onDateTimeChanged(int year, int month, int day, int hour, int min) {

                        String str = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
                        tv_deadline.setText(str);
                        mDeadline = Long.parseLong(DateTool.getDataOne(str));
                        LogUtil.dll("截至时间:" + mDeadline + "");

                    }
                });
                break;

            //负责人选项
            case R.id.layout_responsiblePerson:
                Bundle bundle = new Bundle();
                bundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_SINGLE);
                app.startActivityForResult(this, SelectDetUserActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.request_Code, bundle);
                break;

            //参与人选项
            case R.id.tv_toUsers:

                Bundle bundle1 = new Bundle();
                bundle1.putInt(ExtraAndResult.STR_SHOW_TYPE, ExtraAndResult.TYPE_SHOW_USER);
                bundle1.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_MULTUI);
                app.startActivityForResult(this, SelectDetUserActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.request_Code, bundle1);


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
                app.startActivityForResult(this, ProjectSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_SELECT_PROJECT, bundle2);
                break;

            /*关联客户*/
            case R.id.layout_mycustomer:
                Bundle bundle3 = new Bundle();
                bundle3.putInt("from", TASKS_ADD_CUSTOMER);
                app.startActivityForResult(this, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_SELECT_CUSTOMER, bundle3);
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRemind = Task.RemindListSource.get(position);
                remindTime = Task.RemindList.get(position);
                tv_remind.setText(Task.RemindList.get(position));
                dialog_Product.dismiss();
            }
        });
    }

    /**
     * 负责人值设置,这里User为老版本bean,NewUser为新版本bean,做了一个数据转移
     * RealName字段，改为Name
     */
    void setResponsiblePersion(User user) {
        newUser = user.toShortUser();
        tv_responsiblePerson.setText(newUser.getName());
    }

    void getAttachments() {
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> _attachments, Response response) {
                lstData_Attachment = _attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("获取附件失败");
                super.failure(error);
            }
        });
    }

    void setJoinUsers(String joinedUserIds, String joinedUserName) {

        userss.clear();
        depts.clear();

        String[] userIds = joinedUserIds.split(",");
        String[] userNames = joinedUserName.split(",");

        for (int i = 0; i < userIds.length; i++) {
            NewUser newUser = new NewUser();
            newUser.setName(userNames[i]);
            newUser.setId(userIds[i]);
            userss.add(newUser);
        }

        member.users = userss;

        if (!TextUtils.isEmpty(joinedUserName)) {
            tv_toUsers.setText(joinedUserName);
            layout_del.setVisibility(View.VISIBLE);
            img_title_right_toUsers.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            /*关联任务回调*/
            case FinalVariables.REQUEST_SELECT_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                if (null != customer) {
                    customerId = customer.id;
                    customerName = customer.name;
                    tv_mycustomer.setText(customer.name);
                } else {
                    tv_mycustomer.setText("无");
                }
                break;

            /*所属项目回调*/
            case FinalVariables.REQUEST_SELECT_PROJECT:
                Project _project = (Project) data.getSerializableExtra("data");
                projectId = _project.id;
                if (null != _project) {
                    tv_Project.setText(_project.title);
                } else {
                    tv_Project.setText("无");
                }
                break;

            case DepartmentUserActivity.request_Code:

                User user = (User) data.getSerializableExtra(User.class.getName());
                //负责人回调
                if (user != null) {
                    setResponsiblePersion(user);
                }

                //参与人回调
                else {
                    String cc_user_id = data.getStringExtra(ExtraAndResult.CC_USER_ID);
                    String cc_user_name = data.getStringExtra(ExtraAndResult.CC_USER_NAME);
                    if (cc_user_id != null || cc_user_name != null) {
                        setJoinUsers(cc_user_id, cc_user_name);
                    } else {
                        Toast("未选择相关人员");
                    }
                }

                break;

            case SelectPicPopupWindow.GET_IMG:
                try {
                    ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(this, uri);

                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                Utils.uploadAttachment(uuid, newFile).subscribe(new CommonSubscriber(this) {
                                    @Override
                                    public void onNext(Serializable serializable) {
                                        getAttachments();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }

                break;
            //附件删除
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), new RCallback<Attachment>() {
                    @Override
                    public void success(Attachment attachment, Response response) {
                        Toast("删除附件成功!");
                        lstData_Attachment.remove(delAttachment);
                        init_gridView_photo();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast("删除附件失败!");
                        super.failure(error);
                    }
                });
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
            mTask = new Task();
            mTask.setTitle(edt_title.getText().toString().trim());
            mTask.setContent(edt_content.getText().toString().trim());
            mTask.setReviewFlag(switch_approve.isChecked());

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
        public RemindAdapter(Context context, List<String> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String s) {
            holder.setText(R.id.tv, s);
        }
    }
}